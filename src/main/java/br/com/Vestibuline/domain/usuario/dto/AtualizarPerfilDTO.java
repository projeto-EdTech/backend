package br.com.Vestibuline.domain.usuario.dto;

import jakarta.validation.constraints.Size;

public record AtualizarPerfilDTO(
        @Size(max = 50, message = "A prova alvo deve ter no máximo 50 caracteres.") // ➔ Atualizado para 50!
        String provaAlvo,
        @Size(max = 100, message = "O curso alvo deve ter no máximo 100 caracteres.")
        String cursoAlvo,
        @Size(max = 150, message = "A instituição deve ter no máximo 150 caracteres.")
        String instituicao
) {}
