package org.marketcetera.orderloader.system;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.*;
import org.marketcetera.orderloader.OrderParsingException;
import org.marketcetera.orderloader.Messages;
import org.marketcetera.module.ExpectedFailure;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Map;
import java.util.HashMap;
import java.util.EnumSet;
import java.util.Set;
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
    public void symbolNoSecurityType() throws Exception {
        final SymbolProcessor proc = new SymbolProcessor();
        proc.setSymbolIdx(1);
        assertEquals(null, apply(proc, "java", null, "mava").getSymbol());
        assertEquals(null, apply(proc, "java", "", "mava").getSymbol());
        assertEquals(new MSymbol("kava"), apply(proc, "java", "kava", "mava").getSymbol());
    }
    @Test
    public void symbolWithSecurityType() throws Exception {
        final SymbolProcessor proc = new SymbolProcessor();
        proc.setSymbolIdx(0);
        proc.setSecurityTypeIdx(1);
        assertEquals(null, apply(proc, null, null, "mava").getSymbol());
        assertEquals(null, apply(proc, "", null, "mava").getSymbol());
        assertEquals(null, apply(proc, null, SecurityType.CommonStock.toString(), "mava").getSymbol());
        assertEquals(null, apply(proc, "", SecurityType.CommonStock.toString(), "mava").getSymbol());
        assertEquals(new MSymbol("java"), apply(proc, "java", null, "mava").getSymbol());
        assertEquals(new MSymbol("java"), apply(proc, "java", "", "mava").getSymbol());
        assertEquals(new MSymbol("java", SecurityType.Option), apply(proc, "java", SecurityType.Option.toString(), "mava").getSymbol());
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
    private OrderSingle apply(FieldProcessor inProcessor, String... inRow)
            throws OrderParsingException {
        OrderSingle inOrder = createOrder();
        inProcessor.apply(inRow, inOrder);
        return inOrder;
    }
    private OrderSingle createOrder() {
        return Factory.getInstance().createOrderSingle();
    }
}
