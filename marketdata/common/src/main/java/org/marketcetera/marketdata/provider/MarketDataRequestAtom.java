package org.marketcetera.marketdata.provider;

import org.marketcetera.core.trade.Instrument;
import org.marketcetera.marketdata.Content;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataRequestAtom.java 16327 2012-10-26 21:14:08Z colin $
 * @since $Release$
 */
public interface MarketDataRequestAtom
{
    public Instrument getInstrument();
    public Instrument getUnderlyingInstrument();
    public Content getContent();
}
