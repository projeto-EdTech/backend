package br.com.Simulavest.domain.artigo;

import br.com.Simulavest.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.*;

@Table(name = "artigo_stats")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ArtigoStats {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "artigo_id", referencedColumnName = "id")
    private Artigo artigo;

    private int curtidas;
    private int visualizacoes;
    private int compartilhamentos;
    @Column(name = "tempo_medio_leitura")
    private String tempoMedioLeitura;
}
