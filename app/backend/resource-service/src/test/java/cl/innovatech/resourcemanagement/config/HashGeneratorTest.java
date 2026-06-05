package cl.innovatech.resourcemanagement.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HashGeneratorTest {

    @Test
    void instantiateHashGenerator() {
        HashGenerator hashGenerator = new HashGenerator();
        assertThat(hashGenerator).isNotNull();
    }
}
