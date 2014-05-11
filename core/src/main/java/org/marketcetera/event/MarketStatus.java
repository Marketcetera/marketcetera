package org.marketcetera.event;

import java.util.Map;

import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.Maps;

/* $License$ */

/**
 * Indicates the market status.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public enum MarketStatus
{
    OPEN_REGULAR(1),
    OPEN_AUCTION(65),
    OPEN_FAST(81),
    OPEN_SLOW(97),
    CLOSED_REGULAR(2),
    CLOSED_PRE_MARKET(66),
    CLOSED_POST_MARKET(82),
    CLOSED_AUCTION(98),
    CLOSED_IMBALANCE(114),
    CLOSED_ROTATION(130),
    HALTED_REGULAR(3),
    HALTED_QUOTING_ALLOWED(67),
    HALTED_REGULATORY(83),
    HALTED_QUOTING_ALLOWED_REGULATORY(99),
    ACTIVE_REGULAR(4),
    ACTIVE_CLOSING_ORDERS_ONLY(68),
    INACTIVE_REGULAR(5),
    INACTIVE_SUSPENDED(69),
    INACTIVE_DELISTED(85),
    INACTIVE_EXPIRED(101),
    AUCTION_REGULAR(6),
    AUCTION_OPENING(70),
    AUCTION_CALL_OPENING(86),
    AUCTION_CLOSING(102),
    AUCTION_CALL_CLOSING(118),
    AUCTION_INTRADAY(134),
    AUCTION_VOLATILITY(150),
    AUCTION_CALL_INTRADAY(166),
    AUCTION_CALL_VOLATILITY(182),
    AUCTION(6),
    INVALID(0),
    HALTED(3),
    INACTIVE(5),
    CLOSED(2),
    ACTIVE(4),
    OPEN(1);
    /**
     * Get the code value.
     *
     * @return an <code>int</code> value
     */
    public int getCode()
    {
        return code;
    }
    /**
     * Gets the <code>MarketStatus</code> for the given code.
     *
     * @param inCode an <code>int</code> value
     * @return an <code>MarketStatus</code> value
     * @throws IllegalArgumentException if the code does not correspond to an <code>MarketStatus</code>
     */
    public static MarketStatus getFor(int inCode)
    {
        MarketStatus type = typesByCode.get(inCode);
        if(type == null) {
            throw new IllegalArgumentException();
        }
        return type;
    }
    /**
     * Create a new MarketStatus instance.
     *
     * @param inCode an <code>int</code> value
     */
    private MarketStatus(int inCode)
    {
        code = inCode;
    }
    /**
     * code value
     */
    private final int code;
    /**
     * all types by their code value
     */
    private static final Map<Integer,MarketStatus> typesByCode = Maps.newHashMap();
    /**
     * performs static initialization
     */
    static {
        for(MarketStatus type : MarketStatus.values()) {
            typesByCode.put(type.getCode(),
                            type);
        }
    }
}
