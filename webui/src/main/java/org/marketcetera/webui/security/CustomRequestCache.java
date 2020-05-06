package org.marketcetera.webui.security;

import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* $License$ */

/**
 * HttpSessionRequestCache that avoids saving internal framework requests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class CustomRequestCache
        extends HttpSessionRequestCache
{
    /**
     * {@inheritDoc}
     *
     * If the method is considered an internal request from the framework, we skip
     * saving it.
     *
     * @see SecurityUtils#isFrameworkInternalRequest(HttpServletRequest)
     */
    @Override
    public void saveRequest(HttpServletRequest request,
                            HttpServletResponse response)
    {
        if (!SecurityUtils.isFrameworkInternalRequest(request)) {
            super.saveRequest(request, response);
        }
    }
}
