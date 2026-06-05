package cl.innovatech.analytics;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AnalyticsServiceApplicationTest {

    @Test
    void instantiateAnalyticsServiceApplication() {
        AnalyticsServiceApplication app = new AnalyticsServiceApplication();
        assertThat(app).isNotNull();
        
        RestTemplate rt = app.restTemplate();
        assertThat(rt).isNotNull();
    }

    @Test
    void main_runsSpringApplication() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(eq(AnalyticsServiceApplication.class), any(String[].class)))
                    .thenReturn(mock(ConfigurableApplicationContext.class));

            AnalyticsServiceApplication.main(new String[]{});

            mocked.verify(() -> SpringApplication.run(eq(AnalyticsServiceApplication.class), any(String[].class)));
        }
    }
}
