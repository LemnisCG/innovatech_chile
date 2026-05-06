package cl.innovatech.analytics.dtos;

public class ProductivityKpiDTO {
    private Double leadTimePromedioDias;
    private Double tasaCompletitud;
    private Integer totalProyectosActivos;

    public ProductivityKpiDTO(Double leadTimePromedioDias, Double tasaCompletitud, Integer totalProyectosActivos) {
        this.leadTimePromedioDias = leadTimePromedioDias;
        this.tasaCompletitud = tasaCompletitud;
        this.totalProyectosActivos = totalProyectosActivos;
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
