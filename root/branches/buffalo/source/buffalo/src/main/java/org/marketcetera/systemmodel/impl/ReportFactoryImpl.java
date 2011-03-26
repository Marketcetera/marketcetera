package org.marketcetera.systemmodel.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.ors.history.InstrumentSummaryFields;
import org.marketcetera.ors.history.ReportType;
import org.marketcetera.server.service.OrderManager;
import org.marketcetera.server.service.UserManager;
import org.marketcetera.systemmodel.*;
import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
class ReportFactoryImpl
        implements ReportFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.ReportFactory#createFrom(org.marketcetera.trade.TradeMessage)
     */
    @Override
    public Report createFrom(TradeMessage inTradeMessage)
    {
        if(inTradeMessage instanceof ExecutionReport) {
            ExecutionReport executionReport = (ExecutionReport)inTradeMessage;
            OrderID orderID = executionReport.getOrderID();
            User owner = null;
            Order order = orderManager.getBy(orderID);
            if(order instanceof HasOwner) {
                owner = ((HasOwner)order).getOwner();
            } else {
                if(executionReport.getActorID() != null) {
                    owner = userManager.getById(executionReport.getActorID().getValue());
                }
            }
            OrderDestinationID destinationID = orderDestinationIdFactory.create(executionReport.getBrokerID().getValue());
            String rawMessage = null;
            if(inTradeMessage instanceof HasFIXMessage) {
                HasFIXMessage hasFixMessage = (HasFIXMessage)inTradeMessage;
                rawMessage = hasFixMessage.getMessage().toString();
            }
            Date sendingTime = executionReport.getSendingTime();
            ReportType reportType = null;
            if(inTradeMessage instanceof ExecutionReport) {
                reportType = ReportType.ExecutionReport;
            } else if(inTradeMessage instanceof OrderCancelReject) {
                reportType = ReportType.CancelReject;
            } else {
                // you added new report types but forgot to update the code to persist them
                throw new IllegalArgumentException();
            }
            // these components are necessary for the summary
            OrderID origOrderId = executionReport.getOriginalOrderID();
            SecurityType securityType = null;
            String symbol = null;
            String expiry = null;
            BigDecimal strikePrice = null;
            OptionType optionType = null;
            Instrument instrument = executionReport.getInstrument();
            if (instrument != null) {
                securityType = instrument.getSecurityType();
                symbol = instrument.getSymbol();
                InstrumentSummaryFields<?> summaryFields = InstrumentSummaryFields.SELECTOR.forInstrument(instrument);
                optionType = summaryFields.getOptionType(instrument);
                strikePrice = summaryFields.getStrikePrice(instrument);
                expiry = summaryFields.getExpiry(instrument);
            }
            String account = executionReport.getAccount();
            Side side = executionReport.getSide();
            BigDecimal cumQty = executionReport.getCumulativeQuantity();
            BigDecimal avgPrice = executionReport.getAveragePrice();
            BigDecimal lastQty = executionReport.getLastQuantity();
            BigDecimal lastPrice = executionReport.getLastPrice();
            OrderStatus orderStatus = executionReport.getOrderStatus();
            ReportSummary summary = new ReportSummaryImpl(orderID,
                                                          origOrderId,
                                                          securityType,
                                                          symbol,
                                                          expiry,
                                                          strikePrice,
                                                          optionType,
                                                          account,
                                                          side,
                                                          cumQty,
                                                          avgPrice,
                                                          lastQty,
                                                          lastPrice,
                                                          orderStatus,
                                                          sendingTime,
                                                          owner);
            Report report = new ReportImpl(orderID,
                                           owner,
                                           destinationID,
                                           rawMessage,
                                           sendingTime,
                                           reportType,
                                           summary);
            return report;
        }
        throw new CannotCreateReport("Cannot create a Report from " + inTradeMessage);
    }
    /**
     * 
     */
    @Autowired
    private OrderDestinationIdFactory orderDestinationIdFactory;
    /**
     * 
     */
    @Autowired
    private UserManager userManager;
    /**
     * 
     */
    @Autowired
    private OrderManager orderManager;
}
