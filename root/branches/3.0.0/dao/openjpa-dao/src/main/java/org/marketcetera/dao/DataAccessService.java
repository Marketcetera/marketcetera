package org.marketcetera.dao;

import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */

/**
 * Provides access to database services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: DataAccessService.java 82315 2012-03-17 01:58:54Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: DataAccessService.java 82315 2012-03-17 01:58:54Z colin $")
public interface DataAccessService
{
    /**
     * Gets the <code>UserDao</code> value.
     *
     * @return a <code>UserDao</code> value
     */
    public UserDao getUserDao();
    /**
     * Gets the <code>AuthorityDao</code> value.
     *
     * @return an <code>AuthorityDao</code> value
     */
    public AuthorityDao getAuthorityDao();
    /**
     * Gets the <code>GroupDao</code> value.
     *
     * @return a <code>Group</code> value
     */
    public GroupDao getGroupDao();
}
