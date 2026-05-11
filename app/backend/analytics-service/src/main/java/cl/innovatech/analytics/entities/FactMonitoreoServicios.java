package cl.innovatech.analytics.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fact_monitoreo_servicios")
public class FactMonitoreoServicios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hecho_monitoreo")
    private Long idHechoMonitoreo;

    @ManyToOne
    @JoinColumn(name = "id_tiempo", nullable = false)
    private DimTiempo tiempo;

    @Column(name = "servicio_origen", nullable = false)
    private String servicioOrigen;

    @Column(name = "clase_interceptor")
    private String claseInterceptor;

    @Column(nullable = false)
    private String metodo;

    @Column(name = "latencia_ms", nullable = false)
    private Long latenciaMs;

    @Column(name = "codigo_http")
    private Integer codigoHttp;

    @Column(name = "fecha_registro", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    // Getters y Setters
    public Long getIdHechoMonitoreo() { return idHechoMonitoreo; }
    public void setIdHechoMonitoreo(Long idHechoMonitoreo) { this.idHechoMonitoreo = idHechoMonitoreo; }
    public DimTiempo getTiempo() { return tiempo; }
    public void setTiempo(DimTiempo tiempo) { this.tiempo = tiempo; }
    public String getServicioOrigen() { return servicioOrigen; }
    public void setServicioOrigen(String servicioOrigen) { this.servicioOrigen = servicioOrigen; }
    public String getClaseInterceptor() { return claseInterceptor; }
    public void setClaseInterceptor(String claseInterceptor) { this.claseInterceptor = claseInterceptor; }
    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }
    public Long getLatenciaMs() { return latenciaMs; }
    public void setLatenciaMs(Long latenciaMs) { this.latenciaMs = latenciaMs; }
    public Integer getCodigoHttp() { return codigoHttp; }
    public void setCodigoHttp(Integer codigoHttp) { this.codigoHttp = codigoHttp; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
