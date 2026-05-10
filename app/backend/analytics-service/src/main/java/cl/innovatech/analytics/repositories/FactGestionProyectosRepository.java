package cl.innovatech.analytics.repositories;

import cl.innovatech.analytics.entities.FactGestionProyectos;
import cl.innovatech.analytics.dtos.ProductivityKpiDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FactGestionProyectosRepository extends JpaRepository<FactGestionProyectos, Long> {

    @Query("SELECT new cl.innovatech.analytics.dtos.ProductivityKpiDTO(" +
           "AVG(f.leadTimePromedioDias), AVG(f.tasaCompletitud), CAST(COUNT(DISTINCT f.proyecto.idProyecto) AS int)) " +
           "FROM FactGestionProyectos f")
    ProductivityKpiDTO getProductivityKpi();
}
