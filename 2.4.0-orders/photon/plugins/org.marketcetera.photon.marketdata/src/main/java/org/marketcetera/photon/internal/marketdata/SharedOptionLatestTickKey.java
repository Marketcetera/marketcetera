package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.trade.Equity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Key for latest tick data for an entire option chain that shares a common
 * underlying equity.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class SharedOptionLatestTickKey extends Key {

    /**
     * Constructor.
     * 
     * @param underlying
     *            the underlying equity
     */
    public SharedOptionLatestTickKey(final Equity underlying) {
        super(underlying);
    }
}