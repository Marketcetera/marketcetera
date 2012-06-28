package org.marketcetera.dao.impl;

import org.marketcetera.systemmodel.Group;
import org.marketcetera.systemmodel.GroupFactory;
import org.marketcetera.core.attributes.ClassVersion;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Creates persistent {@link Group} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PersistentGroupFactory.java 82316 2012-03-21 21:13:27Z colin $
 * @since $Release$
 */
@Component
@ClassVersion("$Id: PersistentGroupFactory.java 82316 2012-03-21 21:13:27Z colin $")
public class PersistentGroupFactory
        implements GroupFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.GroupFactory#create(java.lang.String)
     */
    @Override
    public Group create(String inGroupname)
    {
        PersistentGroup group = new PersistentGroup();
        group.setName(inGroupname);
        return group;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.GroupFactory#create()
     */
    @Override
    public Group create()
    {
        return new PersistentGroup();
    }
}
