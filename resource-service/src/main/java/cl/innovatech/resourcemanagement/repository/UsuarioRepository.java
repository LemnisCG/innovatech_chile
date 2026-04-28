package cl.innovatech.resourcemanagement.repository;

import cl.innovatech.resourcemanagement.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

}
