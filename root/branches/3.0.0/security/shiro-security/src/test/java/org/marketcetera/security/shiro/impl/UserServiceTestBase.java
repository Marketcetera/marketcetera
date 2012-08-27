package org.marketcetera.security.shiro.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.BeforeClass;
import org.marketcetera.api.dao.UserDao;
import org.marketcetera.api.security.User;
import org.marketcetera.core.LoggerConfiguration;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/* $License$ */

/**
 * Provides common test facilities for user service tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class UserServiceTestBase
{
    /**
     * Run once before all tests. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
    }
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
     * Generates a test code>User</code> value.
     *
     * @return a <code>User</code> value
     */
    static User generateUser()
    {
        MockUser user = new MockUser("User-" + counter.incrementAndGet(),
                                     "password");
        user.setId(counter.incrementAndGet());
        return user;
    }
    /**
     * Adds the given user to the test user store.
     *
     * @param inUser a <code>User</code> value
     */
    protected void addUser(User inUser)
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
    protected void removeUser(User inUser)
    {
        usersByName.remove(inUser.getName());
        usersById.remove(inUser.getId());
    }
    /**
     * test user DAO object
     */
    protected UserDao userDao;
    /**
     * user manager service test value
     */
    protected UserManagerServiceImpl userManagerService;
    /**
     * test user values by username
     */
    protected Map<String,User> usersByName;
    /**
     * test user values by id
     */
    protected Map<Long,User> usersById;
    /**
     * counter used to guarantee unique test values
     */
    protected static AtomicLong counter = new AtomicLong(0);
}
