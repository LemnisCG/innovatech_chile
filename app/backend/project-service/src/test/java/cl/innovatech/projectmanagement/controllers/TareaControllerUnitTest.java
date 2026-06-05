package cl.innovatech.projectmanagement.controllers;

import cl.innovatech.projectmanagement.dtos.TareaDTO;
import cl.innovatech.projectmanagement.entities.Tarea;
import cl.innovatech.projectmanagement.services.TareaService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TareaControllerUnitTest {

    @Mock
    private TareaService tareaService;

    @InjectMocks
    private TareaController tareaController;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getTareas_returnsList() {
        TareaDTO dto = new TareaDTO(1L, "T1", "D1", "PENDING", 100L, 10L, "2026", "2026", "");
        when(tareaService.getTareas()).thenReturn(List.of(dto));

        List<TareaDTO> result = tareaController.getTareas();

        assertThat(result).hasSize(1);
        verify(tareaService, times(1)).getTareas();
    }

    @Test
    void getTareaById_returnsDTO() {
        TareaDTO dto = new TareaDTO(1L, "T1", "D1", "PENDING", 100L, 10L, "2026", "2026", "");
        when(tareaService.getTareaById(1L)).thenReturn(dto);

        TareaDTO result = tareaController.getTareaById(1L);

        assertThat(result).isEqualTo(dto);
        verify(tareaService, times(1)).getTareaById(1L);
    }

    @Test
    void updateEstado_whenTareaNotFound_returnsNotFound() {
        when(tareaService.getTareaEntityById(1L)).thenReturn(null);

        ResponseEntity<?> response = tareaController.updateEstado(1L, Map.of());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateEstado_whenMissingEstadoParam_returnsBadRequest() {
        Tarea tarea = new Tarea();
        when(tareaService.getTareaEntityById(1L)).thenReturn(tarea);

        ResponseEntity<?> response = tareaController.updateEstado(1L, Map.of("userId", "5"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateEstado_whenAdminUser_savesAndReturnsOk() {
        Tarea tarea = new Tarea();
        tarea.setId(1L);
        tarea.setEstado("PENDIENTE");

        when(tareaService.getTareaEntityById(1L)).thenReturn(tarea);
        when(tareaService.save(any(Tarea.class))).thenReturn(tarea);

        TareaDTO updatedDto = new TareaDTO(1L, "T1", "D1", "COMPLETADO", 100L, 10L, "2026", "2026", "");
        when(tareaService.getTareaById(1L)).thenReturn(updatedDto);

        // Mock Security Context
        Authentication auth = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))).when(auth).getAuthorities();
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        ResponseEntity<?> response = tareaController.updateEstado(1L, Map.of("estado", "COMPLETADO"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedDto);
        verify(tareaService, times(1)).save(tarea);
    }

    @Test
    void updateEstado_whenMemberUserIsAssignedProfessional_savesAndReturnsOk() {
        Tarea tarea = new Tarea();
        tarea.setId(1L);
        tarea.setIdProfesionalAsignado(5L);

        when(tareaService.getTareaEntityById(1L)).thenReturn(tarea);
        when(tareaService.save(any(Tarea.class))).thenReturn(tarea);

        // Mock Security Context
        Authentication auth = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_MIEMBRO"))).when(auth).getAuthorities();
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        ResponseEntity<?> response = tareaController.updateEstado(1L, Map.of("estado", "COMPLETADO", "userId", "5"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(tareaService, times(1)).save(tarea);
    }

    @Test
    void updateEstado_whenMemberUserNotAssignedProfessional_returnsForbidden() {
        Tarea tarea = new Tarea();
        tarea.setId(1L);
        tarea.setIdProfesionalAsignado(10L); // assigned to someone else

        when(tareaService.getTareaEntityById(1L)).thenReturn(tarea);

        // Mock Security Context
        Authentication auth = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_MIEMBRO"))).when(auth).getAuthorities();
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        ResponseEntity<?> response = tareaController.updateEstado(1L, Map.of("estado", "COMPLETADO", "userId", "5"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        verify(tareaService, never()).save(any(Tarea.class));
    }

    @Test
    void updateEstado_whenMemberUserMissingUserId_returnsForbidden() {
        Tarea tarea = new Tarea();
        tarea.setId(1L);

        when(tareaService.getTareaEntityById(1L)).thenReturn(tarea);

        // Mock Security Context
        Authentication auth = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_MIEMBRO"))).when(auth).getAuthorities();
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        ResponseEntity<?> response = tareaController.updateEstado(1L, Map.of("estado", "COMPLETADO"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        verify(tareaService, never()).save(any(Tarea.class));
    }
}
