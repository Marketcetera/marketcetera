package org.marketcetera.orderloader.system;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.trade.*;
import org.marketcetera.orderloader.OrderParsingException;
import org.marketcetera.orderloader.Messages;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.core.LoggerConfiguration;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

import java.util.*;
import java.math.BigDecimal;

/* $License$ */
/**
 * Verifies various {@link FieldProcessor} subclasses.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class FieldProcessorTest {
    @BeforeClass
    public static void logSetup() {
        LoggerConfiguration.logSetup();
    }
    @Test
    public void account() throws Exception {
        AccountProcessor proc = new AccountProcessor(1);
        assertEquals("dfd", apply(proc, "acc", "dfd", "").getAccount());
        proc = new AccountProcessor(0);
        assertEquals("acc", apply(proc, "acc", "dfd", "").getAccount());
        assertEquals("", apply(proc, "", "dfd", "").getAccount());
        assertEquals(null, apply(proc, null, "dfd", "").getAccount());
    }
    @Test
    public void custom() throws Exception {
        CustomFieldProcessor custom = new CustomFieldProcessor();
        assertTrue(custom.isEmpty());
        custom.addField(0, "3001");
        custom.addField(3, "3009");
        assertFalse(custom.isEmpty());
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("3001", "one");
        fields.put("3009", "four");
        assertEquals(fields, apply(custom, "one", "two", "three", "four", "five").getCustomFields());
        fields.clear();
        fields.put("3001", "");
        fields.put("3009", null);
        assertEquals(fields, apply(custom, "", "two", "three", null, "five").getCustomFields());
    }
    @Test
    public void orderCapacity() throws Exception {
        final OrderCapacityProcessor proc = new OrderCapacityProcessor(2);
        assertEquals(null, apply(proc, "blue", "green", "", "red").getOrderCapacity());
        assertEquals(OrderCapacity.Principal, apply(proc, "blue", "green",
                OrderCapacity.Principal.toString(), "red").getOrderCapacity());
        //Valid Values
        Set<OrderCapacity> validValues = EnumSet.allOf(OrderCapacity.class);
        validValues.remove(OrderCapacity.Unknown);
        new ExpectedFailure<OrderParsingException>(
                Messages.INVALID_ORDER_CAPACITY, "blues", validValues.toString()){
            protected void run() throws Exception {
                apply(proc, "red", "greed", "blues", "clues");
            }
        };
        new ExpectedFailure<OrderParsingException>(
                Messages.INVALID_ORDER_CAPACITY,
                OrderCapacity.Unknown.toString(), validValues.toString()){
            protected void run() throws Exception {
                apply(proc, "red", "greed", OrderCapacity.Unknown.toString(),
                        "clues");
            }
        };
    }
    @Test
    public void orderType() throws Exception {
        final OrderTypeProcessor proc = new OrderTypeProcessor(2);
        assertEquals(null, apply(proc, "blue", "green", "", "red").getOrderType());
        assertEquals(OrderType.Limit, apply(proc, "blue", "green",
                OrderType.Limit.toString(), "red").getOrderType());
        //Valid Values
        Set<OrderType> validValues = EnumSet.allOf(OrderType.class);
        validValues.remove(OrderType.Unknown);
        new ExpectedFailure<OrderParsingException>(
                Messages.INVALID_ORDER_TYPE, "blues", validValues.toString()){
            protected void run() throws Exception {
                apply(proc, "red", "greed", "blues", "clues");
            }
        };
        new ExpectedFailure<OrderParsingException>(
                Messages.INVALID_ORDER_TYPE,
                OrderType.Unknown.toString(), validValues.toString()){
            protected void run() throws Exception {
                apply(proc, "red", "greed", OrderType.Unknown.toString(),
                        "clues");
            }
        };
    }
    @Test
    public void positionEffect() throws Exception {
        final PositionEffectProcessor proc = new PositionEffectProcessor(2);
        assertEquals(null, apply(proc, "blue", "green", "", "red").getPositionEffect());
        assertEquals(PositionEffect.Close, apply(proc, "blue", "green",
                PositionEffect.Close.toString(), "red").getPositionEffect());
        //Valid Values
        Set<PositionEffect> validValues = EnumSet.allOf(PositionEffect.class);
        validValues.remove(PositionEffect.Unknown);
        new ExpectedFailure<OrderParsingException>(
                Messages.INVALID_POSITION_EFFECT, "blues", validValues.toString()){
            protected void run() throws Exception {
                apply(proc, "red", "greed", "blues", "clues");
            }
        };
        new ExpectedFailure<OrderParsingException>(
                Messages.INVALID_POSITION_EFFECT,
                PositionEffect.Unknown.toString(), validValues.toString()){
            protected void run() throws Exception {
                apply(proc, "red", "greed", PositionEffect.Unknown.toString(),
                        "clues");
            }
        };
    }
    @Test
    public void price() throws Exception {
        final PriceProcessor proc = new PriceProcessor(1);
        assertEquals(null, apply(proc, "go", "", "step").getPrice());
        assertEquals(null, apply(proc, "go", null, "step").getPrice());
        assertEquals(BigDecimal.ONE, apply(proc, "go", "1", "step").getPrice());
        assertEquals(new BigDecimal("10.345"), apply(proc, "go",
                "10.345", "yards").getPrice());
        new ExpectedFailure<OrderParsingException>(
                Messages.INVALID_PRICE_VALUE, "to"){
            protected void run() throws Exception {
                apply(proc, "go", "to", "hell");
            }
        };
        new ExpectedFailure<OrderParsingException>(
                Messages.INVALID_PRICE_VALUE, " "){
            protected void run() throws Exception {
                apply(proc, "go", " ", "west");
            }
        };

    }
    @Test
    public void quantity() throws Exception {
        final QuantityProcessor proc = new QuantityProcessor(1);
        assertEquals(null, apply(proc, "go", "", "step").getQuantity());
        assertEquals(null, apply(proc, "go", null, "step").getQuantity());
        assertEquals(BigDecimal.ONE, apply(proc, "go", "1", "step").getQuantity());
        assertEquals(new BigDecimal("10.345"), apply(proc, "go",
                "10.345", "yards").getQuantity());
        new ExpectedFailure<OrderParsingException>(
                Messages.INVALID_QUANTITY_VALUE, "to"){
            protected void run() throws Exception {
                apply(proc, "go", "to", "hell");
            }
        };
        new ExpectedFailure<OrderParsingException>(
                Messages.INVALID_QUANTITY_VALUE, " "){
            protected void run() throws Exception {
                apply(proc, "go", " ", "west");
            }
        };

    }
    @Test
    public void side() throws Exception {
        final SideProcessor proc = new SideProcessor(2);
        assertEquals(null, apply(proc, "blue", "green", "", "red").getSide());
        assertEquals(Side.Buy, apply(proc, "blue", "green",
                Side.Buy.toString(), "red").getSide());
        //Valid Values
        Set<Side> validValues = EnumSet.allOf(Side.class);
        validValues.remove(Side.Unknown);
        new ExpectedFailure<OrderParsingException>(
                Messages.INVALID_SIDE, "blues", validValues.toString()){
            protected void run() throws Exception {
                apply(proc, "red", "greed", "blues", "clues");
            }
        };
        new ExpectedFailure<OrderParsingException>(
                Messages.INVALID_SIDE,
                Side.Unknown.toString(), validValues.toString()){
            protected void run() throws Exception {
                apply(proc, "red", "greed", Side.Unknown.toString(),
                        "clues");
            }
        };
    }
    @Test
    public void equityNoSecurityType() throws Exception {
        final InstrumentProcessor proc = new InstrumentProcessor();
        assertTrue(proc.canProcess(InstrumentFromRow.FIELD_SYMBOL, 1));
        assertEquals(null, apply(proc, "java", null, "mava").getInstrument());
        assertEquals(null, apply(proc, "java", "", "mava").getInstrument());
        assertEquals(null, apply(proc, "java", "  ", "mava").getInstrument());
        assertEquals(new Equity("kava"), apply(proc, "java", "kava", "mava").getInstrument());
    }
    @Test
    public void equityWithSecurityType() throws Exception {
        final InstrumentProcessor proc = new InstrumentProcessor();
        assertTrue(proc.canProcess(InstrumentFromRow.FIELD_SYMBOL, 0));
        assertTrue(proc.canProcess(InstrumentFromRow.FIELD_SECURITY_TYPE, 1));
        assertEquals(null, apply(proc, null, null, "mava").getInstrument());
        assertEquals(null, apply(proc, "", null, "mava").getInstrument());
        assertEquals(null, apply(proc, null, SecurityType.CommonStock.toString(), "mava").getInstrument());
        assertEquals(null, apply(proc, "", SecurityType.CommonStock.toString(), "mava").getInstrument());
        assertEquals(new Equity("java"), apply(proc, "java", null, "mava").getInstrument());
        assertEquals(new Equity("java"), apply(proc, "java", "", "mava").getInstrument());
        assertEquals(new Equity("java"), apply(proc, " java ", "", "mava").getInstrument());
        assertEquals(new Equity("java"), apply(proc, "java", SecurityType.CommonStock.toString(), "mava").getInstrument());
        Set<SecurityType> validValues = EnumSet.allOf(SecurityType.class);
        validValues.remove(SecurityType.Unknown);
        for (String invalidSecType:
                Arrays.asList("InvalidSecurityType", SecurityType.Unknown.toString())) {
            verifyParseFailure(proc,
                new I18NBoundMessage2P(Messages.INVALID_SECURITY_TYPE,
                            invalidSecType, validValues.toString()),
                    "java", invalidSecType, "whatever");
        }
    }
    @Test
    public void optionInstrument() throws Exception {
        InstrumentProcessor proc = new InstrumentProcessor();
        assertTrue(proc.canProcess(InstrumentFromRow.FIELD_SYMBOL, 0));
        assertTrue(proc.canProcess(InstrumentFromRow.FIELD_SECURITY_TYPE, 1));
        assertTrue(proc.canProcess(OptionFromRow.FIELD_EXPIRY, 2));
        assertTrue(proc.canProcess(OptionFromRow.FIELD_OPTION_TYPE, 3));
        assertTrue(proc.canProcess(OptionFromRow.FIELD_STRIKE_PRICE, 4));
        //verify fields that cannot be processed.
        assertFalse(proc.canProcess(SystemProcessor.FIELD_ACCOUNT, 4));
        //verify equity works
        final String symbol = "java";
        assertEquals(new Equity(symbol),
                apply(proc, symbol, "", "kava", "lava", "mava").getInstrument());
        assertEquals(new Equity(symbol),
                apply(proc, symbol, SecurityType.CommonStock.toString(), "kava", "lava", "mava").getInstrument());
        //verify option works
        final String expiry = "20101010";
        final BigDecimal strikePrice = BigDecimal.TEN;
        final OptionType type = OptionType.Call;
        assertEquals(new Option(symbol, expiry, strikePrice, type),
                apply(proc, symbol, SecurityType.Option.toString(), expiry,
                        type.toString(), strikePrice.toPlainString()).getInstrument());
        //verify option fields are ignored if the security type is set to equity
        assertEquals(new Equity(symbol),
                apply(proc, symbol, SecurityType.CommonStock.toString(), expiry,
                        type.toString(), strikePrice.toPlainString()).getInstrument());

        //verify failures

        //symbol missing
        verifyOptionMissingFieldFailure(proc, OptionFromRow.FIELD_SYMBOL,
                null, SecurityType.Option.toString(), expiry, type.toString(), strikePrice.toPlainString());
        verifyOptionMissingFieldFailure(proc, OptionFromRow.FIELD_SYMBOL,
                "", SecurityType.Option.toString(), expiry, type.toString(), strikePrice.toPlainString());
        verifyOptionMissingFieldFailure(proc, OptionFromRow.FIELD_SYMBOL,
                "  ", SecurityType.Option.toString(), expiry, type.toString(), strikePrice.toPlainString());
        //expiry missing
        verifyOptionMissingFieldFailure(proc, OptionFromRow.FIELD_EXPIRY,
                symbol, SecurityType.Option.toString(), null, type.toString(), strikePrice.toPlainString());
        verifyOptionMissingFieldFailure(proc, OptionFromRow.FIELD_EXPIRY,
                symbol, SecurityType.Option.toString(), "", type.toString(), strikePrice.toPlainString());
        verifyOptionMissingFieldFailure(proc, OptionFromRow.FIELD_EXPIRY,
                symbol, SecurityType.Option.toString(), "  ", type.toString(), strikePrice.toPlainString());
        //type missing
        verifyOptionMissingFieldFailure(proc, OptionFromRow.FIELD_OPTION_TYPE,
                symbol, SecurityType.Option.toString(), expiry, null, strikePrice.toPlainString());
        verifyOptionMissingFieldFailure(proc, OptionFromRow.FIELD_OPTION_TYPE,
                symbol, SecurityType.Option.toString(), expiry, "", strikePrice.toPlainString());
        //strike missing
        verifyOptionMissingFieldFailure(proc, OptionFromRow.FIELD_STRIKE_PRICE,
                symbol, SecurityType.Option.toString(), expiry, type.toString(), null);
        verifyOptionMissingFieldFailure(proc, OptionFromRow.FIELD_STRIKE_PRICE,
                symbol, SecurityType.Option.toString(), expiry, type.toString(), "");
        //verify invalid option type failure
        EnumSet<OptionType> validValues = EnumSet.allOf(OptionType.class);
        validValues.remove(OptionType.Unknown);
        for (String opType:Arrays.asList("invalid", OptionType.Unknown.toString())) {
            verifyParseFailure(proc,
                new I18NBoundMessage2P(Messages.INVALID_OPTION_TYPE,
                            opType, validValues.toString()),
                    symbol, SecurityType.Option.toString(), expiry,
                            opType, strikePrice.toPlainString());
        }
        //verify invalid strike price value failure
        String invalidStrike = "not big D";
        verifyParseFailure(proc,
            new I18NBoundMessage1P(Messages.INVALID_STRIKE_PRICE_VALUE, invalidStrike),
                symbol, SecurityType.Option.toString(), expiry, type.toString(), invalidStrike);
        //Create a processor without strike,type,expiry headers
        proc = new InstrumentProcessor();
        assertTrue(proc.canProcess(InstrumentFromRow.FIELD_SYMBOL, 0));
        assertTrue(proc.canProcess(InstrumentFromRow.FIELD_SECURITY_TYPE, 1));
        //no expiry header
        verifyOptionMissingFieldFailure(proc, OptionFromRow.FIELD_EXPIRY,
                symbol, SecurityType.Option.toString(), expiry, type.toString(), strikePrice.toPlainString());
        assertTrue(proc.canProcess(OptionFromRow.FIELD_EXPIRY, 2));
        //no option type
        verifyOptionMissingFieldFailure(proc, OptionFromRow.FIELD_OPTION_TYPE,
                symbol, SecurityType.Option.toString(), expiry, type.toString(), strikePrice.toPlainString());
        assertTrue(proc.canProcess(OptionFromRow.FIELD_OPTION_TYPE, 3));
        //no strike header
        verifyOptionMissingFieldFailure(proc, OptionFromRow.FIELD_STRIKE_PRICE,
                symbol, SecurityType.Option.toString(), expiry, type.toString(), strikePrice.toPlainString());

    }
    @Test
    public void timeInForce() throws Exception {
        final TimeInForceProcessor proc = new TimeInForceProcessor(2);
        assertEquals(null, apply(proc, "blue", "green", "", "red").getTimeInForce());
        assertEquals(TimeInForce.FillOrKill, apply(proc, "blue", "green",
                TimeInForce.FillOrKill.toString(), "red").getTimeInForce());
        //Valid Values
        Set<TimeInForce> validValues = EnumSet.allOf(TimeInForce.class);
        validValues.remove(TimeInForce.Unknown);
        new ExpectedFailure<OrderParsingException>(
                Messages.INVALID_TIME_IN_FORCE, "blues", validValues.toString()){
            protected void run() throws Exception {
                apply(proc, "red", "greed", "blues", "clues");
            }
        };
        new ExpectedFailure<OrderParsingException>(
                Messages.INVALID_TIME_IN_FORCE,
                TimeInForce.Unknown.toString(), validValues.toString()){
            protected void run() throws Exception {
                apply(proc, "red", "greed", TimeInForce.Unknown.toString(),
                        "clues");
            }
        };
    }
    private static void verifyOptionMissingFieldFailure(InstrumentProcessor inProc,
                                                        String inFieldName,
                                                        String... inRow) throws Exception {
        verifyParseFailure(inProc,
                new I18NBoundMessage1P(Messages.MISSING_OPTION_FIELD, inFieldName),
                inRow);
    }
    private static void verifyParseFailure(
            final FieldProcessor inProc,
            I18NBoundMessage inMessage,
            final String... inRow) throws Exception {
        new ExpectedFailure<OrderParsingException>(inMessage){
            @Override
            protected void run() throws Exception {
                apply(inProc, inRow);
            }
        };
    }
    private static OrderSingle apply(FieldProcessor inProcessor, String... inRow)
            throws OrderParsingException {
        OrderSingle inOrder = createOrder();
        inProcessor.apply(inRow, inOrder);
        return inOrder;
    }
    private static OrderSingle createOrder() {
        return Factory.getInstance().createOrderSingle();
    }
}
