package cl.innovatech.analytics.services;

import cl.innovatech.analytics.dtos.integration.ProyectoIntegrationDTO;
import cl.innovatech.analytics.dtos.integration.TareaIntegrationDTO;
import cl.innovatech.analytics.entities.DimProyecto;
import cl.innovatech.analytics.entities.DimTiempo;
import cl.innovatech.analytics.entities.FactGestionProyectos;
import cl.innovatech.analytics.repositories.DimProyectoRepository;
import cl.innovatech.analytics.repositories.DimTiempoRepository;
import cl.innovatech.analytics.repositories.FactGestionProyectosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ETLServiceUnitTest {

    @Mock
    private DimTiempoRepository dimTiempoRepository;

    @Mock
    private DimProyectoRepository dimProyectoRepository;

    @Mock
    private FactGestionProyectosRepository factGestionProyectosRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ETLService etlService;

    @Test
    void extractTransformLoad_successFlow() {
        LocalDate hoy = LocalDate.now();

        // Mock dimTiempoRepository
        DimTiempo dimTiempo = new DimTiempo();
        dimTiempo.setIdTiempo(1L);
        dimTiempo.setFecha(hoy);
        when(dimTiempoRepository.findByFecha(hoy)).thenReturn(Optional.of(dimTiempo));

        // Setup mock response for RestTemplate
        ProyectoIntegrationDTO projectDto = new ProyectoIntegrationDTO();
        projectDto.setId(10L);
        projectDto.setNombre("Test Project");
        projectDto.setEstado("EN_PROGRESO");
        projectDto.setFechaInicio("2026-06-01");
        projectDto.setFechaFin("2026-06-10");

        TareaIntegrationDTO tarea1 = new TareaIntegrationDTO();
        tarea1.setId(100L);
        tarea1.setEstado("COMPLETADO");

        TareaIntegrationDTO tarea2 = new TareaIntegrationDTO();
        tarea2.setId(101L);
        tarea2.setEstado("PENDIENTE");

        projectDto.setTareasDelProyecto(List.of(tarea1, tarea2));

        ResponseEntity<List<ProyectoIntegrationDTO>> responseEntity = new ResponseEntity<>(List.of(projectDto), HttpStatus.OK);
        
        when(restTemplate.exchange(
                eq("http://project-service:8081/proyectos"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Mock dimProyectoRepository
        DimProyecto dimProyecto = new DimProyecto();
        dimProyecto.setIdProyecto(10L);
        dimProyecto.setNombre("Test Project");
        when(dimProyectoRepository.findById(10L)).thenReturn(Optional.empty());
        when(dimProyectoRepository.save(any(DimProyecto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute ETL
        etlService.extractTransformLoad();

        // Verify saves and interactions
        verify(dimTiempoRepository, times(1)).findByFecha(hoy);
        verify(dimProyectoRepository, times(1)).findById(10L);
        verify(dimProyectoRepository, times(1)).save(any(DimProyecto.class));
        verify(factGestionProyectosRepository, times(1)).save(any(FactGestionProyectos.class));
    }

    @Test
    void extractTransformLoad_whenProjectsNull_doesNotProcess() {
        LocalDate hoy = LocalDate.now();
        DimTiempo dimTiempo = new DimTiempo();
        dimTiempo.setFecha(hoy);
        when(dimTiempoRepository.findByFecha(hoy)).thenReturn(Optional.of(dimTiempo));

        ResponseEntity<List<ProyectoIntegrationDTO>> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://project-service:8081/proyectos"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        etlService.extractTransformLoad();

        verify(dimProyectoRepository, never()).findById(anyLong());
        verify(factGestionProyectosRepository, never()).save(any(FactGestionProyectos.class));
    }

    @Test
    void extractTransformLoad_whenExceptionThrown_catchesAndLogs() {
        LocalDate hoy = LocalDate.now();
        when(dimTiempoRepository.findByFecha(hoy)).thenThrow(new RuntimeException("Database error"));

        // Should catch the exception and log, not throw it
        etlService.extractTransformLoad();

        verify(restTemplate, never()).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(ParameterizedTypeReference.class));
    }
}
