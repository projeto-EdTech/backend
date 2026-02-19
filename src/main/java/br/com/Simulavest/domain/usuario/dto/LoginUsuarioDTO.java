package br.com.Simulavest.domain.usuario.dto;

import br.com.Simulavest.domain.usuario.TipoUsuario;

import java.util.UUID;

public record LoginUsuarioDTO(
        Boolean newsLetter,
        UUID id,
        TipoUsuario tipoUsuario
) {
}
