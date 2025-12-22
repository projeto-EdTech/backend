package br.com.Simulavest;

import br.com.Simulavest.service.NotificacaArtigoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAsync
public class SimulavestApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimulavestApplication.class, args);
	}
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public CommandLineRunner testarEnvioEmail(NotificacaArtigoService service) {
		return args -> {
			System.out.println("==========================================");
			System.out.println("🚀 INICIANDO TESTE DE NOTIFICACAO (MAIN) 🚀");
			System.out.println("==========================================");

			service.notificarArtigo(
					"Artigo Teste - Newsletter",
					"https://simulavest.com.br/artigo-incrivel"
			);

			System.out.println("✅ Comando de envio disparado!");
		};
	}
}
