package cl.innovatech.projectmanagement.services;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;
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

    public List<Proyecto> getProyectos() {
        return proyectoRepository.findAll();
    }

    public Optional<Proyecto> getProyectoById(Long id) {
        return proyectoRepository.findById(id);
    }

    public void add(Proyecto nuevoProyecto) {
        proyectoRepository.save(nuevoProyecto);
    }

    public void addTarea(Long projectId, Tarea nuevaTarea) {
        Proyecto proyecto = proyectoRepository.findById(projectId).orElse(null);
        if (proyecto != null) {
            System.out.println("Proyecto encontrado: " + proyecto);
            nuevaTarea.setProyecto(proyecto);
            tareaRepository.save(nuevaTarea);
        }
    }
}
