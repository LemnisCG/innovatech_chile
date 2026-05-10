package cl.innovatech.analytics.dtos;

public class SystemHealthKpiDTO {
    private Double latenciaPromedioMs;
    private Double tasaErroresPorcentaje;

    public SystemHealthKpiDTO(Double latenciaPromedioMs, Double tasaErroresPorcentaje) {
        this.latenciaPromedioMs = latenciaPromedioMs != null ? latenciaPromedioMs : 0.0;
        this.tasaErroresPorcentaje = tasaErroresPorcentaje != null ? tasaErroresPorcentaje : 0.0;
    }

    public Double getLatenciaPromedioMs() {
        return latenciaPromedioMs;
    }

    public void setLatenciaPromedioMs(Double latenciaPromedioMs) {
        this.latenciaPromedioMs = latenciaPromedioMs;
    }

    public Double getTasaErroresPorcentaje() {
        return tasaErroresPorcentaje;
    }

    public void setTasaErroresPorcentaje(Double tasaErroresPorcentaje) {
        this.tasaErroresPorcentaje = tasaErroresPorcentaje;
    }
}
