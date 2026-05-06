package cl.innovatech.projectmanagement.config;

import cl.innovatech.projectmanagement.interceptors.HttpStatusInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final HttpStatusInterceptor httpStatusInterceptor;

    public WebMvcConfig(HttpStatusInterceptor httpStatusInterceptor) {
        this.httpStatusInterceptor = httpStatusInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(httpStatusInterceptor).addPathPatterns("/**");
    }
}
