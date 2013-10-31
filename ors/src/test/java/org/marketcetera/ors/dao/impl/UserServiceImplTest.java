package org.marketcetera.ors.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.ors.PersistTestBase;
import org.marketcetera.ors.security.SimpleUser;
import org.springframework.dao.DataIntegrityViolationException;

/* $License$ */

/**
 * Tests {@link UserServiceImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class UserServiceImplTest
        extends PersistTestBase
{
    /**
     * Tests {@link UserServiceImpl#save(SimpleUser)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testSave()
            throws Exception
    {
        List<SimpleUser> allUsers;
        allUsers = userService.findAll();
        for(SimpleUser user : allUsers) {
            userService.delete(user);
        }
        allUsers = userService.findAll();
        assertTrue(allUsers.isEmpty());
        SimpleUser user = generateUser();
        // save a new user
        user = userService.save(user);
        allUsers = userService.findAll();
        assertFalse(allUsers.isEmpty());
        assertEquals(1,
                     allUsers.size());
        assertEquals(user,
                     allUsers.get(0));
        // update the same user and save, no new record
        String newName = randomString();
        assertFalse(newName.equals(user.getName()));
        user.setName(newName);
        // need to set a new password
        user.setPassword(randomString().toCharArray());
        userService.save(user);
        allUsers = userService.findAll();
        assertFalse(allUsers.isEmpty());
        assertEquals(1,
                     allUsers.size());
        assertEquals(user,
                     allUsers.get(0));
        // create a new user
        SimpleUser newUser = generateUser();
        assertFalse(user.equals(newUser));
        newUser = userService.save(newUser);
        allUsers = userService.findAll();
        assertFalse(allUsers.isEmpty());
        assertEquals(2,
                     allUsers.size());
        assertTrue(allUsers.contains(user));
        assertTrue(allUsers.contains(newUser));
        // disallow same-names
        final SimpleUser badUser = generateUser();
        badUser.setName(user.getName());
        badUser.setPassword(randomString().toCharArray());
        new ExpectedFailure<DataIntegrityViolationException>() {
            @Override
            protected void run()
                    throws Exception
            {
                userService.save(badUser);
            }
        };
    }
    /**
     * Tests {@link UserServiceImpl#delete(SimpleUser)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDelete()
            throws Exception
    {
        List<SimpleUser> allUsers = userService.findAll();
        for(SimpleUser user : allUsers) {
            userService.delete(user);
        }
        allUsers = userService.findAll();
        assertTrue(allUsers.isEmpty());
        // delete a user that doesn't exist
        SimpleUser user = generateUser();
        userService.delete(user); // doesn't do anything
        allUsers = userService.findAll();
        assertTrue(allUsers.isEmpty());
        user = userService.save(user);
        allUsers = userService.findAll();
        assertEquals(1,
                     allUsers.size());
        assertTrue(allUsers.contains(user));
        userService.delete(user);
        allUsers = userService.findAll();
        assertTrue(allUsers.isEmpty());
        userService.delete(user);
    }
    /**
     * Tests {@link UserServiceImpl#listUsers(String, Boolean)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testListUsers()
            throws Exception
    {
        List<SimpleUser> allUsers = userService.findAll();
        for(SimpleUser user : allUsers) {
            userService.delete(user);
        }
        allUsers = userService.findAll();
        List<SimpleUser> users = userService.listUsers(null,
                                                       null);
        assertTrue(users.isEmpty());
        SimpleUser user1 = generateUser();
        assertTrue(user1.isActive());
        SimpleUser user2 = generateUser();
        user2.setActive(false);
        SimpleUser user3 = generateUser();
        user1 = userService.save(user1);
        user2 = userService.save(user2);
        user3 = userService.save(user3);
        users = userService.listUsers(user1.getName(),
                                      null);
        assertEquals(1,
                     users.size());
        assertTrue(users.contains(user1));
        users = userService.listUsers(user1.getName(),
                                      true);
        assertEquals(1,
                     users.size());
        assertTrue(users.contains(user1));
        users = userService.listUsers(user1.getName(),
                                      false);
        assertTrue(users.isEmpty());
        users = userService.listUsers(null,
                                      false);
        assertEquals(1,
                     users.size());
        assertTrue(users.contains(user2));
        users = userService.listUsers(null,
                                      true);
        assertEquals(2,
                     users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user3));
        users = userService.listUsers(null,
                                      null);
        assertEquals(3,
                     users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
        assertTrue(users.contains(user3));
    }
    /**
     * Tests {@link UserServiceImpl#findByName(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testFindByName()
            throws Exception
    {
        SimpleUser user1 = generateUser();
        SimpleUser user2 = generateUser();
        assertFalse(user1.getName().equals(user2.getName()));
        assertNull(userService.findByName(user1.getName()));
        assertNull(userService.findByName(user2.getName()));
        user1 = userService.save(user1);
        assertNull(userService.findByName(user2.getName()));
        assertNull(userService.findByName(null));
    }
    /**
     * Tests {@link UserServiceImpl#updateUserDataByName(String, String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testUpdateUserDataByName()
            throws Exception
    {
        userService.updateUserDataByName(null,
                                         null);
        userService.updateUserDataByName(randomNameString(),
                                         null);
        SimpleUser user1 = generateUser();
        assertNull(user1.getUserData());
        user1 = userService.save(user1);
        assertNull(user1.getUserData());
        userService.updateUserDataByName(user1.getName(),
                                         null);
        // re-fetch
        user1 = userService.findOne(user1.getId());
        assertNull(user1.getUserData());
        String newData = randomString();
        userService.updateUserDataByName(user1.getName(),
                                         newData);
        // re-fetch
        user1 = userService.findOne(user1.getId());
        assertEquals(newData,
                     user1.getUserData());
        userService.updateUserDataByName(user1.getName(),
                                         null);
        user1 = userService.findOne(user1.getId());
        assertNull(user1.getUserData());
    }
    /**
     * Tests {@link UserServiceImpl#updateUserActiveStatus(String, boolean)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testUpdateUserActiveStatus()
            throws Exception
    {
        userService.updateUserActiveStatus(null,
                                           false);
        userService.updateUserActiveStatus(randomNameString(),
                                           false);
        SimpleUser user1 = generateUser();
        assertTrue(user1.isActive());
        user1 = userService.save(user1);
        assertTrue(user1.isActive());
        user1 = userService.findOne(user1.getId());
        assertTrue(user1.isActive());
        userService.updateUserActiveStatus(user1.getName(),
                                           false);
        user1 = userService.findOne(user1.getId());
        assertFalse(user1.isActive());
        userService.updateUserActiveStatus(user1.getName(),
                                           true);
        user1 = userService.findOne(user1.getId());
        assertTrue(user1.isActive());
    }
    /**
     * Tests {@link UserServiceImpl#updateSuperUser(String, boolean)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testUpdateSuperUser()
            throws Exception
    {
        userService.updateSuperUser(null,
                                    false);
        userService.updateSuperUser(randomNameString(),
                                    false);
        SimpleUser user1 = generateUser();
        assertFalse(user1.isSuperuser());
        user1 = userService.save(user1);
        assertFalse(user1.isSuperuser());
        user1 = userService.findOne(user1.getId());
        assertFalse(user1.isSuperuser());
        userService.updateSuperUser(user1.getName(),
                                    true);
        user1 = userService.findOne(user1.getId());
        assertTrue(user1.isSuperuser());
        userService.updateSuperUser(user1.getName(),
                                    false);
        user1 = userService.findOne(user1.getId());
        assertFalse(user1.isSuperuser());
    }
}
