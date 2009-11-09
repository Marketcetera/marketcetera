package org.marketcetera.photon;

import org.marketcetera.client.Client;
import org.marketcetera.client.ClientManager;
import org.marketcetera.core.instruments.UnderlyingSymbolSupport;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Implementation that delegates to the current {@link Client} instance. 
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class ClientUnderlyingSymbolSupport implements
        UnderlyingSymbolSupport {
    @Override
    public String getUnderlying(Instrument instrument) {
        if (instrument instanceof Option) {
            try {
                return ClientManager.getInstance().getUnderlying(
                        instrument.getSymbol());
            } catch (Exception e) {
                /*
                 * Unexpected exception, fall through to instrument.getSymbol().
                 */
                SLF4JLoggerProxy.error(this, e);
            }
        }
        return instrument.getSymbol();
    }
}