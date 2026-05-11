package cl.innovatech.projectmanagement.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;
import cl.innovatech.projectmanagement.dtos.ProyectoDTO;
import cl.innovatech.projectmanagement.dtos.CreateProyectoDTO;
import cl.innovatech.projectmanagement.dtos.TareaDTO;
import cl.innovatech.projectmanagement.entities.Proyecto;
import cl.innovatech.projectmanagement.entities.Tarea;
import cl.innovatech.projectmanagement.repository.ProyectoRepository;
import cl.innovatech.projectmanagement.repository.TareaRepository;

@ApplicationScope
@Service
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final TareaRepository tareaRepository;

    public ProyectoService(ProyectoRepository proyectoRepository, TareaRepository tareaRepository) {
        this.proyectoRepository = proyectoRepository;
        this.tareaRepository = tareaRepository;
    }

    public List<ProyectoDTO> getProyectos() {
        return proyectoRepository.findAll().stream()
                .map(this::toProyectoDTO)
                .collect(Collectors.toList());
    }

    public Optional<ProyectoDTO> getProyectoById(Long id) {
        return proyectoRepository.findById(id).map(this::toProyectoDTO);
    }

    public void add(CreateProyectoDTO nuevoProyectoDTO) {
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre(nuevoProyectoDTO.getNombre());
        proyecto.setDescripcion(nuevoProyectoDTO.getDescripcion());
        proyecto.setEstado(nuevoProyectoDTO.getEstado());
        proyecto.setFechaInicio(nuevoProyectoDTO.getFechaInicio());
        proyecto.setFechaFin(nuevoProyectoDTO.getFechaFin());
        proyecto.setComentarios(nuevoProyectoDTO.getComentarios());
        proyectoRepository.save(proyecto);
    }

    public void addTarea(Long projectId, Tarea nuevaTarea) {
        Proyecto proyecto = proyectoRepository.findById(projectId).orElse(null);
        if (proyecto != null) {
            System.out.println("Proyecto encontrado: " + proyecto);
            nuevaTarea.setProyecto(proyecto);
            tareaRepository.save(nuevaTarea);
        }
    }

    private ProyectoDTO toProyectoDTO(Proyecto proyecto) {
        List<TareaDTO> tareasDTO = proyecto.getTareasDelProyecto().stream()
                .map(this::toTareaDTO)
                .collect(Collectors.toList());
        return new ProyectoDTO(
                proyecto.getId(),
                proyecto.getNombre(),
                proyecto.getDescripcion(),
                proyecto.getEstado(),
                proyecto.getFechaInicio(),
                proyecto.getFechaFin(),
                proyecto.getComentarios(),
                tareasDTO
        );
    }

    private TareaDTO toTareaDTO(Tarea tarea) {
        return new TareaDTO(
                tarea.getId(),
                tarea.getNombre(),
                tarea.getDescripcion(),
                tarea.getEstado(),
                tarea.getIdProfesionalAsignado(),
                tarea.getProyecto().getId(),
                tarea.getFechaInicio(),
                tarea.getFechaFin(),
                tarea.getComentarios()
        );
    }
}
