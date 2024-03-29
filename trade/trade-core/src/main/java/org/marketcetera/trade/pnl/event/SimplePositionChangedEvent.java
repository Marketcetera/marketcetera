//
// this file is automatically generated
//
package org.marketcetera.trade.pnl.event;

/* $License$ */

/**
 * Indicates that the position of an instrument has changed for a user.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimplePositionChangedEvent
        implements PositionChangedEvent
{
    /**
     * Create a new SimplePositionChangedEvent instance.
     */
    public SimplePositionChangedEvent() {}
    /**
     * Get the position value.
     *
     * @return a <code>org.marketcetera.trade.pnl.SimplePosition</code> value
     */
    @Override
    public org.marketcetera.trade.pnl.SimplePosition getPosition()
    {
        return position;
    }
    /**
     * Set the position value.
     *
     * @param inPosition a <code>org.marketcetera.trade.pnl.SimplePosition</code> value
     */
    public void setPosition(org.marketcetera.trade.pnl.SimplePosition inPosition)
    {
        position = inPosition;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("PositionChangedEvent [")
            .append("position=").append(position).append("]");
        return builder.toString();
    }
    /**
     * position which changed
     */
    private org.marketcetera.trade.pnl.SimplePosition position;
}
