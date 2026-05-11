package cl.innovatech.projectmanagement.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.ApplicationScope;

import cl.innovatech.projectmanagement.dtos.ProyectoDTO;
import cl.innovatech.projectmanagement.dtos.CreateProyectoDTO;
import cl.innovatech.projectmanagement.dtos.CreateTareaDTO;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE_PROYECTO', 'MIEMBRO', 'CLIENTE')")
    public List<ProyectoDTO> getProyectos() {
        return proyectosService.getProyectos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE_PROYECTO', 'MIEMBRO', 'CLIENTE')")
    public ResponseEntity<ProyectoDTO> getProyectoById(@PathVariable Long id) {
        return proyectosService.getProyectoById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE_PROYECTO')")
    public void crearProyecto(@RequestBody CreateProyectoDTO nuevoProyecto) {
        proyectosService.add(nuevoProyecto);
    }

    @PostMapping("/{id}/tareas")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE_PROYECTO', 'MIEMBRO')")
    public void crearTarea(@PathVariable Long id, @RequestBody CreateTareaDTO nuevaTareaDTO) {
        Tarea nuevaTarea = new Tarea();
        nuevaTarea.setNombre(nuevaTareaDTO.getNombre());
        nuevaTarea.setDescripcion(nuevaTareaDTO.getDescripcion());
        nuevaTarea.setEstado(nuevaTareaDTO.getEstado());
        nuevaTarea.setIdProfesionalAsignado(nuevaTareaDTO.getIdProfesionalAsignado());
        nuevaTarea.setFechaInicio(nuevaTareaDTO.getFechaInicio());
        nuevaTarea.setFechaFin(nuevaTareaDTO.getFechaFin());
        nuevaTarea.setComentarios(nuevaTareaDTO.getComentarios());
        System.out.println("Proyecto ID: " + id);
        System.out.println("Tarea: " + nuevaTarea);
        proyectosService.addTarea(id, nuevaTarea);
    }
}
