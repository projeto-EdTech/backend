package br.com.Simulavest.service;

import br.com.Simulavest.domain.usuario.TipoUsuario;
import br.com.Simulavest.domain.usuario.Usuario;
import br.com.Simulavest.domain.usuario.UsuarioRepository;
import br.com.Simulavest.domain.usuario.dto.CadastrarUsuarioDTO;
import br.com.Simulavest.domain.usuario.dto.InscricaoArtigoDTO;
import br.com.Simulavest.domain.usuario.dto.LoginUsuarioDTO;
import br.com.Simulavest.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    public LoginUsuarioDTO cadastrar(CadastrarUsuarioDTO dto) {

        return repository.findByEmail(dto.email())
                .map(user -> new LoginUsuarioDTO(
                        user.isNewsletter(),
                        user.getId(),
                        user.getTipoUsuario()
                ))
                .orElseGet(() -> {
                    Usuario usuario = new Usuario(
                            dto.nome(),
                            dto.email(),
                            TipoUsuario.FREE
                    );
                    Usuario usuarioSalvo = repository.save(usuario);
                    return new LoginUsuarioDTO(usuarioSalvo.isNewsletter(), usuarioSalvo.getId(), usuarioSalvo.getTipoUsuario());
                });
    }

    public boolean ativarNewsLetter(InscricaoArtigoDTO dto) {
        Usuario usuario = repository.findByEmail(dto.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o email: " + dto.email()));

        boolean novoStatus = !usuario.isNewsletter();

        repository.atualizarStatusNewsletter(dto.email(), novoStatus);

        return novoStatus;
    }
}
