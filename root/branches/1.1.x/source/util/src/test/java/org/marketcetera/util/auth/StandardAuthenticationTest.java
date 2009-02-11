package org.marketcetera.util.auth;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Locale;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.util.file.CloseableRegistry;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class StandardAuthenticationTest
    extends TestCaseBase
{
    private static final String TEST_ROOT=
        DIR_ROOT+File.separator+"auth"+File.separator;

    private static void authenticate
        (String configFile,
         String[] args,
         boolean process,
         String user,
         String password)
    {
        StandardAuthentication sa=new StandardAuthentication(configFile,args);
        assertEquals(process,sa.setValues());
        assertEquals(user,sa.getUser());
        assertEquals(password,sa.getPasswordAsString());
        if (password==null) {
            assertNull(sa.getPassword());
        } else {
            assertArrayEquals(password.toCharArray(),sa.getPassword());
        }
        sa.clearPassword();
        assertNull(sa.getPassword());
        assertNull(sa.getPasswordAsString());
    }


    @Before
    public void setupSetterContextTestBase()
    {
        ActiveLocale.setProcessLocale(Locale.ROOT);
    }


    @Test
    public void all()
    {
        authenticate
            (TEST_ROOT+"auth_user.xml",
             ArrayUtils.EMPTY_STRING_ARRAY,false,"tu1",null);
        authenticate
            (TEST_ROOT+"auth_user.xml",
             new String[] {"--p","tp1"},true,"tu1","tp1");
        authenticate
            (TEST_ROOT+"auth_user.xml",
             new String[] {"-p","tp1"},true,"tu1","tp1");
        authenticate
            (TEST_ROOT+"auth_user.xml",
             new String[] {"-u",StringUtils.EMPTY,"-p","tp1"},
             true,"tu1","tp1");
        authenticate
            (TEST_ROOT+"auth_user.xml",
             new String[] {"--password","tp1"},true,"tu1","tp1");
        authenticate
            (TEST_ROOT+"auth_user.xml",
             new String[] {"-password","tp1"},true,"tu1","tp1");
        authenticate
            (TEST_ROOT+"auth_user.xml",
             new String[] {"-u","tux","-p","tpx"},true,"tux","tpx");

        authenticate
            (TEST_ROOT+"auth_pwd.xml",
             ArrayUtils.EMPTY_STRING_ARRAY,false,null,"tp2");
        authenticate
            (TEST_ROOT+"auth_pwd.xml",
             new String[] {"-u","tu2"},true,"tu2","tp2");
        authenticate
            (TEST_ROOT+"auth_pwd.xml",
             new String[] {"-u","tu2","-p",StringUtils.EMPTY},
             true,"tu2","tp2");
        authenticate
            (TEST_ROOT+"auth_pwd.xml",
             new String[] {"-u","tux","-p","tpx"},true,"tux","tpx");

        authenticate
            (TEST_ROOT+"auth_both.xml",
             ArrayUtils.EMPTY_STRING_ARRAY,true,"tu3","tp3");
        authenticate
            (TEST_ROOT+"auth_both.xml",
             new String[] {"-u","tux","-p","tpx"},true,"tux","tpx");

        authenticate
            (TEST_ROOT+"auth_none.xml",
             ArrayUtils.EMPTY_STRING_ARRAY,false,null,null);
        authenticate
            (TEST_ROOT+"auth_none.xml",
             new String[] {"-u","tux"},false,"tux",null);
        authenticate
            (TEST_ROOT+"auth_none.xml",
             new String[] {"-p","tpx"},false,null,"tpx");
        authenticate
            (TEST_ROOT+"auth_none.xml",
             new String[] {"-u","tu3","-p","tp3"},true,"tu3","tp3");

        authenticate
            (TEST_ROOT+"auth_blank.xml",
             ArrayUtils.EMPTY_STRING_ARRAY,false,null,null);
        authenticate
            (TEST_ROOT+"auth_blank.xml",
             new String[] {"-u","tux"},false,"tux",null);
        authenticate
            (TEST_ROOT+"auth_blank.xml",
             new String[] {"-p","tpx"},false,null,"tpx");
        authenticate
            (TEST_ROOT+"auth_blank.xml",
             new String[] {"-u","tu3","-p","tp3"},true,"tu3","tp3");
    }

    @Test
    public void usage()
    {
        StandardAuthentication sa=new StandardAuthentication
            (TEST_ROOT+"auth_none.xml",ArrayUtils.EMPTY_STRING_ARRAY);
        ByteArrayOutputStream outputStream;
        CloseableRegistry r=new CloseableRegistry();
        try {
            outputStream=new ByteArrayOutputStream();
            r.register(outputStream);
            PrintStream printStream=new PrintStream(outputStream);
            r.register(printStream);
            sa.printUsage(printStream);
        } finally {
            r.close();
        }
        assertEquals
            ("Spring framework (overriding context)"+
             SystemUtils.LINE_SEPARATOR+
             " Set 'metc.amq.user' to username in properties file"+
             SystemUtils.LINE_SEPARATOR+
             " Set 'metc.amq.password' to password in properties file"+
             SystemUtils.LINE_SEPARATOR+
             SystemUtils.LINE_SEPARATOR+
             "Command-line options (overriding context)"+
             SystemUtils.LINE_SEPARATOR+
             " -u or -user followed by username"+
             SystemUtils.LINE_SEPARATOR+
             " -p or -password followed by password"+
             SystemUtils.LINE_SEPARATOR+
             SystemUtils.LINE_SEPARATOR+
             "Console terminal"+
             SystemUtils.LINE_SEPARATOR+
             " Type username when prompted"+
             SystemUtils.LINE_SEPARATOR+
             " Type password when prompted (password won't echo)"+
             SystemUtils.LINE_SEPARATOR+
             SystemUtils.LINE_SEPARATOR,
             new String(outputStream.toByteArray()));
    }

    /*
     * Run via -Pstandard.authentication exec:java. With pom
     * specifying the '-p' argument, it will just print 'true', 'tu1',
     * and 'pass'; otherwise, it will prompt for a password. Press
     * Control+D (Linux), or Control+Z and then Enter (Windows), or
     * leave blank and then Enter (all OS), and it will print 'false',
     * 'tu1', 'null'; or enter a password, and it will show 'true',
     * 'tu1', and the password you typed.
     */

    public static void main(String[] args)
    {
        StandardAuthentication sa=new StandardAuthentication
            (TEST_ROOT+"auth_user.xml",args);
        System.err.println(sa.setValues());
        System.err.println(sa.getUser());
        System.err.println(sa.getPasswordAsString());
    }
}
