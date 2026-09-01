package br.com.Vestibuline.controller;

import br.com.Vestibuline.domain.artigo.Artigo;
import br.com.Vestibuline.domain.artigo.dto.ArtigoDto;
import br.com.Vestibuline.service.ArtigoService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("api/artigos")
public class ArtigosController {

    private static final Logger logger = LoggerFactory.getLogger(ArtigosController.class);

    @Autowired
    private ArtigoService service;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadArtigo(
            @RequestParam("file")MultipartFile file,
            @RequestParam("titulo") String titulo,
            @RequestParam("autor") String autor,
            @RequestParam("materia") String materia,
            UriComponentsBuilder uri
    ) throws Exception {
        Artigo artigo = service.salvarArtigo(file, titulo, autor, materia);
        var uriCriada = uri.path("/api/artigos/{id}").buildAndExpand(artigo.getId()).toUri();

        var dtoCriado = new ArtigoDto(artigo);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Artigo criado com sucesso!");
        response.put("artigo", dtoCriado);

        return ResponseEntity.created(uriCriada).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtigoDto> getArtigoById(@PathVariable UUID id, HttpServletRequest req) throws Exception {
        Artigo artigo = service.buscarPorId(id);

        String baseUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();
        logger.debug("baseUrl: {}", baseUrl);
        String html = artigo.getConteudo().replace("/uploads/", baseUrl + "/uploads/");
        var dto = new ArtigoDto(artigo, html);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<ArtigoDto>> listarArtigos() {
        var listaDto = service.listarArtigos();
        return ResponseEntity.ok(listaDto);
    }
}
