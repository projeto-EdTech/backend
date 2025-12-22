package br.com.Simulavest.domain.questao;

import br.com.Simulavest.domain.alternativa.Alternativa;
import br.com.Simulavest.domain.conteudo.Conteudo;
import br.com.Simulavest.domain.materia.Materia;
import br.com.Simulavest.domain.prova.Prova;
import br.com.Simulavest.domain.questao.dto.QuestaoDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "questao")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Questao {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String enunciado;

    @Column(name = "numero_questao")
    private int numeroQuestao;

    @ManyToOne
    @JoinColumn(name = "prova_id")
    private Prova prova;

    @ElementCollection
    @CollectionTable(name = "questao_imagens", joinColumns = @JoinColumn(name = "questao_id"))
    @Column(name = "imagem")
    private List<String> imagens = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "questao_conteudo",
            joinColumns = @JoinColumn(name = "questao_id"),
            inverseJoinColumns = @JoinColumn(name = "conteudo_id")
    )
    private List<Conteudo> conteudos = new ArrayList<>();

    @OneToMany(mappedBy = "questao", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Alternativa> alternativas = new ArrayList<>();

    public Questao(QuestaoDTO questaoDto) {
        this.enunciado = questaoDto.enunciado().trim();
        this.numeroQuestao = questaoDto.numeroEnunciado();
    }

    public void adicionarAlternativa(Alternativa alternativa) {
        if (alternativa != null) {
            alternativas.add(alternativa);
            alternativa.setQuestao(this);
        }
    }


    public void adicionarConteudos(Conteudo conteudo, Materia materia) {
        if (conteudo != null && this.conteudos.stream().noneMatch(c -> c.getNome().equalsIgnoreCase(conteudo.getNome()) && c.getMateria().getId().equals(materia.getId()))) {
            this.conteudos.add(conteudo);
        }
    }
}
