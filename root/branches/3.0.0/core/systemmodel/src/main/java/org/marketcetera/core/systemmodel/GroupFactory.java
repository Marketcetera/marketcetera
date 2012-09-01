package org.marketcetera.core.systemmodel;

/* $License$ */

import org.marketcetera.api.dao.Group;

/**
 * Creates <code>Group</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: GroupFactory.java 82315 2012-03-17 01:58:54Z colin $
 * @since $Release$
 */
public interface GroupFactory
{
    /**
     * Creates a <code>Group</code> object with the given attributes.
     *
     * @param inGroupname a <code>String</code> value
     * @return a <code>Group</code> value
     */
    public Group create(String inGroupname);
    /**
     * Creates a <code>Group</code> object.
     *
     * @return a <code>Group</code> value
     */
    public Group create();
}
