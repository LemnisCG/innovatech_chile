package cl.innovatech.resourcemanagement.controller;

import cl.innovatech.resourcemanagement.entities.Recurso;
import cl.innovatech.resourcemanagement.services.RecursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recursos")
public class RecursoController {

    @Autowired
    private RecursoService recursoService;

    @GetMapping
    public ResponseEntity<List<Recurso>> getAllRecursos() {
        return ResponseEntity.ok(recursoService.getAllRecursos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recurso> getRecursoById(@PathVariable Long id) {
        return recursoService.getRecursoById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Obtener asignaciones por usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Recurso>> getRecursosByUsuarioId(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(recursoService.getRecursosByUsuarioId(usuarioId));
    }

    // Obtener asignaciones por proyecto
    @GetMapping("/proyecto/{idProyecto}")
    public ResponseEntity<List<Recurso>> getRecursosByProyectoId(@PathVariable Long idProyecto) {
        return ResponseEntity.ok(recursoService.getRecursosByProyectoId(idProyecto));
    }

    // Obtener asignaciones por tarea
    @GetMapping("/tarea/{idTarea}")
    public ResponseEntity<List<Recurso>> getRecursosByTareaId(@PathVariable Long idTarea) {
        return ResponseEntity.ok(recursoService.getRecursosByTareaId(idTarea));
    }

    @PostMapping
    public ResponseEntity<Recurso> createRecurso(@RequestBody Recurso recurso) {
        return ResponseEntity.ok(recursoService.createRecurso(recurso));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recurso> updateRecurso(@PathVariable Long id, @RequestBody Recurso recurso) {
        return recursoService.updateRecurso(id, recurso)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecurso(@PathVariable Long id) {
        if (recursoService.deleteRecurso(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
