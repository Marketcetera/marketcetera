package org.marketcetera.webservices.systemmodel.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.ServerWebApplicationException;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.api.dao.MutableUser;
import org.marketcetera.api.dao.UserDao;
import org.marketcetera.api.dao.UserFactory;
import org.marketcetera.api.security.User;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.webservices.WebServicesTestBase;
import org.marketcetera.webservices.systemmodel.UserService;
import org.marketcetera.webservices.systemmodel.WebServicesUser;
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
        userDao = mock(UserDao.class);
        userFactory = mock(UserFactory.class);
        serviceImplementation.setUserFactory(userFactory);
        serviceImplementation.setUserDao(userDao);
        when(userFactory.create()).thenReturn(new WebServicesUser());
        when(userFactory.create((User)any())).thenAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                User user = (User)inInvocation.getArguments()[0];
                WebServicesUser newUser = new WebServicesUser(user);
                return newUser;
            }
        });
        super.setup();
    }
    @Test
    public void testMarshalling()
            throws Exception
    {
        WebServicesUser user = generateUser();
        String marshalledValue = JsonMarshallingProvider.getInstance().getService().marshal(user);
        SLF4JLoggerProxy.debug(this,
                               "Marshalled value is {}",
                               marshalledValue);
        WebServicesUser newUser = JsonMarshallingProvider.getInstance().getService().unmarshal(marshalledValue,
                                                                                               WebServicesUser.class);
        SLF4JLoggerProxy.debug(this,
                               "Unmarshalled value is {}",
                               newUser);
    }
    /**
     * Tests {@link org.marketcetera.webservices.systemmodel.UserService#addUserJSON(org.marketcetera.api.dao.User)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAddUser()
            throws Exception
    {
        final WebServicesUser newUser = generateUser();
        assertNotNull(newUser.getName());
        // null user name
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                service.addUserJSON(null);
            }
        };
        final long newId = counter.incrementAndGet();
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
//                user.setId(newId);
                return null;
            }
        }).when(userDao).add((MutableUser)any());
        // successful add
        newUser.setId(counter.incrementAndGet());
        long existingId = newUser.getId();
        assertFalse(newId == existingId);
        WebServicesUser response = service.addUserJSON(newUser);
//        assertEquals(newId,
//                     response.getId());
        verify(userDao).add((User)any());
        // add user throws an exception
        doThrow(new RuntimeException("This exception is expected")).when(userDao).add((User) any());
        new ExpectedFailure<RuntimeException>() {
            @Override
            protected void run()
                    throws Exception
            {
                service.addUserJSON(newUser);
            }
        };
        verify(userDao,
               times(2)).add((User) any());
    }
    /**
     * Tests {@link UserServiceImpl#getUserJSON(long)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetUser()
            throws Exception
    {
        // no result
        new ExpectedFailure<ServerWebApplicationException>() {
            @Override
            protected void run()
                    throws Exception
            {
                service.getUserJSON(-1);
            }
        };
        verify(userDao).getById(anyLong());
        // good result
        WebServicesUser newUser = generateUser();
        when(userDao.getById(newUser.getId())).thenReturn(newUser);
        verifyUser(newUser,
                        service.getUserJSON(newUser.getId()));
        verify(userDao,
               times(2)).getById(anyLong());
    }
    /**
     * Tests {@link UserServiceImpl#deleteUser(long)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testdeleteUser()
            throws Exception
    {
        // successful delete
        Response response = service.deleteUser(1);
        assertEquals(Response.Status.OK.getStatusCode(),
                     response.getStatus());
        verify(userDao).delete((User) any());
        // add user throws an exception
        doThrow(new RuntimeException("This exception is expected")).when(userDao).delete((User) any());
        response = service.deleteUser(2);
        assertEquals(Response.serverError().build().getStatus(),
                     response.getStatus());
        verify(userDao,
               times(2)).delete((User) any());
    }
    /**
     * Tests {@link UserServiceImpl#getUsersJSON()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testgetUsers()
            throws Exception
    {
        // no results
        when(userDao.getAll()).thenReturn(new ArrayList<MutableUser>());
        assertTrue(service.getUsersJSON().isEmpty());
        verify(userDao).getAll();
        // single result
        WebServicesUser user1 = generateUser();
        when(userDao.getAll()).thenReturn(Arrays.asList(new MutableUser[] {user1}));
        List<User> expectedResults = new ArrayList<User>();
        expectedResults.add(user1);
        verifyUsers(expectedResults,
                          service.getUsersJSON());
        verify(userDao,
               times(2)).getAll();
        // multiple results
        WebServicesUser user2 = generateUser();
        when(userDao.getAll()).thenReturn(Arrays.asList(new MutableUser[]{user1, user2}));
        expectedResults.add(user2);
        verifyUsers(expectedResults,
                          service.getUsersJSON());
        verify(userDao,
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
     * test user service implementation
     */
    private UserServiceImpl serviceImplementation;
    /**
     * test user manager service value
     */
    private UserDao userDao;
    /**
     * test user factory value
     */
    private UserFactory userFactory;
}
