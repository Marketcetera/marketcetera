package org.marketcetera.api.security;

/**
 * Provides long-running context for a {@link Subject} across multiple stateless calls.
 *
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 8/19/12 4:00 AM
 */

public interface Session {
    Session findSession(String identifier);
    String identifier();
    void invalidate();
}
