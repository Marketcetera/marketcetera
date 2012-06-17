package org.marketcetera.dao.hibernate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.dao.GroupDao;
import org.marketcetera.dao.impl.GroupInitializer;
import org.marketcetera.dao.impl.PersistentGroup;
import org.marketcetera.systemmodel.Group;
import org.marketcetera.systemmodel.SystemGroup;
import org.springframework.dao.DataIntegrityViolationException;

/* $License$ */

/**
 * Tests {@link HibernateGroupDao}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: HibernateGroupDaoTest.java 82316 2012-03-21 21:13:27Z colin $
 * @since $Release$
 */
public class HibernateGroupDaoTest
        extends PersistentVersionedObjectDaoTestBase<Group>
{
    /**
     * Tests the mechanism by which groups are populated.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDefaults()
            throws Exception
    {
        GroupDao myDao = getDao().getGroupDao();
        // verify that the default groups are not present
        for(SystemGroup group : SystemGroup.values()) {
            assertNull(myDao.getByName(group.name()));
        }
        // initialize the db and repeat
        getApp().getContext().getBeanFactory().createBean(GroupInitializer.class);
        // check that all these groups now exist
        for(SystemGroup group : SystemGroup.values()) {
            assertNotNull(myDao.getByName(group.name()));
        }
        // initialize again (adding duplicates, or trying to, anyway
        getApp().getContext().getBeanFactory().createBean(GroupInitializer.class);
        // check that all these groups still exist
        for(SystemGroup group : SystemGroup.values()) {
            assertNotNull(myDao.getByName(group.name()));
        }
    }
    /**
     * Tests that duplicate group names are not allowed.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDuplicateGroupname()
            throws Exception
    {
        PersistentGroup group1 = createNew();
        add(group1);
        final PersistentGroup group2 = createNew();
        group2.setName(group1.getName());
        new ExpectedFailure<DataIntegrityViolationException>() {
            @Override
            protected void run()
                    throws Exception
            {
                add(group2);
            }
        };
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.hibernate.PersistentSystemObjectDaoTestBase#createNew()
     */
    @Override
    protected PersistentGroup createNew()
    {
        PersistentGroup group = new PersistentGroup();
        group.setName("group-" + System.nanoTime());
        return group;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.hibernate.PersistentSystemObjectDaoTestBase#getTableClass()
     */
    @Override
    protected Class<? extends Group> getTableClass()
    {
        return PersistentGroup.class;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.hibernate.PersistentSystemObjectDaoTestBase#add(org.marketcetera.systemmodel.SystemObject)
     */
    @Override
    protected void add(Group inData)
    {
        getDao().getGroupDao().add(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.hibernate.PersistentSystemObjectDaoTestBase#getById(long)
     */
    @Override
    protected Group getById(long inId)
    {
        return getDao().getGroupDao().getById(inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.hibernate.PersistentVersionedObjectDaoTestBase#save(org.marketcetera.systemmodel.VersionedObject)
     */
    @Override
    protected void save(Group inData)
    {
        getDao().getGroupDao().save(inData);
    }
}
