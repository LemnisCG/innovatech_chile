package cl.innovatech.analytics.dtos.integration;

import java.time.LocalDate;
import java.util.List;

public class ProyectoIntegrationDTO {
    private Long id;
    private String nombre;
    private String estado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<TareaIntegrationDTO> tareasDelProyecto;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public List<TareaIntegrationDTO> getTareasDelProyecto() { return tareasDelProyecto; }
    public void setTareasDelProyecto(List<TareaIntegrationDTO> tareasDelProyecto) { this.tareasDelProyecto = tareasDelProyecto; }
}
