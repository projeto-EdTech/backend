package br.com.Simulavest.domain.conteudo;

import br.com.Simulavest.domain.materia.Materia;
import br.com.Simulavest.domain.questao.Questao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "conteudo")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conteudo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome_fundamento")
    private String nome;

    @ManyToOne
    @JoinColumn(name = "materia_id")
    private Materia materia;

    @ManyToMany(mappedBy = "conteudos")
    private List<Questao> questoes = new ArrayList<>();

    public Conteudo(String conteudo, Materia materia) {
        this.nome = conteudo.trim();
        this.materia = materia;
    }
}