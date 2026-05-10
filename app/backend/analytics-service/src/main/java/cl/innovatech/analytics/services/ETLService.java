package cl.innovatech.analytics.services;

import cl.innovatech.analytics.entities.DimProyecto;
import cl.innovatech.analytics.entities.DimTiempo;
import cl.innovatech.analytics.entities.FactGestionProyectos;
import cl.innovatech.analytics.repositories.DimProyectoRepository;
import cl.innovatech.analytics.repositories.DimTiempoRepository;
import cl.innovatech.analytics.repositories.FactGestionProyectosRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import cl.innovatech.analytics.dtos.integration.ProyectoIntegrationDTO;
import cl.innovatech.analytics.dtos.integration.TareaIntegrationDTO;
import java.util.List;
import java.util.Optional;

@Service
public class ETLService {

    private static final Logger log = LoggerFactory.getLogger(ETLService.class);
    
    private final DimTiempoRepository dimTiempoRepository;
    private final DimProyectoRepository dimProyectoRepository;
    private final FactGestionProyectosRepository factGestionProyectosRepository;
    private final RestTemplate restTemplate;

    public ETLService(DimTiempoRepository dimTiempoRepository,
                      DimProyectoRepository dimProyectoRepository,
                      FactGestionProyectosRepository factGestionProyectosRepository,
                      RestTemplate restTemplate) {
        this.dimTiempoRepository = dimTiempoRepository;
        this.dimProyectoRepository = dimProyectoRepository;
        this.factGestionProyectosRepository = factGestionProyectosRepository;
        this.restTemplate = restTemplate;
    }

    // Se ejecuta todos los días a la medianoche (simulación cada 1 minuto para pruebas)
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void extractTransformLoad() {
        log.info("Iniciando proceso ETL de Gestión de Proyectos...");

        try {
            // 1. Asegurar la dimensión de tiempo de hoy
            LocalDate hoy = LocalDate.now();
            DimTiempo dimTiempo = asegurarDimensionTiempo(hoy);

            // 2. Llamada HTTP al project-service
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Auth-User", "analytics-system");
            headers.set("X-Auth-Roles", "ADMIN");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List<ProyectoIntegrationDTO>> response = restTemplate.exchange(
                    "http://project-service:8081/proyectos",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<ProyectoIntegrationDTO>>() {}
            );

            List<ProyectoIntegrationDTO> proyectos = response.getBody();

            if (proyectos != null) {
                for (ProyectoIntegrationDTO p : proyectos) {
                    DimProyecto dimProyecto = asegurarDimProyecto(p);

                    int totalTareas = 0;
                    int tareasCompletadas = 0;
                    
                    if (p.getTareasDelProyecto() != null) {
                        totalTareas = p.getTareasDelProyecto().size();
                        for (TareaIntegrationDTO t : p.getTareasDelProyecto()) {
                            if ("COMPLETADA".equalsIgnoreCase(t.getEstado()) || "DONE".equalsIgnoreCase(t.getEstado())) {
                                tareasCompletadas++;
                            }
                        }
                    }

                    int tareasPendientes = totalTareas - tareasCompletadas;
                    double tasaCompletitud = totalTareas == 0 ? 0.0 : (tareasCompletadas * 100.0) / totalTareas;

                    FactGestionProyectos fact = new FactGestionProyectos();
                    fact.setProyecto(dimProyecto);
                    fact.setTiempo(dimTiempo);
                    fact.setTotalTareas(totalTareas);
                    fact.setTareasCompletadas(tareasCompletadas);
                    fact.setTareasPendientes(tareasPendientes);
                    fact.setLeadTimePromedioDias(3.5); // Dato fijo simulado por ahora
                    fact.setTasaCompletitud(tasaCompletitud);

                    factGestionProyectosRepository.save(fact);
                }
            }

            log.info("Proceso ETL finalizado con éxito.");
        } catch (Exception e) {
            log.error("Error durante el proceso ETL: ", e);
        }
    }

    private DimTiempo asegurarDimensionTiempo(LocalDate fecha) {
        Optional<DimTiempo> optTiempo = dimTiempoRepository.findByFecha(fecha);
        if (optTiempo.isPresent()) {
            return optTiempo.get();
        }
        
        DimTiempo dimTiempo = new DimTiempo();
        dimTiempo.setFecha(fecha);
        dimTiempo.setDia(fecha.getDayOfMonth());
        dimTiempo.setMes(fecha.getMonthValue());
        dimTiempo.setAnio(fecha.getYear());
        dimTiempo.setTrimestre((fecha.getMonthValue() - 1) / 3 + 1);
        dimTiempo.setDiaSemana(fecha.getDayOfWeek().getValue());
        dimTiempo.setEsFinSemana(fecha.getDayOfWeek().getValue() == 6 || fecha.getDayOfWeek().getValue() == 7);
        
        return dimTiempoRepository.save(dimTiempo);
    }
    
    private DimProyecto asegurarDimProyecto(ProyectoIntegrationDTO p) {
        Optional<DimProyecto> opt = dimProyectoRepository.findById(p.getId());
        if (opt.isPresent()) {
            DimProyecto existente = opt.get();
            existente.setNombre(p.getNombre());
            existente.setEstado(p.getEstado() != null ? p.getEstado() : "UNKNOWN");
            return dimProyectoRepository.save(existente);
        }
        
        DimProyecto proyecto = new DimProyecto();
        proyecto.setIdProyecto(p.getId());
        proyecto.setNombre(p.getNombre());
        proyecto.setEstado(p.getEstado() != null ? p.getEstado() : "UNKNOWN");
        proyecto.setFechaInicio(p.getFechaInicio());
        proyecto.setFechaFin(p.getFechaFin());
        
        return dimProyectoRepository.save(proyecto);
    }
}
