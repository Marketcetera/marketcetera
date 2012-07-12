package org.marketcetera.util.exec;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Locale;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.except.I18NInterruptedException;
import org.marketcetera.util.file.CloseableRegistry;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.OperatingSystem;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.RegExAssert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class ExecTest
    extends TestCaseBase
{
    private static final String TEST_CATEGORY=
        InputThread.class.getName();
    private static final String TEST_OUT=
        "out";
    private static final String TEST_ERR=
        "err";
    private static final int TEST_EXIT_CODE=
        3;
    private static final byte[] TEST_OUTPUT=
        (TEST_OUT+TEST_ERR).getBytes();
    private static final String TEST_NONEXISTENT_FILE=
        DIR_ROOT+File.separator+"nonexistent";
    private static final int SLEEP_DURATION=
        1000;
//  private static final String TEST_LOCATION=
//      TEST_CATEGORY;


    /*
     * Independent process helpers.
     */

    // Prints the test text to the standard output and error streams,
    // and exits with the test exit code.

    public static final class CommandStreams
    {
        public static void main
            (String args[])
        {
            System.out.print(TEST_OUT);
            System.err.print(TEST_ERR);
            System.exit(TEST_EXIT_CODE);
        }
    }

    // Prints the current user directory to the standard output stream.

    public static final class CommandCwd
    {
        public static void main
            (String args[])
        {
            System.out.print(System.getProperty("user.dir"));
        }
    }

    // Sleeps for a long-ish duration.

    public static final class CommandSleep
    {
        public static void main
            (String args[])
            throws Exception
        {
            for (int i=0;i<5;i++) {
                // EXTREME TEST 1: some output is required.
                System.err.println(i);
                Thread.sleep(SLEEP_DURATION);
            }
        }
    }

    /*
     * Java process execution in a child thread.
     */

    private static final class ChildExec
        extends Thread
    {
        private String mCommand;
        private I18NException mException;

        ChildExec
            (String command)
        {
            mCommand=command;
        }

        I18NException getException()
        {
            return mException;
        }

        @Override
        public void run() {
            try {
                ExecTest.run((File)null,Disposition.MEMORY,mCommand);
            } catch (I18NException ex) {
                mException=ex;
            }
        }
    };

    /*
     * Process execution with the standard output and error streams
     * redirected to byte arrays.
     */

    private static final class Redirector
        extends ExecResult
    {
        private byte[] mStdOut;
        private byte[] mStdErr;

        Redirector
            (int exitCode,
             byte[] stdOut,
             byte[] stdErr,
             byte[] memory)
        {
            super(exitCode,memory);
            mStdOut=stdOut;
            mStdErr=stdErr;
        }

        byte[] getStdOut()
        {
            return mStdOut;
        }

        byte[] getStdErr()
        {
            return mStdErr;
        }

        static Redirector get
            (Disposition disposition)
            throws Exception
        {
            PrintStream stdOutSave=System.out;
            PrintStream stdErrSave=System.err;
            ByteArrayOutputStream stdOutByteArray;
            ByteArrayOutputStream stdErrByteArray;
            ExecResult result;
            CloseableRegistry r=new CloseableRegistry();
            try {
                stdOutByteArray=new ByteArrayOutputStream();
                r.register(stdOutByteArray);
                PrintStream stdOutNew=new PrintStream(stdOutByteArray);
                r.register(stdOutNew);
                stdErrByteArray=new ByteArrayOutputStream();
                r.register(stdErrByteArray);
                PrintStream stdErrNew=new PrintStream(stdErrByteArray);
                r.register(stdErrNew);
                System.setOut(stdOutNew);
                System.setErr(stdErrNew);
                result=ExecTest.run((File)null,disposition,"CommandStreams");
            } finally {
                System.setErr(stdErrSave);
                System.setOut(stdOutSave);
                r.close();
            }
            return new Redirector
                (result.getExitCode(),stdOutByteArray.toByteArray(),
                 stdErrByteArray.toByteArray(),result.getOutput());
        }
    }

    /*
     * JVM execution in a separate process with stream redirection.
     */

    private static String getJava()
    {
        if (OperatingSystem.LOCAL.isUnix()) {
            return "java";
        }
        if (OperatingSystem.LOCAL.isWin32()) {
            return "java.exe";
        }
        throw new AssertionError("Unknown platform");
    }

    private static ExecResult run
        (File directory,
         Disposition disposition,
         String subclass)
        throws I18NException
    {
        return Exec.run
            (directory,disposition,getJava(),"-classpath",
             (new File(DIR_TEST_CLASSES)).getAbsolutePath(),
             ExecTest.class.getName()+"$"+subclass);
    }

    private static ExecResult run
        (String directory,
         Disposition disposition,
         String subclass)
        throws I18NException
    {
        return Exec.run
            (directory,disposition,getJava(),"-classpath",
             (new File(DIR_TEST_CLASSES)).getAbsolutePath(),
             ExecTest.class.getName()+"$"+subclass);
    }


    @Before
    public void setupExecTest()
    {
        ActiveLocale.setProcessLocale(Locale.ROOT);
        setLevel(TEST_CATEGORY,Level.TRACE);
    }


    @Test
    public void stdOutDisposition()
        throws Exception
    {
        Redirector result=Redirector.get(Disposition.STDOUT);
        assertEquals(TEST_EXIT_CODE,result.getExitCode());
        assertArrayEquals(TEST_OUTPUT,result.getStdOut());
        assertArrayEquals(ArrayUtils.EMPTY_BYTE_ARRAY,result.getStdErr());
        assertNull(result.getOutput());
    }

    @Test
    public void stdErrDisposition()
        throws Exception
    {
        Redirector result=Redirector.get(Disposition.STDERR);
        assertEquals(TEST_EXIT_CODE,result.getExitCode());
        assertArrayEquals(ArrayUtils.EMPTY_BYTE_ARRAY,result.getStdOut());
        assertArrayEquals(TEST_OUTPUT,result.getStdErr());
        assertNull(result.getOutput());
    }

    @Test
    public void memoryDisposition()
        throws Exception
    {
        Redirector result=Redirector.get(Disposition.MEMORY);
        assertEquals(TEST_EXIT_CODE,result.getExitCode());
        assertArrayEquals(ArrayUtils.EMPTY_BYTE_ARRAY,result.getStdOut());
        assertArrayEquals(ArrayUtils.EMPTY_BYTE_ARRAY,result.getStdErr());
        assertArrayEquals(TEST_OUTPUT,result.getOutput());
    }


    @Test
    public void defaultWorkingDirFile()
        throws Exception
    {
        ExecResult result=run((File)null,Disposition.MEMORY,"CommandCwd");
        assertMatches(".*\\"+File.separator+"util",
                      new String(result.getOutput()));
        assertEquals(0,result.getExitCode());
    }

    @Test
    public void defaultWorkingDirString()
        throws Exception
    {
        ExecResult result=run((String)null,Disposition.MEMORY,"CommandCwd");
        assertMatches(".*\\"+File.separator+"util",
                      new String(result.getOutput()));
        assertEquals(0,result.getExitCode());
    }


    @Test
    public void customWorkingDir()
        throws Exception
    {
        ExecResult result=run(DIR_ROOT,Disposition.MEMORY,"CommandCwd");
        assertEquals
            ((new File(DIR_ROOT)).getAbsolutePath(),
             new String(result.getOutput()));
        assertEquals(0,result.getExitCode());
    }


    @Test
    public void nonexistentCommand()
    {
        try {
            Exec.run((File)null,Disposition.MEMORY,TEST_NONEXISTENT_FILE);
            fail();
        } catch (I18NException ex) {
            assertFalse(ex instanceof I18NInterruptedException);
            assertEquals
                (ex.getDetail(),
                 new I18NBoundMessage1P(Messages.CANNOT_EXECUTE,
                                        TEST_NONEXISTENT_FILE),
                 ex.getI18NBoundMessage());
        }
    }

    @Test
    public void nonexistentWorkingDir()
    {
        try {
            run(TEST_NONEXISTENT_FILE,Disposition.MEMORY,DIR_ROOT);
            fail();
        } catch (I18NException ex) {
            assertFalse(ex instanceof I18NInterruptedException);
            assertEquals
                (ex.getDetail(),
                 new I18NBoundMessage1P(Messages.CANNOT_EXECUTE,getJava()),
                 ex.getI18NBoundMessage());
        }
    }

    @Test
    public void unexpectedTerminationAndProcessKilled()
        throws Exception
    {
        ChildExec child=new ChildExec("CommandSleep");
        child.start();
        Thread.sleep(SLEEP_DURATION);
        child.interrupt();
        Thread.sleep(SLEEP_DURATION);
        I18NException ex=child.getException();
        assertTrue(ex instanceof I18NInterruptedException);
        assertEquals
            (ex.getDetail(),
             new I18NBoundMessage1P(Messages.UNEXPECTED_TERMINATION,getJava()),
             ex.getI18NBoundMessage());
    }

    /*
     * EXTREME TEST 1: run alone (no other tests in the same file,
     * and no other units test) after uncommenting sections in main
     * class.
    @Test
    public void exception()
        throws Exception
    {
        run((File)null,Disposition.MEMORY,"CommandSleep");
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,
             "Cannot copy output of command '"+getJava()+"'",TEST_LOCATION);
    }
     */
}
