package org.marketcetera.ors;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage3P;

import java.math.BigDecimal;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$") //$NON-NLS-1$
public class OrderLimitException extends CoreException {

    private static final long serialVersionUID=1L;

    public OrderLimitException(Throwable nested) {
        super(nested);
    }

    public OrderLimitException(I18NBoundMessage message) {
        super(message);
    }

    public OrderLimitException(Throwable nested, I18NBoundMessage message) {
        super(nested, message);
    }

    public static OrderLimitException createMaxQuantityException(BigDecimal over, BigDecimal limit, String symbol)
    {
        return new OrderLimitException(new I18NBoundMessage3P(Messages.ERROR_OL_MAX_QTY,
                                                              over.toPlainString(),  //i18n_number
                                                              limit.toPlainString(),  //i18n_number
                                                              symbol));
    }

    public static OrderLimitException createMaxNotionalException(BigDecimal over, BigDecimal limit, String symbol)
    {
        return new OrderLimitException(new I18NBoundMessage3P(Messages.ERROR_OL_MAX_NOTIONAL,
                                                              over.toPlainString(), //i18n_currency
                                                              limit.toPlainString(), //i18n_currency
                                                              symbol));
    }
    public static OrderLimitException createMaxPriceException(BigDecimal over, BigDecimal limit, String symbol)
    {
        return new OrderLimitException(new I18NBoundMessage3P(Messages.ERROR_OL_MAX_PRICE,
                                                              over.toPlainString(), //i18n_currency
                                                              limit.toPlainString(), //i18n_currency
                                                              symbol));
    }
    public static OrderLimitException createMinPriceException(BigDecimal over, BigDecimal limit, String symbol)
    {
        return new OrderLimitException(new I18NBoundMessage3P(Messages.ERROR_OL_MIN_PRICE,
                                                              over.toPlainString(), //i18n_currency
                                                              limit.toPlainString(), //i18n_currency
                                                              symbol));
    }
    public static OrderLimitException createMarketOrderWithPriceException(String symbol)
    {
        return new OrderLimitException(new I18NBoundMessage1P(Messages.ERROR_OL_MARKET_NOT_ALLOWED_PRICE,
                                                              symbol));
    }
    public static OrderLimitException createMarketOrderException(String symbol)
    {
        return new OrderLimitException(new I18NBoundMessage1P(Messages.ERROR_OL_MARKET_NOT_ALLOWED,
                                                              symbol));
    }
}
