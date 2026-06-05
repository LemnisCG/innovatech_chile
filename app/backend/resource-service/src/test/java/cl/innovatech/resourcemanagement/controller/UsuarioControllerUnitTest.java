package cl.innovatech.resourcemanagement.controller;

import cl.innovatech.resourcemanagement.entities.Usuario;
import cl.innovatech.resourcemanagement.services.UsuarioService;
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
class UsuarioControllerUnitTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @Test
    void getAllUsuarios_returnsList() {
        // Arrange
        List<Usuario> list = List.of(new Usuario(), new Usuario());
        when(usuarioService.getAllUsuarios()).thenReturn(list);

        // Act
        ResponseEntity<List<Usuario>> response = usuarioController.getAllUsuarios();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
    }

    @Test
    void getUsuarioById_whenExists_returnsOk() {
        // Arrange
        Usuario usuario = new Usuario();
        when(usuarioService.getUsuarioById(1L)).thenReturn(Optional.of(usuario));

        // Act
        ResponseEntity<Usuario> response = usuarioController.getUsuarioById(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(usuario);
    }

    @Test
    void getUsuarioById_whenNotExists_returnsNotFound() {
        // Arrange
        when(usuarioService.getUsuarioById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Usuario> response = usuarioController.getUsuarioById(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createUsuario_savesAndReturnsOk() {
        // Arrange
        Usuario usuario = new Usuario();
        Usuario saved = new Usuario();
        when(usuarioService.createUsuario(usuario)).thenReturn(saved);

        // Act
        ResponseEntity<Usuario> response = usuarioController.createUsuario(usuario);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(saved);
    }

    @Test
    void updateUsuario_whenExists_returnsOk() {
        // Arrange
        Usuario usuario = new Usuario();
        Usuario updated = new Usuario();
        when(usuarioService.updateUsuario(1L, usuario)).thenReturn(Optional.of(updated));

        // Act
        ResponseEntity<Usuario> response = usuarioController.updateUsuario(1L, usuario);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updated);
    }

    @Test
    void updateUsuario_whenNotExists_returnsNotFound() {
        // Arrange
        Usuario usuario = new Usuario();
        when(usuarioService.updateUsuario(1L, usuario)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Usuario> response = usuarioController.updateUsuario(1L, usuario);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteUsuario_whenExists_returnsOk() {
        // Arrange
        when(usuarioService.deleteUsuario(1L)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = usuarioController.deleteUsuario(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void deleteUsuario_whenNotExists_returnsNotFound() {
        // Arrange
        when(usuarioService.deleteUsuario(1L)).thenReturn(false);

        // Act
        ResponseEntity<Void> response = usuarioController.deleteUsuario(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
