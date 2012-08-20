package org.marketcetera.dao.hibernate.impl;

import org.marketcetera.dao.DataAccessService;
import org.marketcetera.dao.Messages;
import org.marketcetera.core.systemmodel.GroupFactory;
import org.marketcetera.core.systemmodel.SystemGroup;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.api.attributes.ClassVersion;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Initializes the groups data store.
 *
 * <p>To use this class, instantiate from a Spring context. No existing groups will
 * be removed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: GroupInitializer.java 82316 2012-03-21 21:13:27Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: GroupInitializer.java 82316 2012-03-21 21:13:27Z colin $")
public class GroupInitializer
        implements InitializingBean
{
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
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
