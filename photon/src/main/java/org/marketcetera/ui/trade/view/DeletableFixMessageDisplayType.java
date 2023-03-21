package org.marketcetera.ui.trade.view;

import org.marketcetera.trade.HasReportID;
import org.marketcetera.ui.trade.executionreport.view.FixMessageDisplayType;

/* $License$ */

/**
 * Provides access to <code>ReportID</code> values on <code>FixMessageDisplayType</code> values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface DeletableFixMessageDisplayType
        extends FixMessageDisplayType,HasReportID
{
}
