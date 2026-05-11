package cl.innovatech.resourcemanagement.config;

import cl.innovatech.resourcemanagement.entities.Usuario;
import cl.innovatech.resourcemanagement.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * DataSeeder para el resource-service.
 * Después de que Flyway inserta los usuarios con 'PENDING_HASH',
 * este componente reemplaza las contraseñas con hashes BCrypt reales.
 *
 * Contraseña de prueba para TODOS los usuarios: password123
 */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedPasswords(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            List<Usuario> pendingUsers = usuarioRepository.findAll().stream()
                    .filter(u -> "PENDING_HASH".equals(u.getPassword()))
                    .toList();

            if (pendingUsers.isEmpty()) {
                System.out.println(">>> [DataSeeder] No hay contraseñas pendientes. Saltando.");
                return;
            }

            System.out.println(">>> [DataSeeder] Hasheando contraseñas para " + pendingUsers.size() + " usuarios...");

            String hashedPassword = passwordEncoder.encode("password123");

            for (Usuario usuario : pendingUsers) {
                usuario.setPassword(hashedPassword);
                usuarioRepository.save(usuario);
                System.out.println("    ✓ " + usuario.getUsername() + " (rol: " + usuario.getRoles() + ")");
            }

            System.out.println(">>> [DataSeeder] Contraseñas actualizadas exitosamente.");
            System.out.println(">>> [DataSeeder] Password de prueba: password123");
        };
    }
}
