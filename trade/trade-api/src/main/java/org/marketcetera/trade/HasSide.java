package org.marketcetera.trade;

/* $License$ */

/**
 * Indicates that the implementor has a {@link Side} value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasSide
{
    /**
     * Get the side value.
     *
     * @return a <code>Side</code> value
     */
    Side getSide();
}
