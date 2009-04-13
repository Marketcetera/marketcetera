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
        " "; //$NON-NLS-1$

    /**
     * The space character, as a character array.
     */

    public static final char[] SPACE_CHARS=new char[]
        {' '};

    /**
     * The space character, as a Unicode code point array.
     */

    public static final int[] SPACE_UCPS=new int[]
        {0x00020};

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
        "Hello"; //$NON-NLS-1$

    /**
     * "Hello" in English, as a character array.
     */

    public static final char[] HELLO_EN_CHARS=new char[]
        {'H','e','l','l','o'};

    /**
     * "Hello" in English, as a Unicode code point array.
     */

    public static final int[] HELLO_EN_UCPS=new int[]
        {0x00048,
         0x00065,
         0x0006C,
         0x0006C,
         0x0006F};

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
        "Spr\u00E5k"; //$NON-NLS-1$

    /**
     * "Language" in Norwegian, as a character array.
     */

    public static final char[] LANGUAGE_NO_CHARS=new char[]
        {'S','p','r','\u00E5','k'};

    /**
     * "Language" in Norwegian, as a Unicode code point array.
     */

    public static final int[] LANGUAGE_NO_UCPS=new int[]
        {0x00053,
         0x00070,
         0x00072,
         0x000E5,
         0x0006B};

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
        "\u0393\u0395\u0399\u0391 \u03A3\u039F\u03A5"; //$NON-NLS-1$

    /**
     * "HELLO" in Greek, as a character array.
     */

    public static final char[] HELLO_GR_CHARS=new char[]
        {'\u0393','\u0395','\u0399','\u0391',
         ' ',
         '\u03A3','\u039F','\u03A5'};

    /**
     * "HELLO" in Greek, as a Unicode code point array.
     */

    public static final int[] HELLO_GR_UCPS=new int[]
        {0x00393,
         0x00395,
         0x00399,
         0x00391,
         0x00020,
         0x003A3,
         0x0039F,
         0x003A5};

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
        "\u0645\u0646\u0632\u0644"; //$NON-NLS-1$

    /**
     * "house" in Arabic, as a character array.
     */

    public static final char[] HOUSE_AR_CHARS=new char[]
        {'\u0645','\u0646','\u0632','\u0644'};

    /**
     * "house" in Arabic, as a Unicode code point array.
     */

    public static final int[] HOUSE_AR_UCPS=new int[]
        {0x00645,
         0x00646,
         0x00632,
         0x00644};

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
        "\u3055\u3088\u3046\u306A\u3089"; //$NON-NLS-1$

    /**
     * "goodbye" in Japanese, as a character array.
     */

    public static final char[] GOODBYE_JA_CHARS=new char[]
        {'\u3055','\u3088','\u3046','\u306A','\u3089'};

    /**
     * "goodbye" in Japanese, as a Unicode code point array.
     */

    public static final int[] GOODBYE_JA_UCPS=new int[]
        {0x03055,
         0x03088,
         0x03046,
         0x0306A,
         0x03089};

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
     * The Linear B ideograms for she-goat and he-goat (in this order
     * and separated by a space).
     */

    public static final String GOATS_LNB=
        "\uD800\uDC88 \uD800\uDC89"; //$NON-NLS-1$

    /**
     * The Linear B goat ideograms, as a character array.
     */

    public static final char[] GOATS_LNB_CHARS=new char[]
        {'\uD800','\uDC88',
         ' ',
         '\uD800','\uDC89'};

    /**
     * The Linear B goat ideograms, as a Unicode code point array.
     */

    public static final int[] GOATS_LNB_UCPS=new int[]
        {0x10088,
         0x00020,
         0x10089};

    /**
     * The Linear B goat ideograms, in the default encoding.
     */

    public static final byte[] GOATS_LNB_NAT=
        GOATS_LNB.getBytes();

    /**
     * The Linear B goat ideograms, in UTF-8.
     */

    public static final byte[] GOATS_LNB_UTF8=new byte[]
        {(byte)0xF0,(byte)0x90,(byte)0x82,(byte)0x88,
         (byte)0x20,
         (byte)0xF0,(byte)0x90,(byte)0x82,(byte)0x89};

    /**
     * The Linear B goat ideograms, in UTF-16BE.
     */

    public static final byte[] GOATS_LNB_UTF16BE=new byte[]
        {(byte)0xD8,(byte)0x00,(byte)0xDC,(byte)0x88,
         (byte)0x00,(byte)0x20,
         (byte)0xD8,(byte)0x00,(byte)0xDC,(byte)0x89};

    /**
     * The Linear B goat ideograms, in UTF-16LE.
     */

    public static final byte[] GOATS_LNB_UTF16LE=new byte[]
        {(byte)0x00,(byte)0xD8,(byte)0x88,(byte)0xDC,
         (byte)0x20,(byte)0x00,
         (byte)0x00,(byte)0xD8,(byte)0x89,(byte)0xDC};

    /**
     * The Linear B goat ideograms, in UTF-32BE.
     */

    public static final byte[] GOATS_LNB_UTF32BE=new byte[]
        {(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x88,
         (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x20,
         (byte)0x00,(byte)0x01,(byte)0x00,(byte)0x89};

    /**
     * The Linear B goat ideograms, in UTF-32LE.
     */

    public static final byte[] GOATS_LNB_UTF32LE=new byte[]
        {(byte)0x88,(byte)0x00,(byte)0x01,(byte)0x00,
         (byte)0x20,(byte)0x00,(byte)0x00,(byte)0x00,
         (byte)0x89,(byte)0x00,(byte)0x01,(byte)0x00};


    /**
     * The musical symbol G-clef.
     */

    public static final String G_CLEF_MSC=
        "\uD834\uDD1E"; //$NON-NLS-1$

    /**
     * The G-clef, as a character array.
     */

    public static final char[] G_CLEF_MSC_CHARS=new char[]
        {'\uD834','\uDD1E'};

    /**
     * The G-clef, as a Unicode code point array.
     */

    public static final int[] G_CLEF_MSC_UCPS=new int[]
        {0x1D11E};

    /**
     * The G-clef, in the default encoding.
     */

    public static final byte[] G_CLEF_MSC_NAT=
        G_CLEF_MSC.getBytes();

    /**
     * The G-clef, in UTF-8.
     */

    public static final byte[] G_CLEF_MSC_UTF8=new byte[]
        {(byte)0xF0,(byte)0x9D,(byte)0x84,(byte)0x9E};

    /**
     * The G-clef, in UTF-16BE.
     */

    public static final byte[] G_CLEF_MSC_UTF16BE=new byte[]
        {(byte)0xD8,(byte)0x34,(byte)0xDD,(byte)0x1E};

    /**
     * The G-clef, in UTF-16LE.
     */

    public static final byte[] G_CLEF_MSC_UTF16LE=new byte[]
        {(byte)0x34,(byte)0xD8,(byte)0x1E,(byte)0xDD};

    /**
     * The G-clef, in UTF-32BE.
     */

    public static final byte[] G_CLEF_MSC_UTF32BE=new byte[]
        {(byte)0x00,(byte)0x01,(byte)0xD1,(byte)0x1E};

    /**
     * The G-clef, in UTF-32LE.
     */

    public static final byte[] G_CLEF_MSC_UTF32LE=new byte[]
        {(byte)0x1E,(byte)0xD1,(byte)0x01,(byte)0x00};


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
        GOATS_LNB+SPACE+
        G_CLEF_MSC;

    /**
     * The combo string, as a character array.
     */

    public static final char[] COMBO_CHARS=concat
        (HELLO_EN_CHARS,SPACE_CHARS,
         LANGUAGE_NO_CHARS,SPACE_CHARS,
         HELLO_GR_CHARS,SPACE_CHARS,
         HOUSE_AR_CHARS,SPACE_CHARS,
         GOODBYE_JA_CHARS,SPACE_CHARS,
         GOATS_LNB_CHARS,SPACE_CHARS,
         G_CLEF_MSC_CHARS);

    /**
     * The combo string, as a Unicode code point array.
     */

    public static final int[] COMBO_UCPS=concat
        (HELLO_EN_UCPS,SPACE_UCPS,
         LANGUAGE_NO_UCPS,SPACE_UCPS,
         HELLO_GR_UCPS,SPACE_UCPS,
         HOUSE_AR_UCPS,SPACE_UCPS,
         GOODBYE_JA_UCPS,SPACE_UCPS,
         GOATS_LNB_UCPS,SPACE_UCPS,
         G_CLEF_MSC_UCPS);

    /**
     * The combo string, in the default encoding.
     */

    public static final byte[] COMBO_NAT=concat
        (HELLO_EN_NAT,SPACE_NAT,
         LANGUAGE_NO_NAT,SPACE_NAT,
         HELLO_GR_NAT,SPACE_NAT,
         HOUSE_AR_NAT,SPACE_NAT,
         GOODBYE_JA_NAT,SPACE_NAT,
         GOATS_LNB_NAT,SPACE_NAT,
         G_CLEF_MSC_NAT);

    /**
     * The combo string, in UTF-8.
     */

    public static final byte[] COMBO_UTF8=concat
        (HELLO_EN_UTF8,SPACE_UTF8,
         LANGUAGE_NO_UTF8,SPACE_UTF8,
         HELLO_GR_UTF8,SPACE_UTF8,
         HOUSE_AR_UTF8,SPACE_UTF8,
         GOODBYE_JA_UTF8,SPACE_UTF8,
         GOATS_LNB_UTF8,SPACE_UTF8,
         G_CLEF_MSC_UTF8);

    /**
     * The combo string, in UTF-16BE.
     */

    public static final byte[] COMBO_UTF16BE=concat
        (HELLO_EN_UTF16BE,SPACE_UTF16BE,
         LANGUAGE_NO_UTF16BE,SPACE_UTF16BE,
         HELLO_GR_UTF16BE,SPACE_UTF16BE,
         HOUSE_AR_UTF16BE,SPACE_UTF16BE,
         GOODBYE_JA_UTF16BE,SPACE_UTF16BE,
         GOATS_LNB_UTF16BE,SPACE_UTF16BE,
         G_CLEF_MSC_UTF16BE);

    /**
     * The combo string, in UTF-16LE.
     */

    public static final byte[] COMBO_UTF16LE=concat
        (HELLO_EN_UTF16LE,SPACE_UTF16LE,
         LANGUAGE_NO_UTF16LE,SPACE_UTF16LE,
         HELLO_GR_UTF16LE,SPACE_UTF16LE,
         HOUSE_AR_UTF16LE,SPACE_UTF16LE,
         GOODBYE_JA_UTF16LE,SPACE_UTF16LE,
         GOATS_LNB_UTF16LE,SPACE_UTF16LE,
         G_CLEF_MSC_UTF16LE);

    /**
     * The combo string, in UTF-32BE.
     */

    public static final byte[] COMBO_UTF32BE=concat
        (HELLO_EN_UTF32BE,SPACE_UTF32BE,
         LANGUAGE_NO_UTF32BE,SPACE_UTF32BE,
         HELLO_GR_UTF32BE,SPACE_UTF32BE,
         HOUSE_AR_UTF32BE,SPACE_UTF32BE,
         GOODBYE_JA_UTF32BE,SPACE_UTF32BE,
         GOATS_LNB_UTF32BE,SPACE_UTF32BE,
         G_CLEF_MSC_UTF32BE);

    /**
     * The combo string, in UTF-32LE.
     */

    public static final byte[] COMBO_UTF32LE=concat
        (HELLO_EN_UTF32LE,SPACE_UTF32LE,
         LANGUAGE_NO_UTF32LE,SPACE_UTF32LE,
         HELLO_GR_UTF32LE,SPACE_UTF32LE,
         HOUSE_AR_UTF32LE,SPACE_UTF32LE,
         GOODBYE_JA_UTF32LE,SPACE_UTF32LE,
         GOATS_LNB_UTF32LE,SPACE_UTF32LE,
         G_CLEF_MSC_UTF32LE);


    /**
     * An invalid string, comprising an isolated 16-bit surrogate.
     */

    public static final String INVALID=
        "\uD800"; //$NON-NLS-1$

    /**
     * An invalid string, comprising an isolated 16-bit surrogate, as
     * a character array.
     */

    public static final char[] INVALID_CHARS=new char[]
        {'\uD800'};

    /**
     * A Unicode code point comprising an isolated surrogate code
     * point.
     */

    public static final int[] INVALID_UCPS=new int[]
        {0xD800};

    /**
     * A byte array comprising an invalid UTF-8 byte sequence (the
     * first 3 bytes of a 4-byte sequence).
     */

    public static final byte[] INVALID_UTF8=new byte[]
        {(byte)0xF0,(byte)0x90,(byte)0x82};

    /**
     * A byte array comprising an invalid UTF-16BE byte sequence (an
     * isolated 16-bit surrogate).
     */

    public static final byte[] INVALID_UTF16BE=new byte[]
        {(byte)0xD8,(byte)0x00};

    /**
     * A byte array comprising an invalid UTF-16LE byte sequence (an
     * isolated 16-bit surrogate).
     */

    public static final byte[] INVALID_UTF16LE=new byte[]
        {(byte)0x00,(byte)0xD8};

    /**
     * A byte array comprising an invalid UTF-32BE byte sequence (a
     * 32-bit value outside the valid range for Unicode scalar values).
     */

    public static final byte[] INVALID_UTF32BE=new byte[]
        {(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x00};

    /**
     * A byte array comprising an invalid UTF-32LE byte sequence (a
     * 32-bit value outside the valid range for Unicode scalar values).
     */

    public static final byte[] INVALID_UTF32LE=new byte[]
        {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10};


    // CLASS METHODS.

    /**
     * Concatenates the given byte arrays and returns the result.
     *
     * @param arrays The arrays.
     *
     * @return The concatenated arrays.
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
            fail("Cannot merge arrays"); //$NON-NLS-1$
        }
        return out.toByteArray();
    }

    /**
     * Concatenates the given integer arrays and returns the result.
     *
     * @param arrays The arrays.
     *
     * @return The concatenated arrays.
     */

    private static int[] concat
        (int[]... arrays)
    {
        int len=0;
        for (int[] array:arrays) {
            len+=array.length;
        }
        int[] result=new int[len];
        int i=0;
        for (int[] array:arrays) {
            for (int c:array) {
                result[i++]=c;
            }
        }
        return result;
    }

    /**
     * Concatenates the given character arrays and returns the result.
     *
     * @param arrays The arrays.
     *
     * @return The concatenated arrays.
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
            fail("Cannot merge arrays"); //$NON-NLS-1$
        }
        return out.toCharArray();
    }


    // CONSTRUCTOR.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private UnicodeData() {}
}
