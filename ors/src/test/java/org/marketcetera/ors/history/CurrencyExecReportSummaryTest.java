package org.marketcetera.ors.history;

import org.marketcetera.trade.Currency;

/* $License$ */

/**
 * Tests exec report summary persistence for currency.
 *
 */
public class CurrencyExecReportSummaryTest
        extends ExecReportSummaryTestBase<Currency>
{
    /* (non-Javadoc)
     * @see org.marketcetera.ors.history.ExecReportSummaryTestBase#getInstrument()
     */
    @Override
    protected Currency getInstrument()
    {
        return new Currency("GBP/USD");
    }
}
