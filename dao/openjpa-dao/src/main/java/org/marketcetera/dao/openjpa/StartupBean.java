package org.marketcetera.dao.openjpa;

import org.marketcetera.dao.AuthorityDao;
import org.marketcetera.dao.impl.PersistentAuthority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 7/14/12 3:38 AM
 */

public class StartupBean {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(StartupBean.class);
    private AuthorityDao authorityDao;

    public void setAuthorityDao(AuthorityDao authorityDao) {
        this.authorityDao = authorityDao;
    }

    public void activate() {
        PersistentAuthority authority = new PersistentAuthority();
        authority.setAuthority("foo");
        authorityDao.save(authority);
    }
}
