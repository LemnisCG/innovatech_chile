package cl.innovatech.projectmanagement.config;

import cl.innovatech.projectmanagement.entities.Proyecto;
import cl.innovatech.projectmanagement.entities.Tarea;
import cl.innovatech.projectmanagement.repository.ProyectoRepository;
import cl.innovatech.projectmanagement.repository.TareaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSeederTest {

    @Mock
    private ProyectoRepository proyectoRepository;

    @Mock
    private TareaRepository tareaRepository;

    @InjectMocks
    private DataSeeder dataSeeder;

    @Test
    void seedProjectData_whenDataAlreadyExists_doesNotSeed() throws Exception {
        // Arrange
        when(proyectoRepository.count()).thenReturn(5L);

        // Act
        CommandLineRunner runner = dataSeeder.seedProjectData(proyectoRepository, tareaRepository);
        assertThat(runner).isNotNull();
        runner.run();

        // Assert
        verify(proyectoRepository, times(1)).count();
        verify(proyectoRepository, never()).save(any(Proyecto.class));
        verify(tareaRepository, never()).save(any(Tarea.class));
    }

    @Test
    void seedProjectData_whenNoDataExists_seedsData() throws Exception {
        // Arrange
        when(proyectoRepository.count()).thenReturn(0L);

        // Act
        CommandLineRunner runner = dataSeeder.seedProjectData(proyectoRepository, tareaRepository);
        assertThat(runner).isNotNull();
        runner.run();

        // Assert
        verify(proyectoRepository, times(1)).count();
        // Since 3 projects are created
        verify(proyectoRepository, times(3)).save(any(Proyecto.class));
        // Since 12 tasks are created
        verify(tareaRepository, times(12)).save(any(Tarea.class));
    }
}
