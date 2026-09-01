package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.materia.Materia;
import br.com.Vestibuline.domain.materia.MateriaRepository;
import br.com.Vestibuline.domain.materia.dto.MateriaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MateriaService {

    @Autowired
    private MateriaRepository repository;

    public Materia verificarMateria(String nome) {
        var materia = repository.findByNome(nome);

        if (materia.isPresent()) {
            return materia.get();
        }else {
            var novaMateria = new Materia(nome);
            return repository.save(novaMateria);
        }
    }

    public List<MateriaDto> listarMaterias() {
        return repository.findAll().stream()
                .map(MateriaDto::new)
                .toList();
    }
}
