package org.marketcetera.api.security;

/**
 * Service Presentation for security services in Marketcetera
 *
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 8/19/12 3:59 AM
 */

public interface SecurityService {
    Subject getSubject();
}
