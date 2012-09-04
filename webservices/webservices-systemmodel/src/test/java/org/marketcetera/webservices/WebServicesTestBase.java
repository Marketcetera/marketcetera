package org.marketcetera.webservices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.provider.json.JSONProvider;
import org.junit.After;
import org.junit.Before;
import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.dao.Role;
import org.marketcetera.api.security.User;
import org.marketcetera.core.systemmodel.SystemmodelTestBase;
import org.marketcetera.webservices.systemmodel.WebServicesPermission;
import org.marketcetera.webservices.systemmodel.WebServicesRole;
import org.marketcetera.webservices.systemmodel.WebServicesUser;

import static org.junit.Assert.assertEquals;

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
     * @param inExpectedRole a <code>Role</code> value
     * @param inActualRole a <code>WebServicesRole</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyRole(Role inExpectedRole,
                               WebServicesRole inActualRole)
            throws Exception
    {
        assertEquals(inExpectedRole.getName(),
                     inActualRole.getName());
        assertEquals(inExpectedRole.getId(),
                     inActualRole.getId());
    }
    /**
     * Verifies that the given expected values match the given actual values.
     *
     * @param inExpectedRoles a <code>Collection&lt;Role&gt;</code> value
     * @param inActualRoles a <code>Collection&lt;WebServicesRole&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyRoles(Collection<Role> inExpectedRoles,
                                Collection<WebServicesRole> inActualRoles)
            throws Exception
    {
        assertEquals(inExpectedRoles.size(),
                     inActualRoles.size());
        Iterator<WebServicesRole> actualIterator = inActualRoles.iterator();
        for(Role expectedUser : inExpectedRoles) {
            verifyRole(expectedUser,
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
     * @param inExpectedPermission an <code>Permission</code> value
     * @param inActualPermission a <code>WebServicesPermission</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyPermission(Permission inExpectedPermission,
                                      WebServicesPermission inActualPermission)
            throws Exception
    {
        assertEquals(inExpectedPermission.getName(),
                     inActualPermission.getPermission());
        assertEquals(inExpectedPermission.getId(),
                     inActualPermission.getId());
    }
    /**
     * Verifies that the given expected values match the given actual values.
     *
     * @param inExpectedPermissions a <code>Collection&lt;Permission&gt;</code> value
     * @param inActualPermissions a <code>Collection&lt;WebServicesPermission&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyPermissions(Collection<Permission> inExpectedPermissions,
                                        Collection<WebServicesPermission> inActualPermissions)
            throws Exception
    {
        assertEquals(inExpectedPermissions.size(),
                     inActualPermissions.size());
        Iterator<WebServicesPermission> actualIterator = inActualPermissions.iterator();
        for(Permission expectedUser : inExpectedPermissions) {
            verifyPermission(expectedUser, actualIterator.next());
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
