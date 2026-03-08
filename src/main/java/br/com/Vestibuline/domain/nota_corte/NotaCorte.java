package br.com.Vestibuline.domain.nota_corte;

import br.com.Vestibuline.domain.instituicao.Instituicao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "nota_corte")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class NotaCorte {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ano_vestibular", nullable = false)
    private Integer ano;

    @Column(name = "nome_curso", nullable = false)
    private String nomeCurso;

    @Column(name = "modalidade_concorrencia")
    private String modalidadeConcorrencia;

    @Column(name = "nota_corte", nullable = false)
    private Double notaCorte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instituicao_id", nullable = false)
    private Instituicao instituicao;

    public NotaCorte(Instituicao instituicao, Integer ano, String nomeCurso, String modalidade, Double nota) {
        this.instituicao = instituicao;
        this.ano = ano;
        this.nomeCurso = nomeCurso != null ? nomeCurso.toUpperCase().trim() : null;
        this.modalidadeConcorrencia = modalidade;
        this.notaCorte = nota;
    }
}
