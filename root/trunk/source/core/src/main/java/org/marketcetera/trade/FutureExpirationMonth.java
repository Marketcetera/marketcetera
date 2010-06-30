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
            1),
    /**
     * contract which expires in February
     */
    FEBRUARY('G',
             2),
    /**
     * contract which expires in March
     */
    MARCH('H',
          3),
    /**
     * contract which expires in April
     */
    APRIL('J',
          4),
    /**
     * contract which expires in May
     */
    MAY('K',
        5),
    /**
     * contract which expires in June
     */
    JUNE('M',
         6),
    /**
     * contract which expires in July
     */
    JULY('N',
         7),
    /**
     * contract which expires in August
     */
    AUGUST('Q',
           8),
    /**
     * contract which expires in September
     */
    SEPTEMBER('U',
              9),
    /**
     * contract which expires in October
     */
    OCTOBER('V',
            10),
    /**
     * contract which expires in November
     */
    NOVEMBER('X',
             11),
    /**
     * contract which expires in December
     */
    DECEMBER('Z',
             12);
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
     * @return an <code>int</code> value
     */
    public int getMonthOfYear()
    {
        return monthOfYear;
    }
    /**
     * Gets the <code>FutureExpirationMonth</code> value associated with the given month of the year.
     *
     * @param inMonthOfYear an <code>int</code> value
     * @return a <code>FutureExpirationMonth</code> value
     * @throws IllegalArgumentException if the given monthOfYear value does not correspond to a value in [1..12]
     */
    public static FutureExpirationMonth getByMonthOfYear(int inMonthOfYear)
    {
        Validate.isTrue(inMonthOfYear >= 1 &&
                        inMonthOfYear <= 12);
        return monthsByMonthOfYear.get(inMonthOfYear);
    }
    /**
     * Create a new FutureExpirationMonth instance.
     *
     * @param inCode a <code>char</code> value
     * @param inMonthOfYear an <code>int</code> value
     */
    private FutureExpirationMonth(char inCode,
                                  int inMonthOfYear)
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
    private final int monthOfYear;
    /**
     * <code>FutureExpirationMonth</code> values by <code>monthOfYear</code>
     */
    private static final Map<Integer,FutureExpirationMonth> monthsByMonthOfYear = new HashMap<Integer,FutureExpirationMonth>();
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
