package cl.innovatech.projectmanagement.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.ApplicationScope;
import java.util.List;
import cl.innovatech.projectmanagement.entities.Proyecto;
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

    @PostMapping()
    public void crearProyecto(@RequestBody Proyecto nuevoProyecto) {
        proyectosService.add(nuevoProyecto);
    }

}
