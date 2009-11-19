package org.marketcetera.options;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Various option related utilities. 
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class OptionUtils {

    private static Calendar getSaturdayAfterThirdFriday(int month, int year) {
        Calendar cal = new GregorianCalendar(year, month, 1);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int firstFridayOffset = (Calendar.FRIDAY - dayOfWeek + 7)%7;
		cal.add(Calendar.DAY_OF_MONTH, firstFridayOffset + 15); // two weeks and a day
        return cal;
    }

    /**
     * Adds day to a YYYYMM expiry string. The day is the Saturday after the
     * third Friday of the month (US standard rules). If the provided expiry is
     * not YYYYMM, or cannot be normalized for any reason, null is returned.
     * 
     * @param expiry an option expiry string
     * @return the expiry in YYYYMMDD, or null
     */
    public static String normalizeUSEquityOptionExpiry(String expiry) {
        if (expiry.length() == 6) {
            try {
                int expiryYear = Integer.parseInt(expiry.substring(0, 4));
                int expiryMonth = Integer.parseInt(expiry.substring(4));
                if (expiryMonth > 0 && expiryMonth < 13) {
                    Calendar cal = getSaturdayAfterThirdFriday(expiryMonth - 1,
                            expiryYear);
                    int expiryDay = cal.get(Calendar.DAY_OF_MONTH);
                    return String.format("%s%02d", expiry, expiryDay);
                }
            } catch (NumberFormatException e) {
                // unsupported format, return expiry as is
            }
        }
        return null;
    }

    /**
     * Normalizes the supplied expiry date with a day if it doesn't
     * include the day, ie. if it's in YYYYMM format. If the supplied
     * expiry doesn't need to be normalized or cannot be normalized, a
     * null value is returned back.
     * <p>
     * This method looks for a {@link OptionExpiryNormalizer custom} option
     * expiry normalization implementation. If one is found, that implementation
     * is used to carry out the option expiry normalization. If no such
     * implementation is found the
     * {@link #normalizeUSEquityOptionExpiry(String) US option expiry}
     * normalization is applied. 
     *  
     * @param inExpiry the option expiry string.
     *
     * @return the expiry in YYYYMMDD, if the supplied expiry was normalized,
     * null if it wasn't.
     */
    public static String normalizeEquityOptionExpiry(String inExpiry) {
        OptionExpiryNormalizer normalizer = getNormalizer();
        if(normalizer != null) {
            return normalizer.normalizeEquityOptionExpiry(inExpiry);
        }
        return normalizeUSEquityOptionExpiry(inExpiry);
    }

    /**
	 * Gets the <code>OptionType</code> value for the given character
	 * interpreted as an OSI-compliant symbol value.
	 *
	 * @param inOsiChar a <code>char</code> value
	 * @return an <code>OptionType</code> value corresponding with the given
	 *  <code>character</code> or {@link OptionType#Unknown}
	 */
	public static OptionType getOptionTypeForOSICharacter(char inOsiChar)
	{
	    switch(inOsiChar) {
	        case 'P':
	            return OptionType.Put;
	        case 'C':
	            return OptionType.Call;
	        default:
	            return OptionType.Unknown;
	    }
	}
    /**
     * Gets an <code>Option</code> from the given full symbol if the given symbol
     * complies with the option format set out in the
     * <a href="http://www.theocc.com/initiatives/symbology/default.jsp">Option Symbology Initiative</a>.
     *
     * @param inFullSymbol a <code>String</code> value
     * @return an <code>Option</code> value compliant with the OSI
     * @throws IllegalArgumentException if the given full symbol is not OSI-compliant
     */
    public static Option getOsiOptionFromString(String inFullSymbol)
    {
        // this is basic check for the symbol to see if syntactically conforms to the OSI standard - it does not check
        //  for valid dates, for example, just that the right type of character is at the right place in the symbol
        if(OSI_SYMBOL_PATTERN.matcher(inFullSymbol).matches()) {
            // we now know that the symbol is 21 characters long and can be split into sensical pieces
            String symbol = inFullSymbol.substring(0,
                                                   6);
            // note that the Option object expects a four-character year.  the OSI standard doesn't specify how two-character
            //  years are to be interpreted.
            String expiryYear = String.valueOf(getFullYear(Integer.parseInt(inFullSymbol.substring(6,
                                                                                                   8))));
            String expiryMonth = inFullSymbol.substring(8,
                                                        10);
            String expiryDay = inFullSymbol.substring(10,
                                                      12);
            OptionType type = getOptionTypeForOSICharacter(inFullSymbol.charAt(12));
            BigDecimal strikeWhole = new BigDecimal(inFullSymbol.substring(13,
                                                                           18));
            BigDecimal strikeDecimal = new BigDecimal(inFullSymbol.substring(18,
                                                                             21));
            BigDecimal strike = new BigDecimal(String.format("%s.%s", //$NON-NLS-1$
                                                             strikeWhole,
                                                             strikeDecimal));
            Option osiOption = new Option(symbol,
                                          String.format("%s%s%s", //$NON-NLS-1$
                                                        expiryYear,
                                                        expiryMonth,
                                                        expiryDay),
                                          strike,
                                          type);
            return osiOption;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Gets a string symbol as specified by <a
     * href="http://www.theocc.com/initiatives/symbology/default.jsp">Option
     * Symbology Initiative</a> from an <code>Option</code>. The provided option
     * must have property values compliant with OSI tuples. Otherwise, an
     * exception may be thrown, or an invalid OSI symbol may be returned.
     * 
     * @param inOption an <code>Option</code> value
     * @return a <code>String</code> value compliant with the OSI
     * @throws IllegalArgumentException
     *             if the given option symbol is greater than 6 characters, if
     *             the expiry is not 8 digits, if the type is not {@code
     *             OptionType.Call} or {@code OptionType.Put}, or if the strike
     *             is negative, has more than 5 digits to the left of the
     *             decimal point, or more than 3 digits to the right.
     */
    public static String getOsiSymbolFromOption(Option inOption)
    {
        String symbol = inOption.getSymbol();
        if (symbol.length() > 6) {
            throw new IllegalArgumentException();
        }
        String expiry = inOption.getExpiry().substring(2);
        if (expiry.length() != 6) {
            throw new IllegalArgumentException();
        }
        for (char c : expiry.toCharArray()) {
            if (!Character.isDigit(c)) {
                throw new IllegalArgumentException();
            }
        }
        BigDecimal strike = inOption.getStrikePrice();
        if (strike.signum() == -1 || strike.scale() > 3) {
            throw new IllegalArgumentException();
        }
        long longStrike = inOption.getStrikePrice().movePointRight(3).longValue();
        if (longStrike > 99999999) {
            throw new IllegalArgumentException();
        }
        char type;
        switch (inOption.getType()) {
        case Call:
            type = 'C';
            break;
        case Put:
            type = 'P';
            break;
        default:
            throw new IllegalArgumentException();
        }
        return String.format("%-6s%s%s%08d", //$NON-NLS-1$
               symbol, expiry, type, longStrike);
    }
    /**
     * Returns a full year complete with century from the given
     * year-only value.
     *
     * <p>This method assumes that the given value is a number in the
     * interval [0,99].  The returned value will be the given year plus
     * the current century.  If the resulting value < today, the returned
     * value will be in the next century instead.
     *   
     * @param inYear an <code>int</code> containing a number between 0 and 99 inclusive
     * @return an <code>int</code> value containing a full year representation (century and year)
     * @throws IllegalArgumentException if the given year is outside of the interval [0,99] 
     */
    private static int getFullYear(int inYear)
    {
        if(inYear < 0 ||
           inYear > 99) {
            throw new IllegalArgumentException();
        }
        // determine current century as an int
        int currentYear = GregorianCalendar.getInstance().get(Calendar.YEAR);
        int currentCentury = (currentYear / 100) * 100;
        int extrapolatedYear = currentCentury + inYear;
        // completeYear contains an int representing a specific year
        if(extrapolatedYear < currentYear) {
            extrapolatedYear += 100;
        }
        return extrapolatedYear;
    }
    /**
     * Load the custom option expiry normalizer if any.
     *
     * @return the custom option expiry normalizer if found, null otherwise.
     */
    private static OptionExpiryNormalizer getNormalizer() {
        if (sNormalizerLoaded) {
            return sOptionExpiryNormalizer;
        }
        synchronized (OptionUtils.class) {
            if (!sNormalizerLoaded) {
                Class<OptionExpiryNormalizer> normalizerClass = OptionExpiryNormalizer.class;
                //Use the context class loader when unit testing to facilitate unit testing
                /*
                * The following section of code uses the classloader for this jar
                * for loading the custom loader in a production install.
                * It is written to use the thread context classloader within
                * a unit test run to facilitate testing of this code from within
                * a unit test.
                * It is not desirable to use the context classloader in production
                * as it might yield different results depending on the context
                * it is invoked from.
                */
                ClassLoader cl = sIsTest
                        ? Thread.currentThread().getContextClassLoader()
                        : normalizerClass.getClassLoader();
                OptionExpiryNormalizer normalizer = null;
                try {
                    ServiceLoader<OptionExpiryNormalizer> loader = ServiceLoader.load(
                            normalizerClass, cl);
                    Iterator<OptionExpiryNormalizer> iter = loader.iterator();
                    if (iter.hasNext()) {
                        normalizer = iter.next();
                    }
                } catch (Exception e) {
                    Messages.LOG_ERROR_LOADING_OPTION_EXPIRY_NORMALIZER.warn(OptionUtils.class, e);
                } catch (ServiceConfigurationError e) {
                    Messages.LOG_ERROR_LOADING_OPTION_EXPIRY_NORMALIZER.warn(OptionUtils.class, e);
                } finally {
                    if (normalizer != null) {
                        Messages.LOG_OPTION_EXPIRY_NORMALIZER_CUSTOMIZED.info(
                                OptionUtils.class,
                                normalizer.getClass().getName());
                    }
                    sOptionExpiryNormalizer = normalizer;
                    sNormalizerLoaded = true;
                }
            }
            return sOptionExpiryNormalizer;
        }
    }

    /**
     * This method is provided to facilitate testing. It's not meant to be
     * used outside of unit-testing.
     */
    static void resetNormalizerLoaded() {
        sNormalizerLoaded = false;
    }

    /**
     * Sets up the class for testing. When setup for testing,
     * the {@link #getNormalizer()} method uses the Thread context classloader
     * to load the custom option normalizer instead of the current class'
     * classloader.
     * <p>
     * This method is only meant to be invoked from a unit test.
     */
    static void setupForTest() {
        sIsTest = true;
    }
    /**
     * this pattern does a basic syntax check of a symbol to see if it complies with the OSI
     * note that this pattern does not check the expiry date to see if it is completely valid, for example Feb 31st would be valid
     */
    private static final Pattern OSI_SYMBOL_PATTERN = Pattern.compile(".{6}\\d{2}(0\\d|1[0-2])(0[1-9]|[12]\\d|3[01])(C|P)\\d{5}\\d{3}"); //$NON-NLS-1$
    private static volatile OptionExpiryNormalizer sOptionExpiryNormalizer;
    private static volatile boolean sNormalizerLoaded = false;
    private static volatile boolean sIsTest = false;
}
