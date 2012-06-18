package org.marketcetera.dao;

import java.util.List;

import org.marketcetera.systemmodel.Group;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.security.access.prepost.PreAuthorize;

/* $License$ */

/**
 * Provides data access for {@link Group} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: GroupDao.java 82354 2012-05-11 17:46:11Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: GroupDao.java 82354 2012-05-11 17:46:11Z colin $")
public interface GroupDao
{
    /**
     * Gets the <code>Group</code> corresponding to the given name.
     *
     * @param inName a <code>String</code> value
     * @return a <code>Group</code> value
     */
    public Group getByName(String inName);
    /**
     * Adds the given <code>Group</code> to the database.
     *
     * @param inData a <code>Group</code> value
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void add(Group inData);
    /**
     * Saves the given <code>Group</code> to the database.
     *
     * @param inData a <code>Group</code> value
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void save(Group inData);
    /**
     * Gets the <code>Group</code> corresponding to the given id.
     *
     * @param inId a <code>long</code> value
     * @return a <code>Group</code> value
     */
    public Group getById(long inId);
    /**
     * Gets all <code>Group</code> values.
     *
     * @return a <code>List&lt;Group&gt;</code> value
     */
    public List<Group> getAll();
    /**
     * Deletes the given <code>Group</code> from the database.
     *
     * @param inData a <code>Group</code> value
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(Group inData);
}
