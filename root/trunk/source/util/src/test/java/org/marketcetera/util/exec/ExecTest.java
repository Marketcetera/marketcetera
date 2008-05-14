package org.marketcetera.util.exec;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import org.junit.Test;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.file.CloseableRegistry;
import org.marketcetera.util.misc.OperatingSystem;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.RegExAssert.*;

public class ExecTest
	extends TestCaseBase
{
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
            Thread.sleep(SLEEP_DURATION*10);
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

        public ChildExec
            (String command)
        {
            mCommand=command;
        }

        public I18NException getException()
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

        public Redirector
            (int exitCode,
             byte[] stdOut,
             byte[] stdErr,
             byte[] memory)
        {
            super(exitCode,memory);
            mStdOut=stdOut;
            mStdErr=stdErr;
        }

        public byte[] getStdOut()
        {
            return mStdOut;
        }

        public byte[] getStdErr()
        {
            return mStdErr;
        }

        public static Redirector get
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
                PrintStream stdOutNew=new PrintStream(stdOutByteArray);
                r.register(stdOutNew);
                stdErrByteArray=new ByteArrayOutputStream();
                PrintStream stdErrNew=new PrintStream(stdErrByteArray);
                r.register(stdErrNew);
                System.setOut(stdOutNew);
                System.setErr(stdErrNew);
                result=ExecTest.run((File)null,disposition,"CommandStreams");
            } finally {
                r.close();
                System.setErr(stdErrSave);
                System.setOut(stdOutSave);
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


    @Test
    public void stdOutDisposition()
        throws Exception
    {
        Redirector result=Redirector.get(Disposition.STDOUT);
        assertEquals(TEST_EXIT_CODE,result.getExitCode());
        assertArrayEquals(TEST_OUTPUT,result.getStdOut());
        assertArrayEquals(new byte[0],result.getStdErr());
        assertNull(result.getOutput());
    }

    @Test
    public void stdErrDisposition()
        throws Exception
    {
        Redirector result=Redirector.get(Disposition.STDERR);
        assertEquals(TEST_EXIT_CODE,result.getExitCode());
        assertArrayEquals(new byte[0],result.getStdOut());
        assertArrayEquals(TEST_OUTPUT,result.getStdErr());
        assertNull(result.getOutput());
    }

    @Test
    public void memoryDisposition()
        throws Exception
    {
        Redirector result=Redirector.get(Disposition.MEMORY);
        assertEquals(TEST_EXIT_CODE,result.getExitCode());
        assertArrayEquals(new byte[0],result.getStdOut());
        assertArrayEquals(new byte[0],result.getStdErr());
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
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),Messages.CANNOT_EXECUTE,
                 ex.getI18NMessage());
            return;
        }
        fail();
    }

    @Test
    public void nonexistentWorkingDir()
    {
        try {
            run(TEST_NONEXISTENT_FILE,Disposition.MEMORY,DIR_ROOT);
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),Messages.CANNOT_EXECUTE,
                 ex.getI18NMessage());
            return;
        }
        fail();
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
        assertEquals
            (ex.getDetail(),Messages.UNEXPECTED_TERMINATION,
             ex.getI18NMessage());
    }
}
