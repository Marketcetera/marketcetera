package org.marketcetera.security.shiro.impl;

import static org.mockito.Mockito.mock;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.marketcetera.api.security.UserManagerService;
import org.marketcetera.core.LoggerConfiguration;
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
        userManagerService = mock(UserManagerService.class);
        testUserService = new UserServiceImpl();
        testUserService.setUserManagerService(userManagerService);
        startRestServer();
        startSoapServer();
        waitForWADL();
    }
    /**
     * Run once after all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @AfterClass
    public static void destroy()
            throws Exception
    {
        server.stop();
        server.destroy();
    }
    private static void startRestServer()
    {
        JAXRSServerFactoryBean serverFactory = new JAXRSServerFactoryBean();
        serverFactory.setResourceClasses(UserService.class);
        List<Object> providers = new ArrayList<Object>();
        providers.add(org.apache.cxf.jaxrs.provider.json.JSONProvider.class);
        serverFactory.setProviders(providers);
        serverFactory.setResourceProvider(UserService.class,
                                          new SingletonResourceProvider(testUserService,
                                                                        true));
        serverFactory.setAddress(ENDPOINT_ADDRESS);
//        Map<Object,Object> mappings = new HashMap<Object,Object>();
//        mappings.put("xml",
//                     "application/xml");
//        mappings.put("json",
//                     "application/json");
//        serverFactory.setExtensionMappings(mappings);
        server = serverFactory.create();
        server.start();
    }
    private static void waitForWADL() throws Exception {
        WebClient client = WebClient.create(WADL_ADDRESS);
        // wait for 20 secs or so
        for (int i = 0; i < 20; i++) {
            Thread.sleep(250);
            Response response = client.get();
            if (response.getStatus() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader((InputStream)response.getEntity()));
                String line = null;
                StringBuffer sb = new StringBuffer();
                while((line = br.readLine()) != null) {
                  sb.append(line).append("<eol>");
                }
                SLF4JLoggerProxy.debug(UserServiceTest.class,
                                       "WADL: {}",
                                       sb);
                break;
            }
        }
        // no WADL is available yet - throw an exception or give tests a chance to run anyway
    }
    private static void startSoapServer()
    {
//        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
//        sf.setServiceClass(BlogReader.class);
//        BloggerImpl blogger = new BloggerImpl();
//        sf.setServiceBean(blogger);
//        sf.setAddress("http://localhost:9000/soap/blog");
//        sf.getInInterceptors().add(new LoggingInInterceptor());
//        sf.getOutInterceptors().add(new LoggingOutInterceptor());
//        sf.create();
//        System.out.println("Soap Server started @ " + sf.getAddress());
    }
    //    private static void startServer()
//            throws Exception
//    {
//        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
//        sf.setTransportId(LocalTransportFactory.TRANSPORT_ID);
//        sf.setResourceClasses(UserServiceImpl.class);
//
//        List<Object> providers = new ArrayList<Object>();
//        // add custom providers if any
//        sf.setProviders(providers);
//
//        sf.setResourceProvider(UserServiceImpl.class,
//                               new SingletonResourceProvider(new UserServiceImpl(),
//                                                             true));
//        sf.setAddress(ENDPOINT_ADDRESS);
//        server = sf.create();
//        server.start();
//    }
    @Test@Ignore
    public void testOne()
            throws Exception
    {
//        Thread.sleep(60000);
        ClientProxyFactoryBean cf = new ClientProxyFactoryBean();
        cf.setAddress(ENDPOINT_ADDRESS);
        cf.setServiceClass(UserService.class);
        UserService testService = (UserService)cf.create();
        SLF4JLoggerProxy.debug(this,
                               String.valueOf(testService.getUsers()));
//        WebClient client = WebClient.create(ENDPOINT_ADDRESS);
//        WebClient.getConfig(client).getRequestContext().put(LocalConduit.DIRECT_DISPATCH,
//                                                            Boolean.TRUE);
//        client.accept("text/json");
//        client.path("all");
//        System.out.println("Users: " + client.get(List.class));
    }
//    private final static String ENDPOINT_ADDRESS = "local://users";
    private final static String ENDPOINT_ADDRESS = "http://localhost:9010/users";
    private final static String WADL_ADDRESS = ENDPOINT_ADDRESS + "?_wadl";
    private static Server server;
    private static UserManagerService userManagerService;
    private static UserServiceImpl testUserService;
    ////    @Test
////    public void testGetBookWithWebClient()
////    {
////        WebClient client = WebClient.create(ENDPOINT_ADDRESS);
////        client.accept("text/xml");
////        client.path("somepath");
////        Book book = client.get(Book.class);
////        assertEquals(123L, book.getId());
////    }
//    @Test
//    public void testGetBookWithProxy()
//    {
//        UserService client = JAXRSClientFactory.create(ENDPOINT_ADDRESS,
//                                                       UserService.class);
//        WebClient.getConfig(client).getRequestContext().put(LocalConduit.DIRECT_DISPATCH,
//                                                            Boolean.TRUE);
//        User user = client.getUser("1");
//        assertEquals(123L,
//                     user.getId());
//    }
//    private static void startServer()
//            throws Exception
//    {
//        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
//        sf.setTransportId(LocalTransportFactory.TRANSPORT_ID);
//        sf.setResourceClasses(UserService.class);
//
//        List<Object> providers = new ArrayList<Object>();
//        // add custom providers if any
//        sf.setProviders(providers);
//
//        sf.setResourceProvider(UserService.class,
//                               new SingletonResourceProvider(new UserService(),
//                                                             true));
//        sf.setAddress(ENDPOINT_ADDRESS);
//    }
//    private final static String ENDPOINT_ADDRESS = "local://userservice";
//    private static Server server;

}
