package org.marketcetera.core.position;

import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.EventList;

/* $License$ */

/**
 * Represents a summary row of a group of positions. Its P&L is the simple sum
 * of the P&L values of its children.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface SummaryPositionRow extends PositionRowBase {

    /**
     * Returns the type of grouping of child positions that created this
     * summary. For example, a return value of <code>Account</code> indicates
     * that all children positions being summarized are associated with the same
     * account.
     * 
     * @return the type of grouping that created this summary, should never be
     *         null
     */
    Grouping getGroupingType();

    /**
     * Returns the value used in the grouping for this particular summary. For
     * example, if the grouping type is <code>Account</code> and the grouping
     * value is "XYZ", this indicates that all children positions being
     * summarized are associated with the account "XYZ".
     * 
     * The returned value can be null if this represents the "unknown" group,
     * that is the group that is collecting all positions that have a null value
     * for the grouping type.
     * 
     * @return the grouping value that created this summary, may be null
     */
    String getGroupingValue();

    /**
     * Returns the child positions (or sub-grouping summary positions) that this
     * position is summarizing.
     * 
     * @return the child positions, should never be null
     */
    EventList<PositionRowBase> getChildren();
}
