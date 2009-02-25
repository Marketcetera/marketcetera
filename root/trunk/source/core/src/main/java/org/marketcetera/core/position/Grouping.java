package org.marketcetera.core.position;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The key elements available for grouping.
 * 
 * @see SummaryPositionRow
 * @see PositionEngine#getGroupedData(Grouping...)
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public enum Grouping {
    Symbol, Account, Trader
};
