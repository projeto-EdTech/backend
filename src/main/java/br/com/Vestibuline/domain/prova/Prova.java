package br.com.Vestibuline.domain.prova;

import br.com.Vestibuline.domain.instituicao.Instituicao;
import br.com.Vestibuline.domain.prova.dto.ProvaDTO;
import br.com.Vestibuline.domain.questao.Questao;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "prova")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Prova {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nome;

    private int ano;

    private int dia;

    @Column(name = "quantidade_questoes")
    private int qtdeQuestoes;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instituicao_id")
    private Instituicao instituicao;

    @ToString.Exclude
    @OneToMany(mappedBy = "prova", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Questao> questoes = new ArrayList<>();


    public Prova(ProvaDTO dto) {
        this.nome = dto.nomeProva();
        this.ano = dto.ano();
        this.dia = dto.dia();
        this.qtdeQuestoes = dto.qtdeQuestoes();
    }

    public void adicionarQuestao(@Valid Questao questao){
        if(questao != null) {
            questoes.add(questao);
            questao.setProva(this);
        }
    }
}
