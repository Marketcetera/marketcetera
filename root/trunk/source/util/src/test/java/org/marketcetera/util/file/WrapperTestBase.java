package org.marketcetera.util.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import org.junit.Before;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

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
        "marketcetera";
    protected static final byte[] VALUE_BYTES=
        VALUE.getBytes();


    @Before
    public void deleteTestFile()
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
        try {
            ByteArrayInputStream stdIn=new ByteArrayInputStream(VALUE_BYTES);
            try {
                System.setIn(stdIn);
                testStandardInputStream(stdIn);
            } finally {
                stdIn.close();
            }
        } finally {
            System.setIn(stdInSave);
        }
    }


    protected void testStandardOutputStream
        (ByteArrayOutputStream out)
        throws Exception {}

	protected void testStandardOutputStream()
        throws Exception
    {
        PrintStream stdOutSave=System.out;
        try {
            ByteArrayOutputStream stdOut=new ByteArrayOutputStream();
            try {
                System.setOut(new PrintStream(stdOut));
                testStandardOutputStream(stdOut);
            } finally {
                stdOut.close();
            }
        } finally {
            System.setOut(stdOutSave);
        }
    }


    protected void testStandardErrorStream
        (ByteArrayOutputStream err)
        throws Exception {}

	protected void testStandardErrorStream()
        throws Exception
    {
        PrintStream stdErrSave=System.err;
        try {
            ByteArrayOutputStream stdErr=new ByteArrayOutputStream();
            try {
                System.setErr(new PrintStream(stdErr));
                testStandardErrorStream(stdErr);
            } finally {
                stdErr.close();
            }
        } finally {
            System.setErr(stdErrSave);
        }
    }
}
