package org.marketcetera.trade.event.connector;

import org.marketcetera.trade.HasMutableReportID;
import org.marketcetera.trade.Report;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.event.TradeMessagePackage;
import org.marketcetera.trade.service.Messages;
import org.marketcetera.trade.service.ReportService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.Subscribe;

/* $License$ */

/**
 * Persists incoming trade messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class IncomingTradeMessagePersistenceConnector
        extends AbstractTradeConnector
{
    /**
     * Receive the incoming FIX application message.
     *
     * @param inEvent an <code>IncomingFixAppMessageEvent</code> value
     */
    @Subscribe
    public void receive(TradeMessagePackage inEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "Received {}",
                               inEvent);
        TradeMessage tradeMessage = inEvent.getTradeMessage();
        // TODO under what circumstances would this not be true?
        if(tradeMessage instanceof ReportBase) {
            ReportBase inReport = (ReportBase)tradeMessage;
            if(inReport instanceof HasMutableReportID) {
                reportService.assignReportId((HasMutableReportID)inReport);
            } else {
                SLF4JLoggerProxy.warn(this,
                                      "Not assigning report ID to {}",
                                      tradeMessage);
            }
            Report persistedReport = reportService.save(inReport);
            Messages.PERSISTED_REPLY.debug(this,
                                           persistedReport);
            eventBusService.post(tradeMessage);
        }
    }
    /**
     * provides access to broker services
     */
    @Autowired
    private ReportService reportService;
}
