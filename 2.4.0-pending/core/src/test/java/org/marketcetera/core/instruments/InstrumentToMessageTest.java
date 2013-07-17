package org.marketcetera.core.instruments;

import static org.marketcetera.trade.FutureExpirationMonth.*;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.*;
import org.marketcetera.trade.Currency;
import org.marketcetera.module.ExpectedFailure;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;
import java.util.LinkedList;
import java.math.BigDecimal;

import quickfix.Message;
import quickfix.DataDictionary;
import quickfix.field.*;
import quickfix.field.SecurityType;

/* $License$ */
/**
 * Tests {@link InstrumentToMessage} and its subclasses.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
@RunWith(Parameterized.class)
public class InstrumentToMessageTest {
    /**
     * Creates an instance.
     *
     * @param inCurrentVersion the current version for the test.
     */
    public InstrumentToMessageTest(FIXVersion inCurrentVersion) {
        mCurrentVersion = inCurrentVersion;
    }

    @BeforeClass
    public static void setup() throws Exception {
        LoggerConfiguration.logSetup();
        //Initialize all fix dictionaries.
        for (FIXVersion version : FIXVersion.values()) {
            FIXDataDictionaryManager.initialize(version,
                    version.getDataDictionaryURL());
        }
    }

    /**
     * Supplies each FIX version as a parameter to the test.
     *
     * @return list of FIX versions.
     */
    @Parameterized.Parameters
    public static List<Object[]> parameters() {
        List<Object[]> list = new LinkedList<Object[]>();
        for (FIXVersion version : FIXVersion.values()) {
            list.add(new Object[]{version});
        }
        return list;
    }

    /**
     * Tests equity instrument handling without dictionary.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void equity() throws Exception {
        Message msg = mCurrentVersion.getMessageFactory().newBasicOrder();
        InstrumentToMessage.SELECTOR.forInstrument(TEST_EQUITY).set(TEST_EQUITY, mCurrentVersion.toString(), msg);
        //verify security type
        if (FIXVersion.FIX40.equals(mCurrentVersion)) {
            assertEquals(false, msg.isSetField(SecurityType.FIELD));
        } else {
            assertEquals(true, msg.isSetField(SecurityType.FIELD));
            assertEquals(TEST_EQUITY.getSecurityType().getFIXValue(),
                    msg.getString(SecurityType.FIELD));
        }
        //verify symbol
        assertEquals(true, msg.isSetField(Symbol.FIELD));
        assertEquals(TEST_EQUITY.getSymbol(), msg.getString(Symbol.FIELD));
        //Test equivalence with InstrumentFromMessage
        assertEquals(TEST_EQUITY, InstrumentFromMessage.SELECTOR.forValue(msg).extract(msg));
    }
    
    /**
     * Tests currency instrument handling without dictionary.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void currency() throws Exception {
    	if (FIXVersion.FIX40.equals(mCurrentVersion))  // FX not supported in FIX 4.0
    	{
    		return;
    	}
        Message msg = mCurrentVersion.getMessageFactory().newBasicOrder();
        InstrumentToMessage.SELECTOR.forInstrument(TEST_CURRENCY).set(TEST_CURRENCY, mCurrentVersion.toString(), msg);
        //verify security type

            assertEquals(true, msg.isSetField(SecurityType.FIELD));
            assertEquals(TEST_CURRENCY.getSecurityType().getFIXValue(),
                    msg.getString(SecurityType.FIELD));
        //}
        //verify symbol
        assertEquals(true, msg.isSetField(Symbol.FIELD));
        assertEquals(TEST_CURRENCY.getSymbol(), msg.getString(Symbol.FIELD));
        //Test equivalence with InstrumentFromMessage
        assertEquals(TEST_CURRENCY, InstrumentFromMessage.SELECTOR.forValue(msg).extract(msg));
    }

    /**
     * Tests equity instrument handling with dictionary.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void equityDictionary() throws Exception {
        Message msg = mCurrentVersion.getMessageFactory().newBasicOrder();
        String msgType = msg.getHeader().getString(MsgType.FIELD);
        DataDictionary dictionary = FIXDataDictionaryManager.getFIXDataDictionary(mCurrentVersion).getDictionary();
        assertTrue(InstrumentToMessage.SELECTOR.forInstrument(TEST_EQUITY).isSupported(dictionary, msgType));
        InstrumentToMessage.SELECTOR.forInstrument(TEST_EQUITY).set(TEST_EQUITY, dictionary, msgType, msg);
        //verify security type
        if (isFieldPresent(dictionary, msg, msgType, SecurityType.FIELD)) {
            assertEquals(TEST_EQUITY.getSecurityType().getFIXValue(),
                    msg.getString(SecurityType.FIELD));
        }
        //verify symbol
        if (isFieldPresent(dictionary, msg, msgType, Symbol.FIELD)) {
            assertEquals(TEST_EQUITY.getSymbol(), msg.getString(Symbol.FIELD));
        }
        //Test equivalence with InstrumentFromMessage
        assertEquals(TEST_EQUITY, InstrumentFromMessage.SELECTOR.forValue(msg).extract(msg));
    }
    
    /**
     * Tests currency instrument handling with dictionary.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void currencyDictionary() throws Exception {
    	if (FIXVersion.FIX40.equals(mCurrentVersion))  // FX not supported in FIX 4.0
    	{
    		return;
    	}    	
        Message msg = mCurrentVersion.getMessageFactory().newBasicOrder();
        String msgType = msg.getHeader().getString(MsgType.FIELD);
        DataDictionary dictionary = FIXDataDictionaryManager.getFIXDataDictionary(mCurrentVersion).getDictionary();
        assertTrue(InstrumentToMessage.SELECTOR.forInstrument(TEST_CURRENCY).isSupported(dictionary, msgType));
        InstrumentToMessage.SELECTOR.forInstrument(TEST_CURRENCY).set(TEST_CURRENCY, dictionary, msgType, msg);
        //verify security type
        if (isFieldPresent(dictionary, msg, msgType, SecurityType.FIELD)) {
            assertEquals(TEST_CURRENCY.getSecurityType().getFIXValue(),
                    msg.getString(SecurityType.FIELD));
        }
        //verify symbol
        if (isFieldPresent(dictionary, msg, msgType, Symbol.FIELD)) {
            assertEquals(TEST_CURRENCY.getSymbol(), msg.getString(Symbol.FIELD));
        }
        //Test equivalence with InstrumentFromMessage
        assertEquals(TEST_CURRENCY, InstrumentFromMessage.SELECTOR.forValue(msg).extract(msg));
    }
    

    /**
     * Tests option instrument handling without dictionary.
     *
     * @throws Exception if there were unexpected errors
     */
    @Test
    public void options() throws Exception {
        for (final Option option:TEST_OPTIONS) {
            final Message msg = mCurrentVersion.getMessageFactory().newBasicOrder();
            //verify FIX specific stuff
            switch (mCurrentVersion) {
                case FIX40:
                    new ExpectedFailure<IllegalArgumentException>(
                            Messages.OPTION_NOT_SUPPORTED_FOR_FIX_VERSION.getText(
                                    mCurrentVersion.toString())) {
                        @Override
                        protected void run() throws Exception {
                            InstrumentToMessage.SELECTOR.forInstrument(option).
                                    set(option, mCurrentVersion.toString(), msg);
                        }
                    };
                    return;
                case FIX_SYSTEM:
                case FIX41:
                case FIX42:
                    InstrumentToMessage.SELECTOR.forInstrument(option).
                            set(option, mCurrentVersion.toString(), msg);
                    assertEquals(true, msg.isSetField(SecurityType.FIELD));
                    assertEquals(option.getSecurityType().getFIXValue(),
                            msg.getString(SecurityType.FIELD));
                    assertEquals(false, msg.isSetField(CFICode.FIELD));
                    if(option.getExpiry().length() > 6) {
                        assertEquals(option.getExpiry().substring(0,6), msg.getString(MaturityMonthYear.FIELD));
                        assertEquals(option.getExpiry().substring(6), msg.getString(MaturityDay.FIELD));
                    } else {
                        assertEquals(option.getExpiry(),msg.getString(MaturityMonthYear.FIELD));
                        assertEquals(false, msg.isSetField(MaturityDay.FIELD));
                    }
                    assertEquals(false, msg.isSetField(MaturityDate.FIELD));
                    break;
                default:
                    InstrumentToMessage.SELECTOR.forInstrument(option).
                            set(option, mCurrentVersion.toString(), msg);
                    assertEquals(false, msg.isSetField(SecurityType.FIELD));
                    assertEquals(true, msg.isSetField(CFICode.FIELD));
                    switch (option.getType()) {
                        case Call:
                            assertEquals("OCXXXX", msg.getString(CFICode.FIELD));
                            break;
                        case Put:
                            assertEquals("OPXXXX", msg.getString(CFICode.FIELD));
                            break;
                        default:
                            fail("Unhandled option type");
                            break;
                    }
                    if (FIXVersion.FIX43.equals(mCurrentVersion)) {
                        assertEquals(option.getExpiry(), msg.getString(MaturityDate.FIELD));
                        assertEquals(false, msg.isSetField(MaturityMonthYear.FIELD));
                    } else {
                        assertEquals(option.getExpiry(), msg.getString(MaturityMonthYear.FIELD));
                        assertEquals(false, msg.isSetField(MaturityDate.FIELD));
                    }
                    break;
            }
            //verify symbol
            assertEquals(true, msg.isSetField(Symbol.FIELD));
            assertEquals(option.getSymbol(), msg.getString(Symbol.FIELD));
            //verify strike price
            assertEquals(true, msg.isSetField(StrikePrice.FIELD));
            assertEquals(option.getStrikePrice(), new BigDecimal(msg.getString(StrikePrice.FIELD)));
            //verify equivalence with InstrumentFromMessage
            assertEquals(option, InstrumentFromMessage.SELECTOR.forValue(msg).extract(msg));
        }
    }

    /**
     * Tests option instrument handling with dictionary.
     *
     * @throws Exception if there were unexpected errors
     */
    @Test
    public void optionDictionary() throws Exception {
        for (Option option: TEST_OPTIONS) {
            Message msg = mCurrentVersion.getMessageFactory().newBasicOrder();
            String msgType = msg.getHeader().getString(MsgType.FIELD);
            DataDictionary dictionary = FIXDataDictionaryManager.getFIXDataDictionary(mCurrentVersion).getDictionary();
            //options should be supported in all versions except 4.0
            assertEquals(FIXVersion.FIX40 != mCurrentVersion,
                    InstrumentToMessage.SELECTOR.forInstrument(option).isSupported(dictionary, msgType));
            //Set the option onto the message
            InstrumentToMessage.SELECTOR.forInstrument(option).set(option, dictionary, msgType, msg);
            //verify security type
            String securityType = option.getSecurityType().getFIXValue();
            if(dictionary.isMsgField(msgType, SecurityType.FIELD) &&
                    dictionary.isFieldValue(SecurityType.FIELD, securityType)) {
                assertEquals(securityType,
                        msg.getString(SecurityType.FIELD));
            } else {
                assertEquals(false, msg.isSetField(SecurityType.FIELD));
            }
            //verify CFI code
            if (isFieldPresent(dictionary, msg, msgType, CFICode.FIELD)) {
                assertEquals(option.getType() == OptionType.Call ? "OCXXXX" : "OPXXXX",
                        msg.getString(CFICode.FIELD));
            }
            //verify OptionType
            if (isFieldPresent(dictionary, msg, msgType, PutOrCall.FIELD)) {
                assertEquals(option.getType().getFIXValue(),
                        msg.getInt(PutOrCall.FIELD));
            }
            //verify symbol
            if (isFieldPresent(dictionary, msg, msgType, Symbol.FIELD)) {
                assertEquals(option.getSymbol(), msg.getString(Symbol.FIELD));
            }
            //verify strike price
            if (isFieldPresent(dictionary, msg, msgType, StrikePrice.FIELD)) {
                assertEquals(option.getStrikePrice(), msg.getDecimal(StrikePrice.FIELD));
            }
            //verify expiry
            String expiry = option.getExpiry();
            if (isFieldPresent(dictionary, msg, msgType, MaturityMonthYear.FIELD)) {
                assertEquals(expiry, msg.getString(MaturityMonthYear.FIELD));
            }
            if(expiry.equals("20101010") && dictionary.isMsgField(msgType, MaturityDate.FIELD)) {
                assertTrue(expiry, msg.isSetField(MaturityDate.FIELD));
                assertEquals(expiry, msg.getString(MaturityDate.FIELD));
            } else {
                assertFalse(expiry, msg.isSetField(MaturityDate.FIELD));
            }
            if(expiry.equals("20101010") && dictionary.isMsgField(msgType, MaturityDay.FIELD)) {
                assertTrue(expiry, msg.isSetField(MaturityDay.FIELD));
                assertEquals("10", msg.getString(MaturityDay.FIELD));
            } else {
                assertFalse(expiry, msg.isSetField(MaturityDay.FIELD));
            }
            //verify equivalence with InstrumentFromMessage
            if (InstrumentToMessage.SELECTOR.forInstrument(option).isSupported(dictionary, msgType)) {
                assertEquals(option, InstrumentFromMessage.SELECTOR.forValue(msg).extract(msg));
            }
        }
    }

    @Test
    public void optionExpiry() throws Exception {
        final Option option = new Option("LBZ", "20101010", BigDecimal.TEN, OptionType.Call);
        assertEquals(Option.class, InstrumentToMessage.SELECTOR.forInstrument(option).
                getInstrumentType());
        final Message msg = mCurrentVersion.getMessageFactory().newBasicOrder();
        switch (mCurrentVersion) {
            case FIX40:
                break;
            case FIX_SYSTEM:
            case FIX41:
            case FIX42:
                InstrumentToMessage.SELECTOR.forInstrument(option).
                        set(option, mCurrentVersion.toString(), msg);
                assertEquals(true, msg.isSetField(MaturityMonthYear.FIELD));
                assertEquals("201010", msg.getString(MaturityMonthYear.FIELD));
                assertEquals(true, msg.isSetField(MaturityDay.FIELD));
                assertEquals("10", msg.getString(MaturityDay.FIELD));
                break;
            default:
                InstrumentToMessage.SELECTOR.forInstrument(option).
                        set(option, mCurrentVersion.toString(), msg);
                if (FIXVersion.FIX43.equals(mCurrentVersion)) {
                    assertEquals(true, msg.isSetField(MaturityDate.FIELD));
                    assertEquals(option.getExpiry(), msg.getString(MaturityDate.FIELD));
                } else {
                    assertEquals(true, msg.isSetField(MaturityMonthYear.FIELD));
                    assertEquals(option.getExpiry(), msg.getString(MaturityMonthYear.FIELD));
                }
                break;
        }
    }
    /**
     * Tests futures instrument handling without dictionary.
     *
     * @throws Exception if there were unexpected errors
     */
    @Test
    public void futures()
            throws Exception 
    {
        for (final Future future : TEST_FUTURES) {
            final Message msg = mCurrentVersion.getMessageFactory().newBasicOrder();
            SLF4JLoggerProxy.debug(InstrumentToMessageTest.class,
                                   "{} creates {}",
                                   future,
                                   msg);
            //verify FIX specific stuff
            switch (mCurrentVersion) {
                case FIX40:
                    new ExpectedFailure<IllegalArgumentException>(
                            Messages.FUTURES_NOT_SUPPORTED_FOR_FIX_VERSION.getText(mCurrentVersion.toString())) {
                        @Override
                        protected void run()
                                throws Exception
                        {
                            InstrumentToMessage.SELECTOR.forInstrument(future).set(future,
                                                                                   mCurrentVersion.toString(),
                                                                                   msg);
                        }
                    };
                    return;
                case FIX_SYSTEM:
                case FIX41:
                case FIX42:
                    InstrumentToMessage.SELECTOR.forInstrument(future).set(future,
                                                                           mCurrentVersion.toString(),
                                                                           msg);
                    assertTrue(msg.isSetField(SecurityType.FIELD));
                    assertEquals(future.getSecurityType().getFIXValue(),
                                 msg.getString(SecurityType.FIELD));
                    assertFalse(msg.isSetField(CFICode.FIELD));
                    assertEquals(future.getExpiryAsMaturityMonthYear().getValue(),
                                 msg.getString(MaturityMonthYear.FIELD));
                    break;
                default:
                    InstrumentToMessage.SELECTOR.forInstrument(future).set(future,
                                                                           mCurrentVersion.toString(),
                                                                           msg);
                    assertFalse(msg.isSetField(SecurityType.FIELD));
                    assertTrue(msg.isSetField(CFICode.FIELD));
                    assertEquals(future.getExpiryAsMaturityMonthYear().getValue(),
                                 msg.getString(MaturityMonthYear.FIELD));
                    break;
            }
            //verify symbol
            assertTrue(msg.isSetField(Symbol.FIELD));
            assertEquals(future.getSymbol(),
                         msg.getString(Symbol.FIELD));
            //verify equivalence with InstrumentFromMessage
            assertEquals(future,
                         InstrumentFromMessage.SELECTOR.forValue(msg).extract(msg));
        }
    }
    /**
     * Tests {@link InstrumentToMessage#setSecurityType(org.marketcetera.trade.Instrument, String, quickfix.Message)}.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void setSecurityType() throws Exception {
        Message msg = mCurrentVersion.getMessageFactory().newBasicOrder();
        //set security type to null or unknown value on a message that doesn't have it set.
        InstrumentToMessage.setSecurityType(new MyInstrument(null), mCurrentVersion.toString(), msg);
        assertEquals(false, msg.isSetField(SecurityType.FIELD));
        InstrumentToMessage.setSecurityType(new MyInstrument(
                org.marketcetera.trade.SecurityType.Unknown), mCurrentVersion.toString(), msg);
        assertEquals(false, msg.isSetField(SecurityType.FIELD));

        //set security type to null or unknown value on a message that has it set.
        msg.setField(new SecurityType(SecurityType.BANK_NOTES));
        InstrumentToMessage.setSecurityType(new MyInstrument(null), mCurrentVersion.toString(), msg);
        assertEquals(SecurityType.BANK_NOTES, msg.getString(SecurityType.FIELD));
        InstrumentToMessage.setSecurityType(new MyInstrument(
                org.marketcetera.trade.SecurityType.Unknown), mCurrentVersion.toString(), msg);
        assertEquals(SecurityType.BANK_NOTES, msg.getString(SecurityType.FIELD));

        msg = mCurrentVersion.getMessageFactory().newBasicOrder();
        String msgType = msg.getHeader().getString(MsgType.FIELD);
        DataDictionary dict = FIXDataDictionaryManager.getFIXDataDictionary(mCurrentVersion).getDictionary();
        //set security type to null or unknown value on a message that doesn't have it set.
        InstrumentToMessage.setSecurityType(new MyInstrument(null), dict, msgType, msg);
        assertEquals(false, msg.isSetField(SecurityType.FIELD));
        InstrumentToMessage.setSecurityType(new MyInstrument(
                org.marketcetera.trade.SecurityType.Unknown), dict, msgType, msg);
        assertEquals(false, msg.isSetField(SecurityType.FIELD));

        //set security type to null or unknown value on a message that has it set.
        msg.setField(new SecurityType(SecurityType.BANK_NOTES));
        InstrumentToMessage.setSecurityType(new MyInstrument(null), dict, msgType, msg);
        assertEquals(SecurityType.BANK_NOTES, msg.getString(SecurityType.FIELD));
        InstrumentToMessage.setSecurityType(new MyInstrument(
                org.marketcetera.trade.SecurityType.Unknown), dict, msgType, msg);
        assertEquals(SecurityType.BANK_NOTES, msg.getString(SecurityType.FIELD));
    }

    /**
     * Returns true if the specified field is present in the dictionary.
     * Additionally verifies that the field value is set / unset in the
     * message depending on whether the field is specified in the dictionary.
     *
     * @param inDictionary the dictionary
     * @param inMessage    the message
     * @param inMsgType    the message type
     * @param inField      the field tag
     * @return true if the field is in the dictionary.
     */
    private static boolean isFieldPresent(DataDictionary inDictionary,
                                          Message inMessage,
                                          String inMsgType,
                                          int inField) {
        boolean value = inDictionary.isMsgField(inMsgType, inField);
        assertEquals(value, inMessage.isSetField(inField));
        return value;
    }

    /**
     * Test instrument class to specify invalid security type values.
     */
    private static class MyInstrument extends Instrument {
        public MyInstrument(org.marketcetera.trade.SecurityType inSecurityType) {
            mSecurityType = inSecurityType;
        }

        @Override
        public String getSymbol() {
            return null;
        }

        @Override
        public org.marketcetera.trade.SecurityType getSecurityType() {
            return mSecurityType;
        }

        private final org.marketcetera.trade.SecurityType mSecurityType;
        private static final long serialVersionUID = 1L;
    }

    private final FIXVersion mCurrentVersion;
    private static final Equity TEST_EQUITY = new Equity("YBM");
    private static final Currency TEST_CURRENCY = new Currency("GBP/USD");
    private static final Option [] TEST_OPTIONS = {
            new Option("LBZ", "20101010", BigDecimal.TEN, OptionType.Call),
            new Option("LBZ", "20101010", BigDecimal.ONE, OptionType.Put),
            new Option("LBZ", "201010", BigDecimal.TEN, OptionType.Call),
            new Option("LBZ", "201010w2", BigDecimal.TEN, OptionType.Call),
    };
    private static final Future[] TEST_FUTURES = {
        new Future("LBZ", JANUARY, 2010),
        new Future("LBZ", FEBRUARY, 2011),
        new Future("LBZ", MARCH, 2012),
        new Future("LBZ", APRIL, 2013),
        Future.fromString("LBZ-201101"),
        Future.fromString("LBZ-20110130"),
    };
}
