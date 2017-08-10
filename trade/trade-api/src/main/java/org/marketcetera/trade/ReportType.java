package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * The type of a persistent report. This class is part of the implementation
 * and is made public for persistence. It's not meant to be used by the
 * clients of this package.
 *
 * @author anshul@marketcetera.com
 * @version $Id: ReportType.java 16468 2014-05-12 00:36:56Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: ReportType.java 16468 2014-05-12 00:36:56Z colin $")
public enum ReportType {
    /**
     * Represents an execution report.
     *
     * @see org.marketcetera.trade.ExecutionReport
     */
    ExecutionReport,
    /**
     * Represents an order cancel reject report.
     *
     * @see org.marketcetera.trade.OrderCancelReject
     */
    CancelReject
}
