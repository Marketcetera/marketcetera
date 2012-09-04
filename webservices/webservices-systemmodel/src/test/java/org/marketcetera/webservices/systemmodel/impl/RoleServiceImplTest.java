package org.marketcetera.webservices.systemmodel.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.api.dao.Role;
import org.marketcetera.api.security.RoleManagerService;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.systemmodel.MockRole;
import org.marketcetera.core.systemmodel.RoleFactory;
import org.marketcetera.webservices.WebServicesTestBase;
import org.marketcetera.webservices.systemmodel.RoleService;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/* $License$ */

/**
 * Tests {@link RoleServiceImpl}.
 *
 * @version $Id$
 * @since $Release$
 */
public class RoleServiceImplTest
        extends WebServicesTestBase<RoleService,RoleServiceImpl>
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
        serviceImplementation = new RoleServiceImpl();
        roleManagerService = mock(RoleManagerService.class);
        roleFactory = mock(RoleFactory.class);
        serviceImplementation.setRoleFactory(roleFactory);
        serviceImplementation.setRoleManagerService(roleManagerService);
        when(roleFactory.create()).thenReturn(new MockRole());
        when(roleFactory.create(anyString())).thenAnswer(new Answer<Role>() {
                                         @Override
                                         public Role answer(InvocationOnMock inInvocation)
                                                     throws Throwable
                                         {
                                             MockRole role = new MockRole();
                                             role.setName((String)inInvocation.getArguments()[0]);
                                             role.setId(System.nanoTime());
                                             return role;
                                         }
                                     });
        super.setup();
    }
    /**
     * Tests {@link RoleServiceImpl#addRole(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAddRole()
            throws Exception
    {
        final MockRole newRole = generateRole();
        assertNotNull(newRole.getName());
        // null name
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                service.addRole(null);
            }
        };
        // successful add
        Response response = service.addRole(newRole.getName());
        assertEquals(Response.Status.OK.getStatusCode(),
                     response.getStatus());
        verify(roleManagerService).addRole((Role)any());
        // add role throws an exception
        doThrow(new RuntimeException("This exception is expected")).when(roleManagerService).addRole((Role)any());
        response = service.addRole(newRole.getName());
        assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(),
                     response.getStatus());
        verify(roleManagerService,
               times(2)).addRole((Role)any());
    }
    /**
     * Tests {@link RoleServiceImpl#getRole(long)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetRole()
            throws Exception
    {
        // no result
        assertNull(service.getRole(-1));
        verify(roleManagerService).getRoleById(anyLong());
        // good result
        MockRole newRole = generateRole();
        when(roleManagerService.getRoleById(newRole.getId())).thenReturn(newRole);
        verifyRole(newRole,
                    service.getRole(newRole.getId()));
        verify(roleManagerService,
               times(2)).getRoleById(anyLong());
    }
    /**
     * Tests {@link RoleServiceImpl#deleteRole(long)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDeleteRole()
            throws Exception
    {
        // successful delete
        Response response = service.deleteRole(1);
        assertEquals(Response.Status.OK.getStatusCode(),
                     response.getStatus());
        verify(roleManagerService).deleteRole((Role)any());
        // add role throws an exception
        doThrow(new RuntimeException("This exception is expected")).when(roleManagerService).deleteRole((Role)any());
        response = service.deleteRole(2);
        assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(),
                     response.getStatus());
        verify(roleManagerService,
               times(2)).deleteRole((Role)any());
    }
    /**
     * Tests {@link RoleServiceImpl#getRoles()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetRoles()
            throws Exception
    {
        // no results
        when(roleManagerService.getAllRoles()).thenReturn(new ArrayList<Role>());
        assertTrue(service.getRoles().isEmpty());
        verify(roleManagerService).getAllRoles();
        // single result
        Role role1 = generateRole();
        when(roleManagerService.getAllRoles()).thenReturn(Arrays.asList(new Role[] { role1 }));
        List<Role> expectedResults = new ArrayList<Role>();
        expectedResults.add(role1);
        verifyRoles(expectedResults,
                     service.getRoles());
        verify(roleManagerService,
               times(2)).getAllRoles();
        // multiple results
        Role role2 = generateRole();
        when(roleManagerService.getAllRoles()).thenReturn(Arrays.asList(new Role[] { role1, role2 }));
        expectedResults.add(role2);
        verifyRoles(expectedResults,
                          service.getRoles());
        verify(roleManagerService,
               times(3)).getAllRoles();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.WebServicesTestBase#getServiceInterface()
     */
    @Override
    protected Class<RoleService> getServiceInterface()
    {
        return RoleService.class;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.WebServicesTestBase#getServiceImplementation()
     */
    @Override
    protected RoleServiceImpl getServiceImplementation()
    {
        return serviceImplementation;
    }
    /**
     * role service implementation test value
     */
    private RoleServiceImpl serviceImplementation;
    /**
     * role manager service test value
     */
    private RoleManagerService roleManagerService;
    /**
     * role factory test value
     */
    private RoleFactory roleFactory;
}
