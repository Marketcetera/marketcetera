package org.marketcetera.dao.hibernate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.dao.AuthorityDao;
import org.marketcetera.dao.hibernate.impl.AuthorityInitializer;
import org.marketcetera.dao.impl.PersistentAuthority;
import org.marketcetera.core.systemmodel.Authority;
import org.marketcetera.core.systemmodel.SystemAuthority;
import org.springframework.dao.DataIntegrityViolationException;

/* $License$ */

/**
 * Tests {@link HibernateAuthorityDao}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class HibernateAuthorityDaoTest
        extends PersistentVersionedObjectDaoTestBase<Authority>
{
    /**
     * Tests the mechanism by which authorities are populated.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDefaults()
            throws Exception
    {
        AuthorityDao myDao = getDao().getAuthorityDao();
        // verify that the default authorities are not present
        for(SystemAuthority authority : SystemAuthority.values()) {
            assertNull(myDao.getByName(authority.name()));
        }
        // initialize the db and repeat
        getApp().getContext().getBeanFactory().createBean(AuthorityInitializer.class);
        // check that all these authorities now exist
        for(SystemAuthority authority : SystemAuthority.values()) {
            assertNotNull(myDao.getByName(authority.name()));
        }
        // initialize again (adding duplicates, or trying to, anyway
        getApp().getContext().getBeanFactory().createBean(AuthorityInitializer.class);
        // check that all these authorities still exist
        for(SystemAuthority authority : SystemAuthority.values()) {
            assertNotNull(myDao.getByName(authority.name()));
        }
    }
    /**
     * Tests that duplicate authority names are not allowed.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDuplicateUsername()
            throws Exception
    {
        PersistentAuthority authority1 = createNew();
        add(authority1);
        final PersistentAuthority authority2 = createNew();
        authority2.setAuthority(authority1.getAuthority());
        new ExpectedFailure<DataIntegrityViolationException>() {
            @Override
            protected void run()
                    throws Exception
            {
                add(authority2);
            }
        };
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.hibernate.PersistentSystemObjectDaoTestBase#createNew()
     */
    @Override
    protected PersistentAuthority createNew()
    {
        PersistentAuthority authority = new PersistentAuthority();
        authority.setAuthority("authority-" + System.nanoTime());
        return authority;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.hibernate.PersistentSystemObjectDaoTestBase#getTableClass()
     */
    @Override
    protected Class<? extends Authority> getTableClass()
    {
        return PersistentAuthority.class;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.hibernate.PersistentSystemObjectDaoTestBase#add(org.marketcetera.core.systemmodel.SystemObject)
     */
    @Override
    protected void add(Authority inData)
    {
        getDao().getAuthorityDao().add(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.hibernate.PersistentSystemObjectDaoTestBase#getById(long)
     */
    @Override
    protected Authority getById(long inId)
    {
        return getDao().getAuthorityDao().getById(inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.hibernate.PersistentVersionedObjectDaoTestBase#save(org.marketcetera.core.systemmodel.VersionedObject)
     */
    @Override
    protected void save(Authority inData)
    {
        getDao().getAuthorityDao().save(inData);
    }
}
