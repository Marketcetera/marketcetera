package org.marketcetera.dao.hibernate.impl;

import org.marketcetera.dao.DataAccessService;
import org.marketcetera.dao.Messages;
import org.marketcetera.core.systemmodel.AuthorityFactory;
import org.marketcetera.core.systemmodel.SystemAuthority;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.api.attributes.ClassVersion;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Initializes the authorities data store.
 * 
 * <p>To use this class, instantiate from a Spring context. No existing authorities will
 * be removed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: AuthorityInitializer.java 82316 2012-03-21 21:13:27Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: AuthorityInitializer.java 82316 2012-03-21 21:13:27Z colin $")
public class AuthorityInitializer
        implements InitializingBean
{
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
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
