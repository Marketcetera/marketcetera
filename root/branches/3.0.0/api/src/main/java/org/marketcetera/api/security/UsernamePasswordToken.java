package org.marketcetera.api.security;

/**
 * @version $Id$
 * @date 8/19/12 7:04 AM
 */

public class UsernamePasswordToken implements AuthenticationToken {
    private String principal;
    private String credentials;

    public UsernamePasswordToken(String principal, String credentials) {
        this.credentials = credentials;
        this.principal = principal;
    }

    @Override
    public String getPrincipal() {
        return principal;
    }

    @Override
    public String getCredentials() {
        return credentials;
    }
}
