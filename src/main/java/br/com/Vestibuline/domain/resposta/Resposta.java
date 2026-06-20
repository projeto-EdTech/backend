package br.com.Vestibuline.domain.resposta;

import br.com.Vestibuline.domain.alternativa.Alternativa;
import br.com.Vestibuline.domain.historico.Historico;
import br.com.Vestibuline.domain.questao.Questao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Table(name = "resposta")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")

public class Resposta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "historico_id",nullable = false)
    private Historico historico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questao_id", nullable = false)
    private Questao questao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alternativa_escolhida_id", nullable = false)
    private Alternativa alternativaEscolhida;

    @Column(name = "acertou")
    private boolean acertou;

    @Column(name = "revisado")
    private boolean revisado;
}
