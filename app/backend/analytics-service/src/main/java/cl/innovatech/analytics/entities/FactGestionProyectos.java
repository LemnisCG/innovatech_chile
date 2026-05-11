package cl.innovatech.analytics.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "fact_gestion_proyectos")
public class FactGestionProyectos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hecho_proyecto")
    private Long idHechoProyecto;

    @ManyToOne
    @JoinColumn(name = "id_proyecto", nullable = false)
    private DimProyecto proyecto;

    @ManyToOne
    @JoinColumn(name = "id_recurso")
    private DimRecurso recurso;

    @ManyToOne
    @JoinColumn(name = "id_tiempo", nullable = false)
    private DimTiempo tiempo;

    @Column(name = "total_tareas")
    private Integer totalTareas;

    @Column(name = "tareas_completadas")
    private Integer tareasCompletadas;

    @Column(name = "tareas_pendientes")
    private Integer tareasPendientes;

    @Column(name = "lead_time_promedio_dias")
    private Double leadTimePromedioDias;

    @Column(name = "tasa_completitud")
    private Double tasaCompletitud;

    // Getters y Setters
    public Long getIdHechoProyecto() { return idHechoProyecto; }
    public void setIdHechoProyecto(Long idHechoProyecto) { this.idHechoProyecto = idHechoProyecto; }
    public DimProyecto getProyecto() { return proyecto; }
    public void setProyecto(DimProyecto proyecto) { this.proyecto = proyecto; }
    public DimRecurso getRecurso() { return recurso; }
    public void setRecurso(DimRecurso recurso) { this.recurso = recurso; }
    public DimTiempo getTiempo() { return tiempo; }
    public void setTiempo(DimTiempo tiempo) { this.tiempo = tiempo; }
    public Integer getTotalTareas() { return totalTareas; }
    public void setTotalTareas(Integer totalTareas) { this.totalTareas = totalTareas; }
    public Integer getTareasCompletadas() { return tareasCompletadas; }
    public void setTareasCompletadas(Integer tareasCompletadas) { this.tareasCompletadas = tareasCompletadas; }
    public Integer getTareasPendientes() { return tareasPendientes; }
    public void setTareasPendientes(Integer tareasPendientes) { this.tareasPendientes = tareasPendientes; }
    public Double getLeadTimePromedioDias() { return leadTimePromedioDias; }
    public void setLeadTimePromedioDias(Double leadTimePromedioDias) { this.leadTimePromedioDias = leadTimePromedioDias; }
    public Double getTasaCompletitud() { return tasaCompletitud; }
    public void setTasaCompletitud(Double tasaCompletitud) { this.tasaCompletitud = tasaCompletitud; }
}
