package br.com.Vestibuline.domain.usuario.dto;

import br.com.Vestibuline.domain.usuario.TipoUsuario;

import java.util.UUID;

public record LoginUsuarioDTO(
        Boolean newsLetter,
        UUID id,
        TipoUsuario tipoUsuario
) {
}
