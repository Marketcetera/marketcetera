//
// this file is automatically generated
//
package org.marketcetera.trade.pnl;

/* $License$ */

/**
 * Describes position of a given instrument owned by a given user at a given point in time.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasPosition
{
    /**
     * Get the position value.
     *
     * @return a <code>Position</code> value
     */
    Position getPosition();
    /**
     * Set the position value.
     *
     * @param inPosition a <code>Position</code> value
     */
    void setPosition(Position inPosition);
}
