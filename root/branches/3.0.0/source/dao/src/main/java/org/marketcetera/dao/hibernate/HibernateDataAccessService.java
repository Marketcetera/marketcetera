package org.marketcetera.dao.hibernate;

import javax.persistence.MappedSuperclass;

import org.marketcetera.dao.AuthorityDao;
import org.marketcetera.dao.DataAccessService;
import org.marketcetera.dao.GroupDao;
import org.marketcetera.dao.UserDao;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/* $License$ */

/**
 * Provides hibernate-based access service for system objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: HibernateDataAccessService.java 82355 2012-05-17 23:29:18Z colin $
 * @since $Release$
 */
@MappedSuperclass
@Repository
@ClassVersion("$Id: HibernateDataAccessService.java 82355 2012-05-17 23:29:18Z colin $")
public class HibernateDataAccessService
        implements DataAccessService
{
    /* (non-Javadoc)
     * @see org.marketcetera.dao.DataAccessService#getUserDao()
     */
    @Override
    public UserDao getUserDao()
    {
        return userDao;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.DataAccessService#getAuthorityDao()
     */
    @Override
    public AuthorityDao getAuthorityDao()
    {
        return authorityDao;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.DataAccessService#getGroupDao()
     */
    @Override
    public GroupDao getGroupDao()
    {
        return groupDao;
    }
    /**
     * authority data access object value
     */
    @Autowired
    private volatile AuthorityDao authorityDao;
    /**
     * user data access object value
     */
    @Autowired
    private volatile UserDao userDao;
    /**
     * group data access object value
     */
    @Autowired
    private volatile GroupDao groupDao;
}
