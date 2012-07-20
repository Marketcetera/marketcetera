package org.marketcetera.dao.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.dao.impl.*;
import org.marketcetera.systemmodel.*;
import org.marketcetera.util.test.UnicodeData;
import org.springframework.dao.DataIntegrityViolationException;

/* $License$ */

/**
 * Tests {@link HibernateUserDao}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: HibernateUserDaoTest.java 82384 2012-07-20 19:09:59Z colin $
 * @since $Release$
 */
public class HibernateUserDaoTest
        extends PersistentVersionedObjectDaoTestBase<User>
{
    /**
     * Tests the mechanism by which users are populated.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDefaults()
            throws Exception
    {
        // no default user exists
        assertNull(getDao().getUserDao().getByName("admin"));
        // initialize
        getApp().getContext().getBeanFactory().createBean(UserInitializer.class);
        // no user yet
        assertNull(getDao().getUserDao().getByName("admin"));
        // before running the userInitializer, we also need the groups and authorities initializers
        GroupInitializer groupInitializer = new GroupInitializer();
        // autowire the bean
        getApp().getContext().getBeanFactory().autowireBean(groupInitializer);
        groupInitializer.initialize();
        AuthorityInitializer authorityInitializer = new AuthorityInitializer();
        // autowire the bean
        getApp().getContext().getBeanFactory().autowireBean(authorityInitializer);
        authorityInitializer.initialize();
        // create a specification for the admin user
        UserInitializer userInitializer = new UserInitializer();
        Set<UserSpecification> users = new HashSet<UserSpecification>();
        UserSpecification user = new UserSpecification();
        user.setUsername("admin");
        user.setPassword("admin");
        Set<String> groups = new HashSet<String>();
        groups.add(SystemGroup.ADMINISTRATORS.name());
        user.setGroups(groups);
        users.add(user);
        userInitializer.setUsers(users);
        // autowire the bean
        getApp().getContext().getBeanFactory().autowireBean(userInitializer);
        userInitializer.initialize();
        // default user now exists
        assertNotNull(getDao().getUserDao().getByName("admin"));
        // repeat 
        getApp().getContext().getBeanFactory().createBean(UserInitializer.class);
        // default user still exists
        assertNotNull(getDao().getUserDao().getByName("admin"));
        // validate that the admin user has the appropriate permissions
        User admin = getDao().getUserDao().getByName("admin");
        assertEquals(1,
                     admin.getAuthorities().size());
        assertEquals(SystemAuthority.ROLE_ADMIN.name(),
                     admin.getAuthorities().iterator().next().getAuthority());
        Group userGroup = getDao().getGroupDao().getByName(SystemGroup.USERS.name());
        assertEquals(1,
                     userGroup.getAuthorities().size());
        assertEquals(SystemAuthority.ROLE_USER.name(),
                     userGroup.getAuthorities().iterator().next().getAuthority());
    }
    /**
     * Tests the detection of duplicate authorities. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDuplicateAuthorities()
            throws Exception
    {
        // create a new user
        PersistentUser testUser = new PersistentUser();
        testUser.setUsername("test-" + System.nanoTime());
        getDao().getUserDao().add(testUser);
        // create 2 new groups
        GroupFactory groupFactory = getApp().getContext().getBeanFactory().getBean(GroupFactory.class);
        Group testGroup1 = groupFactory.create("test1-" + System.nanoTime());
        Group testGroup2 = groupFactory.create("test2-" + System.nanoTime());
        // add the user to both groups
        testGroup1.getUsers().add(testUser);
        testGroup2.getUsers().add(testUser);
        // add a single authority to both groups
        AuthorityFactory authorityFactory = getApp().getContext().getBeanFactory().getBean(AuthorityFactory.class);
        Authority userAuthority = authorityFactory.create("test-" + System.nanoTime());
        getDao().getAuthorityDao().add(userAuthority);
        testGroup1.getAuthorities().add(userAuthority);
        testGroup2.getAuthorities().add(userAuthority);
        // persist both groups
        getDao().getGroupDao().add(testGroup1);
        getDao().getGroupDao().add(testGroup2);
        // make sure the user has only one authority, not two
        User user = getDao().getUserDao().getById(testUser.getId());
        assertEquals(1,
                     user.getAuthorities().size());
        assertEquals(userAuthority.getAuthority(),
                     user.getAuthorities().iterator().next().getAuthority());
    }
    /**
     * Tests that duplicate usernames are not allowed.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDuplicateUsername()
            throws Exception
    {
        PersistentUser user1 = createNew();
        add(user1);
        final PersistentUser user2 = createNew();
        user2.setUsername(user1.getUsername());
        new ExpectedFailure<DataIntegrityViolationException>() {
            @Override
            protected void run()
                    throws Exception
            {
                add(user2);
            }
        };
    }
    /**
     * Tests non-ascii attributes.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testUnicode()
            throws Exception
    {
        PersistentUser user1 = createNew();
        user1.setUsername(UnicodeData.GOODBYE_JA);
        user1.setPassword(UnicodeData.HOUSE_AR);
        add(user1);
        User retrievedUser = getById(user1.getId());
        assertEquals(UnicodeData.GOODBYE_JA,
                     retrievedUser.getUsername());
        assertEquals(UnicodeData.HOUSE_AR,
                     retrievedUser.getPassword());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.hibernate.PersistentSystemObjectDaoTestBase#createNew()
     */
    @Override
    protected PersistentUser createNew()
    {
        PersistentUser user = new PersistentUser();
        user.setUsername("test user " + System.nanoTime());
        return user;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.hibernate.PersistentSystemObjectDaoTestBase#getTableClass()
     */
    @Override
    protected Class<PersistentUser> getTableClass()
    {
        return PersistentUser.class;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.hibernate.PersistentVersionedObjectDaoTestBase#save(org.marketcetera.systemmodel.VersionedObject)
     */
    @Override
    protected void save(User inData)
    {
        getDao().getUserDao().save(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.hibernate.PersistentSystemObjectDaoTestBase#add(org.marketcetera.systemmodel.SystemObject)
     */
    @Override
    protected void add(User inData)
    {
        getDao().getUserDao().add(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.hibernate.PersistentSystemObjectDaoTestBase#getById(long)
     */
    @Override
    protected User getById(long inId)
    {
        return getDao().getUserDao().getById(inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.hibernate.PersistentSystemObjectDaoTestBase#clearTable()
     */
    @Override
    protected void clearTable()
            throws Exception
    {
    }
}
