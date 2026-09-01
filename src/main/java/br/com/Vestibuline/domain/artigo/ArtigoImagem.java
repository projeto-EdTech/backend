package br.com.Vestibuline.domain.artigo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "artigo_imagem")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ArtigoImagem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // caminho relativo que será usado no HTML, ex: "uploads/artigos/image1.png"
    @Column(nullable = false)
    private String caminho;

    @Column(name = "nome_original")
    private String nomeOriginal;

    @Column(name = "content_type")
    private String contentType;

    private Long tamanho;

    // ordem no artigo (1,2,3...)
    private Integer ordem;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm = LocalDateTime.now();

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artigo_id")
    private Artigo artigo;
}
