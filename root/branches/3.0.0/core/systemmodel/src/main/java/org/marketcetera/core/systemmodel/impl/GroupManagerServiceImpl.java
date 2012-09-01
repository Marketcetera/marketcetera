package org.marketcetera.core.systemmodel.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.api.dao.Group;
import org.marketcetera.api.dao.GroupDao;
import org.marketcetera.api.security.GroupManagerService;

/* $License$ */

/**
 * Provides Group manager services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class GroupManagerServiceImpl
        implements GroupManagerService
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.GroupManagerService#getGroupByName(java.lang.String)
     */
    @Override
    public Group getGroupByName(String inName)
    {
        inName = StringUtils.trimToNull(inName);
        Validate.notNull(inName);
        return groupDao.getByName(inName);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.GroupManagerService#getGroupById(long)
     */
    @Override
    public Group getGroupById(long inId)
    {
        return groupDao.getById(inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.GroupManagerService#getAllGroups()
     */
    @Override
    public List<Group> getAllGroups()
    {
        return groupDao.getAll();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.GroupManagerService#addGroup(org.marketcetera.api.dao.Group)
     */
    @Override
    public void addGroup(Group inGroup)
    {
        Validate.notNull(inGroup);
        groupDao.add(inGroup);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.GroupManagerService#saveGroup(org.marketcetera.api.dao.Group)
     */
    @Override
    public void saveGroup(Group inGroup)
    {
        Validate.notNull(inGroup);
        groupDao.save(inGroup);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.GroupManagerService#deleteGroup(org.marketcetera.api.dao.Group)
     */
    @Override
    public void deleteGroup(Group inGroup)
    {
        Validate.notNull(inGroup);
        groupDao.delete(inGroup);
    }
    /**
     * Sets the groupDao value.
     *
     * @param an <code>GroupDao</code> value
     */
    public void setGroupDao(GroupDao inGroupDao)
    {
        groupDao = inGroupDao;
    }
    /**
     * group DAO value
     */
    private GroupDao groupDao;
}
