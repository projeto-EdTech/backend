package br.com.Vestibuline.domain.logerro;

// Mesmo fluxo descrito em docs/incidentes/PLANO_ESTABILIZACAO_POS_DEPLOY.md, seção "Processo de Correção"
public enum StatusIncidente {
    ABERTO,
    EM_ANDAMENTO,
    CORRIGIDO,
    VALIDADO
}
