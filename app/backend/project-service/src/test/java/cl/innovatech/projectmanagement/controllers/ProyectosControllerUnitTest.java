package cl.innovatech.projectmanagement.controllers;

import cl.innovatech.projectmanagement.dtos.CreateProyectoDTO;
import cl.innovatech.projectmanagement.dtos.CreateTareaDTO;
import cl.innovatech.projectmanagement.dtos.ProyectoDTO;
import cl.innovatech.projectmanagement.services.ProyectoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProyectosControllerUnitTest {

    @Mock
    private ProyectoService proyectoService;

    @InjectMocks
    private ProyectosController proyectosController;

    @Test
    void getProyectos_returnsList() {
        ProyectoDTO dto = new ProyectoDTO(1L, "Proj", "Desc", "ACTIVE", "2026-01-01", "2026-12-31", "Comment", List.of());
        when(proyectoService.getProyectos()).thenReturn(List.of(dto));

        List<ProyectoDTO> result = proyectosController.getProyectos();

        assertThat(result).hasSize(1);
        verify(proyectoService, times(1)).getProyectos();
    }

    @Test
    void getProyectoById_whenExists_returnsOk() {
        ProyectoDTO dto = new ProyectoDTO(1L, "Proj", "Desc", "ACTIVE", "2026-01-01", "2026-12-31", "Comment", List.of());
        when(proyectoService.getProyectoById(1L)).thenReturn(Optional.of(dto));

        ResponseEntity<ProyectoDTO> result = proyectosController.getProyectoById(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(dto);
    }

    @Test
    void getProyectoById_whenNotExists_returnsNotFound() {
        when(proyectoService.getProyectoById(1L)).thenReturn(Optional.empty());

        ResponseEntity<ProyectoDTO> result = proyectosController.getProyectoById(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void crearProyecto_callsService() {
        CreateProyectoDTO dto = new CreateProyectoDTO();
        proyectosController.crearProyecto(dto);
        verify(proyectoService, times(1)).add(dto);
    }

    @Test
    void crearTarea_callsService() {
        CreateTareaDTO dto = new CreateTareaDTO();
        dto.setNombre("Task");
        dto.setDescripcion("Desc");
        dto.setEstado("PENDIENTE");
        dto.setIdProfesionalAsignado(100L);
        dto.setFechaInicio("2026-06-01");
        dto.setFechaFin("2026-06-10");
        dto.setComentarios("Comment");

        proyectosController.crearTarea(1L, dto);

        verify(proyectoService, times(1)).addTarea(eq(1L), any());
    }
}
