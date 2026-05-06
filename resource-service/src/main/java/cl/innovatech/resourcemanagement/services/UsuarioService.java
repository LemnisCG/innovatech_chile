package cl.innovatech.resourcemanagement.services;

import cl.innovatech.resourcemanagement.entities.Usuario;
import cl.innovatech.resourcemanagement.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> getUsuarioById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario createUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> login(String username, String password) {
        return usuarioRepository.findByUsername(username)
                .filter(usuario -> usuario.getPassword().equals(password) && usuario.isActive());
    }

    public Optional<Usuario> updateUsuario(Long id, Usuario usuario) {
        return usuarioRepository.findById(id).map(existingUsuario -> {
            existingUsuario.setUsername(usuario.getUsername());
            existingUsuario.setEmail(usuario.getEmail());
            existingUsuario.setPassword(usuario.getPassword());
            existingUsuario.setEspecialidad(usuario.getEspecialidad());
            existingUsuario.setTelefono(usuario.getTelefono());
            existingUsuario.setDireccion(usuario.getDireccion());
            existingUsuario.setRut(usuario.getRut());
            existingUsuario.setEstado(usuario.getEstado());
            existingUsuario.setRoles(usuario.getRoles());
            existingUsuario.setActive(usuario.isActive());
            return usuarioRepository.save(existingUsuario);
        });
    }

    public boolean deleteUsuario(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
