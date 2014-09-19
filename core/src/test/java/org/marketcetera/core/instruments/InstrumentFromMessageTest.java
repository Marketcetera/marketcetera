package org.marketcetera.core.instruments;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.ConvertibleBond;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.FutureExpirationMonth;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.DecimalField;
import quickfix.Field;
import quickfix.IntField;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.CFICode;
import quickfix.field.MaturityDate;
import quickfix.field.MaturityDay;
import quickfix.field.MaturityMonthYear;
import quickfix.field.PutOrCall;
import quickfix.field.SecurityType;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;

/* $License$ */

/**
 * Tests {@link InstrumentFromMessage} and its subclasses.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
public class InstrumentFromMessageTest {
    
    @BeforeClass
    public static void logSetup() throws Exception {
        LoggerConfiguration.logSetup();
        FIXDataDictionaryManager.initialize(FIX_VERSION,
                FIX_VERSION.getDataDictionaryURL());
    }
    
    @Test
    public void unhandledType() throws Exception {
        Message msg = FIX_VERSION.getMessageFactory().newBasicOrder();
        //unknown security type
        msg.setField(new SecurityType(SecurityType.BANK_NOTES));
        verifyNoHandlerFailure(msg);
        //null CFI code
        msg.setField(new CFICode());
        verifyNoHandlerFailure(msg);
        //empty CFI code
        msg.setField(new CFICode(""));
        verifyNoHandlerFailure(msg);
        //Invalid CFI code
        msg.setField(new CFICode("S"));
        verifyNoHandlerFailure(msg);
        //Invalid CFI code
        msg.setField(new CFICode("SO"));
        verifyNoHandlerFailure(msg);
        //remove security type field and test
        msg.removeField(SecurityType.FIELD);
        verifyNoHandlerFailure(msg);
    }
    
    @Test
    public void equity() throws Exception {
        //no security type, no symbol
        Message msg = FIX_VERSION.getMessageFactory().newBasicOrder();
        assertEquals(false, msg.isSetField(SecurityType.FIELD));
        assertNull(InstrumentFromMessage.SELECTOR.forValue(msg).extract(msg));
        // no security type, symbol set
        msg.setField(new Symbol("PQR"));
        assertEquals(new Equity("PQR"), InstrumentFromMessage.SELECTOR.forValue(msg).extract(msg));
        //security type set, symbol set
        msg.setField(new SecurityType(SecurityType.COMMON_STOCK));
        assertEquals(new Equity("PQR"), InstrumentFromMessage.SELECTOR.forValue(msg).extract(msg));
    }
    
    @Test
    public void currency() throws Exception {
        //no security type, no symbol
        Message msg = FIX_VERSION.getMessageFactory().newBasicOrder();
        assertEquals(false, msg.isSetField(SecurityType.FIELD));
        assertNull(InstrumentFromMessage.SELECTOR.forValue(msg).extract(msg));
        // no security type, symbol set
        msg.setField(new Symbol("GBP/USD"));
        //security type set, symbol set
        msg.setField(new SecurityType(SecurityType.FOREIGN_EXCHANGE_CONTRACT));
        assertEquals(new Currency("GBP/USD"), InstrumentFromMessage.SELECTOR.forValue(msg).extract(msg));
    }
    /**
     * Tests creating <code>ConvertibleBond</code> instruments from <code>Message</code> objects.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void convertibleBond()
            throws Exception
    {
        //no security type, no symbol
        Message msg = FIX_VERSION.getMessageFactory().newBasicOrder();
        assertEquals(false, msg.isSetField(SecurityType.FIELD));
        assertNull(InstrumentFromMessage.SELECTOR.forValue(msg).extract(msg));
        // security type set, symbol set
        msg.setField(new Symbol("US013817AT86"));
        msg.setField(new SecurityType(SecurityType.CONVERTIBLE_BOND));
        assertEquals(new ConvertibleBond("US013817AT86"),
                     InstrumentFromMessage.SELECTOR.forValue(msg).extract(msg));
    }
    @Test
    public void option() throws Exception {
        String expectedSymbol = "PQR";
        String expectedMY = "201010";
        String expectedDay = "10";
        String expectedExpiry = expectedMY + expectedDay;
        BigDecimal expectedStrike = BigDecimal.TEN;
        //Test all permutations of fields.
        for (Field<?> secType : new Field[]{null, new SecurityType(SecurityType.OPTION), new CFICode("O")}) {
            for (Field<?> symbol : new Field[]{null, new Symbol(expectedSymbol)}) {
                for (Field<?> optType : new Field[]{null, new PutOrCall(), new PutOrCall(PutOrCall.CALL), new PutOrCall(PutOrCall.PUT), new CFICode("O"), new CFICode("OC"), new CFICode("OP")}) {
                    for (Field<?> strikePrice : new Field[]{null, new StrikePrice(expectedStrike)}) {
                        for (Field<?> expiry : new Field[]{null, new MaturityMonthYear(expectedMY), new MaturityDate(expectedExpiry)}) {
                            for(Field<?> expiryDay: new Field[]{null, new MaturityDay(expectedDay)}) {
                                Message m = FIX_VERSION.getMessageFactory().newBasicOrder();
                                setFields(m, secType, symbol, optType, strikePrice, expiry, expiryDay);
                                SLF4JLoggerProxy.debug(this,"{},{},{},{},{},{}",secType,symbol,optType,strikePrice,expiry,expiryDay);
                                //figure out if we expect a value.
                                boolean isNotOption = (secType == null && (optType == null || (!(optType instanceof CFICode)))) ||
                                        symbol == null ||
                                        optType == null ||
                                        (optType instanceof CFICode && ((String)optType.getObject()).length() < 2) ||
                                        strikePrice == null ||
                                        expiry == null;
                                Instrument instrument = InstrumentFromMessage.SELECTOR.forValue(m).extract(m);
                                if(isNotOption) {
                                    assertThat(instrument, anyOf(nullValue(), not(instanceOf(Option.class))));
                                } else {
                                    Option option = (Option) instrument;
                                    assertEquals(org.marketcetera.trade.SecurityType.Option, option.getSecurityType());
                                    assertEquals(expectedSymbol, option.getSymbol());
                                    assertEquals(expectedStrike, option.getStrikePrice());
                                    if (expiry.getTag() == MaturityMonthYear.FIELD) {
                                        assertEquals(expiryDay == null? expectedMY: expectedExpiry, option.getExpiry());
                                    } else {
                                        assertEquals(expectedExpiry, option.getExpiry());
                                    }
                                    OptionType oType;
                                    if(optType.getTag() == PutOrCall.FIELD) {
                                        oType = OptionType.getInstanceForFIXValue((Integer) optType.getObject());
                                    } else {
                                        oType = CFICodeUtils.getOptionType((String)optType.getObject());
                                    }
                                    assertEquals(oType, option.getType());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * Tests futures and options with an invalid day value.
     * 
     * <p>This tests MATP-804.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void invalidDay()
            throws Exception
    {
        Field<?> secType = new SecurityType(SecurityType.FUTURE);
        Field<?> symbol = new Symbol("METC");
        Field<?> expiry = new MaturityMonthYear("201411");
        Field<?> day = new MaturityDay("31"); // this value is intentionally bogus
        Message m = FIX_VERSION.getMessageFactory().newBasicOrder();
        setFields(m,
                  secType,
                  symbol,
                  expiry,
                  day);
        Future future = (Future)InstrumentFromMessage.SELECTOR.forValue(m).extract(m);
        assertNotNull(future);
        assertEquals("201411",
                     future.getExpiryAsString());
        assertEquals(-1,
                     future.getExpirationDay());
        // test again with an option
        Field<?> optType = new PutOrCall(PutOrCall.CALL);
        Field<?> strikePrice = new StrikePrice(BigDecimal.TEN);
        secType = new SecurityType(SecurityType.OPTION);
        m = FIX_VERSION.getMessageFactory().newBasicOrder();
        setFields(m,
                  secType,
                  symbol,
                  optType,
                  strikePrice,
                  expiry,
                  day);
        Option option = (Option)InstrumentFromMessage.SELECTOR.forValue(m).extract(m);
        assertNotNull(option);
        assertEquals("201411",
                     option.getExpiry());
        assertEquals("20141122",
                     option.getAugmentedExpiry());
    }
    @Test
    public void future()
            throws Exception
    {
        String expectedSymbol = "CLF";
        FutureExpirationMonth expectedExpirationMonth = FutureExpirationMonth.JANUARY;
        int expectedExpirationYear = 2015;
        String expectedMY = "201501";
        //Test all permutations of fields.
        for(Field<?> secType : new Field[]{null, new SecurityType(SecurityType.FUTURE), new CFICode("F")}) {
            for(Field<?> symbol : new Field[]{null, new Symbol(expectedSymbol)}) {
                for(Field<?> expiry : new Field[]{null, new MaturityMonthYear(expectedMY)}) {
                    for(Field<?> day : new Field[] { null, new MaturityDay("10") }) {
                        Message m = FIX_VERSION.getMessageFactory().newBasicOrder();
                        setFields(m,
                                  secType,
                                  symbol,
                                  expiry,
                                  day);
                        SLF4JLoggerProxy.debug(this,
                                               "{} - {},{},{},{}",
                                               m,
                                               secType,
                                               symbol,
                                               expiry,
                                               day);
                        //figure out if we expect a value.
                        boolean isNotFuture = (secType == null ||
                                               symbol == null ||
                                               expiry == null);
                        Instrument instrument = InstrumentFromMessage.SELECTOR.forValue(m).extract(m);
                        if(isNotFuture) {
                            assertThat(instrument,
                                       anyOf(nullValue(),
                                             not(instanceOf(Future.class))));
                        } else {
                            Future future = (Future)instrument;
                            assertEquals(org.marketcetera.trade.SecurityType.Future,
                                         future.getSecurityType());
                            assertEquals(expectedSymbol,
                                         future.getSymbol());
                            if(expiry != null) {
                                assertEquals(expectedMY,
                                             future.getExpiryAsMaturityMonthYear().getValue());
                                assertEquals(expectedExpirationMonth,
                                             future.getExpirationMonth());
                                assertEquals(expectedExpirationYear,
                                             future.getExpirationYear());
                            }
                            if(day != null) {
                                assertEquals(10,
                                             future.getExpirationDay());
                            }
                        }
                    }
                }
            }
        }
    }

    private static void setFields(Message inMessage, Field<?>... inFields) {
        for(Field<?> field:inFields) {
            if(field != null) {
                if (field instanceof StringField) {
                    inMessage.setField((StringField) field);
                } else if(field instanceof IntField) {
                    inMessage.setField((IntField) field);
                } else if(field instanceof DecimalField) {
                    inMessage.setField((DecimalField) field);
                } else {
                    fail("unhandled field:" + field);
                }
            }
        }
    }

    private void verifyNoHandlerFailure(final Message inMsg) throws Exception {
        new ExpectedFailure<IllegalArgumentException>(
                Messages.NO_HANDLER_FOR_VALUE.getText(inMsg,
                        InstrumentFromMessage.class.getName())){
            @Override
            protected void run() throws Exception {
                InstrumentFromMessage.SELECTOR.forValue(inMsg);
            }
        };
    }

    private static final FIXVersion FIX_VERSION = FIXVersion.FIX44;
}
