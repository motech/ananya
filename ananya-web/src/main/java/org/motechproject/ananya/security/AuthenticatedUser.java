package org.motechproject.ananya.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class AuthenticatedUser extends User {

    public AuthenticatedUser(List<GrantedAuthority> grantedAuthorityList,
                             String username,
                             String password) {
        super(username, password, true, true, true, true, grantedAuthorityList);
    }

}
