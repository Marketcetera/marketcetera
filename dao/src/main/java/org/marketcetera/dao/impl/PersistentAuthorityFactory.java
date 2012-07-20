package org.marketcetera.dao.impl;

import org.marketcetera.systemmodel.Authority;
import org.marketcetera.systemmodel.AuthorityFactory;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Creates persistent {@link Authority} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@ClassVersion("$Id$")
class PersistentAuthorityFactory
        implements AuthorityFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.AuthorityFactory#create(java.lang.String)
     */
    @Override
    public Authority create(String inAuthorityName)
    {
        PersistentAuthority authority = new PersistentAuthority();
        authority.setAuthority(inAuthorityName);
        return authority;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.AuthorityFactory#create()
     */
    @Override
    public Authority create()
    {
        return new PersistentAuthority();
    }
}
