package org.marketcetera.webservices.systemmodel.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.api.dao.MutableUser;
import org.marketcetera.api.dao.UserDao;
import org.marketcetera.api.dao.UserFactory;
import org.marketcetera.api.security.User;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.webservices.WebServicesTestBase;
import org.marketcetera.webservices.systemmodel.MockUser;
import org.marketcetera.webservices.systemmodel.UserService;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/* $License$ */

/**
 * Tests {@link UserServiceImpl}.
 *
 * @version $Id$
 * @since $Release$
 */
public class UserServiceImplTest
        extends WebServicesTestBase<UserService,UserServiceImpl>
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
        serviceImplementation = new UserServiceImpl();
        userManagerService = mock(UserDao.class);
        userFactory = mock(UserFactory.class);
        serviceImplementation.setUserFactory(userFactory);
        serviceImplementation.setUserDao(userManagerService);
        when(userFactory.create()).thenReturn(new MockUser());
        when(userFactory.create(anyString(),
                                anyString())).thenAnswer(new Answer<User>() {
                                         @Override
                                         public User answer(InvocationOnMock inInvocation)
                                                     throws Throwable
                                         {
                                             MockUser user = new MockUser();
                                             user.setUsername((String)inInvocation.getArguments()[0]);
                                             user.setPassword((String)inInvocation.getArguments()[1]);
                                             user.setId(System.nanoTime());
                                             return user;
                                         }
                                     });
        super.setup();
    }
    /**
     * Tests {@link UserServiceImpl#addUser(String, String)}
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAddUser()
            throws Exception
    {
        final MockUser newUser = generateUser();
        assertNotNull(newUser.getName());
        // null name
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                service.addUser(null,
                                newUser.getPassword());
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                service.addUser(newUser.getUsername(),
                                null);
            }
        };
        // successful add
        Response response = service.addUser(newUser.getName(),
                                            newUser.getPassword());
        assertEquals(Response.Status.OK.getStatusCode(),
                     response.getStatus());
        verify(userManagerService).add((User) any());
        // add user throws an exception
        doThrow(new RuntimeException("This exception is expected")).when(userManagerService).add((User) any());
        response = service.addUser(newUser.getName(),
                                   newUser.getPassword());
        assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(),
                     response.getStatus());
        verify(userManagerService,
               times(2)).add((User) any());
    }
    /**
     * Tests {@link UserServiceImpl#getUser(long)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetUser()
            throws Exception
    {
        // no result
        assertNull(service.getUser(-1));
        verify(userManagerService).getById(anyLong());
        // good result
        MockUser newUser = generateUser();
        when(userManagerService.getById(newUser.getId())).thenReturn(newUser);
        verifyUser(newUser,
                    service.getUser(newUser.getId()));
        verify(userManagerService,
               times(2)).getById(anyLong());
    }
    /**
     * Tests {@link UserServiceImpl#deleteUser(long)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDeleteUser()
            throws Exception
    {
        // successful delete
        Response response = service.deleteUser(1);
        assertEquals(Response.Status.OK.getStatusCode(),
                     response.getStatus());
        verify(userManagerService).delete((User) any());
        // add user throws an exception
        doThrow(new RuntimeException("This exception is expected")).when(userManagerService).delete((User) any());
        response = service.deleteUser(2);
        assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(),
                     response.getStatus());
        verify(userManagerService,
               times(2)).delete((User) any());
    }
    /**
     * Tests {@link UserServiceImpl#getUsers()}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetUsers()
            throws Exception
    {
        // no results
        when(userManagerService.getAll()).thenReturn(new ArrayList<MutableUser>());
        assertTrue(service.getUsers().isEmpty());
        verify(userManagerService).getAll();
        // single result
        MutableUser user1 = generateUser();
        when(userManagerService.getAll()).thenReturn(Arrays.asList(new MutableUser[] { user1 }));
        List<User> expectedResults = new ArrayList<User>();
        expectedResults.add(user1);
        verifyUsers(expectedResults,
                     service.getUsers());
        verify(userManagerService,
               times(2)).getAll();
        // multiple results
        MutableUser user2 = generateUser();
        when(userManagerService.getAll()).thenReturn(Arrays.asList(new MutableUser[]{user1, user2}));
        expectedResults.add(user2);
        verifyUsers(expectedResults,
                          service.getUsers());
        verify(userManagerService,
               times(3)).getAll();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.WebServicesTestBase#getServiceInterface()
     */
    @Override
    protected Class<UserService> getServiceInterface()
    {
        return UserService.class;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.WebServicesTestBase#getServiceImplementation()
     */
    @Override
    protected UserServiceImpl getServiceImplementation()
    {
        return serviceImplementation;
    }
    /**
     * user service implementation test value
     */
    private UserServiceImpl serviceImplementation;
    /**
     * user manager service test value
     */
    private UserDao userManagerService;
    /**
     * user factory test value
     */
    private UserFactory userFactory;
}
