package br.com.Vestibuline.domain.instituicao;

import br.com.Vestibuline.domain.instituicao.dtos.InstituicaoAtualizacaoDTO;
import br.com.Vestibuline.domain.instituicao.dtos.InstituicaoRequestDTO;
import br.com.Vestibuline.domain.prova.Prova;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "instituicao")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Instituicao {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nome;

    @Column(name = "tipo_instituicao")
    @Enumerated(EnumType.STRING)
    private TipoInstituicao tipoInstituicao;

    private String sigla;
    private String logo;
    @Column(name = "estado_origem")
    private String estadoOrigem;

    @OneToMany(mappedBy = "instituicao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Prova> provas = new ArrayList<>();

    public Instituicao(InstituicaoRequestDTO dto) {
        this.nome = dto.nome().trim();
        this.tipoInstituicao = dto.tipoInstituicao();
        this.sigla = dto.sigla().trim().toUpperCase();
        this.logo = dto.logo().trim();
        this.estadoOrigem = dto.estadoOrigem().trim();
    }

    public void adicionarProva(Prova prova){
        if (prova != null) {
            provas.add(prova);
            prova.setInstituicao(this);
        }
    }

    public void atualizarDados(InstituicaoAtualizacaoDTO dto) {
        if (dto.nome() != null) {
            this.nome = dto.nome().trim();
        }

        if (dto.tipoInstituicao() != null) {
            this.tipoInstituicao = dto.tipoInstituicao();
        }

        if (dto.sigla() != null) {
            this.sigla = dto.sigla().trim();
        }

        if (dto.logo() != null) {
            this.logo = dto.logo().trim();
        }
    }
}