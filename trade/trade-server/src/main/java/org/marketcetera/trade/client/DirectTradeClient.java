package org.marketcetera.trade.client;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.marketcetera.admin.User;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.client.SubmitOrderWrapper;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.module.HasStatus;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.symbol.SymbolResolverService;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.ReportID;
import org.marketcetera.trade.SendOrderFailed;
import org.marketcetera.trade.TradeMessageListener;
import org.marketcetera.trade.TradeMessagePublisher;
import org.marketcetera.trade.service.OrderSummaryService;
import org.marketcetera.trade.service.ReportService;
import org.marketcetera.trade.service.TradeService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.context.ApplicationContext;

import quickfix.FieldNotFound;

/* $License$ */

/**
 * Provides an in-process {@link TradeClient} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DirectTradeClient
        implements TradeClient
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.BaseClient#start()
     */
    @Override
    public void start()
            throws Exception
    {
        SLF4JLoggerProxy.info(this,
                              "Starting direct trade client");
        Validate.notNull(applicationContext);
        userService = applicationContext.getBean(UserService.class);
        orderSummaryService = applicationContext.getBean(OrderSummaryService.class);
        tradeMessagePublisher = applicationContext.getBean(TradeMessagePublisher.class);
        tradeService = applicationContext.getBean(TradeService.class);
        reportService = applicationContext.getBean(ReportService.class);
        symbolResolverService = applicationContext.getBean(SymbolResolverService.class);
        SLF4JLoggerProxy.debug(this,
                               "Direct client {} owned by user {}",
                               clientId,
                               username);
        user = userService.findByName(username);
        Validate.notNull(user);
        running = true;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.BaseClient#stop()
     */
    @Override
    public void stop()
            throws Exception
    {
        try {
            SLF4JLoggerProxy.info(this,
                                  "Stopping direct trade client");
        } finally {
            running = false;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.BaseClient#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return running;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.TradeMessagePublisher#addTradeMessageListener(org.marketcetera.trade.TradeMessageListener)
     */
    @Override
    public void addTradeMessageListener(TradeMessageListener inTradeMessageListener)
    {
        tradeMessagePublisher.addTradeMessageListener(inTradeMessageListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.TradeMessagePublisher#removeTradeMessageListener(org.marketcetera.trade.TradeMessageListener)
     */
    @Override
    public void removeTradeMessageListener(TradeMessageListener inTradeMessageListener)
    {
        tradeMessagePublisher.removeTradeMessageListener(inTradeMessageListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradeClient#getOpenOrders()
     */
    @Override
    public Collection<? extends OrderSummary> getOpenOrders()
    {
        return getOpenOrders(PageRequest.ALL).getElements();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradeClient#getOpenOrders(org.marketcetera.persist.PageRequest)
     */
    @Override
    public CollectionPageResponse<? extends OrderSummary> getOpenOrders(PageRequest inPageRequest)
    {
        return orderSummaryService.findOpenOrders(inPageRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradeClient#sendOrders(java.util.List)
     */
    @Override
    public List<SendOrderResponse> sendOrders(List<Order> inOrders)
    {
        List<SendOrderResponse> responses = Lists.newArrayList();
        for(Order order : inOrders) {
            try {
                responses.add(sendOrder(order));
            } catch (SendOrderFailed e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        return responses;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradeClient#sendOrder(org.marketcetera.trade.Order)
     */
    @Override
    public SendOrderResponse sendOrder(Order inOrder)
    {
        Object result = tradeService.submitOrderToOutgoingDataFlow(new SubmitOrderWrapper(user,
                                                                                          inOrder));
        SLF4JLoggerProxy.debug(this,
                               "Order submission returned {}",
                               result);
        SendOrderResponse response = new SendOrderResponse();
        if(result instanceof HasStatus) {
            HasStatus hasStatus = (HasStatus)result;
            if(hasStatus.getFailed()) {
                throw new SendOrderFailed(hasStatus.getErrorMessage());
            }
        }
        OrderID orderId = unknownOrderId;
        if(result instanceof HasFIXMessage) {
            HasFIXMessage hasFixMessage = (HasFIXMessage)result;
            quickfix.Message message = hasFixMessage.getMessage();
            if(message.isSetField(quickfix.field.ClOrdID.FIELD)) {
                try {
                    orderId = new OrderID(message.getString(quickfix.field.ClOrdID.FIELD));
                } catch (FieldNotFound e) {
                    SLF4JLoggerProxy.warn(this,
                                          e);
                }
            }
        }
        response.setOrderId(orderId);
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradeClient#getPositionAsOf(java.util.Date, org.marketcetera.trade.Instrument)
     */
    @Override
    public BigDecimal getPositionAsOf(Date inDate,
                                      Instrument inInstrument)
    {
        return reportService.getPositionAsOf(user,
                                             inDate,
                                             inInstrument);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradeClient#getAllPositionsAsOf(java.util.Date)
     */
    @Override
    public Map<PositionKey<? extends Instrument>,BigDecimal> getAllPositionsAsOf(Date inDate)
    {
        return reportService.getAllPositionsAsOf(user,
                                                 inDate);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradeClient#getOptionPositionsAsOf(java.util.Date, java.lang.String[])
     */
    @Override
    public Map<PositionKey<Option>,BigDecimal> getOptionPositionsAsOf(Date inDate,
                                                                      String... inRootSymbols)
    {
        return reportService.getOptionPositionsAsOf(user,
                                                    inDate,
                                                    inRootSymbols);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradeClient#addReport(org.marketcetera.event.HasFIXMessage, org.marketcetera.trade.BrokerID)
     */
    @Override
    public void addReport(HasFIXMessage inReport,
                          BrokerID inBrokerID)
    {
        reportService.addReport(inReport,
                                inBrokerID,
                                user.getUserID());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradeClient#deleteReport(org.marketcetera.trade.ReportID)
     */
    @Override
    public void deleteReport(ReportID inReportId)
    {
        reportService.delete(inReportId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradeClient#resolveSymbol(java.lang.String)
     */
    @Override
    public Instrument resolveSymbol(String inSymbol)
    {
        return symbolResolverService.resolveSymbol(inSymbol);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradeClient#findRootOrderIdFor(org.marketcetera.trade.OrderID)
     */
    @Override
    public OrderID findRootOrderIdFor(OrderID inOrderID)
    {
        return reportService.getRootOrderIdFor(inOrderID);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradeClient#getOptionRoots(java.lang.String)
     */
    @Override
    public Collection<String> getOptionRoots(String inUnderlying)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradeClient#getUnderlying(java.lang.String)
     */
    @Override
    public String getUnderlying(String inOptionRoot)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * Get the applicationContext value.
     *
     * @return an <code>ApplicationContext</code> value
     */
    public ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }
    /**
     * Sets the applicationContext value.
     *
     * @param inApplicationContext an <code>ApplicationContext</code> value
     */
    public void setApplicationContext(ApplicationContext inApplicationContext)
    {
        applicationContext = inApplicationContext;
    }
    /**
     * Create a new DirectTradeClient instance.
     *
     * @param inApplicationContext an <code>ApplicationContext</code> value
     * @param inUsername a <code>String</code> value
     */
    protected DirectTradeClient(ApplicationContext inApplicationContext, 
                                String inUsername)
    {
        applicationContext = inApplicationContext;
        username = StringUtils.trimToNull(inUsername);
        Validate.notNull(username);
    }
    /**
     * provides access to the application context
     */
    private ApplicationContext applicationContext;
    /**
     * name of user
     */
    private final String username;
    /**
     * user which owns the activity of this client
     */
    private User user;
    /**
     * indicates if the client is running or not
     */
    private boolean running = false;
    /**
     * provides access to user services
     */
    private UserService userService;
    /**
     * provides access to trade messages
     */
    private TradeMessagePublisher tradeMessagePublisher;
    /**
     * provides access to trade services
     */
    private TradeService tradeService;
    /**
     * provides access to report services
     */
    private ReportService reportService;
    /**
     * provides access to order summary services
     */
    private OrderSummaryService orderSummaryService;
    /**
     * resolves symbols
     */
    private SymbolResolverService symbolResolverService;
    /**
     * uniquely identifies this client
     */
    private final String clientId = PlatformServices.generateId();
    /**
     * order id for unknown orders
     */
    private final static OrderID unknownOrderId = new OrderID("unknown");
}
