package cl.innovatech.analytics.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "dim_tiempo")
public class DimTiempo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tiempo")
    private Long idTiempo;

    @Column(nullable = false, unique = true)
    private LocalDate fecha;

    @Column(nullable = false)
    private Integer dia;

    @Column(nullable = false)
    private Integer mes;

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false)
    private Integer trimestre;

    @Column(name = "dia_semana", nullable = false)
    private Integer diaSemana;

    @Column(name = "es_fin_semana", nullable = false)
    private Boolean esFinSemana;

    // Getters y Setters
    public Long getIdTiempo() { return idTiempo; }
    public void setIdTiempo(Long idTiempo) { this.idTiempo = idTiempo; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Integer getDia() { return dia; }
    public void setDia(Integer dia) { this.dia = dia; }
    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }
    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }
    public Integer getTrimestre() { return trimestre; }
    public void setTrimestre(Integer trimestre) { this.trimestre = trimestre; }
    public Integer getDiaSemana() { return diaSemana; }
    public void setDiaSemana(Integer diaSemana) { this.diaSemana = diaSemana; }
    public Boolean getEsFinSemana() { return esFinSemana; }
    public void setEsFinSemana(Boolean esFinSemana) { this.esFinSemana = esFinSemana; }
}
