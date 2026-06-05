package cl.innovatech.resourcemanagement.controller;

import cl.innovatech.resourcemanagement.entities.Recurso;
import cl.innovatech.resourcemanagement.services.RecursoService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecursoControllerUnitTest {

    @Mock
    private RecursoService recursoService;

    @InjectMocks
    private RecursoController recursoController;

    @Test
    void getAllRecursos_returnsList() {
        // Arrange
        List<Recurso> list = List.of(new Recurso(), new Recurso());
        when(recursoService.getAllRecursos()).thenReturn(list);

        // Act
        ResponseEntity<List<Recurso>> response = recursoController.getAllRecursos();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
    }

    @Test
    void getRecursoById_whenExists_returnsOk() {
        // Arrange
        Recurso recurso = new Recurso();
        when(recursoService.getRecursoById(1L)).thenReturn(Optional.of(recurso));

        // Act
        ResponseEntity<Recurso> response = recursoController.getRecursoById(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recurso);
    }

    @Test
    void getRecursoById_whenNotExists_returnsNotFound() {
        // Arrange
        when(recursoService.getRecursoById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Recurso> response = recursoController.getRecursoById(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getRecursosByUsuarioId_returnsList() {
        // Arrange
        List<Recurso> list = List.of(new Recurso());
        when(recursoService.getRecursosByUsuarioId(2L)).thenReturn(list);

        // Act
        ResponseEntity<List<Recurso>> response = recursoController.getRecursosByUsuarioId(2L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
    }

    @Test
    void getRecursosByProyectoId_returnsList() {
        // Arrange
        List<Recurso> list = List.of(new Recurso());
        when(recursoService.getRecursosByProyectoId(3L)).thenReturn(list);

        // Act
        ResponseEntity<List<Recurso>> response = recursoController.getRecursosByProyectoId(3L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
    }

    @Test
    void getRecursosByTareaId_returnsList() {
        // Arrange
        List<Recurso> list = List.of(new Recurso());
        when(recursoService.getRecursosByTareaId(4L)).thenReturn(list);

        // Act
        ResponseEntity<List<Recurso>> response = recursoController.getRecursosByTareaId(4L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
    }

    @Test
    void createRecurso_savesAndReturnsOk() {
        // Arrange
        Recurso recurso = new Recurso();
        Recurso saved = new Recurso();
        when(recursoService.createRecurso(recurso)).thenReturn(saved);

        // Act
        ResponseEntity<Recurso> response = recursoController.createRecurso(recurso);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(saved);
    }

    @Test
    void updateRecurso_whenExists_returnsOk() {
        // Arrange
        Recurso recurso = new Recurso();
        Recurso updated = new Recurso();
        when(recursoService.updateRecurso(1L, recurso)).thenReturn(Optional.of(updated));

        // Act
        ResponseEntity<Recurso> response = recursoController.updateRecurso(1L, recurso);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updated);
    }

    @Test
    void updateRecurso_whenNotExists_returnsNotFound() {
        // Arrange
        Recurso recurso = new Recurso();
        when(recursoService.updateRecurso(1L, recurso)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Recurso> response = recursoController.updateRecurso(1L, recurso);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteRecurso_whenExists_returnsOk() {
        // Arrange
        when(recursoService.deleteRecurso(1L)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = recursoController.deleteRecurso(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void deleteRecurso_whenNotExists_returnsNotFound() {
        // Arrange
        when(recursoService.deleteRecurso(1L)).thenReturn(false);

        // Act
        ResponseEntity<Void> response = recursoController.deleteRecurso(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
