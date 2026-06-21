package br.com.Vestibuline.domain.planner;

import br.com.Vestibuline.domain.alternativa.Alternativa;
import br.com.Vestibuline.domain.conteudo.Conteudo;
import br.com.Vestibuline.domain.historico.Historico;
import br.com.Vestibuline.domain.materia.Materia;
import br.com.Vestibuline.domain.planner.interfaces.PlannerProjection;
import br.com.Vestibuline.domain.prova.Prova;
import br.com.Vestibuline.domain.questao.Questao;
import br.com.Vestibuline.domain.resposta.Resposta;
import br.com.Vestibuline.domain.simulado.TipoSimulado;
import br.com.Vestibuline.domain.usuario.Usuario;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração do PlannerRepository usando banco em memória (H2).
 *
 * Dependências necessárias no pom.xml (escopo test):
 *   <dependency>
 *       <groupId>com.h2database</groupId>
 *       <artifactId>h2</artifactId>
 *       <scope>test</scope>
 *   </dependency>
 *
 * application-test.properties:
 *   spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1
 *   spring.jpa.hibernate.ddl-auto=create-drop
 *   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
 */
@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("PlannerRepository - Testes de Integração")
class PlannerRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private EntityManager em;

    @Autowired
    private PlannerRepository plannerRepository;

    // Entidades base reutilizadas entre testes
    private Usuario usuario;
    private Usuario outroUsuario;
    private Prova prova;

    @BeforeEach
    void setUp() {
        usuario      = criarUsuario("user@test.com");
        outroUsuario = criarUsuario("outro@test.com");
        prova        = criarProva("ENEM 2023");
    }

    // =========================================================================
    // Cenário 1 — retorno vazio para usuário sem respostas
    // =========================================================================

    @Test
    @DisplayName("Deve retornar lista vazia para usuário sem histórico")
    void deveRetornarListaVaziaParaUsuarioSemHistorico() {
        List<PlannerProjection> result =
                plannerRepository.findTop3MateriasComTop3Conteudos(usuario.getId());

        assertThat(result).isEmpty();
    }

    // =========================================================================
    // Cenário 2 — 1 matéria, 1 conteúdo, todos erros
    // =========================================================================

    @Test
    @DisplayName("Deve retornar 1 matéria com 1 conteúdo quando há apenas 1 conteúdo respondido")
    void deveRetornarUmaMateria_QuandoHaUmConteudo() {
        Materia mat    = criarMateria("Física");
        Conteudo con   = criarConteudo("Óptica", mat);
        Questao  q     = criarQuestao(prova, con);
        Alternativa alt = criarAlternativa(q, false);

        Historico hist = criarHistorico(usuario, prova);
        criarResposta(hist, q, alt, false); // errou

        em.flush();

        List<PlannerProjection> result =
                plannerRepository.findTop3MateriasComTop3Conteudos(usuario.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMateriaNome()).isEqualTo("Física");
        assertThat(result.get(0).getConteudoNome()).isEqualTo("Óptica");
        assertThat(result.get(0).getConteudoTotalErros()).isEqualTo(1L);
        assertThat(result.get(0).getConteudoTaxaErro()).isEqualTo(100.0);
    }

    // =========================================================================
    // Cenário 3 — não retorna conteúdos de outro usuário
    // =========================================================================

    @Test
    @DisplayName("Não deve retornar dados de outro usuário")
    void naoDeveRetornarDadosDeOutroUsuario() {
        Materia  mat = criarMateria("Química");
        Conteudo con = criarConteudo("Soluções", mat);
        Questao  q   = criarQuestao(prova, con);
        Alternativa alt = criarAlternativa(q, false);

        // Apenas outroUsuario respondeu
        Historico hist = criarHistorico(outroUsuario, prova);
        criarResposta(hist, q, alt, false);

        em.flush();

        List<PlannerProjection> result =
                plannerRepository.findTop3MateriasComTop3Conteudos(usuario.getId());

        assertThat(result).isEmpty();
    }

    // =========================================================================
    // Cenário 4 — ordena por taxa de erro DESC (piores primeiro)
    // =========================================================================

    @Test
    @DisplayName("Deve ordenar matérias pela maior taxa de erro primeiro")
    void deveOrdenarMateriasPorTaxaErroDesc() {
        // Matéria A: 2 erros em 2 respostas = 100%
        Materia  matA = criarMateria("Matéria A");
        Conteudo conA = criarConteudo("Conteúdo A", matA);
        Questao  qA1  = criarQuestao(prova, conA);
        Questao  qA2  = criarQuestao(prova, conA);
        Alternativa altA1 = criarAlternativa(qA1, false);
        Alternativa altA2 = criarAlternativa(qA2, false);

        // Matéria B: 1 erro em 2 respostas = 50%
        Materia  matB = criarMateria("Matéria B");
        Conteudo conB = criarConteudo("Conteúdo B", matB);
        Questao  qB1  = criarQuestao(prova, conB);
        Questao  qB2  = criarQuestao(prova, conB);
        Alternativa altB1 = criarAlternativa(qB1, true);
        Alternativa altB2 = criarAlternativa(qB2, false);

        Historico hist = criarHistorico(usuario, prova);
        criarResposta(hist, qA1, altA1, false);
        criarResposta(hist, qA2, altA2, false);
        criarResposta(hist, qB1, altB1, true);
        criarResposta(hist, qB2, altB2, false);

        em.flush();

        List<PlannerProjection> result =
                plannerRepository.findTop3MateriasComTop3Conteudos(usuario.getId());

        assertThat(result).hasSize(2);
        // Matéria A (100%) deve vir antes de Matéria B (50%)
        assertThat(result.get(0).getMateriaNome()).isEqualTo("Matéria A");
        assertThat(result.get(1).getMateriaNome()).isEqualTo("Matéria B");
    }

    // =========================================================================
    // Cenário 5 — limita a 3 matérias mesmo com 4+ no banco
    // =========================================================================

    @Test
    @DisplayName("Deve retornar no máximo 3 matérias mesmo com 4 disponíveis")
    void deveRetornarNoMaximo3Materias() {
        // Cria 4 matérias com taxas diferentes
        for (int i = 1; i <= 4; i++) {
            Materia  mat = criarMateria("Matéria " + i);
            Conteudo con = criarConteudo("Conteúdo " + i, mat);
            Questao  q   = criarQuestao(prova, con);
            Alternativa alt = criarAlternativa(q, false);
            Historico hist  = criarHistorico(usuario, prova);
            criarResposta(hist, q, alt, false); // 100% de erro em todas
        }

        em.flush();

        List<PlannerProjection> result =
                plannerRepository.findTop3MateriasComTop3Conteudos(usuario.getId());

        // Distintas matérias no resultado
        long materiasDistintas = result.stream()
                .map(PlannerProjection::getMateriaId)
                .distinct()
                .count();

        assertThat(materiasDistintas).isLessThanOrEqualTo(3);
    }

    // =========================================================================
    // Cenário 6 — limita a 3 conteúdos por matéria
    // =========================================================================

    @Test
    @DisplayName("Deve retornar no máximo 3 conteúdos por matéria mesmo com 4+ disponíveis")
    void deveRetornarNoMaximo3ConteudosPorMateria() {
        Materia mat = criarMateria("Matemática");

        // Cria 4 conteúdos com erros
        for (int i = 1; i <= 4; i++) {
            Conteudo con = criarConteudo("Conteúdo " + i, mat);
            Questao  q   = criarQuestao(prova, con);
            Alternativa alt = criarAlternativa(q, false);
            Historico hist  = criarHistorico(usuario, prova);
            criarResposta(hist, q, alt, false);
        }

        em.flush();

        List<PlannerProjection> result =
                plannerRepository.findTop3MateriasComTop3Conteudos(usuario.getId());

        assertThat(result).hasSizeLessThanOrEqualTo(3);
    }

    // =========================================================================
    // Cenário 7 — acertos não influenciam como erro
    // =========================================================================

    @Test
    @DisplayName("Deve contabilizar acertos sem contar como erros")
    void deveContabilizarAcertosSeparadamente() {
        Materia  mat = criarMateria("História");
        Conteudo con = criarConteudo("Revolução Francesa", mat);
        Questao  q1  = criarQuestao(prova, con);
        Questao  q2  = criarQuestao(prova, con);
        Questao  q3  = criarQuestao(prova, con);
        Questao  q4  = criarQuestao(prova, con);

        Alternativa alt1 = criarAlternativa(q1, true);
        Alternativa alt2 = criarAlternativa(q2, true);
        Alternativa alt3 = criarAlternativa(q3, false);
        Alternativa alt4 = criarAlternativa(q4, false);

        Historico hist = criarHistorico(usuario, prova);
        criarResposta(hist, q1, alt1, true);  // acertou
        criarResposta(hist, q2, alt2, true);  // acertou
        criarResposta(hist, q3, alt3, false); // errou
        criarResposta(hist, q4, alt4, false); // errou

        em.flush();

        List<PlannerProjection> result =
                plannerRepository.findTop3MateriasComTop3Conteudos(usuario.getId());

        assertThat(result).hasSize(1);
        PlannerProjection row = result.get(0);
        assertThat(row.getConteudoTotalRespostas()).isEqualTo(4L);
        assertThat(row.getConteudoTotalErros()).isEqualTo(2L);
        assertThat(row.getConteudoTaxaErro()).isEqualTo(50.0);
    }

    // =========================================================================
    // Builders auxiliares
    // =========================================================================

    private Usuario criarUsuario(String email) {
        Usuario u = new Usuario();
        u.setEmail(email);
        em.persist(u);
        return u;
    }

    private Prova criarProva(String nome) {
        Prova p = new Prova();
        p.setNome(nome);
        em.persist(p);
        return p;
    }

    private Materia criarMateria(String nome) {
        Materia m = new Materia(nome);
        em.persist(m);
        return m;
    }

    private Conteudo criarConteudo(String nome, Materia materia) {
        Conteudo c = new Conteudo(nome, materia);
        em.persist(c);
        return c;
    }

    private Questao criarQuestao(Prova prova, Conteudo conteudo) {
        Questao q = new Questao();
        q.setEnunciado("Enunciado de teste");
        q.setNumeroQuestao(1);
        q.setProva(prova);
        q.getConteudos().add(conteudo);
        em.persist(q);
        return q;
    }

    private Alternativa criarAlternativa(Questao questao, boolean correta) {
        Alternativa a = new Alternativa();
        a.setQuestao(questao);
        a.setTextoAlternativa("Alternativa de teste");
        a.setCorreta(correta);
        em.persist(a);
        return a;
    }

    private Historico criarHistorico(Usuario usuario, Prova prova) {
        Historico h = new Historico();
        h.setUsuario(usuario);
        h.setProva(prova);
        h.setData(LocalDate.now());
        h.setTipo_simulado(TipoSimulado.PERSONALIZADO);
        h.setQuantidade_questoes(10);
        h.setQuantidade_acertos(5);
        em.persist(h);
        return h;
    }

    private Resposta criarResposta(Historico historico, Questao questao,
                                   Alternativa alternativa, boolean acertou) {
        Resposta r = new Resposta();
        r.setHistorico(historico);
        r.setQuestao(questao);
        r.setAlternativaEscolhida(alternativa);
        r.setAcertou(acertou);
        em.persist(r);
        return r;
    }
}