package org.marketcetera.api.security;

/**
 * <p>An <tt>AuthenticationToken</tt> is a consolidation of an account's principals and supporting
 * credentials submitted by a user during an authentication attempt.
 *
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 8/19/12 4:01 AM
 */

public interface AuthenticationToken {
    Object getPrincipal();
    Object getCredentials();
}
