package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.discord.TokenOtpDiscordRepository;
import br.com.Vestibuline.domain.nota_corte.NotaCorteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;

@Service
public class RotinaLimpezaService {

    private static final Logger logger = LoggerFactory.getLogger(RotinaLimpezaService.class);

    @Autowired
    private NotaCorteRepository repository;

    @Autowired
    private TokenOtpDiscordRepository tokenOtpDiscordRepository;

    @Scheduled(cron = "0 0 0 1 1 *")
    @Transactional
    public void limparNotasCorteAntigas() {

        int anoLimite = Year.now().getValue() - 5;
        repository.deletarNotasCorteAntiga(anoLimite);
        logger.info("Limpeza anual de notas de corte executada para o ano limite: {}", anoLimite);
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void limparTokensOtpDiscordExpirados() {
        int removidos = tokenOtpDiscordRepository.deletarUsadosOuExpirados(LocalDateTime.now());
        logger.info("Limpeza diária de tokens OTP do Discord: {} registro(s) removido(s).", removidos);
    }
}
