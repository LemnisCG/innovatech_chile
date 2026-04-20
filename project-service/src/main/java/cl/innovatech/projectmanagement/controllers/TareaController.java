package cl.innovatech.projectmanagement.controllers;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cl.innovatech.projectmanagement.entities.Tarea;
import cl.innovatech.projectmanagement.services.TareaService;

@RestController
@RequestMapping("/tareas")
public class TareaController {

    private final TareaService tareaService;

    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    @GetMapping
    public List<Tarea> getTareas() {
        return tareaService.getTareas();
    }

    @GetMapping("/{id}")
    public Tarea getTareaById(@PathVariable Long id) {
        return tareaService.getTareaById(id);
    }
}
