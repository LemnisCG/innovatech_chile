package cl.innovatech.gateway.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthenticationFilter filter;

    private GatewayFilter gatewayFilter;

    @BeforeEach
    void setUp() {
        gatewayFilter = filter.apply(new AuthenticationFilter.Config());
    }

    @Test
    void filter_whenPathIsAuth_allowsRequest() {
        // Arrange
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/auth/login").build()
        );
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = gatewayFilter.filter(exchange, chain);

        // Assert
        assertThat(result).isNotNull();
        verify(chain, times(1)).filter(exchange);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void filter_whenAuthHeaderMissing_returnsUnauthorized() {
        // Arrange
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/proyectos").build()
        );
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        // Act
        Mono<Void> result = gatewayFilter.filter(exchange, chain);

        // Assert
        assertThat(result).isNotNull();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verifyNoInteractions(chain);
    }

    @Test
    void filter_whenAuthHeaderInvalid_returnsUnauthorized() {
        // Arrange
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/proyectos")
                        .header(HttpHeaders.AUTHORIZATION, "Basic user:pass")
                        .build()
        );
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        // Act
        Mono<Void> result = gatewayFilter.filter(exchange, chain);

        // Assert
        assertThat(result).isNotNull();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verifyNoInteractions(chain);
    }

    @Test
    void filter_whenTokenInvalid_returnsUnauthorized() {
        // Arrange
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/proyectos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token")
                        .build()
        );
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(jwtUtil.validateToken("invalid-token")).thenReturn(false);

        // Act
        Mono<Void> result = gatewayFilter.filter(exchange, chain);

        // Assert
        assertThat(result).isNotNull();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(jwtUtil, times(1)).validateToken("invalid-token");
        verifyNoInteractions(chain);
    }

    @Test
    void filter_whenTokenValidWithRoles_injectsHeadersAndProceeds() {
        // Arrange
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/proyectos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                        .build()
        );
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        ArgumentCaptor<ServerWebExchange> exchangeCaptor = ArgumentCaptor.forClass(ServerWebExchange.class);
        when(chain.filter(exchangeCaptor.capture())).thenReturn(Mono.empty());

        when(jwtUtil.validateToken("valid-token")).thenReturn(true);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("marti");
        when(claims.get("roles", List.class)).thenReturn(List.of("ADMIN", "JEFE_PROYECTO"));
        when(jwtUtil.getClaims("valid-token")).thenReturn(claims);

        // Act
        Mono<Void> result = gatewayFilter.filter(exchange, chain);

        // Assert
        assertThat(result).isNotNull();
        verify(chain, times(1)).filter(any(ServerWebExchange.class));

        ServerWebExchange modifiedExchange = exchangeCaptor.getValue();
        assertThat(modifiedExchange.getRequest().getHeaders().getFirst("X-Auth-User")).isEqualTo("marti");
        assertThat(modifiedExchange.getRequest().getHeaders().getFirst("X-Auth-Roles")).isEqualTo("ADMIN,JEFE_PROYECTO");
    }

    @Test
    void filter_whenTokenValidWithoutRoles_injectsHeadersAndProceeds() {
        // Arrange
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/proyectos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                        .build()
        );
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        ArgumentCaptor<ServerWebExchange> exchangeCaptor = ArgumentCaptor.forClass(ServerWebExchange.class);
        when(chain.filter(exchangeCaptor.capture())).thenReturn(Mono.empty());

        when(jwtUtil.validateToken("valid-token")).thenReturn(true);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("marti");
        when(claims.get("roles", List.class)).thenReturn(null);
        when(jwtUtil.getClaims("valid-token")).thenReturn(claims);

        // Act
        Mono<Void> result = gatewayFilter.filter(exchange, chain);

        // Assert
        assertThat(result).isNotNull();
        verify(chain, times(1)).filter(any(ServerWebExchange.class));

        ServerWebExchange modifiedExchange = exchangeCaptor.getValue();
        assertThat(modifiedExchange.getRequest().getHeaders().getFirst("X-Auth-User")).isEqualTo("marti");
        assertThat(modifiedExchange.getRequest().getHeaders().getFirst("X-Auth-Roles")).isEqualTo("");
    }
}
