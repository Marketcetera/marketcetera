package org.marketcetera.security.shiro.impl;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.api.security.User;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.api.dao.UserDao;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/* $License$ */

/**
 * Tests {@link UserManagerServiceImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class UserManagerServiceImplTest
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
        userDao = mock(UserDao.class);
        userManagerService = new UserManagerServiceImpl();
        userManagerService.setUserDao(userDao);
        usersByName = new HashMap<String,User>();
        usersById = new HashMap<Long,User>();
        // set up test users
        for(int i=0;i<=3;i++) {
            addUser(generateUser());
        }
        when(userDao.getByName(anyString())).thenAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                return usersByName.get((String)inInvocation.getArguments()[0]);
            }
        });
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                User user = (User)inInvocation.getArguments()[0];
                addUser(user);
                return null;
            }
        }).when(userDao).save((User)any());
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                User user = (User)inInvocation.getArguments()[0];
                removeUser(user);
                return null;
            }
        }).when(userDao).delete((User)any());
        when(userDao.getByName(anyString())).thenAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                return usersByName.get((String)inInvocation.getArguments()[0]);
            }
        });
        when(userDao.getById(anyLong())).thenAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                return usersById.get((Long)inInvocation.getArguments()[0]);
            }
        });
        when(userDao.getAll()).thenReturn(new ArrayList<User>(usersByName.values()));
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
        assertFalse(usersByName.containsKey(badUser.getName()));
        assertEquals(null,
                     userManagerService.getUserByName(badUser.getName()));
        verify(userDao).getByName(badUser.getName());
        // good match
        User goodUser = usersByName.values().iterator().next();
        assertNotNull(goodUser);
        assertSame(goodUser,
                   userManagerService.getUserByName(goodUser.getName()));
        verify(userDao).getByName(goodUser.getName());
        verify(userDao,
               times(2)).getByName(anyString());
    }
    /**
     * Tests {@link UserManagerServiceImpl#addUser(org.marketcetera.api.security.User)}. 
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
        assertFalse(usersByName.containsKey(newUser.getName()));
        userManagerService.saveUser(newUser);
        verify(userDao).save(newUser);
        assertSame(newUser,
                   usersByName.get(newUser.getName()));
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
        assertFalse(usersByName.containsKey(newUser.getName()));
        userManagerService.deleteUser(newUser);
        verify(userDao).delete(newUser);
        assertFalse(usersByName.containsKey(newUser.getName()));
        // delete valid user
        User goodUser = usersByName.values().iterator().next();
        assertTrue(usersByName.containsKey(goodUser.getName()));
        userManagerService.deleteUser(goodUser);
        verify(userDao,
               times(2)).delete((User)any());
        assertFalse(usersByName.containsKey(goodUser.getName()));
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
        assertNull(usersById.get(Long.MIN_VALUE));
        assertNull(userManagerService.getUserById(Long.MIN_VALUE));
        verify(userDao).getById(Long.MIN_VALUE);
        verify(userDao,
               times(1)).getById(anyLong());
        assertNull(usersById.get(Long.MAX_VALUE));
        assertNull(userManagerService.getUserById(Long.MAX_VALUE));
        verify(userDao).getById(Long.MAX_VALUE);
        verify(userDao,
               times(2)).getById(anyLong());
        assertNull(usersById.get(0));
        assertNull(userManagerService.getUserById(0));
        verify(userDao).getById(0);
        verify(userDao,
               times(3)).getById(anyLong());
        // existing id
        User goodUser = usersByName.values().iterator().next();
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
        Map<Long,User> actualUsersById = new HashMap<Long,User>();
        for(User user : actualAllUsers) {
            actualUsersById.put(user.getId(),
                                user);
        }
        assertEquals(usersById,
                     actualUsersById);
        verify(userDao).getAll();
    }
    /**
     * Generates a test code>User</code> value.
     *
     * @return a <code>User</code> value
     */
    private static User generateUser()
    {
        User user = mock(User.class);
        when(user.getId()).thenReturn(counter.incrementAndGet());
        when(user.getName()).thenReturn("User-" + counter.incrementAndGet());
        when(user.getPassword()).thenReturn("password");
        when(user.getVersion()).thenReturn(1);
        return user;
    }
    /**
     * Adds the given user to the test user store.
     *
     * @param inUser a <code>User</code> value
     */
    private void addUser(User inUser)
    {
        usersByName.put(inUser.getName(),
                        inUser);
        usersById.put(inUser.getId(),
                      inUser);
    }
    /**
     * Removes the given user from the test user store.
     *
     * @param inUser a <code>User</code> value
     */
    private void removeUser(User inUser)
    {
        usersByName.remove(inUser.getName());
        usersById.remove(inUser.getId());
    }
    /**
     * test user DAO object
     */
    private UserDao userDao;
    /**
     * user manager service test value
     */
    private UserManagerServiceImpl userManagerService;
    /**
     * test user values by username
     */
    private Map<String,User> usersByName;
    /**
     * test user values by id
     */
    private Map<Long,User> usersById;
    /**
     * counter used to guarantee unique test values
     */
    private static AtomicLong counter = new AtomicLong(0);
}
