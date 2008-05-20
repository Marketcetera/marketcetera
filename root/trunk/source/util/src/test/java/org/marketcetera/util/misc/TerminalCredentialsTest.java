package org.marketcetera.util.misc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import org.apache.commons.cli.ParseException;
import org.junit.Test;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

public class TerminalCredentialsTest
	extends TestCaseBase
{
    private static final String TEST_USER=
        "testUser";
    private static final String TEST_PASSWORD=
        "testPassword";


    @Test
    public void normalParsing()
        throws Exception
    {
        TerminalCredentials c=new TerminalCredentials();
        c.parse("-u",TEST_USER,"-p",TEST_PASSWORD,"black","vegetable");
        c.obtainCredentials();
        assertEquals(TEST_USER,c.getUser());
        assertEquals(TEST_PASSWORD,new String(c.getPassword()));
        assertArrayEquals(new String[] {"black","vegetable"},
                          c.getOtherArgs());
    }

    @Test
    public void unknownArgument()
        throws Exception
    {
        TerminalCredentials c=new TerminalCredentials();
        try {
            c.parse("-x");
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),Messages.PARSING_FAILED,
                 ex.getI18NBoundMessage().getMessage());
            assertTrue(ex.getCause() instanceof ParseException);
            return;
        }
        fail();
    }

    @Test
    public void orphanArgument()
        throws Exception
    {
        TerminalCredentials c=new TerminalCredentials();
        try {
            c.parse("-u");
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),Messages.PARSING_FAILED,
                 ex.getI18NBoundMessage().getMessage());
            assertTrue(ex.getCause() instanceof ParseException);
            return;
        }
        fail();
    }

    @Test
    public void noUser()
        throws Exception
    {
        TerminalCredentials c=new TerminalCredentials();
        c.parse();
        try {
            c.obtainUser();
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),Messages.NO_USER,
                 ex.getI18NBoundMessage().getMessage());
            return;
        }
        fail();
    }

    @Test
    public void noPassword()
        throws Exception
    {
        TerminalCredentials c=new TerminalCredentials();
        c.parse();
        try {
            c.obtainPassword();
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),Messages.NO_PASSWORD,
                 ex.getI18NBoundMessage().getMessage());
            return;
        }
        fail();
    }
}
