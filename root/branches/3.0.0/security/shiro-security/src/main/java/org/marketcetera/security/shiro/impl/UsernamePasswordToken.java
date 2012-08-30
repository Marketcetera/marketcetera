package org.marketcetera.security.shiro.impl;

import org.marketcetera.api.security.AuthenticationToken;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 8/19/12 7:04 AM
 */

public class UsernamePasswordToken implements AuthenticationToken {
    private String principal;
    private String credentials;

    public UsernamePasswordToken(String credentials, String principal) {
        this.credentials = credentials;
        this.principal = principal;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }
}
