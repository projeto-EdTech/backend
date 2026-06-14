package br.com.Vestibuline.domain.planner.interfaces;

/**
 * Projection para mapear cada linha retornada pela query nativa do Planner.
 *
 * A query retorna uma linha por (matéria, conteúdo), já com os rankings
 * calculados via ROW_NUMBER(). O service agrupa as linhas em MateriaDesempenhoDTO.
 */
public interface PlannerProjection {

    // --- Matéria ---
    String getMateriaId();
    String getMateriaNome();
    Long   getMateriaTotalRespostas();
    Long   getMateriaTotalErros();
    Double getMateriaTaxaErro();

    // --- Conteúdo ---
    String getConteudoId();
    String getConteudoNome();
    Long   getConteudoTotalRespostas();
    Long   getConteudoTotalErros();
    Double getConteudoTaxaErro();
}
