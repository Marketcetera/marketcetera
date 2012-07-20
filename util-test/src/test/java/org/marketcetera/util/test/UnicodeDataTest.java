package org.marketcetera.util.test;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import org.apache.commons.lang.CharEncoding;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.UnicodeData.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class UnicodeDataTest
    extends TestCaseBase
{
    private static final Charset NAT=
        Charset.defaultCharset();
    private static final Charset UTF8=
        Charset.forName(CharEncoding.UTF_8);
    private static final Charset UTF16BE=
        Charset.forName(CharEncoding.UTF_16BE);
    private static final Charset UTF16LE=
        Charset.forName(CharEncoding.UTF_16LE);
    private static final Charset UTF32BE=
        Charset.forName("UTF-32BE");
    private static final Charset UTF32LE=
        Charset.forName("UTF-32LE");

    private static final String TEST_FILE_PREFIX=
        DIR_TARGET+File.separator+"unicode_";


    private static void singleValid
        (String str,
         char[] chars,
         int[] ucps,
         byte[] nat,
         byte[] utf8,
         byte[] utf16be,
         byte[] utf16le,
         byte[] utf32be,
         byte[] utf32le)
    {
        assertArrayEquals(str.toCharArray(),chars);
        int i=0;
        int j=0;
        while (i<str.length()) {
            int ucp=str.codePointAt(i);
            assertEquals("At code point position "+j,ucp,ucps[j++]);
            i+=Character.charCount(ucp);
        }
        assertArrayEquals(str.getBytes(),nat);
        assertArrayEquals(str.getBytes(UTF8),utf8);
        assertArrayEquals(str.getBytes(UTF16BE),utf16be);
        assertArrayEquals(str.getBytes(UTF16LE),utf16le);
        assertArrayEquals(str.getBytes(UTF32BE),utf32be);
        assertArrayEquals(str.getBytes(UTF32LE),utf32le);
    }

    private static void singleInvalid
        (Charset cs,
         byte[] encoded)
    {
        CharsetDecoder dec=cs.newDecoder();
        dec.onMalformedInput(CodingErrorAction.REPORT);
        try {
            dec.decode(ByteBuffer.wrap(encoded));
            fail();
        } catch (CharacterCodingException ex) {
            // Desired.
        }
    }

    private static void writeFile
        (Charset charset,
         String fileName,
         byte[] data)
        throws Exception
    {
        FileOutputStream out=new FileOutputStream
            (TEST_FILE_PREFIX+fileName+".xml");
        out.write(("<?xml version=\"1.0\" encoding=\""+
                   charset.name()+"\"?><root>").getBytes(charset));
        out.write(data);
        out.write("</root>".getBytes(charset));
        out.close();
    }


    @Test
    public void valid()
    {
        singleValid(SPACE,SPACE_CHARS,SPACE_UCPS,
                    SPACE_NAT,SPACE_UTF8,
                    SPACE_UTF16BE,SPACE_UTF16LE,
                    SPACE_UTF32BE,SPACE_UTF32LE);
        singleValid(HELLO_EN,HELLO_EN_CHARS,HELLO_EN_UCPS,
                    HELLO_EN_NAT,HELLO_EN_UTF8,
                    HELLO_EN_UTF16BE,HELLO_EN_UTF16LE,
                    HELLO_EN_UTF32BE,HELLO_EN_UTF32LE);
        singleValid(LANGUAGE_NO,LANGUAGE_NO_CHARS,LANGUAGE_NO_UCPS,
                    LANGUAGE_NO_NAT,LANGUAGE_NO_UTF8,
                    LANGUAGE_NO_UTF16BE,LANGUAGE_NO_UTF16LE,
                    LANGUAGE_NO_UTF32BE,LANGUAGE_NO_UTF32LE);
        singleValid(HELLO_GR,HELLO_GR_CHARS,HELLO_GR_UCPS,
                    HELLO_GR_NAT,HELLO_GR_UTF8,
                    HELLO_GR_UTF16BE,HELLO_GR_UTF16LE,
                    HELLO_GR_UTF32BE,HELLO_GR_UTF32LE);
        singleValid(HOUSE_AR,HOUSE_AR_CHARS,HOUSE_AR_UCPS,
                    HOUSE_AR_NAT,HOUSE_AR_UTF8,
                    HOUSE_AR_UTF16BE,HOUSE_AR_UTF16LE,
                    HOUSE_AR_UTF32BE,HOUSE_AR_UTF32LE);
        singleValid(GOODBYE_JA,GOODBYE_JA_CHARS,GOODBYE_JA_UCPS,
                    GOODBYE_JA_NAT,GOODBYE_JA_UTF8,
                    GOODBYE_JA_UTF16BE,GOODBYE_JA_UTF16LE,
                    GOODBYE_JA_UTF32BE,GOODBYE_JA_UTF32LE);
        singleValid(GOATS_LNB,GOATS_LNB_CHARS,GOATS_LNB_UCPS,
                    GOATS_LNB_NAT,GOATS_LNB_UTF8,
                    GOATS_LNB_UTF16BE,GOATS_LNB_UTF16LE,
                    GOATS_LNB_UTF32BE,GOATS_LNB_UTF32LE);
        singleValid(G_CLEF_MSC,G_CLEF_MSC_CHARS,G_CLEF_MSC_UCPS,
                    G_CLEF_MSC_NAT,G_CLEF_MSC_UTF8,
                    G_CLEF_MSC_UTF16BE,G_CLEF_MSC_UTF16LE,
                    G_CLEF_MSC_UTF32BE,G_CLEF_MSC_UTF32LE);
        singleValid(COMBO,COMBO_CHARS,COMBO_UCPS,
                    COMBO_NAT,COMBO_UTF8,
                    COMBO_UTF16BE,COMBO_UTF16LE,
                    COMBO_UTF32BE,COMBO_UTF32LE);
    }

    @Test
    public void invalid()
    {
        CharsetEncoder enc=UTF8.newEncoder();
        assertFalse(enc.canEncode(INVALID));
        assertFalse(enc.canEncode(new String(INVALID_CHARS)));
        assertFalse(enc.canEncode
                    (new String(INVALID_UCPS,0,INVALID_UCPS.length)));

        singleInvalid(UTF8,INVALID_UTF8);
        singleInvalid(UTF16BE,INVALID_UTF16BE);
        singleInvalid(UTF16LE,INVALID_UTF16LE);
        singleInvalid(UTF32BE,INVALID_UTF32BE);
        singleInvalid(UTF32LE,INVALID_UTF32LE);
    }

    /**
     * Writes a set of files that can opened using external
     * unicode-aware tools (e.g. Firefox) to check visually the test
     * data.
     *
     * @throws Exception Thrown if an I/O error occurs.
     */

    @Test
    public void writeFiles()
        throws Exception
    {
        writeFile(NAT,"native",COMBO_NAT);
        writeFile(UTF8,"utf8",COMBO_UTF8);
        writeFile(UTF16BE,"utf16be",COMBO_UTF16BE);
        writeFile(UTF16LE,"utf16le",COMBO_UTF16LE);
        writeFile(UTF32BE,"utf32be",COMBO_UTF32BE);
        writeFile(UTF32LE,"utf32le",COMBO_UTF32LE);
    }
}
