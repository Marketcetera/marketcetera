package org.marketcetera.trade.modules;

import org.marketcetera.module.AbstractDataReemitterModule;
import org.marketcetera.module.AutowiredModule;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ReceiveDataException;
import org.marketcetera.trade.HasMutableReportID;
import org.marketcetera.trade.HasTradeMessage;
import org.marketcetera.trade.Report;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.service.ReportService;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Persists incoming messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@AutowiredModule
public class TradeMessagePersistenceModule
        extends AbstractDataReemitterModule
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.AbstractDataReemitterModule#onReceiveData(java.lang.Object, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    protected HasTradeMessage onReceiveData(Object inData,
                                            DataEmitterSupport inDataSupport)
    {
        if(!(inData instanceof HasTradeMessage)) {
            throw new ReceiveDataException(new I18NBoundMessage2P(org.marketcetera.module.Messages.WRONG_DATA_TYPE,
                                                                  HasTradeMessage.class.getSimpleName(),
                                                                  inData.getClass().getSimpleName()));
        }
        TradeMessage tradeMessage = ((HasTradeMessage)inData).getTradeMessage();
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
            // TODO need to make the return value of this call be set in the HasTradeMessage that gets emitted somehow
            //  we can just punt and make the return type of this message be "HasReport"?
            Report persistedReport = reportService.save(inReport);
            Messages.PERSISTED_REPLY.debug(this,
                                           persistedReport);
        }
        return (HasTradeMessage)inData;
    }
    /**
     * Create a new TradeMessagePersistenceModule instance.
     *
     * @param inURN a <code>ModuleURN</code> value
     */
    TradeMessagePersistenceModule(ModuleURN inURN)
    {
        super(inURN,
              true);
    }
    /**
     * provides access to report services
     */
    @Autowired
    private ReportService reportService;
}
