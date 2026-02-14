package br.com.Simulavest.service;

import br.com.Simulavest.domain.nota_corte.NotaCorteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
public class RotinaLimpezaService {

    @Autowired
    private NotaCorteRepository repository;

    @Async
    @Transactional
    public void limparNotasCorteAntigas() {

        int anoLimite = Year.now().getValue() - 5;
        repository.deletarNotasCorteAntiga(anoLimite);
        System.out.println("🧹 Faxina pós-cadastro concluída em segundo plano!");
    }
}
