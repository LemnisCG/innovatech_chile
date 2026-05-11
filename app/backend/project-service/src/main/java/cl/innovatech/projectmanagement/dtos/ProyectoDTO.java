package cl.innovatech.projectmanagement.dtos;

import java.util.List;

public class ProyectoDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private String estado;
    private String fechaInicio;
    private String fechaFin;
    private String comentarios;
    private List<TareaDTO> tareasDelProyecto;

    // Constructor vacío
    public ProyectoDTO() {}

    // Constructor con parámetros
    public ProyectoDTO(Long id, String nombre, String descripcion, String estado, String fechaInicio, String fechaFin, String comentarios, List<TareaDTO> tareasDelProyecto) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.comentarios = comentarios;
        this.tareasDelProyecto = tareasDelProyecto;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public List<TareaDTO> getTareasDelProyecto() {
        return tareasDelProyecto;
    }

    public void setTareasDelProyecto(List<TareaDTO> tareasDelProyecto) {
        this.tareasDelProyecto = tareasDelProyecto;
    }
}