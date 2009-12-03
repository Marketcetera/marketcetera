package org.marketcetera.orderloader.system;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.SecurityType;
import org.marketcetera.orderloader.OrderParsingException;

/* $License$ */
/**
 * Implements extraction of an equity instrument from a row.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class EquityFromRow extends InstrumentFromRow {

    @Override
    protected boolean canProcess(String inHeader, int inIndex) {
        //no extra headers need to be processed for equity.
        return false;
    }

    @Override
    protected Instrument extract(Row inRow) throws OrderParsingException {
        //Fetch the security type to catch any security type parsing errors
        getSecurityType(inRow.getRow());
        String symbol = getSymbol(inRow.getRow());
        return symbol != null && (!symbol.trim().isEmpty())
                ? new Equity(symbol)
                : null;
    }

    @Override
    protected boolean isHandled(Row inValue) {
        SecurityType secType = null;
        try {
            secType = getSecurityType(inValue.getRow());
        } catch (OrderParsingException ignore) {
        }
        return secType == null || SecurityType.CommonStock.equals(secType);
    }
}
