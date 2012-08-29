package org.marketcetera.webservices;

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
import org.marketcetera.api.security.User;
import org.marketcetera.core.systemmodel.SystemmodelTestBase;

/* $License$ */

/**
 * Provides common services for web services tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class WebServicesTestBase<InterfaceClazz,ImplementationClazz>
        extends SystemmodelTestBase
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
        testServiceImplementation = getServiceImplementation();
        startServer();
        service = JAXRSClientFactory.create(ENDPOINT_ADDRESS,
                                                         getServiceInterface());
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
     * Starts the test server. 
     */
    protected void startServer()
    {
        JAXRSServerFactoryBean serverFactory = new JAXRSServerFactoryBean();
        Class<InterfaceClazz> interfaceClazz = getServiceInterface();
        serverFactory.setResourceClasses(interfaceClazz);
        List<Object> providers = new ArrayList<Object>();
        providers.add(new JSONProvider<User>());
        serverFactory.setProviders(providers);
        serverFactory.setResourceProvider(interfaceClazz,
                                          new SingletonResourceProvider(testServiceImplementation,
                                                                        true));
        serverFactory.setAddress(ENDPOINT_ADDRESS);
        Map<Object,Object> mappings = new HashMap<Object,Object>();
        mappings.put("json",
                     "application/json");
        serverFactory.setExtensionMappings(mappings);
        server = serverFactory.create();
    }
    /**
     * Gets the service interface to test. 
     *
     * @return a <code>Class&lt;Clazz&gt;</code> value
     */
    protected abstract Class<InterfaceClazz> getServiceInterface();
    /**
     * Get the service implementation to test. 
     *
     * @return an <code>ImplementationClazz</code> value
     */
    protected abstract ImplementationClazz getServiceImplementation();
    /**
     * address of service
     */
    private final static String ENDPOINT_ADDRESS = "http://localhost:9010/";
    /**
     * server test object
     */
    private Server server;
    /**
     * test service implementation value
     */
    protected ImplementationClazz testServiceImplementation;
    /**
     * test service interface value
     */
    protected InterfaceClazz service;
}
