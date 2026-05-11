package cl.innovatech.analytics.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "dim_recurso")
public class DimRecurso {

    @Id
    @Column(name = "id_recurso")
    private Long idRecurso;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String rol;

    @Column
    private String departamento;

    // Getters y Setters
    public Long getIdRecurso() { return idRecurso; }
    public void setIdRecurso(Long idRecurso) { this.idRecurso = idRecurso; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
}
