package org.motechproject.ananya.security;


import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final String INVALID_PASSWORD_ERROR = "Incorrect password";
    private final String USER_NOT_EXISTS_ERROR = "User does not exist";

    public AuthenticationResponse checkFor(String username, String password) {
        if (StringUtils.isBlank(username))
            throw new BadCredentialsException(USER_NOT_EXISTS_ERROR);

        if (!username.equals("admin") || !password.equals("p@ssw0rd")) {
            throw new BadCredentialsException(INVALID_PASSWORD_ERROR);
        }
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.addRole("admin");
        return authenticationResponse;
    }
}
