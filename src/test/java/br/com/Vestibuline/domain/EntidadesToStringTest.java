package br.com.Vestibuline.domain;

import br.com.Vestibuline.domain.alternativa.Alternativa;
import br.com.Vestibuline.domain.artigo.Artigo;
import br.com.Vestibuline.domain.artigo.ArtigoImagem;
import br.com.Vestibuline.domain.conteudo.Conteudo;
import br.com.Vestibuline.domain.instituicao.Instituicao;
import br.com.Vestibuline.domain.materia.Materia;
import br.com.Vestibuline.domain.prova.Prova;
import br.com.Vestibuline.domain.questao.Questao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Entidades JPA com relação bidirecional usavam @Data (Lombok) sem excluir os campos
 * de relação do toString(), causando StackOverflowError em qualquer log/debug que
 * imprimisse a entidade (ver RELATORIO_AUDITORIA.md, item 4.4). Estes testes montam o
 * ciclo bidirecional manualmente e garantem que toString() não estoura a pilha.
 */
@DisplayName("Entidades JPA - toString() não deve estourar StackOverflowError em ciclos bidirecionais")
class EntidadesToStringTest {

    @Test
    @DisplayName("Questao <-> Alternativa")
    void questaoAlternativa_naoEstouraStack() {
        Questao questao = new Questao();
        Alternativa alternativa = new Alternativa();
        alternativa.setQuestao(questao);
        questao.setAlternativas(java.util.List.of(alternativa));

        assertThatCode(questao::toString).doesNotThrowAnyException();
        assertThatCode(alternativa::toString).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Materia <-> Conteudo")
    void materiaConteudo_naoEstouraStack() {
        Materia materia = new Materia();
        Conteudo conteudo = new Conteudo();
        conteudo.setMateria(materia);
        materia.setConteudos(java.util.List.of(conteudo));

        assertThatCode(materia::toString).doesNotThrowAnyException();
        assertThatCode(conteudo::toString).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Instituicao <-> Prova")
    void instituicaoProva_naoEstouraStack() {
        Instituicao instituicao = new Instituicao();
        Prova prova = new Prova();
        prova.setInstituicao(instituicao);
        instituicao.setProvas(java.util.List.of(prova));

        assertThatCode(instituicao::toString).doesNotThrowAnyException();
        assertThatCode(prova::toString).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Artigo <-> ArtigoImagem")
    void artigoArtigoImagem_naoEstouraStack() {
        Artigo artigo = new Artigo();
        ArtigoImagem imagem = new ArtigoImagem();
        imagem.setArtigo(artigo);
        artigo.setImagens(java.util.List.of(imagem));

        assertThatCode(artigo::toString).doesNotThrowAnyException();
        assertThatCode(imagem::toString).doesNotThrowAnyException();
    }
}
