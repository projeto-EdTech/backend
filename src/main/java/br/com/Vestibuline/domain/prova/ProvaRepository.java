package br.com.Vestibuline.domain.prova;

import br.com.Vestibuline.domain.instituicao.Instituicao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProvaRepository extends JpaRepository<Prova, UUID> {
    Optional<Prova> findProvaByInstituicaoAndAno(Instituicao instituicao, int ano);


    Optional<Prova> findProvaByInstituicaoAndAnoAndDia(Instituicao instituicao, @NotNull(message = "O ano é obrigatório") int ano, @NotNull(message = "O dia é obrigatório") @Positive(message = "Dia deve ser maior que 0") int dia);
}
