package cl.innovatech.analytics.services;

import cl.innovatech.analytics.dtos.ProductivityKpiDTO;
import cl.innovatech.analytics.dtos.SystemHealthKpiDTO;
import cl.innovatech.analytics.repositories.FactGestionProyectosRepository;
import cl.innovatech.analytics.repositories.FactMonitoreoServiciosRepository;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    private final FactGestionProyectosRepository factGestionProyectosRepository;
    private final FactMonitoreoServiciosRepository factMonitoreoServiciosRepository;

    public AnalyticsService(FactGestionProyectosRepository factGestionProyectosRepository, 
                            FactMonitoreoServiciosRepository factMonitoreoServiciosRepository) {
        this.factGestionProyectosRepository = factGestionProyectosRepository;
        this.factMonitoreoServiciosRepository = factMonitoreoServiciosRepository;
    }

    public ProductivityKpiDTO getProductivityKpi() {
        return factGestionProyectosRepository.getProductivityKpi();
    }

    public SystemHealthKpiDTO getSystemHealthKpi() {
        return factMonitoreoServiciosRepository.getSystemHealthKpi();
    }
}
