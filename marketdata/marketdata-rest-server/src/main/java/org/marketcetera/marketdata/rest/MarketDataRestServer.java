package org.marketcetera.marketdata.rest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Deque;
import java.util.Set;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.event.EventType;
import org.marketcetera.event.QuoteAction;
import org.marketcetera.marketdata.AssetClass;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Sets;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.FluxSink;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@RestController
@SpringBootConfiguration
@ConfigurationProperties
@Tag(name="Market data server operations")
public class MarketDataRestServer
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
        fluxProcessor = DirectProcessor.<SimpleMarketDataEvent>create().serialize();
        fluxSink = fluxProcessor.sink();
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
    /**
     * Request market data.
     * 
     * @param inRequest a <code>MarketDataRequest</code> value
     * @return a <code>String</code> value containing the request ID
     */
    @ApiResponses(value={@ApiResponse(responseCode="200",
                                      description="Successfully executed a market data request",
                                      content=@io.swagger.v3.oas.annotations.media.Content(examples={@ExampleObject(name="marketDataRequestId",
                                                                                                                    summary="Market Data Request UUID",
                                                                                                                    description="Uniquely identifies a market data request",
                                                                                                                    value="this-is-my-market-data-request-42")})),
                         @ApiResponse(responseCode="400",description="Invalid parameters",content=@io.swagger.v3.oas.annotations.media.Content)})
    @Operation(summary="Makes a market data request",
               description="Makes a market data request which relies on the caller to make subsequent poll requests to retrieve events using the market data request id")
    @RequestMapping(value="/matp/marketData/marketDataRequest",method=RequestMethod.POST,produces={ MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE })
    public String requestMarketData(@Parameter(description="market data request object",allowEmptyValue=false)
                                    @RequestParam(name="request",required=true)SimpleMarketDataRequest inRequest)
    {
        throw new UnsupportedOperationException();
    }
    /**
     * Subscribe to generated events.
     *
     * @return a <code>Flux&lt;ServerSentEvent&lt;Event&gt;&gt;</code> value
     */
    @ApiResponses(value={@ApiResponse(responseCode="200",description="Successfully executed a market data request",
                  content=@io.swagger.v3.oas.annotations.media.Content(mediaType=MediaType.TEXT_EVENT_STREAM_VALUE,
                                                                       schema=@Schema(implementation=SimpleMarketDataEvent.class))),
                         @ApiResponse(responseCode="400",description="Invalid parameters",content=@io.swagger.v3.oas.annotations.media.Content)})
    @RequestMapping(value="/matp/marketData/marketDataRequestStream",method=RequestMethod.POST,produces={MediaType.TEXT_EVENT_STREAM_VALUE})
    @Operation(summary="Makes a market data request and receives market data events as server-stream-events",
               description="Makes a market data request and sets up to receive subsequent market data events as server-stream-events")
    public Flux<ServerSentEvent<SimpleMarketDataEvent>> requestMarketDataStream(@Parameter(description="market data request object",allowEmptyValue=false)
                                                                                @RequestParam(name="request",required=true)SimpleMarketDataRequest inRequest)
    {
        return fluxProcessor.map(e -> ServerSentEvent.builder(e).build());
    }
    /**
     * Cancels a market data request.
     *
     * @param inRequestId a <code>String</code> value
     */
    @ApiResponses(value={@ApiResponse(responseCode="200",description="Successfully canceled market data request"),
                         @ApiResponse(responseCode="400",description="Invalid parameters",content=@io.swagger.v3.oas.annotations.media.Content)})
    @Operation(summary="Cancels a given market data request",
               description="Cancels a previously submitted market data request using the market data request id")
    @RequestMapping(value="/matp/marketData/cancelMarketDataRequest",method=RequestMethod.PUT)
    public void cancel(@Parameter(description="market data request identifier",allowEmptyValue=false,example="this-is-my-market-data-request-42")
                       @RequestParam(name="requestId",required=true)String inRequestId)
    {
        throw new UnsupportedOperationException();
    }
    /**
     * Gets the most recent snapshot of the given market data.
     * 
     * <p>Market data must be pre-requested via {@link #request(MarketDataRequest, MarketDataListener)}.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inContent a <code>Content</code> value
     * @return a <code>Deque&lt;Event&gt;</code>
     */
    @ApiResponses(value={@ApiResponse(responseCode="200",
                                      description="Get the latest static snapshot for a particular instrument and content type",
                                      content=@io.swagger.v3.oas.annotations.media.Content(mediaType=MediaType.APPLICATION_JSON_VALUE,
                                      schema=@Schema(implementation=SimpleMarketDataEvent.class))),
                         @ApiResponse(responseCode="400",description="Invalid parameters",content=@io.swagger.v3.oas.annotations.media.Content)})
    @RequestMapping(value="/matp/marketData/marketDataSnapshot",method=RequestMethod.GET,produces={MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary="Retrieves the latest static snapshot for a particular instrument and content type",
               description="Makes a non-subscribing market data request for a particular instrument and content type")
    public Deque<SimpleMarketDataEvent> getSnapshot(@Parameter(description="instrument for which to request market data",
                                                               allowEmptyValue=false,
                                                               example="IBM")
                                                    @RequestParam(name="instrument",required=true)Instrument inInstrument,
                                                    @Parameter(description="content type for which to request market data",
                                                               allowEmptyValue=false,
                                                               example="TOP_OF_BOOK")
                                                    @RequestParam(name="content",required=true)Content inContent)
    {
        throw new UnsupportedOperationException();
    }
    /**
     * Gets a subset of the most recent snapshot available of the given market data.
     *
     * <p>Market data must be pre-requested via {@link #request(MarketDataRequest, MarketDataListener)}.</p>
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inContent a <code>Content</code> value
     * @param inPage a <code>PageRequest</code> value indicating what subset to return
     * @return a <code>CollectionPageResponse&lt;Event&gt;</code>
     */
    @ApiResponses(value={@ApiResponse(responseCode="200",
                  description="Get a page of data for the latest static snapshot for a particular instrument and content type"),
    @ApiResponse(responseCode="400",description="Invalid parameters",content=@io.swagger.v3.oas.annotations.media.Content)})
    @RequestMapping(value="/matp/marketData/marketDataSnapshotPage",method=RequestMethod.GET,produces={MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary="Retrieves the latest static snapshot for a particular instrument and content type",
               description="Makes a non-subscribing market data request for a particular instrument and content type, retrieving only a page of values")
    public CollectionPageResponse<SimpleMarketDataEvent> getSnapshot(@Parameter(description="instrument for which to request market data",
                                                                                allowEmptyValue=false,
                                                                                example="IBM")
                                                                     @RequestParam(name="instrument",required=true)Instrument inInstrument,
                                                                     @Parameter(description="content type for which to request market data",
                                                                                allowEmptyValue=false,
                                                                                example="TOP_OF_BOOK")
                                                                     @RequestParam(name="content",required=true)Content inContent,
                                                                     @Parameter(description="0-based page number",
                                                                                allowEmptyValue=false,
                                                                                example="0")
                                                                     @RequestParam(name="pageNumber",
                                                                                   required=true)Integer inPageNumber,
                                                                     @Parameter(description="non-zero page size",
                                                                                allowEmptyValue=false,
                                                                                example="20")
                                                                     @RequestParam(name="pageSize",
                                                                                   required=true)Integer inPageSize)
    {
        throw new UnsupportedOperationException();
    }
    /**
     * Gets the available capabilities of active market data providers.
     *
     * @return a <code>Set&lt;Capability&gt;</code> value
     */
    @ApiResponses(value={ @ApiResponse(responseCode="200",
                                       description="The available capabilities of all active market data providers",
                                       content=@io.swagger.v3.oas.annotations.media.Content(examples={ @ExampleObject(name="TOP_OF_BOOK"),
                                                                                                       @ExampleObject(name="LATES_TICK"),
                                                                                                       @ExampleObject(name="MARKET_STAT") })),
    @ApiResponse(responseCode="400",description="Request failed",content=@io.swagger.v3.oas.annotations.media.Content)})
    @RequestMapping(value="/matp/marketData/availableCapabilities",method=RequestMethod.GET,produces={MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary="Gets the available capabilities of all active market data providers",
               description="Gets the aggregate set of all available capabilities from all active market data providers")
    public Set<Capability> getAvailableCapability()
    {
        throw new UnsupportedOperationException();
    }
    /**
     * Gets the active providers.
     * 
     * <p>Providers may or may not be connected at this time, these are the providers known
     * to the system.</p>
     *
     * @return a <code>Set&lt;String&gt;</code> value
     */
    @ApiResponses(value={ @ApiResponse(responseCode="200",
                                       description="All market data providers, regardless of status",
                                       content=@io.swagger.v3.oas.annotations.media.Content(examples={ @ExampleObject(name="ACTIV"),
                                                                                                       @ExampleObject(name="REUTERS") })),
    @ApiResponse(responseCode="400",description="Request failed",content=@io.swagger.v3.oas.annotations.media.Content)})
    @RequestMapping(value="/matp/marketData/providers",method=RequestMethod.GET,produces={MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary="Gets the all configured market data providers",
               description="Gets all configured market data providers, may or may not be available")
    public Set<String> getProviders()
    {
        throw new UnsupportedOperationException();
    }
    public static class SimpleMarketDataEvent
    {
        /**
         * Get the eventType value.
         *
         * @return a <code>SimpleMarketDataEventType</code> value
         */
        public SimpleMarketDataEventType getType()
        {
            return type;
        }
        /**
         * Sets the eventType value.
         *
         * @param inEventType a <code>SimpleMarketDataEventType</code> value
         */
        public void setType(SimpleMarketDataEventType inEventType)
        {
            type = inEventType;
        }
        /**
         * Get the price value.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal getPrice()
        {
            return price;
        }
        /**
         * Get the size value.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal getSize()
        {
            return size;
        }
        /**
         * Get the provider value.
         *
         * @return a <code>String</code> value
         */
        public String getProvider()
        {
            return provider;
        }
        /**
         * Get the requestId value.
         *
         * @return a <code>String</code> value
         */
        public String getRequestId()
        {
            return requestId;
        }
        /**
         * Get the exchange value.
         *
         * @return a <code>String</code> value
         */
        public String getExchange()
        {
            return exchange;
        }
        /**
         * Get the quoteAction value.
         *
         * @return a <code>QuoteAction</code> value
         */
        public QuoteAction getQuoteAction()
        {
            return quoteAction;
        }
        /**
         * Get the quoteLevel value.
         *
         * @return a <code>int</code> value
         */
        public int getQuoteLevel()
        {
            return quoteLevel;
        }
        /**
         * Get the count value.
         *
         * @return a <code>int</code> value
         */
        public int getCount()
        {
            return count;
        }
        /**
         * Get the timestamp value.
         *
         * @return a <code>Date</code> value
         */
        public Date getTimestamp()
        {
            return timestamp;
        }
        /**
         * Get the eventType value.
         *
         * @return a <code>EventType</code> value
         */
        public EventType getEventType()
        {
            return eventType;
        }
        /**
         * Get the instrument value.
         *
         * @return a <code>Instrument</code> value
         */
        public Instrument getInstrument()
        {
            return instrument;
        }
        /**
         * Get the marketDataType value.
         *
         * @return a <code>SimpleMarketDataEventType</code> value
         */
        public SimpleMarketDataEventType getMarketDataType()
        {
            return type;
        }
        @Schema(description="Market data price",example="162.05")
        private BigDecimal price;
        @Schema(description="Market data size",example="200")
        private BigDecimal size;
        @Schema(description="Market data provider",example="ACTIV")
        private String provider;
        @Schema(description="Market data request UUID",example="this-is-my-market-data-request-id-42")
        private String requestId;
        @Schema(description="Market data exchange",example="Q")
        private String exchange;
        @Schema(description="Indicates how to treat this event within the context of previously received events",example="ADD")
        private QuoteAction quoteAction;
        @Schema(description="For depth-of-book quotes, indicates the price level, 1 being top-of-book, 2 being 2nd best, etc",example="4")
        private int quoteLevel;
        @Schema(description="For aggregated quotes, indicates the number of quotes at this level",example="35")
        private int count;
        @Schema(description="Indicates the time timestamp of this event as generated by the market data provider",example="20200111T141342.123Z")
        private Date timestamp;
        @Schema(description="The event-type of an event indicates whether it is a SNAPSHOT or an UPDATE and whether this event marks the completion of a multi-event message or there is more to come",example="SNAPSHOT_FINAL")
        private EventType eventType;
        @Schema(description="Indicates the provider-given symbol of this event",example="IBM")
        private Instrument instrument;
        @Schema(description="Indicates the event type of this event",example="TRADE")
        private SimpleMarketDataEventType type;
    }
    public static enum SimpleMarketDataEventType
    {
        TRADE,
        BID,
        ASK;
    }
    public static class SimpleMarketDataRequest
    {
        /*
         * 
         * @return a <code>String[]</code> value
         */
        public Set<String> getSymbols()
        {
            return symbols;
        }
       /**
        * Get the underlying symbols value.
        * 
        * @return a <code>String[]</code> value
        */
       public Set<String> getUnderlyingSymbols()
       {
           return underlyingSymbols;
       }
       /**
        * Get the provider value.
        *
        * @return a <code>String</code> value
        */
       public String getProvider()
       {
           return provider;
       }
       /**
        * Get the exchange value.
        *
        * @return a <code>String</code> value
        */
       public String getExchange()
       {
           return exchange;
       }
       /**
        * Get the content value.
        * 
        * @return a <code>Set&lt;Content&gt;</code> value
        */
       public Set<Content> getContent()
       {
           return content;
       }
       /**
        * Get the asset class value.
        *
        * @return an <code>AssetClass</code> value
        */
       public AssetClass getAssetClass()
       {
           return assetClass;
       }
       /**
        * Get the request id value.
        *
        * @return a <code>String</code> value
        */
       public String getRequestId()
       {
           return requestId;
       }
       @Schema(description="indicates the desired symbol(s) for which to retrieve market data - use symbols xor underlyingSymbols",example="IBM",requiredMode=RequiredMode.NOT_REQUIRED)
       private final Set<String> symbols = Sets.newHashSet();
       @Schema(description="indicates the desired underlying symbol(s) for which to retrieve option market data - use symbols xor underlyingSymbols",example="IBM",requiredMode=RequiredMode.NOT_REQUIRED)
       private final Set<String> underlyingSymbols = Sets.newHashSet();
       @Schema(description="indicates the desired type(s) of market data",example="LATEST_TICK,TOP_OF_BOOK",requiredMode=RequiredMode.REQUIRED)
       private final Set<Content> content = Sets.newHashSet();
       @Schema(description="optionally indicates a market data provider",example="ACTIV",requiredMode=RequiredMode.NOT_REQUIRED)
       private String provider;
       @Schema(description="optionally indicates a specific exchange",example="Q",requiredMode=RequiredMode.NOT_REQUIRED)
       private String exchange;
       @Schema(description="optionally disambiguates the asset class of the requested symbols",example="FUTURE",requiredMode=RequiredMode.NOT_REQUIRED)
       private AssetClass assetClass;
       @Schema(description="client-supplied UUID for the market data request",example="this-is-my-request-id-42",requiredMode=RequiredMode.REQUIRED)
       private String requestId;
    }
    /**
     * manages subscriptions to events
     */
    private FluxProcessor<SimpleMarketDataEvent,SimpleMarketDataEvent> fluxProcessor;
    /**
     * publishes events to subscribers
     */
    private FluxSink<SimpleMarketDataEvent> fluxSink;
    /**
     * human-readable name of this service
     */
    private String serviceName;
}
