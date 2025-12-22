package br.com.Simulavest.controller;

import br.com.Simulavest.domain.instituicao.dtos.InstituicaoAtualizacaoDTO;
import br.com.Simulavest.domain.instituicao.dtos.InstituicaoDTO;
import br.com.Simulavest.domain.instituicao.dtos.InstituicaoRequestDTO;
import br.com.Simulavest.domain.instituicao.dtos.InstituicaoResponseDTO;
import br.com.Simulavest.domain.prova.dto.ProvaDTO;
import br.com.Simulavest.service.InstituicaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("api/sigla")
public class InstituicaoController {

    @Autowired
    private InstituicaoService service;

    @PostMapping
    public ResponseEntity cadastrarInstituicao(@RequestBody @Valid InstituicaoRequestDTO dto, UriComponentsBuilder uri){
        var instituicao = service.cadastrarInstituicao(dto);

        var uriCriada = uri.path("/api/sigla/{id}").buildAndExpand(instituicao.getId()).toUri();

        var dtoCriado = new InstituicaoDTO(instituicao);

        return ResponseEntity.created(uriCriada).body(new InstituicaoResponseDTO("Instituição criada com sucesso!", dtoCriado));
    }

    @GetMapping
    public ResponseEntity listarInstituicoes() {
        var instituicoes = service.listarInstituicoes();
        return ResponseEntity.ok(instituicoes);
    }

    @GetMapping("{id}")
    public ResponseEntity buscarInstituicaoPorId(@PathVariable UUID id) {
        var instituicao = service.buscarInstituicaoPorId(id);

        return ResponseEntity.ok(instituicao);
    }

    @PutMapping("{id}")
    public ResponseEntity atualizarInstituicao(@RequestBody @Valid InstituicaoAtualizacaoDTO dto, @PathVariable UUID id) {
        var instituicao = service.atualizarInstituicao(id, dto);

        var dtoAtualizado = new InstituicaoDTO(instituicao);

        return ResponseEntity.ok(new InstituicaoResponseDTO("Instituição atualizada com sucesso!", dtoAtualizado));
    }

    @PutMapping("/adicionar-prova")
    public ResponseEntity adicionarProva( @RequestBody @Valid ProvaDTO dto) {
        service.adicionarProva(dto);

        return ResponseEntity.ok().build();
    }

    @GetMapping("estatisticas/{universidadeId}/{materiaId}")
    public ResponseEntity obterEstatisticasPorUniversidadeEMateria(@PathVariable String universidadeId, @PathVariable UUID materiaId) {
        var estatisticas = service.obterEstatisticasPorUniversidadeEMateria(universidadeId, materiaId);
        return ResponseEntity.ok(estatisticas);
    }
}