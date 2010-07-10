package org.marketcetera.trade;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates the expiration month of a futures contract.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public enum FutureExpirationMonth
{
    /**
     * contract which expires in January
     */
    JANUARY('F',
            "JAN"), //$NON-NLS-1$
    /**
     * contract which expires in February
     */
    FEBRUARY('G',
             "FEB"), //$NON-NLS-1$
    /**
     * contract which expires in March
     */
    MARCH('H',
          "MAR"), //$NON-NLS-1$
    /**
     * contract which expires in April
     */
    APRIL('J',
          "APR"), //$NON-NLS-1$
    /**
     * contract which expires in May
     */
    MAY('K',
        "MAY"), //$NON-NLS-1$
    /**
     * contract which expires in June
     */
    JUNE('M',
         "JUN"), //$NON-NLS-1$
    /**
     * contract which expires in July
     */
    JULY('N',
         "JUL"), //$NON-NLS-1$
    /**
     * contract which expires in August
     */
    AUGUST('Q',
           "AUG"), //$NON-NLS-1$
    /**
     * contract which expires in September
     */
    SEPTEMBER('U',
              "SEP"), //$NON-NLS-1$
    /**
     * contract which expires in October
     */
    OCTOBER('V',
            "OCT"), //$NON-NLS-1$
    /**
     * contract which expires in November
     */
    NOVEMBER('X',
             "NOV"), //$NON-NLS-1$
    /**
     * contract which expires in December
     */
    DECEMBER('Z',
             "DEC"); //$NON-NLS-1$
    /**
     * Get the <code>FutureExpirationMonth</code> value that corresponds to the given code.
     *
     * @param inCode a <code>char</code> value
     * @return a <code>FutureExpirationMonth</code> value
     * @throws IllegalArgumentException if the given code does not correspond to a valid expiration month
     */
    public static FutureExpirationMonth getByCfiCode(char inCode)
    {
        return getByCfiCode(new StringBuffer().append(Character.toUpperCase(inCode)).toString());
    }
    /**
     * Get the <code>FutureExpirationMonth</code> value that corresponds to the given code.
     *
     * @param inCode a <code>String</code> value
     * @return a <code>FutureExpirationMonth</code> value
     * @throws IllegalArgumentException if the given code does not correspond to a valid expiration month
     */
    public static FutureExpirationMonth getByCfiCode(String inCode)
    {
        Validate.notNull(StringUtils.trimToNull(inCode),
                         Messages.NULL_CFI_CODE.getText());
        FutureExpirationMonth month = monthsByCode.get(inCode.toUpperCase());
        Validate.notNull(month,
                         Messages.INVALID_CFI_CODE.getText(inCode));
        return month;
    }
    /**
     * Get the <code>FutureExpirationMonth</code> value that corresponds to the given month name.
     *
     * @param inMonthName a <code>String</code> value containing a short US English month name
     * @return a <code>FutureExpirationMonth</code> value
     * @throws IllegalArgumentException if the given month name does not correspond to a valid expiration month
     */
    public static FutureExpirationMonth getByMonthShortName(String inMonthName)
    {
        Validate.notNull(StringUtils.trimToNull(inMonthName),
                         Messages.NULL_MONTH.getText());
        FutureExpirationMonth month = monthsByDescription.get(inMonthName.toUpperCase());
        Validate.notNull(month,
                         Messages.INVALID_MONTH.getText(inMonthName));
        return month;
    }
    /**
     * Gets the <code>FutureExpirationMonth</code> value that corresponds to the given week of the year.
     *
     * @param inWeekOfYear an <code>int</code> value containing the week of the year
     * @param inYear an <code>int</code> value containing the year in question
     * @return a <code>FutureExpirationMonth</code> value
     * @throws IllegalArgumentException if the given week number is invalid
     */
    public static FutureExpirationMonth getByWeekOfYear(int inWeekOfYear,
                                                        int inYear)
    {
        Validate.isTrue(inWeekOfYear >= 1 &&
                        inWeekOfYear <= 53,
                        Messages.INVALID_WEEK.getText(inWeekOfYear));
        Calendar calendar = Calendar.getInstance();
        calendar.setLenient(false);
        calendar.set(Calendar.YEAR,
                     inYear);
        calendar.set(Calendar.WEEK_OF_YEAR,
                     inWeekOfYear);
        int numValue = calendar.get(Calendar.MONTH);
        FutureExpirationMonth month = FutureExpirationMonth.values()[numValue]; 
        Validate.notNull(month);
        return month;
    }
    /**
     * Gets the <code>FutureExpirationMonth</code> value associated with the given month of the year.
     *
     * @param inMonthOfYear a <code>String</code> value
     * @return a <code>FutureExpirationMonth</code> value
     * @throws IllegalArgumentException if the given monthOfYear value does not correspond to a value in [1..12]
     */
    public static FutureExpirationMonth getByMonthOfYear(String inMonthOfYear)
    {
        int month = Integer.valueOf(inMonthOfYear);
        Validate.isTrue(month >= 1 &&
                        month <= 12);
        return FutureExpirationMonth.values()[month-1];
    }
    /**
     * Gets the code that corresponds to this <code>FutureExpirationMonth</code>.
     *
     * @return a <code>char</code> value
     */
    public char getCode()
    {
        return code;
    }
    /**
     * Get the monthOfYear value.
     *
     * @return a <code>String</code> value
     */
    public String getMonthOfYear()
    {
        StringBuffer output = new StringBuffer();
        if(ordinal()+1 < 10) {
            output.append('0');
        }
        output.append(ordinal()+1);
        return output.toString();
    }
    /**
     * Create a new FutureExpirationMonth instance.
     *
     * @param inCode a <code>char</code> value
     * @param inMonthOfYear a <code>String</code> value
     */
    private FutureExpirationMonth(char inCode,
                                  String inMonth)
    {
        code = inCode;
        month = inMonth;
    }
    /**
     * the code that corresponds to the expiration month
     */
    private final char code;
    /**
     * the description of the month
     */
    private final String month;
    /**
     * <code>FutureExpirationMonth</code> values by <code>CFI Code</code>
     */
    private static final Map<String,FutureExpirationMonth> monthsByCode = new HashMap<String,FutureExpirationMonth>();
    /**
     * <code>FutureExpirationMonth</code> values by month description
     */
    private static final Map<String,FutureExpirationMonth> monthsByDescription = new HashMap<String,FutureExpirationMonth>();
    /**
     * Initializes static values for <code>FutureExpirationMonth</code>.
     */
    static
    {
        for(FutureExpirationMonth month : FutureExpirationMonth.values()) {
            monthsByCode.put("" + month.getCode(),
                             month);
            monthsByDescription.put(month.month,
                                    month);
        }
    }
}
