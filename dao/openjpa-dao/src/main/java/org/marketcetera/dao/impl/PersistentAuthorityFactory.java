package org.marketcetera.dao.impl;

import org.marketcetera.api.dao.Authority;
import org.marketcetera.core.systemmodel.AuthorityFactory;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 9/1/12 7:34 PM
 */

public class PersistentAuthorityFactory implements AuthorityFactory {

    @Override
    public Authority create(String inAuthorityName) {
        return null;
    }

    @Override
    public Authority create() {
        return null;
    }
}
