package br.com.Vestibuline.domain.historico;

import br.com.Vestibuline.domain.materia.dto.PerformanceMateriaProjection;
import br.com.Vestibuline.domain.usuario.dto.StatsGeralDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface HistoricoRepository extends JpaRepository<Historico, UUID> {
    List<Historico> findByUsuarioId(UUID usuarioId);

    @Query("""
    SELECT new br.com.Vestibuline.domain.usuario.dto.StatsGeralDTO(
        COUNT(h), 
        SUM(h.quantidade_acertos), 
        SUM(h.quantidade_questoes)
    ) 
    FROM Historico h 
    WHERE h.usuario.id = :usuarioId
""")
    StatsGeralDTO findStatsGeralByUsuarioId(@Param("usuarioId") UUID usuarioId);

    @Query(value = """
        SELECT m.nome_materia AS nomeMateria,
               CAST(ROUND((SUM(CASE WHEN r.acertou = TRUE THEN 1 ELSE 0 END) * 100.0) / COUNT(*)) AS int) AS percentualAcertos
        FROM historico h
        JOIN resposta r ON h.id = r.historico_id
        JOIN (
            SELECT DISTINCT ON (q.id) q.id, c.materia_id
            FROM questao q
            JOIN questao_conteudo qc ON q.id = qc.questao_id
            JOIN conteudo c ON qc.conteudo_id = c.id
        ) q_mat ON r.questao_id = q_mat.id
        JOIN materia m ON q_mat.materia_id = m.id
        WHERE h.usuario_id = :usuarioId
        GROUP BY m.nome_materia
        """, nativeQuery = true)
    List<PerformanceMateriaProjection> findPerformancePorMateriaNativo(@Param("usuarioId") UUID usuarioId);
}
