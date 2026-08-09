package br.com.Vestibuline.controller;

import br.com.Vestibuline.domain.instituicao.dtos.EstatisticaMateriaDto;
import br.com.Vestibuline.domain.instituicao.dtos.InstituicaoAtualizacaoDTO;
import br.com.Vestibuline.domain.instituicao.dtos.InstituicaoCompletaDto;
import br.com.Vestibuline.domain.instituicao.dtos.InstituicaoDTO;
import br.com.Vestibuline.domain.instituicao.dtos.InstituicaoRequestDTO;
import br.com.Vestibuline.domain.instituicao.dtos.InstituicaoResponseDTO;
import br.com.Vestibuline.domain.prova.dto.ProvaDTO;
import br.com.Vestibuline.service.InstituicaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/instituicao")
public class InstituicaoController {

    @Autowired
    private InstituicaoService service;

    @PostMapping
    public ResponseEntity<InstituicaoResponseDTO> cadastrarInstituicao(@RequestBody @Valid InstituicaoRequestDTO dto, UriComponentsBuilder uri){
        var instituicao = service.cadastrarInstituicao(dto);

        var uriCriada = uri.path("/api/sigla/{id}").buildAndExpand(instituicao.getId()).toUri();

        var dtoCriado = new InstituicaoDTO(instituicao);

        return ResponseEntity.created(uriCriada).body(new InstituicaoResponseDTO("Instituição criada com sucesso!", dtoCriado));
    }

    @GetMapping
    public ResponseEntity<List<InstituicaoDTO>> listarInstituicoes() {
        var instituicoes = service.listarInstituicoes();
        return ResponseEntity.ok(instituicoes);
    }

    @GetMapping("{id}")
    public ResponseEntity<InstituicaoCompletaDto> buscarInstituicaoPorId(@PathVariable UUID id) {
        var instituicao = service.buscarInstituicaoPorId(id);

        return ResponseEntity.ok(instituicao);
    }

    @PutMapping("{id}")
    public ResponseEntity<InstituicaoResponseDTO> atualizarInstituicao(@RequestBody @Valid InstituicaoAtualizacaoDTO dto, @PathVariable UUID id) {
        var instituicao = service.atualizarInstituicao(id, dto);

        var dtoAtualizado = new InstituicaoDTO(instituicao);

        return ResponseEntity.ok(new InstituicaoResponseDTO("Instituição atualizada com sucesso!", dtoAtualizado));
    }

    @PostMapping("/adicionar-prova")
    public ResponseEntity<Void> adicionarProva( @RequestBody @Valid ProvaDTO dto) {
        service.adicionarProva(dto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("estatisticas/{universidadeId}/{materia}")
    public ResponseEntity<List<EstatisticaMateriaDto>> obterEstatisticasPorUniversidadeEMateria(@PathVariable String universidadeId, @PathVariable String materia) {
        var estatisticas = service.obterEstatisticasPorUniversidadeEMateria(universidadeId, materia);
        return ResponseEntity.ok(estatisticas);
    }
}