package cl.innovatech.resourcemanagement.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilUnitTest {

    private JwtUtil jwtUtil;
    private final String secretKey = "testSecretKeyWithAtLeast32CharactersForHmacSha256AndTesting";
    private final long expiration = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", secretKey);
        ReflectionTestUtils.setField(jwtUtil, "expiration", expiration);
    }

    @Test
    void generateToken_createsValidJwt() {
        // Arrange
        String username = "marti";
        Set<String> roles = Set.of("ADMIN", "JEFE_PROYECTO");

        // Act
        String token = jwtUtil.generateToken(username, roles);

        // Assert
        assertThat(token).isNotBlank();

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo(username);
        
        @SuppressWarnings("unchecked")
        Collection<String> rolesClaim = (Collection<String>) claims.get("roles");
        assertThat(rolesClaim).containsExactlyInAnyOrder("ADMIN", "JEFE_PROYECTO");
    }
}
