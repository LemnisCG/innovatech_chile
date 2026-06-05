package cl.innovatech.projectmanagement.services;

import cl.innovatech.projectmanagement.dtos.CreateProyectoDTO;
import cl.innovatech.projectmanagement.dtos.ProyectoDTO;
import cl.innovatech.projectmanagement.entities.Proyecto;
import cl.innovatech.projectmanagement.entities.Tarea;
import cl.innovatech.projectmanagement.repository.ProyectoRepository;
import cl.innovatech.projectmanagement.repository.TareaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProyectoServiceUnitTest {

    @Mock
    private ProyectoRepository proyectoRepository;

    @Mock
    private TareaRepository tareaRepository;

    @InjectMocks
    private ProyectoService proyectoService;

    @Test
    void getProyectos_returnsList() {
        Proyecto proyecto = new Proyecto(1L, "ERP", "ERP Desc", "EN_PROGRESO", "2026-01-01", "2026-12-31", "Comentarios");
        when(proyectoRepository.findAll()).thenReturn(List.of(proyecto));

        List<ProyectoDTO> result = proyectoService.getProyectos();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("ERP");
        verify(proyectoRepository, times(1)).findAll();
    }

    @Test
    void getProyectoById_whenExists_returnsProyecto() {
        Proyecto proyecto = new Proyecto(1L, "ERP", "ERP Desc", "EN_PROGRESO", "2026-01-01", "2026-12-31", "Comentarios");
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));

        Optional<ProyectoDTO> result = proyectoService.getProyectoById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getNombre()).isEqualTo("ERP");
        verify(proyectoRepository, times(1)).findById(1L);
    }

    @Test
    void getProyectoById_whenNotExists_returnsEmpty() {
        when(proyectoRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<ProyectoDTO> result = proyectoService.getProyectoById(1L);

        assertThat(result).isEmpty();
        verify(proyectoRepository, times(1)).findById(1L);
    }

    @Test
    void add_savesProyecto() {
        CreateProyectoDTO dto = new CreateProyectoDTO();
        dto.setNombre("ERP");
        dto.setDescripcion("ERP Desc");
        dto.setEstado("EN_PROGRESO");

        proyectoService.add(dto);

        verify(proyectoRepository, times(1)).save(any(Proyecto.class));
    }

    @Test
    void addTarea_whenProyectoExists_savesTarea() {
        Proyecto proyecto = new Proyecto(1L, "ERP", "ERP Desc", "EN_PROGRESO", "2026-01-01", "2026-12-31", "Comentarios");
        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea 1");

        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));

        proyectoService.addTarea(1L, tarea);

        assertThat(proyecto.getTareasDelProyecto()).contains(tarea);
        assertThat(tarea.getProyecto()).isEqualTo(proyecto);
        verify(tareaRepository, times(1)).save(tarea);
    }

    @Test
    void addTarea_whenProyectoDoesNotExist_doesNotSaveTarea() {
        Tarea tarea = new Tarea();
        tarea.setNombre("Tarea 1");

        when(proyectoRepository.findById(1L)).thenReturn(Optional.empty());

        proyectoService.addTarea(1L, tarea);

        verify(tareaRepository, never()).save(any(Tarea.class));
    }
}
