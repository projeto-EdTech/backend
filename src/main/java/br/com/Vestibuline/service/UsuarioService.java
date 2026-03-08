package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.usuario.TipoUsuario;
import br.com.Vestibuline.domain.usuario.Usuario;
import br.com.Vestibuline.domain.usuario.UsuarioRepository;
import br.com.Vestibuline.domain.usuario.dto.InscricaoArtigoDTO;
import br.com.Vestibuline.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Transactional
    public Usuario buscarOuCriarViaGoogle(String email, String nome) {
        return repository.findByEmail(email)
                .map(usuarioExistente -> {
                    if (!usuarioExistente.getNome().equals(nome)) {
                        usuarioExistente.setNome(nome);
                        return repository.save(usuarioExistente);
                    }
                    return usuarioExistente;
                })
                .orElseGet(() -> {
                    Usuario novo = new Usuario(nome, email, TipoUsuario.FREE);
                    return repository.save(novo);
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
