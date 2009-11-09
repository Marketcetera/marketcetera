package org.marketcetera.client.instruments;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.client.OrderValidationException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.GregorianCalendar;
import java.util.Calendar;

/* $License$ */
/**
 * Validates option instruments.
 * <p>
 * The option expiry date is validated by {@link #validateExpiry(String)}.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class OptionValidationHandler extends InstrumentValidationHandler<Option> {
    /**
     * Creates an instance.
     */
    public OptionValidationHandler() {
        super(Option.class);
    }

    @Override
    public void validate(Instrument instrument) throws OrderValidationException {
        Option option = (Option) instrument;
        validateExpiry(option.getExpiry());
    }

    /**
     * Validates expiry field of an instrument.
     * <p>
     * Verifies that the expiry field has either of the following formats
     * <ul>
     * <li>YYYYMM</li>
     * <li>YYYYMMDD</li>
     * <li>YYYYMMwN</li>
     * </ul>
     * where:
     * <ul>
     * YYYY: represents the year
     * <br/>
     * MM: represents the month: 01-12
     * <br/>
     * DD: represents the day of the month: 01-31
     * <br/>
     * wN: represents the week number: w1-w5
     * </ul>
     *
     * @param inExpiry the expiry field of the option
     *
     * @throws OrderValidationException if the expiry field failed validation.
     */
    public static void validateExpiry(String inExpiry) throws OrderValidationException {
        Matcher m = EXPIRY_PATTERN.matcher(inExpiry);
        if(!m.matches()) {
            throw new OrderValidationException(new I18NBoundMessage1P(
                    Messages.INVALID_OPTION_EXPIRY_FORMAT,inExpiry));
        }
    }

    /**
     * Validates that the date matches the expected pattern per
     * {@link #validateExpiry(String)} and is a correct date for the given
     * month, year and day/week combination.
     * <p>
     * Note that this method is only available for convenience for UI validation.
     * This method is not invoked for validation when the client is sending
     * orders.
     *
     * @param inExpiry the expiry field of the option
     *
     * @throws OrderValidationException if the expiry field failed validation.
     */
    public static void validateExpiryDate(String inExpiry) throws OrderValidationException {
        Matcher m = EXPIRY_PATTERN.matcher(inExpiry);
        if(!m.matches()) {
            throw new OrderValidationException(new I18NBoundMessage1P(
                    Messages.INVALID_OPTION_EXPIRY_FORMAT,inExpiry));
        }
        if (m.groupCount() > 2) {
            String last = m.group(3);
            if (last != null && (!last.isEmpty())) {
                try {
                    int year = Integer.parseInt(m.group(1));
                    int month = Calendar.JANUARY + Integer.parseInt(m.group(2)) - 1;
                    if(last.charAt(0) == 'w'){  //$NON-NLS-1$
                        GregorianCalendar cal = new GregorianCalendar(year, month, 1);
                        cal.setFirstDayOfWeek(Calendar.SUNDAY);
                        //It's unclear how the current week number in the month
                        //should be computed. Based on the week number in year
                        //computation as specified by ISO 8601, assume
                        //that the first week of the month needs to have atleast
                        //4 days.
                        cal.setMinimalDaysInFirstWeek(4);
                        int week = Integer.parseInt(last.substring(1));
                        if(week > cal.getActualMaximum(Calendar.WEEK_OF_MONTH)) {
                            throw new OrderValidationException(new I18NBoundMessage1P(
                                    Messages.INVALID_OPTION_EXPIRY_WEEK, inExpiry));
                        }
                    } else {
                        int day = Integer.parseInt(last);
                        GregorianCalendar cal = new GregorianCalendar();
                        cal.clear();
                        cal.setLenient(false);
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, day);
                        try {
                            cal.getTime();
                        } catch (Exception e) {
                            throw new OrderValidationException(new I18NBoundMessage1P(
                                    Messages.INVALID_OPTION_EXPIRY_DAY, inExpiry));
                        }
                    }
                } catch (NumberFormatException e) {
                    throw new OrderValidationException(new I18NBoundMessage1P(
                            Messages.INVALID_OPTION_EXPIRY_FORMAT,inExpiry));
                }
            }
        }
    }

    private static final Pattern EXPIRY_PATTERN = Pattern.compile(
            "^(\\d{4})((?:0[1-9])|(?:1[012]))((?:(?:0[1-9])|(?:[12]\\d)|(?:3[01]))|(?:w[1-5]))?$");  //$NON-NLS-1$
}