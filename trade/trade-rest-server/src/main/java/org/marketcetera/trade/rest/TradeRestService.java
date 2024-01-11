package org.marketcetera.trade.rest;

import java.util.Collection;
import java.util.List;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.fix.impl.SimpleActiveFixSession;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderCancelSuggestion;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderReplaceSuggestion;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderSingleSuggestion;
import org.marketcetera.trade.SimpleAverageFillPrice;
import org.marketcetera.trade.SimpleExecutionReportSummary;
import org.marketcetera.trade.SimpleOrderSummary;
import org.marketcetera.trade.SimpleReport;
import org.marketcetera.trade.client.SendOrderResponse;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.PortDescriptor;
import org.marketcetera.util.ws.stateful.UsesPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import reactor.core.publisher.Flux;

/* $License$ */

/**
 *
 * <p>Connect to API at: {@link http://localhost:13000/swagger-ui.html}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@RestController
@SpringBootConfiguration
@ConfigurationProperties
@Tag(name="Trade server operations")
public class TradeRestService
        implements UsesPort
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        serviceName = PlatformServices.getServiceName(getClass());
        SLF4JLoggerProxy.info(this,
                              "Starting {}",
                              serviceName);
        ports.add(new PortDescriptor(serverPort,
                                     "REST Server"));
    }
    /**
     * Stop the object.
     */
    @PreDestroy
    public void stop()
    {
        SLF4JLoggerProxy.info(this,
                              "Stopping {}",
                              serviceName);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.util.ws.stateful.UsesPort#getPortDescriptors()
     */
    @Override
    public Collection<PortDescriptor> getPortDescriptors()
    {
        return ports;
    }
    /**
     * Get the currently available FIX initiator sessions.
     *
     * @return a <code>List&lt;SimpleActiveFixSession&gt;</code> value
     */
    @ApiResponses(value={@ApiResponse(responseCode="200",description="Successfully returned available FIX sessions"),
                         @ApiResponse(responseCode="400",description="Invalid parameters",content=@Content)})
    @RequestMapping(value="/matp/trade/availableFixSessions",method=RequestMethod.GET,produces={"application/json","appliciation/xml"})
    @Operation(summary="Gets the currently available FIX initiator sessions",
               description="Available FIX sessions are order destinations that are currently connected and authorized for the authenticated user")
    public List<SimpleActiveFixSession> readAvailableFixInitiatorSessions()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * Get open orders.
     *
     * @return a <code>Collection&lt;SimpleOrderSummary&gt;</code> value
     */
    @ApiResponses(value={@ApiResponse(responseCode="200",description="Successfully returned open orders"),
                         @ApiResponse(responseCode="400",description="Invalid parameters",content=@Content)})
    @RequestMapping(value="/matp/trade/openOrders",method=RequestMethod.GET,produces={"application/json","appliciation/xml"})
    @Operation(summary="Gets open orders visible to the authenticated user",
               description="Returns all orders visible to the authenticated user that are in a cancellable status")
    public Collection<SimpleOrderSummary> getOpenOrders()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * Get open orders.
     *
     * @param inPageNumber an <code>Integer</code> value
     * @param inPageSize an <code>Integer</code> value
     * @return a <code>CollectionPageResponset&lt;OrderSummary&gt;</code> value
     */
//    @ApiResponses(value={@ApiResponse(responseCode="200",description="Successfully returned open orders"),
//                         @ApiResponse(responseCode="400",description="Invalid parameters",content=@Content)})
    @RequestMapping(value="/matp/trade/openOrdersPage",method=RequestMethod.GET,produces={"application/json","appliciation/xml"})
    @Operation(summary="Gets paged open orders visible to the authenticated user",
               description="Returns a page's worth of orders visible to the authenticated user that are in a cancellable status",
               responses={@ApiResponse(responseCode="200",description="Successfully returned open orders"),
                          @ApiResponse(responseCode="400",description="Invalid parameters",content=@Content)})
    public CollectionPageResponse<SimpleOrderSummary> getOpenOrders(@Parameter(description="0-based page number",allowEmptyValue=false,example="0")
                                                                    @RequestParam(name="pageNumber",required=true)Integer inPageNumber,
                                                                    @Parameter(description="non-zero page size",allowEmptyValue=false,example="20")
                                                                    @RequestParam(name="pageSize",required=true)Integer inPageSize)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * Submit the given orders.
     *
     * @param inOrders a <code>List&lt;Order&gt;</code> value
     * @return a <code>List&lt;SendOrderResponse&gt;</code> value
     */
    @ApiResponses(value={@ApiResponse(responseCode="200",description="Successfully submitted orders"),
                         @ApiResponse(responseCode="400",description="Invalid parameters",content=@Content)})
    @Operation(summary="Submits the given orders",
               description="Submits the given order or orders with the owner set to the authenticated user")
    @RequestMapping(value="/matp/trade/sendOrders",method=RequestMethod.POST,produces={"application/json","appliciation/xml"})
    public SendOrderResponses sendOrders(@Parameter(description="Array of orders to send",allowEmptyValue=false)
                                         @RequestParam(name="orders",required=true)OrdersWrapper inOrders)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * Submit a trade suggestion.
     *
     * @param inSuggestion an <code>AbstractSuggestion</code> value
     */
    @ApiResponses(value={@ApiResponse(responseCode="200",description="Successfully submitted suggestion"),
                         @ApiResponse(responseCode="400",description="Invalid parameters",content=@Content)})
    @Operation(summary="Submits the given order suggestion",
               description="Submits the given order suggestion owned by the authenticated user")
    @RequestMapping(value="/matp/trade/sendOrderSuggestion",method=RequestMethod.POST,produces={"application/json","appliciation/xml"})
    public void sendOrderSuggestion(@RequestParam(name="suggestion",required=true)OrderSuggestionWrapper inSuggestion)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * Submit the given order.
     *
     * @param inOrder an <code>Order</code> value
     * @return a <code>SendOrderResponse</code> value
     */
    @ApiResponses(value={ @ApiResponse(responseCode="200",description="Successfully submitted order"),
                          @ApiResponse(responseCode="400",description="Invalid parameters",content=@Content) })
    @Operation(summary="Submits the given order",
               description="Submits the given single order owned by the authenticated user")
    @RequestMapping(value="/matp/trade/sendOrder",method=RequestMethod.POST,produces={"application/json","appliciation/xml"})
    public SendOrderResponse sendOrder(@RequestParam(name="suggestion",required=true)OrderWrapper inOrder)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * Resolves the given symbol to an <code>Instrument</code>.
     *
     * @param inSymbol a <code>String</code> value
     * @return an <code>Instrument</code> value
     */
    @ApiResponses(value={ @ApiResponse(responseCode="200",description="Successfully resolved symbol"),
                          @ApiResponse(responseCode="400",description="Invalid parameters",content=@Content) })
    @Operation(summary="Resolves the given symbol to an instrument",
               description="Gets an instrument from the string according to the rules established for symbol resolution")
    @RequestMapping(value="/matp/trade/resolveSymbol",method=RequestMethod.GET,produces={"application/json","appliciation/xml"})
    public Instrument resolveSymbol(@RequestParam(name="symbol",required=true)String inSymbol)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * Subscribe to execution reports.
     *
     * @return a <code>Flux&lt;ServerSentEvent&lt;SimpleReportt&gt;&gt;</code> value
     */
    @ApiResponses(value={@ApiResponse(responseCode="200",description="Successfully executed an execution report stream request",
                  content=@io.swagger.v3.oas.annotations.media.Content(mediaType=MediaType.TEXT_EVENT_STREAM_VALUE,
                                                                       schema=@Schema(implementation=SimpleReport.class))),
                         @ApiResponse(responseCode="400",description="Invalid parameters",content=@io.swagger.v3.oas.annotations.media.Content)})
    @RequestMapping(value="/matp/trade/reportsStream",method=RequestMethod.POST,produces={MediaType.TEXT_EVENT_STREAM_VALUE})
    @Operation(summary="Creates a subscription to server-side-event streamed execution reports",
               description="Subscribes to execution reports visible to the authenticated user")
    public Flux<ServerSentEvent<SimpleReport>> requestMarketDataStream()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * Get reports with the given page request.
     *
     * @param inPageNumber an <code>Integer</code> value
     * @param inPageSize an <code>Integer</code> value
     * @return a <code>CollectionPageResponse&lt;Report&gt;</code> value
     */
    @ApiResponses(value={ @ApiResponse(responseCode="200",description="Successfully returned reports"),
                          @ApiResponse(responseCode="400",description="Invalid parameters",content=@Content)})
    @Operation(summary="Gets a page of execution reports",
               description="Returns a page of execution reports of all statuses that the authorized user is entitled to view")
    @RequestMapping(value="/matp/trade/reportsPage",method=RequestMethod.GET,produces={"application/json","appliciation/xml"})
    public CollectionPageResponse<SimpleReport> getReports(@Parameter(description="0-based page number",allowEmptyValue=false,example="0")
                                                           @RequestParam(name="pageNumber",required=true)Integer inPageNumber,
                                                           @Parameter(description="non-zero page size",allowEmptyValue=false,example="20")
                                                           @RequestParam(name="pageSize",required=true)Integer inPageSize)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * Get fills with the given page request.
     *
     * @param inPageNumber an <code>Integer</code> value
     * @param inPageSize an <code>Integer</code> value
     * @return a <code>CollectionPageResponse&lt;ExecutionReportSummary&gt;</code> value
     */
    @ApiResponses(value={ @ApiResponse(responseCode="200",description="Successfully returned fills"),
                          @ApiResponse(responseCode="400",description="Invalid parameters",content=@Content)})
    @Operation(summary="Gets a page of execution fills",
               description="Returns a page of execution report fills that the authorized user is entitiled to view")
    @RequestMapping(value="/matp/trade/fillsPage",method=RequestMethod.GET,produces={"application/json","appliciation/xml"})
    public CollectionPageResponse<SimpleExecutionReportSummary> getFills(@Parameter(description="0-based page number",allowEmptyValue=false,example="0")
                                                                         @RequestParam(name="pageNumber",required=true)Integer inPageNumber,
                                                                         @Parameter(description="non-zero page size",allowEmptyValue=false,example="20")
                                                                         @RequestParam(name="pageSize",required=true)Integer inPageSize)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * Get average price fills values.
     *
     * @param inPageNumber an <code>Integer</code> value
     * @param inPageSize an <code>Integer</code> value
     * @return a <code>CollectionPageResponse&lt;AveragePriceFill&gt;</code> value
     */
    @ApiResponses(value={ @ApiResponse(responseCode="200",description="Successfully returned average fill prices"),
                          @ApiResponse(responseCode="400",description="Invalid parameters",content=@Content)})
    @Operation(summary="Gets a page of execution fills",
               description="Returns a page of average fill price values grouped by instrument that the authorized user is entitiled to view")
    @RequestMapping(value="/matp/trade/averagePriceFillsPage",method=RequestMethod.GET,produces={"application/json","appliciation/xml"})
    public CollectionPageResponse<SimpleAverageFillPrice> getAveragePriceFills(@RequestParam(name="pageNumber",required=true)Integer inPageNumber,
                                                                               @RequestParam(name="pageSize",required=true)Integer inPageSize)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    public static class OrderSuggestionWrapper
    {
        /**
         * Get the orderSuggestion value.
         *
         * @return a <code>OrderSingleSuggestion</code> value
         */
        public OrderSingleSuggestion getOrderSuggestion()
        {
            return orderSuggestion;
        }
        /**
         * Sets the orderSuggestion value.
         *
         * @param inOrderSuggestion a <code>OrderSingleSuggestion</code> value
         */
        public void setOrderSuggestion(OrderSingleSuggestion inOrderSuggestion)
        {
            orderSuggestion = inOrderSuggestion;
        }
        /**
         * Get the orderReplaceSuggestion value.
         *
         * @return a <code>OrderReplaceSuggestion</code> value
         */
        public OrderReplaceSuggestion getOrderReplaceSuggestion()
        {
            return orderReplaceSuggestion;
        }
        /**
         * Sets the orderReplaceSuggestion value.
         *
         * @param inOrderReplaceSuggestion a <code>OrderReplaceSuggestion</code> value
         */
        public void setOrderReplaceSuggestion(OrderReplaceSuggestion inOrderReplaceSuggestion)
        {
            orderReplaceSuggestion = inOrderReplaceSuggestion;
        }
        /**
         * Get the orderCancelSuggesion value.
         *
         * @return a <code>OrderCancelSuggestion</code> value
         */
        public OrderCancelSuggestion getOrderCancelSuggesion()
        {
            return orderCancelSuggesion;
        }
        /**
         * Sets the orderCancelSuggesion value.
         *
         * @param inOrderCancelSuggesion a <code>OrderCancelSuggestion</code> value
         */
        public void setOrderCancelSuggesion(OrderCancelSuggestion inOrderCancelSuggesion)
        {
            orderCancelSuggesion = inOrderCancelSuggesion;
        }
        private OrderSingleSuggestion orderSuggestion;
        private OrderReplaceSuggestion orderReplaceSuggestion;
        private OrderCancelSuggestion orderCancelSuggesion;
    }
    /**
     *
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static class SendOrderResponses
    {
        /**
         * Get the sendOrderResponses value.
         *
         * @return a <code>List<SendOrderResponse></code> value
         */
        public List<SendOrderResponse> getSendOrderResponses()
        {
            return sendOrderResponses;
        }
        private final List<SendOrderResponse> sendOrderResponses = Lists.newArrayList();
    }
    /**
     *
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static class OrdersWrapper
    {
        /**
         * Get the orders value.
         *
         * @return a <code>List<OrderWrapper></code> value
         */
        public List<OrderWrapper> getOrders()
        {
            return orders;
        }
        private final List<OrderWrapper> orders = Lists.newArrayList();
    }
    /**
     *
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public abstract static class OrderWrapper
    {
        /**
         * Get the orderSingle value.
         *
         * @return a <code>OrderSingleImpl</code> value
         */
        public OrderSingle getOrderSingle()
        {
            return orderSingle;
        }
        /**
         * Sets the orderSingle value.
         *
         * @param inOrderSingle a <code>OrderSingleImpl</code> value
         */
        public void setOrderSingle(OrderSingle inOrderSingle)
        {
            orderSingle = inOrderSingle;
        }
//        /**
//         * Get the fixOrder value.
//         *
//         * @return a <code>FIXOrderImpl</code> value
//         */
//        public FIXOrderImpl getFixOrder()
//        {
//            return fixOrder;
//        }
//        /**
//         * Sets the fixOrder value.
//         *
//         * @param inFixOrder a <code>FIXOrderImpl</code> value
//         */
//        public void setFixOrder(FIXOrderImpl inFixOrder)
//        {
//            fixOrder = inFixOrder;
//        }
        /**
         * Get the orderCancel value.
         *
         * @return a <code>OrderCancelImpl</code> value
         */
        public OrderCancel getOrderCancel()
        {
            return orderCancel;
        }
        /**
         * Sets the orderCancel value.
         *
         * @param inOrderCancel a <code>OrderCancelImpl</code> value
         */
        public void setOrderCancel(OrderCancel inOrderCancel)
        {
            orderCancel = inOrderCancel;
        }
        /**
         * Get the orderReplace value.
         *
         * @return a <code>OrderReplaceImpl</code> value
         */
        public OrderReplace getOrderReplace()
        {
            return orderReplace;
        }
        /**
         * Sets the orderReplace value.
         *
         * @param inOrderReplace a <code>OrderReplaceImpl</code> value
         */
        public void setOrderReplace(OrderReplace inOrderReplace)
        {
            orderReplace = inOrderReplace;
        }
        private OrderSingle orderSingle;
        private OrderCancel orderCancel;
        private OrderReplace orderReplace;
//        private FIXOrderImpl fixOrder;
    }
    /**
     * indicates the port to bind the REST services to
     */
    @Value("${server.port}")
    private int serverPort;
    /**
     * indicates ports in use
     */
    private final Collection<PortDescriptor> ports = Lists.newArrayList();
    /**
     * human-readable name of this service
     */
    private String serviceName;
}
