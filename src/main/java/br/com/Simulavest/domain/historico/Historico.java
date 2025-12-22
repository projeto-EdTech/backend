package br.com.Simulavest.domain.historico;

import br.com.Simulavest.domain.prova.Prova;
import br.com.Simulavest.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Table(name = "historico")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")

public class Historico {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "data_historico")
    private LocalDate data;

    @Column(name = "nota_final")
    private double notaFinal;

    @Column(name = "feedback_gemini")
    private String feedbackGemini;

    @Column(name = "quantidade_acertos")
    private int quantidadeAcertos;

    @Column(name = "quantidade_erros")
    private int quantidadeErros;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prova_id")
    private Prova prova;

}
