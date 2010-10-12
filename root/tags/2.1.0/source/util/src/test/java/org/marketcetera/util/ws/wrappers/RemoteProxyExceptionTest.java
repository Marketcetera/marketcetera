package org.marketcetera.util.ws.wrappers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.Test;
import org.marketcetera.util.file.CloseableRegistry;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.EqualityAssert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class RemoteProxyExceptionTest
    extends TestCaseBase
{
    private static final String TEST_MESSAGE=
        "testMessage";
    private static final String[] TEST_TRACE=
        new String[] {"testTrace"};
    private static final String TEST_TRACE_PRINT=
        "testTrace"+SystemUtils.LINE_SEPARATOR;
    private static final String TEST_STRING=
        "testString";
    private static final String TEST_CLASS=
        "testClass";


    private static String getStackTraceNoArg
        (RemoteProxyException ex)
    {
        PrintStream stdErrSave=System.err;
        CloseableRegistry r=new CloseableRegistry();
        ByteArrayOutputStream byteArray=new ByteArrayOutputStream();
        try {
            r.register(byteArray);
            PrintStream stdErr=new PrintStream(byteArray);
            r.register(stdErr);
            System.setErr(stdErr);
            ex.printStackTrace();
        } finally {
            System.setErr(stdErrSave);
            r.close();
        }
        return new String(byteArray.toByteArray());
    }

    private static String getStackTraceStream
        (RemoteProxyException ex)
    {
        CloseableRegistry r=new CloseableRegistry();
        ByteArrayOutputStream byteArray=new ByteArrayOutputStream();
        try {
            r.register(byteArray);
            PrintStream out=new PrintStream(byteArray);
            r.register(out);
            ex.printStackTrace(out);
        } finally {
            r.close();
        }
        return new String(byteArray.toByteArray());
    }

    private static String getStackTraceWriter
        (RemoteProxyException ex)
    {
        CloseableRegistry r=new CloseableRegistry();
        StringWriter string=new StringWriter();
        try {
            r.register(string);
            PrintWriter out=new PrintWriter(string);
            r.register(out);
            ex.printStackTrace(out);
        } finally {
            r.close();
        }
        return string.toString();
    }


    @Test
    public void basics()
    {
        assertEquality(new RemoteProxyException
                       (TEST_MESSAGE,TEST_TRACE,TEST_STRING,TEST_CLASS),
                       new RemoteProxyException
                       (TEST_MESSAGE,TEST_TRACE,TEST_STRING,TEST_CLASS),
                       new RemoteProxyException
                       (null,TEST_TRACE,TEST_STRING,TEST_CLASS),
                       new RemoteProxyException
                       (TEST_MESSAGE,null,TEST_STRING,TEST_CLASS),
                       new RemoteProxyException
                       (TEST_MESSAGE,TEST_TRACE,null,TEST_CLASS),
                       new RemoteProxyException
                       (TEST_MESSAGE,TEST_TRACE,TEST_STRING,null),
                       new RemoteProxyException
                       (TEST_MESSAGE+"d",TEST_TRACE,TEST_STRING,TEST_CLASS),
                       new RemoteProxyException
                       (TEST_MESSAGE,new String[] {"d"},TEST_STRING,TEST_CLASS),
                       new RemoteProxyException
                       (TEST_MESSAGE,TEST_TRACE,TEST_STRING+"d",TEST_CLASS),
                       new RemoteProxyException
                       (TEST_MESSAGE,TEST_TRACE,TEST_STRING,TEST_CLASS+"d"));

        RemoteProxyException ex=new RemoteProxyException
            (TEST_MESSAGE,TEST_TRACE,TEST_STRING,TEST_CLASS);
        assertEquals(TEST_MESSAGE,ex.getMessage());
        assertArrayEquals(TEST_TRACE,ex.getTraceCapture());
        assertEquals(TEST_TRACE_PRINT,getStackTraceNoArg(ex));
        assertEquals(TEST_TRACE_PRINT,getStackTraceStream(ex));
        assertEquals(TEST_TRACE_PRINT,getStackTraceWriter(ex));
        assertNull(ex.getStackTrace());
        assertEquals(TEST_STRING,ex.toString());
        assertEquals(TEST_CLASS,ex.getServerName());
    }

    @Test
    public void nullParams()
    {
        assertEquality(new RemoteProxyException
                       (null,null,null,null),
                       new RemoteProxyException
                       (null,null,null,null),
                       new RemoteProxyException
                       (TEST_MESSAGE,TEST_TRACE,TEST_STRING,TEST_CLASS));

        RemoteProxyException ex=new RemoteProxyException
            (null,null,null,null);
        assertNull(ex.getMessage());
        assertNull(ex.getTraceCapture());
        assertEquals(StringUtils.EMPTY,getStackTraceNoArg(ex));
        assertEquals(StringUtils.EMPTY,getStackTraceStream(ex));
        assertEquals(StringUtils.EMPTY,getStackTraceWriter(ex));
        assertNull(ex.getStackTrace());
        assertNull(ex.toString());
        assertNull(ex.getServerName());
    }
}
