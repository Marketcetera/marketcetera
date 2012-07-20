package org.marketcetera.systemmodel;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Creates <code>Group</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
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
