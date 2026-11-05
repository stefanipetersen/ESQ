package br.com.fiap.esq.repository;

import br.com.fiap.esq.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByDsEmail(String dsEmail);

    boolean existsByDsEmail(String dsEmail);

    boolean existsByDsEmailAndIdUsuarioNot(String dsEmail, int idUsuario);
}
