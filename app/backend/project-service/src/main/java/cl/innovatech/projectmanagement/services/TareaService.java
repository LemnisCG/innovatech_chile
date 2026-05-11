package cl.innovatech.projectmanagement.services;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import cl.innovatech.projectmanagement.dtos.TareaDTO;
import cl.innovatech.projectmanagement.entities.Tarea;
import cl.innovatech.projectmanagement.repository.TareaRepository;

@Service
public class TareaService {

    private final TareaRepository tareaRepository;

    public TareaService(TareaRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    public List<TareaDTO> getTareas() {
        return tareaRepository.findAll().stream()
                .map(this::toTareaDTO)
                .collect(Collectors.toList());
    }

    public TareaDTO getTareaById(Long id) {
        Tarea tarea = tareaRepository.findById(id).orElse(null);
        return tarea != null ? toTareaDTO(tarea) : null;
    }

    public Tarea getTareaEntityById(Long id) {
        return tareaRepository.findById(id).orElse(null);
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
