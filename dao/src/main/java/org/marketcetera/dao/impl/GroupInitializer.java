package org.marketcetera.dao.impl;

import org.marketcetera.dao.DataAccessService;
import org.marketcetera.dao.Messages;
import org.marketcetera.systemmodel.GroupFactory;
import org.marketcetera.systemmodel.SystemGroup;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Initializes the groups data store.
 *
 * <p>To use this class, instantiate from a Spring context. No existing groups will
 * be removed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class GroupInitializer
        implements Initializer
{
    /* (non-Javadoc)
     * @see org.marketcetera.dao.impl.Initializer#initialize()
     */
    @Override
    public void initialize()
    {
        for(SystemGroup group : SystemGroup.values()) {
            try {
                SLF4JLoggerProxy.debug(GroupInitializer.class,
                                       "Adding default group {}", //$NON-NLS-1$
                                       group);
                dataService.getGroupDao().add(groupFactory.create(group.name()));
            } catch (Exception e) {
                Messages.CANNOT_ADD_GROUP.warn(GroupInitializer.class,
                                               e,
                                               group);
            }
        }
    }
    /**
     * provides data services
     */
    @Autowired
    private DataAccessService dataService;
    /**
     * provides the ability to construct <code>Group</code> objects
     */
    @Autowired
    private GroupFactory groupFactory;
}
