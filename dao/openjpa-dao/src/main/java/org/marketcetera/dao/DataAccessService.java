package org.marketcetera.dao;

/* $License$ */

import org.marketcetera.api.dao.UserDao;
import org.marketcetera.api.dao.AuthorityDao;
import org.marketcetera.api.dao.GroupDao;

/**
 * Provides access to database services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: DataAccessService.java 82315 2012-03-17 01:58:54Z colin $
 * @since $Release$
 */
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
