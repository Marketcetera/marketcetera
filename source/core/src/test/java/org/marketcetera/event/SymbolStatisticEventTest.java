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

/* $License$ */

/**
 * Tests {@link SymbolStatisticEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
public class SymbolStatisticEventTest
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
        for(int openCounter=0;openCounter<bigDecimals.length;openCounter++) {
            for(int highCounter=0;highCounter<bigDecimals.length;highCounter++) {
                for(int lowCounter=0;lowCounter<bigDecimals.length;lowCounter++) {
                    for(int closeCounter=0;closeCounter<bigDecimals.length;closeCounter++) {
                        for(int previousCloseCounter=0;previousCloseCounter<bigDecimals.length;previousCloseCounter++) {
                            for(int volumeCounter=0;volumeCounter<bigDecimals.length;volumeCounter++) {
                                for(int closeDateCounter=0;closeDateCounter<dates.length;closeDateCounter++) {
                                    for(int previousCloseDateCounter=0;previousCloseDateCounter<dates.length;previousCloseDateCounter++) {
                                        for(int timestampCounter=0;timestampCounter<dates.length;timestampCounter++) {
                                            for(int symbolCounter=0;symbolCounter<symbols.length;symbolCounter++) {
                                                final BigDecimal open = bigDecimals[openCounter];
                                                final BigDecimal high = bigDecimals[highCounter];
                                                final BigDecimal low = bigDecimals[lowCounter];
                                                final BigDecimal close = bigDecimals[closeCounter];
                                                final BigDecimal previousClose = bigDecimals[previousCloseCounter];
                                                final BigDecimal volume = bigDecimals[volumeCounter];
                                                final Date closeDate = dates[closeDateCounter];
                                                final Date previousCloseDate = dates[previousCloseDateCounter];
                                                final Date timestamp = dates[timestampCounter];
                                                final MSymbol symbol = symbols[symbolCounter];
                                                SLF4JLoggerProxy.debug(this,
                                                                       "{} {} {} {} {} {} {} {} {} {}",
                                                                       open,
                                                                       high,
                                                                       low,
                                                                       close,
                                                                       previousClose,
                                                                       volume,
                                                                       closeDate,
                                                                       previousCloseDate,
                                                                       timestamp,
                                                                       symbol);
                                                if(timestamp != null &&
                                                   timestamp.getTime() < 0) {
                                                    new ExpectedFailure<IllegalArgumentException>(null) {
                                                        @Override
                                                        protected void run()
                                                                throws Exception
                                                        {
                                                            new SymbolStatisticEvent(symbol,
                                                                                     timestamp,
                                                                                     open,
                                                                                     high,
                                                                                     low,
                                                                                     close,
                                                                                     previousClose,
                                                                                     volume,
                                                                                     closeDate,
                                                                                     previousCloseDate);                                                        }
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
                                                            new SymbolStatisticEvent(symbol,
                                                                                     timestamp,
                                                                                     open,
                                                                                     high,
                                                                                     low,
                                                                                     close,
                                                                                     previousClose,
                                                                                     volume,
                                                                                     closeDate,
                                                                                     previousCloseDate);                                                        }
                                                    };
                                                    continue;
                                                }
                                                verifySymbolStatisticEvent(new SymbolStatisticEvent(symbol,
                                                                                                    timestamp,
                                                                                                    open,
                                                                                                    high,
                                                                                                    low,
                                                                                                    close,
                                                                                                    previousClose,
                                                                                                    volume,
                                                                                                    closeDate,
                                                                                                    previousCloseDate),
                                                                           open,
                                                                           high,
                                                                           low,
                                                                           close,
                                                                           previousClose,
                                                                           volume,
                                                                           closeDate,
                                                                           previousCloseDate,
                                                                           timestamp,
                                                                           symbol);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * Verifies the given {@link SymbolStatisticEvent} object has the given attributes.
     *
     * @param inActualSymbolStatisticEvent an <code>OpenHighLowCloseVolument</code> value
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
     */
    private static void verifySymbolStatisticEvent(SymbolStatisticEvent inActualSymbolStatisticEvent,
                                                   BigDecimal inExpectedOpen,
                                                   BigDecimal inExpectedHigh,
                                                   BigDecimal inExpectedLow,
                                                   BigDecimal inExpectedClose,
                                                   BigDecimal inExpectedPreviousClose,
                                                   BigDecimal inExpectedVolume,
                                                   Date inExpectedCloseDate,
                                                   Date inExpectedPreviousCloseDate,
                                                   Date inExpectedTimestamp,
                                                   MSymbol inExpectedSymbol)
    {
        assertEquals(inExpectedOpen,
                     inActualSymbolStatisticEvent.getOpen());
        assertEquals(inExpectedHigh,
                     inActualSymbolStatisticEvent.getHigh());
        assertEquals(inExpectedLow,
                     inActualSymbolStatisticEvent.getLow());
        assertEquals(inExpectedClose,
                     inActualSymbolStatisticEvent.getClose());
        assertEquals(inExpectedPreviousClose,
                     inActualSymbolStatisticEvent.getPreviousClose());
        assertEquals(inExpectedVolume,
                     inActualSymbolStatisticEvent.getVolume());
        assertEquals(inExpectedPreviousCloseDate,
                     inActualSymbolStatisticEvent.getPreviousCloseDate());
        assertEquals(inExpectedTimestamp,
                     inActualSymbolStatisticEvent.getTimestampAsDate());
        assertEquals(inExpectedSymbol,
                     inActualSymbolStatisticEvent.getSymbol());
    }
}
