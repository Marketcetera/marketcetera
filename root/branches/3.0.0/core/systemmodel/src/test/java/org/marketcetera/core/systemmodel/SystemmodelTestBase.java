package org.marketcetera.core.systemmodel;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.marketcetera.core.LoggerConfiguration;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/* $License$ */

/**
 * Provides common test services for systemmodel objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SystemmodelTestBase
{
    /**
     * Run once before all tests. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
    }
    /**
     * Run once before each test. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        initializeAuthorities();
    }
    /**
     * Initializes the authorities objects. 
     *
     * @throws Exception if an unexpected error occurs
     */
    protected void initializeAuthorities()
            throws Exception
    {
        authorityDao = mock(AuthorityDao.class);
        authoritiesByName = new HashMap<String,Authority>();
        authoritiesById = new HashMap<Long,Authority>();
        for(int i=0;i<=3;i++) {
            addAuthority(generateAuthority());
        }
        when(authorityDao.getByName(anyString())).thenAnswer(new Answer<Authority>() {
            @Override
            public Authority answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                return getAuthorityByName((String)inInvocation.getArguments()[0]);
            }
        });
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                Authority authority = (Authority)inInvocation.getArguments()[0];
                addAuthority(authority);
                return null;
            }
        }).when(authorityDao).save((Authority)any());
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                Authority authority = (Authority)inInvocation.getArguments()[0];
                removeAuthority(authority);
                return null;
            }
        }).when(authorityDao).delete((Authority)any());
        when(authorityDao.getByName(anyString())).thenAnswer(new Answer<Authority>() {
            @Override
            public Authority answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                return getAuthorityByName((String)inInvocation.getArguments()[0]);
            }
        });
        when(authorityDao.getById(anyLong())).thenAnswer(new Answer<Authority>() {
            @Override
            public Authority answer(InvocationOnMock inInvocation)
                    throws Throwable
            {
                return getAuthorityById((Long)inInvocation.getArguments()[0]);
            }
        });
        when(authorityDao.getAll()).thenReturn(new ArrayList<Authority>(getAllAuthorities()));
    }
    /**
     * Generates a unique <code>MockAuthority</code> value.
     *
     * @return a <code>MockAuthority</code> value
     */
    protected MockAuthority generateAuthority()
    {
        MockAuthority authority = new MockAuthority();
        authority.setAuthority("authority-" + System.nanoTime());
        authority.setId(System.nanoTime());
        return authority;
    }
    /**
     * Adds the given authority to the test authority store.
     *
     * @param inAuthority an <code>Authority</code> value
     */
    protected void addAuthority(Authority inAuthority)
    {
        authoritiesByName.put(inAuthority.getName(),
                              inAuthority);
        authoritiesById.put(inAuthority.getId(),
                            inAuthority);
    }
    /**
     * Removes the given authority from the test authority store.
     *
     * @param inAuthority an <code>Authority</code> value
     */
    protected void removeAuthority(Authority inAuthority)
    {
        authoritiesByName.remove(inAuthority.getName());
        authoritiesById.remove(inAuthority.getId());
    }
    /**
     * Gets the authority with the given name from the test authority store.
     *
     * @param inName a <code>String</code> value
     * @return an <code>Authority</code> value or <code>null</code>
     */
    protected Authority getAuthorityByName(String inName)
    {
        return authoritiesByName.get(inName);
    }
    /**
     * Gets the authority with the given id from the test authority store.
     *
     * @param inId a <code>long</code> value
     * @return an <code>Authority</code> value
     */
    protected Authority getAuthorityById(long inId)
    {
        return authoritiesById.get(inId);
    }
    /**
     * Gets all authorities from the test authority store.
     *
     * @return a <code>Collection&lt;Authority&gt;</code> value
     */
    protected Collection<Authority> getAllAuthorities()
    {
        return authoritiesByName.values();
    }
    /**
     * authority DAO value
     */
    protected AuthorityDao authorityDao;
    /**
     * test authority values by name
     */
    protected Map<String,Authority> authoritiesByName;
    /**
     * test authority values by id
     */
    protected Map<Long,Authority> authoritiesById;
}
