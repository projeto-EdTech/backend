package br.com.Simulavest.service;

import br.com.Simulavest.domain.materia.Materia;
import br.com.Simulavest.domain.materia.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
