package br.com.Vestibuline.controller;

import br.com.Vestibuline.domain.materia.dto.MateriaDto;
import br.com.Vestibuline.service.MateriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/materias")
public class MateriaController {
    @Autowired
    private MateriaService service;

    @GetMapping
    public ResponseEntity<List<MateriaDto>> listarMaterias() {
        return ResponseEntity.ok(service.listarMaterias());
    }
}
