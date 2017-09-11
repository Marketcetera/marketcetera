package org.marketcetera.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;
import org.marketcetera.admin.User;
import org.marketcetera.brokers.Broker;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.module.HasMutableStatus;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TradeMessage;

import quickfix.Message;

/* $License$ */

/**
 * Tests data flows through the application.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DataFlowTest
        extends IntegrationTestBase
{
    /**
     * Tests adding a report.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAddReport()
            throws Exception
    {
        Broker initiatorBroker = getInitiator();
        User user = getTraderUser();
        CalculatedOrderData order1Data = new CalculatedOrderData(new BigDecimal(10000),
                                                                 new BigDecimal(100),
                                                                 OrderType.Limit,
                                                                 Side.Buy);
        quickfix.Message order1 = order1Data.generateOrder(new Equity("METC"),
                                                           initiatorBroker.getSessionId());
        FIXVersion fixVersion = FIXVersion.getFIXVersion(order1);
        quickfix.Message message = generateExecutionReport(order1,
                                                           order1Data,
                                                           PlatformServices.generateId(),
                                                           OrderStatus.New,
                                                           ExecutionType.New,
                                                           fixVersion.getMessageFactory());
        ReportWrapper reportWrapper = new ReportWrapper(message);
        reportService.addReport(reportWrapper,
                                initiatorBroker.getBrokerId(),
                                user.getUserID());
        assertFalse(reportWrapper.getFailed());
    }
    /**
     * Test that a report can be deleted.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDeleteReport()
            throws Exception
    {
        Broker initiatorBroker = getInitiator();
        User user = getTraderUser();
        CalculatedOrderData order1Data = new CalculatedOrderData(new BigDecimal(10000),
                                                                 new BigDecimal(100),
                                                                 OrderType.Limit,
                                                                 Side.Buy);
        quickfix.Message order1 = order1Data.generateOrder(new Equity("METC"),
                                                           initiatorBroker.getSessionId());
        FIXVersion fixVersion = FIXVersion.getFIXVersion(order1);
        quickfix.Message message = generateExecutionReport(order1,
                                                           order1Data,
                                                           PlatformServices.generateId(),
                                                           OrderStatus.New,
                                                           ExecutionType.New,
                                                           fixVersion.getMessageFactory());
        ReportWrapper reportWrapper = new ReportWrapper(message);
        reportService.addReport(reportWrapper,
                                initiatorBroker.getBrokerId(),
                                user.getUserID());
        TradeMessage tradeMessage = waitForNextTradeMessage();
        assertTrue(tradeMessage instanceof ExecutionReport);
        ExecutionReport executionReport = (ExecutionReport)tradeMessage;
        assertNotNull(reportService.getReportFor(executionReport.getReportID()));
        assertNotNull(orderSummaryService.findByRootOrderIdAndOrderId(executionReport.getOrderID(),
                                                                      executionReport.getOrderID()));
        reportService.delete(executionReport.getReportID());
        assertNull(reportService.getReportFor(executionReport.getReportID()));
        assertNull(orderSummaryService.findByRootOrderIdAndOrderId(executionReport.getOrderID(),
                                                                   executionReport.getOrderID()));
    }
    /**
     * Wraps the report with a status holder.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    protected static class ReportWrapper
            implements HasFIXMessage,HasMutableStatus
    {
        /* (non-Javadoc)
         * @see org.marketcetera.module.HasStatus#getFailed()
         */
        @Override
        public boolean getFailed()
        {
            return failed;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.module.HasStatus#getErrorMessage()
         */
        @Override
        public String getErrorMessage()
        {
            return errorMessage;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.module.HasMutableStatus#setErrorMessage(java.lang.String)
         */
        @Override
        public void setErrorMessage(String inMessage)
        {
            errorMessage = inMessage;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.module.HasMutableStatus#setFailed(boolean)
         */
        @Override
        public void setFailed(boolean inFailed)
        {
            failed = inFailed;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.event.HasFIXMessage#getMessage()
         */
        @Override
        public Message getMessage()
        {
            return hasFixMessage;
        }
        /**
         * Create a new ReportWrapper instance.
         *
         * @param inMessage a <code>quickfix.Message</code> value
         */
        private ReportWrapper(quickfix.Message inMessage)
        {
            hasFixMessage = inMessage;
        }
        /**
         * fix message
         */
        private final quickfix.Message hasFixMessage;
        /**
         * failed value
         */
        private boolean failed;
        /**
         * error message value
         */
        private String errorMessage;
    }
}
