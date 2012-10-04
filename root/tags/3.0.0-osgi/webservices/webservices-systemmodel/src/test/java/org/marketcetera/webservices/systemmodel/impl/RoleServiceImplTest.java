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

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.api.dao.MutableRole;
import org.marketcetera.api.dao.Role;
import org.marketcetera.api.dao.RoleDao;
import org.marketcetera.api.dao.RoleFactory;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.webservices.WebServicesTestBase;
import org.marketcetera.webservices.systemmodel.RoleService;
import org.marketcetera.webservices.systemmodel.WebServicesRole;
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
        roleDao = mock(RoleDao.class);
        roleFactory = mock(RoleFactory.class);
        serviceImplementation.setRoleFactory(roleFactory);
        serviceImplementation.setRoleDao(roleDao);
        when(roleFactory.create()).thenReturn(new WebServicesRole());
        when(roleFactory.create((Role)any())).thenAnswer(new Answer<Role>() {
            @Override
            public Role answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                Role role = (Role)inInvocation.getArguments()[0];
                WebServicesRole newRole = new WebServicesRole(role);
                return newRole;
            }
        });
        when(roleFactory.create(anyString())).thenAnswer(new Answer<Role>() {
                                         @Override
                                         public Role answer(InvocationOnMock inInvocation)
                                                     throws Throwable
                                         {
                                             WebServicesRole role = generateRole();
                                             role.setName((String)inInvocation.getArguments()[0]);
                                             role.setId(System.nanoTime());
                                             return role;
                                         }
                                     });
        super.setup();
    }
    @Test
    public void testMarshalling()
            throws Exception
    {
        WebServicesRole role = generateRole();
        String marshalledValue = JsonMarshallingProvider.getInstance().getService().marshal(role);
        SLF4JLoggerProxy.debug(this,
                               "Marshalled value is {}",
                               marshalledValue);
        WebServicesRole newRole = JsonMarshallingProvider.getInstance().getService().unmarshal(marshalledValue,
                                                                                               WebServicesRole.class);
        SLF4JLoggerProxy.debug(this,
                               "Unmarshalled value is {}",
                               newRole);
    }
    /**
     * Tests {@link org.marketcetera.webservices.systemmodel.RoleService#addRoleJSON(org.marketcetera.api.dao.Role)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAddRole()
            throws Exception
    {
        final WebServicesRole newRole = generateRole();
        assertNotNull(newRole.getName());
        // null role name
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                service.addRoleJSON(null);
            }
        };
        final long newId = counter.incrementAndGet();
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
//                role.setId(newId);
                return null;
            }
        }).when(roleDao).add((MutableRole)any());
        // successful add
        newRole.setId(counter.incrementAndGet());
        long existingId = newRole.getId();
        assertFalse(newId == existingId);
        WebServicesRole response = service.addRoleJSON(newRole);
//        assertEquals(newId,
//                     response.getId());
        verify(roleDao).add((Role)any());
        // add role throws an exception
        doThrow(new RuntimeException("This exception is expected")).when(roleDao).add((Role) any());
        new ExpectedFailure<RuntimeException>() {
            @Override
            protected void run()
                    throws Exception
            {
                service.addRoleJSON(newRole);
            }
        };
        verify(roleDao,
               times(2)).add((Role) any());
    }
    /**
     * Tests {@link RoleServiceImpl#getRoleJSON(long)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetRole()
            throws Exception
    {
        // TODO
//        // no result
//        new ExpectedFailure<ServerWebApplicationException>() {
//            @Override
//            protected void run()
//                    throws Exception
//            {
//                service.getRoleJSON(-1);
//            }
//        };
//        verify(roleDao).getById(anyLong());
        // good result
        WebServicesRole newRole = generateRole();
        when(roleDao.getById(newRole.getId())).thenReturn(newRole);
        verifyRole(newRole,
                        service.getRoleJSON(newRole.getId()));
      verify(roleDao).getById(anyLong());
//        verify(roleDao,
//               times(2)).getById(anyLong());
    }
    /**
     * Tests {@link RoleServiceImpl#deleteRole(long)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testdeleteRole()
            throws Exception
    {
        // successful delete
        Response response = service.deleteRole(1);
        assertEquals(Response.Status.OK.getStatusCode(),
                     response.getStatus());
        verify(roleDao).delete((Role) any());
        // add role throws an exception
        doThrow(new RuntimeException("This exception is expected")).when(roleDao).delete((Role) any());
        response = service.deleteRole(2);
        assertEquals(Response.serverError().build().getStatus(),
                     response.getStatus());
        verify(roleDao,
               times(2)).delete((Role) any());
    }
    /**
     * Tests {@link RoleServiceImpl#getRolesJSON()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testgetRoles()
            throws Exception
    {
        // no results
        when(roleDao.getAll()).thenReturn(new ArrayList<MutableRole>());
        assertTrue(service.getRolesJSON().isEmpty());
        verify(roleDao).getAll();
        // single result
        WebServicesRole role1 = generateRole();
        when(roleDao.getAll()).thenReturn(Arrays.asList(new MutableRole[] {role1}));
        List<Role> expectedResults = new ArrayList<Role>();
        expectedResults.add(role1);
        verifyRoles(expectedResults,
                    service.getRolesJSON());
        verify(roleDao,
               times(2)).getAll();
        // multiple results
        WebServicesRole role2 = generateRole();
        when(roleDao.getAll()).thenReturn(Arrays.asList(new MutableRole[]{role1, role2}));
        expectedResults.add(role2);
        verifyRoles(expectedResults,
                          service.getRolesJSON());
        verify(roleDao,
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
     * test role service implementation
     */
    private RoleServiceImpl serviceImplementation;
    /**
     * test role manager service value
     */
    private RoleDao roleDao;
    /**
     * test role factory value
     */
    private RoleFactory roleFactory;
}
