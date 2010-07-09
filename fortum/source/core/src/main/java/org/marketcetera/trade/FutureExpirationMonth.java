package org.marketcetera.trade;

import java.util.HashMap;
import java.util.Map;

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
            "01"),
    /**
     * contract which expires in February
     */
    FEBRUARY('G',
             "02"),
    /**
     * contract which expires in March
     */
    MARCH('H',
          "03"),
    /**
     * contract which expires in April
     */
    APRIL('J',
          "04"),
    /**
     * contract which expires in May
     */
    MAY('K',
        "05"),
    /**
     * contract which expires in June
     */
    JUNE('M',
         "06"),
    /**
     * contract which expires in July
     */
    JULY('N',
         "07"),
    /**
     * contract which expires in August
     */
    AUGUST('Q',
           "08"),
    /**
     * contract which expires in September
     */
    SEPTEMBER('U',
              "09"),
    /**
     * contract which expires in October
     */
    OCTOBER('V',
            "10"),
    /**
     * contract which expires in November
     */
    NOVEMBER('X',
             "11"),
    /**
     * contract which expires in December
     */
    DECEMBER('Z',
             "12");
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
        FutureExpirationMonth month = monthsByCode.get(inCode.toUpperCase());
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
        return monthOfYear;
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
        return monthsByMonthOfYear.get(inMonthOfYear);
    }
    /**
     * Create a new FutureExpirationMonth instance.
     *
     * @param inCode a <code>char</code> value
     * @param inMonthOfYear a <code>String</code> value
     */
    private FutureExpirationMonth(char inCode,
                                  String inMonthOfYear)
    {
        code = inCode;
        monthOfYear = inMonthOfYear;
    }
    /**
     * the code that corresponds to the expiration month
     */
    private final char code;
    /**
     * the month of year in the range of [01-12]
     */
    private final String monthOfYear;
    /**
     * <code>FutureExpirationMonth</code> values by <code>monthOfYear</code>
     */
    private static final Map<String,FutureExpirationMonth> monthsByMonthOfYear = new HashMap<String,FutureExpirationMonth>();
    /**
     * <code>FutureExpirationMonth</code> values by <code>CFI Code</code>
     */
    private static final Map<String,FutureExpirationMonth> monthsByCode = new HashMap<String,FutureExpirationMonth>(); 
    /**
     * Initializes static values for <code>FutureExpirationMonth</code>.
     */
    static
    {
        for(FutureExpirationMonth month : FutureExpirationMonth.values()) {
            monthsByMonthOfYear.put(month.getMonthOfYear(),
                                    month);
            monthsByCode.put("" + month.getCode(),
                             month);
        }
    }
}
