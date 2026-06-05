package cl.innovatech.resourcemanagement.services;

import cl.innovatech.resourcemanagement.entities.Usuario;
import cl.innovatech.resourcemanagement.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceUnitTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void getAllUsuarios_returnsList() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("admin");
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<Usuario> result = usuarioService.getAllUsuarios();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("admin");
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void getUsuarioById_whenExists_returnsUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = usuarioService.getUsuarioById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void createUsuario_encodesPasswordAndSaves() {
        Usuario usuario = new Usuario();
        usuario.setUsername("johndoe");
        usuario.setPassword("rawPassword");

        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario result = usuarioService.createUsuario(usuario);

        assertThat(result).isNotNull();
        assertThat(result.getPassword()).isEqualTo("encodedPassword");
        verify(passwordEncoder, times(1)).encode("rawPassword");
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void login_whenCredentialsCorrectAndActive_returnsUsuario() {
        Usuario usuario = new Usuario();
        usuario.setUsername("johndoe");
        usuario.setPassword("encodedPassword");
        usuario.setActive(true);

        when(usuarioRepository.findByUsername("johndoe")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);

        Optional<Usuario> result = usuarioService.login("johndoe", "rawPassword");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("johndoe");
    }

    @Test
    void login_whenCredentialsIncorrect_returnsEmpty() {
        Usuario usuario = new Usuario();
        usuario.setUsername("johndoe");
        usuario.setPassword("encodedPassword");
        usuario.setActive(true);

        when(usuarioRepository.findByUsername("johndoe")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        Optional<Usuario> result = usuarioService.login("johndoe", "wrongPassword");

        assertThat(result).isEmpty();
    }

    @Test
    void login_whenInactive_returnsEmpty() {
        Usuario usuario = new Usuario();
        usuario.setUsername("johndoe");
        usuario.setPassword("encodedPassword");
        usuario.setActive(false);

        when(usuarioRepository.findByUsername("johndoe")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);

        Optional<Usuario> result = usuarioService.login("johndoe", "rawPassword");

        assertThat(result).isEmpty();
    }

    @Test
    void updateUsuario_whenExists_updatesAndSaves() {
        Usuario existing = new Usuario();
        existing.setId(1L);
        existing.setUsername("oldName");
        existing.setPassword("oldEncodedPassword");

        Usuario updateInfo = new Usuario();
        updateInfo.setUsername("newName");
        updateInfo.setEmail("new@email.com");
        updateInfo.setPassword("newRawPassword");
        updateInfo.setEspecialidad("Backend");
        updateInfo.setTelefono("9999");
        updateInfo.setDireccion("Santiago");
        updateInfo.setRut("1-9");
        updateInfo.setEstado("ACTIVO");
        updateInfo.setRoles(Set.of("ADMIN"));
        updateInfo.setActive(true);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("newRawPassword")).thenReturn("newEncodedPassword");
        when(usuarioRepository.save(existing)).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Usuario> result = usuarioService.updateUsuario(1L, updateInfo);

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("newName");
        assertThat(result.get().getPassword()).isEqualTo("newEncodedPassword");
        assertThat(result.get().getEmail()).isEqualTo("new@email.com");
        verify(passwordEncoder, times(1)).encode("newRawPassword");
        verify(usuarioRepository, times(1)).save(existing);
    }

    @Test
    void updateUsuario_whenExistsWithoutNewPassword_doesNotEncodePassword() {
        Usuario existing = new Usuario();
        existing.setId(1L);
        existing.setUsername("oldName");
        existing.setPassword("oldEncodedPassword");

        Usuario updateInfo = new Usuario();
        updateInfo.setUsername("newName");
        updateInfo.setPassword(""); // Empty password

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(usuarioRepository.save(existing)).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Usuario> result = usuarioService.updateUsuario(1L, updateInfo);

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("newName");
        assertThat(result.get().getPassword()).isEqualTo("oldEncodedPassword");
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void deleteUsuario_whenExists_deletesAndReturnsTrue() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        boolean result = usuarioService.deleteUsuario(1L);

        assertThat(result).isTrue();
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUsuario_whenNotExists_returnsFalse() {
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        boolean result = usuarioService.deleteUsuario(1L);

        assertThat(result).isFalse();
        verify(usuarioRepository, never()).deleteById(anyLong());
    }
}
