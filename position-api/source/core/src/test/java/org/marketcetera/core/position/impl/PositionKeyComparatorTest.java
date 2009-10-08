package org.marketcetera.core.position.impl;

import static org.marketcetera.core.position.PositionKeyFactory.createEquityKey;
import static org.marketcetera.core.position.PositionKeyFactory.createOptionKey;

import java.math.BigDecimal;

import org.junit.Test;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.trade.OptionType;

import com.google.common.collect.ImmutableList;

/* $License$ */

/**
 * Tests {@link PositionKeyComparator}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class PositionKeyComparatorTest {

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

        createEquityKey("ABC", null, "Me"),

        createEquityKey("ABC", "Account", "Me"),

        createOptionKey("ABC", "20090101", BigDecimal.ONE, OptionType.Put,
                null, "Me"),

        createOptionKey("ABC", "20090101", BigDecimal.ONE, OptionType.Put,
                "Account", "Me")),

        new PositionKeyComparator());
    }
}
