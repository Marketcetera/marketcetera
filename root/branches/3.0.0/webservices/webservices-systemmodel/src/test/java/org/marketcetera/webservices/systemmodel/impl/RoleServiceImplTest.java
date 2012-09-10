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
import org.marketcetera.api.dao.MutableRole;
import org.marketcetera.api.dao.Role;
import org.marketcetera.api.dao.RoleDao;
import org.marketcetera.api.dao.RoleFactory;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.webservices.WebServicesTestBase;
import org.marketcetera.webservices.systemmodel.MockRole;
import org.marketcetera.webservices.systemmodel.RoleService;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

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
        roleManagerService = mock(RoleDao.class);
        roleFactory = mock(RoleFactory.class);
        serviceImplementation.setRoleFactory(roleFactory);
        serviceImplementation.setRoleDao(roleManagerService);
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
        final MutableRole newRole = generateRole();
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
        verify(roleManagerService).add((Role)any());
        // add role throws an exception
        doThrow(new RuntimeException("This exception is expected")).when(roleManagerService).add((Role)any());
        response = service.addRole(newRole.getName());
        assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(),
                     response.getStatus());
        verify(roleManagerService,
               times(2)).add((Role)any());
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
        verify(roleManagerService).getById(anyLong());
        // good result
        MutableRole newRole = generateRole();
        when(roleManagerService.getById(newRole.getId())).thenReturn(newRole);
        verifyRole(newRole,
                    service.getRole(newRole.getId()));
        verify(roleManagerService,
               times(2)).getById(anyLong());
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
        verify(roleManagerService).delete((Role)any());
        // add role throws an exception
        doThrow(new RuntimeException("This exception is expected")).when(roleManagerService).delete((Role)any());
        response = service.deleteRole(2);
        assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(),
                     response.getStatus());
        verify(roleManagerService,
               times(2)).delete((Role)any());
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
        when(roleManagerService.getAll()).thenReturn(new ArrayList<MutableRole>());
        assertTrue(service.getRoles().isEmpty());
        verify(roleManagerService).getAll();
        // single result
        MutableRole role1 = (MutableRole)generateRole();
        when(roleManagerService.getAll()).thenReturn(Arrays.asList(new MutableRole[] { role1 }));
        List<Role> expectedResults = new ArrayList<Role>();
        expectedResults.add(role1);
        verifyRoles(expectedResults,
                     service.getRoles());
        verify(roleManagerService,
               times(2)).getAll();
        // multiple results
        MutableRole role2 = (MutableRole)generateRole();
        when(roleManagerService.getAll()).thenReturn(Arrays.asList(new MutableRole[] { role1, role2 }));
        expectedResults.add(role2);
        verifyRoles(expectedResults,
                          service.getRoles());
        verify(roleManagerService,
               times(3)).getAll();
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
    private RoleDao roleManagerService;
    /**
     * role factory test value
     */
    private RoleFactory roleFactory;
}
