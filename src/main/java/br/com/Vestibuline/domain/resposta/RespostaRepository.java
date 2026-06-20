package br.com.Vestibuline.domain.resposta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface RespostaRepository extends JpaRepository<Resposta, UUID> {

    boolean existsByUsuarioIdAndQuestaoId(UUID usuarioId, UUID questaoId);

    public interface ResumoErroProjection {
        String getMateria();
        String getTopico();
        Long getQtdErros();
    }

    @Query("""
        SELECT 
            m.nome AS materia, 
            c.nome AS topico, 
            COUNT(r) AS qtdErros
        FROM Resposta r
        JOIN r.questao q
        JOIN q.conteudos c
        JOIN c.materia m
        WHERE r.historico.usuario.id = :userId
          AND r.acertou = false
        GROUP BY m.nome, c.nome
        ORDER BY m.nome, COUNT(r) DESC
    """)
    List<ResumoErroProjection> buscarTodosErrosPorMateriaETopico(@Param("userId") UUID userId);

    @Query(value = """
        SELECT m.nome_materia,
               c.nome_fundamento,
               r.id AS resposta_id,
               q.id AS questao_id,
               q.enunciado,
               alt.id AS alternativa_id,
               alt.texto_alternativa,
               alt.correta,
               r.alternativa_escolhida_id
        FROM resposta r
        JOIN historico h ON r.historico_id = h.id
        JOIN questao q ON r.questao_id = q.id
        JOIN alternativa alt ON q.id = alt.questao_id
        JOIN questao_conteudo qc ON q.id = qc.questao_id
        JOIN conteudo c ON qc.conteudo_id = c.id
        JOIN materia m ON c.materia_id = m.id
        WHERE r.id IN (
            -- Subquery que escolhe apenas o ID da resposta mais recente de cada questão errada
            SELECT DISTINCT ON (inner_r.questao_id) inner_r.id
            FROM resposta inner_r
            JOIN historico inner_h ON inner_r.historico_id = inner_h.id
            WHERE inner_h.usuario_id = :usuarioId
              AND inner_r.acertou = FALSE
              AND inner_r.revisado = FALSE
            ORDER BY inner_r.questao_id, inner_h.id DESC
        )
        ORDER BY m.nome_materia, q.id
        """, nativeQuery = true)
    List<Object[]> buscarQuestoesEAlternativasParaRevisarPorUsuario(@Param("usuarioId") UUID usuarioId);

    @Modifying
    @Transactional
    @Query(value = """
    UPDATE resposta r
    SET revisado = TRUE
    FROM historico h
    WHERE r.historico_id = h.id
      AND h.usuario_id = :usuarioId
      AND r.questao_id = :questaoId
    """, nativeQuery = true)
    void marcarQuestaoComoRevisada(@Param("usuarioId") UUID usuarioId, @Param("questaoId") UUID questaoId);
}
