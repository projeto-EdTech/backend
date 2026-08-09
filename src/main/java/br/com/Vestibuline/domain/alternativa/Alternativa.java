package br.com.Vestibuline.domain.alternativa;

import br.com.Vestibuline.domain.alternativa.dto.AlternativaDTO;
import br.com.Vestibuline.domain.questao.Questao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Table(name = "alternativa")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Alternativa {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private UUID id;
    private String alternativa;

    @Column(name = "texto_alternativa")
    private String textoAlternativa;

    private boolean correta;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questao_id")
    private Questao questao;


    public Alternativa(AlternativaDTO alternativaDto, String s) {
        this.alternativa = alternativaDto.letra().trim();
        // texto pode ser nulo/vazio quando o conteúdo da alternativa é uma imagem (sem texto associado).
        this.textoAlternativa = alternativaDto.texto() != null ? alternativaDto.texto().trim() : null;
        this.correta = s.toLowerCase().contentEquals(alternativaDto.letra().toLowerCase());
    }
}