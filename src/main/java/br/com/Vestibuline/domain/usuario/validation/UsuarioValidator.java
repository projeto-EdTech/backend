package br.com.Vestibuline.domain.usuario.validation;

import br.com.Vestibuline.domain.usuario.UsuarioRepository;
import br.com.Vestibuline.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UsuarioValidator {

    @Autowired
    private UsuarioRepository repository;

    /**
     * Valida defensivamente se o usuário existe no sistema.
     * Dispara ResourceNotFoundException caso não encontre.
     */
    public void validarExistencia(UUID usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("O ID do usuário não pode ser nulo.");
        }

        if (!repository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário com o ID " + usuarioId + " não foi encontrado no sistema.");
        }
    }
}
