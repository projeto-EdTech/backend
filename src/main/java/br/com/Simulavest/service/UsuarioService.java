package br.com.Simulavest.service;

import br.com.Simulavest.domain.usuario.TipoUsuario;
import br.com.Simulavest.domain.usuario.Usuario;
import br.com.Simulavest.domain.usuario.UsuarioRepository;
import br.com.Simulavest.domain.usuario.dto.CadastrarUsuarioDTO;
import br.com.Simulavest.domain.usuario.dto.InscricaoArtigoDTO;
import br.com.Simulavest.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    public Usuario cadastrar(CadastrarUsuarioDTO dto) {

        if (repository.existsByEmail(dto.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado.");
        }


        Usuario usuario = new Usuario(
                dto.nome(),
                dto.email(),
                TipoUsuario.FREE
        );
        return repository.save(usuario);
    }

    public boolean ativarNewsLetter(InscricaoArtigoDTO dto) {
        boolean existe = repository.existsByEmail(dto.email());

        if(!existe) {
            throw new ResourceNotFoundException("Usuário não encontrado com o email: " + dto.email());
        }

        int updatedRows = repository.atualizarInscricaoArtigo(dto.email());
        return updatedRows > 0;
    }
}
