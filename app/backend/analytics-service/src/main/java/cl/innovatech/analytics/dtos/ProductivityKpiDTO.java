package cl.innovatech.analytics.dtos;

public class ProductivityKpiDTO {
    private Double leadTimePromedioDias;
    private Double tasaCompletitud;
    private Integer totalProyectosActivos;

    public ProductivityKpiDTO(Double leadTimePromedioDias, Double tasaCompletitud, Integer totalProyectosActivos) {
        this.leadTimePromedioDias = leadTimePromedioDias != null ? leadTimePromedioDias : 0.0;
        this.tasaCompletitud = tasaCompletitud != null ? tasaCompletitud : 0.0;
        this.totalProyectosActivos = totalProyectosActivos != null ? totalProyectosActivos : 0;
    }

    public Double getLeadTimePromedioDias() {
        return leadTimePromedioDias;
    }

    public void setLeadTimePromedioDias(Double leadTimePromedioDias) {
        this.leadTimePromedioDias = leadTimePromedioDias;
    }

    public Double getTasaCompletitud() {
        return tasaCompletitud;
    }

    public void setTasaCompletitud(Double tasaCompletitud) {
        this.tasaCompletitud = tasaCompletitud;
    }

    public Integer getTotalProyectosActivos() {
        return totalProyectosActivos;
    }

    public void setTotalProyectosActivos(Integer totalProyectosActivos) {
        this.totalProyectosActivos = totalProyectosActivos;
    }
}
