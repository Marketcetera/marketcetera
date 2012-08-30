package org.marketcetera.core.systemmodel.impl;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.systemmodel.Group;
import org.marketcetera.core.systemmodel.SystemmodelTestBase;

/* $License$ */

/**
 * Tests {@link GroupManagerServiceImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class GroupManagerServiceImplTest
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
        groupManagerService = new GroupManagerServiceImpl();
        groupManagerService.setGroupDao(groupDao);
    }
    /**
     * Tests {@link GroupManagerServiceImpl#getGroupByName(String)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetGroupByName()
            throws Exception
    {
        // null value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                groupManagerService.getGroupByName(null);
            }
        };
        // empty value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                groupManagerService.getGroupByName("");
            }
        };
        // empty value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                groupManagerService.getGroupByName("    ");
            }
        };
        // no match
        Group badGroup = generateGroup();
        assertNull(getGroupByName(badGroup.getName()));
        assertEquals(null,
                     groupManagerService.getGroupByName(badGroup.getName()));
        verify(groupDao).getByName(badGroup.getName());
        // good match
        Group goodGroup = getAllGroups().iterator().next();
        assertNotNull(goodGroup);
        assertSame(goodGroup,
                   groupManagerService.getGroupByName(goodGroup.getName()));
        verify(groupDao).getByName(goodGroup.getName());
        verify(groupDao,
               times(2)).getByName(anyString());
    }
    /**
     * Tests {@link GroupManagerServiceImpl#addGroup(Group)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAddGroup()
            throws Exception
    {
        // null value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                groupManagerService.addGroup(null);
            }
        };
        // non-null value
        Group newGroup = generateGroup();
        groupManagerService.addGroup(newGroup);
        verify(groupDao).add(newGroup);
        // re-add same value
        groupManagerService.addGroup(newGroup);
        verify(groupDao,
               times(2)).add(newGroup);
        // add new, distinct value
        Group anotherNewGroup = generateGroup();
        groupManagerService.addGroup(anotherNewGroup);
        verify(groupDao,
               times(3)).add((Group)any());
        verify(groupDao).add(anotherNewGroup);
    }
    /**
     * Tests {@link GroupManagerServiceImpl#saveGroup(Group)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testSaveGroup()
            throws Exception
    {
        // null value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                groupManagerService.saveGroup(null);
            }
        };
        Group newGroup = generateGroup();
        assertFalse(groupsByName.containsKey(newGroup.getName()));
        groupManagerService.saveGroup(newGroup);
        verify(groupDao).save(newGroup);
        assertSame(newGroup,
                   groupsByName.get(newGroup.getName()));
    }
    /**
     * Tests {@link GroupManagerServiceImpl#deleteGroup(Group)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDeleteGroup()
            throws Exception
    {
        // null value
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                groupManagerService.deleteGroup(null);
            }
        };
        // delete non-existent group
        Group newGroup = generateGroup();
        assertFalse(groupsByName.containsKey(newGroup.getName()));
        groupManagerService.deleteGroup(newGroup);
        verify(groupDao).delete(newGroup);
        assertFalse(groupsByName.containsKey(newGroup.getName()));
        // delete valid group
        Group goodGroup = groupsByName.values().iterator().next();
        assertTrue(groupsByName.containsKey(goodGroup.getName()));
        groupManagerService.deleteGroup(goodGroup);
        verify(groupDao,
               times(2)).delete((Group)any());
        assertFalse(groupsByName.containsKey(goodGroup.getName()));
    }
    /**
     * Tests {@link GroupManagerServiceImpl#getGroupById(long)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetGroupById()
            throws Exception
    {
        // missing ids
        assertNull(groupsById.get(Long.MIN_VALUE));
        assertNull(groupManagerService.getGroupById(Long.MIN_VALUE));
        verify(groupDao).getById(Long.MIN_VALUE);
        verify(groupDao,
               times(1)).getById(anyLong());
        assertNull(groupsById.get(Long.MAX_VALUE));
        assertNull(groupManagerService.getGroupById(Long.MAX_VALUE));
        verify(groupDao).getById(Long.MAX_VALUE);
        verify(groupDao,
               times(2)).getById(anyLong());
        assertNull(groupsById.get(0));
        assertNull(groupManagerService.getGroupById(0));
        verify(groupDao).getById(0);
        verify(groupDao,
               times(3)).getById(anyLong());
        // existing id
        Group goodGroup = groupsByName.values().iterator().next();
        assertSame(goodGroup,
                   groupManagerService.getGroupById(goodGroup.getId()));
        verify(groupDao).getById(goodGroup.getId());
        verify(groupDao,
               times(4)).getById(anyLong());
    }
    /**
     * Tests {@link GroupManagerServiceImpl#getAllGroups()}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetAllGroups()
            throws Exception
    {
        List<Group> actualAllGroups = groupManagerService.getAllGroups();
        Map<Long,Group> actualGroupsById = new HashMap<Long,Group>();
        for(Group group : actualAllGroups) {
            actualGroupsById.put(group.getId(),
                                group);
        }
        assertEquals(groupsById,
                     actualGroupsById);
        verify(groupDao).getAll();
    }
    /**
     * test group manager service object
     */
    private GroupManagerServiceImpl groupManagerService;
}
