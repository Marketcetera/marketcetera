package org.marketcetera.marketdata.request;

import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.core.trade.Instrument;
import org.marketcetera.marketdata.Content;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement
public interface MarketDataRequestAtom
{
    public Instrument getInstrument();
    public Instrument getUnderlyingInstrument();
    public Content getContent();
}
