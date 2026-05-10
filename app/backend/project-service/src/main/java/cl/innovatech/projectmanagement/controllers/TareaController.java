package cl.innovatech.projectmanagement.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    /**
     * Actualiza el estado de una tarea.
     * Solo pueden hacerlo:
     *   - El profesional asignado a la tarea (MIEMBRO)
     *   - Usuarios con rol ADMIN o JEFE_PROYECTO
     *
     * Payload esperado: { "estado": "EN_PROGRESO", "userId": 5 }
     * userId es el ID del usuario que solicita el cambio (enviado por el frontend).
     */
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE_PROYECTO', 'MIEMBRO')")
    public ResponseEntity<?> updateEstado(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        Tarea tarea = tareaService.getTareaById(id);

        if (tarea == null) {
            return ResponseEntity.notFound().build();
        }

        if (!payload.containsKey("estado")) {
            return ResponseEntity.badRequest().body("El campo 'estado' es obligatorio.");
        }

        // Obtener info del usuario autenticado desde el SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdminOrJefe = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN") || role.equals("ROLE_JEFE_PROYECTO"));

        // Si NO es admin/jefe, verificar que sea el profesional asignado
        if (!isAdminOrJefe) {
            String userIdStr = payload.get("userId");
            if (userIdStr == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No tienes permiso para modificar esta tarea.");
            }

            Long requestingUserId = Long.parseLong(userIdStr);
            if (tarea.getIdProfesionalAsignado() == null ||
                !tarea.getIdProfesionalAsignado().equals(requestingUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Solo el profesional asignado puede modificar el estado de esta tarea.");
            }
        }

        tarea.setEstado(payload.get("estado"));
        Tarea updated = tareaService.save(tarea);
        return ResponseEntity.ok(updated);
    }
}
