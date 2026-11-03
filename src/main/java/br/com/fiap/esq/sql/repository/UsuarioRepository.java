package br.com.fiap.esq.sql.repository;

import br.com.fiap.esq.sql.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    boolean existsByDsEmail(String dsEmail);

    boolean existsByDsEmailAndIdUsuarioNot(String dsEmail, int idUsuario);
}
