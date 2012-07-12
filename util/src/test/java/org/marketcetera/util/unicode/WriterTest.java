package org.marketcetera.util.unicode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import org.marketcetera.util.file.CloseableRegistry;
import org.marketcetera.util.file.OutputStreamWrapper;
import org.marketcetera.util.file.WriterWrapper;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class WriterTest
    extends TestCaseBase
{
    @Test
    public void writer()
        throws Exception
    {
        CloseableRegistry r=new CloseableRegistry();
        try {
            ByteArrayOutputStream os=new ByteArrayOutputStream();
            r.register(new OutputStreamWrapper(os));
            UnicodeOutputStreamWriter writer=
                new UnicodeOutputStreamWriter(os);
            r.register(new WriterWrapper(writer));

            assertNull(writer.getRequestedSignatureCharset());
            assertNull(writer.getSignatureCharset());

            assertArrayEquals(ArrayUtils.EMPTY_BYTE_ARRAY,os.toByteArray());

            writer.write('a');
            writer.flush();
            assertEquals("a",new String(os.toByteArray()));

            writer.write(new char[] {'b','c'});
            writer.flush();
            assertEquals("abc",new String(os.toByteArray()));

            writer.write(new char[] {'b','d','e','f'},1,2);
            writer.flush();
            assertEquals("abcde",new String(os.toByteArray()));

            writer.write("fg");
            writer.flush();
            assertEquals("abcdefg",new String(os.toByteArray()));

            writer.write("ghij",1,2);
            writer.flush();
            assertEquals("abcdefghi",new String(os.toByteArray()));

            writer.append("jk");
            writer.flush();
            assertEquals("abcdefghijk",new String(os.toByteArray()));

            writer.append("klmn",1,3);
            writer.flush();
            assertEquals("abcdefghijklm",new String(os.toByteArray()));

            writer.append('n');
            writer.flush();
            assertEquals("abcdefghijklmn",new String(os.toByteArray()));

            writer.close();
            writer.close();

            // Ensure that close() has closed the stream, by trying to
            // write to the writer: this is not testing whether
            // write() fails; it tests whether close() worked.
            try {
                writer.write('a');
                fail();
            } catch (IOException ex) {
                // Desired.
            }
        } finally {
            r.close();
        }
    }
}
