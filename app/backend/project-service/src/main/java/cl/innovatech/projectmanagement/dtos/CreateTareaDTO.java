package cl.innovatech.projectmanagement.dtos;

public class CreateTareaDTO {

    private String nombre;
    private String descripcion;
    private String estado;
    private Long idProfesionalAsignado;
    private Long idProyecto;
    private String fechaInicio;
    private String fechaFin;
    private String comentarios;

    // Constructor vacío
    public CreateTareaDTO() {}

    // Constructor con parámetros
    public CreateTareaDTO(String nombre, String descripcion, String estado, Long idProfesionalAsignado, Long idProyecto, String fechaInicio, String fechaFin, String comentarios) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
        this.idProfesionalAsignado = idProfesionalAsignado;
        this.idProyecto = idProyecto;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.comentarios = comentarios;
    }

    // Getters y Setters
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

    public Long getIdProfesionalAsignado() {
        return idProfesionalAsignado;
    }

    public void setIdProfesionalAsignado(Long idProfesionalAsignado) {
        this.idProfesionalAsignado = idProfesionalAsignado;
    }

    public Long getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(Long idProyecto) {
        this.idProyecto = idProyecto;
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
}