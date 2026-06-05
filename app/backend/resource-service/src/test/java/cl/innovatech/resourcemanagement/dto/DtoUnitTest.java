package cl.innovatech.resourcemanagement.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DtoUnitTest {

    @Test
    void testAuthRequest() {
        AuthRequest req1 = new AuthRequest();
        req1.setUsername("user");
        req1.setPassword("pass");

        assertThat(req1.getUsername()).isEqualTo("user");
        assertThat(req1.getPassword()).isEqualTo("pass");
        assertThat(req1.toString()).contains("username=user", "password=pass");

        // Equals & HashCode
        AuthRequest req2 = new AuthRequest();
        req2.setUsername("user");
        req2.setPassword("pass");

        assertThat(req1).isEqualTo(req1);
        assertThat(req1).isEqualTo(req2);
        assertThat(req1.hashCode()).isEqualTo(req2.hashCode());

        assertThat(req1).isNotEqualTo(null);
        assertThat(req1).isNotEqualTo(new Object());

        // Test different fields
        AuthRequest diffUser = new AuthRequest();
        diffUser.setUsername("diff");
        diffUser.setPassword("pass");
        assertThat(req1).isNotEqualTo(diffUser);

        AuthRequest diffPass = new AuthRequest();
        diffPass.setUsername("user");
        diffPass.setPassword("diff");
        assertThat(req1).isNotEqualTo(diffPass);
    }

    @Test
    void testAuthResponse() {
        AuthResponse resp1 = new AuthResponse("token123");
        assertThat(resp1.getToken()).isEqualTo("token123");
        assertThat(resp1.toString()).contains("token=token123");

        AuthResponse resp2 = new AuthResponse("token123");
        assertThat(resp1).isEqualTo(resp1);
        assertThat(resp1).isEqualTo(resp2);
        assertThat(resp1.hashCode()).isEqualTo(resp2.hashCode());

        assertThat(resp1).isNotEqualTo(null);
        assertThat(resp1).isNotEqualTo(new Object());

        AuthResponse diffToken = new AuthResponse("different");
        assertThat(resp1).isNotEqualTo(diffToken);

        resp1.setToken("token456");
        assertThat(resp1.getToken()).isEqualTo("token456");
    }

    @Test
    void testRegisterRequest() {
        RegisterRequest req1 = new RegisterRequest();
        req1.setUsername("user");
        req1.setPassword("pass");
        req1.setEmail("email");
        req1.setEspecialidad("esp");
        req1.setTelefono("tel");
        req1.setDireccion("dir");
        req1.setRut("rut");
        req1.setEstado("estado");

        assertThat(req1.getUsername()).isEqualTo("user");
        assertThat(req1.getPassword()).isEqualTo("pass");
        assertThat(req1.getEmail()).isEqualTo("email");
        assertThat(req1.getEspecialidad()).isEqualTo("esp");
        assertThat(req1.getTelefono()).isEqualTo("tel");
        assertThat(req1.getDireccion()).isEqualTo("dir");
        assertThat(req1.getRut()).isEqualTo("rut");
        assertThat(req1.getEstado()).isEqualTo("estado");
        assertThat(req1.toString()).contains("username=user", "password=pass", "email=email");

        // Equals & HashCode
        RegisterRequest req2 = new RegisterRequest();
        req2.setUsername("user");
        req2.setPassword("pass");
        req2.setEmail("email");
        req2.setEspecialidad("esp");
        req2.setTelefono("tel");
        req2.setDireccion("dir");
        req2.setRut("rut");
        req2.setEstado("estado");

        assertThat(req1).isEqualTo(req1);
        assertThat(req1).isEqualTo(req2);
        assertThat(req1.hashCode()).isEqualTo(req2.hashCode());

        assertThat(req1).isNotEqualTo(null);
        assertThat(req1).isNotEqualTo(new Object());

        // Test every field different for coverage
        RegisterRequest diff;

        diff = copy(req1); diff.setUsername("diff"); assertThat(req1).isNotEqualTo(diff);
        diff = copy(req1); diff.setUsername(null); assertThat(req1).isNotEqualTo(diff);

        diff = copy(req1); diff.setPassword("diff"); assertThat(req1).isNotEqualTo(diff);
        diff = copy(req1); diff.setPassword(null); assertThat(req1).isNotEqualTo(diff);

        diff = copy(req1); diff.setEmail("diff"); assertThat(req1).isNotEqualTo(diff);
        diff = copy(req1); diff.setEmail(null); assertThat(req1).isNotEqualTo(diff);

        diff = copy(req1); diff.setEspecialidad("diff"); assertThat(req1).isNotEqualTo(diff);
        diff = copy(req1); diff.setEspecialidad(null); assertThat(req1).isNotEqualTo(diff);

        diff = copy(req1); diff.setTelefono("diff"); assertThat(req1).isNotEqualTo(diff);
        diff = copy(req1); diff.setTelefono(null); assertThat(req1).isNotEqualTo(diff);

        diff = copy(req1); diff.setDireccion("diff"); assertThat(req1).isNotEqualTo(diff);
        diff = copy(req1); diff.setDireccion(null); assertThat(req1).isNotEqualTo(diff);

        diff = copy(req1); diff.setRut("diff"); assertThat(req1).isNotEqualTo(diff);
        diff = copy(req1); diff.setRut(null); assertThat(req1).isNotEqualTo(diff);

        diff = copy(req1); diff.setEstado("diff"); assertThat(req1).isNotEqualTo(diff);
        diff = copy(req1); diff.setEstado(null); assertThat(req1).isNotEqualTo(diff);
    }

    private RegisterRequest copy(RegisterRequest src) {
        RegisterRequest dest = new RegisterRequest();
        dest.setUsername(src.getUsername());
        dest.setPassword(src.getPassword());
        dest.setEmail(src.getEmail());
        dest.setEspecialidad(src.getEspecialidad());
        dest.setTelefono(src.getTelefono());
        dest.setDireccion(src.getDireccion());
        dest.setRut(src.getRut());
        dest.setEstado(src.getEstado());
        return dest;
    }
}
