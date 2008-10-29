package org.marketcetera.util.misc;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;
import org.marketcetera.util.unicode.UnicodeCharset;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class RandomStringsTest
    extends TestCaseBase
{
    private static final int RANDOM_ITERATION_COUNT=
        100;
    private static final int CHAR_ITERATION_COUNT=
        100;
    private static final int STR_LEN=
        20;
    private static final int STR_LEN_ITERATION_COUNT=
        100;
    private static final int STR_ITERATION_COUNT=
        100;

    private static final UCPFilter UCP_FILTER_CUSTOM=
        new UCPFilter()
        {
            @Override
            public boolean isAcceptable(int ucp)
            {
                return ((ucp>=0x100) && (ucp<=0x102));
            }
        };
    public static final UCPFilter UCP_FILTER_ALL=
        new UCPFilter ()
        {
            @Override
            public boolean isAcceptable(int ucp)
            {
                return ((ucp>=Character.MIN_CODE_POINT) &&
                        (ucp<=Character.MAX_CODE_POINT));
            }
        };
    public static final UCPFilter UCP_FILTER_UTF8=
        UCPFilter.forCharset(UnicodeCharset.UTF8.getCharset());
    public static final UCPFilter UCP_FILTER_DC=
        UCPFilter.getDefaultCharset();
    public static final UCPFilter UCP_FILTER_FS=
        UCPFilter.getFileSystemCharset();


    private static interface UCPGenerator
    {
        int generate();
    }

    private static interface StrGenerator
    {
        String generate();
    }


    private static void testUCP
        (UCPGenerator generator,
         UCPFilter checker)
    {
        int ucpFirst=generator.generate();
        boolean foundDifferent=false;
        for (int i=0;i<CHAR_ITERATION_COUNT;i++) {
            int ucp=generator.generate();
            assertTrue("Value was "+Integer.toHexString(ucp),
                       checker.isAcceptable(ucp));
            if (ucp!=ucpFirst) {
                foundDifferent=true;
            }
        }
        assertTrue(foundDifferent);
    }

    private static void testStrLen
        (StrGenerator generator,
         UCPFilter checker)
    {
        String sFirst=generator.generate();
        boolean foundDifferent=false;
        for (int i=0;i<STR_LEN_ITERATION_COUNT;i++) {
            String s=generator.generate();
            int[] ucps=StringUtils.toUCPArray(s);
            assertEquals(STR_LEN,ucps.length);
            for (int ucp:ucps) {                
                assertTrue("Value was "+Integer.toHexString(ucp),
                           checker.isAcceptable(ucp));
            }
            if (!s.equals(sFirst)) {
                foundDifferent=true;
            }
        }
        assertTrue(foundDifferent);
    }

    private static void testStr
        (int minLen,
         int maxLen,
         StrGenerator generator,
         UCPFilter checker)
    {
        String sFirst=generator.generate();
        boolean foundDifferent=false;
        for (int i=0;i<STR_ITERATION_COUNT;i++) {
            String s=generator.generate();
            int[] ucps=StringUtils.toUCPArray(s);
            assertTrue("Length is "+ucps.length,
                       ((ucps.length>=minLen) && (ucps.length<=maxLen)));
            for (int ucp:ucps) {                
                assertTrue("Value was "+Integer.toHexString(ucp),
                           checker.isAcceptable(ucp));
            }
            if (!s.equals(sFirst)) {
                foundDifferent=true;
            }
        }
        assertTrue(foundDifferent);
    }

    private static void testStrId
        (int len,
         String s)
    {
        int[] ucps=StringUtils.toUCPArray(s);
        assertEquals(len,ucps.length);
        if (len>=1) {
            assertTrue("Value was "+Integer.toHexString(ucps[0]),
                       Character.isLetter(ucps[0]));
        }
        if (len>=2) {
            assertTrue("Value was "+Integer.toHexString(ucps[1]),
                       Character.isDigit(ucps[1]));
        }
        for (int i=2;i<len;i++) {
            assertTrue("Value was "+Integer.toHexString(ucps[i]),
                       Character.isLetterOrDigit(ucps[i]));
        }
    }


    @Before
    public void setupRandomStringsTest()
    {
        RandomStrings.resetGeneratorFixed();
    }


    @Test
    public void reset()
    {
        int ucp=RandomStrings.genUCP();
        for (int i=0;i<RANDOM_ITERATION_COUNT;i++) {
            RandomStrings.resetGeneratorFixed();
            assertEquals(ucp,RandomStrings.genUCP());
        }

        long seed=RandomStrings.resetGeneratorRandom();
        ucp=RandomStrings.genUCP();
        for (int i=0;i<RANDOM_ITERATION_COUNT;i++) {
            RandomStrings.resetGeneratorRandom(seed);
            assertEquals(ucp,RandomStrings.genUCP());
        }

        boolean failure=true;
        seed=RandomStrings.resetGeneratorRandom();
        for (int i=0;i<RANDOM_ITERATION_COUNT;i++) {
            if (seed!=RandomStrings.resetGeneratorRandom()) {
                failure=false;
                break;
            }
        }
        if (failure) {
            fail("Cannot create random seed");
        }

        failure=true;
        RandomStrings.resetGeneratorRandom();
        ucp=RandomStrings.genUCP();
        for (int i=0;i<RANDOM_ITERATION_COUNT;i++) {
            RandomStrings.resetGeneratorRandom();
            if (ucp!=RandomStrings.genUCP()) {
                failure=false;
                break;
            }
        }
        if (failure) {
            fail("Cannot produce random numbers");
        }
    }

    @Test
    public void genUCP()
    {
        testUCP
            (new UCPGenerator() {
                @Override
                public int generate() {
                    return RandomStrings.genUCP(); }},
                UCP_FILTER_ALL);
        testUCP
            (new UCPGenerator() {
                @Override
                public int generate() {
                    return RandomStrings.genUCP(UCP_FILTER_CUSTOM); }},
                UCP_FILTER_CUSTOM);
        testUCP
            (new UCPGenerator() {
                @Override
                public int generate() {
                    return RandomStrings.genUCPCharset
                        (UnicodeCharset.UTF8.getCharset()); }},
                UCP_FILTER_UTF8);
        testUCP
            (new UCPGenerator() {
                @Override
                public int generate() {
                    return RandomStrings.genUCPDefCharset(); }},
                UCP_FILTER_DC);
        testUCP
            (new UCPGenerator() {
                @Override
                public int generate() {
                    return RandomStrings.genUCPFileSystem(); }},
                UCP_FILTER_FS);
        testUCP
            (new UCPGenerator() {
                @Override
                public int generate() {
                    return RandomStrings.genUCPValid(); }},
                UCPFilter.VALID);
        testUCP
            (new UCPGenerator() {
                @Override
                public int generate() {
                    return RandomStrings.genUCPDigit(); }},
                UCPFilter.DIGIT);
        testUCP
            (new UCPGenerator() {
                @Override
                public int generate() {
                    return RandomStrings.genUCPLetter(); }},
                UCPFilter.LETTER);
        testUCP
            (new UCPGenerator() {
                @Override
                public int generate() {
                    return RandomStrings.genUCPAlNum(); }},
                UCPFilter.ALNUM);
    }

    @Test
    public void genStrLen()
    {
        assertEquals(org.apache.commons.lang.StringUtils.EMPTY,
                     RandomStrings.genStr(UCP_FILTER_CUSTOM,0));
        testStrLen
            (new StrGenerator() {
                @Override
                public String generate() {
                    return RandomStrings.genStr
                        (UCP_FILTER_CUSTOM,STR_LEN); }},
                UCP_FILTER_CUSTOM);

        assertEquals(org.apache.commons.lang.StringUtils.EMPTY,
                     RandomStrings.genStrCharset
                     (UnicodeCharset.UTF8.getCharset(),0));
        testStrLen
            (new StrGenerator() {
                @Override
                public String generate() {
                    return RandomStrings.genStrCharset
                        (UnicodeCharset.UTF8.getCharset(),STR_LEN); }},
                UCP_FILTER_UTF8);

        assertEquals(org.apache.commons.lang.StringUtils.EMPTY,
                     RandomStrings.genStrDefCharset(0));
        testStrLen
            (new StrGenerator() {
                @Override
                public String generate() {
                    return RandomStrings.genStrDefCharset(STR_LEN); }},
                UCP_FILTER_DC);

        assertEquals(org.apache.commons.lang.StringUtils.EMPTY,
                     RandomStrings.genStrFileSystem(0));
        testStrLen
            (new StrGenerator() {
                @Override
                public String generate() {
                    return RandomStrings.genStrFileSystem(STR_LEN); }},
                UCP_FILTER_FS);

        assertEquals(org.apache.commons.lang.StringUtils.EMPTY,
                     RandomStrings.genStrValid(0));
        testStrLen
            (new StrGenerator() {
                @Override
                public String generate() {
                    return RandomStrings.genStrValid(STR_LEN); }},
                UCPFilter.VALID);

        assertEquals(org.apache.commons.lang.StringUtils.EMPTY,
                     RandomStrings.genStrDigit(0));
        testStrLen
            (new StrGenerator() {
                @Override
                public String generate() {
                    return RandomStrings.genStrDigit(STR_LEN); }},
                UCPFilter.DIGIT);

        assertEquals(org.apache.commons.lang.StringUtils.EMPTY,
                     RandomStrings.genStrLetter(0));
        testStrLen
            (new StrGenerator() {
                @Override
                public String generate() {
                    return RandomStrings.genStrLetter(STR_LEN); }},
                UCPFilter.LETTER);

        assertEquals(org.apache.commons.lang.StringUtils.EMPTY,
                     RandomStrings.genStrAlNum(0));
        testStrLen
            (new StrGenerator() {
                @Override
                public String generate() {
                    return RandomStrings.genStrAlNum(STR_LEN); }},
                UCPFilter.ALNUM);
    }

    @Test
    public void genStrIdLen()
    {

        assertEquals(org.apache.commons.lang.StringUtils.EMPTY,
                     RandomStrings.genStrId(0));
        testStrId(1,RandomStrings.genStrId(1));
        testStrId(2,RandomStrings.genStrId(2));

        String sFirst=RandomStrings.genStrId(STR_LEN);
        boolean foundDifferent=false;
        for (int i=0;i<STR_LEN_ITERATION_COUNT;i++) {
            String s=RandomStrings.genStrId(STR_LEN);
            testStrId(STR_LEN,s);
            if (!s.equals(sFirst)) {
                foundDifferent=true;
            }
        }
        assertTrue(foundDifferent);
    }

    @Test
    public void genStr()
    {
        testStr
            (1,RandomStrings.DEFAULT_LEN_STR_CHARSET,new StrGenerator() {
                @Override
                public String generate() {
                    return RandomStrings.genStrCharset
                        (UnicodeCharset.UTF8.getCharset()); }},
                UCP_FILTER_UTF8);
        testStr
            (1,RandomStrings.DEFAULT_LEN_STR_CHARSET,new StrGenerator() {
                @Override
                public String generate() {
                    return RandomStrings.genStrDefCharset(); }},
                UCP_FILTER_DC);
        testStr
            (1,RandomStrings.DEFAULT_LEN_STR_CHARSET,new StrGenerator() {
                @Override
                public String generate() {
                    return RandomStrings.genStrFileSystem(); }},
                UCP_FILTER_FS);
        testStr
            (1,RandomStrings.DEFAULT_LEN_STR_VALID,new StrGenerator() {
                @Override
                public String generate() {
                    return RandomStrings.genStrValid(); }},
                UCPFilter.VALID);
        testStr
            (1,RandomStrings.DEFAULT_LEN_STR_DIGIT,new StrGenerator() {
                @Override
                public String generate() {
                    return RandomStrings.genStrDigit(); }},
                UCPFilter.DIGIT);
        testStr
            (1,RandomStrings.DEFAULT_LEN_STR_LETTER,new StrGenerator() {
                @Override
                public String generate() {
                    return RandomStrings.genStrLetter(); }},
                UCPFilter.LETTER);
        testStr
            (1,RandomStrings.DEFAULT_LEN_STR_ALNUM,new StrGenerator() {
                @Override
                public String generate() {
                    return RandomStrings.genStrAlNum(); }},
                UCPFilter.ALNUM);
    }

    @Test
    public void genStrId()
    {
        String sFirst=RandomStrings.genStrId();
        boolean foundDifferent=false;
        for (int i=0;i<STR_ITERATION_COUNT;i++) {
            String s=RandomStrings.genStrId();
            int[] ucps=StringUtils.toUCPArray(s);
            assertTrue("Length is "+ucps.length,
                       ((ucps.length>=2) &&
                        (ucps.length<=RandomStrings.DEFAULT_LEN_STR_ID)));
            assertTrue("Value was "+Integer.toHexString(ucps[0]),
                       Character.isLetter(ucps[0]));
            assertTrue("Value was "+Integer.toHexString(ucps[1]),
                       Character.isDigit(ucps[1]));
            for (int j=2;j<ucps.length;j++) {
                assertTrue("Value was "+Integer.toHexString(ucps[j]),
                           Character.isLetterOrDigit(ucps[j]));
            }
            if (!s.equals(sFirst)) {
                foundDifferent=true;
            }
        }
        assertTrue(foundDifferent);
    }
}
