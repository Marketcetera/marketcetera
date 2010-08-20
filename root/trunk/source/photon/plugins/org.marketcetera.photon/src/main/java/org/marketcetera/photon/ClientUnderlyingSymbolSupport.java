package org.marketcetera.photon;

import org.marketcetera.client.Client;
import org.marketcetera.client.ClientManager;
import org.marketcetera.core.instruments.UnderlyingSymbolSupport;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Implementation that delegates to the current {@link Client} instance.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public final class ClientUnderlyingSymbolSupport implements
        UnderlyingSymbolSupport {
    @Override
    public String getUnderlying(Instrument instrument) {
        /*
         * For options, attempt to map the option root symbol to an underlying
         * (since in some symbology option root symbol is not the symbol of the
         * underlying instrument).
         */
        if (instrument instanceof Option) {
            try {
                String underlying = ClientManager.getInstance().getUnderlying(
                        instrument.getSymbol());
                if (underlying != null) {
                    return underlying;
                }
            } catch (Exception e) {
                PhotonPlugin.getMainConsoleLogger().error(
                        Messages.CLIENT_UNDERLYING_SYMBOL_SUPPORT_MAPPING_ERROR
                                .getText(), e);
            }
        } else if(instrument instanceof Future) {
            return ((Future)instrument).getUnderlying();
        }
        return instrument.getSymbol();
    }
}