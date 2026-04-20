package cl.innovatech.projectmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cl.innovatech.projectmanagement.entities.Tarea;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {

}
