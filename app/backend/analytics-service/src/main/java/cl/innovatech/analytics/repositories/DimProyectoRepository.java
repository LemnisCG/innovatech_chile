package cl.innovatech.analytics.repositories;

import cl.innovatech.analytics.entities.DimProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DimProyectoRepository extends JpaRepository<DimProyecto, Long> {
}
