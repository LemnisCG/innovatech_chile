package cl.innovatech.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtUtilUnitTest {

    private JwtUtil jwtUtil;
    private final String secret = "mySecretKeyForTestingJwtWithAtLeast32CharactersLong";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
    }

    @Test
    void validateToken_withValidToken_returnsTrue() {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject("testUser")
                .signWith(key)
                .compact();

        boolean isValid = jwtUtil.validateToken(token);

        assertThat(isValid).isTrue();
    }

    @Test
    void validateToken_withInvalidToken_returnsFalse() {
        boolean isValid = jwtUtil.validateToken("invalidToken");

        assertThat(isValid).isFalse();
    }

    @Test
    void getClaims_returnsCorrectClaims() {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject("testUser")
                .claim("roles", "ADMIN")
                .signWith(key)
                .compact();

        Claims claims = jwtUtil.getClaims(token);

        assertThat(claims.getSubject()).isEqualTo("testUser");
        assertThat(claims.get("roles")).isEqualTo("ADMIN");
    }
}
