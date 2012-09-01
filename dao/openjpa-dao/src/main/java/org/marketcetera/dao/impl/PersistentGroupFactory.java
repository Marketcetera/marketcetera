package org.marketcetera.dao.impl;

import org.marketcetera.api.dao.Group;
import org.marketcetera.core.systemmodel.GroupFactory;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 9/1/12 7:33 PM
 */

public class PersistentGroupFactory implements GroupFactory {

    @Override
    public Group create(String inGroupname) {
        return null;
    }

    @Override
    public Group create() {
        return null;
    }
}
