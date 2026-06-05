package cl.innovatech.analytics.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private HeaderAuthenticationFilter headerAuthenticationFilter;

    @Mock
    private HttpSecurity httpSecurity;

    @Test
    void filterChain_configuresSecurity() throws Exception {
        // Arrange
        SecurityConfig securityConfig = new SecurityConfig(headerAuthenticationFilter);

        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);

        // Act
        securityConfig.filterChain(httpSecurity);

        // Assert
        verify(httpSecurity, times(1)).csrf(any());
        verify(httpSecurity, times(1)).sessionManagement(any());
        verify(httpSecurity, times(1)).authorizeHttpRequests(any());
        verify(httpSecurity, times(1)).addFilterBefore(eq(headerAuthenticationFilter), any());
        verify(httpSecurity, times(1)).build();
    }
}
