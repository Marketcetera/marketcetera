package org.marketcetera.ors.history;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.Equity;

/* $License$ */
/**
 * Tests exec report summary persistence for equity.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class EquityExecReportSummaryTest extends ExecReportSummaryTestBase<Equity> {
    @Override
    protected Equity getInstrument() {
        return new Equity("sym");
    }
}
