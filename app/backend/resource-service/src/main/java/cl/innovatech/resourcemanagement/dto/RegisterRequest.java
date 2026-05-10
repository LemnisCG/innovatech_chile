package cl.innovatech.resourcemanagement.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String especialidad;
    private String telefono;
    private String direccion;
    private String rut;
    private String estado;
}
