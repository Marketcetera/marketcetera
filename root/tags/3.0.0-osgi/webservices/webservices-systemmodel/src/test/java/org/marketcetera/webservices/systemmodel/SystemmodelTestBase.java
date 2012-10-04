package org.marketcetera.webservices.systemmodel;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.BeforeClass;
import org.marketcetera.api.dao.*;
import org.marketcetera.api.security.User;
import org.marketcetera.core.LoggerConfiguration;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/* $License$ */

/**
 * Provides common test services for systemmodel objects.
 *
 * @version $Id: SystemmodelTestBase.java 16254 2012-09-04 23:19:20Z colin $
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
        initializePermissions();
        initializeRoles();
        initializeUsers();
    }
    /**
     * Initializes the permissions objects.
     *
     * @throws Exception if an unexpected error occurs
     */
    protected void initializePermissions()
            throws Exception
    {
        permissionDao = mock(PermissionDao.class);
        permissionDataStore = new NamedTestDataStore<MutablePermission>();
        for(int i=0;i<=3;i++) {
            permissionDataStore.add(generatePermission());
        }
        when(permissionDao.getByName(anyString())).thenAnswer(new Answer<Permission>() {
            @Override
            public Permission answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                return permissionDataStore.getByName((String)inInvocation.getArguments()[0]);
            }
        });
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                MutablePermission permission = (MutablePermission)inInvocation.getArguments()[0];
                permissionDataStore.add(permission);
                return null;
            }
        }).when(permissionDao).save((Permission)any());
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                MutablePermission permission = (MutablePermission)inInvocation.getArguments()[0];
                permissionDataStore.remove(permission);
                return null;
            }
        }).when(permissionDao).delete((Permission)any());
        when(permissionDao.getByName(anyString())).thenAnswer(new Answer<Permission>() {
            @Override
            public Permission answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                return permissionDataStore.getByName((String)inInvocation.getArguments()[0]);
            }
        });
        when(permissionDao.getById(anyLong())).thenAnswer(new Answer<Permission>() {
            @Override
            public Permission answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                return permissionDataStore.getById((Long)inInvocation.getArguments()[0]);
            }
        });
        when(permissionDao.getAll()).thenReturn(new ArrayList<MutablePermission>(permissionDataStore.getAll()));
    }
    /**
     * Initializes the roles objects. 
     *
     * @throws Exception if an unexpected error occurs
     */
    protected void initializeRoles()
            throws Exception
    {
        roleDao = mock(RoleDao.class);
        roleDataStore = new NamedTestDataStore<MutableRole>();
        for(int i=0;i<=3;i++) {
            roleDataStore.add(generateRole());
        }
        when(roleDao.getByName(anyString())).thenAnswer(new Answer<Role>() {
            @Override
            public Role answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                return roleDataStore.getByName((String)inInvocation.getArguments()[0]);
            }
        });
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                MutableRole role = (MutableRole)inInvocation.getArguments()[0];
                roleDataStore.add(role);
                return null;
            }
        }).when(roleDao).save((Role)any());
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                MutableRole role = (MutableRole)inInvocation.getArguments()[0];
                roleDataStore.remove(role);
                return null;
            }
        }).when(roleDao).delete((Role)any());
        when(roleDao.getByName(anyString())).thenAnswer(new Answer<Role>() {
            @Override
            public Role answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                return roleDataStore.getByName((String)inInvocation.getArguments()[0]);
            }
        });
        when(roleDao.getById(anyLong())).thenAnswer(new Answer<Role>() {
            @Override
            public Role answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                return roleDataStore.getById((Long)inInvocation.getArguments()[0]);
            }
        });
        when(roleDao.getAll()).thenReturn(new ArrayList<MutableRole>(roleDataStore.getAll()));
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
        userDataStore = new NamedTestDataStore<MutableUser>();
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
                MutableUser user = (MutableUser)inInvocation.getArguments()[0];
                userDataStore.add(user);
                return null;
            }
        }).when(userDao).save((User)any());
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                MutableUser user = (MutableUser)inInvocation.getArguments()[0];
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
        when(userDao.getAll()).thenReturn(new ArrayList<MutableUser>(userDataStore.getAll()));
    }
    /**
     * Generates a unique <code>WebServicesPermission</code> value.
     *
     * @return a <code>WebServicesPermission</code> value
     */
    protected WebServicesPermission generatePermission()
    {
        WebServicesPermission permission = new WebServicesPermission();
        permission.setName("something:something else-" + counter.incrementAndGet());
        permission.setDescription("description of " +permission.getName());
        permission.setId(1);
        permission.setMethod(EnumSet.of(PermissionAttribute.Create,PermissionAttribute.Delete));
        return permission;
    }
    /**
     * Generates a unique <code>WebServicesRole</code> value.
     *
     * @return a <code>WebServicesRole</code> value
     */
    protected WebServicesRole generateRole()
    {
        WebServicesRole role = new WebServicesRole();
        role.setName("role-" + counter.incrementAndGet());
        role.setDescription("description-" + counter.incrementAndGet());
        role.setId(counter.incrementAndGet());
        role.getUsers().add(generateUser());
        role.getUsers().add(generateUser());
        role.getUsers().add(generateUser());
        role.getPermissions().add(generatePermission());
        role.getPermissions().add(generatePermission());
        role.getPermissions().add(generatePermission());
        return role;
    }
    /**
     * Generates a test <code>User</code> value.
     *
     * @return a <code>WebServicesUser</code> value
     */
    protected WebServicesUser generateUser()
    {
        WebServicesUser user = new WebServicesUser();
        user.setUsername("username-" + counter.incrementAndGet());
        user.setDescription("description-" + counter.incrementAndGet());
        user.setPassword("password-" + counter.incrementAndGet());
        user.setId(counter.incrementAndGet());
        user.setIsAccountNonExpired(true);
        user.setIsAccountNonLocked(true);
        user.setIsCredentialsNonExpired(true);
        user.setIsEnabled(true);
        return user;
    }
    /**
     * permission DAO value
     */
    protected PermissionDao permissionDao;
    /**
     * user DAO value
     */
    protected UserDao userDao;
    /**
     * role DAO value
     */
    protected RoleDao roleDao;
    /**
     * data store for user test values 
     */
    protected NamedTestDataStore<MutableUser> userDataStore;
    /**
     * data store for role test values 
     */
    protected NamedTestDataStore<MutableRole> roleDataStore;
    /**
     * data store for permission test values
     */
    protected NamedTestDataStore<MutablePermission> permissionDataStore;
    /**
     * counter used to guarantee unique test values
     */
    protected static AtomicLong counter = new AtomicLong(0);
}
