package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.artigo.Artigo;
import br.com.Vestibuline.domain.artigo.ArtigoImagem;
import br.com.Vestibuline.domain.artigo.ArtigoStats;
import br.com.Vestibuline.domain.artigo.dto.ArtigoDto;
import br.com.Vestibuline.domain.artigo.repository.ArtigoRepository;
import br.com.Vestibuline.domain.artigo.repository.ArtigoStatsRepository;
import br.com.Vestibuline.domain.materia.MateriaRepository;
import br.com.Vestibuline.exception.ResourceNotFoundException;
import fr.opensagres.poi.xwpf.converter.core.BasicURIResolver;
import fr.opensagres.poi.xwpf.converter.core.FileImageExtractor;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ArtigoService {

    @Value("${upload.dir}")
    private String uploadDir;

    @Autowired
    private ArtigoRepository repository;

    @Autowired
    private ArtigoStatsRepository artigoStatsRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Transactional
    public Artigo salvarArtigo(MultipartFile file, String titulo, String autor, String materia) throws Exception {
        var materiaArtigo = materiaRepository.findMateriaByNomeContainingIgnoreCase(materia);
        if (materiaArtigo.isEmpty()) {
            throw new ResourceNotFoundException("Matéria não encontrada: " + materia);
        }

        Path pastaBaseArtigos = Paths.get(uploadDir, "artigos");
        Files.createDirectories(pastaBaseArtigos);

        String slug = gerarSlug(titulo);
        String data = LocalDate.now().toString();
        String pastaArtigo = slug + "_" + data;

        Path pastaImagensArtigo = pastaBaseArtigos.resolve(pastaArtigo);
        Files.createDirectories(pastaImagensArtigo);

        String nomeArquivo = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path caminhoArquivo = pastaImagensArtigo.resolve(nomeArquivo);
        Files.copy(file.getInputStream(), caminhoArquivo, StandardCopyOption.REPLACE_EXISTING);

        String html = converterDocxParaHtml(caminhoArquivo.toFile(), pastaImagensArtigo, pastaArtigo);
        String tempoMedioLeitura = calcularTempoMedioLeitura(caminhoArquivo.toFile());

        Artigo artigo = new Artigo();
        ArtigoStats stats = new ArtigoStats();

        artigo.setTitulo(titulo);
        artigo.setCriadoPor(autor);
        artigo.setConteudo(html);
        artigo.setMateria(materiaArtigo.get());
        artigo.setArtigoStats(stats);

        stats.setArtigo(artigo);
        stats.setTempoMedioLeitura(tempoMedioLeitura);

        List<ArtigoImagem> imagens = new ArrayList<>();
        Pattern imgPattern = Pattern.compile(
                "<img[^>]*src=[\"']([^\"']+)[\"'][^>]*>",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = imgPattern.matcher(html);
        int ordem = 1;

        while (matcher.find()) {
            String src = matcher.group(1);
            String caminhoRelativo = extrairCaminhoRelativoImagem(src);
            String filename = Paths.get(caminhoRelativo).getFileName().toString();
            Path arquivoNoFS = pastaImagensArtigo.resolve(filename);

            ArtigoImagem ai = new ArtigoImagem();
            ai.setCaminho(caminhoRelativo);
            ai.setNomeOriginal(filename);
            ai.setOrdem(ordem++);
            ai.setArtigo(artigo);

            try {
                ai.setContentType(Files.probeContentType(arquivoNoFS));
                ai.setTamanho(Files.size(arquivoNoFS));
            } catch (IOException e) {
                ai.setContentType(null);
                ai.setTamanho(null);
            }

            imagens.add(ai);
        }

        artigo.setImagens(imagens);
        return repository.save(artigo);
    }

    private String converterDocxParaHtml(File docxFile, Path pastaImagensArtigo, String pastaPublica) {
        try (FileInputStream in = new FileInputStream(docxFile);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XWPFDocument document = new XWPFDocument(in);
            XHTMLOptions options = XHTMLOptions.create();
            options.setExtractor(new FileImageExtractor(pastaImagensArtigo.toFile()));

            String basePath = "/uploads/artigos/" + pastaPublica + "/";
            options.URIResolver(new BasicURIResolver(basePath));

            XHTMLConverter.getInstance().convert(document, out, options);
            String html = out.toString();

            return limparEEstruturarHtml(html);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao converter DOCX para HTML: " + e.getMessage(), e);
        }
    }

    private String limparEEstruturarHtml(String html) {
        // Remove tags HTML, HEAD e BODY
        html = html.replaceAll("(?i)<html[^>]*>", "");
        html = html.replaceAll("(?i)</html>", "");
        html = html.replaceAll("(?i)<head[^>]*>.*?</head>", "");
        html = html.replaceAll("(?i)<body[^>]*>", "");
        html = html.replaceAll("(?i)</body>", "");

        // Remove barras desnecessárias antes de URLs absolutas
        html = html.replaceAll("src=\"/+http", "src=\"http");
        html = html.replaceAll("href=\"/+http", "href=\"http");
        html = html.replaceAll("src=\"/uploads/", "src=\"/uploads/");

        // Remove tags style e script
        html = html.replaceAll("(?i)<style[^>]*>.*?</style>", "");
        html = html.replaceAll("(?i)<script[^>]*>.*?</script>", "");

        // Remove atributos desnecessários
        html = html.replaceAll("\\s+style=\"[^\"]*\"", "");
        html = html.replaceAll("\\s+class=\"[^\"]*\"", "");
        html = html.replaceAll("\\s+id=\"[^\"]*\"", "");
        html = html.replaceAll("\\s+data-[^=]*=\"[^\"]*\"", "");
        html = html.replaceAll("\\s+xmlns[^=]*=\"[^\"]*\"", "");
        html = html.replaceAll("\\s+lang=\"[^\"]*\"", "");
        html = html.replaceAll("\\s+dir=\"[^\"]*\"", "");
        html = html.replaceAll("\\s+role=\"[^\"]*\"", "");
        html = html.replaceAll("\\s+aria-[^=]*=\"[^\"]*\"", "");

        // Limpa espaços múltiplos
        html = html.replaceAll(">\\s+<", "><");
        html = html.replaceAll("&nbsp;+", " ");

        // Estrutura: converte parágrafos em headings para seções numeradas
        // 1) ... -> <h2>1)</h2>
        html = html.replaceAll("<p>\\s*<span>?\\s*([0-9]+\\))\\s*</span>?\\s*</p>",
                "<h2>$1</h2>");

        // Estrutura: converte subtítulos (sem números, após bullets)
        // ● Histograma -> <h3>Histograma</h3>
        html = html.replaceAll("<p>\\s*<span>●\\s*</span>\\s*<span>\\s*</span>\\s*<span>([^<]+)</span>\\s*</p>",
                "<h3>$1</h3>");

        // Agrupa bullets em listas não ordenadas
        html = agruparBulletsEmListas(html);

        // Remove spans vazios
        html = html.replaceAll("<span>\\s*</span>", "");

        // Formata código
        html = formatarCodigoEmBloco(html);

        // Corrige URLs novamente
        html = html.replaceAll("src=\"/+http", "src=\"http");

        return html;
    }

    private String agruparBulletsEmListas(String html) {
        // Agrupa parágrafos com bullets consecutivos em listas
        Pattern bulletPattern = Pattern.compile(
                "(?:<p>\\s*<span>●[^<]*</span>[^<]*</p>\\s*)+"
        );

        Matcher matcher = bulletPattern.matcher(html);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String bullets = matcher.group(0);
            String items = bullets.replaceAll(
                    "<p>\\s*<span>●\\s*</span>\\s*<span>\\s*</span>\\s*<span>([^<]*)</span>([^<]*)</p>",
                    "<li>$1$2</li>"
            );
            items = items.replaceAll("<li>\\s+", "<li>");

            String replacement = "<ul>\n" + items + "</ul>\n";
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    private String formatarCodigoEmBloco(String html) {
        // Detecta blocos com 'import' e converte para code block
        html = html.replaceAll(
                "(<p>\\s*<span>import</span>.*?</p>)",
                "<pre><code>$1</code></pre>"
        );

        // Limpa tags <p> e <span> dentro dos blocos de código
        Pattern codePattern = Pattern.compile("(?s)<pre><code>(.*?)</code></pre>");
        Matcher codeMatcher = codePattern.matcher(html);
        StringBuffer sb = new StringBuffer();
        while (codeMatcher.find()) {
            String code = codeMatcher.group(1)
                    .replaceAll("</?p>", "\n")
                    .replaceAll("</?span>", "")
                    .replaceAll("&nbsp;", " ");
            codeMatcher.appendReplacement(sb, Matcher.quoteReplacement("<pre><code>" + code + "</code></pre>"));
        }
        codeMatcher.appendTail(sb);
        html = sb.toString();

        return html;
    }

    private String extrairCaminhoRelativoImagem(String src) {
        if (src == null || src.isBlank()) {
            return "";
        }

        src = src.replace("\\", "");

        if (src.startsWith("http://") || src.startsWith("https://")) {
            try {
                java.net.URL url = new java.net.URL(src);
                src = url.getPath();
            } catch (java.net.MalformedURLException e) {
                int pathStart = src.indexOf("/", src.indexOf("://") + 3);
                if (pathStart > 0) {
                    src = src.substring(pathStart);
                }
            }
        }

        if (src.startsWith("/")) {
            src = src.substring(1);
        }

        return src;
    }

    private String gerarSlug(String titulo) {
        return titulo
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    private String calcularTempoMedioLeitura(File docxFile) throws IOException {
        long totalWords = 0;

        try (FileInputStream fis = new FileInputStream(docxFile);
             XWPFDocument document = new XWPFDocument(fis)) {

            for (var para : document.getParagraphs()) {
                Matcher matcher = Pattern.compile("\\b\\w+\\b").matcher(para.getText());
                while (matcher.find()) {
                    totalWords++;
                }
            }

            for (var table : document.getTables()) {
                for (var row : table.getRows()) {
                    for (var cell : row.getTableCells()) {
                        Matcher matcher = Pattern.compile("\\b\\w+\\b").matcher(cell.getText());
                        while (matcher.find()) {
                            totalWords++;
                        }
                    }
                }
            }
        }

        long segundos = totalWords / 4;
        if (segundos < 60) {
            return segundos + "s";
        }

        long minutos = segundos / 60;
        return (minutos + 1) + " min";
    }

    @Transactional(readOnly = true)
    public Artigo buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artigo não encontrado com id: " + id));
    }

    @Transactional(readOnly = true)
    public List<ArtigoDto> listarArtigos() {
        List<Artigo> artigos = repository.findAll();
        List<ArtigoDto> dtos = new ArrayList<>();
        for (Artigo artigo : artigos) {
            dtos.add(new ArtigoDto(artigo));
        }
        return dtos;
    }
}