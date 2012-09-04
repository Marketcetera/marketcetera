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
import org.marketcetera.api.dao.Permission;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.systemmodel.SystemmodelTestBase;

/* $License$ */

/**
 * Tests {@link PermissionManagerServiceImpl}.
 *
 * @version $Id$
 * @since $Release$
 */
public class PermissionManagerServiceImplTest
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
        permissionManagerService = new PermissionManagerServiceImpl();
        permissionManagerService.setPermissionDao(permissionDao);
    }
    /**
     * Tests {@link PermissionManagerServiceImpl#getPermissionByName(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetPermissionByName()
            throws Exception
    {
        // null value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                permissionManagerService.getPermissionByName(null);
            }
        };
        // empty value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                permissionManagerService.getPermissionByName("");
            }
        };
        // empty value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                permissionManagerService.getPermissionByName("    ");
            }
        };
        // no match
        Permission badPermission = generatePermission();
        assertNull(permissionDataStore.getByName(badPermission.getName()));
        assertEquals(null,
                     permissionManagerService.getPermissionByName(badPermission.getName()));
        verify(permissionDao).getByName(badPermission.getName());
        // good match
        Permission goodPermission = permissionDataStore.getAll().iterator().next();
        assertNotNull(goodPermission);
        assertSame(goodPermission,
                   permissionManagerService.getPermissionByName(goodPermission.getName()));
        verify(permissionDao).getByName(goodPermission.getName());
        verify(permissionDao,
               times(2)).getByName(anyString());
    }
    /**
     * Tests {@link PermissionManagerServiceImpl#addPermission(org.marketcetera.api.dao.Permission)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAddPermission()
            throws Exception
    {
        // null value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                permissionManagerService.addPermission(null);
            }
        };
        // non-null value
        Permission newPermission = generatePermission();
        permissionManagerService.addPermission(newPermission);
        verify(permissionDao).add(newPermission);
        // re-add same value
        permissionManagerService.addPermission(newPermission);
        verify(permissionDao,
               times(2)).add(newPermission);
        // add new, distinct value
        Permission anotherNewPermission = generatePermission();
        permissionManagerService.addPermission(anotherNewPermission);
        verify(permissionDao,
               times(3)).add((Permission)any());
        verify(permissionDao).add(anotherNewPermission);
    }
    /**
     * Tests {@link PermissionManagerServiceImpl#savePermission(org.marketcetera.api.dao.Permission)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testSavePermission()
            throws Exception
    {
        // null value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                permissionManagerService.savePermission(null);
            }
        };
        Permission newPermission = generatePermission();
        assertNull(permissionDataStore.getByName(newPermission.getName()));
        permissionManagerService.savePermission(newPermission);
        verify(permissionDao).save(newPermission);
        assertSame(newPermission,
                   permissionDataStore.getByName(newPermission.getName()));
    }
    /**
     * Tests {@link PermissionManagerServiceImpl#deletePermission(org.marketcetera.api.dao.Permission)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDeletePermission()
            throws Exception
    {
        // null value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                permissionManagerService.deletePermission(null);
            }
        };
        // delete non-existent permission
        Permission newPermission = generatePermission();
        assertNull(permissionDataStore.getByName(newPermission.getName()));
        permissionManagerService.deletePermission(newPermission);
        verify(permissionDao).delete(newPermission);
        assertNull(permissionDataStore.getByName(newPermission.getName()));
        // delete valid permission
        Permission goodPermission = permissionDataStore.getAll().iterator().next();
        assertNotNull(permissionDataStore.getByName(goodPermission.getName()));
        permissionManagerService.deletePermission(goodPermission);
        verify(permissionDao,
               times(2)).delete((Permission)any());
        assertNull(permissionDataStore.getByName(goodPermission.getName()));
    }
    /**
     * Tests {@link PermissionManagerServiceImpl#getPermissionById(long)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetPermissionById()
            throws Exception
    {
        // missing ids
        assertNull(permissionDataStore.getById(Long.MIN_VALUE));
        assertNull(permissionManagerService.getPermissionById(Long.MIN_VALUE));
        verify(permissionDao).getById(Long.MIN_VALUE);
        verify(permissionDao,
               times(1)).getById(anyLong());
        assertNull(permissionDataStore.getById(Long.MAX_VALUE));
        assertNull(permissionManagerService.getPermissionById(Long.MAX_VALUE));
        verify(permissionDao).getById(Long.MAX_VALUE);
        verify(permissionDao,
               times(2)).getById(anyLong());
        assertNull(permissionDataStore.getById(0));
        assertNull(permissionManagerService.getPermissionById(0));
        verify(permissionDao).getById(0);
        verify(permissionDao,
               times(3)).getById(anyLong());
        // existing id
        Permission goodPermission = permissionDataStore.getAll().iterator().next();
        assertSame(goodPermission,
                   permissionManagerService.getPermissionById(goodPermission.getId()));
        verify(permissionDao).getById(goodPermission.getId());
        verify(permissionDao,
               times(4)).getById(anyLong());
    }
    /**
     * Tests {@link PermissionManagerServiceImpl#getAllPermissions()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetAllPermissions()
            throws Exception
    {
        List<Permission> actualAllPermissions = permissionManagerService.getAllPermissions();
        verifyPermissions(permissionDataStore.getAll(), actualAllPermissions);
        verify(permissionDao).getAll();
    }
    /**
     * Verifies that the given actual value matches the given expected value.
     *
     * @param inExpectedPermission a <code>Collection&lt;Permission&gt;</code> value
     * @param inActualPermission a <code>Collection&lt;Permission&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyPermissions(Collection<Permission> inExpectedPermissions,
                                   Collection<Permission> inActualPermissions)
            throws Exception
    {
        assertEquals(inExpectedPermissions.size(),
                     inActualPermissions.size());
        Map<Long,Permission> expectedPermissions = new HashMap<Long,Permission>();
        Map<Long,Permission> actualPermissions = new HashMap<Long,Permission>();
        for(Permission user : inExpectedPermissions) {
            expectedPermissions.put(user.getId(),
                              user);
        }
        for(Permission user : inActualPermissions) {
            actualPermissions.put(user.getId(),
                            user);
        }
        assertEquals(expectedPermissions,
                     actualPermissions);
    }
    /**
     * test permission manager service object
     */
    private PermissionManagerServiceImpl permissionManagerService;
}
