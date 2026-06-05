package cl.innovatech.analytics.controllers;

import cl.innovatech.analytics.dtos.ProductivityKpiDTO;
import cl.innovatech.analytics.dtos.SystemHealthKpiDTO;
import cl.innovatech.analytics.services.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsControllerUnitTest {

    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private AnalyticsController analyticsController;

    @Test
    void getProductivityKpi_returnsDto() {
        // Arrange
        ProductivityKpiDTO dto = new ProductivityKpiDTO(10.0, 80.0, 5);
        when(analyticsService.getProductivityKpi()).thenReturn(dto);

        // Act
        ProductivityKpiDTO result = analyticsController.getProductivityKpi();

        // Assert
        assertThat(result).isEqualTo(dto);
        verify(analyticsService, times(1)).getProductivityKpi();
    }

    @Test
    void getSystemHealthKpi_returnsDto() {
        // Arrange
        SystemHealthKpiDTO dto = new SystemHealthKpiDTO(250.0, 1.5);
        when(analyticsService.getSystemHealthKpi()).thenReturn(dto);

        // Act
        SystemHealthKpiDTO result = analyticsController.getSystemHealthKpi();

        // Assert
        assertThat(result).isEqualTo(dto);
        verify(analyticsService, times(1)).getSystemHealthKpi();
    }
}
