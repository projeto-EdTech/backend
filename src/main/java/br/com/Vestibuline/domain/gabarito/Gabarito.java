package br.com.Vestibuline.domain.gabarito;

// Supondo que você tenha as entidades Usuario e Questao nos pacotes corretos
import br.com.Vestibuline.domain.usuario.Usuario;
import br.com.Vestibuline.domain.questao.Questao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Table(name = "gabarito")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Gabarito {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_questao")
    private Questao questao;

    @Column(name = "acertou")
    private boolean acertou;

    @Column(name = "resposta_usuario", length = 1)
    private char resposta_usuario;
}