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
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.systemmodel.Authority;
import org.marketcetera.core.systemmodel.AuthorityFactory;
import org.marketcetera.core.systemmodel.AuthorityManagerService;
import org.marketcetera.core.systemmodel.MockAuthority;
import org.marketcetera.webservices.WebServicesTestBase;
import org.marketcetera.webservices.systemmodel.AuthorityService;
import org.marketcetera.webservices.systemmodel.WebServicesAuthority;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/* $License$ */

/**
 * Tests {@link AuthorityServiceImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AuthorityServiceImplTest
        extends WebServicesTestBase<AuthorityService,AuthorityServiceImpl>
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
        serviceImplementation = new AuthorityServiceImpl();
        authorityManagerService = mock(AuthorityManagerService.class);
        authorityFactory = mock(AuthorityFactory.class);
        serviceImplementation.setAuthorityFactory(authorityFactory);
        serviceImplementation.setAuthorityManagerService(authorityManagerService);
        when(authorityFactory.create()).thenReturn(new MockAuthority());
        when(authorityFactory.create(anyString())).thenAnswer(new Answer<Authority>() {
                                         @Override
                                         public Authority answer(InvocationOnMock inInvocation)
                                                     throws Throwable
                                         {
                                             MockAuthority authority = new MockAuthority();
                                             authority.setAuthority((String)inInvocation.getArguments()[0]);
                                             authority.setId(System.nanoTime());
                                             return authority;
                                         }
                                     });
        super.setup();
    }
    /**
     * Tests {@link AuthorityServiceImpl#addAuthority(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAddAuthority()
            throws Exception
    {
        final MockAuthority newAuthority = generateAuthority();
        assertNotNull(newAuthority.getAuthority());
        // null authority name
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                service.addAuthority(null);
            }
        };
        // successful add
        Response response = service.addAuthority(newAuthority.getAuthority());
        assertEquals(Response.Status.OK.getStatusCode(),
                     response.getStatus());
        verify(authorityManagerService).addAuthority((Authority)any());
        // add authority throws an exception
        doThrow(new RuntimeException("This exception is expected")).when(authorityManagerService).addAuthority((Authority)any());
        response = service.addAuthority(newAuthority.getAuthority());
        assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(),
                     response.getStatus());
        verify(authorityManagerService,
               times(2)).addAuthority((Authority)any());
    }
    /**
     * Tests {@link AuthorityServiceImpl#getAuthority(long)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetAuthority()
            throws Exception
    {
        // no result
        assertNull(service.getAuthority(-1));
        verify(authorityManagerService).getAuthorityById(anyLong());
        // good result
        MockAuthority newAuthority = generateAuthority();
        when(authorityManagerService.getAuthorityById(newAuthority.getId())).thenReturn(newAuthority);
        verifyAuthority(newAuthority,
                        service.getAuthority(newAuthority.getId()));
        verify(authorityManagerService,
               times(2)).getAuthorityById(anyLong());
    }
    /**
     * Tests {@link AuthorityServiceImpl#deleteAuthority(long)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testdeleteAuthority()
            throws Exception
    {
        // successful delete
        Response response = service.deleteAuthority(1);
        assertEquals(Response.Status.OK.getStatusCode(),
                     response.getStatus());
        verify(authorityManagerService).deleteAuthority((Authority)any());
        // add authority throws an exception
        doThrow(new RuntimeException("This exception is expected")).when(authorityManagerService).deleteAuthority((Authority)any());
        response = service.deleteAuthority(2);
        assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(),
                     response.getStatus());
        verify(authorityManagerService,
               times(2)).deleteAuthority((Authority)any());
    }
    /**
     * Tests {@link AuthorityServiceImpl#getAuthorities()}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testgetAuthorities()
            throws Exception
    {
        // no results
        when(authorityManagerService.getAllAuthorities()).thenReturn(new ArrayList<Authority>());
        assertTrue(service.getAuthorities().isEmpty());
        verify(authorityManagerService).getAllAuthorities();
        // single result
        Authority authority1 = generateAuthority();
        when(authorityManagerService.getAllAuthorities()).thenReturn(Arrays.asList(new Authority[] { authority1 }));
        List<Authority> expectedResults = new ArrayList<Authority>();
        expectedResults.add(authority1);
        verifyAuthorities(expectedResults,
                          service.getAuthorities());
        verify(authorityManagerService,
               times(2)).getAllAuthorities();
        // multiple results
        Authority authority2 = generateAuthority();
        when(authorityManagerService.getAllAuthorities()).thenReturn(Arrays.asList(new Authority[] { authority1, authority2 }));
        expectedResults.add(authority2);
        verifyAuthorities(expectedResults,
                          service.getAuthorities());
        verify(authorityManagerService,
               times(3)).getAllAuthorities();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.WebServicesTestBase#getServiceInterface()
     */
    @Override
    protected Class<AuthorityService> getServiceInterface()
    {
        return AuthorityService.class;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.WebServicesTestBase#getServiceImplementation()
     */
    @Override
    protected AuthorityServiceImpl getServiceImplementation()
    {
        return serviceImplementation;
    }
    /**
     * Verifies that the given expected value matches the given actual value.
     *
     * @param inExpectedAuthority an <code>Authority</code> value
     * @param inActualAuthority a <code>WebServicesAuthority</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyAuthority(Authority inExpectedAuthority,
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
    private void verifyAuthorities(Collection<Authority> inExpectedAuthorities,
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
     * test authority service implementation 
     */
    private AuthorityServiceImpl serviceImplementation;
    /**
     * test authority manager service value
     */
    private AuthorityManagerService authorityManagerService;
    /**
     * test authority factory value
     */
    private AuthorityFactory authorityFactory;
}
