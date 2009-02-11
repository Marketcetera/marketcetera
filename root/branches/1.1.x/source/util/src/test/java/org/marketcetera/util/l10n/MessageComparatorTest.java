package org.marketcetera.util.l10n;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.Test;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessageNP;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.CollectionAssert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class MessageComparatorTest
    extends TestCaseBase
{
    private static interface CorrectMessages
        extends TestMessages
    {
        static final I18NMessage0P B1_TTL=
            new I18NMessage0P(LOGGER,"b1","ttl");
        static final I18NMessage1P B2_TTL=
            new I18NMessage1P(LOGGER,"b2","ttl");
    }

    private static interface MismatchMessages
        extends TestMessages
    {
        static final I18NMessage1P B1_TTL=
            new I18NMessage1P(LOGGER,"b1","ttl");
        static final I18NMessageNP B3_TTL=
            new I18NMessageNP(LOGGER,"b3","ttl");
    }

    private static void assertMatches
        (MessageComparator comparator)
    {
        assertEquals(StringUtils.EMPTY,comparator.getDifferences());
        assertTrue(comparator.isMatch());
    }


    @Test
    public void match()
        throws Exception
    {
        assertMatches(new MessageComparator(CorrectMessages.class));

        MessageInfoProvider provider=
            new ContainerClassInfo(CorrectMessages.class);
        assertMatches(new MessageComparator(provider,provider));

        provider=new PropertiesFileInfo(TestMessages.PROVIDER);
        assertMatches(new MessageComparator(provider,provider));
    }

    @Test
    public void mismatch()
        throws Exception
    {
        MessageComparator comparator=
            new MessageComparator(MismatchMessages.class);
        assertFalse(comparator.isMatch());

        assertArrayPermutation
            (new MessageInfoPair[] {
                new MessageInfoPair
                (new I18NMessageInfo("b1.ttl",1,MismatchMessages.B1_TTL),
                 new PropertyMessageInfo("b1.ttl",0,"B Text"))
            },comparator.getMismatches());
        assertArrayPermutation
            (new MessageInfo[] {
                new I18NMessageInfo("b3.ttl",-1,MismatchMessages.B3_TTL)
            },comparator.getExtraSrcInfo());
        assertArrayPermutation
            (new MessageInfo[] {
                new PropertyMessageInfo("b2.ttl",1,"B Text {0,date,full}")
            },comparator.getExtraDstInfo());

        assertEquals
            ("Parameter count mismatch: message key 'b1.ttl'; "+
             "source count is 1; destination count is 0"+
             SystemUtils.LINE_SEPARATOR+
             "Extra message in source: key 'b3.ttl'"+
             SystemUtils.LINE_SEPARATOR+
             "Extra message in destination: key 'b2.ttl'",
             comparator.getDifferences());
    }
}
