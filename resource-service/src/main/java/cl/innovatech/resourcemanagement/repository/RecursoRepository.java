package cl.innovatech.resourcemanagement.repository;

import cl.innovatech.resourcemanagement.entities.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long> {
    List<Recurso> findByUsuarioId(Long usuarioId);
    List<Recurso> findByIdProyecto(Long idProyecto);
    List<Recurso> findByIdTarea(Long idTarea);
}
