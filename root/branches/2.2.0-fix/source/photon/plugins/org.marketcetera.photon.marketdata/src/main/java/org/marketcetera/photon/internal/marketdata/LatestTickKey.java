package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Key for latest tick data.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class LatestTickKey extends Key {

    /**
     * Constructor.
     * 
     * @param instrument
     *            the instrument
     */
    public LatestTickKey(final Instrument instrument) {
        super(instrument);
    }
}