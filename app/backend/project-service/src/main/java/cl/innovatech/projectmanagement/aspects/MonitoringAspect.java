package cl.innovatech.projectmanagement.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Aspect
@Component
public class MonitoringAspect {

    private static final Logger log = LoggerFactory.getLogger(MonitoringAspect.class);
    private final JdbcTemplate jdbcTemplate;

    public MonitoringAspect(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Around("execution(* cl.innovatech.projectmanagement.services.ProyectoService.*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        
        Object proceed;
        try {
            proceed = joinPoint.proceed();
        } finally {
            long executionTime = System.currentTimeMillis() - start;
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
            
            log.info("{} {} ejecutado en {} ms", className, methodName, executionTime);
            saveMetricsAsync("project-service", className, methodName, executionTime, null);
        }
        return proceed;
    }

    @Async
    public void saveMetricsAsync(String serviceName, String className, String methodName, long latency, Integer httpStatus) {
        try {
            // Asegurar dimensión de tiempo
            LocalDate hoy = LocalDate.now();
            String sqlTiempo = "INSERT INTO dim_tiempo (fecha, dia, mes, anio, trimestre, dia_semana, es_fin_semana) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT (fecha) DO NOTHING";
            boolean isWeekend = (hoy.getDayOfWeek().getValue() == 6 || hoy.getDayOfWeek().getValue() == 7);
            int trimestre = (hoy.getMonthValue() - 1) / 3 + 1;
            
            jdbcTemplate.update(sqlTiempo, hoy, hoy.getDayOfMonth(), hoy.getMonthValue(), hoy.getYear(), 
                                trimestre, hoy.getDayOfWeek().getValue(), isWeekend);

            // Insertar hecho de monitoreo
            String sqlInsert = "INSERT INTO fact_monitoreo_servicios " +
                    "(id_tiempo, servicio_origen, clase_interceptor, metodo, latencia_ms, codigo_http) " +
                    "VALUES ((SELECT id_tiempo FROM dim_tiempo WHERE fecha = ?), ?, ?, ?, ?, ?)";
                    
            jdbcTemplate.update(sqlInsert, hoy, serviceName, className, methodName, latency, httpStatus);
        } catch (Exception e) {
            log.error("Error al guardar métricas de monitoreo: ", e);
        }
    }
}
