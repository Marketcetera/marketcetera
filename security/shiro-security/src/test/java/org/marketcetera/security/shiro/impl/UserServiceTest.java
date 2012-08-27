package org.marketcetera.security.shiro.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.provider.json.JSONProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.api.security.User;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.security.shiro.UserService;

/* $License$ */

/**
 * Tests {@link UserServiceImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class UserServiceTest
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
        testUserService = new UserServiceImpl();
        testUserService.setUserManagerService(userManagerService);
        startRestServer();
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
     * 
     *
     *
     * @throws Exception
     */
    @Test
    public void testOne()
            throws Exception
    {
        UserService service = JAXRSClientFactory.create(ENDPOINT_ADDRESS,
                                                        UserService.class);
        SLF4JLoggerProxy.debug(this,
                               String.valueOf(service.getUsers()));
        MockUser newUser = new MockUser("username-" + System.nanoTime(),
                                        "password-" + System.nanoTime());
        newUser.setId(1);
        SLF4JLoggerProxy.debug(this,
                               String.valueOf(service.addUser(newUser)));
        SLF4JLoggerProxy.debug(this,
                               String.valueOf(service.getUsers()));
        SLF4JLoggerProxy.debug(this,
                               String.valueOf(service.getUser(1)));
        newUser.setUsername("newusername");
        SLF4JLoggerProxy.debug(this,
                               String.valueOf(service.updateUser(newUser)));
        SLF4JLoggerProxy.debug(this,
                               String.valueOf(service.getUsers()));
        SLF4JLoggerProxy.debug(this,
                               String.valueOf(service.deleteUser(1)));
        SLF4JLoggerProxy.debug(this,
                               String.valueOf(service.getUsers()));
    }
    private void startRestServer()
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
//    private final static String ENDPOINT_ADDRESS = "local://users";
    private final static String ENDPOINT_ADDRESS = "http://localhost:9010/";
    private Server server;
    private UserServiceImpl testUserService;
}
