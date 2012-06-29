package org.marketcetera.dao.openjpa;

import java.util.List;

import org.marketcetera.dao.GroupDao;
import org.marketcetera.systemmodel.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 6/29/12 12:39 AM
 */

public class GroupDaoImpl implements GroupDao {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(GroupDaoImpl.class);


    @Override
    public Group getByName(String inName) {
        log.trace("Entering getByName");
        return null;
    }

    @Override
    public void add(Group inData) {
        log.trace("Entering add");

    }

    @Override
    public void save(Group inData) {
        log.trace("Entering save");

    }

    @Override
    public Group getById(long inId) {
        log.trace("Entering getById");
        return null;
    }

    @Override
    public List<Group> getAll() {
        log.trace("Entering getAll");
        return null;
    }

    @Override
    public void delete(Group inData) {
        log.trace("Entering delete");

    }
}
