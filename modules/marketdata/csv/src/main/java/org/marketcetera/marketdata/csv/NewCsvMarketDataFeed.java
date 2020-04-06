package org.marketcetera.marketdata.csv;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.EnumSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.marketdata.AbstractManagedSubscriptionMarketDataFeed;
import org.marketcetera.marketdata.AssetClass;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * Provides market data read from one or more CSV files.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
@EnableAutoConfiguration
public class NewCsvMarketDataFeed
        extends AbstractManagedSubscriptionMarketDataFeed
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.NewAbstractMarketDataFeed#doGetCapabilities()
     */
    @Override
    protected Set<Capability> doGetCapabilities()
    {
        return capabilities;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.NewAbstractMarketDataFeed#doGetAssetClasses()
     */
    @Override
    protected Set<AssetClass> doGetAssetClasses()
    {
        return supportedAssetClasses;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.NewAbstractMarketDataFeed#getProviderName()
     */
    @Override
    protected String getProviderName()
    {
        return PROVIDER;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.NewAbstractMarketDataFeed#onStart()
     */
    @Override
    protected void onStart()
            throws Exception
    {
        super.onStart();
        startTimer = new Timer(serviceName + "-PlaybackThread");
        SLF4JLoggerProxy.info(this,
                              "{} scheduling market data replay to start in {}ms",
                              this,
                              config.getStartDelay());
        startTimer.schedule(new TimerTask() {
            @Override
            public void run()
            {
                abortReplay = false;
                executeMarketDataReplay();
            }},config.getStartDelay());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.NewAbstractMarketDataFeed#onStop()
     */
    @Override
    protected void onStop()
            throws Exception
    {
        try {
            if(startTimer != null) {
                abortReplay = true;
                startTimer.cancel();
                startTimer = null;
            }
        } catch (Exception ignored) {}
        super.onStop();
    }
    private void executeMarketDataReplay()
    {
        // replay the market data files in the order specified by the config
        for(File marketdataFile : config.getMarketdataFiles()) {
            if(abortReplay) {
                SLF4JLoggerProxy.info(this,
                                      "{} aborted market data replay",
                                      this);
                return;
            }
            SLF4JLoggerProxy.info(this,
                                  "{} processing market data file {}",
                                  this,
                                  marketdataFile);
            int lineNumber = 1;
            try {
                Reader marketdataFileReader = new FileReader(marketdataFile);
                Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(marketdataFileReader);
                for(CSVRecord record : records) {
                    if(abortReplay) {
                        SLF4JLoggerProxy.info(this,
                                              "{} aborted market data replay",
                                              this);
                        return;
                    }
                    for(String field : record) {
                        
                    }
                    lineNumber += 1;
                }
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      "{} could not read '{}' line {} because: '{}', skipping",
                                      this,
                                      marketdataFile.getName(),
                                      lineNumber,
                                      ExceptionUtils.getRootCauseMessage(e));
            }
        }
    }
    /**
     * indicates that the market data replay should be aborted
     */
    private boolean abortReplay;
    /**
     * timer that controls the start of the market data replay
     */
    private Timer startTimer;
    /**
     * the event translator to use
     */
    @Autowired
    private CSVFeedEventTranslator eventTranslator;
    /**
     * indicates how the feed is supposed to work
     */
    @Autowired
    private CSVFeedConfiguration config;
    /**
     * name uniquely identifying this market data feed
     */
    public static final String PROVIDER = "csv";
    /**
     * static set of asset classes
     */
    private static final Set<AssetClass> supportedAssetClasses = EnumSet.of(AssetClass.CURRENCY);
    /**
     * static set of capabilities
     */
    private static final Set<Capability> capabilities = EnumSet.of(Capability.EVENT_BOUNDARY,Capability.LATEST_TICK,Capability.MARKET_STAT,Capability.TOP_OF_BOOK);
}
