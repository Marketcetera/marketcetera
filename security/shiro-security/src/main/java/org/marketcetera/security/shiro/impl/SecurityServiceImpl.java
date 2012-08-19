package org.marketcetera.security.shiro.impl;

import org.marketcetera.api.security.SecurityService;
import org.marketcetera.api.security.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 8/19/12 5:03 AM
 */

public class SecurityServiceImpl implements SecurityService {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(SecurityServiceImpl.class);


    @Override
    public Subject getSubject() {
        return new SubjectImpl();
    }
}
