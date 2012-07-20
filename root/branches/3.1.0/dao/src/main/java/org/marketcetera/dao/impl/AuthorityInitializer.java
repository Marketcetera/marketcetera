package org.marketcetera.dao.impl;

import org.marketcetera.dao.DataAccessService;
import org.marketcetera.dao.Messages;
import org.marketcetera.systemmodel.AuthorityFactory;
import org.marketcetera.systemmodel.SystemAuthority;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Initializes the authorities data store.
 * 
 * <p>To use this class, instantiate from a Spring context. No existing authorities will
 * be removed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class AuthorityInitializer
        implements Initializer
{
    /* (non-Javadoc)
     * @see org.marketcetera.dao.impl.Initializer#initialize()
     */
    @Override
    public void initialize()
    {
        for(SystemAuthority authority : SystemAuthority.values()) {
            try {
                SLF4JLoggerProxy.debug(AuthorityInitializer.class,
                                       "Adding default authority {}", //$NON-NLS-1$
                                       authority);
                dataService.getAuthorityDao().add(authorityFactory.create(authority.name()));
            } catch (Exception e) {
                Messages.CANNOT_ADD_AUTHORITY.warn(AuthorityInitializer.class,
                                                   e,
                                                   authority);
            }
        }
    }
    /**
     * provides data services
     */
    @Autowired
    private DataAccessService dataService;
    /**
     * provides the ability to construct <code>Authority</code> objects
     */
    @Autowired
    private AuthorityFactory authorityFactory;
}
