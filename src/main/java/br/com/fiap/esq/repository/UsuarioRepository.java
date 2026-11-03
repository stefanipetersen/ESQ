package br.com.fiap.esq.repository;

import br.com.fiap.esq.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    boolean existsByDsEmail(String dsEmail);

    boolean existsByDsEmailAndIdUsuarioNot(String dsEmail, int idUsuario);
}
