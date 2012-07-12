package org.marketcetera.util.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.marketcetera.util.test.TestCaseBase;

import static org.marketcetera.util.test.UnicodeData.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@Ignore
public class WrapperTestBase
    extends TestCaseBase
{
    protected static final String TEST_ROOT=
        DIR_ROOT+File.separator+"wrappers"+File.separator;
    protected static final String TEST_FILE=
        TEST_ROOT+"test_file";
    protected static final String TEST_NONEXISTENT_FILE=
        TEST_ROOT+"nonexistent"+File.separator+"nonexistent";
    protected static final String VALUE=
        HELLO_EN;
    protected static final byte[] VALUE_BYTES=
        HELLO_EN_NAT;


    @Before
    @After
    public void setupTearDownWrapperTestBase()
        throws Exception
    {
        Deleter.apply(TEST_FILE);
    }


    protected void testStandardInputStream
        (ByteArrayInputStream in)
        throws Exception {}

    protected void testStandardInputStream()
        throws Exception
    {
        InputStream stdInSave=System.in;
        CloseableRegistry r=new CloseableRegistry();
        try {
            ByteArrayInputStream stdIn=new ByteArrayInputStream(VALUE_BYTES);
            r.register(stdIn);
            System.setIn(stdIn);
            testStandardInputStream(stdIn);
        } finally {
            System.setIn(stdInSave);
            r.close();
        }
    }


    protected void testStandardOutputStream
        (ByteArrayOutputStream out)
        throws Exception {}

    protected void testStandardOutputStream()
        throws Exception
    {
        PrintStream stdOutSave=System.out;
        CloseableRegistry r=new CloseableRegistry();
        try {
            ByteArrayOutputStream stdOutByteArray=new ByteArrayOutputStream();
            r.register(stdOutByteArray);
            PrintStream stdOut=new PrintStream(stdOutByteArray);
            r.register(stdOut);
            System.setOut(stdOut);
            testStandardOutputStream(stdOutByteArray);
        } finally {
            System.setOut(stdOutSave);
            r.close();
        }
    }


    protected void testStandardErrorStream
        (ByteArrayOutputStream err)
        throws Exception {}

    protected void testStandardErrorStream()
        throws Exception
    {
        PrintStream stdErrSave=System.err;
        CloseableRegistry r=new CloseableRegistry();
        try {
            ByteArrayOutputStream stdErrByteArray=new ByteArrayOutputStream();
            r.register(stdErrByteArray);
            PrintStream stdErr=new PrintStream(stdErrByteArray);
            r.register(stdErr);
            System.setErr(stdErr);
            testStandardErrorStream(stdErrByteArray);
        } finally {
            System.setErr(stdErrSave);
            r.close();
        }
    }
}
