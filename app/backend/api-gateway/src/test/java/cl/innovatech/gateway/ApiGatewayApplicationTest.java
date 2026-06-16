package cl.innovatech.gateway;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ApiGatewayApplicationTest {

    @Test
    void instantiateApiGatewayApplication() {
        ApiGatewayApplication app = new ApiGatewayApplication();
        assertThat(app).isNotNull();
    }

    @Test
    void main_runsSpringApplication() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(eq(ApiGatewayApplication.class), any(String[].class)))
                    .thenReturn(mock(ConfigurableApplicationContext.class));

            ApiGatewayApplication.main(new String[]{});

            mocked.verify(() -> SpringApplication.run(eq(ApiGatewayApplication.class), any(String[].class)));
        }
    }
}
