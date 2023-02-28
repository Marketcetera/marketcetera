package org.marketcetera.ui.service.trade;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.AverageFillPrice;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.ExecutionReportSummary;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.Report;
import org.marketcetera.trade.client.SendOrderResponse;
import org.marketcetera.trade.client.TradeClient;
import org.marketcetera.trading.rpc.TradeRpcClientFactory;
import org.marketcetera.trading.rpc.TradeRpcClientParametersImpl;
import org.marketcetera.ui.service.ConnectableService;
import org.marketcetera.ui.service.ServiceManager;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Provides access to trade services for a given user.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TradeClientService
        implements ConnectableService
{
    /**
     * Get the <code>TradeClientService</code> instance for the current session.
     *
     * @return a <code>TradeClientService</code> value or <code>null</code>
     */
    public static TradeClientService getInstance()
    {
        return ServiceManager.getInstance().getService(TradeClientService.class);
    }
    /**
     * Create a new TradeClientService instance.
     */
    public TradeClientService() {}
    /**
     * Get the latest execution report from the order chain represented by the given order id from the chain or chain root order id.
     *
     * @param inOrderId an <code>OrderID</code> value
     * @return an <code>ExecutionReport</code> or <code>null</code>
     */
    public ExecutionReport getLatestExecutionReportForOrderChain(OrderID inOrderId)
    {
        return tradeClient.getLatestExecutionReportForOrderChain(inOrderId);
    }
    /**
     * Get report values.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponse&lt;Report&gt;</code> value
     */
    public CollectionPageResponse<Report> getReports(PageRequest inPageRequest)
    {
        return tradeClient.getReports(inPageRequest);
    }
    /**
     * Get fill execution report values.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponse&lt;ExecutionReportSummary&gt;</code> value
     */
    public CollectionPageResponse<ExecutionReportSummary> getFills(PageRequest inPageRequest)
    {
        return tradeClient.getFills(inPageRequest);
    }
    /**
     * Get average price fills values.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>Collection&lt;AverageFillPrice&gt;</code> value
     */
    public CollectionPageResponse<AverageFillPrice> getAveragePrice(PageRequest inPageRequest)
    {
        return tradeClient.getAveragePriceFills(inPageRequest);
    }
    /**
     * Get open orders.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponse&lt;OrderSummary&gt;</code> value
     */
    public CollectionPageResponse<OrderSummary> getOpenOrders(PageRequest inPageRequest)
    {
        return tradeClient.getOpenOrders(inPageRequest);
    }
    /**
     * Get the available fix initiator sessions visible to the current user.
     *
     * @return a <code>List&lt;ActiveFixSession&gt;</code> value
     */
    public List<ActiveFixSession> getAvailableFixInitiatorSessions()
    {
        return tradeClient.readAvailableFixInitiatorSessions();
    }
    /**
     * Resolves the given symbol to an <code>Instrument</code>.
     *
     * @param inSymbol a <code>String</code> value
     * @return an <code>Instrument</code> value
     */
    public Instrument resolveSymbol(String inSymbol)
    {
        return tradeClient.resolveSymbol(inSymbol);
    }
    /**
     * Send the given order.
     *
     * @param inOrder an <code>Order</code> value
     * @return a <code>SendOrderResponse</code> value
     */
    public SendOrderResponse send(Order inOrder)
    {
        return tradeClient.sendOrder(inOrder);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.ConnectableService#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return tradeClient != null && tradeClient.isRunning();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.ConnectableService#disconnect()
     */
    @Override
    public void disconnect()
    {
        if(tradeClient != null) {
            try {
                tradeClient.close();
            } catch (IOException e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        tradeClient = null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.services.ConnectableService#connect(java.lang.String, java.lang.String, java.lang.String, int)
     */
    @Override
    public boolean connect(String inUsername,
                           String inPassword,
                           String inHostname,
                           int inPort)
            throws Exception
    {
        if(tradeClient != null) {
            try {
                tradeClient.stop();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      "Unable to stop existing Trade client for {}: {}",
                                      inUsername,
                                      ExceptionUtils.getRootCauseMessage(e));
            } finally {
                tradeClient = null;
            }
        }
        SLF4JLoggerProxy.debug(this,
                               "Creating Trade client for {} to {}:{}",
                               inUsername,
                               inHostname,
                               inPort);
        TradeRpcClientParametersImpl params = new TradeRpcClientParametersImpl();
        params.setHostname(inHostname);
        params.setPort(inPort);
        params.setUsername(inUsername);
        params.setPassword(inPassword);
        tradeClient = tradeClientFactory.create(params);
        tradeClient.start();
        return tradeClient.isRunning();
    }
    /**
     * Sets the TradeClientFactory value.
     *
     * @param inTradeClientFactory an <code>TradeRpcClientFactory</code> value
     */
    public void setTradeClientFactory(TradeRpcClientFactory inTradeClientFactory)
    {
        tradeClientFactory = inTradeClientFactory;
    }
    /**
     * creates an Trade client to connect to the Trade server
     */
    private TradeRpcClientFactory tradeClientFactory;
    /**
     * client object used to communicate with the server
     */
    private TradeClient tradeClient;
}
