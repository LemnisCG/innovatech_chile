package cl.innovatech.analytics.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HeaderAuthenticationFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private HeaderAuthenticationFilter filter;

    @BeforeEach
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_whenHeadersPresent_setsAuthentication() throws Exception {
        // Arrange
        when(request.getHeader("X-Auth-User")).thenReturn("marti");
        when(request.getHeader("X-Auth-Roles")).thenReturn("ADMIN, JEFE_PROYECTO");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo("marti");
        assertThat(auth.getAuthorities()).hasSize(2);
        assertThat(auth.getAuthorities().stream().map(a -> a.getAuthority()))
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_JEFE_PROYECTO");

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_whenHeadersMissing_doesNotSetAuthentication() throws Exception {
        // Arrange
        when(request.getHeader("X-Auth-User")).thenReturn(null);
        when(request.getHeader("X-Auth-Roles")).thenReturn(null);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();

        verify(filterChain, times(1)).doFilter(request, response);
    }
}
