package cl.innovatech.resourcemanagement.config;

import cl.innovatech.resourcemanagement.entities.Usuario;
import cl.innovatech.resourcemanagement.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSeederTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DataSeeder dataSeeder;

    @Test
    void seedPasswords_whenNoPendingUsers_doesNotEncodeOrSave() throws Exception {
        // Arrange
        Usuario u1 = new Usuario();
        u1.setUsername("user1");
        u1.setPassword("already_hashed_password");
        
        when(usuarioRepository.findAll()).thenReturn(List.of(u1));

        // Act
        CommandLineRunner runner = dataSeeder.seedPasswords(usuarioRepository, passwordEncoder);
        assertThat(runner).isNotNull();
        runner.run();

        // Assert
        verify(usuarioRepository, times(1)).findAll();
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void seedPasswords_whenPendingUsersExist_encodesAndSavesAll() throws Exception {
        // Arrange
        Usuario u1 = new Usuario();
        u1.setUsername("user1");
        u1.setPassword("PENDING_HASH");

        Usuario u2 = new Usuario();
        u2.setUsername("user2");
        u2.setPassword("already_hashed_password");

        Usuario u3 = new Usuario();
        u3.setUsername("user3");
        u3.setPassword("PENDING_HASH");

        when(usuarioRepository.findAll()).thenReturn(List.of(u1, u2, u3));
        when(passwordEncoder.encode("password123")).thenReturn("new_hash_value");

        // Act
        CommandLineRunner runner = dataSeeder.seedPasswords(usuarioRepository, passwordEncoder);
        assertThat(runner).isNotNull();
        runner.run();

        // Assert
        verify(usuarioRepository, times(1)).findAll();
        verify(passwordEncoder, times(1)).encode("password123");
        
        verify(usuarioRepository, times(1)).save(u1);
        verify(usuarioRepository, times(1)).save(u3);
        verify(usuarioRepository, never()).save(u2);

        assertThat(u1.getPassword()).isEqualTo("new_hash_value");
        assertThat(u3.getPassword()).isEqualTo("new_hash_value");
    }
}
