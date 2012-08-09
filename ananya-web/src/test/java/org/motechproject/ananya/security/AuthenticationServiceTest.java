package org.motechproject.ananya.security;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.support.console.AdminUser;
import org.motechproject.ananya.support.console.AllAdminUsers;
import org.springframework.security.authentication.BadCredentialsException;

import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class AuthenticationServiceTest {

    @Mock
    private AllAdminUsers allAdminUsers;

    private AuthenticationService authenticationService;

    @Before
    public void setUp() {
        initMocks(this);
        authenticationService = new AuthenticationService(allAdminUsers);
    }

    @Test
    public void shouldUseAllAdminUsersToValidateUser() {
        String username = "samuraiJack";
        String password = "aku";
        AdminUser adminUser = new AdminUser(username, password);

        when(allAdminUsers.findByName(username)).thenReturn(adminUser);

        AuthenticationResponse authenticationResponse = authenticationService.checkFor(username, password);
        assertTrue(authenticationResponse.roles().contains("admin"));
    }

    @Test(expected = BadCredentialsException.class)
    public void shouldThrowExceptionIfUserIsNotFound() {
        String username = "samuraiJack";
        String password = "aku";

        when(allAdminUsers.findByName(username)).thenReturn(null);

        authenticationService.checkFor(username, password);
    }
    
    @Test(expected = BadCredentialsException.class)
    public void shouldThrowExceptionIfUserPasswordIsNotCorrect() {
        String username = "samuraiJack";
        String password = "aku";
        AdminUser adminUser = new AdminUser(username, password);

        when(allAdminUsers.findByName(username)).thenReturn(adminUser);

        authenticationService.checkFor(username, "notAku");
    }
}
