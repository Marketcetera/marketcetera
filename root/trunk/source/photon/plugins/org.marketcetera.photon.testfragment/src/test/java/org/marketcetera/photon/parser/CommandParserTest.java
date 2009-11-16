package org.marketcetera.photon.parser;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;

import org.codehaus.jparsec.error.ParserException;
import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.photon.IBrokerIdValidator;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.Side;

/* $License$ */

/**
 * Tests {@link CommandParser}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class CommandParserTest extends PhotonTestBase {

    @Test
    public void testCancel() {
        Object result = new CommandParser(null).parseCommand("c 1234 asdf");
        assertThat(result, is((Object) Arrays.asList("1234", "asdf")));
        result = new CommandParser(null).parseCommand("C\t2 \t 3 4 5");
        assertThat(result, is((Object) Arrays.asList("2", "3", "4", "5")));
    }

    @Test
    public void testOrder() {
        OrderSingle result = (OrderSingle) new CommandParser(null)
                .parseCommand("o b 10 MSFT 10");
        assertThat(result.getSide(), is(Side.Buy));
        assertThat(result.getQuantity(), is(new BigDecimal("10")));
        assertThat(result.getInstrument(), is((Instrument) new Equity("MSFT")));
        assertThat(result.getPrice(), is(new BigDecimal("10")));
        result = (OrderSingle) new CommandParser(null)
                .parseCommand("O S 5 MSFT200912P10 12.2 b:gs");
        assertThat(result.getSide(), is(Side.Sell));
        assertThat(result.getQuantity(), is(new BigDecimal("5")));
        assertThat(result.getInstrument(), is((Instrument) new Option("MSFT",
                "200912", BigDecimal.TEN, OptionType.Put)));
        assertThat(result.getPrice(), is(new BigDecimal("12.2")));
    }

    @Test
    public void testErrors() throws Exception {
        new ExpectedFailure<ParserException>(
                "[cC] or [oO] expected, x encountered.", false) {
            @Override
            protected void run() throws Exception {
                new CommandParser(null).parseCommand("x asdf");
            }
        };

        final IBrokerIdValidator mockValidator = mock(IBrokerIdValidator.class);
        when(mockValidator.isValid("ABC")).thenReturn(false);
        new ExpectedFailure<ParserException>(OrderSingleParserTest
                .getInvalidBrokerMessage("ABC"), false) {
            @Override
            protected void run() throws Exception {
                new CommandParser(mockValidator)
                        .parseCommand("o b 10 MSFT 10 b:ABC");
            }
        };
    }

}
