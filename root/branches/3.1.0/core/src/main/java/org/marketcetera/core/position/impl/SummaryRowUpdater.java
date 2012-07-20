package org.marketcetera.core.position.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.Validate;
import org.marketcetera.core.position.PositionMetrics;
import org.marketcetera.core.position.PositionRow;
import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

/* $License$ */

/**
 * This class maintains a summary position row based on its child positions.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class SummaryRowUpdater {

    private final EventList<PositionRow> mChildren;
    private final PositionRowImpl mPositionRow;
    private final ListEventListener<PositionRow> listChangeListener;

    /**
     * Constructor.
     * 
     * @param summary
     *            the summary row to be updated, must have a child list
     * @throws IllegalArgumentException
     *             if summary is null or summary.getChildren() is null
     */
    public SummaryRowUpdater(PositionRowImpl summary) {
        Validate.notNull(summary);
        Validate.notNull(summary.getChildren());
        mChildren = summary.getChildren();
        mPositionRow = summary;
        listChangeListener = new ListEventListener<PositionRow>() {

            @Override
            public void listChanged(ListEvent<PositionRow> listChanges) {
                SummaryRowUpdater.this.listChanged(listChanges);
            }
        };
        recalculate();
        mChildren.addListEventListener(listChangeListener);
    }

    /**
     * Returns the dynamically updated summary row.
     * 
     * @return the dynamically updated summary row
     */
    public PositionRow getSummary() {
        return mPositionRow;
    }

    /**
     * Cleanup this object
     */
    public void dispose() {
        mChildren.removeListEventListener(listChangeListener);
    }

    private void listChanged(ListEvent<PositionRow> listChanges) {
        if (listChanges.getSourceList() != mChildren) {
            throw new IllegalStateException();
        }
        while (listChanges.next()) {
            final int changeIndex = listChanges.getIndex();
            final int changeType = listChanges.getType();
            if (changeType == ListEvent.INSERT) {
                // we can optimize on insert
                PositionRow row = mChildren.get(changeIndex);
                mPositionRow.setPositionMetrics(add(mPositionRow.getPositionMetrics(), row
                        .getPositionMetrics()));
            } else {
                // recompute summary from scratch
                recalculate();
            }
        }
    }

    private void recalculate() {
        PositionMetrics metrics = new PositionMetricsImpl(BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        for (PositionRow row : mChildren) {
            metrics = add(metrics, row.getPositionMetrics());
        }
        mPositionRow.setPositionMetrics(metrics);
    }

    private PositionMetrics add(PositionMetrics current, PositionMetrics augend) {
        BigDecimal incomingPosition = add(current.getIncomingPosition(), augend
                .getIncomingPosition());
        BigDecimal position = add(current.getPosition(), augend.getPosition());
        BigDecimal positionPL = add(current.getPositionPL(), augend.getPositionPL());
        BigDecimal tradingPL = add(current.getTradingPL(), augend.getTradingPL());
        BigDecimal realizedPL = add(current.getRealizedPL(), augend.getRealizedPL());
        BigDecimal unrealizedPL = add(current.getUnrealizedPL(), augend.getUnrealizedPL());
        BigDecimal totalPL = add(current.getTotalPL(), augend.getTotalPL());
        return new PositionMetricsImpl(incomingPosition, position, positionPL, tradingPL,
                realizedPL, unrealizedPL, totalPL);
    }

    private BigDecimal add(BigDecimal current, BigDecimal augend) {
        // if either is null (unknown), the result is also unknown
        return current == null || augend == null ? null : current.add(augend);
    }

}
