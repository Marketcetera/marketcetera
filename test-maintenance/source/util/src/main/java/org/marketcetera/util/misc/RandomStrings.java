package org.marketcetera.util.misc;

import java.nio.charset.Charset;
import java.util.Random;

/**
 * Utilities for random string generation. All random distributions
 * are uniform.
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public final class RandomStrings
{

    // CLASS DATA.

    /**
     * The default maximum length of a randomly generated string whose
     * characters are all Unicode code points that can be encoded by
     * some charset (supplied or implied); the minimum length is 1.
     */

    public static final int DEFAULT_LEN_STR_CHARSET=255;

    /**
     * The default maximum length of a randomly generated string whose
     * characters are all Unicode code points that are deemed valid by
     * {@link StringUtils#isValid(int)}; the minimum length is 1.
     */

    public static final int DEFAULT_LEN_STR_VALID=255;

    /**
     * The default maximum length of a randomly generated string whose
     * characters are all Unicode code points that are digits; the
     * minimum length is 1.
     */

    public static final int DEFAULT_LEN_STR_DIGIT=20;

    /**
     * The default maximum length of a randomly generated string whose
     * characters are all Unicode code points that are letters; the
     * minimum length is 1.
     */

    public static final int DEFAULT_LEN_STR_LETTER=255;

    /**
     * The default maximum length of a randomly generated string whose
     * characters are all Unicode code points that are letters or
     * digits; the minimum length is 1.
     */

    public static final int DEFAULT_LEN_STR_ALNUM=255;

    /**
     * The default maximum length of a randomly generated string whose
     * characters are all Unicode code points; in addition, the first
     * character is always a letter, the second is always a digit, and
     * the rest (if any) are either letters or digits; the minimum
     * length is 2.
     */

    public static final int DEFAULT_LEN_STR_ID=255;

    private static Random sGenerator;
    

    // CLASS METHODS.

    /**
     * Initialization.
     */

    static {
        sGenerator=new Random();
        resetGeneratorRandom();
    }

    /**
     * Resets the random number generator used by this class to the
     * given seed value, which henceforth results in a repeatable,
     * deterministic sequence of random code points and strings.
     *
     * @param seed The seed.
     */

    public static void resetGeneratorRandom
        (long seed)
    {
        sGenerator.setSeed(seed);
    }

    /**
     * Resets the random number generator used by this class to a
     * random seed value, and returns that value.
     *
     * @return The seed.
     */

    public static long resetGeneratorRandom()
    {
        long seed=System.nanoTime();
        resetGeneratorRandom(seed);
        return seed;
    }

    /**
     * Resets the random number generator used by this class to a
     * fixed seed value, which henceforth results in a repeatable,
     * deterministic sequence of random code points and strings.
     */

    public static void resetGeneratorFixed()
    {
        resetGeneratorRandom(0);
    }

    /**
     * Returns a randomly generated Unicode code point within the
     * full range of valid Unicode scalar values.
     *
     * @return The code point.
     */

    public static int genUCP()
    {
        return sGenerator.nextInt
            (Character.MAX_CODE_POINT-Character.MIN_CODE_POINT+1)+
            Character.MIN_CODE_POINT;
    }

    /**
     * Returns a randomly generated Unicode code point that meets the
     * constraints of the given filter.
     *
     * @param filter The filter.
     *
     * @return The code point.
     */

    public static int genUCP
        (UCPFilter filter)
    {
        int[] ucps=UCPFilterInfo.getInfo(filter).getUCPs();
        return ucps[sGenerator.nextInt(ucps.length)];
    }

    /**
     * Returns a randomly generated Unicode code point that can be
     * encoded by the given charset.
     *
     * @param cs The charset.
     *
     * @return The code point.
     */

    public static int genUCPCharset
        (Charset cs)
    {
        return genUCP(UCPFilter.forCharset(cs));
    }

    /**
     * Returns a randomly generated Unicode code point that can be
     * encoded by the default JVM charset.
     *
     * @return The code point.
     */

    public static int genUCPDefCharset()
    {
        return genUCP(UCPFilter.getDefaultCharset());
    }

    /**
     * Returns a randomly generated Unicode code point that can be
     * encoded by the current system file encoding/charset (as
     * specified in the system property <code>file.encoding</code>).
     *
     * @return The code point.
     */

    public static int genUCPFileSystem()
    {
        return genUCP(UCPFilter.getFileSystemCharset());
    }

    /**
     * Returns a randomly generated Unicode code point that is deemed
     * valid by {@link StringUtils#isValid(int)}.
     *
     * @return The code point.
     */

    public static int genUCPValid()
    {
        return genUCP(UCPFilter.VALID);
    }

    /**
     * Returns a randomly generated Unicode code point that is
     * a digit.
     *
     * @return The code point.
     */

    public static int genUCPDigit()
    {
        return genUCP(UCPFilter.DIGIT);
    }

    /**
     * Returns a randomly generated Unicode code point that is
     * a letter.
     *
     * @return The code point.
     */

    public static int genUCPLetter()
    {
        return genUCP(UCPFilter.LETTER);
    }

    /**
     * Returns a randomly generated Unicode code point that is
     * either a letter or a digit.
     *
     * @return The code point.
     */

    public static int genUCPAlNum()
    {
        return genUCP(UCPFilter.ALNUM);
    }

    /**
     * Returns a generated string of the given length whose characters
     * are all Unicode code points that meet the constraints of the
     * given filter.
     *
     * @param filter The filter.
     * @param len The length (in code points).
     *
     * @return The string.
     */

    public static String genStr
        (UCPFilter filter,
         int len)
    {
        int[] ucps=UCPFilterInfo.getInfo(filter).getUCPs();
        StringBuilder builder=new StringBuilder();
        for (int i=0;i<len;i++) {
            builder.appendCodePoint(ucps[sGenerator.nextInt(ucps.length)]);
        }
        return builder.toString();
    }

    /**
     * Returns a randomly generated string of the given length whose
     * characters are all Unicode code points that can be encoded by
     * the given charset.
     *
     * @param cs The charset.
     * @param len The length (in code points).
     *
     * @return The string.
     */

    public static String genStrCharset
        (Charset cs,
         int len)
    {
        return genStr(UCPFilter.forCharset(cs),len);
    }

    /**
     * Returns a randomly generated string of the given length whose
     * characters are all Unicode code points that can be encoded by
     * the default JVM charset.
     *
     * @param len The length (in code points).
     *
     * @return The string.
     */

    public static String genStrDefCharset
        (int len)
    {
        return genStr(UCPFilter.getDefaultCharset(),len);
    }

    /**
     * Returns a randomly generated string of the given length whose
     * characters are all Unicode code points that can be encoded by
     * the current system file encoding/charset (as specified in the
     * system property <code>file.encoding</code>).
     *
     * @param len The length (in code points).
     *
     * @return The string.
     */

    public static String genStrFileSystem
        (int len)
    {
        return genStr(UCPFilter.getFileSystemCharset(),len);
    }

    /**
     * Returns a randomly generated string of the given length whose
     * characters are all Unicode code points that are deemed valid by
     * {@link StringUtils#isValid(int)}.
     *
     * @param len The length (in code points).
     *
     * @return The string.
     */

    public static String genStrValid
        (int len)
    {
        return genStr(UCPFilter.VALID,len);
    }

    /**
     * Returns a randomly generated string of the given length whose
     * characters are all Unicode code points that are digits.
     *
     * @param len The length (in code points).
     *
     * @return The string.
     */

    public static String genStrDigit
        (int len)
    {
        return genStr(UCPFilter.DIGIT,len);
    }

    /**
     * Returns a randomly generated string of the given length whose
     * characters are all Unicode code points that are letters.
     *
     * @param len The length (in code points).
     *
     * @return The string.
     */

    public static String genStrLetter
        (int len)
    {
        return genStr(UCPFilter.LETTER,len);
    }

    /**
     * Returns a randomly generated string of the given length whose
     * characters are all Unicode code points that are letters or
     * digits.
     *
     * @param len The length (in code points).
     *
     * @return The string.
     */

    public static String genStrAlNum
        (int len)
    {
        return genStr(UCPFilter.ALNUM,len);
    }

    /**
     * Returns a randomly generated string of the given length whose
     * characters are all Unicode code points; in addition, the first
     * character (if any) is always a letter, the second (if any) is
     * always a digit, and the rest (if any) are either letters or
     * digits.
     *
     * @param len The length (in code points).
     *
     * @return The string.
     */

    public static String genStrId
        (int len)
    {
        if (len<=0) {
            return org.apache.commons.lang.StringUtils.EMPTY;
        }
        StringBuilder builder=new StringBuilder();
        builder.appendCodePoint(genUCPLetter());
        if (len==1) {
            return builder.toString();
        }
        builder.appendCodePoint(genUCPDigit());
        if (len==2) {
            return builder.toString();
        }
        builder.append(genStrAlNum(len-2));
        return builder.toString();
    }

    /**
     * Returns a randomly generated string of random length (bound by
     * {@link #DEFAULT_LEN_STR_CHARSET}) whose characters are all
     * Unicode code points that can be encoded by the given charset.
     *
     * @param cs The charset.
     *
     * @return The string.
     */

    public static String genStrCharset
        (Charset cs)
    {
        return genStrCharset(cs,sGenerator.nextInt(DEFAULT_LEN_STR_CHARSET)+1);
    }

    /**
     * Returns a randomly generated string of random length (bound by
     * {@link #DEFAULT_LEN_STR_CHARSET}) whose characters are all
     * Unicode code points that can be encoded by the default JVM
     * charset.
     *
     * @return The string.
     */

    public static String genStrDefCharset()
    {
        return genStrDefCharset(sGenerator.nextInt(DEFAULT_LEN_STR_CHARSET)+1);
    }

    /**
     * Returns a randomly generated string of random length (bound by
     * {@link #DEFAULT_LEN_STR_CHARSET}) whose characters are all
     * Unicode code points that can be encoded by the current system
     * file encoding/charset (as specified in the system property
     * <code>file.encoding</code>).
     *
     * @return The string.
     */

    public static String genStrFileSystem()
    {
        return genStrFileSystem(sGenerator.nextInt(DEFAULT_LEN_STR_CHARSET)+1);
    }

    /**
     * Returns a randomly generated string of random length (bound by
     * {@link #DEFAULT_LEN_STR_VALID}) whose characters are all
     * Unicode code points that are deemed valid by {@link
     * StringUtils#isValid(int)}.
     *
     * @return The string.
     */

    public static String genStrValid()
    {
        return genStrValid(sGenerator.nextInt(DEFAULT_LEN_STR_VALID)+1);
    }

    /**
     * Returns a randomly generated string of random length (bound by
     * {@link #DEFAULT_LEN_STR_DIGIT}) whose characters are all
     * Unicode code points that are digits.
     *
     * @return The string.
     */

    public static String genStrDigit()
    {
        return genStrDigit(sGenerator.nextInt(DEFAULT_LEN_STR_DIGIT)+1);
    }

    /**
     * Returns a randomly generated string of random length (bound by
     * {@link #DEFAULT_LEN_STR_LETTER}) whose characters are all
     * Unicode code points that are letters.
     *
     * @return The string.
     */

    public static String genStrLetter()
    {
        return genStrLetter(sGenerator.nextInt(DEFAULT_LEN_STR_LETTER)+1);
    }

    /**
     * Returns a randomly generated string of random length (bound by
     * {@link #DEFAULT_LEN_STR_ALNUM}) whose characters are all
     * Unicode code points that are letters or digits.
     *
     * @return The string.
     */

    public static String genStrAlNum()
    {
        return genStrAlNum(sGenerator.nextInt(DEFAULT_LEN_STR_ALNUM)+1);
    }

    /**
     * Returns a randomly generated string of random length (bound by
     * {@link #DEFAULT_LEN_STR_ID}) whose characters are all Unicode
     * code points; in addition, the first character is always a
     * letter, the second is always a digit, and the rest (if any) are
     * either letters or digits.
     *
     * @return The string.
     */

    public static String genStrId()
    {
        return genStrId(sGenerator.nextInt(DEFAULT_LEN_STR_ID-1)+2);
    }


    // CONSTRUCTOR.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private RandomStrings() {}
}
