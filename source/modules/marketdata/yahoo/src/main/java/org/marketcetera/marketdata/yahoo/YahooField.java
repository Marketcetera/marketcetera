package org.marketcetera.marketdata.yahoo;

import java.util.HashMap;
import java.util.Map;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents a field in a Yahoo market data request. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.4
 */
@ClassVersion("$Id$")
enum YahooField
{
    ASK("a"), //$NON-NLS-1$
    AVERAGE_DAILY_VOLUME("a2"), //$NON-NLS-1$
    ASK_SIZE("a5"), //$NON-NLS-1$
    BID("b"), //$NON-NLS-1$
    REAL_TIME_ASK("b2"), //$NON-NLS-1$
    REAL_TIME_BID("b3"), //$NON-NLS-1$
    BOOK_VALUE("b4"), //$NON-NLS-1$
    BID_SIZE("b6"), //$NON-NLS-1$
    CHANGE_AND_PERCENT_CHANGE("c"), //$NON-NLS-1$
    CHANGE("c1"), //$NON-NLS-1$
    COMMISSION("c3"), //$NON-NLS-1$
    REAL_TIME_CHANGE("c6"), //$NON-NLS-1$
    REAL_TIME_AFTER_HOURS_CHANGE("c8"), //$NON-NLS-1$
    DIVIDEND_PER_SHARE("d"), //$NON-NLS-1$
    LAST_TRADE_DATE("d1"), //$NON-NLS-1$
    TRADE_DATE("d2"), //$NON-NLS-1$
    EARNINGS_PER_SHARE("e"), //$NON-NLS-1$
    ERROR_INDICATION("e1"), //$NON-NLS-1$
    EPS_ESTIMATE_CURRENT_YEAR("e7"), //$NON-NLS-1$
    EPS_ESTIMATE_NEXT_YEAR("e8"), //$NON-NLS-1$
    EPS_ESTIMATE_NEXT_QUARTER("e9"), //$NON-NLS-1$
    FLOAT_SHARES("f6"), //$NON-NLS-1$
    DAY_LOW("g"), //$NON-NLS-1$
    DAY_HIGH("h"), //$NON-NLS-1$
    YEAR_LOW("j"), //$NON-NLS-1$
    YEAR_HIGH("k"), //$NON-NLS-1$
    HOLDINGS_GAIN_PERCENT("g1"), //$NON-NLS-1$
    ANNUALIZED_GAIN("g3"), //$NON-NLS-1$
    HOLDINGS_GAIN("g4"), //$NON-NLS-1$
    REAL_TIME_HOLDINGS_GAIN_PERCENT("g5"), //$NON-NLS-1$
    REAL_TIME_HOLDINGS_GAIN("g6"), //$NON-NLS-1$
    MORE_INFO("i"), //$NON-NLS-1$
    REAL_TIME_ORDER_BOOK("i5"), //$NON-NLS-1$
    MARKET_CAPITALIZATION("j1"), //$NON-NLS-1$
    REAL_TIME_MARKET_CAPITALIZATION("j3"), //$NON-NLS-1$
    EBITDA("j4"), //$NON-NLS-1$
    CHANGE_FROM_YEAR_LOW("j5"), //$NON-NLS-1$
    PERCENT_CHANGE_FROM_YEAR_LOW("j6"), //$NON-NLS-1$
    REAL_TIME_LAST_TRADE_WITH_TIME("k1"), //$NON-NLS-1$
    REAL_TIME_CHANGE_PERCENT("k2"), //$NON-NLS-1$
    LAST_TRADE_SIZE("k3"), //$NON-NLS-1$
    CHANGE_FROM_YEAR_HIGH("k4"), //$NON-NLS-1$
    PERCENT_CHANGE_FROM_YEAR_HIGH("k5"), //$NON-NLS-1$
    LAST_TRADE_WITH_TIME("l"), //$NON-NLS-1$
    LAST_TRADE_PRICE_ONLY("l1"), //$NON-NLS-1$
    HIGH_LIMIT("l2"), //$NON-NLS-1$
    LOW_LIMIT("l3"), //$NON-NLS-1$
    DAY_RANGE("m"), //$NON-NLS-1$
    REAL_TIME_DAY_RANGE("m2"), //$NON-NLS-1$
    MOVING_AVERAGE_50_DAY("m3"), //$NON-NLS-1$
    MOVING_AVERAGE_200_DAY("m4"), //$NON-NLS-1$
    CHANGE_FROM_MOVING_AVERAGE_200_DAY("m5"), //$NON-NLS-1$
    PERCENT_CHANGE_FROM_MOVING_AVERAGE_200_DAY("m6"), //$NON-NLS-1$
    CHANGE_FROM_MOVING_AVERAGE_50_DAY("m7"), //$NON-NLS-1$
    PERCENT_CHANGE_FROM_MOVING_AVERAGE_50_DAY("m8"), //$NON-NLS-1$
    NAME("n"), //$NON-NLS-1$
    NOTES("n4"), //$NON-NLS-1$
    OPEN("o"), //$NON-NLS-1$
    PREVIOUS_CLOSE("p"), //$NON-NLS-1$
    PRICE_PAID("p1"), //$NON-NLS-1$
    PERCENT_CHANGE("p2"), //$NON-NLS-1$
    PRICE_OVER_SALES("p5"), //$NON-NLS-1$
    PRICE_OVER_BOOK("p6"), //$NON-NLS-1$
    EXPECTED_DIVIDEND_DATE("q"), //$NON-NLS-1$
    PE_RATIO("r"), //$NON-NLS-1$
    DIVIDEND_PAY_DATE("r1"), //$NON-NLS-1$
    REAL_TIME_PE_RATION("r2"), //$NON-NLS-1$
    PEG_RATION("r5"), //$NON-NLS-1$
    PRICE_OVER_EPS_ESTIMATE_CURRENT_YEAR("r6"), //$NON-NLS-1$
    PRICE_OVER_EPS_ESTIMATE_NEXT_YEAR("r7"), //$NON-NLS-1$
    SYMBOL("s"), //$NON-NLS-1$
    SHARES_OWNED("s1"), //$NON-NLS-1$
    SHORT_RATIO("s7"), //$NON-NLS-1$
    LAST_TRADE_TIME("t1"), //$NON-NLS-1$
    TRADE_LINKS("t6"), //$NON-NLS-1$
    TICKER_TREND("t7"), //$NON-NLS-1$
    TARGET_PRICE_1_YEAR("t8"), //$NON-NLS-1$
    VOLUME("v"), //$NON-NLS-1$
    HOLDINGS_VALUE("v1"), //$NON-NLS-1$
    REAL_TIME_HOLDINGS_VALUE("v7"), //$NON-NLS-1$
    RANGE_52_WEEK("w"), //$NON-NLS-1$
    DAY_VALUE_CHANGE("w1"), //$NON-NLS-1$
    REAL_TIME_DAY_VALUE_CHANGE("w4"), //$NON-NLS-1$
    STOCK_EXCHANGE("x"), //$NON-NLS-1$
    DIVIDEND_YIELD("y"); //$NON-NLS-1$
    /**
     * Gets the field for the given literal value.
     *
     * @param inCode a <code>String</code> value
     * @return a <code>YahooField</code> value or <code>null</code> if no field corresponds to the given literal
     */
    public static YahooField getFieldFor(String inCode)
    {
        synchronized(fields) {
            if(fields.isEmpty()) {
                for(YahooField field : YahooField.values()) {
                    fields.put(field.getCode(),
                               field);
                }
            }
            return fields.get(inCode);
        }
    }
    /**
     * Get the code value.
     *
     * @return a <code>String</code> value
     */
    public String getCode()
    {
        return code;
    }
    /**
     * Create a new YahooField instance.
     *
     * @param inCode a <code>YahooField</code> value
     */
    private YahooField(String inCode)
    {
        code = inCode;
    }
    /**
     * literal value associated with the field
     */
    private final String code;
    /**
     * fields by literal value
     */
    private static final Map<String,YahooField> fields = new HashMap<String,YahooField>();
}
