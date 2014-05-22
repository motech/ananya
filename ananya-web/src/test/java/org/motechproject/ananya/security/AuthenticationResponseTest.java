package org.motechproject.ananya.security;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class AuthenticationResponseTest {

    @Test
    public void shouldAddRole(){
        AuthenticationResponse response = new AuthenticationResponse();
        response.addRole("admin");
        assertTrue(response.roles().contains("admin"));
    }
}
