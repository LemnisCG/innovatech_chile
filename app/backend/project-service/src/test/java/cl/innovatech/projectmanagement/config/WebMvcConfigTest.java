package cl.innovatech.projectmanagement.config;

import cl.innovatech.projectmanagement.interceptors.HttpStatusInterceptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebMvcConfigTest {

    @Mock
    private HttpStatusInterceptor httpStatusInterceptor;

    @Mock
    private InterceptorRegistry interceptorRegistry;

    @Mock
    private InterceptorRegistration interceptorRegistration;

    @InjectMocks
    private WebMvcConfig webMvcConfig;

    @Test
    void addInterceptors_registersHttpStatusInterceptor() {
        // Arrange
        when(interceptorRegistry.addInterceptor(httpStatusInterceptor)).thenReturn(interceptorRegistration);
        when(interceptorRegistration.addPathPatterns("/**")).thenReturn(interceptorRegistration);

        // Act
        webMvcConfig.addInterceptors(interceptorRegistry);

        // Assert
        verify(interceptorRegistry, times(1)).addInterceptor(httpStatusInterceptor);
        verify(interceptorRegistration, times(1)).addPathPatterns("/**");
    }
}
