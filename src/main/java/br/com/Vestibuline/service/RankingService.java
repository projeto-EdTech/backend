package br.com.Vestibuline.service;

import br.com.Vestibuline.domain.usuario.Usuario;
import br.com.Vestibuline.domain.usuario.UsuarioRepository;
import br.com.Vestibuline.domain.usuario.dto.RankDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class RankingService {

    private final UsuarioRepository repository;

    public RankingService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public List<RankDTO> getRanking() {
        List<RankDTO> ranking = repository.findAllByOrderByRankPointsDesc();
        return IntStream.range(0, ranking.size())
                .mapToObj(i -> new RankDTO(
                        i + 1,
                        ranking.get(i).username(),
                        ranking.get(i).userEmail(),
                        ranking.get(i).rankPoints(),
                        ranking.get(i).rank()
                ))
                .toList();
    }
}
