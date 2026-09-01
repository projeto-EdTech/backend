package br.com.Vestibuline.controller;

import br.com.Vestibuline.domain.logerro.LogErro;
import br.com.Vestibuline.domain.logerro.Severidade;
import br.com.Vestibuline.domain.logerro.StatusIncidente;
import br.com.Vestibuline.domain.logerro.dto.AtualizarLogErroDTO;
import br.com.Vestibuline.domain.logerro.dto.LogErroDetalheDTO;
import br.com.Vestibuline.domain.logerro.dto.LogErroResumoDTO;
import br.com.Vestibuline.service.LogErroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Ferramenta interna para devs mapearem erros de produção capturados por {@link LogErroService}.
 * Protegida por token compartilhado — ver {@link br.com.Vestibuline.infra.security.InternalAccessFilter} —
 * não por login de usuário.
 */
@RestController
@RequestMapping("/internal/logs-erro")
@RequiredArgsConstructor
public class LogErroController {

    private final LogErroService service;

    @GetMapping
    public ResponseEntity<Page<LogErroResumoDTO>> listar(
            @RequestParam(required = false) StatusIncidente status,
            @RequestParam(required = false) Severidade severidade,
            @RequestParam(required = false) String ambiente,
            @PageableDefault(size = 20, sort = "ultimaOcorrencia", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable
    ) {
        Page<LogErroResumoDTO> pagina = service.listar(status, severidade, ambiente, pageable)
                .map(LogErroResumoDTO::new);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LogErroDetalheDTO> buscarPorId(@PathVariable UUID id) {
        LogErro logErro = service.buscarPorId(id);
        return ResponseEntity.ok(new LogErroDetalheDTO(logErro));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LogErroDetalheDTO> atualizar(@PathVariable UUID id, @RequestBody @Valid AtualizarLogErroDTO dto) {
        LogErro atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(new LogErroDetalheDTO(atualizado));
    }
}
