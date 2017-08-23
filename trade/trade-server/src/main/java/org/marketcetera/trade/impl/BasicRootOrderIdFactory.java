package org.marketcetera.trade.impl;

import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.RootOrderIdFactory;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.dao.ExecutionReportDao;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.OrigClOrdID;

/* $License$ */

/**
 * Constructs root order ID values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: BasicRootOrderIdFactory.java 17345 2017-08-10 20:47:47Z colin $
 * @since 2.5.0
 */
@Service
public class BasicRootOrderIdFactory
        implements RootOrderIdFactory
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.RootOrderIdFactory#getRootOrderId(quickfix.Message)
     */
    @Override
    public OrderID getRootOrderId(Message inMessage)
    {
        try {
            OrderID orderId = null;
            if(inMessage.isSetField(OrigClOrdID.FIELD)) {
                orderId = new OrderID(inMessage.getString(OrigClOrdID.FIELD));
                SLF4JLoggerProxy.debug(this,
                                       "Using origOrderID {} for query",  //$NON-NLS-1$
                                       orderId);
            }
            if(orderId == null && inMessage.isSetField(ClOrdID.FIELD)) {
                orderId = new OrderID(inMessage.getString(ClOrdID.FIELD));
                SLF4JLoggerProxy.debug(this,
                                       "No origOrderID present, using orderID {} for query",  //$NON-NLS-1$
                                       orderId);
            }
            if(orderId == null) {
                return null;
            }
            OrderID rootId = executionReportDao.findRootIDForOrderID(orderId);
            if(rootId == null) {
                SLF4JLoggerProxy.debug(this,
                                       "No other orders match this orderID - this must be the first in the order chain");  //$NON-NLS-1$
                // this is the first order in this chain
                return orderId;
            } else {
                SLF4JLoggerProxy.debug(this,
                                       "Not the first orderID in the chain, using {} for rootID",  //$NON-NLS-1$
                                       rootId);
            }
            return rootId;
        } catch (FieldNotFound e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            return null;
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.RootOrderIdFactory#getRootOrderId(org.marketcetera.trade.TradeMessage)
     */
    @Override
    public OrderID getRootOrderId(TradeMessage inReport)
    {
        // CD 17-Mar-2011 ORS-79
        // we need to find the correct root ID of the incoming ER. for cancels and cancel/replaces,
        //  this is easy - we can look up the root ID from the origOrderID. for a partial fill or fill
        //  of an original order, this is also easy - the rootID is just the orderID. the difficult case
        //  is a partial fill or fill of a replaced order. the origOrderID won't be present (not required)
        //  but there still exists an order chain to be respected or position reporting will be broken.
        //  therefore, the algorithm should be:
        // if the original orderID is present, use the root from that order
        // if it's not present, look for the rootID of an existing record with the same orderID
        if(inReport instanceof ReportBase) {
            ReportBase report = (ReportBase)inReport;
            SLF4JLoggerProxy.debug(this,
                                   "Searching for rootID for {}",  //$NON-NLS-1$
                                   report.getOrderID());
            OrderID orderId = null;
            if(report.getOriginalOrderID() == null) {
                SLF4JLoggerProxy.debug(this,
                                       "No origOrderID present, using orderID for query");  //$NON-NLS-1$
                orderId = report.getOrderID();
            } else {
                SLF4JLoggerProxy.debug(this,
                                       "Using origOrderID {} for query",  //$NON-NLS-1$
                                       report.getOriginalOrderID());
                orderId = report.getOriginalOrderID();
            }
            OrderID rootId = executionReportDao.findRootIDForOrderID(orderId);
            if(rootId == null) {
                SLF4JLoggerProxy.debug(this,
                                       "No other orders match this orderID - this must be the first in the order chain");  //$NON-NLS-1$
                // this is the first order in this chain
                rootId = report.getOrderID();
            } else {
                SLF4JLoggerProxy.debug(this,
                                       "Not the first orderID in the chain, using {} for rootID",  //$NON-NLS-1$
                                       rootId);
            }
            return rootId;
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * Get the executionReportDao value.
     *
     * @return an <code>ExecutionReportDao</code> value
     */
    public ExecutionReportDao getExecutionReportDao()
    {
        return executionReportDao;
    }
    /**
     * Sets the executionReportDao value.
     *
     * @param inExecutionReportDao an <code>ExecutionReportDao</code> value
     */
    public void setExecutionReportDao(ExecutionReportDao inExecutionReportDao)
    {
        executionReportDao = inExecutionReportDao;
    }
    /**
     * provides datastore access to execution reports
     */
    @Autowired
    private ExecutionReportDao executionReportDao;
}
