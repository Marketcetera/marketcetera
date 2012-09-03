package org.marketcetera.webservices.systemmodel.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.security.PermissionManagerService;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.systemmodel.PermissionFactory;
import org.marketcetera.core.systemmodel.MockPermission;
import org.marketcetera.webservices.WebServicesTestBase;
import org.marketcetera.webservices.systemmodel.PermissionService;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/* $License$ */

/**
 * Tests {@link PermissionServiceImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PermissionServiceImplTest
        extends WebServicesTestBase<PermissionService,PermissionServiceImpl>
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
        serviceImplementation = new PermissionServiceImpl();
        permissionManagerService = mock(PermissionManagerService.class);
        permissionFactory = mock(PermissionFactory.class);
        serviceImplementation.setPermissionFactory(permissionFactory);
        serviceImplementation.setPermissionManagerService(permissionManagerService);
        when(permissionFactory.create()).thenReturn(new MockPermission());
        when(permissionFactory.create(anyString())).thenAnswer(new Answer<Permission>() {
                                         @Override
                                         public Permission answer(InvocationOnMock inInvocation)
                                                     throws Throwable
                                         {
                                             MockPermission permission = new MockPermission();
                                             permission.setPermission((String)inInvocation.getArguments()[0]);
                                             permission.setId(System.nanoTime());
                                             return permission;
                                         }
                                     });
        super.setup();
    }
    /**
     * Tests {@link PermissionServiceImpl#addPermission(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAddPermission()
            throws Exception
    {
        final MockPermission newPermission = generatePermission();
        assertNotNull(newPermission.getPermission());
        // null permission name
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                service.addPermission(null);
            }
        };
        // successful add
        Response response = service.addPermission(newPermission.getPermission());
        assertEquals(Response.Status.OK.getStatusCode(),
                     response.getStatus());
        verify(permissionManagerService).addPermission((Permission) any());
        // add permission throws an exception
        doThrow(new RuntimeException("This exception is expected")).when(permissionManagerService).addPermission((Permission) any());
        response = service.addPermission(newPermission.getPermission());
        assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(),
                     response.getStatus());
        verify(permissionManagerService,
               times(2)).addPermission((Permission) any());
    }
    /**
     * Tests {@link PermissionServiceImpl#getPermission(long)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetPermission()
            throws Exception
    {
        // no result
        assertNull(service.getPermission(-1));
        verify(permissionManagerService).getPermissionById(anyLong());
        // good result
        MockPermission newPermission = generatePermission();
        when(permissionManagerService.getPermissionById(newPermission.getId())).thenReturn(newPermission);
        verifyPermission(newPermission,
                        service.getPermission(newPermission.getId()));
        verify(permissionManagerService,
               times(2)).getPermissionById(anyLong());
    }
    /**
     * Tests {@link PermissionServiceImpl#deletePermission(long)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testdeletePermission()
            throws Exception
    {
        // successful delete
        Response response = service.deletePermission(1);
        assertEquals(Response.Status.OK.getStatusCode(),
                     response.getStatus());
        verify(permissionManagerService).deletePermission((Permission) any());
        // add permission throws an exception
        doThrow(new RuntimeException("This exception is expected")).when(permissionManagerService).deletePermission((Permission) any());
        response = service.deletePermission(2);
        assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(),
                     response.getStatus());
        verify(permissionManagerService,
               times(2)).deletePermission((Permission) any());
    }
    /**
     * Tests {@link PermissionServiceImpl#getPermissions()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testgetPermissions()
            throws Exception
    {
        // no results
        when(permissionManagerService.getAllPermissions()).thenReturn(new ArrayList<Permission>());
        assertTrue(service.getPermissions().isEmpty());
        verify(permissionManagerService).getAllPermissions();
        // single result
        Permission permission1 = generatePermission();
        when(permissionManagerService.getAllPermissions()).thenReturn(Arrays.asList(new Permission[] {permission1}));
        List<Permission> expectedResults = new ArrayList<Permission>();
        expectedResults.add(permission1);
        verifyPermissions(expectedResults,
                          service.getPermissions());
        verify(permissionManagerService,
               times(2)).getAllPermissions();
        // multiple results
        Permission permission2 = generatePermission();
        when(permissionManagerService.getAllPermissions()).thenReturn(Arrays.asList(new Permission[] {permission1, permission2}));
        expectedResults.add(permission2);
        verifyPermissions(expectedResults,
                          service.getPermissions());
        verify(permissionManagerService,
               times(3)).getAllPermissions();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.WebServicesTestBase#getServiceInterface()
     */
    @Override
    protected Class<PermissionService> getServiceInterface()
    {
        return PermissionService.class;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.WebServicesTestBase#getServiceImplementation()
     */
    @Override
    protected PermissionServiceImpl getServiceImplementation()
    {
        return serviceImplementation;
    }
    /**
     * test permission service implementation
     */
    private PermissionServiceImpl serviceImplementation;
    /**
     * test permission manager service value
     */
    private PermissionManagerService permissionManagerService;
    /**
     * test permission factory value
     */
    private PermissionFactory permissionFactory;
}
