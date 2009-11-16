package org.marketcetera.photon.internal.core;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.marketcetera.photon.commons.Validate;
import org.marketcetera.photon.core.InstrumentPrettyPrinter;
import org.marketcetera.trade.Option;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Pretty prints {@link Option} objects for the UI.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class OptionPrettyPrinter extends InstrumentPrettyPrinter<Option> {

    private static final Pattern EXPIRY_PATTERN = Pattern
            .compile("^(\\d{4})(\\d{2})(\\d{2})?"); //$NON-NLS-1$

    private static final String EXPIRY_DISPLAY_MONTH = "%tb %<ty"; //$NON-NLS-1$

    private static final String EXPIRY_DISPLAY_DAY = "%tb %<td %<ty"; //$NON-NLS-1$

    /**
     * Constructor.
     */
    public OptionPrettyPrinter() {
        super(Option.class);
    }

    @Override
    protected String doPrint(Option instrument) {
        return String.format("%s %s %s %.2f", printExpiry(instrument), //$NON-NLS-1$
                instrument.getSymbol(), instrument.getType(), instrument
                        .getStrikePrice());
    }

    /**
     * Pretty prints an option expiry. If the expiry cannot be parsed, it is
     * returned.
     * 
     * @param option
     *            the option
     * @return the string value
     * @throws IllegalArgumentException
     *             if option is null
     */
    public static String printExpiry(Option option) {
        Validate.notNull(option, "option"); //$NON-NLS-1$
        String expiry = option.getExpiry();
        Matcher matcher = EXPIRY_PATTERN.matcher(expiry);
        if (matcher.matches()) {
            String day = matcher.group(3);
            Calendar c = new GregorianCalendar(Integer.parseInt(matcher
                    .group(1)), Integer.parseInt(matcher.group(2)) - 1,
                    day == null ? 1 : Integer.parseInt(day));
            if (day != null) {
                return String.format(EXPIRY_DISPLAY_DAY, c);
            } else {
                return String.format(EXPIRY_DISPLAY_MONTH, c);
            }
        }
        return expiry;
    }
}
