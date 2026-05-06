package cl.innovatech.analytics.controllers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.innovatech.analytics.dtos.ProductivityKpiDTO;
import cl.innovatech.analytics.dtos.SystemHealthKpiDTO;

@RestController
@RequestMapping("/api/analytics/kpis")
public class AnalyticsController {

    private final JdbcTemplate jdbcTemplate;

    public AnalyticsController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/productivity")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE_PROYECTO')")
    public ProductivityKpiDTO getProductivityKpi() {
        // En un caso real, estas consultas irían sobre fact_gestion_proyectos agregando por tiempo
        String sql = "SELECT AVG(lead_time_promedio_dias) as avg_lead_time, " +
                     "AVG(tasa_completitud) as avg_completitud, " +
                     "COUNT(DISTINCT id_proyecto) as total_proyectos " +
                     "FROM fact_gestion_proyectos";

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new ProductivityKpiDTO(
                rs.getDouble("avg_lead_time"),
                rs.getDouble("avg_completitud"),
                rs.getInt("total_proyectos")
        ));
    }

    @GetMapping("/system-health")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE_PROYECTO')")
    public SystemHealthKpiDTO getSystemHealthKpi() {
        // En un caso real, esto consulta fact_monitoreo_servicios
        String sql = "SELECT AVG(latencia_ms) as avg_latencia, " +
                     "(SUM(CASE WHEN codigo_http >= 500 THEN 1 ELSE 0 END) * 100.0 / COUNT(*)) as tasa_errores " +
                     "FROM fact_monitoreo_servicios";

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new SystemHealthKpiDTO(
                rs.getDouble("avg_latencia"),
                rs.getDouble("tasa_errores")
        ));
    }
}
