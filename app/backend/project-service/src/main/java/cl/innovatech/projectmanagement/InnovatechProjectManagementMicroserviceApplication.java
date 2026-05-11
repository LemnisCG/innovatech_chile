package cl.innovatech.projectmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class InnovatechProjectManagementMicroserviceApplication {

    public static void main(String[] argumentsPassedFromCommandLine) {
        SpringApplication.run(InnovatechProjectManagementMicroserviceApplication.class, argumentsPassedFromCommandLine);
    }
}
