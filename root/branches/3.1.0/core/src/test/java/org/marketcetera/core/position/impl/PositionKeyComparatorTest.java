package org.marketcetera.core.position.impl;

import static org.marketcetera.core.position.PositionKeyFactory.createConvertibleBondKey;
import static org.marketcetera.core.position.PositionKeyFactory.createEquityKey;
import static org.marketcetera.core.position.PositionKeyFactory.createOptionKey;

import java.math.BigDecimal;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.trade.OptionType;

import com.google.common.collect.ImmutableList;

/* $License$ */

/**
 * Tests {@link PositionKeyComparator}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class PositionKeyComparatorTest
{
    /**
     * Run once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
    }
    @Test
    public void testOrdering() throws Exception {
        OrderingTestHelper.testOrdering(ImmutableList.<PositionKey<?>> of(

        createEquityKey("ABC", null, null),

        createEquityKey("ABC", "Account", null),

        createEquityKey("IBM", null, null),

        createOptionKey("ABC", "20090101", BigDecimal.ONE, OptionType.Put,
                null, null),

        createOptionKey("ABC", "20090101", BigDecimal.ONE, OptionType.Put,
                "Account", null),

        createOptionKey("ABC", "20090101", BigDecimal.ONE, OptionType.Call,
                null, null),

        createOptionKey("ABC", "20090101", BigDecimal.TEN, OptionType.Put,
                null, null),

        createOptionKey("ABC", "20090102", BigDecimal.ONE, OptionType.Put,
                null, null),

        createOptionKey("METC", "20090101", BigDecimal.ONE, OptionType.Put,
                null, null),
        createConvertibleBondKey("ABC",
                                 null,
                                 null),
        createConvertibleBondKey("ABC",
                                 "Account",
                                 null),
        createConvertibleBondKey("IBM",
                                 null,
                                 null),
        createEquityKey("ABC", null, "Me"),
        createEquityKey("ABC", "Account", "Me"),
        createOptionKey("ABC", "20090101", BigDecimal.ONE, OptionType.Put,
                null, "Me"),

        createOptionKey("ABC", "20090101", BigDecimal.ONE, OptionType.Put,
                "Account", "Me"),
        createConvertibleBondKey("ABC",
                                 null,
                                 "Me"),
        createConvertibleBondKey("ABC",
                                 "Account",
                                 "Me")),

        new PositionKeyComparator());
    }
}
