package cl.innovatech.resourcemanagement.controller;

import cl.innovatech.resourcemanagement.dto.AuthRequest;
import cl.innovatech.resourcemanagement.dto.AuthResponse;
import cl.innovatech.resourcemanagement.entities.Usuario;
import cl.innovatech.resourcemanagement.repository.UsuarioRepository;
import cl.innovatech.resourcemanagement.security.JwtUtil;
import cl.innovatech.resourcemanagement.dto.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername()).orElse(null);

        if (usuario != null && passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {

            if (!usuario.isActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is inactive");
            }

            // Update last login
            usuario.setLastLogin(LocalDateTime.now());
            usuarioRepository.save(usuario);

            // Generate token
            String token = jwtUtil.generateToken(usuario.getUsername(), usuario.getRoles());
            return ResponseEntity.ok(new AuthResponse(token));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setEmail(request.getEmail());
        usuario.setEspecialidad(request.getEspecialidad());
        usuario.setTelefono(request.getTelefono());
        usuario.setDireccion(request.getDireccion());
        usuario.setRut(request.getRut());
        usuario.setEstado(request.getEstado() != null ? request.getEstado() : "ACTIVO");
        usuario.setActive(true);
        usuario.setRoles(Set.of("JEFE_PROYECTO"));

        usuarioRepository.save(usuario);

        String token = jwtUtil.generateToken(usuario.getUsername(), usuario.getRoles());
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token));
    }
}
