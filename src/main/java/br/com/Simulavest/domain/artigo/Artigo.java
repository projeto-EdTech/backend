package br.com.Simulavest.domain.artigo;

import br.com.Simulavest.domain.materia.Materia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "artigo")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Artigo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String titulo;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String conteudo;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "criado_por")
    private String criadoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_id")
    private Materia materia;

    @OneToOne(mappedBy = "artigo", cascade = CascadeType.ALL, orphanRemoval = true)
    private ArtigoStats artigoStats;

    @OneToMany(mappedBy = "artigo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArtigoImagem> imagens = new ArrayList<>();


}
