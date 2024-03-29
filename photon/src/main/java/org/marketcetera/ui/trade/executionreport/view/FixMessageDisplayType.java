package org.marketcetera.ui.trade.executionreport.view;

import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.trade.ExecutionReportSummary;
import org.marketcetera.trade.HasOrderId;
import org.marketcetera.trade.HasOrderStatus;
import org.marketcetera.trade.Report;

import javafx.beans.property.BooleanProperty;

/* $License$ */

/**
 * Provides an interface to support UI views for {@link ExecutionReportSummary} and {@link Report} values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FixMessageDisplayType
        extends HasFIXMessage,HasOrderId,HasOrderStatus
{
    /**
     * Determine if the display message is a recent fill.
     *
     * @return a <code>BooleanProperty</code> value indicating if the display message is a recent fill
     */
    BooleanProperty isFillProperty();
    /**
     * Determine if the display message is a recent cancel.
     *
     * @return a <code>BooleanProperty</code> value indicating if the display message is a recent cancel
     */
    BooleanProperty isCancelProperty();
}
