package cl.innovatech.projectmanagement.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "proyectos")
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column
    private String estado;

    @Column(name = "fecha_inicio")
    private String fechaInicio;

    @Column(name = "fecha_fin")
    private String fechaFin;

    @Column(length = 1000)
    private String comentarios;

    // Relación One-To-Many: Un proyecto abarca muchas tareas.
    // 'mappedBy' indica que la variable 'proyecto' en la clase Tarea es la dueña de la relación.
    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tarea> tareasDelProyecto = new ArrayList<>();

    // Constructor vacío (Obligatorio para que Spring Data JPA funcione por debajo)
    public Proyecto() {}

    // Constructor con parámetros (Opcional, útil para ti manual)
    public Proyecto(Long id, String nombre, String descripcion, String estado, String fechaInicio, String fechaFin, String comentarios) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.comentarios = comentarios;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }

    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }

    public List<Tarea> getTareasDelProyecto() { return tareasDelProyecto; }
    public void setTareasDelProyecto(List<Tarea> tareasDelProyecto) { this.tareasDelProyecto = tareasDelProyecto; }
}