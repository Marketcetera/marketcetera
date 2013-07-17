package org.marketcetera.ors.history;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;

/* $License$ */
/**
 * Tests exec report summary persistence for currency.
 *
 */
@ClassVersion("$Id$")
public class CurrencyExecReportSummaryTest extends ExecReportSummaryTestBase<Currency> {
    @Override
    protected Currency getInstrument() {
        return new Currency("GBP/USD");
    }
}
