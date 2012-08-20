package org.marketcetera.dao.hibernate.impl;

import org.marketcetera.core.systemmodel.Authority;
import org.marketcetera.core.systemmodel.AuthorityFactory;
import org.marketcetera.api.attributes.ClassVersion;
import org.marketcetera.dao.impl.PersistentAuthority;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Creates persistent {@link Authority} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PersistentAuthorityFactory.java 82315 2012-03-17 01:58:54Z colin $
 * @since $Release$
 */
@Component
@ClassVersion("$Id: PersistentAuthorityFactory.java 82315 2012-03-17 01:58:54Z colin $")
class PersistentAuthorityFactory
        implements AuthorityFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.AuthorityFactory#create(java.lang.String)
     */
    @Override
    public Authority create(String inAuthorityName)
    {
        PersistentAuthority authority = new PersistentAuthority();
        authority.setAuthority(inAuthorityName);
        return authority;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.AuthorityFactory#create()
     */
    @Override
    public Authority create()
    {
        return new PersistentAuthority();
    }
}
