package org.marketcetera.strategy.sample;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataClient;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.strategy.StrategyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Test strategy that demonstrates how a strategy can be built and use services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@ConfigurationProperties
@EnableAutoConfiguration
@PropertySources({@PropertySource("classpath:application.properties")})
public class TestStrategy
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        strategyClient.emitMessage(Severity.INFO,
                                   "Starting test strategy");
        MarketDataRequestBuilder marketDataRequestBuilder = MarketDataRequestBuilder.newRequest();
        marketDataRequestBuilder.withContent(Content.TOP_OF_BOOK,Content.LATEST_TICK)
            .withSymbols("AAPL,METC");
        MarketDataRequest request = marketDataRequestBuilder.create();
        marketDataRequestId = marketDataClient.request(request,
                                 new MarketDataListener() {
            /* (non-Javadoc)
             * @see org.marketcetera.marketdata.MarketDataListener#receiveMarketData(org.marketcetera.event.Event)
             */
            @Override
            public void receiveMarketData(Event inEvent)
            {
                strategyClient.emitMessage(Severity.INFO,
                                           String.valueOf(inEvent));
            }
        });
    }
    /**
     * Stop the object.
     */
    @PreDestroy
    public void stop()
    {
        strategyClient.emitMessage(Severity.INFO,
                                   "Stopping test strategy");
        if(marketDataRequestId != null) {
            try {
                marketDataClient.cancel(marketDataRequestId);
                marketDataRequestId = null;
            } catch (Exception ignored) {}
        }
    }
    /**
     * holds the market data request id
     */
    private String marketDataRequestId;
    /**
     * provides access to strategy services
     */
    @Autowired
    private StrategyClient strategyClient;
    /**
     * provides access to market data services
     */
    @Autowired
    private MarketDataClient marketDataClient;
}
