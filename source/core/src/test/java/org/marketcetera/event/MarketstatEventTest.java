package org.marketcetera.event;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.test.UnicodeData;

/* $License$ */

/**
 * Tests {@link MarketstatEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
public class MarketstatEventTest
{
    /**
     * Executed once before all tests.
     *
     * @throws Exception if an error occurs
     */
    @BeforeClass
    public static void once()
        throws Exception
    {
        LoggerConfiguration.logSetup();
    }
    /**
     * Tests the construction of the object with various values.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void construction()
        throws Exception
    {
        MSymbol[] symbols = new MSymbol[] { null, new MSymbol("metc") };
        Date[] dates = new Date[] { null, new Date(-1), new Date(0), new Date(), new Date(System.currentTimeMillis() + 1000l*60l*60l*24l*7l) };
        BigDecimal[] bigDecimals = new BigDecimal[] { null, new BigDecimal(-100), BigDecimal.ZERO, BigDecimal.TEN };
        String[] strings = new String[] { null, "", "exchange", UnicodeData.GOODBYE_JA };
        for(int bigDecimalCounter=0;bigDecimalCounter<bigDecimals.length;bigDecimalCounter++) {
            for(int dateCounter=0;dateCounter<dates.length;dateCounter++) {
                for(int symbolCounter=0;symbolCounter<symbols.length;symbolCounter++) {
                    for(int stringCounter=0;stringCounter<strings.length;stringCounter++) {
                        final MSymbol symbol = symbols[symbolCounter];
                        final Date timestamp = dates[dateCounter];
                        final BigDecimal open = bigDecimals[bigDecimalCounter];
                        final BigDecimal high = bigDecimals[bigDecimalCounter];
                        final BigDecimal low = bigDecimals[bigDecimalCounter];
                        final BigDecimal close = bigDecimals[bigDecimalCounter];
                        final BigDecimal previousClose = bigDecimals[bigDecimalCounter];
                        final BigDecimal volume = bigDecimals[bigDecimalCounter];
                        final Date closeDate = dates[dateCounter];
                        final Date previousCloseDate = dates[dateCounter];
                        final Date highTime = dates[dateCounter];
                        final Date lowTime = dates[dateCounter];
                        final String closeExchange = strings[stringCounter];
                        final String openExchange = strings[stringCounter];
                        final String highExchange = strings[stringCounter];
                        final String lowExchange = strings[stringCounter];
                        SLF4JLoggerProxy.debug(this,
                                               "{} {} {} {} {} {} {} {} {} {} {} {} {} {} {} {}",
                                               open,
                                               high,
                                               low,
                                               close,
                                               previousClose,
                                               volume,
                                               closeDate,
                                               previousCloseDate,
                                               timestamp,
                                               symbol,
                                               highTime,
                                               lowTime,
                                               closeExchange,
                                               openExchange,
                                               highExchange,
                                               lowExchange);
                        if(timestamp != null &&
                                timestamp.getTime() < 0) {
                            new ExpectedFailure<IllegalArgumentException>(null) {
                                @Override
                                protected void run()
                                    throws Exception
                                {
                                    new MarketstatEvent(symbol,
                                                        timestamp,
                                                        open,
                                                        high,
                                                        low,
                                                        close,
                                                        previousClose,
                                                        volume,
                                                        closeDate,
                                                        previousCloseDate,
                                                        highTime,
                                                        lowTime,
                                                        openExchange,
                                                        highExchange,
                                                        lowExchange,
                                                        closeExchange);
                                }
                            };
                            continue;
                        }
                        if(symbol == null ||
                                timestamp == null) {
                            new ExpectedFailure<NullPointerException>(null) {
                                @Override
                                protected void run()
                                    throws Exception
                                {
                                    new MarketstatEvent(symbol,
                                                        timestamp,
                                                        open,
                                                        high,
                                                        low,
                                                        close,
                                                        previousClose,
                                                        volume,
                                                        closeDate,
                                                        previousCloseDate,
                                                        highTime,
                                                        lowTime,
                                                        openExchange,
                                                        highExchange,
                                                        lowExchange,
                                                        closeExchange);
                                }
                            };
                            continue;
                        }
                        verifyMarketstatEvent(new MarketstatEvent(symbol,
                                                                  timestamp,
                                                                  open,
                                                                  high,
                                                                  low,
                                                                  close,
                                                                  previousClose,
                                                                  volume,
                                                                  closeDate,
                                                                  previousCloseDate,
                                                                  highTime,
                                                                  lowTime,
                                                                  openExchange,
                                                                  highExchange,
                                                                  lowExchange,
                                                                  closeExchange),
                                              open,
                                              high,
                                              low,
                                              close,
                                              previousClose,
                                              volume,
                                              closeDate,
                                              previousCloseDate,
                                              timestamp,
                                              symbol,
                                              highTime,
                                              lowTime,
                                              closeExchange,
                                              openExchange,
                                              highExchange,
                                              lowExchange);
                    }
                }
            }
        }
    }
    /**
     * Verifies the given {@link MarketstatEvent} object has the given attributes.
     *
     * @param inActualMarketstatEvent a <code>MarketstatEvent</code> value
     * @param inExpectedOpen a <code>BigDecimal</code> value
     * @param inExpectedHigh a <code>BigDecimal</code> value
     * @param inExpectedLow a <code>BigDecimal</code> value
     * @param inExpectedClose a <code>BigDecimal</code> value
     * @param inExpectedPreviousClose a <code>BigDecimal</code> value
     * @param inExpectedVolume a <code>BigDecimal</code> value
     * @param inExpectedCloseDate a <code>Date</code> value
     * @param inExpectedPreviousCloseDate a <code>Date</code> value
     * @param inExpectedTimestamp a <code>Date</code> value
     * @param inExpectedSymbol an <code>MSymbol</code> value
     * @param inExpectedHighTime a <code>Date</code> value
     * @param inExpectedLowTime a <code>Date</code> value
     * @param inExpectedCloseExchange a <code>String</code> value
     * @param inExpectedOpenExchange a <code>String</code> value
     * @param inExpectedHIghExchange a <code>String</code> value
     * @param inExpectedLowExchange a <code>String</code> value
     */
    private static void verifyMarketstatEvent(MarketstatEvent inActualMarketstatEvent,
                                              BigDecimal inExpectedOpen,
                                              BigDecimal inExpectedHigh,
                                              BigDecimal inExpectedLow,
                                              BigDecimal inExpectedClose,
                                              BigDecimal inExpectedPreviousClose,
                                              BigDecimal inExpectedVolume,
                                              Date inExpectedCloseDate,
                                              Date inExpectedPreviousCloseDate,
                                              Date inExpectedTimestamp,
                                              MSymbol inExpectedSymbol,
                                              Date inExpectedHighTime,
                                              Date inExpectedLowTime,
                                              String inExpectedCloseExchange,
                                              String inExpectedOpenExchange,
                                              String inExpectedHIghExchange,
                                              String inExpectedLowExchange)
    {
        assertEquals(inExpectedOpen,
                     inActualMarketstatEvent.getOpen());
        assertEquals(inExpectedHigh,
                     inActualMarketstatEvent.getHigh());
        assertEquals(inExpectedLow,
                     inActualMarketstatEvent.getLow());
        assertEquals(inExpectedClose,
                     inActualMarketstatEvent.getClose());
        assertEquals(inExpectedPreviousClose,
                     inActualMarketstatEvent.getPreviousClose());
        assertEquals(inExpectedVolume,
                     inActualMarketstatEvent.getVolume());
        assertEquals(inExpectedPreviousCloseDate,
                     inActualMarketstatEvent.getPreviousCloseDate());
        assertEquals(inExpectedTimestamp,
                     inActualMarketstatEvent.getTimestampAsDate());
        assertEquals(inExpectedSymbol,
                     inActualMarketstatEvent.getSymbol());
    }
}
