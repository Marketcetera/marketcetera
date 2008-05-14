package org.marketcetera.util.except;

import java.util.Locale;
import org.junit.Before;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

public class I18NThrowableTestBase
	extends TestCaseBase
{
    protected static final String TEST_MSG_1=
        "Test message 1 (expected)";
    protected static final String TEST_MSG_2=
        "Test message 2 (expected)";

    protected static final String BOT_MSG=
        "provider 'except_test'; id 'bot_exception'; entry 'msg'; "+
        "parameters ()";
    protected static final String BOT_MSG_EN=
        "Bottom-level test exception (expected)";
    protected static final String BOT_MSG_FR=
        "Niveau bas test exception (attendu)";

    protected static final String MID_MSG_PARAM=
        "a";
    protected static final String MID_MSG=
        "provider 'except_test'; id 'mid_exception'; entry 'msg'; "+
        "parameters ('"+MID_MSG_PARAM+"')";
    protected static final String MID_MSG_EN=
        "Middle-level test exception (expected) "+MID_MSG_PARAM;
    protected static final String MID_MSG_FR=
        "Niveau moyen test exception (attendu) "+MID_MSG_PARAM;

    protected static final String TOP_MSG=
        "provider 'except_test'; id 'top_exception'; entry 'msg'; "+
        "parameters ()";
    protected static final String TOP_MSG_ALL=
        "Top-level test exception (expected)";

    @Before
    public void setup()
    {
        Messages.PROVIDER.setLocale(Locale.US);
    }

    protected static void empty
        (Throwable t1,
         I18NThrowable t2)
    {
        assertNull(t1.getMessage());
        assertNull(t1.getLocalizedMessage());
        assertEquals(t1.getClass().getName(),t1.toString());

        assertNull(t2.getI18NProvider());
        assertNull(t2.getI18NMessage());
        assertNull(t2.getParams());
        assertNull(t2.getCause());

        assertNull(t2.getMessage());
        assertNull(t2.getLocalizedMessage());
        assertNull(t2.getDetail());
        assertNull(t2.getLocalizedDetail());
        assertEquals(t2.getClass().getName(),t2.toString());

        Messages.PROVIDER.setLocale(Locale.FRENCH);
        assertNull(t2.getMessage());
        assertNull(t2.getLocalizedMessage());
        assertNull(t2.getDetail());
        assertNull(t2.getLocalizedDetail());
        assertEquals(t2.getClass().getName(),t2.toString());
    }

    protected static void causeWithoutMessage
        (Throwable tNested,
         Throwable t1,
         I18NThrowable t2)
    {
        String nestedMessage=tNested.getClass().getName();

        assertEquals(nestedMessage,t1.getMessage());
        assertEquals(nestedMessage,t1.getLocalizedMessage());
        assertEquals(t1.getClass().getName()+": "+nestedMessage,
                     t1.toString());

        assertNull(t2.getI18NProvider());
        assertNull(t2.getI18NMessage());
        assertNull(t2.getParams());
        assertEquals(tNested,t2.getCause());

        assertEquals(nestedMessage,t2.getMessage());
        assertEquals(nestedMessage,t2.getLocalizedMessage());
        assertNull(t2.getDetail());
        assertNull(t2.getLocalizedDetail());
        assertEquals(t2.getClass().getName()+": "+nestedMessage,
                     t2.toString());

        Messages.PROVIDER.setLocale(Locale.FRENCH);
        assertEquals(nestedMessage,t2.getMessage());
        assertEquals(nestedMessage,t2.getLocalizedMessage());
        assertNull(t2.getDetail());
        assertNull(t2.getLocalizedDetail());
        assertEquals(t2.getClass().getName()+": "+nestedMessage,
                     t2.toString());
    }

    protected static void causeWithMessage
        (Throwable tNested,
         Throwable t1,
         I18NThrowable t2)
    {
        String nestedMessage=tNested.getClass().getName()+": "+TEST_MSG_1;

        assertEquals(nestedMessage,t1.getMessage());
        assertEquals(nestedMessage,t1.getLocalizedMessage());
        assertEquals(t1.getClass().getName()+": "+nestedMessage,
                     t1.toString());

        assertNull(t2.getI18NProvider());
        assertNull(t2.getI18NMessage());
        assertNull(t2.getParams());
        assertEquals(tNested,t2.getCause());

        assertEquals(nestedMessage,t2.getMessage());
        assertEquals(nestedMessage,t2.getLocalizedMessage());
        assertEquals(TEST_MSG_1,t2.getDetail());
        assertEquals(TEST_MSG_1,t2.getLocalizedDetail());
        assertEquals(t2.getClass().getName()+": "+nestedMessage,
                     t2.toString());

        Messages.PROVIDER.setLocale(Locale.FRENCH);
        assertEquals(nestedMessage,t2.getMessage());
        assertEquals(nestedMessage,t2.getLocalizedMessage());
        assertEquals(TEST_MSG_1,t2.getDetail());
        assertEquals(TEST_MSG_1,t2.getLocalizedDetail());
        assertEquals(t2.getClass().getName()+": "+nestedMessage,
                     t2.toString());
    }

    protected static void causeWithI18NMessage
        (I18NThrowable tNested,
         Throwable t1,
         I18NThrowable t2)
    {
        String nestedMessage=tNested.getClass().getName()+": "+MID_MSG_EN;

        assertEquals(nestedMessage,t1.getMessage());
        assertEquals(nestedMessage,t1.getLocalizedMessage());
        assertEquals(t1.getClass().getName()+": "+nestedMessage,
                     t1.toString());

        assertNull(t2.getI18NProvider());
        assertNull(t2.getI18NMessage());
        assertNull(t2.getParams());
        assertEquals(tNested,t2.getCause());

        assertEquals(nestedMessage,t2.getMessage());
        assertEquals(nestedMessage,t2.getLocalizedMessage());
        assertEquals(MID_MSG,t2.getDetail());
        assertEquals(MID_MSG_EN,t2.getLocalizedDetail());
        assertEquals(t2.getClass().getName()+": "+nestedMessage,
                     t2.toString());

        Messages.PROVIDER.setLocale(Locale.FRENCH);
        assertEquals(nestedMessage,t2.getMessage());
        assertEquals(nestedMessage,t2.getLocalizedMessage());
        assertEquals(MID_MSG,t2.getDetail());
        assertEquals(MID_MSG_FR,t2.getLocalizedDetail());
        assertEquals(t2.getClass().getName()+": "+nestedMessage,
                     t2.toString());
    }

    protected static void myMessage
        (Throwable t1,
         I18NThrowable t2)
    {
        assertEquals(TEST_MSG_1,t1.getMessage());
        assertEquals(TEST_MSG_1,t1.getLocalizedMessage());
        assertEquals(t1.getClass().getName()+": "+TEST_MSG_1,
                     t1.toString());

        assertEquals(TestMessages.PROVIDER,t2.getI18NProvider());
        assertEquals(TestMessages.MID_EXCEPTION,t2.getI18NMessage());
        assertArrayEquals(new Object[] {MID_MSG_PARAM},t2.getParams());
        assertNull(t2.getCause());

        assertEquals(MID_MSG,t2.getMessage());
        assertEquals(MID_MSG_EN,t2.getLocalizedMessage());
        assertEquals(MID_MSG,t2.getDetail());
        assertEquals(MID_MSG_EN,t2.getLocalizedDetail());
        assertEquals(t2.getClass().getName()+": "+MID_MSG_EN,
                     t2.toString());

        Messages.PROVIDER.setLocale(Locale.FRENCH);
        assertEquals(MID_MSG,t2.getMessage());
        assertEquals(MID_MSG_FR,t2.getLocalizedMessage());
        assertEquals(MID_MSG,t2.getDetail());
        assertEquals(MID_MSG_FR,t2.getLocalizedDetail());
        assertEquals(t2.getClass().getName()+": "+MID_MSG_FR,
                     t2.toString());
    }

    protected static void myMessageAndCauseWithoutMessage
        (Throwable tNested,
         Throwable t1,
         I18NThrowable t2)
    {
        assertEquals(TEST_MSG_1,t1.getMessage());
        assertEquals(TEST_MSG_1,t1.getLocalizedMessage());
        assertEquals(t1.getClass().getName()+": "+TEST_MSG_1,
                     t1.toString());

        assertEquals(TestMessages.PROVIDER,t2.getI18NProvider());
        assertEquals(TestMessages.MID_EXCEPTION,t2.getI18NMessage());
        assertArrayEquals(new Object[] {MID_MSG_PARAM},t2.getParams());
        assertEquals(tNested,t2.getCause());

        assertEquals(MID_MSG,t2.getMessage());
        assertEquals(MID_MSG_EN,t2.getLocalizedMessage());
        assertEquals(MID_MSG,t2.getDetail());
        assertEquals(MID_MSG_EN,t2.getLocalizedDetail());
        assertEquals(t2.getClass().getName()+": "+MID_MSG_EN,
                     t2.toString());

        Messages.PROVIDER.setLocale(Locale.FRENCH);
        assertEquals(MID_MSG,t2.getMessage());
        assertEquals(MID_MSG_FR,t2.getLocalizedMessage());
        assertEquals(MID_MSG,t2.getDetail());
        assertEquals(MID_MSG_FR,t2.getLocalizedDetail());
        assertEquals(t2.getClass().getName()+": "+MID_MSG_FR,
                     t2.toString());
    }

    protected static void myMessageAndCauseWithMessage
        (Throwable tNested,
         Throwable t1,
         I18NThrowable t2)
    {
        String suffix=" ("+TEST_MSG_2+")";

        assertEquals(TEST_MSG_1,t1.getMessage());
        assertEquals(TEST_MSG_1,t1.getLocalizedMessage());
        assertEquals(t1.getClass().getName()+": "+TEST_MSG_1,
                     t1.toString());

        assertEquals(TestMessages.PROVIDER,t2.getI18NProvider());
        assertEquals(TestMessages.MID_EXCEPTION,t2.getI18NMessage());
        assertArrayEquals(new Object[] {MID_MSG_PARAM},t2.getParams());
        assertEquals(tNested,t2.getCause());

        assertEquals(MID_MSG,t2.getMessage());
        assertEquals(MID_MSG_EN,t2.getLocalizedMessage());
        assertEquals(MID_MSG+suffix,t2.getDetail());
        assertEquals(MID_MSG_EN+suffix,t2.getLocalizedDetail());
        assertEquals(t2.getClass().getName()+": "+MID_MSG_EN,
                     t2.toString());

        Messages.PROVIDER.setLocale(Locale.FRENCH);
        assertEquals(MID_MSG,t2.getMessage());
        assertEquals(MID_MSG_FR,t2.getLocalizedMessage());
        assertEquals(MID_MSG+suffix,t2.getDetail());
        assertEquals(MID_MSG_FR+suffix,t2.getLocalizedDetail());
        assertEquals(t2.getClass().getName()+": "+MID_MSG_FR,
                     t2.toString());
    }

    protected static void myMessageAndCauseWithI18NMessage
        (I18NThrowable tNested,
         Throwable t1,
         I18NThrowable t2)
    {
        assertEquals(TEST_MSG_1,t1.getMessage());
        assertEquals(TEST_MSG_1,t1.getLocalizedMessage());
        assertEquals(t1.getClass().getName()+": "+TEST_MSG_1,
                     t1.toString());

        assertEquals(TestMessages.PROVIDER,t2.getI18NProvider());
        assertEquals(TestMessages.MID_EXCEPTION,t2.getI18NMessage());
        assertArrayEquals(new Object[] {MID_MSG_PARAM},t2.getParams());
        assertEquals(tNested,t2.getCause());

        assertEquals(MID_MSG,t2.getMessage());
        assertEquals(MID_MSG_EN,t2.getLocalizedMessage());
        assertEquals(MID_MSG+" ("+BOT_MSG+")",t2.getDetail());
        assertEquals(MID_MSG_EN+" ("+BOT_MSG_EN+")",t2.getLocalizedDetail());
        assertEquals(t2.getClass().getName()+": "+MID_MSG_EN,
                     t2.toString());

        Messages.PROVIDER.setLocale(Locale.FRENCH);
        assertEquals(MID_MSG,t2.getMessage());
        assertEquals(MID_MSG_FR,t2.getLocalizedMessage());
        assertEquals(MID_MSG+" ("+BOT_MSG+")",t2.getDetail());
        assertEquals(MID_MSG_FR+" ("+BOT_MSG_FR+")",t2.getLocalizedDetail());
        assertEquals(t2.getClass().getName()+": "+MID_MSG_FR,
                     t2.toString());
    }

    protected static void nesting
        (I18NThrowable tBot,
         I18NThrowable tMid,
         I18NThrowable tTop)
    {
        assertEquals(BOT_MSG,tBot.getDetail());
        assertEquals(BOT_MSG_EN,tBot.getLocalizedDetail());
        assertEquals
            (MID_MSG+" ("+BOT_MSG+")",tMid.getDetail());
        assertEquals
            (MID_MSG_EN+" ("+BOT_MSG_EN+")",tMid.getLocalizedDetail());
        assertEquals
            (TOP_MSG+" ("+MID_MSG+" ("+BOT_MSG+"))",
             tTop.getDetail());
        assertEquals
            (TOP_MSG_ALL+" ("+MID_MSG_EN+" ("+BOT_MSG_EN+"))",
             tTop.getLocalizedDetail());

        Messages.PROVIDER.setLocale(Locale.FRENCH);
        assertEquals(BOT_MSG,tBot.getDetail());
        assertEquals(BOT_MSG_FR,tBot.getLocalizedDetail());
        assertEquals
            (MID_MSG+" ("+BOT_MSG+")",tMid.getDetail());
        assertEquals
            (MID_MSG_FR+" ("+BOT_MSG_FR+")",tMid.getLocalizedDetail());
        assertEquals
            (TOP_MSG+" ("+MID_MSG+" ("+BOT_MSG+"))",
             tTop.getDetail());
        assertEquals
            (TOP_MSG_ALL+" ("+MID_MSG_FR+" ("+BOT_MSG_FR+"))",
             tTop.getLocalizedDetail());
    }
}
