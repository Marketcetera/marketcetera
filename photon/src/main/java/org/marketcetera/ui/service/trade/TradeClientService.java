package org.marketcetera.ui.service.trade;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.core.ClientStatusListener;
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
import org.marketcetera.trade.ReportID;
import org.marketcetera.trade.Suggestion;
import org.marketcetera.trade.SuggestionListener;
import org.marketcetera.trade.TradeMessageListener;
import org.marketcetera.trade.client.SendOrderResponse;
import org.marketcetera.trade.client.TradeClient;
import org.marketcetera.trading.rpc.TradeRpcClientFactory;
import org.marketcetera.trading.rpc.TradeRpcClientParametersImpl;
import org.marketcetera.ui.service.ConnectableService;
import org.marketcetera.ui.service.ServiceManager;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/* $License$ */

/**
 * Provides access to trade services for a given user.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Configuration
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
     * Removes the given report from the persistent report store.
     * 
     * <p>Reports removed this way will not be added to the system data bus and no clients
     * will receive this report.
     * 
     * <p><em>This will affect reported positions</em></p>.
     *
     * @param inReportId a <code>ReportID</code> value
     */
    public void deleteReport(ReportID inReportId)
    {
        tradeClient.deleteReport(inReportId);
    }
    /**
     * Add the given trade message listener.
     *
     * @param inTradeMessageListener a <code>TradeMessageListener</code> value
     */
    public void addTradeMessageListener(TradeMessageListener inTradeMessageListener)
    {
        tradeClient.addTradeMessageListener(inTradeMessageListener);
    }
    /**
     * Remove the given trade message listener.
     *
     * @param inTradeMessageListener a <code>TradeMessageListener</code> value
     */
    public void removeTradeMessageListener(TradeMessageListener inTradeMessageListener)
    {
        tradeClient.removeTradeMessageListener(inTradeMessageListener);
    }
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
     * Get the symbol modified for use in the server as necessary.
     *
     * @param inSymbol a <code>String</code> value
     * @return a <code>String</code> value or <code>null</code>
     */
    public String getTreatedSymbol(String inSymbol)
    {
        inSymbol = StringUtils.trimToNull(inSymbol);
        if(inSymbol == null) {
            return null;
        }
        if(isForceCapitalSymbols()) {
            inSymbol = inSymbol.toUpperCase();
        }
        return inSymbol;
    }
    /**
     * Indicates if symbols should be forced to capital letters.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isForceCapitalSymbols()
    {
        return forceCapitalSymbols;
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
    /**
     * Submit a trade suggestion.
     *
     * @param inSuggestion a <code>Suggestion</code> value
     */
    public void sendOrderSuggestion(Suggestion inSuggestion)
    {
        tradeClient.sendOrderSuggestion(inSuggestion);
    }
    /**
     * Add the given suggestion listener.
     *
     * @param inSuggestionListener a <code>SuggestionListener</code> value
     */
    public void addSuggestionListener(SuggestionListener inSuggestionListener)
    {
        tradeClient.addSuggestionListener(inSuggestionListener);
    }
    /**
     * Remove the given suggestion listener.
     *
     * @param inSuggestionListener a <code>SuggestionListener</code> value
     */
    public void removeSuggestionListener(SuggestionListener inSuggestionListener)
    {
        tradeClient.removeSuggestionListener(inSuggestionListener);
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
     * @see org.marketcetera.ui.service.ConnectableService#connect(java.lang.String, java.lang.String, java.lang.String, int, boolean)
     */
    @Override
    public boolean connect(String inUsername,
                           String inPassword,
                           String inHostname,
                           int inPort,
                           boolean inUseSsl)
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
                               "Creating trade client for {} to {}:{} with ssl: {}",
                               inUsername,
                               inHostname,
                               inPort,
                               inUseSsl);
        TradeRpcClientParametersImpl params = new TradeRpcClientParametersImpl();
        params.setHostname(inHostname);
        params.setPort(inPort);
        params.setUsername(inUsername);
        params.setPassword(inPassword);
        params.setUseSsl(inUseSsl);
        tradeClient = tradeClientFactory.create(params);
        tradeClient.start();
        return tradeClient.isRunning();
    }
    /**
     * Add the given client status listener.
     *
     * @param inListener a <code>ClientStatusListener</code> value
     */
    public void addClientStatusListener(ClientStatusListener inListener)
    {
        tradeClient.addClientStatusListener(inListener);
    }
    /**
     * Remove the given client status listener.
     *
     * @param inListener a <code>ClientStatusListener</code> value
     */
    public void removeClientStatusListener(ClientStatusListener inListener)
    {
        tradeClient.removeClientStatusListener(inListener);
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
     * strategy temporary directory base
     */
    @Value("${metc.photon.capital.symbols}")
    private boolean forceCapitalSymbols;
    /**
     * creates an Trade client to connect to the Trade server
     */
    private TradeRpcClientFactory tradeClientFactory;
    /**
     * client object used to communicate with the server
     */
    private TradeClient tradeClient;
}
