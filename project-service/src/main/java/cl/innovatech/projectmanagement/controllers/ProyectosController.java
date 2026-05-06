package cl.innovatech.projectmanagement.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.ApplicationScope;
import java.util.List;
import cl.innovatech.projectmanagement.entities.Proyecto;
import cl.innovatech.projectmanagement.entities.Tarea;
import cl.innovatech.projectmanagement.services.ProyectoService;

@ApplicationScope
@RestController
@RequestMapping("/proyectos")
public class ProyectosController {

    private final ProyectoService proyectosService;

    public ProyectosController(ProyectoService proyectosService) {
        this.proyectosService = proyectosService;
    }

    @GetMapping()
    public List<Proyecto> getProyectos() {
        return proyectosService.getProyectos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proyecto> getProyectoById(@PathVariable Long id) {
        return proyectosService.getProyectoById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping()
    public void crearProyecto(@RequestBody Proyecto nuevoProyecto) {
        proyectosService.add(nuevoProyecto);
    }

    @PostMapping("/{id}/tareas")
    public void crearTarea(@PathVariable Long id, @RequestBody Tarea nuevaTarea) {
        System.out.println("Proyecto ID: " + id);
        System.out.println("Tarea: " + nuevaTarea);
        proyectosService.addTarea(id, nuevaTarea);
    }
}
