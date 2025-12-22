package br.com.Simulavest.controller;

import br.com.Simulavest.domain.materia.MateriaRepository;
import br.com.Simulavest.domain.materia.dto.MateriaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/materias")
public class MateriaController {
    @Autowired
    private MateriaRepository repository;

    @GetMapping
    public ResponseEntity listarMaterias() {
        var listaMaterias = repository.findAll().stream()
                .map(MateriaDto::new);

        return ResponseEntity.ok(listaMaterias);
    }
}
