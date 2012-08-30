package org.marketcetera.security.shiro.impl;

import org.marketcetera.api.security.SecurityService;
import org.marketcetera.api.security.Subject;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 8/19/12 5:03 AM
 */

public class SecurityServiceImpl implements SecurityService {


    @Override
    public Subject getSubject() {
        return new SubjectImpl();
    }
}
