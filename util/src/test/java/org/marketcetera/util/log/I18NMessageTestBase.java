package org.marketcetera.util.log;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.marketcetera.util.test.EqualityAssert.assertEquality;
import static org.marketcetera.util.test.SerializableAssert.assertSerializable;

import java.io.Serializable;
import java.util.Locale;

import org.junit.Before;
import org.junit.Ignore;
import org.marketcetera.util.test.TestCaseBase;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@Ignore
public class I18NMessageTestBase
    extends TestCaseBase
{
    protected static final String TEST_P1=
        "1";
    protected static final String TEST_P2=
        "2";
    protected static final String TEST_P3=
        "3";
    protected static final String TEST_P4=
        "4";
    protected static final String TEST_P5=
        "5";
    protected static final String TEST_P6=
        "6";
    protected static final String TEST_P7=
        "7";
    protected static final String TEST_CATEGORY=
        "TestCategory";

    protected static final I18NLoggerProxy TEST_LOGGER_D=
        new I18NLoggerProxy(new I18NMessageProvider("test"));
    protected static final String TEST_MSG_ID=
        "base";
    protected static final String TEST_MSG_ID_D=
        "baseD";
    protected static final String TEST_ENTRY_ID=
        "ttl";
    protected static final String TEST_ENTRY_ID_D=
        "ttlD";
    protected static final Exception TEST_THROWABLE=
        new IllegalArgumentException("Test exception (expected)");


    @Before
    public void setupI18NMessageXPTestBase()
    {
        ActiveLocale.setProcessLocale(Locale.ROOT);
    }


    protected static void unboundTests
        (int paramCount,
         I18NMessage withEntry,
         I18NMessage withEntryCopy,
         Object[] withEntryDiffs,
         I18NMessage withoutEntry)
    {
        assertEquality(withEntry,withEntryCopy,withEntryDiffs);
        assertSerializable(withEntry);

        assertEquals(paramCount,withEntry.getParamCount());
        assertEquals(paramCount,withoutEntry.getParamCount());

        assertEquals(TestMessages.LOGGER,withoutEntry.getLoggerProxy());
        assertEquals(TestMessages.PROVIDER,withoutEntry.getMessageProvider());
        assertEquals(TEST_MSG_ID,withoutEntry.getMessageId());
        assertEquals(I18NMessage.UNKNOWN_ENTRY_ID,withoutEntry.getEntryId());

        assertEquals(TestMessages.LOGGER,withEntry.getLoggerProxy());
        assertEquals(TestMessages.PROVIDER,withEntry.getMessageProvider());
        assertEquals(TEST_MSG_ID,withEntry.getMessageId());
        assertEquals(TEST_ENTRY_ID,withEntry.getEntryId());
    }

    protected void boundTests
        (I18NBoundMessage m,
         I18NBoundMessage copy,
         Object[] diffs,
         Serializable[] params,
         I18NMessage unbound,
         String textEn,
         String testFr)
    {
        assertEquality(m,copy,diffs);
        assertSerializable(m);

        assertEquals(TestMessages.LOGGER,m.getLoggerProxy());
        assertEquals(TestMessages.PROVIDER,m.getMessageProvider());

        assertEquals(unbound,m.getMessage());
        assertArrayEquals(params,m.getParams());
        assertArrayEquals(params,m.getParamsAsObjects());

        assertEquals(textEn,m.getText());
        assertEquals(testFr,m.getText(Locale.FRENCH));

        assertEquals(textEn,m.toString());

        m.error(TEST_CATEGORY,TEST_THROWABLE);
        m.error(TEST_CATEGORY);

        m.warn(TEST_CATEGORY,TEST_THROWABLE);
        m.warn(TEST_CATEGORY);

        m.info(TEST_CATEGORY,TEST_THROWABLE);
        m.info(TEST_CATEGORY);

        m.debug(TEST_CATEGORY,TEST_THROWABLE);
        m.debug(TEST_CATEGORY);

        m.trace(TEST_CATEGORY,TEST_THROWABLE);
        m.trace(TEST_CATEGORY);
    }
}
