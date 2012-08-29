package org.marketcetera.webservices.security.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.util.*;

import javax.ws.rs.core.Response;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.provider.json.JSONProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.api.security.User;
import org.marketcetera.api.security.UserManagerService;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.systemmodel.UserFactory;
import org.marketcetera.security.shiro.impl.MockUser;
import org.marketcetera.security.shiro.impl.UserServiceTestBase;
import org.marketcetera.webservices.security.UserService;
import org.marketcetera.webservices.security.WebServicesUser;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/* $License$ */

/**
 * Tests {@link UserServiceImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: UserServiceImplTest.java 16218 2012-08-27 23:23:59Z colin $
 * @since $Release$
 */
public class UserServiceImplTest
        extends UserServiceTestBase
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
        userManagerService = mock(UserManagerService.class);
        userFactory = mock(UserFactory.class);
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
        testUserService = new UserServiceImpl();
        testUserService.setUserManagerService(userManagerService);
        testUserService.setUserFactory(userFactory);
        startServer();
        service = JAXRSClientFactory.create(ENDPOINT_ADDRESS,
                                            UserService.class);
    }
    /**
     * Run after each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @After
    public void cleanup()
            throws Exception
    {
        if(server != null) {
            server.stop();
            server.destroy();
        }
    }
    /**
     * Tests {@link UserServiceImpl#addUser(org.marketcetera.dao.impl.User)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAddUser()
            throws Exception
    {
        final MockUser newUser = generateUser();
        assertNotNull(newUser.getUsername());
        assertNotNull(newUser.getPassword());
        // null username & password
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
        Response response = service.addUser(newUser.getUsername(),
                                            newUser.getPassword());
        assertEquals(Response.Status.OK.getStatusCode(),
                     response.getStatus());
        verify(userManagerService).addUser((User)any());
        // add user throws an exception
        doThrow(new RuntimeException("This exception is expected")).when(userManagerService).addUser((User)any());
        response = service.addUser(newUser.getUsername(),
                                   newUser.getPassword());
        assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(),
                     response.getStatus());
        verify(userManagerService,
               times(2)).addUser((User)any());
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
        verify(userManagerService).getUserById(anyLong());
        // good result
        MockUser newUser = generateUser();
        when(userManagerService.getUserById(anyLong())).thenReturn(newUser);
        verifyUser(newUser,
                   service.getUser(newUser.getId()));
        verify(userManagerService,
               times(2)).getUserById(anyLong());
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
        verify(userManagerService).deleteUser((User)any());
        // add user throws an exception
        doThrow(new RuntimeException("This exception is expected")).when(userManagerService).deleteUser((User)any());
        response = service.deleteUser(2);
        assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(),
                     response.getStatus());
        verify(userManagerService,
               times(2)).deleteUser((User)any());
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
        when(userManagerService.getAllUsers()).thenReturn(new ArrayList<User>());
        assertTrue(service.getUsers().isEmpty());
        verify(userManagerService).getAllUsers();
        // single result
        User user1 = generateUser();
        when(userManagerService.getAllUsers()).thenReturn(Arrays.asList(new User[] { user1 }));
        List<User> expectedResults = new ArrayList<User>();
        expectedResults.add(user1);
        verifyUsers(expectedResults,
                    service.getUsers());
        verify(userManagerService,
               times(2)).getAllUsers();
        // multiple results
        User user2 = generateUser();
        when(userManagerService.getAllUsers()).thenReturn(Arrays.asList(new User[] { user1, user2 }));
        expectedResults.add(user2);
        verifyUsers(expectedResults,
                    service.getUsers());
        verify(userManagerService,
               times(3)).getAllUsers();
    }
    /**
     * Starts the test server. 
     */
    private void startServer()
    {
        JAXRSServerFactoryBean serverFactory = new JAXRSServerFactoryBean();
        serverFactory.setResourceClasses(UserService.class);
        List<Object> providers = new ArrayList<Object>();
        providers.add(new JSONProvider<User>());
        serverFactory.setProviders(providers);
        serverFactory.setResourceProvider(UserService.class,
                                          new SingletonResourceProvider(testUserService,
                                                                        true));
        serverFactory.setAddress(ENDPOINT_ADDRESS);
        Map<Object,Object> mappings = new HashMap<Object,Object>();
        mappings.put("json",
                     "application/json");
        serverFactory.setExtensionMappings(mappings);
        server = serverFactory.create();
    }
    /**
     * Verifies that the given expected value matches the given actual value.
     *
     * @param inExpectedUser a <code>User</code> value
     * @param inActualUser a <code>WebServicesUser</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyUser(User inExpectedUser,
                            WebServicesUser inActualUser)
            throws Exception
    {
        assertEquals(inExpectedUser.getName(),
                     inActualUser.getUsername());
        assertEquals(inExpectedUser.getId(),
                     inActualUser.getId());
    }
    /**
     * Verifies that the given expected user values match the given actual user values.
     *
     * @param inExpectedUsers a <code>Collection&lt;User&gt;</code> value
     * @param inActualUsers a <code>Collection&lt;WebServicesUser&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyUsers(Collection<User> inExpectedUsers,
                             Collection<WebServicesUser> inActualUsers)
            throws Exception
    {
        assertEquals(inExpectedUsers.size(),
                     inActualUsers.size());
        Iterator<WebServicesUser> actualIterator = inActualUsers.iterator();
        for(User expectedUser : inExpectedUsers) {
            verifyUser(expectedUser,
                       actualIterator.next());
        }
    }
    /**
     * address of service
     */
    private final static String ENDPOINT_ADDRESS = "http://localhost:9010/";
//    private final static String ENDPOINT_ADDRESS = "local://userservice/";
    /**
     * server test object
     */
    private Server server;
    /**
     * test user service
     */
    private UserServiceImpl testUserService;
    /**
     * test user manager service
     */
    private UserManagerService userManagerService;
    /**
     * test client service
     */
    private UserService service;
    /**
     * test user factory object
     */
    private UserFactory userFactory;
}
