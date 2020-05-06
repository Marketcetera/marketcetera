package org.marketcetera.webui.security;

import com.vaadin.flow.server.ServletHelper.RequestType;
import com.vaadin.flow.shared.ApplicationConstants;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Stream;

/* $License$ */

/**
 * SecurityUtils takes care of all such static operations that have to do with security and querying rights from different beans of the UI.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public final class SecurityUtils
{
    /**
     * Tests if the request is an internal framework request.
     * 
     * <p>The test consists of checking if the request parameter is present and if its value is consistent
     * with any of the request types know.
     *
     * @param inRequest a <code>HttpServletRequest</code> value
     * @return a <code>boolean</code> value set to true if is an internal framework request. False otherwise.
     */
    static boolean isFrameworkInternalRequest(HttpServletRequest inRequest)
    {
        final String parameterValue = inRequest.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null && Stream.of(RequestType.values()).anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }
    /**
     * Tests if some user is authenticated. As Spring Security always will create an {@link AnonymousAuthenticationToken}
     * we have to ignore those tokens explicitly.
     */
    static boolean isUserLoggedIn()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.isAuthenticated();
    }
    /**
     * Create a new SecurityUtils instance.
     */
    private SecurityUtils()
    {
        // Util methods only
    }
}
