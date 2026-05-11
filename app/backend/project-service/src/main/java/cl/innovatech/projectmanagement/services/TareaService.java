package cl.innovatech.projectmanagement.services;

import java.util.List;
import org.springframework.stereotype.Service;
import cl.innovatech.projectmanagement.entities.Tarea;
import cl.innovatech.projectmanagement.repository.TareaRepository;

@Service
public class TareaService {

    private final TareaRepository tareaRepository;

    public TareaService(TareaRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    public List<Tarea> getTareas() {
        return tareaRepository.findAll();
    }

    public Tarea getTareaById(Long id) {
        return tareaRepository.findById(id).orElse(null);
    }

    public Tarea save(Tarea tarea) {
        return tareaRepository.save(tarea);
    }
}
