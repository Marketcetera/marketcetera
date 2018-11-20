package org.marketcetera.trade;

import org.marketcetera.admin.User;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.FieldNotFound;
import quickfix.Message;

/* $License$ */

/**
 * Creates {@link SimpleReport} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleReportFactory
        implements MutableReportFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReportFactory#create()
     */
    @Override
    public SimpleReport create()
    {
        return new SimpleReport();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReportFactory#create(org.marketcetera.trade.TradeMessage, org.marketcetera.admin.User)
     */
    @Override
    public MutableReport create(TradeMessage inTradeMessage,
                                User inUser)
    {
        SimpleReport simpleReport = new SimpleReport();
        simpleReport.setActor(inUser);
        if(inTradeMessage instanceof HasBrokerID) {
            simpleReport.setBrokerID(((HasBrokerID)inTradeMessage).getBrokerID());
        }
        Message fixMessage = null;
        if(inTradeMessage instanceof HasFIXMessage) {
            fixMessage = ((HasFIXMessage)inTradeMessage).getMessage();
            simpleReport.setFixMessage(fixMessage.toString());
            try {
                simpleReport.setMsgSeqNum(fixMessage.getHeader().getInt(quickfix.field.MsgSeqNum.FIELD));
            } catch (FieldNotFound e) {
                PlatformServices.handleException(this,
                                                 "No MsgSeqNum",
                                                 e);
            }
            try {
                simpleReport.setSessionId(FIXMessageUtil.getSessionId(fixMessage));
            } catch (FieldNotFound e) {
                PlatformServices.handleException(this,
                                                 "No SessionId",
                                                 e);
            }
        }
        if(inTradeMessage instanceof ReportBase) {
            ReportBase reportBase = (ReportBase)inTradeMessage;
            simpleReport.setHierarchy(reportBase.getHierarchy());
            simpleReport.setOrderID(reportBase.getOrderID());
            simpleReport.setOriginator(reportBase.getOriginator());
            simpleReport.setReportID(reportBase.getReportID());
            simpleReport.setSendingTime(reportBase.getSendingTime());
        }
        if(inTradeMessage instanceof ExecutionReport) {
            simpleReport.setReportType(ReportType.ExecutionReport);
        } else if(inTradeMessage instanceof OrderCancelReject) {
            simpleReport.setReportType(ReportType.CancelReject);
        }
        return simpleReport;
    }
}
