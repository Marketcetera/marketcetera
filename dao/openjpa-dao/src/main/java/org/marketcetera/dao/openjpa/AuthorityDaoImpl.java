package org.marketcetera.dao.openjpa;

import java.util.List;

import org.marketcetera.dao.AuthorityDao;
import org.marketcetera.core.systemmodel.Authority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 6/29/12 12:38 AM
 */

public class AuthorityDaoImpl implements AuthorityDao {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(AuthorityDaoImpl.class);


    @Override
    public void add(Authority inData) {
        log.trace("Entering add");

    }

    @Override
    public void save(Authority inData) {
        log.trace("Entering save");

    }

    @Override
    public Authority getByName(String inName) {
        log.trace("Entering getByName");
        return null;
    }

    @Override
    public Authority getById(long inId) {
        log.trace("Entering getById");
        return null;
    }

    @Override
    public List<Authority> getAll() {
        log.trace("Entering getAll");
        return null;
    }
}
