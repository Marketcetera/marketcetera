package org.marketcetera.marketdata.yahoo;

import java.util.HashMap;
import java.util.Map;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
enum YahooField
{
    ASK("a"),
    AVERAGE_DAILY_VOLUME("a2"),
    ASK_SIZE("a5"),
    BID("b"),
    REAL_TIME_ASK("b2"),
    REAL_TIME_BID("b3"),
    BOOK_VALUE("b4"),
    BID_SIZE("b6"),
    CHANGE_AND_PERCENT_CHANGE("c"),
    CHANGE("c1"),
    COMMISSION("c3"),
    REAL_TIME_CHANGE("c6"),
    REAL_TIME_AFTER_HOURS_CHANGE("c8"),
    DIVIDEND_PER_SHARE("d"),
    LAST_TRADE_DATE("d1"),
    TRADE_DATE("d2"),
    EARNINGS_PER_SHARE("e"),
    ERROR_INDICATION("e1"),
    EPS_ESTIMATE_CURRENT_YEAR("e7"),
    EPS_ESTIMATE_NEXT_YEAR("e8"),
    EPS_ESTIMATE_NEXT_QUARTER("e9"),
    FLOAT_SHARES("f6"),
    DAY_LOW("g"),
    DAY_HIGH("h"),
    YEAR_LOW("j"),
    YEAR_HIGH("k"),
    HOLDINGS_GAIN_PERCENT("g1"),
    ANNUALIZED_GAIN("g3"),
    HOLDINGS_GAIN("g4"),
    REAL_TIME_HOLDINGS_GAIN_PERCENT("g5"),
    REAL_TIME_HOLDINGS_GAIN("g6"),
    MORE_INFO("i"),
    REAL_TIME_ORDER_BOOK("i5"),
    MARKET_CAPITALIZATION("j1"),
    REAL_TIME_MARKET_CAPITALIZATION("j3"),
    EBITDA("j4"),
    CHANGE_FROM_YEAR_LOW("j5"),
    PERCENT_CHANGE_FROM_YEAR_LOW("j6"),
    REAL_TIME_LAST_TRADE_WITH_TIME("k1"),
    REAL_TIME_CHANGE_PERCENT("k2"),
    LAST_TRADE_SIZE("k3"),
    CHANGE_FROM_YEAR_HIGH("k4"),
    PERCENT_CHANGE_FROM_YEAR_HIGH("k5"),
    LAST_TRADE_WITH_TIME("l"),
    LAST_TRADE_PRICE_ONLY("l1"),
    HIGH_LIMIT("l2"),
    LOW_LIMIT("l3"),
    DAY_RANGE("m"),
    REAL_TIME_DAY_RANGE("m2"),
    MOVING_AVERAGE_50_DAY("m3"),
    MOVING_AVERAGE_200_DAY("m4"),
    CHANGE_FROM_MOVING_AVERAGE_200_DAY("m5"),
    PERCENT_CHANGE_FROM_MOVING_AVERAGE_200_DAY("m6"),
    CHANGE_FROM_MOVING_AVERAGE_50_DAY("m7"),
    PERCENT_CHANGE_FROM_MOVING_AVERAGE_50_DAY("m8"),
    NAME("n"),
    NOTES("n4"),
    OPEN("o"),
    PREVIOUS_CLOSE("p"),
    PRICE_PAID("p1"),
    PERCENT_CHANGE("p2"),
    PRICE_OVER_SALES("p5"),
    PRICE_OVER_BOOK("p6"),
    EXPECTED_DIVIDEND_DATE("q"),
    PE_RATIO("r"),
    DIVIDEND_PAY_DATE("r1"),
    REAL_TIME_PE_RATION("r2"),
    PEG_RATION("r5"),
    PRICE_OVER_EPS_ESTIMATE_CURRENT_YEAR("r6"),
    PRICE_OVER_EPS_ESTIMATE_NEXT_YEAR("r7"),
    SYMBOL("s"),
    SHARES_OWNED("s1"),
    SHORT_RATIO("s7"),
    LAST_TRADE_TIME("t1"),
    TRADE_LINKS("t6"),
    TICKER_TREND("t7"),
    TARGET_PRICE_1_YEAR("t8"),
    VOLUME("v"),
    HOLDINGS_VALUE("v1"),
    REAL_TIME_HOLDINGS_VALUE("v7"),
    RANGE_52_WEEK("w"),
    DAY_VALUE_CHANGE("w1"),
    REAL_TIME_DAY_VALUE_CHANGE("w4"),
    STOCK_EXCHANGE("x"),
    DIVIDEND_YIELD("y");
    /**
     * 
     *
     *
     * @param inCode
     * @return
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
     * 
     */
    private static final Map<String,YahooField> fields = new HashMap<String,YahooField>();
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
     * @param inCode
     */
    private YahooField(String inCode)
    {
        code = inCode;
    }
    /**
     * 
     */
    private final String code;
}