package org.marketcetera.dao.hibernate.impl;

import org.marketcetera.core.systemmodel.Group;
import org.marketcetera.core.systemmodel.GroupFactory;
import org.marketcetera.api.attributes.ClassVersion;
import org.marketcetera.dao.impl.PersistentGroup;
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
public class PersistentGroupFactory
        implements GroupFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.GroupFactory#create(java.lang.String)
     */
    @Override
    public Group create(String inGroupname)
    {
        PersistentGroup group = new PersistentGroup();
        group.setName(inGroupname);
        return group;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.GroupFactory#create()
     */
    @Override
    public Group create()
    {
        return new PersistentGroup();
    }
}
