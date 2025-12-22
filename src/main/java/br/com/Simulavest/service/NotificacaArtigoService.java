package br.com.Simulavest.service;

import br.com.Simulavest.domain.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificacaArtigoService {

    private final UsuarioRepository repository;
    private final JavaMailSender javaMailSender;

    private static final long DELAY_ENTRE_EMAIL = 2000; //milissegundos


    @Async
    public void notificarArtigo(String tituloArtigo, String linkArtigo) {

        log.info("Iniciando processo de notificação em massa...");

        List<String> emails = repository.buscarEmailsNewsletter();

        if (emails.isEmpty()) {
            log.info("Nenhum usuário inscrito na newsletter.");
            return;
        }

        log.info("Encontrados {} inscritos. Iniciando envios...", emails.size());

        int enviados = 0;
        for (String email: emails) {
            try {
                enviarEmail(email, tituloArtigo, linkArtigo);
                enviados++;

                log.info("E-mail {}/{} enviado para: {}", enviados, emails.size(), email);

                Thread.sleep(DELAY_ENTRE_EMAIL);
            } catch (InterruptedException e) {
                log.error("Processo de envio interrompido!", e);
                throw new RuntimeException(e);
            } catch (Exception e) {
                log.error("Falha ao enviar para {}: {}", email, e.getMessage());
            }
        }
        log.info("Processo de notificação finalizado.");
    }
    private void enviarEmail(String destinatario, String titulo, String link) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("nao-responda@simulavest.com.br");
        message.setTo(destinatario);
        message.setSubject("Novo Artigo: " + titulo);
        message.setText("Olá! Um novo artigo saiu: " + titulo + "\nLeia em: " + link);

        javaMailSender.send(message);
    }
}
