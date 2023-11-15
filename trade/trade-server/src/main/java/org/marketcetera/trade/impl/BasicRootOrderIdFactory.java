package org.marketcetera.trade.impl;

import javax.annotation.PostConstruct;

import org.hibernate.NonUniqueResultException;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.RootOrderIdFactory;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.service.ReportService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/* $License$ */

/**
 * Constructs root order ID values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
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
    public OrderID getRootOrderId(quickfix.Message inMessage)
    {
        try {
            OrderID orderId = null;
            if(inMessage.isSetField(quickfix.field.OrigClOrdID.FIELD)) {
                orderId = new OrderID(inMessage.getString(quickfix.field.OrigClOrdID.FIELD));
                SLF4JLoggerProxy.debug(this,
                                       "Using origOrderID {} for query",  //$NON-NLS-1$
                                       orderId);
            }
            if(orderId == null && inMessage.isSetField(quickfix.field.ClOrdID.FIELD)) {
                orderId = new OrderID(inMessage.getString(quickfix.field.ClOrdID.FIELD));
                SLF4JLoggerProxy.debug(this,
                                       "No origOrderID present, using orderID {} for query",  //$NON-NLS-1$
                                       orderId);
            }
            if(orderId == null) {
                return null;
            }
            OrderID rootId = reportService.findRootIDForOrderID(orderId);
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
        } catch (NonUniqueResultException e) {
            // TODO the data is horked a little, pick the root from the most recent result
            throw new RuntimeException(e);
        } catch (quickfix.FieldNotFound e) {
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
            OrderID rootId = reportService.findRootIDForOrderID(orderId);
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
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.RootOrderIdFactory#receiveOutgoingMessage(quickfix.Message)
     */
    @Override
    public void receiveOutgoingMessage(quickfix.Message inMessage)
    {
        try {
            String msgType = inMessage.getHeader().getString(quickfix.field.MsgType.FIELD);
            switch(msgType) {
                case quickfix.field.MsgType.ORDER_SINGLE:
                    String clOrdId = inMessage.getString(quickfix.field.ClOrdID.FIELD);
                    SLF4JLoggerProxy.debug(this,
                                           "Caching root order id {} for {}",
                                           clOrdId,
                                           inMessage);
                    rootOrderIdCache.put(new OrderID(clOrdId),
                                         new OrderID(clOrdId));
                    break;
                case quickfix.field.MsgType.ORDER_CANCEL_REPLACE_REQUEST:
                case quickfix.field.MsgType.ORDER_CANCEL_REQUEST:
                    clOrdId = inMessage.getString(quickfix.field.ClOrdID.FIELD);
                    OrderID origClOrdId = new OrderID(inMessage.getString(quickfix.field.OrigClOrdID.FIELD));
                    OrderID cachedRootOrderId = rootOrderIdCache.getIfPresent(origClOrdId);
                    if(cachedRootOrderId == null) {
                        SLF4JLoggerProxy.debug(this,
                                               "No cached root order id for {}, searching the database",
                                               origClOrdId);
                        cachedRootOrderId = reportService.findRootIDForOrderID(origClOrdId);
                    }
                    if(cachedRootOrderId == null) {
                        SLF4JLoggerProxy.debug(this,
                                               "No cached root order id for {}",
                                               origClOrdId);
                        cachedRootOrderId = reportService.findRootIDForOrderID(origClOrdId);
                    } else {
                        SLF4JLoggerProxy.debug(this,
                                               "Caching root order id {} for {}",
                                               clOrdId,
                                               inMessage);
                        rootOrderIdCache.put(new OrderID(clOrdId),
                                             cachedRootOrderId);
                    }
                    break;
            }
        } catch (quickfix.FieldNotFound e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
        }
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        rootOrderIdCache = CacheBuilder.newBuilder().maximumSize(cacheSize).build();
    }
    /**
     * number of root order ids cache
     */
    private int cacheSize = 10000;
    /**
     * caches root order id values
     */
    private Cache<OrderID,OrderID> rootOrderIdCache;
    /**
     * provides access to report services
     */
    @Autowired
    private ReportService reportService;
}
