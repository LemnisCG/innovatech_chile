package cl.innovatech.resourcemanagement.services;

import cl.innovatech.resourcemanagement.entities.Recurso;
import cl.innovatech.resourcemanagement.entities.Usuario;
import cl.innovatech.resourcemanagement.repository.RecursoRepository;
import cl.innovatech.resourcemanagement.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecursoService {

    private final RecursoRepository recursoRepository;
    private final UsuarioRepository usuarioRepository;

    public RecursoService(RecursoRepository recursoRepository, UsuarioRepository usuarioRepository) {
        this.recursoRepository = recursoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Recurso> getAllRecursos() {
        return recursoRepository.findAll();
    }

    public Optional<Recurso> getRecursoById(Long id) {
        return recursoRepository.findById(id);
    }

    // Buscar todas las asignaciones de un usuario
    public List<Recurso> getRecursosByUsuarioId(Long usuarioId) {
        return recursoRepository.findByUsuarioId(usuarioId);
    }

    // Buscar todas las asignaciones a un proyecto
    public List<Recurso> getRecursosByProyectoId(Long idProyecto) {
        return recursoRepository.findByIdProyecto(idProyecto);
    }

    // Buscar la asignación a una tarea específica
    public List<Recurso> getRecursosByTareaId(Long idTarea) {
        return recursoRepository.findByIdTarea(idTarea);
    }

    public Recurso createRecurso(Recurso recurso) {
        // Validar que el usuario exista
        Usuario usuario = usuarioRepository.findById(recurso.getUsuario().getId())
                .orElseThrow(
                        () -> new RuntimeException("Usuario no encontrado con id: " + recurso.getUsuario().getId()));
        recurso.setUsuario(usuario);
        return recursoRepository.save(recurso);
    }

    public Optional<Recurso> updateRecurso(Long id, Recurso recurso) {
        return recursoRepository.findById(id).map(existing -> {
            existing.setIdProyecto(recurso.getIdProyecto());
            existing.setIdTarea(recurso.getIdTarea());
            existing.setRolEnProyecto(recurso.getRolEnProyecto());
            existing.setHorasAsignadas(recurso.getHorasAsignadas());
            existing.setFechaAsignacion(recurso.getFechaAsignacion());
            existing.setFechaLiberacion(recurso.getFechaLiberacion());
            existing.setEstado(recurso.getEstado());
            return recursoRepository.save(existing);
        });
    }

    public boolean deleteRecurso(Long id) {
        if (recursoRepository.existsById(id)) {
            recursoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
