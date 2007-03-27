package org.marketcetera.oms;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraException;

import java.math.BigDecimal;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class OrderLimitException extends MarketceteraException {
    public OrderLimitException(String localizedMsg) {
        super(localizedMsg);
    }

    public static OrderLimitException createMaxQuantityException(BigDecimal over, BigDecimal limit, String symbol)
    {
        return new OrderLimitException(OMSMessageKey.ERROR_OL_MAX_QTY.getLocalizedMessage(over.toPlainString(),
                limit.toPlainString(), symbol));
    }

    public static OrderLimitException createMaxNotionalException(BigDecimal over, BigDecimal limit, String symbol)
    {
        return new OrderLimitException(OMSMessageKey.ERROR_OL_MAX_NOTIONAL.getLocalizedMessage(over.toPlainString(),
                limit.toPlainString(), symbol));
    }
    public static OrderLimitException createMaxPriceException(BigDecimal over, BigDecimal limit, String symbol)
    {
        return new OrderLimitException(OMSMessageKey.ERROR_OL_MAX_PRICE.getLocalizedMessage(over.toPlainString(),
                limit.toPlainString(), symbol));
    }
    public static OrderLimitException createMinPriceException(BigDecimal over, BigDecimal limit, String symbol)
    {
        return new OrderLimitException(OMSMessageKey.ERROR_OL_MIN_PRICE.getLocalizedMessage(over.toPlainString(),
                limit.toPlainString(), symbol));
    }
    public static OrderLimitException createMarketOrderWithPriceException(String symbol)
    {
        return new OrderLimitException(OMSMessageKey.ERROR_OL_MARKET_NOT_ALLOWED_PRICE.getLocalizedMessage(symbol));
    }
    public static OrderLimitException createMarketOrderException(String symbol)
    {
        return new OrderLimitException(OMSMessageKey.ERROR_OL_MARKET_NOT_ALLOWED.getLocalizedMessage(symbol));
    }
}
