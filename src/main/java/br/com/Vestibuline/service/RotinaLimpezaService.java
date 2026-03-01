package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.nota_corte.NotaCorteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
public class RotinaLimpezaService {

    @Autowired
    private NotaCorteRepository repository;

    @Scheduled(cron = "0 0 0 1 1 *")
    @Transactional
    public void limparNotasCorteAntigas() {

        int anoLimite = Year.now().getValue() - 5;
        repository.deletarNotasCorteAntiga(anoLimite);
        System.out.println("📅 Limpeza anual de notas de corte executada para o ano limite: " + anoLimite);
    }
}
