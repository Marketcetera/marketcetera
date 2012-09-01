package org.marketcetera.webservices.systemmodel.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.util.*;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.api.dao.Group;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.systemmodel.GroupFactory;
import org.marketcetera.api.security.GroupManagerService;
import org.marketcetera.core.systemmodel.MockGroup;
import org.marketcetera.webservices.WebServicesTestBase;
import org.marketcetera.webservices.systemmodel.GroupService;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/* $License$ */

/**
 * Tests {@link GroupServiceImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class GroupServiceImplTest
        extends WebServicesTestBase<GroupService,GroupServiceImpl>
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
        serviceImplementation = new GroupServiceImpl();
        groupManagerService = mock(GroupManagerService.class);
        groupFactory = mock(GroupFactory.class);
        serviceImplementation.setGroupFactory(groupFactory);
        serviceImplementation.setGroupManagerService(groupManagerService);
        when(groupFactory.create()).thenReturn(new MockGroup());
        when(groupFactory.create(anyString())).thenAnswer(new Answer<Group>() {
                                         @Override
                                         public Group answer(InvocationOnMock inInvocation)
                                                     throws Throwable
                                         {
                                             MockGroup group = new MockGroup();
                                             group.setName((String)inInvocation.getArguments()[0]);
                                             group.setId(System.nanoTime());
                                             return group;
                                         }
                                     });
        super.setup();
    }
    /**
     * Tests {@link GroupServiceImpl#addGroup(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAddGroup()
            throws Exception
    {
        final MockGroup newGroup = generateGroup();
        assertNotNull(newGroup.getName());
        // null name
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                service.addGroup(null);
            }
        };
        // successful add
        Response response = service.addGroup(newGroup.getName());
        assertEquals(Response.Status.OK.getStatusCode(),
                     response.getStatus());
        verify(groupManagerService).addGroup((Group)any());
        // add group throws an exception
        doThrow(new RuntimeException("This exception is expected")).when(groupManagerService).addGroup((Group)any());
        response = service.addGroup(newGroup.getName());
        assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(),
                     response.getStatus());
        verify(groupManagerService,
               times(2)).addGroup((Group)any());
    }
    /**
     * Tests {@link GroupServiceImpl#getGroup(long)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetGroup()
            throws Exception
    {
        // no result
        assertNull(service.getGroup(-1));
        verify(groupManagerService).getGroupById(anyLong());
        // good result
        MockGroup newGroup = generateGroup();
        when(groupManagerService.getGroupById(newGroup.getId())).thenReturn(newGroup);
        verifyGroup(newGroup,
                    service.getGroup(newGroup.getId()));
        verify(groupManagerService,
               times(2)).getGroupById(anyLong());
    }
    /**
     * Tests {@link GroupServiceImpl#deleteGroup(long)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDeleteGroup()
            throws Exception
    {
        // successful delete
        Response response = service.deleteGroup(1);
        assertEquals(Response.Status.OK.getStatusCode(),
                     response.getStatus());
        verify(groupManagerService).deleteGroup((Group)any());
        // add group throws an exception
        doThrow(new RuntimeException("This exception is expected")).when(groupManagerService).deleteGroup((Group)any());
        response = service.deleteGroup(2);
        assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(),
                     response.getStatus());
        verify(groupManagerService,
               times(2)).deleteGroup((Group)any());
    }
    /**
     * Tests {@link GroupServiceImpl#getGroups()}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetGroups()
            throws Exception
    {
        // no results
        when(groupManagerService.getAllGroups()).thenReturn(new ArrayList<Group>());
        assertTrue(service.getGroups().isEmpty());
        verify(groupManagerService).getAllGroups();
        // single result
        Group group1 = generateGroup();
        when(groupManagerService.getAllGroups()).thenReturn(Arrays.asList(new Group[] { group1 }));
        List<Group> expectedResults = new ArrayList<Group>();
        expectedResults.add(group1);
        verifyGroups(expectedResults,
                     service.getGroups());
        verify(groupManagerService,
               times(2)).getAllGroups();
        // multiple results
        Group group2 = generateGroup();
        when(groupManagerService.getAllGroups()).thenReturn(Arrays.asList(new Group[] { group1, group2 }));
        expectedResults.add(group2);
        verifyGroups(expectedResults,
                          service.getGroups());
        verify(groupManagerService,
               times(3)).getAllGroups();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.WebServicesTestBase#getServiceInterface()
     */
    @Override
    protected Class<GroupService> getServiceInterface()
    {
        return GroupService.class;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.WebServicesTestBase#getServiceImplementation()
     */
    @Override
    protected GroupServiceImpl getServiceImplementation()
    {
        return serviceImplementation;
    }
    /**
     * group service implementation test value
     */
    private GroupServiceImpl serviceImplementation;
    /**
     * group manager service test value
     */
    private GroupManagerService groupManagerService;
    /**
     * group factory test value
     */
    private GroupFactory groupFactory;
}
