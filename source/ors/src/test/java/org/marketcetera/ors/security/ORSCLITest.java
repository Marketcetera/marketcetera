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
@ClassVersion("$Id$") //$NON-NLS-1$
public class ORSCLITest extends PersistTestBase {
    private static final String ENCODING = "UTF-8"; //$NON-NLS-1$
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
                    return ORSLoginModuleTest.getSpringFiles();
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
        runCLI(MissingArgumentException.class, "-u"); //$NON-NLS-1$
        runCLI(MissingArgumentException.class, "-p"); //$NON-NLS-1$
        runCLI(UnrecognizedOptionException.class, "-b"); //$NON-NLS-1$
        runCLI(MissingArgumentException.class, "-u","name","-p"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        runCLI(MissingArgumentException.class, "-u","-p","--listUsers"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        runCLI(MissingArgumentException.class, "-u","name","-p","--listUsers"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        runCLI(I18NException.class, "-u","name","--listUsers"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        runCLI(I18NException.class, "-u","name","-p","pass","--addUser"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        runCLI(MissingArgumentException.class, "-u","name","-p","pass","--addUser","-n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        runCLI(I18NException.class, "-u","name","-p","pass","--addUser","-n","name"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
        runCLI(MissingArgumentException.class, "-u","name","-p","pass","--addUser","-n","name","-w"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
        runCLI(MissingArgumentException.class, "-u","name","-p","pass","--addUser","-w","pass","-n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
        runCLI(I18NException.class, "-u","name","-p","pass","--deleteUser"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        runCLI(MissingArgumentException.class, "-u","name","-p","pass","--deleteUser","-n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        runCLI(I18NException.class, "-u","name","-p","pass","--changePassword"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        runCLI(MissingArgumentException.class, "-u","name","-p","pass","--changePassword","-w"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        runCLI(MissingArgumentException.class, "-u","name","-p","pass","--changePassword","-w","pass","-n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
        runCLI(MissingArgumentException.class, "-u","name","-p","pass","--changePassword","-n","name","-w"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
        runCLI(I18NException.class, "-u","name","-p","pass","--changePassword","-n","name"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
        runCLI(MissingArgumentException.class, "-u","name","-p","pass","--listUsers","-n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    }
    @Test
    public void commands() throws Exception {
        //Create an admin account
        SimpleUser admin = new SimpleUser();
        admin.setName("admin"); //$NON-NLS-1$
        final String password = "admin"; //$NON-NLS-1$
        admin.setPassword(password.toCharArray());
        admin.save();
        //Try list users
        runCLI("-u",admin.getName(),"-p",password,"--listUsers"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        matchOut("^\\s*admin\\s*$"); //$NON-NLS-1$
        //Try creating admin again
        runCLI(EntityExistsException.class, "-u", admin.getName(), "-p", //$NON-NLS-1$ //$NON-NLS-2$
                password, "--addUser", "-n", "admin", "-w", "pssst"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        matchOut("^$"); //$NON-NLS-1$
        //Try creating a different user
        runCLI("-u", admin.getName(), "-p", //$NON-NLS-1$ //$NON-NLS-2$
                password, "--addUser", "-n", "blah", "-w", "meh"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        matchOut("^[\\p{L}\\s]*'blah'[\\p{L}\\s]*$"); //$NON-NLS-1$
        //Ensure that it appears in the listing
        //Try list users
        runCLI("-u",admin.getName(),"-p",password,"--listUsers"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        matchOut("^\\s*admin\\s*blah\\s*$"); //$NON-NLS-1$
        //Try list users as the new user
        runCLI("-u","blah","-p","meh","--listUsers"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        matchOut("^\\s*admin\\s*blah\\s*$"); //$NON-NLS-1$
        //Try list users with filtering
        runCLI("-u","blah","-p","meh","--listUsers","-n","*dm*"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
        matchOut("^\\s*admin\\s*$"); //$NON-NLS-1$
        runCLI("-u","blah","-p","meh","--listUsers","-n","?la?"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
        matchOut("^\\s*blah\\s*$"); //$NON-NLS-1$
        runCLI("-u","blah","-p","meh","--listUsers","-n","*a*"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
        matchOut("^\\s*admin\\s*blah\\s*$"); //$NON-NLS-1$
        //Try logging in as an invalid user
        runCLI(I18NException.class,"-u","wah","-p","meh","--listUsers"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        matchOut("^$"); //$NON-NLS-1$
        //Try logging in with an incorrect password
        runCLI(I18NException.class,"-u","blah","-p","wha?","--listUsers"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        matchOut("^$"); //$NON-NLS-1$
        //Try changing new user's password
        runCLI("-u","blah","-p","meh","--changePassword","-w","mgh"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
        matchOut("[\\p{L}\\s]*'blah'[\\p{L}\\s]*"); //$NON-NLS-1$
        //verify that new password works
        runCLI("-u","blah","-p","mgh","--listUsers"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        matchOut("^\\s*admin\\s*blah\\s*$"); //$NON-NLS-1$
        //Try changing new user's password by supplying the same user name
        runCLI("-u","blah","-p","mgh","--changePassword","-w","ugh","n","blah"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
        matchOut("[\\p{L}\\s]*'blah'[\\p{L}\\s]*"); //$NON-NLS-1$
        //verify that new password works
        runCLI("-u","blah","-p","ugh","--listUsers"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        matchOut("^\\s*admin\\s*blah\\s*$"); //$NON-NLS-1$
        //Try changing another user password as a non-admin user
        runCLI(I18NException.class, "-u", "blah", "-p", "ugh", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                "--changePassword", "-w", "meh", "-n", "admin"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        matchOut("^$"); //$NON-NLS-1$
        //Try changing another user password as a admin user
        runCLI("-u", "admin", "-p", password, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                "--changePassword", "-w", "meh", "-n", "blah"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        matchOut("[\\p{L}\\s]*'blah'[\\p{L}\\s]*"); //$NON-NLS-1$
        //verify that new password works
        runCLI("-u","blah","-p","meh","--listUsers"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        matchOut("^\\s*admin\\s*blah\\s*$"); //$NON-NLS-1$
        //try deleting a user a non admin user
        runCLI(I18NException.class, "-u","blah","-p","meh", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                "--deleteUser","-n","admin"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        matchOut("^$"); //$NON-NLS-1$
        //try deleting the user as admin user
        runCLI("-u","admin","-p",password, "--deleteUser","-n","blah"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        matchOut("[\\p{L}\\s]*'blah'[\\p{L}\\s]*"); //$NON-NLS-1$
        //Try changing admin's password
        runCLI("-u","admin","-p",password,"--changePassword","-w","ugh"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        matchOut("[\\p{L}\\s]*'admin'[\\p{L}\\s]*"); //$NON-NLS-1$
        //verify that new password works
        runCLI("-u","admin","-p","ugh","--listUsers"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        matchOut("^\\s*admin\\s*$"); //$NON-NLS-1$
        //verify that admin cannot be deleted
        runCLI(I18NException.class, "-u","admin","-p","ugh", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                "--deleteUser","-n","admin"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        //Try changing password of a non-existent user
        runCLI(I18NException.class, "-u","admin","-p","ugh", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                "--changePassword","-w","ugh","-n","who"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        matchOut("^$"); //$NON-NLS-1$

        //Create a user with unicode name and password
        runCLI("-u", admin.getName(), "-p", "ugh", "--addUser", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                "-n", UnicodeData.HELLO_GR, "-w", UnicodeData.COMBO); //$NON-NLS-1$ //$NON-NLS-2$
        matchOut("^[\\p{L}\\s]*'" + UnicodeData.HELLO_GR + "'[\\p{L}\\s]*$"); //$NON-NLS-1$ //$NON-NLS-2$
//        matchOut("^ User '" + UnicodeData.HELLO_GR + "' created$");
        //Ensure that it appears in the listing
        //Try list users
        runCLI("-u",admin.getName(),"-p","ugh","--listUsers"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        matchOut("^\\s*admin\\s*" + UnicodeData.HELLO_GR + "\\s*$"); //$NON-NLS-1$ //$NON-NLS-2$
        //verify that the new user can login with the unicode password
        runCLI("-u",UnicodeData.HELLO_GR,"-p", UnicodeData.COMBO, "--listUsers"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        matchOut("^\\s*admin\\s*" + UnicodeData.HELLO_GR + "\\s*$"); //$NON-NLS-1$ //$NON-NLS-2$

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
        assertTrue(str + "~" + regex, Pattern.matches(regex,str)); //$NON-NLS-1$
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
            matchErr("^$"); //$NON-NLS-1$
        } catch (Exception e) {
            assertNotNull(e.toString(), expectedFailure);
            assertTrue(e.toString(),expectedFailure.isInstance(e));
            //verify that the exception message and usage message is
            //contained in error output
            String err = bErr.toString(ENCODING);
            assertTrue(e.getLocalizedMessage() + " in " + err, err.indexOf(e.getLocalizedMessage()) >= 0); //$NON-NLS-1$
            assertTrue(err, err.indexOf(ORSAdminCLI.CMD_NAME) >= 0);
        }
    }
}
