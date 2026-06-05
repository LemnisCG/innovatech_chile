package cl.innovatech.resourcemanagement.controller;

import cl.innovatech.resourcemanagement.dto.AuthRequest;
import cl.innovatech.resourcemanagement.dto.AuthResponse;
import cl.innovatech.resourcemanagement.dto.RegisterRequest;
import cl.innovatech.resourcemanagement.entities.Usuario;
import cl.innovatech.resourcemanagement.repository.UsuarioRepository;
import cl.innovatech.resourcemanagement.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerUnitTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_whenUserExistsAndPasswordMatchesAndIsActive_returnsOkWithToken() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        usuario.setPassword("hashed_password");
        usuario.setActive(true);
        usuario.setRoles(Set.of("JEFE_PROYECTO"));

        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("password123", "hashed_password")).thenReturn(true);
        when(jwtUtil.generateToken("testuser", usuario.getRoles())).thenReturn("mocked_token");

        // Act
        ResponseEntity<?> response = authController.login(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(AuthResponse.class);
        assertThat(((AuthResponse) response.getBody()).getToken()).isEqualTo("mocked_token");
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void login_whenUserExistsAndPasswordMatchesButIsInactive_returnsForbidden() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        usuario.setPassword("hashed_password");
        usuario.setActive(false);

        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("password123", "hashed_password")).thenReturn(true);

        // Act
        ResponseEntity<?> response = authController.login(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isEqualTo("User is inactive");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void login_whenPasswordDoesNotMatch_returnsUnauthorized() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("wrong_password");
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        usuario.setPassword("hashed_password");

        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrong_password", "hashed_password")).thenReturn(false);

        // Act
        ResponseEntity<?> response = authController.login(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo("Invalid credentials");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void login_whenUserDoesNotExist_returnsUnauthorized() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setUsername("nonexistent");
        request.setPassword("password123");
        when(usuarioRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = authController.login(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo("Invalid credentials");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void register_whenUsernameAlreadyExists_returnsConflict() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        when(usuarioRepository.findByUsername("existinguser")).thenReturn(Optional.of(new Usuario()));

        // Act
        ResponseEntity<?> response = authController.register(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isEqualTo("Username already exists");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void register_whenSuccessful_savesUserAndReturnsCreatedWithToken() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setEmail("newuser@example.com");
        request.setEspecialidad("Backend");
        request.setTelefono("123456789");
        request.setDireccion("Main St");
        request.setRut("12345678-9");
        request.setEstado("ACTIVO");

        when(usuarioRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");
        when(jwtUtil.generateToken("newuser", Set.of("JEFE_PROYECTO"))).thenReturn("mocked_token");

        // Act
        ResponseEntity<?> response = authController.register(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isInstanceOf(AuthResponse.class);
        assertThat(((AuthResponse) response.getBody()).getToken()).isEqualTo("mocked_token");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }
}
