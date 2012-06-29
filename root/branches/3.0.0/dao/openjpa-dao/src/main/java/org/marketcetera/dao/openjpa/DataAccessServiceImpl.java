package org.marketcetera.dao.openjpa;

import org.marketcetera.dao.AuthorityDao;
import org.marketcetera.dao.DataAccessService;
import org.marketcetera.dao.GroupDao;
import org.marketcetera.dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 6/29/12 12:39 AM
 */

public class DataAccessServiceImpl implements DataAccessService {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(DataAccessServiceImpl.class);


    @Override
    public UserDao getUserDao() {
        log.trace("Entering getUserDao");
        return null;
    }

    @Override
    public AuthorityDao getAuthorityDao() {
        log.trace("Entering getAuthorityDao");
        return null;
    }

    @Override
    public GroupDao getGroupDao() {
        log.trace("Entering getGroupDao");
        return null;
    }
}
