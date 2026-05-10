package cl.innovatech.projectmanagement.services;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import cl.innovatech.projectmanagement.aspects.MonitoringAspect;
import cl.innovatech.projectmanagement.entities.Proyecto;
import cl.innovatech.projectmanagement.entities.Tarea;
import cl.innovatech.projectmanagement.repository.ProyectoRepository;
import cl.innovatech.projectmanagement.repository.TareaRepository;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class ProyectoServiceIntegrationTest {

    @MockBean
    private MonitoringAspect monitoringAspect;

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private TareaRepository tareaRepository;

    @Test
    void crearProyectoConTareasYProfesionalesAsignados() {
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre("Proyecto Integración");
        proyecto.setDescripcion("Verifica que un proyecto se guarde con sus tareas asignadas");
        proyecto.setEstado("EN_PROGRESO");
        proyecto.setFechaInicio("2026-05-06");
        proyecto.setFechaFin("2026-10-01");
        proyecto.setComentarios("Proyecto de prueba con tareas y usuarios asignados");

        Tarea tarea1 = new Tarea();
        tarea1.setNombre("Análisis de requerimientos");
        tarea1.setDescripcion("Revisar casos de uso y mapear flujo");
        tarea1.setEstado("PENDIENTE");
        tarea1.setIdProfesionalAsignado(101L);
        tarea1.setFechaInicio("2026-05-06");
        tarea1.setFechaFin("2026-05-10");

        Tarea tarea2 = new Tarea();
        tarea2.setNombre("Desarrollo del backend");
        tarea2.setDescripcion("Implementar endpoints y persistencia");
        tarea2.setEstado("EN_PROGRESO");
        tarea2.setIdProfesionalAsignado(102L);
        tarea2.setFechaInicio("2026-05-11");
        tarea2.setFechaFin("2026-06-30");

        tarea1.setProyecto(proyecto);
        tarea2.setProyecto(proyecto);
        proyecto.getTareasDelProyecto().add(tarea1);
        proyecto.getTareasDelProyecto().add(tarea2);

        proyectoService.add(proyecto);

        assertThat(proyecto.getId()).isNotNull();
        List<Proyecto> proyectos = proyectoRepository.findAll();
        assertThat(proyectos).hasSize(1);

        Proyecto saved = proyectoRepository.findById(proyecto.getId()).orElseThrow();
        assertThat(saved.getNombre()).isEqualTo("Proyecto Integración");
        assertThat(saved.getTareasDelProyecto()).hasSize(2);
        assertThat(saved.getTareasDelProyecto()).extracting(Tarea::getNombre)
                .containsExactlyInAnyOrder("Análisis de requerimientos", "Desarrollo del backend");
        assertThat(saved.getTareasDelProyecto()).extracting(Tarea::getIdProfesionalAsignado)
                .containsExactlyInAnyOrder(101L, 102L);
        assertThat(saved.getTareasDelProyecto().stream().allMatch(t -> t.getProyecto() != null)).isTrue();
    }

    @Test
    void agregarTareaAProyectoExistenteConUsuarioAsignado() {
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre("Proyecto con tarea");
        proyecto.setDescripcion("Proyecto para probar assignación de tareas");
        proyectoService.add(proyecto);

        Long projectId = proyecto.getId();
        Tarea tarea = new Tarea();
        tarea.setNombre("QA funcional");
        tarea.setDescripcion("Verificar que el sistema cumpla los flujos");
        tarea.setEstado("PENDIENTE");
        tarea.setIdProfesionalAsignado(200L);
        tarea.setFechaInicio("2026-07-01");
        tarea.setFechaFin("2026-07-10");

        proyectoService.addTarea(projectId, tarea);

        List<Tarea> tareas = tareaRepository.findAll();
        assertThat(tareas).hasSize(1);
        Tarea saved = tareas.get(0);
        assertThat(saved.getProyecto()).isNotNull();
        assertThat(saved.getProyecto().getId()).isEqualTo(projectId);
        assertThat(saved.getIdProfesionalAsignado()).isEqualTo(200L);
    }
}
