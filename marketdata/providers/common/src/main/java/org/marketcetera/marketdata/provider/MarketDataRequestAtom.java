package org.marketcetera.marketdata.provider;

import org.marketcetera.api.systemmodel.Instrument;
import org.marketcetera.marketdata.Content;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataRequestAtom
{
    public Instrument getInstrument();
    public Instrument getUnderlyingInstrument();
    public Content getContent();
}
