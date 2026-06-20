package br.com.Vestibuline.domain.resposta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RespostaRepository extends JpaRepository<Resposta, UUID> {
    
    boolean existsByHistoricoUsuarioIdAndQuestaoId(UUID usuarioId, UUID questaoId);
    
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
}