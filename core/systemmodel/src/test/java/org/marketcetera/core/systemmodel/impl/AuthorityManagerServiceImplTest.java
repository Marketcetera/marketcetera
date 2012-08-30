package org.marketcetera.core.systemmodel.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.systemmodel.Authority;
import org.marketcetera.core.systemmodel.SystemmodelTestBase;

/* $License$ */

/**
 * Tests {@link AuthorityManagerServiceImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AuthorityManagerServiceImplTest
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
        authorityManagerService = new AuthorityManagerServiceImpl();
        authorityManagerService.setAuthorityDao(authorityDao);
    }
    /**
     * Tests {@link AuthorityManagerServiceImpl#getAuthorityByName(String)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetAuthorityByName()
            throws Exception
    {
        // null value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                authorityManagerService.getAuthorityByName(null);
            }
        };
        // empty value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                authorityManagerService.getAuthorityByName("");
            }
        };
        // empty value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                authorityManagerService.getAuthorityByName("    ");
            }
        };
        // no match
        Authority badAuthority = generateAuthority();
        assertNull(authorityDataStore.getByName(badAuthority.getName()));
        assertEquals(null,
                     authorityManagerService.getAuthorityByName(badAuthority.getName()));
        verify(authorityDao).getByName(badAuthority.getName());
        // good match
        Authority goodAuthority = authorityDataStore.getAll().iterator().next();
        assertNotNull(goodAuthority);
        assertSame(goodAuthority,
                   authorityManagerService.getAuthorityByName(goodAuthority.getName()));
        verify(authorityDao).getByName(goodAuthority.getName());
        verify(authorityDao,
               times(2)).getByName(anyString());
    }
    /**
     * Tests {@link AuthorityManagerServiceImpl#addAuthority(Authority)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAddAuthority()
            throws Exception
    {
        // null value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                authorityManagerService.addAuthority(null);
            }
        };
        // non-null value
        Authority newAuthority = generateAuthority();
        authorityManagerService.addAuthority(newAuthority);
        verify(authorityDao).add(newAuthority);
        // re-add same value
        authorityManagerService.addAuthority(newAuthority);
        verify(authorityDao,
               times(2)).add(newAuthority);
        // add new, distinct value
        Authority anotherNewAuthority = generateAuthority();
        authorityManagerService.addAuthority(anotherNewAuthority);
        verify(authorityDao,
               times(3)).add((Authority)any());
        verify(authorityDao).add(anotherNewAuthority);
    }
    /**
     * Tests {@link AuthorityManagerServiceImpl#saveAuthority(Authority)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testSaveAuthority()
            throws Exception
    {
        // null value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                authorityManagerService.saveAuthority(null);
            }
        };
        Authority newAuthority = generateAuthority();
        assertNull(authorityDataStore.getByName(newAuthority.getName()));
        authorityManagerService.saveAuthority(newAuthority);
        verify(authorityDao).save(newAuthority);
        assertSame(newAuthority,
                   authorityDataStore.getByName(newAuthority.getName()));
    }
    /**
     * Tests {@link AuthorityManagerServiceImpl#deleteAuthority(Authority)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDeleteAuthority()
            throws Exception
    {
        // null value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                authorityManagerService.deleteAuthority(null);
            }
        };
        // delete non-existent authority
        Authority newAuthority = generateAuthority();
        assertNull(authorityDataStore.getByName(newAuthority.getName()));
        authorityManagerService.deleteAuthority(newAuthority);
        verify(authorityDao).delete(newAuthority);
        assertNull(authorityDataStore.getByName(newAuthority.getName()));
        // delete valid authority
        Authority goodAuthority = authorityDataStore.getAll().iterator().next();
        assertNotNull(authorityDataStore.getByName(goodAuthority.getName()));
        authorityManagerService.deleteAuthority(goodAuthority);
        verify(authorityDao,
               times(2)).delete((Authority)any());
        assertNull(authorityDataStore.getByName(goodAuthority.getName()));
    }
    /**
     * Tests {@link AuthorityManagerServiceImpl#getAuthorityById(long)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetAuthorityById()
            throws Exception
    {
        // missing ids
        assertNull(authorityDataStore.getById(Long.MIN_VALUE));
        assertNull(authorityManagerService.getAuthorityById(Long.MIN_VALUE));
        verify(authorityDao).getById(Long.MIN_VALUE);
        verify(authorityDao,
               times(1)).getById(anyLong());
        assertNull(authorityDataStore.getById(Long.MAX_VALUE));
        assertNull(authorityManagerService.getAuthorityById(Long.MAX_VALUE));
        verify(authorityDao).getById(Long.MAX_VALUE);
        verify(authorityDao,
               times(2)).getById(anyLong());
        assertNull(authorityDataStore.getById(0));
        assertNull(authorityManagerService.getAuthorityById(0));
        verify(authorityDao).getById(0);
        verify(authorityDao,
               times(3)).getById(anyLong());
        // existing id
        Authority goodAuthority = authorityDataStore.getAll().iterator().next();
        assertSame(goodAuthority,
                   authorityManagerService.getAuthorityById(goodAuthority.getId()));
        verify(authorityDao).getById(goodAuthority.getId());
        verify(authorityDao,
               times(4)).getById(anyLong());
    }
    /**
     * Tests {@link AuthorityManagerServiceImpl#getAllAuthorities()}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetAllAuthorities()
            throws Exception
    {
        List<Authority> actualAllAuthorities = authorityManagerService.getAllAuthorities();
        verifyAuthorities(authorityDataStore.getAll(),
                          actualAllAuthorities);
        verify(authorityDao).getAll();
    }
    /**
     * Verifies that the given actual value matches the given expected value.
     *
     * @param inExpectedAuthority a <code>Collection&lt;Authority&gt;</code> value
     * @param inActualAuthority a <code>Collection&lt;Authority&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyAuthorities(Collection<Authority> inExpectedAuthorities,
                                   Collection<Authority> inActualAuthorities)
            throws Exception
    {
        assertEquals(inExpectedAuthorities.size(),
                     inActualAuthorities.size());
        Map<Long,Authority> expectedAuthoritys = new HashMap<Long,Authority>();
        Map<Long,Authority> actualAuthoritys = new HashMap<Long,Authority>();
        for(Authority user : inExpectedAuthorities) {
            expectedAuthoritys.put(user.getId(),
                              user);
        }
        for(Authority user : inActualAuthorities) {
            actualAuthoritys.put(user.getId(),
                            user);
        }
        assertEquals(expectedAuthoritys,
                     actualAuthoritys);
    }
    /**
     * test authority manager service object
     */
    private AuthorityManagerServiceImpl authorityManagerService;
}
