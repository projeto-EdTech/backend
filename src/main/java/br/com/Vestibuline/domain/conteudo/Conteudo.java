package br.com.Vestibuline.domain.conteudo;

import br.com.Vestibuline.domain.materia.Materia;
import br.com.Vestibuline.domain.questao.Questao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "conteudo")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Conteudo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome_fundamento")
    private String nome;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_id")
    private Materia materia;

    @ToString.Exclude
    @ManyToMany(mappedBy = "conteudos")
    private List<Questao> questoes = new ArrayList<>();

    public Conteudo(String conteudo, Materia materia) {
        this.nome = conteudo.trim();
        this.materia = materia;
    }
}