package org.marketcetera.webservices;

import static org.junit.Assert.assertEquals;

import java.util.*;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.provider.json.JSONProvider;
import org.junit.After;
import org.junit.Before;
import org.marketcetera.api.dao.Authority;
import org.marketcetera.api.security.User;
import org.marketcetera.api.dao.Group;
import org.marketcetera.core.systemmodel.SystemmodelTestBase;
import org.marketcetera.webservices.systemmodel.WebServicesAuthority;
import org.marketcetera.webservices.systemmodel.WebServicesGroup;
import org.marketcetera.webservices.systemmodel.WebServicesUser;

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
     * Verifies that the given expected value matches the given actual value.
     *
     * @param inExpectedGroup a <code>Group</code> value
     * @param inActualGroup a <code>WebServicesGroup</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyGroup(Group inExpectedGroup,
                               WebServicesGroup inActualGroup)
            throws Exception
    {
        assertEquals(inExpectedGroup.getName(),
                     inActualGroup.getName());
        assertEquals(inExpectedGroup.getId(),
                     inActualGroup.getId());
    }
    /**
     * Verifies that the given expected values match the given actual values.
     *
     * @param inExpectedGroups a <code>Collection&lt;Group&gt;</code> value
     * @param inActualGroups a <code>Collection&lt;WebServicesGroup&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyGroups(Collection<Group> inExpectedGroups,
                                Collection<WebServicesGroup> inActualGroups)
            throws Exception
    {
        assertEquals(inExpectedGroups.size(),
                     inActualGroups.size());
        Iterator<WebServicesGroup> actualIterator = inActualGroups.iterator();
        for(Group expectedUser : inExpectedGroups) {
            verifyGroup(expectedUser,
                        actualIterator.next());
        }
    }
    /**
     * Verifies that the given expected value matches the given actual value.
     *
     * @param inExpectedUser a <code>User</code> value
     * @param inActualUser a <code>WebServicesUser</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyUser(User inExpectedUser,
                                 WebServicesUser inActualUser)
            throws Exception
    {
        assertEquals(inExpectedUser.getName(),
                     inActualUser.getUsername());
        assertEquals(inExpectedUser.getId(),
                     inActualUser.getId());
    }
    /**
     * Verifies that the given expected values match the given actual values.
     *
     * @param inExpectedUsers a <code>Collection&lt;User&gt;</code> value
     * @param inActualUsers a <code>Collection&lt;WebServicesUser&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyUsers(Collection<User> inExpectedUsers,
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
     * Verifies that the given expected value matches the given actual value.
     *
     * @param inExpectedAuthority an <code>Authority</code> value
     * @param inActualAuthority a <code>WebServicesAuthority</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyAuthority(Authority inExpectedAuthority,
                                      WebServicesAuthority inActualAuthority)
            throws Exception
    {
        assertEquals(inExpectedAuthority.getName(),
                     inActualAuthority.getAuthority());
        assertEquals(inExpectedAuthority.getId(),
                     inActualAuthority.getId());
    }
    /**
     * Verifies that the given expected values match the given actual values.
     *
     * @param inExpectedAuthorities a <code>Collection&lt;Authority&gt;</code> value
     * @param inActualAuthorities a <code>Collection&lt;WebServicesAuthority&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyAuthorities(Collection<Authority> inExpectedAuthorities,
                                        Collection<WebServicesAuthority> inActualAuthorities)
            throws Exception
    {
        assertEquals(inExpectedAuthorities.size(),
                     inActualAuthorities.size());
        Iterator<WebServicesAuthority> actualIterator = inActualAuthorities.iterator();
        for(Authority expectedUser : inExpectedAuthorities) {
            verifyAuthority(expectedUser,
                            actualIterator.next());
        }
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
