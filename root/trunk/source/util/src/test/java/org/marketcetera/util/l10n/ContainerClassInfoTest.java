package org.marketcetera.util.l10n;

import java.util.Iterator;
import java.util.Locale;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.CollectionAssert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class ContainerClassInfoTest
    extends TestCaseBase
{
    private static interface EmptyNoProvider {}

    private static interface EmptyWithProvider
    {
        static final I18NMessageProvider PROVIDER=
            TestMessages.PROVIDER;
    }


    private static class NonstandardFields
    {
        static final I18NMessageProvider PROVIDER=
            TestMessages.PROVIDER;
        static I18NLoggerProxy LOGGER=
            new I18NLoggerProxy(PROVIDER);

        static final int INTEGER_FIELD=
            1;
        protected static I18NMessage0P M0_MSG=
            new I18NMessage0P(LOGGER,"m0");
        I18NMessage1P M1_MSG=
            new I18NMessage1P(LOGGER,"m1");
        static I18NMessage1P M2_MSG;
    }


    @Test
    public void all()
        throws Exception
    {
        ContainerClassInfo info=new ContainerClassInfo(TestMessages.class);
        assertEquals(TestMessages.class,info.getContainer());
        assertEquals(TestMessages.PROVIDER,info.getProvider());
        assertArrayPermutation
            (new I18NMessageInfo[] {
                new I18NMessageInfo("m0.msg",0,TestMessages.M0_MSG),
                new I18NMessageInfo("m1.msg",1,TestMessages.M1_MSG),
                new I18NMessageInfo("m2.msg",2,TestMessages.M2_MSG),
                new I18NMessageInfo("m3.msg",3,TestMessages.M3_MSG),
                new I18NMessageInfo("m4.msg",4,TestMessages.M4_MSG),
                new I18NMessageInfo("m5.msg",5,TestMessages.M5_MSG),
                new I18NMessageInfo("m6.msg",6,TestMessages.M6_MSG),
                new I18NMessageInfo("m7.msg",-1,TestMessages.M7_MSG),
                new I18NMessageInfo("m8.msg",-1,TestMessages.M8_MSG),
            },info.getMessageInfo());
    }

    @Test
    public void emptyWithProvider()
        throws Exception
    {
        ContainerClassInfo info=new ContainerClassInfo(EmptyWithProvider.class);
        assertEquals(EmptyWithProvider.class,info.getContainer());
        assertEquals(EmptyWithProvider.PROVIDER,info.getProvider());
        assertArrayEquals(I18NMessageInfo.EMPTY_ARRAY,info.getMessageInfo());
    }

    @Test
    public void nonstandardFields()
        throws Exception
    {
        ActiveLocale.setProcessLocale(Locale.ROOT);
        ContainerClassInfo info=new ContainerClassInfo(NonstandardFields.class);
        String category=info.getClass().getName();
        setLevel(category,Level.INFO);
        assertEquals(NonstandardFields.class,info.getContainer());
        assertEquals(NonstandardFields.PROVIDER,info.getProvider());
        assertArrayPermutation
            (new I18NMessageInfo[] {
                new I18NMessageInfo("m0.msg",0,NonstandardFields.M0_MSG)
            },info.getMessageInfo());
        Iterator<LoggingEvent> events=getAppender().getEvents().iterator();
        assertEvent
            (events.next(),Level.INFO,category,
             "Non-static field 'M1_MSG' is ignored",category);
        assertEvent
            (events.next(),Level.INFO,category,
             "Null field 'M2_MSG' is ignored",category);
        assertFalse(events.hasNext());
        getAppender().clear();
    }

    @Test
    public void missingProvider()
    {
        try {
            new ContainerClassInfo(EmptyNoProvider.class);
            fail();
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),
                 new I18NBoundMessage1P(Messages.MISSING_PROVIDER,
                                        EmptyNoProvider.class.getName()),
                 ex.getI18NBoundMessage());
        }
    }
}
