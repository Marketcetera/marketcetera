package org.marketcetera.util.test;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Unicode test data. Some were obtained by applying <a
 * href="http://rishida.net/scripts/uniview/conversion.php">an online
 * converter</a> onto the results of <a
 * href="http://translate.google.com/">Google translation</a>.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public final class UnicodeData
{

    // CLASS DATA.

    /**
     * The space character.
     */

    public static final String SPACE=
        " ";

    /**
     * The space character, as a character array.
     */

    public static final char[] SPACE_CHARS=new char[]
        {' '};

    /**
     * The space character, in the default encoding.
     */

    public static final byte[] SPACE_NAT=
        SPACE.getBytes();

    /**
     * The space, in UTF-8.
     */

    public static final byte[] SPACE_UTF8=new byte[]
        {(byte)0x20};

    /**
     * The space, in UTF-16BE.
     */

    public static final byte[] SPACE_UTF16BE=new byte[]
        {(byte)0x00,(byte)0x20};

    /**
     * The space, in UTF-16LE.
     */

    public static final byte[] SPACE_UTF16LE=new byte[]
        {(byte)0x20,(byte)0x00};

    /**
     * The space, in UTF-32BE.
     */

    public static final byte[] SPACE_UTF32BE=new byte[]
        {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x20};

    /**
     * The space, in UTF-32LE.
     */

    public static final byte[] SPACE_UTF32LE=new byte[]
        {(byte)0x20,(byte)0x00,(byte)0x00,(byte)0x00};


    /**
     * "Hello" in English.
     */

    public static final String HELLO_EN=
        "Hello";

    /**
     * "Hello" in English, as a character array.
     */

    public static final char[] HELLO_EN_CHARS=new char[]
        {'H','e','l','l','o'};

    /**
     * "Hello" in English, in the default encoding.
     */

    public static final byte[] HELLO_EN_NAT=
        HELLO_EN.getBytes();

    /**
     * "Hello" in English, in UTF-8.
     */

    public static final byte[] HELLO_EN_UTF8=new byte[]
        {(byte)0x48,
         (byte)0x65,
         (byte)0x6C,
         (byte)0x6C,
         (byte)0x6F};

    /**
     * "Hello" in English, in UTF-16BE.
     */

    public static final byte[] HELLO_EN_UTF16BE=new byte[]
        {(byte)0x00,(byte)0x48,
         (byte)0x00,(byte)0x65,
         (byte)0x00,(byte)0x6C,
         (byte)0x00,(byte)0x6C,
         (byte)0x00,(byte)0x6F};

    /**
     * "Hello" in English, in UTF-16LE.
     */

    public static final byte[] HELLO_EN_UTF16LE=new byte[]
        {(byte)0x48,(byte)0x00,
         (byte)0x65,(byte)0x00,
         (byte)0x6C,(byte)0x00,
         (byte)0x6C,(byte)0x00,
         (byte)0x6F,(byte)0x00};

    /**
     * "Hello" in English, in UTF-32BE.
     */

    public static final byte[] HELLO_EN_UTF32BE=new byte[]
        {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x48,
         (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x65,
         (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x6C,
         (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x6C,
         (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x6F};

    /**
     * "Hello" in English, in UTF-32LE.
     */

    public static final byte[] HELLO_EN_UTF32LE=new byte[]
        {(byte)0x48,(byte)0x00,(byte)0x00,(byte)0x00,
         (byte)0x65,(byte)0x00,(byte)0x00,(byte)0x00,
         (byte)0x6C,(byte)0x00,(byte)0x00,(byte)0x00,
         (byte)0x6C,(byte)0x00,(byte)0x00,(byte)0x00,
         (byte)0x6F,(byte)0x00,(byte)0x00,(byte)0x00};


    /**
     * "Language" (pronounced "sprook") in Norwegian: this is the word
     * "language" in Norwegian, with the first letter capitalized.
     */

    public static final String LANGUAGE_NO=
        "Spr\u00E5k";

    /**
     * "Language" in Norwegian, as a character array.
     */

    public static final char[] LANGUAGE_NO_CHARS=new char[]
        {'S','p','r','\u00E5','k'};

    /**
     * "Language" in Norwegian, in the default encoding.
     */

    public static final byte[] LANGUAGE_NO_NAT=
        LANGUAGE_NO.getBytes();

    /**
     * "Language" in Norwegian, in UTF-8.
     */

    public static final byte[] LANGUAGE_NO_UTF8=new byte[]
        {(byte)0x53,
         (byte)0x70,
         (byte)0x72,
         (byte)0xC3,
         (byte)0xA5,
         (byte)0x6B};

    /**
     * "Language" in Norwegian, in UTF-16BE.
     */

    public static final byte[] LANGUAGE_NO_UTF16BE=new byte[]
        {(byte)0x00,(byte)0x53,
         (byte)0x00,(byte)0x70,
         (byte)0x00,(byte)0x72,
         (byte)0x00,(byte)0xE5,
         (byte)0x00,(byte)0x6B};

    /**
     * "Language" in Norwegian, in UTF-16LE.
     */

    public static final byte[] LANGUAGE_NO_UTF16LE=new byte[]
        {(byte)0x53,(byte)0x00,
         (byte)0x70,(byte)0x00,
         (byte)0x72,(byte)0x00,
         (byte)0xE5,(byte)0x00,
         (byte)0x6B,(byte)0x00};

    /**
     * "Language" in Norwegian, in UTF-32BE.
     */

    public static final byte[] LANGUAGE_NO_UTF32BE=new byte[]
        {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x53,
         (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x70,
         (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x72,
         (byte)0x00,(byte)0x00,(byte)0x00,(byte)0xE5,
         (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x6B};

    /**
     * "Language" in Norwegian, in UTF-32LE.
     */

    public static final byte[] LANGUAGE_NO_UTF32LE=new byte[]
        {(byte)0x53,(byte)0x00,(byte)0x00,(byte)0x00,
         (byte)0x70,(byte)0x00,(byte)0x00,(byte)0x00,
         (byte)0x72,(byte)0x00,(byte)0x00,(byte)0x00,
         (byte)0xE5,(byte)0x00,(byte)0x00,(byte)0x00,
         (byte)0x6B,(byte)0x00,(byte)0x00,(byte)0x00};


    /**
     * "HELLO" (pronounced "yassou") in Greek: this is the word
     * "hello" in all uppercase Greek letters (it is, in fact, two
     * Greek words, separated by a space).
     */

    public static final String HELLO_GR=
        "\u0393\u0395\u0399\u0391 \u03A3\u039F\u03A5";

    /**
     * "HELLO" in Greek, as a character array.
     */

    public static final char[] HELLO_GR_CHARS=new char[]
        {'\u0393','\u0395','\u0399','\u0391',
         ' ',
         '\u03A3','\u039F','\u03A5'};

    /**
     * "HELLO" in Greek, in the default encoding.
     */

    public static final byte[] HELLO_GR_NAT=
        HELLO_GR.getBytes();

    /**
     * "HELLO" in Greek, in UTF-8.
     */

    public static final byte[] HELLO_GR_UTF8=new byte[]
        {(byte)0xCE,(byte)0x93,
         (byte)0xCE,(byte)0x95,
         (byte)0xCE,(byte)0x99,
         (byte)0xCE,(byte)0x91,
         (byte)0x20,
         (byte)0xCE,(byte)0xA3,
         (byte)0xCE,(byte)0x9F,
         (byte)0xCE,(byte)0xA5};

    /**
     * "HELLO" in Greek, in UTF-16BE.
     */

    public static final byte[] HELLO_GR_UTF16BE=new byte[]
        {(byte)0x03,(byte)0x93,
         (byte)0x03,(byte)0x95,
         (byte)0x03,(byte)0x99,
         (byte)0x03,(byte)0x91,
         (byte)0x00,(byte)0x20,
         (byte)0x03,(byte)0xA3,
         (byte)0x03,(byte)0x9F,
         (byte)0x03,(byte)0xA5};

    /**
     * "HELLO" in Greek, in UTF-16LE.
     */

    public static final byte[] HELLO_GR_UTF16LE=new byte[]
        {(byte)0x93,(byte)0x03,
         (byte)0x95,(byte)0x03,
         (byte)0x99,(byte)0x03,
         (byte)0x91,(byte)0x03,
         (byte)0x20,(byte)0x00,
         (byte)0xA3,(byte)0x03,
         (byte)0x9F,(byte)0x03,
         (byte)0xA5,(byte)0x03};

    /**
     * "HELLO" in Greek, in UTF-32BE.
     */

    public static final byte[] HELLO_GR_UTF32BE=new byte[]
        {(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x93,
         (byte)0x00,(byte)0x00,(byte)0x03,(byte)0x95,
         (byte)0x00,(byte)0x00,(byte)0x03,(byte)0x99,
         (byte)0x00,(byte)0x00,(byte)0x03,(byte)0x91,
         (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x20,
         (byte)0x00,(byte)0x00,(byte)0x03,(byte)0xA3,
         (byte)0x00,(byte)0x00,(byte)0x03,(byte)0x9F,
         (byte)0x00,(byte)0x00,(byte)0x03,(byte)0xA5};

    /**
     * "HELLO" in Greek, in UTF-32LE.
     */

    public static final byte[] HELLO_GR_UTF32LE=new byte[]
        {(byte)0x93,(byte)0x03,(byte)0x00,(byte)0x00,
         (byte)0x95,(byte)0x03,(byte)0x00,(byte)0x00,
         (byte)0x99,(byte)0x03,(byte)0x00,(byte)0x00,
         (byte)0x91,(byte)0x03,(byte)0x00,(byte)0x00,
         (byte)0x20,(byte)0x00,(byte)0x00,(byte)0x00,
         (byte)0xA3,(byte)0x03,(byte)0x00,(byte)0x00,
         (byte)0x9F,(byte)0x03,(byte)0x00,(byte)0x00,
         (byte)0xA5,(byte)0x03,(byte)0x00,(byte)0x00};


    /**
     * "house" (pronounced "manzil") in Arabic.
     */

    public static final String HOUSE_AR=
        "\u0645\u0646\u0632\u0644";

    /**
     * "house" in Arabic, as a character array.
     */

    public static final char[] HOUSE_AR_CHARS=new char[]
        {'\u0645','\u0646','\u0632','\u0644'};

    /**
     * "house" in Arabic, in the default encoding.
     */

    public static final byte[] HOUSE_AR_NAT=
        HOUSE_AR.getBytes();

    /**
     * "house" in Arabic, in UTF-8.
     */

    public static final byte[] HOUSE_AR_UTF8=new byte[]
        {(byte)0xD9,(byte)0x85,
         (byte)0xD9,(byte)0x86,
         (byte)0xD8,(byte)0xB2,
         (byte)0xD9,(byte)0x84};

    /**
     * "house" in Arabic, in UTF-16BE.
     */

    public static final byte[] HOUSE_AR_UTF16BE=new byte[]
        {(byte)0x06,(byte)0x45,
         (byte)0x06,(byte)0x46,
         (byte)0x06,(byte)0x32,
         (byte)0x06,(byte)0x44};

    /**
     * "house" in Arabic, in UTF-16LE.
     */

    public static final byte[] HOUSE_AR_UTF16LE=new byte[]
        {(byte)0x45,(byte)0x06,
         (byte)0x46,(byte)0x06,
         (byte)0x32,(byte)0x06,
         (byte)0x44,(byte)0x06};

    /**
     * "house" in Arabic, in UTF-32BE.
     */

    public static final byte[] HOUSE_AR_UTF32BE=new byte[]
        {(byte)0x00,(byte)0x00,(byte)0x06,(byte)0x45,
         (byte)0x00,(byte)0x00,(byte)0x06,(byte)0x46,
         (byte)0x00,(byte)0x00,(byte)0x06,(byte)0x32,
         (byte)0x00,(byte)0x00,(byte)0x06,(byte)0x44};

    /**
     * "house" in Arabic, in UTF-32LE.
     */

    public static final byte[] HOUSE_AR_UTF32LE=new byte[]
        {(byte)0x45,(byte)0x06,(byte)0x00,(byte)0x00,
         (byte)0x46,(byte)0x06,(byte)0x00,(byte)0x00,
         (byte)0x32,(byte)0x06,(byte)0x00,(byte)0x00,
         (byte)0x44,(byte)0x06,(byte)0x00,(byte)0x00};


    /**
     * "goodbye" (pronounced "sayonara") in Japanese, in the Hiragana
     * writing system.
     */

    public static final String GOODBYE_JA=
        "\u3055\u3088\u3046\u306A\u3089";

    /**
     * "goodbye" in Japanese, as a character array.
     */

    public static final char[] GOODBYE_JA_CHARS=new char[]
        {'\u3055','\u3088','\u3046','\u306A','\u3089'};

    /**
     * "goodbye" in Japanese, in the default encoding.
     */

    public static final byte[] GOODBYE_JA_NAT=
        GOODBYE_JA.getBytes();

    /**
     * "goodbye" in Japanese, in UTF-8.
     */

    public static final byte[] GOODBYE_JA_UTF8=new byte[]
        {(byte)0xE3,(byte)0x81,(byte)0x95,
         (byte)0xE3,(byte)0x82,(byte)0x88,
         (byte)0xE3,(byte)0x81,(byte)0x86,
         (byte)0xE3,(byte)0x81,(byte)0xAA,
         (byte)0xE3,(byte)0x82,(byte)0x89};

    /**
     * "goodbye" in Japanese, in UTF-16BE.
     */

    public static final byte[] GOODBYE_JA_UTF16BE=new byte[]
        {(byte)0x30,(byte)0x55,
         (byte)0x30,(byte)0x88,
         (byte)0x30,(byte)0x46,
         (byte)0x30,(byte)0x6A,
         (byte)0x30,(byte)0x89};

    /**
     * "goodbye" in Japanese, in UTF-16LE.
     */

    public static final byte[] GOODBYE_JA_UTF16LE=new byte[]
        {(byte)0x55,(byte)0x30,
         (byte)0x88,(byte)0x30,
         (byte)0x46,(byte)0x30,
         (byte)0x6A,(byte)0x30,
         (byte)0x89,(byte)0x30};

    /**
     * "goodbye" in Japanese, in UTF-32BE.
     */

    public static final byte[] GOODBYE_JA_UTF32BE=new byte[]
        {(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x55,
         (byte)0x00,(byte)0x00,(byte)0x30,(byte)0x88,
         (byte)0x00,(byte)0x00,(byte)0x30,(byte)0x46,
         (byte)0x00,(byte)0x00,(byte)0x30,(byte)0x6A,
         (byte)0x00,(byte)0x00,(byte)0x30,(byte)0x89};

    /**
     * "goodbye" in Japanese, in UTF-32LE.
     */

    public static final byte[] GOODBYE_JA_UTF32LE=new byte[]
        {(byte)0x55,(byte)0x30,(byte)0x00,(byte)0x00,
         (byte)0x88,(byte)0x30,(byte)0x00,(byte)0x00,
         (byte)0x46,(byte)0x30,(byte)0x00,(byte)0x00,
         (byte)0x6A,(byte)0x30,(byte)0x00,(byte)0x00,
         (byte)0x89,(byte)0x30,(byte)0x00,(byte)0x00};


    /**
     * The musical symbol G-clef.
     */

    public static final String G_CLEF=
        "\uD834\uDD1E";

    /**
     * The G-clef, as a character array.
     */

    public static final char[] G_CLEF_CHARS=new char[]
        {'\uD834','\uDD1E'};

    /**
     * The G-clef, in the default encoding.
     */

    public static final byte[] G_CLEF_NAT=
        G_CLEF.getBytes();

    /**
     * The G-clef, in UTF-8.
     */

    public static final byte[] G_CLEF_UTF8=new byte[]
        {(byte)0xF0,(byte)0x9D,(byte)0x84,(byte)0x9E};

    /**
     * The G-clef, in UTF-16BE.
     */

    public static final byte[] G_CLEF_UTF16BE=new byte[] {
        (byte)0xD8,(byte)0x34,(byte)0xDD,(byte)0x1E};

    /**
     * The G-clef, in UTF-16LE.
     */

    public static final byte[] G_CLEF_UTF16LE=new byte[] {
        (byte)0x34,(byte)0xD8,(byte)0x1E,(byte)0xDD};

    /**
     * The G-clef, in UTF-32BE.
     */

    public static final byte[] G_CLEF_UTF32BE=new byte[] {
        (byte)0x00,(byte)0x01,(byte)0xD1,(byte)0x1E};

    /**
     * The G-clef, in UTF-32LE.
     */

    public static final byte[] G_CLEF_UTF32LE=new byte[] {
        (byte)0x1E,(byte)0xD1,(byte)0x01,(byte)0x00};


    /**
     * A combo string that includes "Hello" in English, "Language" in
     * Norwegian, "HELLO" in Greek, "house" in Arabic, "goodbye" in
     * Japanese, and the G-clef, each successive pair separated by
     * exactly one space.
     */

    public static final String COMBO=
        HELLO_EN+SPACE+
        LANGUAGE_NO+SPACE+
        HELLO_GR+SPACE+
        HOUSE_AR+SPACE+
        GOODBYE_JA+SPACE+
        G_CLEF;

    /**
     * The combo string, as a character array.
     */

    public static final char[] COMBO_CHARS=concat
        (HELLO_EN_CHARS,SPACE_CHARS,
         LANGUAGE_NO_CHARS,SPACE_CHARS,
         HELLO_GR_CHARS,SPACE_CHARS,
         HOUSE_AR_CHARS,SPACE_CHARS,
         GOODBYE_JA_CHARS,SPACE_CHARS,
         G_CLEF_CHARS);

    /**
     * The combo string, in the default encoding.
     */

    public static final byte[] COMBO_NAT=concat
        (HELLO_EN_NAT,SPACE_NAT,
         LANGUAGE_NO_NAT,SPACE_NAT,
         HELLO_GR_NAT,SPACE_NAT,
         HOUSE_AR_NAT,SPACE_NAT,
         GOODBYE_JA_NAT,SPACE_NAT,
         G_CLEF_NAT);

    /**
     * The combo string, in UTF-8.
     */

    public static final byte[] COMBO_UTF8=concat
        (HELLO_EN_UTF8,SPACE_UTF8,
         LANGUAGE_NO_UTF8,SPACE_UTF8,
         HELLO_GR_UTF8,SPACE_UTF8,
         HOUSE_AR_UTF8,SPACE_UTF8,
         GOODBYE_JA_UTF8,SPACE_UTF8,
         G_CLEF_UTF8);

    /**
     * The combo string, in UTF-16BE.
     */

    public static final byte[] COMBO_UTF16BE=concat
        (HELLO_EN_UTF16BE,SPACE_UTF16BE,
         LANGUAGE_NO_UTF16BE,SPACE_UTF16BE,
         HELLO_GR_UTF16BE,SPACE_UTF16BE,
         HOUSE_AR_UTF16BE,SPACE_UTF16BE,
         GOODBYE_JA_UTF16BE,SPACE_UTF16BE,
         G_CLEF_UTF16BE);

    /**
     * The combo string, in UTF-16LE.
     */

    public static final byte[] COMBO_UTF16LE=concat
        (HELLO_EN_UTF16LE,SPACE_UTF16LE,
         LANGUAGE_NO_UTF16LE,SPACE_UTF16LE,
         HELLO_GR_UTF16LE,SPACE_UTF16LE,
         HOUSE_AR_UTF16LE,SPACE_UTF16LE,
         GOODBYE_JA_UTF16LE,SPACE_UTF16LE,
         G_CLEF_UTF16LE);

    /**
     * The combo string, in UTF-32BE.
     */

    public static final byte[] COMBO_UTF32BE=concat
        (HELLO_EN_UTF32BE,SPACE_UTF32BE,
         LANGUAGE_NO_UTF32BE,SPACE_UTF32BE,
         HELLO_GR_UTF32BE,SPACE_UTF32BE,
         HOUSE_AR_UTF32BE,SPACE_UTF32BE,
         GOODBYE_JA_UTF32BE,SPACE_UTF32BE,
         G_CLEF_UTF32BE);

    /**
     * The combo string, in UTF-32LE.
     */

    public static final byte[] COMBO_UTF32LE=concat
        (HELLO_EN_UTF32LE,SPACE_UTF32LE,
         LANGUAGE_NO_UTF32LE,SPACE_UTF32LE,
         HELLO_GR_UTF32LE,SPACE_UTF32LE,
         HOUSE_AR_UTF32LE,SPACE_UTF32LE,
         GOODBYE_JA_UTF32LE,SPACE_UTF32LE,
         G_CLEF_UTF32LE);


    // CLASS METHODS.

    /**
     * Concatenates the given byte arrays and returns the result.
     *
     * @param arrays The arrays.
     *
     * @return The result.
     */

    private static byte[] concat
        (byte[]... arrays)
    {
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        try {
            for (byte[] array:arrays) {
                out.write(array);
            }
            out.close();
        } catch (IOException ex) {
            fail("Cannot merge arrays");
        }
        return out.toByteArray();
    }

    /**
     * Concatenates the given character arrays and returns the result.
     *
     * @param arrays The arrays.
     *
     * @return The result.
     */

    private static char[] concat
        (char[]... arrays)
    {
        CharArrayWriter out=new CharArrayWriter();
        try {
            for (char[] array:arrays) {
                out.write(array);
            }
            out.close();
        } catch (IOException ex) {
            fail("Cannot merge arrays");
        }
        return out.toCharArray();
    }


    // CONSTRUCTOR.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private UnicodeData() {}
}
