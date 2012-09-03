package org.marketcetera.dao.impl;

import org.marketcetera.api.dao.Permission;
import org.marketcetera.core.systemmodel.PermissionFactory;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 9/1/12 7:34 PM
 */

public class PersistentPermissionFactory implements PermissionFactory {

    @Override
    public Permission create(String inPermissionName) {
        return null;
    }

    @Override
    public Permission create() {
        return null;
    }
}
