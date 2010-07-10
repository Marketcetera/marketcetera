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
            "JAN"),
    /**
     * contract which expires in February
     */
    FEBRUARY('G',
             "FEB"),
    /**
     * contract which expires in March
     */
    MARCH('H',
          "MAR"),
    /**
     * contract which expires in April
     */
    APRIL('J',
          "APR"),
    /**
     * contract which expires in May
     */
    MAY('K',
        "MAY"),
    /**
     * contract which expires in June
     */
    JUNE('M',
         "JUN"),
    /**
     * contract which expires in July
     */
    JULY('N',
         "JUL"),
    /**
     * contract which expires in August
     */
    AUGUST('Q',
           "AUG"),
    /**
     * contract which expires in September
     */
    SEPTEMBER('U',
              "SEP"),
    /**
     * contract which expires in October
     */
    OCTOBER('V',
            "OCT"),
    /**
     * contract which expires in November
     */
    NOVEMBER('X',
             "NOV"),
    /**
     * contract which expires in December
     */
    DECEMBER('Z',
             "DEC");
    /**
     * Get the <code>FutureExpirationMonth</code> value that corresponds to the given code.
     *
     * @param inCode a <code>char</code> value
     * @return a <code>FutureExpirationMonth</code> value
     * @throws IllegalArgumentException if the given code does not correspond to a valid expiration month
     */
    public static FutureExpirationMonth getFutureExpirationMonth(char inCode)
    {
        return getFutureExpirationMonth(new StringBuffer().append(Character.toUpperCase(inCode)).toString());
    }
    /**
     * Get the <code>FutureExpirationMonth</code> value that corresponds to the given code.
     *
     * @param inCode a <code>String</code> value
     * @return a <code>FutureExpirationMonth</code> value
     * @throws IllegalArgumentException if the given code does not correspond to a valid expiration month
     */
    public static FutureExpirationMonth getFutureExpirationMonth(String inCode)
    {
        Validate.notNull(StringUtils.trimToNull(inCode));
        FutureExpirationMonth month = monthsByCode.get(inCode.toUpperCase());
        Validate.notNull(month);
        return month;
    }
    /**
     * Get the <code>FutureExpirationMonth</code> value that corresponds to the given description.
     *
     * @param inDescription a <code>String</code> value
     * @return a <code>FutureExpirationMonth</code> value
     * @throws IllegalArgumentException if the given description does not correspond to a valid expiration month
     */
    public static FutureExpirationMonth getFutureExpirationMonthByDescription(String inDescription)
    {
        Validate.notNull(StringUtils.trimToNull(inDescription));
        FutureExpirationMonth month = monthsByDescription.get(inDescription.toUpperCase());
        Validate.notNull(month);
        return month;
    }
    /**
     * Gets the <code>FutureExpirationMonth</code> value that corresponds to the given week of the year.
     *
     * @param inWeekOfYear an <code>int</code> value containing the week of the year
     * @param inYear an <code>int</code> value containing the year in question
     * @return a <code>FutureExpirationMonth</code> value
     */
    public static FutureExpirationMonth getFutureExpirationMonthByWeek(int inWeekOfYear,
                                                                       int inYear)
    {
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
