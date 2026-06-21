package br.com.Vestibuline.domain.planner;

import br.com.Vestibuline.domain.planner.interfaces.PlannerProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlannerRepository extends JpaRepository<PlannerEntity, UUID> {

    /**
     * Query nativa que retorna as top 3 matérias com pior desempenho proporcional
     * e, dentro de cada matéria, os top 3 conteúdos com pior desempenho.
     *
     * Lógica:
     *  1. Agrega respostas por (matéria, conteúdo) calculando total e erros.
     *  2. Usa ROW_NUMBER() para rankear conteúdos dentro de cada matéria (piores primeiro).
     *  3. Agrega os dados da matéria (soma de todos os conteúdos dela).
     *  4. Usa ROW_NUMBER() para rankear matérias globalmente (piores primeiro).
     *  5. Filtra: rank_materia <= 3 E rank_conteudo <= 3.
     *
     * Requer PostgreSQL 9.4+ (suporte a window functions e CTEs).
     */
    @Query(value = """
            WITH respostas_por_conteudo AS (
                -- Agrega acertos/erros por (usuário, matéria, conteúdo)
                SELECT
                    m.id                                          AS materia_id,
                    m.nome_materia                                AS materia_nome,
                    c.id                                          AS conteudo_id,
                    c.nome_fundamento                             AS conteudo_nome,
                    COUNT(r.id)                                   AS total_respostas,
                    COUNT(r.id) FILTER (WHERE r.acertou = false)  AS total_erros
                FROM resposta r
                JOIN historico   h  ON h.id            = r.historico_id
                JOIN questao     q  ON q.id            = r.questao_id
                JOIN questao_conteudo qc ON qc.questao_id = q.id
                JOIN conteudo    c  ON c.id            = qc.conteudo_id
                JOIN materia     m  ON m.id            = c.materia_id
                WHERE h.usuario_id = :usuarioId
                GROUP BY m.id, m.nome_materia, c.id, c.nome_fundamento
                HAVING COUNT(r.id) > 0
            ),
            conteudos_rankeados AS (
                -- Rankeia conteúdos dentro de cada matéria (maior taxa de erro primeiro)
                SELECT
                    materia_id,
                    materia_nome,
                    conteudo_id,
                    conteudo_nome,
                    total_respostas                                          AS conteudo_total_respostas,
                    total_erros                                              AS conteudo_total_erros,
                    ROUND((total_erros::NUMERIC / total_respostas) * 100, 2) AS conteudo_taxa_erro,
                    ROW_NUMBER() OVER (
                        PARTITION BY materia_id
                        ORDER BY (total_erros::NUMERIC / total_respostas) DESC,
                                 total_erros DESC
                    )                                                        AS rank_conteudo
                FROM respostas_por_conteudo
            ),
            materias_agregadas AS (
                -- Agrega desempenho geral da matéria (soma de todos os conteúdos)
                SELECT
                    materia_id,
                    materia_nome,
                    SUM(conteudo_total_respostas)                                          AS materia_total_respostas,
                    SUM(conteudo_total_erros)                                              AS materia_total_erros,
                    ROUND(
                        (SUM(conteudo_total_erros)::NUMERIC / SUM(conteudo_total_respostas)) * 100,
                        2
                    )                                                                      AS materia_taxa_erro,
                    ROW_NUMBER() OVER (
                        ORDER BY (SUM(conteudo_total_erros)::NUMERIC / SUM(conteudo_total_respostas)) DESC,
                                 SUM(conteudo_total_erros) DESC
                    )                                                                      AS rank_materia
                FROM conteudos_rankeados
                GROUP BY materia_id, materia_nome
            )
            -- Junta matérias e conteúdos aplicando os filtros de ranking
            SELECT
                ma.materia_id          AS materiaId,
                ma.materia_nome        AS materiaNome,
                ma.materia_total_respostas AS materiaTotalRespostas,
                ma.materia_total_erros     AS materiaTotalErros,
                ma.materia_taxa_erro       AS materiaTaxaErro,
                cr.conteudo_id             AS conteudoId,
                cr.conteudo_nome           AS conteudoNome,
                cr.conteudo_total_respostas AS conteudoTotalRespostas,
                cr.conteudo_total_erros     AS conteudoTotalErros,
                cr.conteudo_taxa_erro       AS conteudoTaxaErro
            FROM materias_agregadas ma
            JOIN conteudos_rankeados cr
                ON cr.materia_id    = ma.materia_id
                AND cr.rank_conteudo <= 3
            WHERE ma.rank_materia <= 3
            ORDER BY
                ma.rank_materia   ASC,
                cr.rank_conteudo  ASC
            """,
            nativeQuery = true)
    List<PlannerProjection> findTop3MateriasComTop3Conteudos(@Param("usuarioId") UUID usuarioId);
}