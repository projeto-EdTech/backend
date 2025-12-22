package br.com.Simulavest.domain.alternativa;

import br.com.Simulavest.domain.alternativa.dto.AlternativaDTO;
import br.com.Simulavest.domain.questao.Questao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Table(name = "alternativa")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Alternativa {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private UUID id;
    private String alternativa;

    @Column(name = "texto_alternativa")
    private String textoAlternativa;

    private boolean correta;

    @ManyToOne
    @JoinColumn(name = "questao_id")
    private Questao questao;


    public Alternativa(AlternativaDTO alternativaDto, String s) {
        this.alternativa = alternativaDto.letra().trim();
        this.textoAlternativa = alternativaDto.texto().trim();
        this.correta = s.contentEquals(alternativaDto.letra());
    }
}