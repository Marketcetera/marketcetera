package org.marketcetera.client.instruments;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.client.OrderValidationException;
import static org.marketcetera.client.instruments.OptionValidationHandler.validateExpiryDate;
import static org.marketcetera.client.instruments.OptionValidationHandler.validateExpiry;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.core.LoggerConfiguration;
import org.junit.Test;
import org.junit.BeforeClass;

import java.math.BigDecimal;

/* $License$ */
/**
 * Tests {@link InstrumentValidationHandler}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class InstrumentValidationHandlerTest {
    @BeforeClass
    public static void logSetup() {
        LoggerConfiguration.logSetup();
    }

    @Test
    public void equity() throws Exception {
        Equity equity = new Equity("WIU");
        InstrumentValidationHandler.SELECTOR.forInstrument(equity).validate(equity);
    }

    @Test
    public void option() throws Exception {
        validate("000001");
        validate("000009");
        validate("000010");
        validate("999912");
        validate("00000101");
        validate("00000109");
        validate("00000110");
        validate("00000119");
        validate("00000120");
        validate("00000129");
        validate("00000130");
        validate("99991231");
        validate("20090231");
        validate("999912w1");
        validate("999912w3");
        validate("999912w5");
        //incorrect length
        invalid("100");
        invalid("2000");
        invalid("10001");
        invalid("1000101");
        invalid("100010101");
        //invalid year
        invalid("AAAA10");
        invalid("AAAA1010");
        //invalid month
        invalid("100000");
        invalid("100013");
        invalid("100020");
        invalid("100032");
        invalid("10000012");
        invalid("10001312");
        invalid("10002012");
        invalid("1000AB12");
        //invalid day
        invalid("10000100");
        invalid("10000132");
        invalid("10000140");
        invalid("100001BA");
        //invalid week
        invalid("100001w0");
        invalid("100001w6");
        invalid("100001a1");
        invalid("100001w9");
        invalid("100001ww");
        invalid("100001x2");
    }

    @Test
    public void optionExpiryDate() throws Exception {
        validateExpiryDate("20010101");
        validateExpiryDate("20010131");
        validateExpiryDate("20001231");
        validateExpiryDate("20001201");
        validateExpiryDate("20000228");
        validateExpiryDate("20000229");
        validateExpiryDate("200002w1");
        validateExpiryDate("200002w4");
        validateExpiryDate("200905w5");
        invalidExpiryDate("20000230", Messages.INVALID_OPTION_EXPIRY_DAY);
        invalidExpiryDate("20010229", Messages.INVALID_OPTION_EXPIRY_DAY);
        invalidExpiryDate("20000631", Messages.INVALID_OPTION_EXPIRY_DAY);
        invalidExpiryDate("20090231", Messages.INVALID_OPTION_EXPIRY_DAY);
        //has only 4 weeks
        invalidExpiryDate("200902w5", Messages.INVALID_OPTION_EXPIRY_WEEK);
        //doesn't have 4 days in the first week.
        invalidExpiryDate("200910w5", Messages.INVALID_OPTION_EXPIRY_WEEK);
    }

    private static void invalid(final String inExpiry) throws Exception {
        new ExpectedFailure<OrderValidationException>(
                Messages.INVALID_OPTION_EXPIRY_FORMAT, inExpiry) {
            @Override
            protected void run() throws Exception {
                validateOption(inExpiry);
            }
        };
        new ExpectedFailure<OrderValidationException>(
                Messages.INVALID_OPTION_EXPIRY_FORMAT, inExpiry) {
            @Override
            protected void run() throws Exception {
                validateExpiry(inExpiry);
            }
        };
        new ExpectedFailure<OrderValidationException>(
                Messages.INVALID_OPTION_EXPIRY_FORMAT, inExpiry) {
            @Override
            protected void run() throws Exception {
                validateExpiryDate(inExpiry);
            }
        };

    }

    private static void invalidExpiryDate(final String inExpiry,
                                          I18NMessage1P inMsg) throws Exception {
        new ExpectedFailure<OrderValidationException>(
                inMsg, inExpiry) {
            @Override
            protected void run() throws Exception {
                validateExpiryDate(inExpiry);
            }
        };
    }

    private static void validate(String inExpiry) throws OrderValidationException {
        validateOption(inExpiry);
        validateExpiry(inExpiry);
    }

    private static void validateOption(String inExpiry) throws OrderValidationException {
        Option option = new Option("XYZ", inExpiry, BigDecimal.TEN, OptionType.Call);
        InstrumentValidationHandler.SELECTOR.forInstrument(option).validate(option);
    }

}
