package org.motechproject.ananya.security;

import java.util.ArrayList;
import java.util.List;

public class AuthenticationResponse {
    private List<String> roles = new ArrayList<String>();

    public List<String> roles() {
        return roles;
    }

    public void addRole(String roleName) {
        this.roles.add(roleName);
    }

}
