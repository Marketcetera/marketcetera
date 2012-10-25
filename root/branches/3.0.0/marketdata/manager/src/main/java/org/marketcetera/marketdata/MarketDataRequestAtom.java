package org.marketcetera.marketdata;

import java.util.Set;

import org.marketcetera.api.systemmodel.Instrument;

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
    public Content getContent();
    public Set<Capability> getRequiredCapabilities();
    public String getProvider();
    public String getExchange();
}
