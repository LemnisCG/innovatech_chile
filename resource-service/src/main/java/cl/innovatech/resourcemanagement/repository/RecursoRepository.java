package cl.innovatech.resourcemanagement.repository;

import cl.innovatech.resourcemanagement.entities.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long> {

}
