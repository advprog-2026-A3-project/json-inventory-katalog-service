package id.ac.ui.cs.advprog.inventorykatalog.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AuthProfileResponseTest {

    @Test
    void settersAndGettersShouldWork() {
        AuthProfileResponse response = new AuthProfileResponse();

        response.setId(10L);
        response.setEmail("user@example.com");
        response.setUsername("user");
        response.setRole("JASTIPER");
        response.setKycStatus("APPROVED");
        response.setActive(true);

        assertEquals(10L, response.getId());
        assertEquals("user@example.com", response.getEmail());
        assertEquals("user", response.getUsername());
        assertEquals("JASTIPER", response.getRole());
        assertEquals("APPROVED", response.getKycStatus());
        assertTrue(response.isActive());

        response.setActive(false);
        assertFalse(response.isActive());
    }
}
