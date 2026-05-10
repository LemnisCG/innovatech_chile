package cl.innovatech.analytics.dtos.integration;

import java.util.List;

public class ProyectoIntegrationDTO {
    private Long id;
    private String nombre;
    private String estado;
    private String fechaInicio;
    private String fechaFin;
    private List<TareaIntegrationDTO> tareasDelProyecto;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }
    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }
    public List<TareaIntegrationDTO> getTareasDelProyecto() { return tareasDelProyecto; }
    public void setTareasDelProyecto(List<TareaIntegrationDTO> tareasDelProyecto) { this.tareasDelProyecto = tareasDelProyecto; }
}
