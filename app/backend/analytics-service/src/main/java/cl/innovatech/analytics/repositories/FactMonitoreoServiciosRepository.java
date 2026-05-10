package cl.innovatech.analytics.repositories;

import cl.innovatech.analytics.entities.FactMonitoreoServicios;
import cl.innovatech.analytics.dtos.SystemHealthKpiDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FactMonitoreoServiciosRepository extends JpaRepository<FactMonitoreoServicios, Long> {

    @Query("SELECT new cl.innovatech.analytics.dtos.SystemHealthKpiDTO(" +
           "AVG(f.latenciaMs), " +
           "(SUM(CASE WHEN f.codigoHttp >= 500 THEN 1.0 ELSE 0.0 END) * 100.0 / COUNT(f.idHechoMonitoreo))) " +
           "FROM FactMonitoreoServicios f")
    SystemHealthKpiDTO getSystemHealthKpi();
}
