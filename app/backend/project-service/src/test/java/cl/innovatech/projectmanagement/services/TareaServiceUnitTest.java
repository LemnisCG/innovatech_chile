package cl.innovatech.projectmanagement.services;

import cl.innovatech.projectmanagement.dtos.TareaDTO;
import cl.innovatech.projectmanagement.entities.Proyecto;
import cl.innovatech.projectmanagement.entities.Tarea;
import cl.innovatech.projectmanagement.repository.TareaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TareaServiceUnitTest {

    @Mock
    private TareaRepository tareaRepository;

    @InjectMocks
    private TareaService tareaService;

    @Test
    void getTareas_returnsList() {
        Proyecto proyecto = new Proyecto();
        proyecto.setId(10L);

        Tarea tarea = new Tarea();
        tarea.setId(1L);
        tarea.setNombre("Diseño DB");
        tarea.setDescripcion("Modelado relacional");
        tarea.setEstado("PENDIENTE");
        tarea.setIdProfesionalAsignado(100L);
        tarea.setProyecto(proyecto);
        tarea.setFechaInicio("2026-06-01");
        tarea.setFechaFin("2026-06-10");
        tarea.setComentarios("Comentarios");

        when(tareaRepository.findAll()).thenReturn(List.of(tarea));

        List<TareaDTO> result = tareaService.getTareas();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Diseño DB");
        assertThat(result.get(0).getIdProyecto()).isEqualTo(10L);
        verify(tareaRepository, times(1)).findAll();
    }

    @Test
    void getTareaById_whenExists_returnsDTO() {
        Proyecto proyecto = new Proyecto();
        proyecto.setId(10L);

        Tarea tarea = new Tarea();
        tarea.setId(1L);
        tarea.setNombre("Diseño DB");
        tarea.setProyecto(proyecto);

        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));

        TareaDTO result = tareaService.getTareaById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Diseño DB");
        verify(tareaRepository, times(1)).findById(1L);
    }

    @Test
    void getTareaById_whenNotExists_returnsNull() {
        when(tareaRepository.findById(1L)).thenReturn(Optional.empty());

        TareaDTO result = tareaService.getTareaById(1L);

        assertThat(result).isNull();
        verify(tareaRepository, times(1)).findById(1L);
    }

    @Test
    void getTareaEntityById_whenExists_returnsEntity() {
        Tarea tarea = new Tarea();
        tarea.setId(1L);

        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));

        Tarea result = tareaService.getTareaEntityById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(tareaRepository, times(1)).findById(1L);
    }

    @Test
    void getTareaEntityById_whenNotExists_returnsNull() {
        when(tareaRepository.findById(1L)).thenReturn(Optional.empty());

        Tarea result = tareaService.getTareaEntityById(1L);

        assertThat(result).isNull();
        verify(tareaRepository, times(1)).findById(1L);
    }

    @Test
    void save_savesAndReturnsTarea() {
        Tarea tarea = new Tarea();
        tarea.setNombre("Diseño DB");

        when(tareaRepository.save(tarea)).thenReturn(tarea);

        Tarea result = tareaService.save(tarea);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Diseño DB");
        verify(tareaRepository, times(1)).save(tarea);
    }
}
