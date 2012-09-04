package org.marketcetera.core.systemmodel.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.api.dao.Role;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.systemmodel.SystemmodelTestBase;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/* $License$ */

/**
 * Tests {@link RoleManagerServiceImpl}.
 *
 * @version $Id$
 * @since $Release$
 */
public class RoleManagerServiceImplTest
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
        roleManagerService = new RoleManagerServiceImpl();
        roleManagerService.setRoleDao(roleDao);
    }
    /**
     * Tests {@link RoleManagerServiceImpl#getRoleByName(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetRoleByName()
            throws Exception
    {
        // null value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                roleManagerService.getRoleByName(null);
            }
        };
        // empty value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                roleManagerService.getRoleByName("");
            }
        };
        // empty value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                roleManagerService.getRoleByName("    ");
            }
        };
        // no match
        Role badRole = generateRole();
        assertNull(roleDataStore.getByName(badRole.getName()));
        assertEquals(null,
                     roleManagerService.getRoleByName(badRole.getName()));
        verify(roleDao).getByName(badRole.getName());
        // good match
        Role goodRole = roleDataStore.getAll().iterator().next();
        assertNotNull(goodRole);
        assertSame(goodRole,
                   roleManagerService.getRoleByName(goodRole.getName()));
        verify(roleDao).getByName(goodRole.getName());
        verify(roleDao,
               times(2)).getByName(anyString());
    }
    /**
     * Tests {@link RoleManagerServiceImpl#addRole(Role)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAddRole()
            throws Exception
    {
        // null value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                roleManagerService.addRole(null);
            }
        };
        // non-null value
        Role newRole = generateRole();
        roleManagerService.addRole(newRole);
        verify(roleDao).add(newRole);
        // re-add same value
        roleManagerService.addRole(newRole);
        verify(roleDao,
               times(2)).add(newRole);
        // add new, distinct value
        Role anotherNewRole = generateRole();
        roleManagerService.addRole(anotherNewRole);
        verify(roleDao,
               times(3)).add((Role)any());
        verify(roleDao).add(anotherNewRole);
    }
    /**
     * Tests {@link RoleManagerServiceImpl#saveRole(Role)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testSaveRole()
            throws Exception
    {
        // null value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                roleManagerService.saveRole(null);
            }
        };
        Role newRole = generateRole();
        assertNull(roleDataStore.getByName(newRole.getName()));
        roleManagerService.saveRole(newRole);
        verify(roleDao).save(newRole);
        assertSame(newRole,
                   roleDataStore.getByName(newRole.getName()));
    }
    /**
     * Tests {@link RoleManagerServiceImpl#deleteRole(Role)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDeleteRole()
            throws Exception
    {
        // null value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                roleManagerService.deleteRole(null);
            }
        };
        // delete non-existent role
        Role newRole = generateRole();
        assertNull(roleDataStore.getByName(newRole.getName()));
        roleManagerService.deleteRole(newRole);
        verify(roleDao).delete(newRole);
        assertNull(roleDataStore.getByName(newRole.getName()));
        // delete valid role
        Role goodRole = roleDataStore.getAll().iterator().next();
        assertNotNull(roleDataStore.getByName(goodRole.getName()));
        roleManagerService.deleteRole(goodRole);
        verify(roleDao,
               times(2)).delete((Role)any());
        assertNull(roleDataStore.getByName(goodRole.getName()));
    }
    /**
     * Tests {@link RoleManagerServiceImpl#getRoleById(long)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetRoleById()
            throws Exception
    {
        // missing ids
        assertNull(roleDataStore.getById(Long.MIN_VALUE));
        assertNull(roleManagerService.getRoleById(Long.MIN_VALUE));
        verify(roleDao).getById(Long.MIN_VALUE);
        verify(roleDao,
               times(1)).getById(anyLong());
        assertNull(roleDataStore.getById(Long.MAX_VALUE));
        assertNull(roleManagerService.getRoleById(Long.MAX_VALUE));
        verify(roleDao).getById(Long.MAX_VALUE);
        verify(roleDao,
               times(2)).getById(anyLong());
        assertNull(roleDataStore.getById(0));
        assertNull(roleManagerService.getRoleById(0));
        verify(roleDao).getById(0);
        verify(roleDao,
               times(3)).getById(anyLong());
        // existing id
        Role goodRole = roleDataStore.getAll().iterator().next();
        assertSame(goodRole,
                   roleManagerService.getRoleById(goodRole.getId()));
        verify(roleDao).getById(goodRole.getId());
        verify(roleDao,
               times(4)).getById(anyLong());
    }
    /**
     * Tests {@link RoleManagerServiceImpl#getAllRoles()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetAllRoles()
            throws Exception
    {
        List<Role> actualAllRoles = roleManagerService.getAllRoles();
        verifyRoles(roleDataStore.getAll(),
                     actualAllRoles);
        verify(roleDao).getAll();
    }
    /**
     * Verifies that the given actual value matches the given expected value.
     *
     * @param inActualRoles a <code>Collection&lt;Role&gt;</code> value
     * @param inExpectedRoles a <code>Collection&lt;Role&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyRoles(Collection<Role> inExpectedRoles,
                              Collection<Role> inActualRoles)
            throws Exception
    {
        assertEquals(inExpectedRoles.size(),
                     inActualRoles.size());
        Map<Long,Role> expectedRoles = new HashMap<Long,Role>();
        Map<Long,Role> actualRoles = new HashMap<Long,Role>();
        for(Role user : inExpectedRoles) {
            expectedRoles.put(user.getId(),
                              user);
        }
        for(Role user : inActualRoles) {
            actualRoles.put(user.getId(),
                            user);
        }
        assertEquals(expectedRoles,
                     actualRoles);
    }
    /**
     * test role manager service object
     */
    private RoleManagerServiceImpl roleManagerService;
}
