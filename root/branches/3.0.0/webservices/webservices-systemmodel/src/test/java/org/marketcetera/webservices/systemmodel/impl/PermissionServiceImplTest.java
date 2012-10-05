package org.marketcetera.webservices.systemmodel.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.ServerWebApplicationException;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.api.dao.MutablePermission;
import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.dao.PermissionDao;
import org.marketcetera.api.dao.PermissionFactory;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.webservices.WebServicesTestBase;
import org.marketcetera.webservices.systemmodel.PermissionService;
import org.marketcetera.webservices.systemmodel.WebServicesPermission;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/* $License$ */

/**
 * Tests {@link PermissionServiceImpl}.
 *
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
        permissionDao = mock(PermissionDao.class);
        permissionFactory = mock(PermissionFactory.class);
        serviceImplementation.setPermissionFactory(permissionFactory);
        serviceImplementation.setPermissionDao(permissionDao);
        when(permissionFactory.create()).thenReturn(new WebServicesPermission());
        when(permissionFactory.create((Permission)any())).thenAnswer(new Answer<Permission>() {
            @Override
            public Permission answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                Permission permission = (Permission)inInvocation.getArguments()[0];
                WebServicesPermission newPermission = new WebServicesPermission(permission);
                return newPermission;
            }
        });
        when(permissionFactory.create(anyString())).thenAnswer(new Answer<Permission>() {
                                         @Override
                                         public Permission answer(InvocationOnMock inInvocation)
                                                     throws Throwable
                                         {
                                             WebServicesPermission permission = generatePermission();
                                             permission.setName((String)inInvocation.getArguments()[0]);
                                             permission.setId(System.nanoTime());
                                             return permission;
                                         }
                                     });
        super.setup();
    }
    @Test
    public void testMarshalling()
            throws Exception
    {
        WebServicesPermission permission = generatePermission();
        String marshalledValue = JsonMarshallingProvider.getInstance().getService().marshal(permission);
        SLF4JLoggerProxy.debug(this,
                               "Marshalled value is {}",
                               marshalledValue);
        WebServicesPermission newPermission = JsonMarshallingProvider.getInstance().getService().unmarshal(marshalledValue,
                                                                                                               WebServicesPermission.class);
        SLF4JLoggerProxy.debug(this,
                               "Unmarshalled value is {}",
                               newPermission);
    }
    /**
     * Tests {@link org.marketcetera.webservices.systemmodel.PermissionService#addPermission(org.marketcetera.api.dao.Permission)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAddPermission()
            throws Exception
    {
        final WebServicesPermission newPermission = generatePermission();
        assertNotNull(newPermission.getName());
        // null permission name
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                service.addPermission(null);
            }
        };
        final long newId = counter.incrementAndGet();
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
//                permission.setId(newId);
                return null;
            }
        }).when(permissionDao).add((MutablePermission)any());
        // successful add
        newPermission.setId(counter.incrementAndGet());
        long existingId = newPermission.getId();
        assertFalse(newId == existingId);
        WebServicesPermission response = service.addPermission(newPermission);
//        assertEquals(newId,
//                     response.getId());
        verify(permissionDao).add((Permission)any());
        // add permission throws an exception
        doThrow(new RuntimeException("This exception is expected")).when(permissionDao).add((Permission) any());
        new ExpectedFailure<RuntimeException>() {
            @Override
            protected void run()
                    throws Exception
            {
                service.addPermission(newPermission);
            }
        };
        verify(permissionDao,
               times(2)).add((Permission) any());
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
        new ExpectedFailure<ServerWebApplicationException>() {
            @Override
            protected void run()
                    throws Exception
            {
                service.getPermission(-1);
            }
        };
        verify(permissionDao).getById(anyLong());
        // good result
        WebServicesPermission newPermission = generatePermission();
        when(permissionDao.getById(newPermission.getId())).thenReturn(newPermission);
        verifyPermission(newPermission,
                        service.getPermission(newPermission.getId()));
        verify(permissionDao,
               times(2)).getById(anyLong());
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
        verify(permissionDao).delete((Permission) any());
        // add permission throws an exception
        doThrow(new RuntimeException("This exception is expected")).when(permissionDao).delete((Permission) any());
        response = service.deletePermission(2);
        assertEquals(Response.serverError().build().getStatus(),
                     response.getStatus());
        verify(permissionDao,
               times(2)).delete((Permission) any());
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
        when(permissionDao.getAll()).thenReturn(new ArrayList<MutablePermission>());
        assertTrue(service.getPermissions().isEmpty());
        verify(permissionDao).getAll();
        // single result
        WebServicesPermission permission1 = generatePermission();
        when(permissionDao.getAll()).thenReturn(Arrays.asList(new MutablePermission[] {permission1}));
        List<Permission> expectedResults = new ArrayList<Permission>();
        expectedResults.add(permission1);
        verifyPermissions(expectedResults,
                          service.getPermissions());
        verify(permissionDao,
               times(2)).getAll();
        // multiple results
        WebServicesPermission permission2 = generatePermission();
        when(permissionDao.getAll()).thenReturn(Arrays.asList(new MutablePermission[]{permission1, permission2}));
        expectedResults.add(permission2);
        verifyPermissions(expectedResults,
                          service.getPermissions());
        verify(permissionDao,
               times(3)).getAll();
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
    private PermissionDao permissionDao;
    /**
     * test permission factory value
     */
    private PermissionFactory permissionFactory;
}
