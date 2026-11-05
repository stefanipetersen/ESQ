package br.com.fiap.esq.repository;

import br.com.fiap.esq.model.Configuracao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfiguracaoRepository extends JpaRepository<Configuracao, Integer> {

    List<Configuracao> findByIdUsuario(int idUsuario);

    boolean existsByIdUsuario(int idUsuario);

    boolean existsByIdUsuarioAndIdConfiguracaoNot(int idUsuario, int idConfiguracao);
}
