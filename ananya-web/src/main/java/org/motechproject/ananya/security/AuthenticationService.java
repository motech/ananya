package org.motechproject.ananya.security;


import org.motechproject.ananya.support.console.AdminUser;
import org.motechproject.ananya.support.console.AllAdminUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final String USER_NOT_EXISTS_ERROR = "User does not exist";
    private final String INVALID_PASSWORD_ERROR = "Incorrect password";

    private AllAdminUsers allAdminUsers;

    @Autowired
    public AuthenticationService(AllAdminUsers allAdminUsers) {
        this.allAdminUsers = allAdminUsers;
    }

    public AuthenticationResponse checkFor(String username, String password) {
        AdminUser user = allAdminUsers.findByName(username);
        if (user == null)
            throw new BadCredentialsException(USER_NOT_EXISTS_ERROR);

        if (!user.passwordIs(password))
            throw new BadCredentialsException(INVALID_PASSWORD_ERROR);

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.addRole("admin");
        return authenticationResponse;
    }
}
