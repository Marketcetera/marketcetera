package org.marketcetera.security.shiro.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
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
import org.marketcetera.dao.impl.PersistentUser;
import org.marketcetera.security.shiro.UserService;

/* $License$ */

/**
 * Tests {@link UserServiceImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
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
        testUserService = new UserServiceImpl();
        testUserService.setUserManagerService(userManagerService);
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
     * Tests {@link UserServiceImpl#addUser(org.marketcetera.dao.impl.PersistentUser)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAddUser()
            throws Exception
    {
        // null user
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                service.addUser(null);
            }
        };
        // successful add
        MockUser newUser = generateUser();
        Response response = service.addUser(newUser);
        assertEquals(Response.Status.OK.getStatusCode(),
                     response.getStatus());
        verify(userManagerService).addUser((User)any());
        // add user throws an exception
        doThrow(new RuntimeException("This exception is expected")).when(userManagerService).addUser((User)any());
        response = service.addUser(newUser);
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
        assertEquals(newUser,
                     service.getUser(newUser.getId()));
        verify(userManagerService,
               times(2)).getUserById(anyLong());
    }
    /**
     * Tests {@link UserServiceImpl#updateUser(org.marketcetera.dao.impl.PersistentUser)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testUpdateUser()
            throws Exception
    {
        // null user
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                service.updateUser(null);
            }
        };
        // successful add
        MockUser newUser = generateUser();
        Response response = service.updateUser(newUser);
        assertEquals(Response.Status.OK.getStatusCode(),
                     response.getStatus());
        verify(userManagerService).saveUser((User)any());
        // add user throws an exception
        doThrow(new RuntimeException("This exception is expected")).when(userManagerService).saveUser((User)any());
        response = service.updateUser(newUser);
        assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(),
                     response.getStatus());
        verify(userManagerService,
               times(2)).saveUser((User)any());
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
        PersistentUser user1 = generateUser();
        when(userManagerService.getAllUsers()).thenReturn(Arrays.asList(new User[] { user1 }));
        List<PersistentUser> expectedResults = new ArrayList<PersistentUser>();
        expectedResults.add(user1);
        assertEquals(expectedResults,
                     service.getUsers());
        verify(userManagerService,
               times(2)).getAllUsers();
        // multiple results
        PersistentUser user2 = generateUser();
        when(userManagerService.getAllUsers()).thenReturn(Arrays.asList(new User[] { user1, user2 }));
        expectedResults.add(user2);
        assertEquals(expectedResults,
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
}
