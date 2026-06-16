package cl.innovatech.projectmanagement.interceptors;

import cl.innovatech.projectmanagement.aspects.MonitoringAspect;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.HandlerMethod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HttpStatusInterceptorUnitTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private MonitoringAspect monitoringAspect;

    @InjectMocks
    private HttpStatusInterceptor interceptor;

    @Test
    void preHandle_setsStartTime() {
        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
        verify(request, times(1)).setAttribute(eq("startTime"), anyLong());
    }

    @Test
    void afterCompletion_whenHandlerIsController_savesMetrics() throws Exception {
        HandlerMethod handlerMethod = mock(HandlerMethod.class);
        doReturn(DummyController.class).when(handlerMethod).getBeanType();
        doReturn(DummyController.class.getMethod("dummyMethod")).when(handlerMethod).getMethod();

        when(request.getAttribute("startTime")).thenReturn(System.currentTimeMillis() - 100);
        when(response.getStatus()).thenReturn(200);

        interceptor.afterCompletion(request, response, handlerMethod, null);

        verify(monitoringAspect, times(1)).saveMetricsAsync(
                eq("project-service"),
                eq("DummyController"),
                eq("dummyMethod"),
                anyLong(),
                eq(200)
        );
    }

    @Test
    void afterCompletion_whenHandlerIsNotController_doesNotSaveMetrics() throws Exception {
        HandlerMethod handlerMethod = mock(HandlerMethod.class);
        doReturn(Object.class).when(handlerMethod).getBeanType();
        doReturn(Object.class.getMethod("toString")).when(handlerMethod).getMethod();

        interceptor.afterCompletion(request, response, handlerMethod, null);

        verify(monitoringAspect, never()).saveMetricsAsync(anyString(), anyString(), anyString(), anyLong(), anyInt());
    }

    @Test
    void afterCompletion_whenHandlerIsNotHandlerMethod_doesNotSaveMetrics() {
        Object handler = new Object();

        interceptor.afterCompletion(request, response, handler, null);

        verify(monitoringAspect, never()).saveMetricsAsync(anyString(), anyString(), anyString(), anyLong(), anyInt());
    }

    private static class DummyController {
        public void dummyMethod() {}
    }
}
