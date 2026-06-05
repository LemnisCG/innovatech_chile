package cl.innovatech.analytics.services;

import cl.innovatech.analytics.dtos.ProductivityKpiDTO;
import cl.innovatech.analytics.dtos.SystemHealthKpiDTO;
import cl.innovatech.analytics.repositories.FactGestionProyectosRepository;
import cl.innovatech.analytics.repositories.FactMonitoreoServiciosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnalyticsServiceUnitTest {

    @Mock
    private FactGestionProyectosRepository factGestionProyectosRepository;

    @Mock
    private FactMonitoreoServiciosRepository factMonitoreoServiciosRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void getProductivityKpi_returnsKpi() {
        ProductivityKpiDTO mockKpi = new ProductivityKpiDTO(4.2, 85.0, 3);
        when(factGestionProyectosRepository.getProductivityKpi()).thenReturn(mockKpi);

        ProductivityKpiDTO result = analyticsService.getProductivityKpi();

        assertThat(result).isNotNull();
        assertThat(result.getLeadTimePromedioDias()).isEqualTo(4.2);
        assertThat(result.getTasaCompletitud()).isEqualTo(85.0);
        assertThat(result.getTotalProyectosActivos()).isEqualTo(3);
        verify(factGestionProyectosRepository, times(1)).getProductivityKpi();
    }

    @Test
    void getSystemHealthKpi_returnsKpi() {
        SystemHealthKpiDTO mockKpi = new SystemHealthKpiDTO(120.5, 1.5);
        when(factMonitoreoServiciosRepository.getSystemHealthKpi()).thenReturn(mockKpi);

        SystemHealthKpiDTO result = analyticsService.getSystemHealthKpi();

        assertThat(result).isNotNull();
        assertThat(result.getLatenciaPromedioMs()).isEqualTo(120.5);
        assertThat(result.getTasaErroresPorcentaje()).isEqualTo(1.5);
        verify(factMonitoreoServiciosRepository, times(1)).getSystemHealthKpi();
    }
}
