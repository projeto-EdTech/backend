package br.com.Vestibuline.controller;

import br.com.Vestibuline.domain.historico.dto.HistoricoDTO;
import br.com.Vestibuline.domain.resposta.dto.SimuladoInputDTO;
import br.com.Vestibuline.domain.usuario.Usuario;
import br.com.Vestibuline.service.HistoricoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/simulados")
public class SimuladoFinalizadoController {

    @Autowired
    private HistoricoService service;

    @PostMapping("/finalizar")
    public ResponseEntity<HistoricoDTO> finalizarSimulado(@AuthenticationPrincipal Usuario usuarioLogado,
                                                            @RequestBody @Valid SimuladoInputDTO dto) {

        var historico = service.cadastrarHistorico(usuarioLogado.getId(), dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(historico);
    }
}
