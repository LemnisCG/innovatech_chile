package cl.innovatech.analytics.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;

@Service
public class ETLService {

    private static final Logger log = LoggerFactory.getLogger(ETLService.class);
    private final JdbcTemplate jdbcTemplate;

    public ETLService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Se ejecuta todos los días a la medianoche (simulación cada 1 minuto para pruebas)
    @Scheduled(cron = "0 * * * * *")
    public void extractTransformLoad() {
        log.info("Iniciando proceso ETL de Gestión de Proyectos...");

        try {
            // 1. Asegurar la dimensión de tiempo de hoy
            LocalDate hoy = LocalDate.now();
            insertarDimensionTiempo(hoy);

            // 1.5. Asegurar proyecto de prueba en dim_proyecto
            String sqlDimProyecto = "INSERT INTO dim_proyecto (id_proyecto, nombre, estado, fecha_inicio, fecha_fin) " +
                                    "VALUES (1, 'Proyecto Alpha', 'EN_PROGRESO', CURRENT_DATE, null) " +
                                    "ON CONFLICT (id_proyecto) DO NOTHING";
            jdbcTemplate.update(sqlDimProyecto);

            // 2. Aquí iría la llamada HTTP/Feign al project-service o consulta directa a su DB
            // para obtener el estado actual de los proyectos y tareas.
            // Para el ejemplo, simulamos la inserción en la tabla de hechos:
            
            String sqlInsertFact = "INSERT INTO fact_gestion_proyectos " +
                    "(id_proyecto, id_tiempo, total_tareas, tareas_completadas, tareas_pendientes, lead_time_promedio_dias, tasa_completitud) " +
                    "VALUES (?, (SELECT id_tiempo FROM dim_tiempo WHERE fecha = ?), ?, ?, ?, ?, ?)";
            
            // Simulación de carga (ID Proyecto 1)
            jdbcTemplate.update(sqlInsertFact, 1L, hoy, 10, 8, 2, 3.5, 80.0);
            
            log.info("Proceso ETL finalizado con éxito.");
        } catch (Exception e) {
            log.error("Error durante el proceso ETL: ", e);
        }
    }

    private void insertarDimensionTiempo(LocalDate fecha) {
        String sql = "INSERT INTO dim_tiempo (fecha, dia, mes, anio, trimestre, dia_semana, es_fin_semana) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT (fecha) DO NOTHING";
                     
        boolean isWeekend = (fecha.getDayOfWeek().getValue() == 6 || fecha.getDayOfWeek().getValue() == 7);
        int trimestre = (fecha.getMonthValue() - 1) / 3 + 1;
        
        jdbcTemplate.update(sql, fecha, fecha.getDayOfMonth(), fecha.getMonthValue(), fecha.getYear(), 
                trimestre, fecha.getDayOfWeek().getValue(), isWeekend);
    }
}
