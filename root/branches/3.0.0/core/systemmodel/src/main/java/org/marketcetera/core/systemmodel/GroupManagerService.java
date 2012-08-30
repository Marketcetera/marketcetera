package org.marketcetera.core.systemmodel;

import java.util.List;

/* $License$ */

/**
 * Provides access to and management of <code>Group</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface GroupManagerService
{
    /**
     * Gets the <code>Group</code> with the given name. 
     *
     * @param inName a <code>String</code> value
     * @return an <code>Group</code> value
     */
    public Group getGroupByName(String inName);
    /**
     * Gets the <code>Group</code> with the given id.
     *
     * @param inId a <code>long</code> value
     * @return an <code>Group</code> value
     */
    public Group getGroupById(long inId);
    /**
     * Gets all <code>Group</code> values.
     *
     * @return a <code>List&lt;Group&gt;</code> value
     */
    public List<Group> getAllGroups();
    /**
     * Adds the given <code>Group</code> value.
     *
     * @param inGroup an <code>Group</code> value
     */
    public void addGroup(Group inGroup);
    /**
     * Saves the given <code>Group</code> value.
     *
     * @param inGroup an <code>Group</code> value
     */
    public void saveGroup(Group inGroup);
    /**
     * Deletes the given <code>Group</code> value.
     *
     * @param inGroup an <code>Group</code> value
     */
    public void deleteGroup(Group inGroup);
}
