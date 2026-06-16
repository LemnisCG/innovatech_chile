package cl.innovatech.analytics.dtos;

import cl.innovatech.analytics.dtos.integration.ProyectoIntegrationDTO;
import cl.innovatech.analytics.dtos.integration.TareaIntegrationDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DtosUnitTest {

    @Test
    void testProductivityKpiDTO() {
        ProductivityKpiDTO dto = new ProductivityKpiDTO(1.5, 95.0, 10);
        assertThat(dto.getLeadTimePromedioDias()).isEqualTo(1.5);
        assertThat(dto.getTasaCompletitud()).isEqualTo(95.0);
        assertThat(dto.getTotalProyectosActivos()).isEqualTo(10);

        dto.setLeadTimePromedioDias(2.5);
        dto.setTasaCompletitud(90.0);
        dto.setTotalProyectosActivos(8);

        assertThat(dto.getLeadTimePromedioDias()).isEqualTo(2.5);
        assertThat(dto.getTasaCompletitud()).isEqualTo(90.0);
        assertThat(dto.getTotalProyectosActivos()).isEqualTo(8);

        // Test constructor null-safety
        ProductivityKpiDTO nullDto = new ProductivityKpiDTO(null, null, null);
        assertThat(nullDto.getLeadTimePromedioDias()).isEqualTo(0.0);
        assertThat(nullDto.getTasaCompletitud()).isEqualTo(0.0);
        assertThat(nullDto.getTotalProyectosActivos()).isEqualTo(0);
    }

    @Test
    void testSystemHealthKpiDTO() {
        SystemHealthKpiDTO dto = new SystemHealthKpiDTO(150.0, 2.5);
        assertThat(dto.getLatenciaPromedioMs()).isEqualTo(150.0);
        assertThat(dto.getTasaErroresPorcentaje()).isEqualTo(2.5);

        dto.setLatenciaPromedioMs(200.0);
        dto.setTasaErroresPorcentaje(1.0);

        assertThat(dto.getLatenciaPromedioMs()).isEqualTo(200.0);
        assertThat(dto.getTasaErroresPorcentaje()).isEqualTo(1.0);

        // Test constructor null-safety
        SystemHealthKpiDTO nullDto = new SystemHealthKpiDTO(null, null);
        assertThat(nullDto.getLatenciaPromedioMs()).isEqualTo(0.0);
        assertThat(nullDto.getTasaErroresPorcentaje()).isEqualTo(0.0);
    }

    @Test
    void testProyectoIntegrationDTO() {
        ProyectoIntegrationDTO dto = new ProyectoIntegrationDTO();
        dto.setId(1L);
        dto.setNombre("ERP");
        dto.setEstado("EN_PROGRESO");
        dto.setFechaInicio("2026-01-01");
        dto.setFechaFin("2026-12-31");
        
        List<TareaIntegrationDTO> tareas = List.of(new TareaIntegrationDTO());
        dto.setTareasDelProyecto(tareas);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getNombre()).isEqualTo("ERP");
        assertThat(dto.getEstado()).isEqualTo("EN_PROGRESO");
        assertThat(dto.getFechaInicio()).isEqualTo("2026-01-01");
        assertThat(dto.getFechaFin()).isEqualTo("2026-12-31");
        assertThat(dto.getTareasDelProyecto()).isEqualTo(tareas);
    }

    @Test
    void testTareaIntegrationDTO() {
        TareaIntegrationDTO dto = new TareaIntegrationDTO();
        dto.setId(10L);
        dto.setNombre("DB Design");
        dto.setEstado("COMPLETADO");

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getNombre()).isEqualTo("DB Design");
        assertThat(dto.getEstado()).isEqualTo("COMPLETADO");
    }
}
