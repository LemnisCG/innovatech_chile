package cl.innovatech.projectmanagement.interceptors;

import cl.innovatech.projectmanagement.aspects.MonitoringAspect;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class HttpStatusInterceptor implements HandlerInterceptor {

    private final MonitoringAspect monitoringAspect;

    public HttpStatusInterceptor(MonitoringAspect monitoringAspect) {
        this.monitoringAspect = monitoringAspect;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            String className = handlerMethod.getBeanType().getSimpleName();
            String methodName = handlerMethod.getMethod().getName();

            // Solo nos interesan los endpoints de nuestra API, ej. ProyectosController
            if (className.contains("Controller")) {
                long startTime = (Long) request.getAttribute("startTime");
                long latency = System.currentTimeMillis() - startTime;
                int status = response.getStatus();

                monitoringAspect.saveMetricsAsync("project-service", className, methodName, latency, status);
            }
        }
    }
}
