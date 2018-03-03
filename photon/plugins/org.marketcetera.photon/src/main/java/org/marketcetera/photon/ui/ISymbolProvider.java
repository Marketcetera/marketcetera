package org.marketcetera.photon.ui;

import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 * Interface for objects that provide an {@link Instrument} symbol.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public interface ISymbolProvider
{
    /**
     * Get the instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    Instrument getInstrument();
}
