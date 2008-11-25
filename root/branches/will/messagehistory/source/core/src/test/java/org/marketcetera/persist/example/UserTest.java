package org.marketcetera.persist.example;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.persist.*;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.*;


/* $License$ */
/**
 * Tests persistence of the User Entity.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class UserTest extends CorePersistNDTestBase<User, SummaryUser> {

    /* ************************Tests specific to User entity************* */

    /**
     * Test authorization failures and locking.
     * @throws Exception if there are errors
     */
    @Test
    public void authAndLocking() throws Exception {
        //Create a user
        User u = new User();
        u.setName(randomString());
        final String password = randomString();
        //Set the password
        assertTrue(u.changePassword(password.toCharArray(), null));
        assertSavedEntity(u);
        u = fetchByID(u.getId());
        //Verify initial state
        assertEquals(0, u.getNumFailedPasswordAttempts());
        assertFalse(u.isLocked());
        User fetched = fetchByID(u.getId());
        //Verify that unlock does nothing.
        u.unLock();
        assertEntityEquals(fetched, u);
        //Verify successful authentication
        u.authenticate(password.toCharArray());
        assertEquals(0, u.getNumFailedPasswordAttempts());
        //Now try failed authentication and verify that
        //the the count of number of failed attempts is
        //incremented.
        try {
            u.authenticate(null);
            fail();
        } catch (IllegalArgumentException expected) {
        }
        assertEquals(1, u.getNumFailedPasswordAttempts());
        assertEntityEquals(u, fetchByID(u.getId()));
        assertFalse(u.isLocked());
        try {
            u.authenticate("blah".toCharArray()); //$NON-NLS-1$
            fail();
        } catch (IllegalArgumentException expected) {
        }
        assertEquals(2, u.getNumFailedPasswordAttempts());
        assertFalse(u.isLocked());
        assertEntityEquals(u, fetchByID(u.getId()));
        try {
            u.authenticate("meh".toCharArray()); //$NON-NLS-1$
            fail();
        } catch (IllegalArgumentException expected) {
        }
        assertEquals(3, u.getNumFailedPasswordAttempts());
        assertTrue(u.isLocked());
        assertEntityEquals(u, fetchByID(u.getId()));
        //Verify that the count gets incremented
        //even after the user is locked and that
        //auth fails with the correct password
        try {
            u.authenticate(password.toCharArray());
            fail();
        } catch (IllegalStateException expected) {
        }
        assertEquals(4, u.getNumFailedPasswordAttempts());
        assertTrue(u.isLocked());
        assertEntityEquals(u, fetchByID(u.getId()));
        //Unlock the user and verify its state
        u.unLock();
        assertFalse(u.isLocked());
        assertEquals(0, u.getNumFailedPasswordAttempts());
        assertEntityEquals(u, fetchByID(u.getId()));
        //Verify that we can successfully authenticate
        u.authenticate(password.toCharArray());
        //Disable the user
        u.setEnabled(false);
        u.save();
        //Verify that auth fails
        try {
            u.authenticate(password.toCharArray());
            fail();
        } catch (IllegalStateException expected) {
        }
        assertFalse(u.isLocked());
        assertEquals(1, u.getNumFailedPasswordAttempts());
        assertEntityEquals(u, fetchByID(u.getId()));
        //Try 2 more times so that the user gets locked
        try {
            u.authenticate(password.toCharArray());
            fail();
        } catch (IllegalStateException expected) {
        }
        try {
            u.authenticate(password.toCharArray());
            fail();
        } catch (IllegalStateException expected) {
        }
        assertEquals(3, u.getNumFailedPasswordAttempts());
        assertTrue(u.isLocked());
        assertEntityEquals(u, fetchByID(u.getId()));
        //unlock the user and verify authenticate still fails
        u.unLock();
        assertEquals(0, u.getNumFailedPasswordAttempts());
        assertEntityEquals(u, fetchByID(u.getId()));
        assertFalse(u.isEnabled());
        try {
            u.authenticate(password.toCharArray());
            fail();
        } catch (IllegalStateException expected) {
        }
        assertFalse(u.isLocked());
        assertEquals(1, u.getNumFailedPasswordAttempts());
        assertEntityEquals(u, fetchByID(u.getId()));
        //Now enable the user and verify that you can login
        u.setEnabled(true);
        u.save();
        u.authenticate(password.toCharArray());
        assertEquals(0, u.getNumFailedPasswordAttempts());
        assertEntityEquals(u, fetchByID(u.getId()));
    }

    /**
     * Test change password behavior
     * @throws Exception if there are errors
     */
    @Test
    public void changePassword() throws Exception {
        User u = new User();
        u.setName(randomString());
        final String password = randomString();
        u.changePassword(password.toCharArray(),null);
        u = fetchByID(u.getId());
        User oldState = fetchByID(u.getId());
        u.authenticate(password.toCharArray());
        //Now try changing the password with some invalid values.
        assertFalse(u.changePassword(randomString().toCharArray(),null));
        assertFalse(u.changePassword(randomString().toCharArray(),"".toCharArray())); //$NON-NLS-1$
        assertFalse(u.changePassword(randomString().toCharArray(),randomString().toCharArray()));
        //Verify that none of this changes the object state
        assertEntityEquals(oldState, u);
        //Now change password with the correct old password
        String newPassword = randomString();
        assertTrue(u.changePassword(newPassword.toCharArray(), password.toCharArray()));
        u.authenticate(newPassword.toCharArray());
        fetchByID(u.getId()).authenticate(newPassword.toCharArray());
    }

    /**
     * Verify that the lifecycle of settings is correctly managed
     * @throws Exception if there are errors
     */
    @Test
    public void userSettings() throws Exception {
        User u = createFilled();
        u.save();
        assertEntityEquals(u, fetchByID(u.getId()));
        //save again with description change and
        //verify that settings get saved correctly
        u.setDescription(randomString());
        u.save();
        assertEntityEquals(u,fetchByID(u.getId()));
        //Just updated the setting values and verify that
        //they get updated correctly
        Map<String, String> s = u.getSettings();
        for(String str:s.keySet()) {
            s.put(str,randomString());
        }
        u.setSettings(s);
        u.save();
        assertEntityEquals(u,fetchByID(u.getId()));
        //Remove the settings and verify that they are removed correctly.
        u.setSettings(new HashMap<String, String>());
        u.save();
        assertEntityEquals(u,fetchByID(u.getId()));
        //Then add 'em back
        u.setSettings(s);
        u.save();
        assertEntityEquals(u,fetchByID(u.getId()));
        //Then remove them with a null value
        u.setSettings(null);
        u.save();
        //Since null value comes back as an empty map, set
        //an empty map in
        u.setSettings(new HashMap<String, String>());
        assertEntityEquals(u,fetchByID(u.getId()));
    }

/* ************************Implement necessary operations************* */

    protected void save(User user) throws Exception {
        user.save();
    }

    protected void delete(User user) throws Exception {
        user.delete();
    }

    protected void deleteAll() throws Exception {
        MultiUserQuery.all().delete();
    }

    protected User fetchByID(long id) throws Exception {
        return new SingleUserQuery(id).fetch();
    }

    protected SummaryUser fetchSummaryByID(long id) throws Exception {
        return new SingleUserQuery(id).fetchSummary();
    }

    protected User fetchByName(String name) throws Exception {
        return new SingleUserQuery(name).fetch();
    }

    protected SummaryUser fetchSummaryByName(String name) throws Exception {
        return new SingleUserQuery(name).fetchSummary();
    }

    protected List<SummaryUser> fetchSummaryQuery(
            MultipleEntityQuery query) throws Exception {
        return ((MultiUserQuery) query).fetchSummary();
    }

    protected List<User> fetchQuery(MultipleEntityQuery query)
            throws Exception {
        return ((MultiUserQuery) query).fetch();
    }

    protected MultipleEntityQuery getAllQuery() {
        return MultiUserQuery.all();
    }

    protected User createEmpty() {
        return new User();
    }

    protected boolean fetchExistsByName(String name) throws Exception {
        return new SingleUserQuery(name).exists();
    }

    protected boolean fetchExistsByID(long id) throws Exception {
        return new SingleUserQuery(id).exists();
    }

    protected Class<User> getEntityClass() {
        return User.class;
    }

    protected Class<? extends MultipleEntityQuery> getMultiQueryClass() {
        return MultiUserQuery.class;
    }

    /*
     * **********************Override necessary operations*************
     */

    @Override
    protected List<MultiQueryOrderTestHelper<User, SummaryUser>>
            getOrderTestHelpers() throws Exception {
        List<MultiQueryOrderTestHelper<User, SummaryUser>> l =
                super.getOrderTestHelpers();
        l.add(stringOrderHelper(MultiUserQuery.BY_EMPLOYEE_ID,
                User.ATTRIBUTE_EMPLOYEE_ID));
        l.add(stringOrderHelper(MultiUserQuery.BY_EMAIL,
                User.ATTRIBUTE_EMAIL));
        return l;
    }

    @Override
    protected List<MultiQueryFilterTestHelper<User, SummaryUser>>
            getFilterTestHelpers() throws Exception {
        List<MultiQueryFilterTestHelper<User, SummaryUser>> l =
                super.getFilterTestHelpers();
        l.add(stringFilterHelper(User.ATTRIBUTE_EMAIL, "emailFilter")); //$NON-NLS-1$
        l.add(stringFilterHelper(User.ATTRIBUTE_EMPLOYEE_ID,
                "employeeIDFilter")); //$NON-NLS-1$
        l.add(booleanFilterHelper(User.ATTRIBUTE_ENABLED, "enabledFilter")); //$NON-NLS-1$
        return l;
    }

    @Override
    protected User createFilled() throws Exception {
        User u = super.createFilled();
        u.setEmail(randomString() + "@" + randomString() + ".com"); //$NON-NLS-1$ //$NON-NLS-2$
        u.setEmployeeID(randomString());
        u.setEnabled(false);
        HashMap<String, String> m = new HashMap<String, String>();
        m.put(randomString(), randomString());
        m.put(randomString(), randomString());
        u.setSettings(m);
        return u;
    }

    @Override
    protected User createCopy(User src) throws Exception {
        User u = super.createCopy(src);
        u.setEmail(src.getEmail());
        u.setEmployeeID(src.getEmployeeID());
        u.setEnabled(src.isEnabled());
        u.setSettings(src.getSettings());
        return u;
    }

    @Override
    protected void assertDefaultValues(User user) {
        super.assertDefaultValues(user);
        assertNull(user.getGroups());
        assertNull(user.getSettings());
        assertNull(user.getEmail());
        assertNull(user.getEmployeeID());
        assertTrue(user.isEnabled());
        assertFalse(user.isLocked());
    }

    @Override
    protected void assertEntityEquals(User u1, User u2,
                                      boolean skipTimestamp) {
        super.assertEntityEquals(u1, u2, skipTimestamp);
        assertSummaryEquals(u1, u2);
        assertCollectionPermutation(u1.getGroups(), u2.getGroups());
        assertEquals(u1.getSettings(), u2.getSettings());
    }

    @Override
    protected void assertEntitySummaryEquals(User user,
                                             SummaryUser summaryUser) {
        super.assertEntitySummaryEquals(user, summaryUser);
        assertSummaryEquals(user, summaryUser);
    }

    private static void assertSummaryEquals(SummaryUser u1, SummaryUser u2) {
        assertEquals(u1.getEmail(), u2.getEmail());
        assertEquals(u1.getEmployeeID(), u2.getEmployeeID());
        assertEquals(u1.isEnabled(), u2.isEnabled());
        assertEquals(u1.isLocked(), u1.isLocked());
    }

    @Override
    protected void changeAttributes(User user) {
        super.changeAttributes(user);
        user.setEmail(randomString() + "@" + randomString() + ".com"); //$NON-NLS-1$ //$NON-NLS-2$
        user.setEmployeeID(randomString());
        HashMap<String, String> m = new HashMap<String, String>();
        m.put(randomString(), randomString());
        m.put(randomString(), randomString());
        user.setSettings(m);
    }

    @Override
    protected void assertQueryDefaults(MultipleEntityQuery q) {
        super.assertQueryDefaults(q);
        MultiUserQuery uq = (MultiUserQuery) q;
        assertNull(uq.getEmailFilter());
        assertNull(uq.getEmployeeIDFilter());
        assertNull(uq.getEnabledFilter());
    }

    @Override
    protected String getUserFriendlyName() {
        return Messages.NAME_USER.getText();
    }
}
