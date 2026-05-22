package id.ac.ui.cs.advprog.inventorykatalog.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AuthClientTest {

    private static final String AUTH_BASE_URL = "http://auth-service";
    private static final String AUTH_HEADER = "Bearer valid-token";

    @Mock
    private RestTemplate restTemplate;

    private AuthClient authClient;

    @BeforeEach
    void setUp() {
        authClient = new AuthClient();
        ReflectionTestUtils.setField(authClient, "authServiceBaseUrl", AUTH_BASE_URL);
        ReflectionTestUtils.setField(authClient, "restTemplate", restTemplate);
    }

    @Test
    void getCurrentUserProfileShouldRejectMissingAuthorizationHeader() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authClient.getCurrentUserProfile(null)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verifyNoInteractions(restTemplate);
    }

    @Test
    void getCurrentUserProfileShouldRejectInvalidAuthorizationHeader() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authClient.getCurrentUserProfile("Basic token")
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verifyNoInteractions(restTemplate);
    }

    @Test
    void getCurrentUserProfileShouldReturnProfileAndForwardAuthorizationHeader() {
        AuthProfileResponse profile = activeProfile(7L, "JASTIPER");
        whenExchangeProfile().thenReturn(ResponseEntity.ok(profile));

        AuthProfileResponse result = authClient.getCurrentUserProfile(AUTH_HEADER);

        assertSame(profile, result);
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                eq(AUTH_BASE_URL + "/api/profile/me"),
                eq(HttpMethod.GET),
                entityCaptor.capture(),
                eq(AuthProfileResponse.class)
        );
        assertEquals(AUTH_HEADER, entityCaptor.getValue().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
    }

    @Test
    void getCurrentUserProfileShouldRejectNullBodyFromAuthService() {
        whenExchangeProfile().thenReturn(ResponseEntity.ok(null));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authClient.getCurrentUserProfile(AUTH_HEADER)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void getCurrentUserProfileShouldRejectProfileWithoutId() {
        AuthProfileResponse profile = activeProfile(null, "JASTIPER");
        whenExchangeProfile().thenReturn(ResponseEntity.ok(profile));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authClient.getCurrentUserProfile(AUTH_HEADER)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void getCurrentUserProfileShouldTranslateUnauthorizedAuthServiceResponse() {
        HttpClientErrorException exceptionFromAuthService = HttpClientErrorException.create(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8
        );
        whenExchangeProfile().thenThrow(exceptionFromAuthService);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authClient.getCurrentUserProfile(AUTH_HEADER)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void getCurrentUserProfileShouldTranslateForbiddenAuthServiceResponse() {
        HttpClientErrorException exceptionFromAuthService = HttpClientErrorException.create(
                HttpStatus.FORBIDDEN,
                "Forbidden",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8
        );
        whenExchangeProfile().thenThrow(exceptionFromAuthService);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authClient.getCurrentUserProfile(AUTH_HEADER)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void getCurrentUserProfileShouldTranslateUnavailableAuthService() {
        whenExchangeProfile().thenThrow(new ResourceAccessException("connection refused"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authClient.getCurrentUserProfile(AUTH_HEADER)
        );

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatusCode());
    }

    @Test
    void getCurrentUserIdShouldReturnProfileIdAsString() {
        whenExchangeProfile().thenReturn(ResponseEntity.ok(activeProfile(12L, "JASTIPER")));

        assertEquals("12", authClient.getCurrentUserId(AUTH_HEADER));
    }

    @Test
    void getCurrentJastiperIdShouldReturnIdForActiveJastiper() {
        whenExchangeProfile().thenReturn(ResponseEntity.ok(activeProfile(99L, "JASTIPER")));

        assertEquals("99", authClient.getCurrentJastiperId(AUTH_HEADER));
    }

    @Test
    void getCurrentJastiperIdShouldRejectNonJastiper() {
        whenExchangeProfile().thenReturn(ResponseEntity.ok(activeProfile(99L, "BUYER")));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authClient.getCurrentJastiperId(AUTH_HEADER)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    private org.mockito.stubbing.OngoingStubbing<ResponseEntity<AuthProfileResponse>> whenExchangeProfile() {
        return when(restTemplate.exchange(
                eq(AUTH_BASE_URL + "/api/profile/me"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(AuthProfileResponse.class)
        ));
    }

    private AuthProfileResponse activeProfile(Long id, String role) {
        AuthProfileResponse profile = new AuthProfileResponse();
        profile.setId(id);
        profile.setRole(role);
        profile.setActive(true);
        return profile;
    }
}
