package cl.innovatech.resourcemanagement.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "recursos")
@Data
public class Recurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Usuario (el profesional asignado)
    // Esta es una FK real dentro de la misma base de datos del resource-service
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // ID del proyecto que viene del project-service (NO es una FK real,
    // porque ese dato vive en otra base de datos, otro microservicio)
    @Column(name = "id_proyecto", nullable = false)
    private Long idProyecto;

    // ID de la tarea que viene del project-service (mismo caso)
    @Column(name = "id_tarea")
    private Long idTarea;

    @Column(name = "rol_en_proyecto", nullable = false)
    private String rolEnProyecto;

    @Column(name = "horas_asignadas")
    private Integer horasAsignadas;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDate fechaAsignacion;

    @Column(name = "fecha_liberacion")
    private LocalDate fechaLiberacion;

    @Column(nullable = false)
    private String estado; // ASIGNADO, LIBERADO, EN_ESPERA
}
