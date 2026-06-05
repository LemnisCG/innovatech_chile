package cl.innovatech.resourcemanagement.entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class EntitiesUnitTest {

    @Test
    void testRecurso() {
        Usuario usuario = new Usuario();
        usuario.setId(10L);

        LocalDate now = LocalDate.now();

        Recurso r = new Recurso();
        r.setId(1L);
        r.setUsuario(usuario);
        r.setIdProyecto(2L);
        r.setIdTarea(3L);
        r.setRolEnProyecto("Developer");
        r.setHorasAsignadas(40);
        r.setFechaAsignacion(now);
        r.setFechaLiberacion(now.plusDays(5));
        r.setEstado("ASIGNADO");

        assertThat(r.getId()).isEqualTo(1L);
        assertThat(r.getUsuario()).isEqualTo(usuario);
        assertThat(r.getIdProyecto()).isEqualTo(2L);
        assertThat(r.getIdTarea()).isEqualTo(3L);
        assertThat(r.getRolEnProyecto()).isEqualTo("Developer");
        assertThat(r.getHorasAsignadas()).isEqualTo(40);
        assertThat(r.getFechaAsignacion()).isEqualTo(now);
        assertThat(r.getFechaLiberacion()).isEqualTo(now.plusDays(5));
        assertThat(r.getEstado()).isEqualTo("ASIGNADO");

        // test toString, equals and hashCode via lombok
        assertThat(r.toString()).contains("horasAsignadas=40");
        Recurso r2 = new Recurso();
        r2.setId(1L);
        r2.setUsuario(usuario);
        r2.setIdProyecto(2L);
        r2.setIdTarea(3L);
        r2.setRolEnProyecto("Developer");
        r2.setHorasAsignadas(40);
        r2.setFechaAsignacion(now);
        r2.setFechaLiberacion(now.plusDays(5));
        r2.setEstado("ASIGNADO");

        assertThat(r).isEqualTo(r2);
        assertThat(r.hashCode()).isEqualTo(r2.hashCode());
    }

    @Test
    void testUsuario() {
        LocalDateTime now = LocalDateTime.now();

        Usuario u = new Usuario();
        u.setId(1L);
        u.setUsername("username");
        u.setPassword("password");
        u.setEmail("email@example.com");
        u.setEspecialidad("QA");
        u.setTelefono("987654321");
        u.setDireccion("Avenue 1");
        u.setRut("11.111.111-1");
        u.setEstado("ACTIVO");
        u.setRoles(Set.of("ROLE_USER"));
        u.setActive(true);
        u.setCreatedAt(now);
        u.setUpdatedAt(now);
        u.setLastLogin(now);

        assertThat(u.getId()).isEqualTo(1L);
        assertThat(u.getUsername()).isEqualTo("username");
        assertThat(u.getPassword()).isEqualTo("password");
        assertThat(u.getEmail()).isEqualTo("email@example.com");
        assertThat(u.getEspecialidad()).isEqualTo("QA");
        assertThat(u.getTelefono()).isEqualTo("987654321");
        assertThat(u.getDireccion()).isEqualTo("Avenue 1");
        assertThat(u.getRut()).isEqualTo("11.111.111-1");
        assertThat(u.getEstado()).isEqualTo("ACTIVO");
        assertThat(u.getRoles()).containsExactly("ROLE_USER");
        assertThat(u.isActive()).isTrue();
        assertThat(u.getCreatedAt()).isEqualTo(now);
        assertThat(u.getUpdatedAt()).isEqualTo(now);
        assertThat(u.getLastLogin()).isEqualTo(now);

        assertThat(u.toString()).contains("username=username");
        Usuario u2 = new Usuario();
        u2.setId(1L);
        u2.setUsername("username");
        u2.setPassword("password");
        u2.setEmail("email@example.com");
        u2.setEspecialidad("QA");
        u2.setTelefono("987654321");
        u2.setDireccion("Avenue 1");
        u2.setRut("11.111.111-1");
        u2.setEstado("ACTIVO");
        u2.setRoles(Set.of("ROLE_USER"));
        u2.setActive(true);
        u2.setCreatedAt(now);
        u2.setUpdatedAt(now);
        u2.setLastLogin(now);

        assertThat(u).isEqualTo(u2);
        assertThat(u.hashCode()).isEqualTo(u2.hashCode());
    }
}
