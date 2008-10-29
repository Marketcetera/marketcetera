package org.marketcetera.util.unicode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import org.marketcetera.util.file.CloseableRegistry;
import org.marketcetera.util.file.InputStreamWrapper;
import org.marketcetera.util.file.ReaderWrapper;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.UnicodeData.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class ReaderTest
    extends TestCaseBase
{
    @Test
    public void regularReader()
        throws Exception
    {
        CloseableRegistry r=new CloseableRegistry();
        try {
            ByteArrayInputStream is=new ByteArrayInputStream(HELLO_EN_NAT);
            r.register(new InputStreamWrapper(is));
            UnicodeInputStreamReader reader=
                new UnicodeInputStreamReader(is);
            r.register(new ReaderWrapper(reader));

            assertNull(reader.getDecodingStrategy());
            assertNull(reader.getRequestedSignatureCharset());
            assertNull(reader.getSignatureCharset());

            assertTrue(reader.ready());

            assertFalse(reader.markSupported());

            try {
                reader.mark(0);
                fail();
            } catch (IOException ex) {
                // Desired.
            }

            try {
                reader.reset();
                fail();
            } catch (IOException ex) {
                // Desired.
            }

            assertEquals(HELLO_EN.charAt(0),reader.read());

            assertEquals(1,reader.skip(1));

            char[] charArray=new char[1];
            assertEquals(1,reader.read(charArray));
            assertEquals(HELLO_EN.charAt(2),charArray[0]);

            charArray=new char[3];
            assertEquals(1,reader.read(charArray,1,1));
            assertEquals(HELLO_EN.charAt(3),charArray[1]);

            CharBuffer charBuffer=CharBuffer.allocate(10);
            assertEquals(1,reader.read(charBuffer));
            assertEquals(HELLO_EN.charAt(4),charBuffer.get(0));

            assertEquals(-1,reader.read());
            assertEquals(-1,reader.read(charArray));
            assertEquals(-1,reader.read(charArray,1,1));
            assertEquals(-1,reader.read(charBuffer));
            assertFalse(reader.ready());

            reader.close();
            reader.close();

            // Ensure that close() has closed the stream, by trying to
            // read from the reader: this is not testing whether
            // read() fails; it tests whether close() worked.
            try {
                reader.read();
                fail();
            } catch (IOException ex) {
                // Desired.
            }

            try {
                reader.ready();
                fail();
            } catch (IOException ex) {
                // Desired.
            }
        } finally {
            r.close();
        }
    }

    @Test
    public void emptyReader()
        throws Exception
    {
        CloseableRegistry r=new CloseableRegistry();
        r=new CloseableRegistry();
        try {
            ByteArrayInputStream is=new ByteArrayInputStream
                (ArrayUtils.EMPTY_BYTE_ARRAY);
            r.register(new InputStreamWrapper(is));
            UnicodeInputStreamReader reader=
                new UnicodeInputStreamReader(is);
            r.register(new ReaderWrapper(reader));

            assertNull(reader.getDecodingStrategy());
            assertNull(reader.getRequestedSignatureCharset());
            assertNull(reader.getSignatureCharset());

            assertFalse(reader.ready());

            assertFalse(reader.markSupported());

            try {
                reader.mark(0);
                fail();
            } catch (IOException ex) {
                // Desired.
            }

            try {
                reader.reset();
                fail();
            } catch (IOException ex) {
                // Desired.
            }

            assertEquals(-1,reader.read());

            char[] charArray=new char[1];
            assertEquals(-1,reader.read(charArray));

            charArray=new char[3];
            assertEquals(-1,reader.read(charArray,1,1));

            CharBuffer charBuffer=CharBuffer.allocate(10);
            assertEquals(-1,reader.read(charBuffer));

            reader.close();
            reader.close();

            // Ensure that close() has closed the stream, by trying to
            // read from the reader: this is not testing whether
            // read() fails; it tests whether close() worked.
            try {
                reader.read();
                fail();
            } catch (IOException ex) {
                // Desired.
            }

            try {
                reader.ready();
                fail();
            } catch (IOException ex) {
                // Desired.
            }
        } finally {
            r.close();
        }
    }
}
