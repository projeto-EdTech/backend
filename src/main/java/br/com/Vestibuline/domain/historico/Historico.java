package br.com.Vestibuline.domain.historico;

import br.com.Vestibuline.domain.prova.Prova;
import br.com.Vestibuline.domain.simulado.TipoSimulado;
import br.com.Vestibuline.domain.usuario.Usuario;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_simulado")
    private TipoSimulado tipo_simulado;

    @Column(name = "quantidade_acertos")
    private int quantidade_acertos;

    @Column(name = "quantidade_questoes")
    private int quantidade_questoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prova_id")
    private Prova prova;

    @Column(name = "tempo_gasto")
    private Integer tempoGasto;

}
