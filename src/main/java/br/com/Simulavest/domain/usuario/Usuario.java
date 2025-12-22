package br.com.Simulavest.domain.usuario;

import br.com.Simulavest.domain.historico.Historico;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity(name = "Usuario")
@Table(name = "usuario")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "email_usuario", unique = true)
    private String email;

    @Column(name = "nome_usuario")
    private String nome;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Historico> historicos = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario")
    private TipoUsuario tipoUsuario;

    @Column(name = "newsletter")
    private boolean newsletter;

    public Usuario(String nome, String email, TipoUsuario tipoUsuario) {
        this.nome = nome;
        this.email = email;
        this.tipoUsuario = tipoUsuario;
    }
}
