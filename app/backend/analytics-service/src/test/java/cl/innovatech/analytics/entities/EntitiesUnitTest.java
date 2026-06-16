package cl.innovatech.analytics.entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EntitiesUnitTest {

    @Test
    void testDimProyecto() {
        DimProyecto p = new DimProyecto();
        LocalDate now = LocalDate.now();

        p.setIdProyecto(1L);
        p.setNombre("ERP");
        p.setEstado("EN_PROGRESO");
        p.setFechaInicio(now);
        p.setFechaFin(now.plusMonths(6));

        assertThat(p.getIdProyecto()).isEqualTo(1L);
        assertThat(p.getNombre()).isEqualTo("ERP");
        assertThat(p.getEstado()).isEqualTo("EN_PROGRESO");
        assertThat(p.getFechaInicio()).isEqualTo(now);
        assertThat(p.getFechaFin()).isEqualTo(now.plusMonths(6));
    }

    @Test
    void testDimRecurso() {
        DimRecurso r = new DimRecurso();
        r.setIdRecurso(2L);
        r.setNombre("Carlos");
        r.setRol("Developer");
        r.setDepartamento("TI");

        assertThat(r.getIdRecurso()).isEqualTo(2L);
        assertThat(r.getNombre()).isEqualTo("Carlos");
        assertThat(r.getRol()).isEqualTo("Developer");
        assertThat(r.getDepartamento()).isEqualTo("TI");
    }

    @Test
    void testDimTiempo() {
        DimTiempo t = new DimTiempo();
        LocalDate now = LocalDate.now();

        t.setIdTiempo(3L);
        t.setFecha(now);
        t.setDia(5);
        t.setMes(6);
        t.setAnio(2026);
        t.setTrimestre(2);
        t.setDiaSemana(4);
        t.setEsFinSemana(false);

        assertThat(t.getIdTiempo()).isEqualTo(3L);
        assertThat(t.getFecha()).isEqualTo(now);
        assertThat(t.getDia()).isEqualTo(5);
        assertThat(t.getMes()).isEqualTo(6);
        assertThat(t.getAnio()).isEqualTo(2026);
        assertThat(t.getTrimestre()).isEqualTo(2);
        assertThat(t.getDiaSemana()).isEqualTo(4);
        assertThat(t.getEsFinSemana()).isFalse();
    }

    @Test
    void testFactGestionProyectos() {
        FactGestionProyectos f = new FactGestionProyectos();
        DimProyecto p = new DimProyecto();
        DimRecurso r = new DimRecurso();
        DimTiempo t = new DimTiempo();

        f.setIdHechoProyecto(10L);
        f.setProyecto(p);
        f.setRecurso(r);
        f.setTiempo(t);
        f.setTotalTareas(15);
        f.setTareasCompletadas(10);
        f.setTareasPendientes(5);
        f.setLeadTimePromedioDias(4.5);
        f.setTasaCompletitud(66.6);

        assertThat(f.getIdHechoProyecto()).isEqualTo(10L);
        assertThat(f.getProyecto()).isEqualTo(p);
        assertThat(f.getRecurso()).isEqualTo(r);
        assertThat(f.getTiempo()).isEqualTo(t);
        assertThat(f.getTotalTareas()).isEqualTo(15);
        assertThat(f.getTareasCompletadas()).isEqualTo(10);
        assertThat(f.getTareasPendientes()).isEqualTo(5);
        assertThat(f.getLeadTimePromedioDias()).isEqualTo(4.5);
        assertThat(f.getTasaCompletitud()).isEqualTo(66.6);
    }

    @Test
    void testFactMonitoreoServicios() {
        FactMonitoreoServicios f = new FactMonitoreoServicios();
        DimTiempo t = new DimTiempo();
        LocalDateTime reg = LocalDateTime.now();

        f.setIdHechoMonitoreo(20L);
        f.setTiempo(t);
        f.setServicioOrigen("project-service");
        f.setClaseInterceptor("HttpStatusInterceptor");
        f.setMetodo("getProyectos");
        f.setLatenciaMs(150L);
        f.setCodigoHttp(200);
        // Using reflection because LocalDateTime is annotated with @CreationTimestamp and is read-only
        org.springframework.test.util.ReflectionTestUtils.setField(f, "fechaRegistro", reg);

        assertThat(f.getIdHechoMonitoreo()).isEqualTo(20L);
        assertThat(f.getTiempo()).isEqualTo(t);
        assertThat(f.getServicioOrigen()).isEqualTo("project-service");
        assertThat(f.getClaseInterceptor()).isEqualTo("HttpStatusInterceptor");
        assertThat(f.getMetodo()).isEqualTo("getProyectos");
        assertThat(f.getLatenciaMs()).isEqualTo(150L);
        assertThat(f.getCodigoHttp()).isEqualTo(200);
        assertThat(f.getFechaRegistro()).isEqualTo(reg);
    }
}
