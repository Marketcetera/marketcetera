package org.marketcetera.ors.history;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.security.User;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Side;

/* $License$ */
/**
 * Verifies {@link ReportHistoryServices#getCurrencyPositionAsOf(org.marketcetera.security.User, Date, Currency)}
 * & {@link ReportHistoryServices#getAllCurrencyPositionsAsOf(org.marketcetera.security.User, Date)}.
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
    protected BigDecimal getInstrumentPosition(Date inDate, Currency inInstrument, User inUser) throws Exception {
        return getPosition(inDate, inInstrument, inUser);
    }

    @Override
    protected Map<PositionKey<Currency>, BigDecimal> getInstrumentPositions(Date inDate) throws Exception {
        return getCurrencyPositions(inDate);
    }

    @Override
    protected Map<PositionKey<Currency>, BigDecimal> getInstrumentPositions(Date inAfter, User inUser) throws Exception {
        return getCurrencyPositions(inAfter, inUser);
    }
    

    private static final Currency CURRENCY_A = new Currency("USD","INR","","");
    private static final Currency CURRENCY_B = new Currency("GBP","JPY","","");
    private static final Currency TEST_CURRENCY = new Currency("USD","GBP","","");
}
