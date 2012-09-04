package org.marketcetera.core.systemmodel.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.api.security.User;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.systemmodel.SystemmodelTestBase;

/* $License$ */

/**
 * Tests {@link UserManagerServiceImpl}.
 *
 * @version $Id$
 * @since $Release$
 */
public class UserManagerServiceImplTest
        extends SystemmodelTestBase
{
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        super.setup();
        userManagerService = new UserManagerServiceImpl();
        userManagerService.setUserDao(userDao);
    }
    /**
     * Tests {@link UserManagerServiceImpl#getUserByName(String)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetUserByName()
            throws Exception
    {
        // null value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                userManagerService.getUserByName(null);
            }
        };
        // empty value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                userManagerService.getUserByName("");
            }
        };
        // empty value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                userManagerService.getUserByName("    ");
            }
        };
        // no match
        User badUser = generateUser();
        assertNull(userDataStore.getByName(badUser.getName()));
        assertEquals(null,
                     userManagerService.getUserByName(badUser.getName()));
        verify(userDao).getByName(badUser.getName());
        // good match
        User goodUser = userDataStore.getAll().iterator().next();
        assertNotNull(goodUser);
        assertSame(goodUser,
                   userManagerService.getUserByName(goodUser.getName()));
        verify(userDao).getByName(goodUser.getName());
        verify(userDao,
               times(2)).getByName(anyString());
    }
    /**
     * Tests {@link UserManagerServiceImpl#addUser(User)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAddUser()
            throws Exception
    {
        // null value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                userManagerService.addUser(null);
            }
        };
        // non-null value
        User newUser = generateUser();
        userManagerService.addUser(newUser);
        verify(userDao).add(newUser);
        // re-add same value
        userManagerService.addUser(newUser);
        verify(userDao,
               times(2)).add(newUser);
        // add new, distinct value
        User anotherNewUser = generateUser();
        userManagerService.addUser(anotherNewUser);
        verify(userDao,
               times(3)).add((User)any());
        verify(userDao).add(anotherNewUser);
    }
    /**
     * Tests {@link UserManagerServiceImpl#saveUser(User)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testSaveUser()
            throws Exception
    {
        // null value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                userManagerService.saveUser(null);
            }
        };
        User newUser = generateUser();
        assertNull(userDataStore.getByName(newUser.getName()));
        userManagerService.saveUser(newUser);
        verify(userDao).save(newUser);
        assertSame(newUser,
                   userDataStore.getByName(newUser.getName()));
    }
    /**
     * Tests {@link UserManagerServiceImpl#deleteUser(User)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDeleteUser()
            throws Exception
    {
        // null value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                userManagerService.deleteUser(null);
            }
        };
        // delete non-existent user
        User newUser = generateUser();
        assertNull(userDataStore.getByName(newUser.getName()));
        userManagerService.deleteUser(newUser);
        verify(userDao).delete(newUser);
        assertNull(userDataStore.getByName(newUser.getName()));
        // delete valid user
        User goodUser = userDataStore.getAll().iterator().next();
        assertNotNull(userDataStore.getByName(goodUser.getName()));
        userManagerService.deleteUser(goodUser);
        verify(userDao,
               times(2)).delete((User)any());
        assertNull(userDataStore.getByName(goodUser.getName()));
    }
    /**
     * Tests {@link UserManagerServiceImpl#getUserById(long)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetUserById()
            throws Exception
    {
        // missing ids
        assertNull(userDataStore.getById(Long.MIN_VALUE));
        assertNull(userManagerService.getUserById(Long.MIN_VALUE));
        verify(userDao).getById(Long.MIN_VALUE);
        verify(userDao,
               times(1)).getById(anyLong());
        assertNull(userDataStore.getById(Long.MAX_VALUE));
        assertNull(userManagerService.getUserById(Long.MAX_VALUE));
        verify(userDao).getById(Long.MAX_VALUE);
        verify(userDao,
               times(2)).getById(anyLong());
        assertNull(userDataStore.getById(0));
        assertNull(userManagerService.getUserById(0));
        verify(userDao).getById(0);
        verify(userDao,
               times(3)).getById(anyLong());
        // existing id
        User goodUser = userDataStore.getAll().iterator().next();
        assertSame(goodUser,
                   userManagerService.getUserById(goodUser.getId()));
        verify(userDao).getById(goodUser.getId());
        verify(userDao,
               times(4)).getById(anyLong());
    }
    /**
     * Tests {@link UserManagerServiceImpl#getAllUsers()}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetAllUsers()
            throws Exception
    {
        List<User> actualAllUsers = userManagerService.getAllUsers();
        verifyUsers(userDataStore.getAll(),
                    actualAllUsers);
        verify(userDao).getAll();
    }
    /**
     * Verifies that the given actual value matches the given expected value.
     *
     * @param inExpectedUsers a <code>Collection&lt;User&gt;</code> value
     * @param inActualUsers a <code>Collection&lt;User&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyUsers(Collection<User> inExpectedUsers,
                            Collection<User> inActualUsers)
            throws Exception
    {
        assertEquals(inExpectedUsers.size(),
                     inActualUsers.size());
        Map<Long,User> expectedUsers = new HashMap<Long,User>();
        Map<Long,User> actualUsers = new HashMap<Long,User>();
        for(User user : inExpectedUsers) {
            expectedUsers.put(user.getId(),
                              user);
        }
        for(User user : inActualUsers) {
            actualUsers.put(user.getId(),
                            user);
        }
        assertEquals(expectedUsers,
                     actualUsers);
    }
    /**
     * test user manager service object
     */
    private UserManagerServiceImpl userManagerService;
}
