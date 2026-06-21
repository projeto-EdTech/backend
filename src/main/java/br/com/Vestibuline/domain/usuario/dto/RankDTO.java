package br.com.Vestibuline.domain.usuario.dto;

import br.com.Vestibuline.domain.usuario.Rank;

public record RankDTO (
         int RankNumber,
         String username,
         String userEmail,
         int rankPoints,
         Rank rank
) {}
