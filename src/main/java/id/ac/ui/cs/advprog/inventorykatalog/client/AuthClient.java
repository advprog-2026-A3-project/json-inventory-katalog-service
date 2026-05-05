package id.ac.ui.cs.advprog.inventorykatalog.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthClient {

    @Value("${auth.service.base-url}")
    private String authServiceBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public AuthProfileResponse getCurrentUserProfile(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Missing or invalid Authorization header"
            );
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<AuthProfileResponse> response = restTemplate.exchange(
                    authServiceBaseUrl + "/api/profile/me",
                    HttpMethod.GET,
                    entity,
                    AuthProfileResponse.class
            );

            AuthProfileResponse profile = response.getBody();

            if (profile == null || profile.getId() == null) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Cannot extract user profile from auth service"
                );
            }

            return profile;

        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid or expired token"
            );
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Auth service is unavailable"
            );
        }
    }

    public String getCurrentUserId(String authorizationHeader) {
        return getCurrentUserProfile(authorizationHeader).getId().toString();
    }

    public String getCurrentJastiperId(String authorizationHeader) {
        AuthProfileResponse profile = getCurrentUserProfile(authorizationHeader);

        if (!profile.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "User account is not active"
            );
        }

        if (!"JASTIPER".equals(profile.getRole())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only JASTIPER can manage products"
            );
        }

        return profile.getId().toString();
    }
}