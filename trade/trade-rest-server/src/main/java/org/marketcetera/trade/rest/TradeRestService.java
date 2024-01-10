package org.marketcetera.trade.rest;

import java.util.Collection;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.fix.impl.SimpleActiveFixSession;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.trade.AbstractSuggestion;
import org.marketcetera.trade.FIXOrderImpl;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OrderCancelImpl;
import org.marketcetera.trade.OrderReplaceImpl;
import org.marketcetera.trade.OrderSingleImpl;
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
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
    @ApiResponses(value={@ApiResponse(responseCode="400",description="Invalid parameters")})
    @GetMapping(value="/matp/trade/availableFixSessions",produces="application/json")
    @Operation(summary="get the currently available FIX initiator sessions")
    public List<SimpleActiveFixSession> readAvailableFixInitiatorSessions()
    {
        throw new UnsupportedOperationException(); // TODO
    }
//    /**
//     * Get open orders.
//     *
//     * @return a <code>Collection&lt;SimpleOrderSummary&gt;</code> value
//     */
//    @ApiResponses(value={@ApiResponse(responseCode=400,message="Invalid parameters")})
//    @GetMapping(value="/matp/trade/openOrders",produces="application/json")
//    @Operation(description="get open orders",protocols="http,https",produces="application/json")
//    public Collection<SimpleOrderSummary> getOpenOrders()
//    {
//        throw new UnsupportedOperationException(); // TODO
//    }
//    /**
//     * Get open orders.
//     *
//     * @param inPageNumber an <code>Integer</code> value
//     * @param inPageSize an <code>Integer</code> value
//     * @return a <code>CollectionPageResponset&lt;OrderSummary&gt;</code> value
//     */
//    @ApiResponses(value={@ApiResponse(responseCode=400,message="Invalid parameters")})
//    @GetMapping(value="/matp/trade/openOrdersPage",produces="application/json")
//    @Operation(description="get paged open orders",protocols="http,https",produces="application/json")
//    public CollectionPageResponse<SimpleOrderSummary> getOpenOrders(@RequestParam(name="pageNumber",required=true)Integer inPageNumber,
//                                                                    @RequestParam(name="pageSize",required=true)Integer inPageSize)
//    {
//        throw new UnsupportedOperationException(); // TODO
//    }
//    /**
//     * Submit the given orders.
//     *
//     * @param inOrders a <code>List&lt;Order&gt;</code> value
//     * @return a <code>List&lt;SendOrderResponse&gt;</code> value
//     */
//    @Operation(description="submit the given orders",response=SendOrderResponses.class,protocols="http,https",produces="application/json")
//    @ApiResponses(value={@ApiResponse(responseCode=200,message="Successfully submitted orders"),
//                         @ApiResponse(responseCode=400,message="Invalid parameters")})
//    @PostMapping(value="/matp/trade/sendOrders",produces="application/json")
//    public SendOrderResponses sendOrders(@RequestParam(name="orders",required=true)OrdersWrapper inOrders)
//    {
//        throw new UnsupportedOperationException(); // TODO
//    }
//    /**
//     * Submit a trade suggestion.
//     *
//     * @param inSuggestion an <code>AbstractSuggestion</code> value
//     */
//    @Operation(description="submit the given suggestion",protocols="http,https",produces="application/json")
//    @ApiResponses(value={@ApiResponse(responseCode=200,message="Successfully submitted suggestion"),
//                         @ApiResponse(responseCode=400,message="Invalid parameters")})
//    @PostMapping(value="/matp/trade/sendOrderSuggestion",produces="application/json")
//    public void sendOrderSuggestion(@RequestParam(name="suggestion",required=true)AbstractSuggestion inSuggestion)
//    {
//        throw new UnsupportedOperationException(); // TODO
//    }
//    /**
//     * Submit the given order.
//     *
//     * @param inOrder an <code>Order</code> value
//     * @return a <code>SendOrderResponse</code> value
//     */
//    @Operation(description="submit the given order",response=SendOrderResponse.class,protocols="http,https",produces="application/json")
//    @ApiResponses(value={@ApiResponse(responseCode=200,message="Successfully submitted order"),
//                         @ApiResponse(responseCode=400,message="Invalid parameters")})
//    @PostMapping(value="/matp/trade/sendOrder",produces="application/json")
//    public SendOrderResponse sendOrder(@RequestParam(name="suggestion",required=true)OrderWrapper inOrder)
//    {
//        throw new UnsupportedOperationException(); // TODO
//    }
//    /**
//     * Resolves the given symbol to an <code>Instrument</code>.
//     *
//     * @param inSymbol a <code>String</code> value
//     * @return an <code>Instrument</code> value
//     */
//    @ApiResponses(value={@ApiResponse(responseCode=400,message="Invalid parameters")})
//    @GetMapping(value="/matp/trade/resolveSymbol",produces="application/json")
//    @Operation(description="get the resolved symbol",protocols="http,https",produces="application/json")
//    public Instrument resolveSymbol(@RequestParam(name="symbol",required=true)String inSymbol)
//    {
//        throw new UnsupportedOperationException(); // TODO
//    }
//    /**
//     * Get reports with the given page request.
//     *
//     * @param inPageNumber an <code>Integer</code> value
//     * @param inPageSize an <code>Integer</code> value
//     * @return a <code>CollectionPageResponse&lt;Report&gt;</code> value
//     */
//    @ApiResponses(value={@ApiResponse(responseCode=400,response=CollectionPageResponse.class,message="Invalid parameters")})
//    @GetMapping(value="/matp/trade/reportsPage",produces="application/json")
//    @Operation(description="get a page of execution reports",protocols="http,https",produces="application/json")
//    public CollectionPageResponse<SimpleReport> getReports(@RequestParam(name="pageNumber",required=true)Integer inPageNumber,
//                                                           @RequestParam(name="pageSize",required=true)Integer inPageSize)
//    {
//        throw new UnsupportedOperationException(); // TODO
//    }
//    /**
//     * Get fills with the given page request.
//     *
//     * @param inPageNumber an <code>Integer</code> value
//     * @param inPageSize an <code>Integer</code> value
//     * @return a <code>CollectionPageResponse&lt;ExecutionReportSummary&gt;</code> value
//     */
//    public CollectionPageResponse<SimpleExecutionReportSummary> getFills(@RequestParam(name="pageNumber",required=true)Integer inPageNumber,
//                                                                         @RequestParam(name="pageSize",required=true)Integer inPageSize)
//    {
//        throw new UnsupportedOperationException(); // TODO
//    }
//    /**
//     * Get average price fills values.
//     *
//     * @param inPageNumber an <code>Integer</code> value
//     * @param inPageSize an <code>Integer</code> value
//     * @return a <code>CollectionPageResponse&lt;AveragePriceFill&gt;</code> value
//     */
//    public CollectionPageResponse<SimpleAverageFillPrice> getAveragePriceFills(@RequestParam(name="pageNumber",required=true)Integer inPageNumber,
//                                                                               @RequestParam(name="pageSize",required=true)Integer inPageSize)
//    {
//        throw new UnsupportedOperationException(); // TODO
//    }
//    /**
//     *
//     *
//     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
//     * @version $Id$
//     * @since $Release$
//     */
//    public static class SendOrderResponses
//    {
//        private final List<SendOrderResponse> sendOrderResponses = Lists.newArrayList();
//    }
//    /**
//     *
//     *
//     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
//     * @version $Id$
//     * @since $Release$
//     */
//    public static class OrdersWrapper
//    {
//        private final List<OrderWrapper> orders = Lists.newArrayList();
//    }
//    /**
//     *
//     *
//     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
//     * @version $Id$
//     * @since $Release$
//     */
//    public abstract static class OrderWrapper
//    {
//        // TODO FIX Order
//        // TODO Order Single
//        // TODO Order Cancel
//        // TODO Order Replace
//        private OrderSingleImpl orderSingle;
//    }
//    /**
//     *
//     *
//     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
//     * @version $Id$
//     * @since $Release$
//     */
//    private static class FixOrderWrapper
//            extends OrderWrapper
//    {
//        private FIXOrderImpl fixOrder;
//    }
//    /**
//     *
//     *
//     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
//     * @version $Id$
//     * @since $Release$
//     */
//    private static class OrderSingleWrapper
//            extends OrderWrapper
//    {
//        private OrderSingleImpl orderSingle;
//    }
//    /**
//     *
//     *
//     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
//     * @version $Id$
//     * @since $Release$
//     */
//    private static class OrderCancelWrapper
//            extends OrderWrapper
//    {
//        private OrderCancelImpl orderCancel;
//    }
//    /**
//     *
//     *
//     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
//     * @version $Id$
//     * @since $Release$
//     */
//    private static class OrderReplaceWrapper
//            extends OrderWrapper
//    {
//        private OrderReplaceImpl orderReplace;
//    }
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
