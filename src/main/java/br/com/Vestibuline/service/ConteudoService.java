package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.conteudo.Conteudo;
import br.com.Vestibuline.domain.conteudo.ConteudoRepository;
import br.com.Vestibuline.domain.materia.Materia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConteudoService {

    @Autowired
    private ConteudoRepository repository;

    public void adicionarConteudos(String conteudo, Materia materia) {
            var conteudoExistente = repository.existsByNomeAndMateria(conteudo, materia);
            if (!conteudoExistente) {
                var novoConteudo = new Conteudo(conteudo, materia);
                repository.save(novoConteudo);
            }

    }

    public Conteudo verificarConteudo(String nome, Materia materia) {
        var conteudo = repository.findByNomeAndMateria(nome, materia);

        if (conteudo.isPresent()) {
            return conteudo.get();
        } else {
            var novoConteudo = new Conteudo(nome, materia);
            return repository.save(novoConteudo);
        }
    }

    public List<Conteudo> verificarConteudos(List<String> conteudos, Materia materia) {
        return conteudos.stream()
                .map(conteudo -> verificarConteudo(conteudo, materia))
                .toList();
    }
}
