package org.marketcetera.ors.security;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.persist.PersistTestBase;
import org.marketcetera.persist.EntityExistsException;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.test.UnicodeData;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.UnrecognizedOptionException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.regex.Pattern;

/* $License$ */
/**
 * Tests the ORS CLI functionality 
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class ORSCLITest extends PersistTestBase {
    private static final String ENCODING = "UTF-8"; 
    private static ORSAdminCLI instance;
    private static ByteArrayOutputStream bOut = new ByteArrayOutputStream();
    private static PrintStream pOut;
    private static ByteArrayOutputStream bErr = new ByteArrayOutputStream();
    private static PrintStream pErr;
    @BeforeClass
    public static void setup() throws Exception {
        if(pOut == null) {
            pOut = new PrintStream(bOut, false, ENCODING);
        }
        if(pErr == null) {
            pErr = new PrintStream(bErr, false, ENCODING);
        }
        try {
            instance = new ORSAdminCLI(pOut,pErr) {
                @Override
                protected String[] getConfigurations() {
                    //Initialize the DB.
                    return new String[]{"ors_initdb_vendor.xml",
                            "ors_orm.xml", "ors_db.xml"};
                }

                @Override
                protected char[] readPasswordFromConsole(String message) {
                    //don't try to read from console in unit tests
                    return null;
                }
            };
        } catch (Exception e) {
            SLF4JLoggerProxy.error(ORSCLITest.class, e);
            throw e;
        }
    }
    @Test
    public void parsingFailures() throws Exception {
        runCLI(MissingOptionException.class);
        runCLI(MissingArgumentException.class, "-u");
        runCLI(MissingArgumentException.class, "-p");
        runCLI(UnrecognizedOptionException.class, "-b");
        runCLI(MissingArgumentException.class, "-u","name","-p");
        runCLI(MissingArgumentException.class, "-u","-p","--listUsers");
        runCLI(MissingArgumentException.class, "-u","name","-p","--listUsers");
        runCLI(I18NException.class, "-u","name","--listUsers");
        runCLI(I18NException.class, "-u","name","-p","pass","--addUser");
        runCLI(MissingArgumentException.class, "-u","name","-p","pass","--addUser","-n");
        runCLI(I18NException.class, "-u","name","-p","pass","--addUser","-n","name");
        runCLI(MissingArgumentException.class, "-u","name","-p","pass","--addUser","-n","name","-w");
        runCLI(MissingArgumentException.class, "-u","name","-p","pass","--addUser","-w","pass","-n");
        runCLI(I18NException.class, "-u","name","-p","pass","--deleteUser");
        runCLI(MissingArgumentException.class, "-u","name","-p","pass","--deleteUser","-n");
        runCLI(I18NException.class, "-u","name","-p","pass","--changePassword");
        runCLI(MissingArgumentException.class, "-u","name","-p","pass","--changePassword","-w");
        runCLI(MissingArgumentException.class, "-u","name","-p","pass","--changePassword","-w","pass","-n");
        runCLI(MissingArgumentException.class, "-u","name","-p","pass","--changePassword","-n","name","-w");
        runCLI(I18NException.class, "-u","name","-p","pass","--changePassword","-n","name");
        runCLI(MissingArgumentException.class, "-u","name","-p","pass","--listUsers","-n");
    }
    @Test
    public void commands() throws Exception {
        //Create an admin account
        SimpleUser admin = new SimpleUser();
        admin.setName("admin");
        final String password = "admin";
        admin.setPassword(password.toCharArray());
        admin.save();
        //Try list users
        runCLI("-u",admin.getName(),"-p",password,"--listUsers");
        matchOut("^\\s*admin\\s*$");
        //Try creating admin again
        runCLI(EntityExistsException.class, "-u", admin.getName(), "-p",
                password, "--addUser", "-n", "admin", "-w", "pssst");
        matchOut("^$");
        //Try creating a different user
        runCLI("-u", admin.getName(), "-p",
                password, "--addUser", "-n", "blah", "-w", "meh");
        matchOut("^[\\p{L}\\s]*'blah'[\\p{L}\\s]*$");
        //Ensure that it appears in the listing
        //Try list users
        runCLI("-u",admin.getName(),"-p",password,"--listUsers");
        matchOut("^\\s*admin\\s*blah\\s*$");
        //Try list users as the new user
        runCLI("-u","blah","-p","meh","--listUsers");
        matchOut("^\\s*admin\\s*blah\\s*$");
        //Try list users with filtering
        runCLI("-u","blah","-p","meh","--listUsers","-n","*dm*");
        matchOut("^\\s*admin\\s*$");
        runCLI("-u","blah","-p","meh","--listUsers","-n","?la?");
        matchOut("^\\s*blah\\s*$");
        runCLI("-u","blah","-p","meh","--listUsers","-n","*a*");
        matchOut("^\\s*admin\\s*blah\\s*$");
        //Try logging in as an invalid user
        runCLI(I18NException.class,"-u","wah","-p","meh","--listUsers");
        matchOut("^$");
        //Try logging in with an incorrect password
        runCLI(I18NException.class,"-u","blah","-p","wha?","--listUsers");
        matchOut("^$");
        //Try changing new user's password
        runCLI("-u","blah","-p","meh","--changePassword","-w","mgh");
        matchOut("[\\p{L}\\s]*'blah'[\\p{L}\\s]*");
        //verify that new password works
        runCLI("-u","blah","-p","mgh","--listUsers");
        matchOut("^\\s*admin\\s*blah\\s*$");
        //Try changing new user's password by supplying the same user name
        runCLI("-u","blah","-p","mgh","--changePassword","-w","ugh","n","blah");
        matchOut("[\\p{L}\\s]*'blah'[\\p{L}\\s]*");
        //verify that new password works
        runCLI("-u","blah","-p","ugh","--listUsers");
        matchOut("^\\s*admin\\s*blah\\s*$");
        //Try changing another user password as a non-admin user
        runCLI(I18NException.class, "-u", "blah", "-p", "ugh",
                "--changePassword", "-w", "meh", "-n", "admin");
        matchOut("^$");
        //Try changing another user password as a admin user
        runCLI("-u", "admin", "-p", password,
                "--changePassword", "-w", "meh", "-n", "blah");
        matchOut("[\\p{L}\\s]*'blah'[\\p{L}\\s]*");
        //verify that new password works
        runCLI("-u","blah","-p","meh","--listUsers");
        matchOut("^\\s*admin\\s*blah\\s*$");
        //try deleting a user a non admin user
        runCLI(I18NException.class, "-u","blah","-p","meh",
                "--deleteUser","-n","admin");
        matchOut("^$");
        //try deleting the user as admin user
        runCLI("-u","admin","-p",password, "--deleteUser","-n","blah");
        matchOut("[\\p{L}\\s]*'blah'[\\p{L}\\s]*");
        //Try changing admin's password
        runCLI("-u","admin","-p",password,"--changePassword","-w","ugh");
        matchOut("[\\p{L}\\s]*'admin'[\\p{L}\\s]*");
        //verify that new password works
        runCLI("-u","admin","-p","ugh","--listUsers");
        matchOut("^\\s*admin\\s*$");
        //verify that admin cannot be deleted
        runCLI(I18NException.class, "-u","admin","-p","ugh",
                "--deleteUser","-n","admin");
        //Try changing password of a non-existent user
        runCLI(I18NException.class, "-u","admin","-p","ugh",
                "--changePassword","-w","ugh","-n","who");
        matchOut("^$");

        //Create a user with unicode name and password
        runCLI("-u", admin.getName(), "-p", "ugh", "--addUser",
                "-n", UnicodeData.HELLO_GR, "-w", UnicodeData.COMBO);
        matchOut("^[\\p{L}\\s]*'" + UnicodeData.HELLO_GR + "'[\\p{L}\\s]*$");
//        matchOut("^ User '" + UnicodeData.HELLO_GR + "' created$");
        //Ensure that it appears in the listing
        //Try list users
        runCLI("-u",admin.getName(),"-p","ugh","--listUsers");
        matchOut("^\\s*admin\\s*" + UnicodeData.HELLO_GR + "\\s*$");
        //verify that the new user can login with the unicode password
        runCLI("-u",UnicodeData.HELLO_GR,"-p", UnicodeData.COMBO, "--listUsers");
        matchOut("^\\s*admin\\s*" + UnicodeData.HELLO_GR + "\\s*$");

    }
    static void matchOut(String regex) throws Exception {
        matchStream(regex, bOut);
    }
    static void matchErr(String regex) throws Exception {
        matchStream(regex, bErr);
    }
    static void matchStream(String regex, ByteArrayOutputStream baos)
            throws Exception {
        String str = baos.toString(ENCODING);
        assertTrue(str + "~" + regex, Pattern.matches(regex,str));
    }
    static void runCLI(String... args) throws Exception{
        runCLI(null,args);
    }
    static void runCLI(Class<? extends Exception> expectedFailure,
                       String... args) throws Exception{
        //Uncomment the following lines to view CLI output
        //System.out.print(bOut.toString());
        //System.err.print(bErr.toString());
        bOut.reset();
        bErr.reset();
        try {
            instance.parseAndRun(args);
            pOut.flush();
            pErr.flush();
            assertNull(expectedFailure);
            //no error output generated during normal run
            matchErr("^$");
        } catch (Exception e) {
            assertNotNull(e.toString(), expectedFailure);
            assertTrue(e.toString(),expectedFailure.isInstance(e));
            //verify that the exception message and usage message is
            //contained in error output
            String err = bErr.toString(ENCODING);
            assertTrue(e.getLocalizedMessage() + " in " + err, err.indexOf(e.getLocalizedMessage()) >= 0);
            assertTrue(err, err.indexOf(ORSAdminCLI.CMD_NAME) >= 0);
        }
    }
}
