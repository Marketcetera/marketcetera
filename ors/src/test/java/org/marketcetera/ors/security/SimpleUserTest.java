package org.marketcetera.ors.security;

import static org.junit.Assert.*;
import static org.marketcetera.ors.security.Messages.CANNOT_SET_PASSWORD;
import static org.marketcetera.ors.security.Messages.EMPTY_PASSWORD;
import static org.marketcetera.ors.security.Messages.INVALID_PASSWORD;
import static org.marketcetera.persist.Messages.UNSPECIFIED_NAME_ATTRIBUTE;

import java.util.List;
import java.util.Properties;

import javax.persistence.PersistenceException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.Util;
import org.marketcetera.persist.MultiQueryFilterTestHelper;
import org.marketcetera.persist.MultipleEntityQuery;
import org.marketcetera.persist.NDEntityTestBase;
import org.marketcetera.util.log.I18NMessage;

/* $License$ */
/**
 * Unit Test to verify SimpleUser
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class SimpleUserTest extends NDEntityTestBase<SimpleUser,SimpleUser> {
    /**
     * Validates the update behavior of name and password
     *
     * @throws Exception if there was an error
     */
    @Test
    public void nameAndPassword() throws Exception {
        SimpleUser u = new SimpleUser();
        //run tests on an unsaved user
        doNamePasswordTests(u);
        //verify that user can be saved after password has been set
        u.validate();
        u.save();
        assertTrue(u.isPasswordSet());
        //reset name to empty
        u.setName(null);
        //run tests on a saved user
        doNamePasswordTests(u);
        //verify that user can be updated after password has been set
        u.validate();
        u.save();
        u.delete();
    }
    /**
     * Tests the behavior of user properties.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void userProperties()
            throws Exception
    {
        // null user data
        SimpleUser u = new SimpleUser();
        final String name = "colin-" + System.nanoTime();
        u.setName(name);
        u.setPassword("password".toCharArray());
        u.setUserData((String)null);
        assertNull(u.getUserData());
        u.validate();
        u.save();
        u = fetchByName(name);
        assertNull(u.getUserData());
        u.delete();
        // empty userdata
        u = new SimpleUser();
        u.setName(name);
        u.setPassword("password".toCharArray());
        u.setUserData("");
        assertEquals("",
                     u.getUserData());
        u.validate();
        u.save();
        u = fetchByName(name);
        assertEquals("",
                     u.getUserData());
        u.delete();
        // non-empty userdata
        Properties properties = new Properties();
        properties.setProperty("key1",
                               "value1");
        properties.setProperty("key2",
                               "value2");
        u = new SimpleUser();
        u.setName(name);
        u.setPassword("password".toCharArray());
        u.setUserData(Util.propertiesToString(properties));
        assertEquals(properties,
                     Util.propertiesFromString(u.getUserData()));
        u.validate();
        u.save();
        u = fetchByName(name);
        u.delete();
    }
    /**
     * Validates the update behavior of the superuser flag
     *
     * @throws Exception if there was an error
     */
    @Test
    public void superuser()
        throws Exception
    {
        SimpleUser u=new SimpleUser();
        final String name="TESTUSER";
        u.setName(name);

        assertFalse(u.isSuperuser());
        u.setSuperuser(true);
        assertTrue(u.isSuperuser());

        save(u);
        u=fetchByName(name);
        assertTrue(u.isSuperuser());

        u.setSuperuser(false);
        save(u);
        u=fetchByName(name);
        assertFalse(u.isSuperuser());

        u.delete();
    }

    /**
     * Validates the update behavior of the active flag
     *
     * @throws Exception if there was an error
     */
    @Test
    public void active()
        throws Exception
    {
        SimpleUser u=new SimpleUser();
        final String name="TESTUSER";
        u.setName(name);

        assertTrue(u.isActive());
        u.setActive(false);
        assertFalse(u.isActive());

        save(u);
        u=fetchByName(name);
        assertFalse(u.isActive());

        u.setActive(true);
        save(u);
        u=fetchByName(name);
        assertTrue(u.isActive());

        u.delete();
    }

    private void doNamePasswordTests(SimpleUser u) throws Exception {
        //verify that user cannot be saved without a user name
        assertValidateAndSaveFailure(u,UNSPECIFIED_NAME_ATTRIBUTE);
        //verify that the password cannot be set when the name is not set
        try {
            u.setPassword(randomString().toCharArray());
        } catch(PersistenceException e) {
            assertEquals(UNSPECIFIED_NAME_ATTRIBUTE.getText(),e.getMessage());
        }
        u.setName(randomString());
        assertFalse(u.isPasswordSet());
        //verify that the password is being validated
        assertValidateAndSaveFailure(u,EMPTY_PASSWORD);
        char[] password = randomString().toCharArray();
        u.setPassword(password);
        assertTrue(u.isPasswordSet());
        //verify that password set fails if the current password is non-empty
        try {
            u.setPassword(randomString().toCharArray());
        } catch(PersistenceException ex) {
            assertEquals(CANNOT_SET_PASSWORD.getText(password),
                    ex.getMessage());
        }
        //verify that password gets reset, when the user name is reset
        u.setName(randomString());
        assertFalse(u.isPasswordSet());
        assertValidateAndSaveFailure(u,EMPTY_PASSWORD);
        u.setPassword(randomString().toCharArray());
        assertTrue(u.isPasswordSet());
    }

    /**
     * Verifies password validate and change behavior
     * 
     * @throws Exception if there was an error
     */
    @Test
    public void validateAndChangePassword() throws Exception {
        SimpleUser u = new SimpleUser();
        //any password validates until a non-empty password is set
        u.validatePassword(null);
        u.validatePassword("".toCharArray()); //$NON-NLS-1$
        u.validatePassword(randomString().toCharArray());
        //run tests on an unsaved user
        char [] pass = doValidateChangePassTests(u);
        u.save();
        //verify that the password still validates
        u.validatePassword(pass);
        //verify db copy is the same
        SimpleUser fetched = fetchByID(u.getId());
        assertEntityEquals(u, fetched);
        fetched.validatePassword(pass);
        //run tests on a saved user
        pass = doValidateChangePassTests(u);
        //save changes
        u.save();
        //verify that the password still validates
        u.validatePassword(pass);
        fetched = fetchByID(u.getId());
        assertEntityEquals(u, fetched);
        fetched.validatePassword(pass);
    }

    private char[] doValidateChangePassTests(SimpleUser u) throws PersistenceException {
        final String pass = randomString();
        u.setName(randomString());
        //any password validates because changing a name resets the password
        u.validatePassword(null);
        u.validatePassword("".toCharArray()); //$NON-NLS-1$
        u.validatePassword(pass.toCharArray());

        u.setPassword(pass.toCharArray());
        //an empty password value is invalid
        assertPasswordValidateFailure(u, null, EMPTY_PASSWORD);
        assertPasswordValidateFailure(u, "".toCharArray(), EMPTY_PASSWORD); //$NON-NLS-1$
        //cannot validate or change password when supplying an incorrect password
        assertPasswordValidateFailure(u, randomString().toCharArray(),
                INVALID_PASSWORD);
        assertChangePasswordFailure(u, randomString().toCharArray(),
                randomString().toCharArray(), INVALID_PASSWORD);
        //verify that the correct password validates
        u.validatePassword(pass.toCharArray());
        //now try changing the password supplying an empty new password
        assertChangePasswordFailure(u, pass.toCharArray(), null,
                EMPTY_PASSWORD);
        assertChangePasswordFailure(u, pass.toCharArray(), "".toCharArray(), //$NON-NLS-1$
                EMPTY_PASSWORD);
        //verify that change password succeeds with proper old/new passwords
        final char[] newPass = randomString().toCharArray();
        u.changePassword(pass.toCharArray(), newPass);
        //verify that it validates for good measure
        u.validatePassword(newPass);
        //changing the name clears the password...
        u.setName(randomString());
        //... making it possible to set the new password while
        //supplying anything for the old password.
        u.changePassword(randomString().toCharArray(), newPass);
        //verify that it validates for good measure
        u.validatePassword(newPass);
        return newPass;
    }

    private static void assertPasswordValidateFailure(SimpleUser u,
                                                      char []password,
                                                      I18NMessage msg) {
        try {
            u.validatePassword(password);
            fail("Password validation should fail"); //$NON-NLS-1$
        } catch (PersistenceException e) {
            assertEquals(msg,e.getMessage());
        }
    }
    private static void assertChangePasswordFailure(SimpleUser u,
                                                    char[] oldPassword,
                                                    char[] newPassword,
                                                    I18NMessage msg) {
        try {
            u.changePassword(oldPassword, newPassword);
            fail("Password validation should fail"); //$NON-NLS-1$
        } catch (PersistenceException e) {
            assertEquals(msg,e.getMessage());
        }
    }
    private static void assertValidateAndSaveFailure(
            SimpleUser u, I18NMessage expectedMsg,
            Object ...params)
            throws Exception {
        try {
            u.validate();
            fail("Validation should fail"); //$NON-NLS-1$
        } catch(PersistenceException ex) {
            assertEquals(expectedMsg,
                    ex.getMessage());
        }
        try {
            u.save();
            fail("Save should fail"); //$NON-NLS-1$
        } catch(PersistenceException ex) {
            assertEquals(expectedMsg,
                    ex.getMessage());
        }
    }

    /* *****Implement necessary methods****** */

    protected SimpleUser fetchByName(String name) throws Exception {
        return new SingleSimpleUserQuery(name).fetch();
    }

    protected boolean fetchExistsByName(String name) throws Exception {
        return new SingleSimpleUserQuery(name).exists();
    }

    protected SimpleUser fetchSummaryByName(String name) throws Exception {
        return new SingleSimpleUserQuery(name).fetchSummary();
    }

    protected void save(SimpleUser simpleUser) throws Exception {
        //Set the password to avoid validation failure when saving
        if(!simpleUser.isPasswordSet()) {
            simpleUser.setPassword(randomString().toCharArray());
        }
        simpleUser.save();
    }

    protected void delete(SimpleUser simpleUser) throws Exception {
        simpleUser.delete();
    }

    protected void deleteAll() throws Exception {
        MultiSimpleUserQuery.all().delete();
    }

    protected SimpleUser fetchByID(long id) throws Exception {
        return new SingleSimpleUserQuery(id).fetch();
    }

    protected boolean fetchExistsByID(long id) throws Exception {
        return new SingleSimpleUserQuery(id).exists();
    }

    protected SimpleUser fetchSummaryByID(long id) throws Exception {
        return new SingleSimpleUserQuery(id).fetchSummary();
    }

    protected List<SimpleUser> fetchSummaryQuery(MultipleEntityQuery query) throws Exception {
        return ((MultiSimpleUserQuery)query).fetch();
    }

    protected List<SimpleUser> fetchQuery(MultipleEntityQuery query) throws Exception {
        return ((MultiSimpleUserQuery)query).fetch();
    }

    protected MultipleEntityQuery getAllQuery() throws Exception {
        return MultiSimpleUserQuery.all();
    }

    protected SimpleUser createEmpty() throws Exception {
        return new SimpleUser();
    }

    protected Class<SimpleUser> getEntityClass() {
        return SimpleUser.class;
    }

    protected Class<? extends MultipleEntityQuery> getMultiQueryClass() {
        return MultiSimpleUserQuery.class;
    }
    /* *****Over-ride necessary methods****** */

    @Override
    protected List<MultiQueryFilterTestHelper<SimpleUser, SimpleUser>>
            getFilterTestHelpers() throws Exception {
        List<MultiQueryFilterTestHelper<SimpleUser, SimpleUser>> l =
                super.getFilterTestHelpers();
        l.add(booleanFilterHelper(SimpleUser.ATTRIBUTE_ACTIVE, "activeFilter")); //$NON-NLS-1$
        return l;
    }

    @Override
    protected String getUserFriendlyName() {
        return Messages.SIMPLE_USER_NAME.getText();
    }

    @BeforeClass
    public static void setup() throws Exception {
        ORSLoginModuleTest.springSetup();
    }
}
