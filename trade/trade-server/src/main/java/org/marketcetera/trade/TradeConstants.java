package org.marketcetera.trade;

/* $License$ */

/**
 * Provides some system-wide constants for Trade.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TradeConstants
{
    /**
     * the system-wide name of the outgoing data flow
     */
    static String outgoingDataFlowName = "outgoingDataFlow";
    /**
     * the system-wide name of the report-injection data flow
     */
    static String reportInjectionDataFlowName = "reportInjectionDataFlow";
    /**
     * the system-wide name of the incoming data flow
     */
    static String incomingDataFlowName = "incomingDataFlow";
}
