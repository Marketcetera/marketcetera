package org.marketcetera.trade;

import java.util.EnumSet;
import java.util.Set;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Identifies the type of entity that originated the report.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public enum Originator
{
    /**
     * Indicates that the report was originated by the system's server.
     */
    Server,
    /**
     * Indicates that the report was originated by the FIX broker. 
     */
    Broker,
    /**
     * Indicates that the report is a child of parent orders
     */
    Child,
    /**
     * Indicates that the report is a parent of child orders
     */
    Parent;
    /**
     * Indicates that this originator is to be used for position calculations.
     *
     * @return a <code>boolean</code> value
     */
    public boolean showInPositions()
    {
        return POSITIONS.contains(this);
    }
    /**
     * Indicates that this originator is to be used for FIX Message views.
     *
     * @return a <code>boolean</code> value
     */
    public boolean showInFixMessageViews()
    {
        return FIX_MESSAGE_VIEWS.contains(this);
    }
    /**
     * originators for positions
     */
    private static final Set<Originator> POSITIONS = EnumSet.of(Broker,Parent);
    /**
     * originators for FIX Message Views
     */
    private static final Set<Originator> FIX_MESSAGE_VIEWS = EnumSet.of(Broker,Parent,Child);
}
