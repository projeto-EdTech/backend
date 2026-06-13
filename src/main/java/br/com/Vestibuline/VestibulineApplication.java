package br.com.Vestibuline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAsync
public class VestibulineApplication {

	public static void main(String[] args) {
		SpringApplication.run(VestibulineApplication.class, args);
	}
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	/*@Bean
	public CommandLineRunner testarEnvioEmail(NotificacaArtigoService service) {
		return args -> {
			System.out.println("==========================================");
			System.out.println("🚀 INICIANDO TESTE DE NOTIFICACAO (MAIN) 🚀");
			System.out.println("==========================================");

			service.notificarArtigo(
					"Artigo Teste - Newsletter",
					"https://vestibuline.com.br/artigo-incrivel"
			);

			System.out.println("✅ Comando de envio disparado!");
		};
	}*/
}
