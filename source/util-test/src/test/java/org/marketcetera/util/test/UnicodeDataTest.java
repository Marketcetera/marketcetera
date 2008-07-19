package org.marketcetera.util.test;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
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


    private static void single
        (String str,
         char[] chars,
         byte[] nat,
         byte[] utf8,
         byte[] utf16be,
         byte[] utf16le,
         byte[] utf32be,
         byte[] utf32le)
    {
        assertArrayEquals(str.toCharArray(),chars);
        assertArrayEquals(str.getBytes(),nat);
        assertArrayEquals(str.getBytes(UTF8),utf8);
        assertArrayEquals(str.getBytes(UTF16BE),utf16be);
        assertArrayEquals(str.getBytes(UTF16LE),utf16le);
        assertArrayEquals(str.getBytes(UTF32BE),utf32be);
        assertArrayEquals(str.getBytes(UTF32LE),utf32le);
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
    public void all()
    {
        single(SPACE,SPACE_CHARS,
               SPACE_NAT,SPACE_UTF8,
               SPACE_UTF16BE,SPACE_UTF16LE,
               SPACE_UTF32BE,SPACE_UTF32LE);
        single(HELLO_EN,HELLO_EN_CHARS,
               HELLO_EN_NAT,HELLO_EN_UTF8,
               HELLO_EN_UTF16BE,HELLO_EN_UTF16LE,
               HELLO_EN_UTF32BE,HELLO_EN_UTF32LE);
        single(LANGUAGE_NO,LANGUAGE_NO_CHARS,
               LANGUAGE_NO_NAT,LANGUAGE_NO_UTF8,
               LANGUAGE_NO_UTF16BE,LANGUAGE_NO_UTF16LE,
               LANGUAGE_NO_UTF32BE,LANGUAGE_NO_UTF32LE);
        single(HELLO_GR,HELLO_GR_CHARS,
               HELLO_GR_NAT,HELLO_GR_UTF8,
               HELLO_GR_UTF16BE,HELLO_GR_UTF16LE,
               HELLO_GR_UTF32BE,HELLO_GR_UTF32LE);
        single(HOUSE_AR,HOUSE_AR_CHARS,
               HOUSE_AR_NAT,HOUSE_AR_UTF8,
               HOUSE_AR_UTF16BE,HOUSE_AR_UTF16LE,
               HOUSE_AR_UTF32BE,HOUSE_AR_UTF32LE);
        single(GOODBYE_JA,GOODBYE_JA_CHARS,
               GOODBYE_JA_NAT,GOODBYE_JA_UTF8,
               GOODBYE_JA_UTF16BE,GOODBYE_JA_UTF16LE,
               GOODBYE_JA_UTF32BE,GOODBYE_JA_UTF32LE);
        single(G_CLEF,G_CLEF_CHARS,
               G_CLEF_NAT,G_CLEF_UTF8,
               G_CLEF_UTF16BE,G_CLEF_UTF16LE,
               G_CLEF_UTF32BE,G_CLEF_UTF32LE);
        single(COMBO,COMBO_CHARS,
               COMBO_NAT,COMBO_UTF8,
               COMBO_UTF16BE,COMBO_UTF16LE,
               COMBO_UTF32BE,COMBO_UTF32LE);
    }

    /**
     * Writes a set of files that can opened using external
     * unicode-aware tools (e.g. Firefox) to check visually the test
     * data.
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
