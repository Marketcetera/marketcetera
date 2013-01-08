package org.marketcetera.ors.history;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.marketcetera.core.position.PositionKey;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.trade.Currency;

/* $License$ */
/**
 * Verifies {@link ReportHistoryServices#getCurrencyPositionAsOf(org.marketcetera.ors.security.SimpleUser, Date, Currency)}
 * & {@link ReportHistoryServices#getAllCurrencyPositionsAsOf(org.marketcetera.ors.security.SimpleUser, Date)}.
 *
 */
public class CurrencyPositionsTest extends PositionsTestBase<Currency> {

    @Override
    protected Currency getInstrument() {
        return TEST_CURRENCY;
    }

    @Override
    protected Currency getInstrumentA() {
        return CURRENCY_A;
    }

    @Override
    protected Currency getInstrumentB() {
        return CURRENCY_B;
    }

    @Override
    protected BigDecimal getInstrumentPosition(Date inDate, Currency inCurrency) throws Exception {
        return getPosition(inDate, inCurrency);
    }

    @Override
    protected BigDecimal getInstrumentPosition(Date inDate, Currency inInstrument, SimpleUser inUser) throws Exception {
        return getPosition(inDate, inInstrument, inUser);
    }

    @Override
    protected Map<PositionKey<Currency>, BigDecimal> getInstrumentPositions(Date inDate) throws Exception {
        return getCurrencyPositions(inDate);
    }

    @Override
    protected Map<PositionKey<Currency>, BigDecimal> getInstrumentPositions(Date inAfter, SimpleUser inUser) throws Exception {
        return getCurrencyPositions(inAfter, inUser);
    }
    

    private static final Currency CURRENCY_A = new Currency("USD","INR","","");
    private static final Currency CURRENCY_B = new Currency("GBP","JPY","","");
    private static final Currency TEST_CURRENCY = new Currency("USD","GBP","","");
}
