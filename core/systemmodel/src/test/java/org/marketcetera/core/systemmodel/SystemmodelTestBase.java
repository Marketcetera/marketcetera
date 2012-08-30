package org.marketcetera.core.systemmodel;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.*;
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
 * Provides common test services for systemmodel objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SystemmodelTestBase
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
     * Run once before each test. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        initializeAuthorities();
        initializeGroups();
        initializeUsers();
    }
    /**
     * Initializes the authorities objects. 
     *
     * @throws Exception if an unexpected error occurs
     */
    protected void initializeAuthorities()
            throws Exception
    {
        authorityDao = mock(AuthorityDao.class);
        authorityDataStore = new TestDataStore<Authority>();
        for(int i=0;i<=3;i++) {
            authorityDataStore.add(generateAuthority());
        }
        when(authorityDao.getByName(anyString())).thenAnswer(new Answer<Authority>() {
            @Override
            public Authority answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                return authorityDataStore.getByName((String)inInvocation.getArguments()[0]);
            }
        });
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                Authority authority = (Authority)inInvocation.getArguments()[0];
                authorityDataStore.add(authority);
                return null;
            }
        }).when(authorityDao).save((Authority)any());
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                Authority authority = (Authority)inInvocation.getArguments()[0];
                authorityDataStore.remove(authority);
                return null;
            }
        }).when(authorityDao).delete((Authority)any());
        when(authorityDao.getByName(anyString())).thenAnswer(new Answer<Authority>() {
            @Override
            public Authority answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                return authorityDataStore.getByName((String)inInvocation.getArguments()[0]);
            }
        });
        when(authorityDao.getById(anyLong())).thenAnswer(new Answer<Authority>() {
            @Override
            public Authority answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                return authorityDataStore.getById((Long)inInvocation.getArguments()[0]);
            }
        });
        when(authorityDao.getAll()).thenReturn(new ArrayList<Authority>(authorityDataStore.getAll()));
    }
    /**
     * Initializes the groups objects. 
     *
     * @throws Exception if an unexpected error occurs
     */
    protected void initializeGroups()
            throws Exception
    {
        groupDao = mock(GroupDao.class);
        groupDataStore = new TestDataStore<Group>();
        for(int i=0;i<=3;i++) {
            groupDataStore.add(generateGroup());
        }
        when(groupDao.getByName(anyString())).thenAnswer(new Answer<Group>() {
            @Override
            public Group answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                return groupDataStore.getByName((String)inInvocation.getArguments()[0]);
            }
        });
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                Group group = (Group)inInvocation.getArguments()[0];
                groupDataStore.add(group);
                return null;
            }
        }).when(groupDao).save((Group)any());
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                Group group = (Group)inInvocation.getArguments()[0];
                groupDataStore.remove(group);
                return null;
            }
        }).when(groupDao).delete((Group)any());
        when(groupDao.getByName(anyString())).thenAnswer(new Answer<Group>() {
            @Override
            public Group answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                return groupDataStore.getByName((String)inInvocation.getArguments()[0]);
            }
        });
        when(groupDao.getById(anyLong())).thenAnswer(new Answer<Group>() {
            @Override
            public Group answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                return groupDataStore.getById((Long)inInvocation.getArguments()[0]);
            }
        });
        when(groupDao.getAll()).thenReturn(new ArrayList<Group>(groupDataStore.getAll()));
    }
    /**
     * Initializes the user objects. 
     *
     * @throws Exception if an unexpected error occurs
     */
    protected void initializeUsers()
            throws Exception
    {
        userDao = mock(UserDao.class);
        userDataStore = new TestDataStore<User>();
        for(int i=0;i<=3;i++) {
            userDataStore.add(generateUser());
        }
        when(userDao.getByName(anyString())).thenAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                return userDataStore.getByName((String)inInvocation.getArguments()[0]);
            }
        });
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                User user = (User)inInvocation.getArguments()[0];
                userDataStore.add(user);
                return null;
            }
        }).when(userDao).save((User)any());
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                User user = (User)inInvocation.getArguments()[0];
                userDataStore.remove(user);
                return null;
            }
        }).when(userDao).delete((User)any());
        when(userDao.getByName(anyString())).thenAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                return userDataStore.getByName((String)inInvocation.getArguments()[0]);
            }
        });
        when(userDao.getById(anyLong())).thenAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                return userDataStore.getById((Long)inInvocation.getArguments()[0]);
            }
        });
        when(userDao.getAll()).thenReturn(new ArrayList<User>(userDataStore.getAll()));
    }
    /**
     * Generates a unique <code>MockAuthority</code> value.
     *
     * @return a <code>MockAuthority</code> value
     */
    protected MockAuthority generateAuthority()
    {
        MockAuthority authority = new MockAuthority();
        authority.setAuthority("authority-" + counter.incrementAndGet());
        authority.setId(counter.incrementAndGet());
        return authority;
    }
    /**
     * Generates a unique <code>MockGroup</code> value.
     *
     * @return a <code>MockGroup</code> value
     */
    protected MockGroup generateGroup()
    {
        MockGroup group = new MockGroup();
        group.setName("group-" + counter.incrementAndGet());
        group.setId(counter.incrementAndGet());
        return group;
    }
    /**
     * Generates a test <code>User</code> value.
     *
     * @return a <code>MockUser</code> value
     */
    protected MockUser generateUser()
    {
        MockUser user = new MockUser();
        user.setUsername("username-" + counter.incrementAndGet());
        user.setPassword("password-" + counter.incrementAndGet());
        user.setId(counter.incrementAndGet());
        user.setAuthorities(Arrays.asList(new Authority[] { generateAuthority(), generateAuthority(), generateAuthority() }));
        return user;
    }
    /**
     * authority DAO value
     */
    protected AuthorityDao authorityDao;
    /**
     * user DAO value
     */
    protected UserDao userDao;
    /**
     * group DAO value
     */
    protected GroupDao groupDao;
    /**
     * data store for user test values 
     */
    protected TestDataStore<User> userDataStore;
    /**
     * data store for group test values 
     */
    protected TestDataStore<Group> groupDataStore;
    /**
     * data store for authority test values 
     */
    protected TestDataStore<Authority> authorityDataStore;
    /**
     * counter used to guarantee unique test values
     */
    protected static AtomicLong counter = new AtomicLong(0);
}
