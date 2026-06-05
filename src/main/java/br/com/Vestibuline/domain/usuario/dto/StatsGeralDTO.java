package br.com.Vestibuline.domain.usuario.dto;

public record StatsGeralDTO(
        Long totalSimulados,
        Long totalAcertos,
        Long totalQuestoes,
        Integer percentualAcertos
) {
    public StatsGeralDTO(Long totalSimulados, Long totalAcertos, Long totalQuestoes) {
        this(
                totalSimulados,
                totalAcertos,
                totalQuestoes,
                (totalQuestoes != null && totalQuestoes > 0) ? (int) ((totalAcertos * 100) / totalQuestoes) : 0
        );
    }
}
