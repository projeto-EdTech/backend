package br.com.Vestibuline.domain.alternativa;

import br.com.Vestibuline.domain.alternativa.dto.AlternativaDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("Alternativa - construtor a partir de AlternativaDTO")
class AlternativaTest {

    @Test
    @DisplayName("aceita texto nulo (alternativa cujo conteúdo é uma imagem) sem lançar NPE")
    void construtor_aceitaTextoNulo() {
        var dto = new AlternativaDTO("B", null);

        assertThatCode(() -> new Alternativa(dto, "B")).doesNotThrowAnyException();

        var alternativa = new Alternativa(dto, "B");
        assertThat(alternativa.getTextoAlternativa()).isNull();
        assertThat(alternativa.getAlternativa()).isEqualTo("B");
        assertThat(alternativa.isCorreta()).isTrue();
    }

    @Test
    @DisplayName("continua fazendo trim no texto quando presente")
    void construtor_fazTrimNoTextoQuandoPresente() {
        var dto = new AlternativaDTO("A", "  Texto com espaços  ");

        var alternativa = new Alternativa(dto, "B");

        assertThat(alternativa.getTextoAlternativa()).isEqualTo("Texto com espaços");
        assertThat(alternativa.isCorreta()).isFalse();
    }
}
