package org.marketcetera.photon.parser;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.text.MessageFormat;

import org.codehaus.jparsec.error.ParserException;
import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.photon.IBrokerIdValidator;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;

/* $License$ */

/**
 * Tests {@link OrderSingleParser}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class OrderSingleParserTest extends PhotonTestBase {

    @Test
    public void testSide() throws Exception {
        testSideHelper("s", Side.Sell);
        testSideHelper("S", Side.Sell);
        testSideHelper("b", Side.Buy);
        testSideHelper("B", Side.Buy);
        testSideHelper("ss", Side.SellShort);
        testSideHelper("SS", Side.SellShort);
        testSideHelper("sS", Side.SellShort);
        testSideHelper("Ss", Side.SellShort);
        new ExpectedFailure<ParserException>(
                "Unexpected side: x\nValid values are b, s, and ss.", false) {
            @Override
            protected void run() throws Exception {
                testSideHelper("x", null);
            }
        };
    }

    private void testSideHelper(String string, Side side) {
        assertOrder(string + " 10 METC 10", side, "10", new Equity("METC"),
                OrderType.Limit, "10", null, null, null);
    }

    @Test
    public void testQuantity() throws Exception {
        testQuantityHelper("1", "1");
        testQuantityHelper("1.", "1");
        testQuantityHelper("-12.234", "-12.234");
        testQuantityHelper("10.500", "10.500");
        testQuantityHelper("0", "0");
        testQuantityHelper(".5", ".5");
        new ExpectedFailure<ParserException>("Not a decimal: xyz", false) {
            @Override
            protected void run() throws Exception {
                testQuantityHelper("xyz", null);
            }
        };
    }

    private void testQuantityHelper(String string, String quantity) {
        assertOrder("B " + string + " METC 10", Side.Buy, quantity, new Equity(
                "METC"), OrderType.Limit, "10", null, null, null);
    }

    @Test
    public void testInstrument() throws Exception {
        testInstrumentHelper("MSFT", new Equity("MSFT"));
        testInstrumentHelper("MSQ20091010C5", new Option("MSQ", "20091010",
                new BigDecimal("5"), OptionType.Call));
        testInstrumentHelper("PXQ200910P24.5", new Option("PXQ", "200910",
                new BigDecimal("24.5"), OptionType.Put));
        testInstrumentHelper("PXQ200910p.5", new Option("PXQ", "200910",
                new BigDecimal(".5"), OptionType.Put));
        testInstrumentHelper("PXQ200910c.44444", new Option("PXQ", "200910",
                new BigDecimal(".44444"), OptionType.Call));
        testInstrumentHelper("PXQ200910c1.000", new Option("PXQ", "200910",
                new BigDecimal("1"), OptionType.Call));
        /*
         * No date validation.
         */
        testInstrumentHelper("PXQ2009c1", new Option("PXQ", "2009",
                new BigDecimal("1"), OptionType.Call));
        testInstrumentHelper("PXQ200910101001c1", new Option("PXQ",
                "200910101001", new BigDecimal("1"), OptionType.Call));
        /*
         * If an option can't be parsed, an equity is assumed.
         */
        testInstrumentHelper("PXQ200910Z5.5", new Equity("PXQ200910Z5.5"));
        testInstrumentHelper("PX2Q200910c1", new Equity("PX2Q200910c1"));

    }

    private void testInstrumentHelper(String string, Instrument instrument) {
        assertOrder("B 10 " + string + " 10", Side.Buy, "10", instrument,
                OrderType.Limit, "10", null, null, null);
    }

    @Test
    public void testPrice() throws Exception {
        testPriceHelper("1", "1");
        testPriceHelper("1.", "1");
        testPriceHelper("-12.234", "-12.234");
        testPriceHelper("10.500", "10.500");
        testPriceHelper(".5", ".5");
        testPriceHelper("00.5", ".5");
        testPriceHelper("-.5", "-.5");
        testPriceHelper("0", "0");
        testPriceHelper("MKT", null);
        testPriceHelper("mkt", null);
        new ExpectedFailure<ParserException>(
                "Invalid price: xyz\nPrice must be a decimal or MKT", false) {
            @Override
            protected void run() throws Exception {
                testPriceHelper("xyz", null);
            }
        };
    }

    private void testPriceHelper(String string, String quantity) {
        OrderType type = quantity == null ? OrderType.Market : OrderType.Limit;
        assertOrder("B 10 IBM " + string, Side.Buy, "10", new Equity("IBM"),
                type, quantity, null, null, null);
    }

    @Test
    public void testAccount() throws Exception {
        testOptionalHelper("acc:abc", "abc", null, null);
        testOptionalHelper("acc:\"My account\"", "My account", null, null);
        testOptionalHelper("acc:102567", "102567", null, null);
        testOptionalHelper("ACC:1", "1", null, null);
        testOptionalHelper("aCC:1", "1", null, null);
        testOptionalHelper("acc:\"My\\\"account\"", "My\"account", null, null);
        testOptionalHelper("acc:!@#$%^&*()-=", "!@#$%^&*()-=", null, null);
        new ExpectedFailure<ParserException>(
                "No value specified for optional field acc", false) {
            @Override
            protected void run() throws Exception {
                testOptionalHelper("acc:", null, null, null);
            }
        };
    }

    @Test
    public void testBroker() throws Exception {
        testOptionalHelper("b:abc", null, "abc", null);
        testOptionalHelper("B:gs", null, "gs", null);
        testOptionalHelper("B:123", null, "123", null);
        new ExpectedFailure<ParserException>(
                "No value specified for optional field b", false) {
            @Override
            protected void run() throws Exception {
                testOptionalHelper("b:", null, null, null);
            }
        };
    }

    @Test
    public void testBrokerWithValidator() throws Exception {
        mBrokerIdValidator = mock(IBrokerIdValidator.class);
        when(mBrokerIdValidator.isValid("ABC")).thenReturn(false);
        when(mBrokerIdValidator.isValid("gs")).thenReturn(true);

        new ExpectedFailure<ParserException>(getInvalidBrokerMessage("ABC"),
                false) {
            @Override
            protected void run() throws Exception {
                testOptionalHelper("b:ABC", null, null, null);
            }
        };
        new ExpectedFailure<ParserException>(getInvalidBrokerMessage("GS"),
                false) {
            @Override
            protected void run() throws Exception {
                testOptionalHelper("b:GS", null, null, null);
            }
        };
        testOptionalHelper("B:gs", null, "gs", null);
    }

    @Test
    public void testTimeInForce() throws Exception {
        testOptionalHelper("tif:day", null, null, TimeInForce.Day);
        testOptionalHelper("tif:DAY", null, null, TimeInForce.Day);
        testOptionalHelper("tif:gtc", null, null, TimeInForce.GoodTillCancel);
        testOptionalHelper("TIF:GTC", null, null, TimeInForce.GoodTillCancel);
        testOptionalHelper("tif:fok", null, null, TimeInForce.FillOrKill);
        testOptionalHelper("tif:FOK", null, null, TimeInForce.FillOrKill);
        testOptionalHelper("tif:clo", null, null, TimeInForce.AtTheClose);
        testOptionalHelper("tif:CLO", null, null, TimeInForce.AtTheClose);
        testOptionalHelper("tif:opg", null, null, TimeInForce.AtTheOpening);
        testOptionalHelper("tif:OPG", null, null, TimeInForce.AtTheOpening);
        testOptionalHelper("tif:ioc", null, null, TimeInForce.ImmediateOrCancel);
        testOptionalHelper("tif:IOC", null, null, TimeInForce.ImmediateOrCancel);
        new ExpectedFailure<ParserException>(
                "No value specified for optional field tif", false) {
            @Override
            protected void run() throws Exception {
                testOptionalHelper("tif:", null, null, null);
            }
        };
        new ExpectedFailure<ParserException>(
                "Invalid time in force: xyz\nSupported values are DAY, GTC, FOK, CLO, OPG, and IOC.",
                false) {
            @Override
            protected void run() throws Exception {
                testOptionalHelper("tif:xyz", null, null, null);
            }
        };
    }

    @Test
    public void testOptional() throws Exception {
        testOptionalHelper("acc:abc b:xyz tif:day", "abc", "xyz",
                TimeInForce.Day);
        testOptionalHelper("acc:abc tif:day b:xyz", "abc", "xyz",
                TimeInForce.Day);
        testOptionalHelper("tif:day acc:abc b:xyz", "abc", "xyz",
                TimeInForce.Day);
        testOptionalHelper("tif:day b:xyz acc:abc", "abc", "xyz",
                TimeInForce.Day);
        testOptionalHelper("b:xyz tif:day acc:abc", "abc", "xyz",
                TimeInForce.Day);
        testOptionalHelper("b:xyz acc:abc tif:day", "abc", "xyz",
                TimeInForce.Day);
        testOptionalHelper("acc:abc b:xyz", "abc", "xyz", null);
        testOptionalHelper("acc:abc tif:day", "abc", null, TimeInForce.Day);
        testOptionalHelper("tif:day acc:abc", "abc", null, TimeInForce.Day);
        testOptionalHelper("tif:day b:xyz", null, "xyz", TimeInForce.Day);
        testOptionalHelper("b:xyz tif:day", null, "xyz", TimeInForce.Day);
        testOptionalHelper("b:xyz acc:abc", "abc", "xyz", null);
        /*
         * If a field is specified twice, the later value overwrites.
         */
        testOptionalHelper("acc:1 acc:2", "2", null, null);
        new ExpectedFailure<ParserException>(
                getInvalidOptionalFieldMessage("acc:asdf:asdf"), false) {
            @Override
            protected void run() throws Exception {
                testOptionalHelper("acc:asdf:asdf", null, null, null);
            }
        };
        new ExpectedFailure<ParserException>(
                getInvalidOptionalFieldMessage("price:3"), false) {
            @Override
            protected void run() throws Exception {
                testOptionalHelper("price:3", null, null, null);
            }
        };
    }

    @Test
    public void miscellaneous() throws Exception {
        /*
         * Whitespace.
         */
        assertOrder("  \t  b     10\tMETC 10   ", Side.Buy, "10", new Equity(
                "METC"), OrderType.Limit, "10", null, null, null);
        /*
         * No space.
         */
        new ExpectedFailure<ParserException>() {
            @Override
            protected void run() throws Exception {
                parse("B10IBM10");
            }
        };
    }

    private void testOptionalHelper(String string, String account,
            String broker, TimeInForce tif) {
        assertOrder("B 10 IBM 10 " + string, Side.Buy, "10", new Equity("IBM"),
                OrderType.Limit, "10", account, broker, tif);
    }

    private IBrokerIdValidator mBrokerIdValidator = null;

    private void assertOrder(String command, Side side, String quantity,
            Instrument instrument, OrderType orderType, String price,
            String account, String broker, TimeInForce tif) {
        OrderSingle o = parse(command);
        assertEquals(side, o.getSide());
        assertEquals(new BigDecimal(quantity), o.getQuantity());
        assertEquals(instrument, o.getInstrument());
        assertEquals(orderType, o.getOrderType());
        assertEquals(price == null ? null : new BigDecimal(price), o.getPrice());
        assertEquals(account, o.getAccount());
        assertEquals(broker == null ? null : new BrokerID(broker), o
                .getBrokerID());
        assertEquals(tif, o.getTimeInForce());
    }

    private OrderSingle parse(String command) {
        return new OrderSingleParser(mBrokerIdValidator).getParser().parse(
                command);
    }

    public static String getInvalidBrokerMessage(String broker) {
        return MessageFormat
                .format(
                        "The specified broker id ''{0}'' is invalid.  The broker id must correspond to the id of a broker configured on the server.  If the desired broker is unknown, do not specify a broker and the server will select one.",
                        broker);
    }

    public static String getInvalidOptionalFieldMessage(String value) {
        return MessageFormat
                .format(
                        "Invalid optional field {0}\nSupported fields are ''acc'' (Account), ''tif'' (Time in Force), and ''b'' (broker id), e.g. ''acc:123''.\nThe '':'' character is not allowed in field values.",
                        value);
    }
}
