package org.marketcetera.core.position.impl;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.marketcetera.api.systemmodel.instruments.OptionType;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.PositionKeyFactory;
import org.marketcetera.core.position.PositionKeyTestBase;
import org.marketcetera.core.trade.EquityImpl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.marketcetera.core.position.PositionKeyFactory.*;

/* $License$ */

/**
 * Tests {@link PositionKeyImpl}.
 * 
 * @version $Id: PositionKeyImplTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
public class PositionKeyImplTest extends PositionKeyTestBase {

    @Override
    protected PositionKey<?> createFixture() {
        return PositionKeyFactory.createEquityKey("ABC", "MyAccount", "Me");
    }

    @Override
    protected PositionKey<?> createEqualFixture() {
        return PositionKeyFactory.createEquityKey("ABC", "MyAccount", "Me");
    }

    @Override
    protected List<PositionKey<?>> createDifferentFixtures() {
        return ImmutableList.<PositionKey<?>> of(

        createEquityKey("ABC", null, null),

        createEquityKey("ABC", "Account", null),

        createEquityKey("IBM", null, null),

        createOptionKey("ABC", "20090101", BigDecimal.ONE, OptionType.Call,
                null, null),

        createOptionKey("ABC", "20090101", BigDecimal.ONE, OptionType.Call,
                "Account", null),

        createOptionKey("ABC", "20090101", BigDecimal.ONE, OptionType.Put,
                null, null),
        createConvertibleBondKey("ABC", null, null),
        createConvertibleBondKey("ABC", "Account", null),
        createConvertibleBondKey("IBM", null, null),
        createEquityKey("ABC", null, "Me"),

        createEquityKey("ABC", "Account", "Me"),

        createOptionKey("ABC", "20090101", BigDecimal.ONE, OptionType.Call,
                "Account", "Me"),
        createConvertibleBondKey("ABC", null, "Me"),
        createConvertibleBondKey("ABC", "Account", "Me"));
    }

    @Test
    public void testNullInstrument() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new PositionKeyImpl<EquityImpl>(null, "abc", "abc");
            }
        };
    }

    @Test
    public void testWhitespaceAccountIsNull() throws Exception {
        assertNull(new PositionKeyImpl<EquityImpl>(new EquityImpl("abc"), "", "abc")
                .getAccount());
        assertNull(new PositionKeyImpl<EquityImpl>(new EquityImpl("abc"), "     ",
                "abc").getAccount());
    }

    @Test
    public void testWhitespaceTraderIdIsNull() throws Exception {
        assertNull(new PositionKeyImpl<EquityImpl>(new EquityImpl("abc"), "abc", "")
                .getTraderId());
        assertNull(new PositionKeyImpl<EquityImpl>(new EquityImpl("abc"), "abc",
                "  \n   ").getTraderId());
    }

    @Test
    public void testToString() throws Exception {
        assertThat(createFixture().toString(),
                is("PositionKeyImpl[symbol=ABC,account=MyAccount,traderId=Me]"));
    }
}
