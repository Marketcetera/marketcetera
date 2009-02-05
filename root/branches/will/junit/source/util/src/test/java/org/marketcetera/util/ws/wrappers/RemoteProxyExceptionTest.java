package org.marketcetera.util.ws.wrappers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
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


    @Test
    public void all()
    {
        assertEquality(new RemoteProxyException
                       (TEST_MESSAGE,TEST_TRACE,TEST_STRING),
                       new RemoteProxyException
                       (TEST_MESSAGE,TEST_TRACE,TEST_STRING),
                       new RemoteProxyException
                       (TEST_MESSAGE+"d",TEST_TRACE,TEST_STRING),
                       new RemoteProxyException
                       (TEST_MESSAGE,new String[] {"d"},TEST_STRING),
                       new RemoteProxyException
                       (TEST_MESSAGE,TEST_TRACE,TEST_STRING+"d"));

        RemoteProxyException ex=new RemoteProxyException
            (TEST_MESSAGE,TEST_TRACE,TEST_STRING);

        assertEquals(TEST_MESSAGE,ex.getMessage());
        assertArrayEquals(TEST_TRACE,ex.getTraceCapture());

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
        assertEquals(TEST_TRACE_PRINT,new String(byteArray.toByteArray()));

        r=new CloseableRegistry();
        byteArray=new ByteArrayOutputStream();
        try {
            r.register(byteArray);
            PrintStream out=new PrintStream(byteArray);
            r.register(out);
            ex.printStackTrace(out);
        } finally {
            r.close();
        }
        assertEquals(TEST_TRACE_PRINT,new String(byteArray.toByteArray()));

        r=new CloseableRegistry();
        StringWriter string=new StringWriter();
        try {
            r.register(string);
            PrintWriter out=new PrintWriter(string);
            r.register(out);
            ex.printStackTrace(out);
        } finally {
            r.close();
        }
        assertEquals(TEST_TRACE_PRINT,string.toString());

        assertNull(ex.getStackTrace());

        assertEquals(TEST_STRING,ex.toString());
    }
}
