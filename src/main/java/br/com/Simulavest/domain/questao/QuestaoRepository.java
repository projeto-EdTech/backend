package br.com.Simulavest.domain.questao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface QuestaoRepository extends JpaRepository<Questao, UUID> {

    @Query("""
    SELECT COUNT(DISTINCT q)
    FROM Questao q
    JOIN q.conteudos c
    JOIN q.prova p
    JOIN p.instituicao i
    WHERE LOWER(i.sigla) = LOWER(:sigla) 
      AND LOWER(c.nome) IN :conteudos
""")
    long quantidadeQuestoesDisponiveis(
            @Param("conteudos") List<String> conteudos,
            @Param("sigla") String instituicao
    );

    @Query("""
    SELECT DISTINCT q
    FROM Questao q
    JOIN FETCH q.alternativas a
    JOIN q.prova p
    JOIN p.instituicao i
    JOIN q.conteudos c
    WHERE LOWER(i.sigla) = LOWER(:sigla) 
      AND LOWER(c.nome) IN :conteudos
""")
    List<Questao> buscarQuestoesPorInstituicaoEFundamentos(
            @Param("sigla") String sigla,
            @Param("conteudos") List<String> conteudos
    );

    /**
     * Busca TODAS as questões de uma instituição específica.
     * Usamos JOIN FETCH para garantir performance nas alternativas.
     */
    @Query("""
    SELECT DISTINCT q
    FROM Questao q
    JOIN FETCH q.alternativas a
    JOIN q.prova p
    JOIN p.instituicao i
    WHERE LOWER(i.sigla) = LOWER(:sigla)
""")
    List<Questao> buscarQuestoesPorInstituicao(@Param("sigla") String sigla);

    @Query("""
            SELECT q FROM Questao q
            JOIN q.conteudos c
            WHERE c.materia.id = :materiaId
    """)
    List<Questao> findQuestoesByMateriaId(UUID materiaId);

    @Query("""
        SELECT q from Questao q
            join q.conteudos c
            join q.prova p
            WHERE c.materia.id = :materiaId
            AND p.instituicao.id = :instituicaoId
    """)
    List<Questao> findQuestoesByMateriaAndInstituicaoId(UUID materiaId, UUID instituicaoId);
}