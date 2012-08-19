package org.marketcetera.security.shiro.impl;

import org.marketcetera.api.security.AuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 8/19/12 7:04 AM
 */

public class UsernamePasswordToken implements AuthenticationToken {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(UsernamePasswordToken.class);
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
