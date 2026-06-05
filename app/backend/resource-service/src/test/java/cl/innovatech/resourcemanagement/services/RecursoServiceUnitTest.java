package cl.innovatech.resourcemanagement.services;

import cl.innovatech.resourcemanagement.entities.Recurso;
import cl.innovatech.resourcemanagement.entities.Usuario;
import cl.innovatech.resourcemanagement.repository.RecursoRepository;
import cl.innovatech.resourcemanagement.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecursoServiceUnitTest {

    @Mock
    private RecursoRepository recursoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private RecursoService recursoService;

    @Test
    void getAllRecursos_returnsList() {
        Recurso recurso = new Recurso();
        recurso.setId(1L);
        when(recursoRepository.findAll()).thenReturn(List.of(recurso));

        List<Recurso> result = recursoService.getAllRecursos();

        assertThat(result).hasSize(1);
        verify(recursoRepository, times(1)).findAll();
    }

    @Test
    void getRecursoById_whenExists_returnsRecurso() {
        Recurso recurso = new Recurso();
        recurso.setId(1L);
        when(recursoRepository.findById(1L)).thenReturn(Optional.of(recurso));

        Optional<Recurso> result = recursoService.getRecursoById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(recursoRepository, times(1)).findById(1L);
    }

    @Test
    void getRecursoById_whenNotExists_returnsEmpty() {
        when(recursoRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Recurso> result = recursoService.getRecursoById(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void createRecurso_whenUsuarioExists_savesAndReturnsRecurso() {
        Usuario usuario = new Usuario();
        usuario.setId(10L);
        usuario.setUsername("johndoe");

        Recurso recurso = new Recurso();
        recurso.setUsuario(usuario);
        recurso.setRolEnProyecto("DESARROLLADOR");

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(recursoRepository.save(recurso)).thenReturn(recurso);

        Recurso result = recursoService.createRecurso(recurso);

        assertThat(result).isNotNull();
        assertThat(result.getUsuario().getUsername()).isEqualTo("johndoe");
        verify(usuarioRepository, times(1)).findById(10L);
        verify(recursoRepository, times(1)).save(recurso);
    }

    @Test
    void createRecurso_whenUsuarioDoesNotExist_throwsRuntimeException() {
        Usuario usuario = new Usuario();
        usuario.setId(10L);

        Recurso recurso = new Recurso();
        recurso.setUsuario(usuario);

        when(usuarioRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recursoService.createRecurso(recurso))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario no encontrado con id: 10");

        verify(recursoRepository, never()).save(any(Recurso.class));
    }

    @Test
    void updateRecurso_whenExists_updatesAndSaves() {
        Recurso existing = new Recurso();
        existing.setId(1L);
        existing.setRolEnProyecto("QA");
        existing.setEstado("ASIGNADO");

        Recurso updateInfo = new Recurso();
        updateInfo.setIdProyecto(5L);
        updateInfo.setRolEnProyecto("DEV");
        updateInfo.setEstado("LIBERADO");
        updateInfo.setHorasAsignadas(40);
        updateInfo.setFechaAsignacion(LocalDate.of(2026, 1, 1));

        when(recursoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(recursoRepository.save(existing)).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Recurso> result = recursoService.updateRecurso(1L, updateInfo);

        assertThat(result).isPresent();
        assertThat(result.get().getRolEnProyecto()).isEqualTo("DEV");
        assertThat(result.get().getEstado()).isEqualTo("LIBERADO");
        assertThat(result.get().getIdProyecto()).isEqualTo(5L);
        verify(recursoRepository, times(1)).save(existing);
    }

    @Test
    void updateRecurso_whenDoesNotExist_returnsEmpty() {
        Recurso updateInfo = new Recurso();
        when(recursoRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Recurso> result = recursoService.updateRecurso(1L, updateInfo);

        assertThat(result).isEmpty();
        verify(recursoRepository, never()).save(any(Recurso.class));
    }

    @Test
    void deleteRecurso_whenExists_deletesAndReturnsTrue() {
        when(recursoRepository.existsById(1L)).thenReturn(true);

        boolean result = recursoService.deleteRecurso(1L);

        assertThat(result).isTrue();
        verify(recursoRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteRecurso_whenDoesNotExist_returnsFalse() {
        when(recursoRepository.existsById(1L)).thenReturn(false);

        boolean result = recursoService.deleteRecurso(1L);

        assertThat(result).isFalse();
        verify(recursoRepository, never()).deleteById(anyLong());
    }
}
