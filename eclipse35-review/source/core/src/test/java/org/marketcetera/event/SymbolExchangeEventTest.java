package org.marketcetera.event;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Tests {@link SymbolExchangeEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
public class SymbolExchangeEventTest
{
    /**
     * Tests the ability to construct an event extending {@link SymbolExchangeEvent}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void constructors()
        throws Exception
    {
        long[] ids = new long[] { -1, 0, 1, System.nanoTime() };
        long[] timestamps = new long[] { -1, 0, 1, System.currentTimeMillis() };
        MSymbol[] symbols = new MSymbol[] { null, new MSymbol("METC") };
        String[] exchanges = new String[] { null, "", "Q" };
        BigDecimal[] prices = new BigDecimal[] { null, new BigDecimal(-1), BigDecimal.ZERO, BigDecimal.TEN };
        BigDecimal[] sizes = new BigDecimal[] { null, new BigDecimal(-1), BigDecimal.ZERO, BigDecimal.TEN };
        for(int idCounter=0;idCounter<ids.length;idCounter++) {
            for(int timestampCounter=0;timestampCounter<timestamps.length;timestampCounter++) {
                for(int symbolCounter=0;symbolCounter<symbols.length;symbolCounter++) {
                    for(int exchangeCounter=0;exchangeCounter<exchanges.length;exchangeCounter++) {
                        for(int priceCounter=0;priceCounter<prices.length;priceCounter++) {
                            for(int sizeCounter=0;sizeCounter<sizes.length;sizeCounter++) {
                                final long id = ids[idCounter];
                                final long timestamp = timestamps[timestampCounter];
                                final MSymbol symbol = symbols[symbolCounter];
                                final String exchange = exchanges[exchangeCounter];
                                final BigDecimal price = prices[priceCounter];
                                final BigDecimal size = sizes[sizeCounter];
                                SLF4JLoggerProxy.debug(this,
                                                       "{} {} {} {} {} {}",
                                                       id,
                                                       timestamp,
                                                       symbol,
                                                       exchange,
                                                       price,
                                                       size);
                                if(id < 0 ||
                                   timestamp < 0) {
                                    new ExpectedFailure<IllegalArgumentException>(null) {
                                        @Override
                                        protected void run()
                                                throws Exception
                                        {
                                            getObject(id,
                                                      timestamp,
                                                      symbol,
                                                      exchange,
                                                      price,
                                                      size);
                                        }
                                    };
                                    continue;
                                }
                                if(symbol == null ||
                                   exchange == null ||
                                   price == null ||
                                   size == null) {
                                    new ExpectedFailure<NullPointerException>(null) {
                                        @Override
                                        protected void run()
                                                throws Exception
                                        {
                                            getObject(id,
                                                      timestamp,
                                                      symbol,
                                                      exchange,
                                                      price,
                                                      size);
                                        }
                                    };
                                    continue;
                                }
                                if(exchange.isEmpty()) {
                                    new ExpectedFailure<IllegalArgumentException>(null) {
                                        @Override
                                        protected void run()
                                                throws Exception
                                        {
                                            getObject(id,
                                                      timestamp,
                                                      symbol,
                                                      exchange,
                                                      price,
                                                      size);
                                        }
                                    };
                                    continue;
                                }
                                SymbolExchangeEvent event = getObject(id,
                                                                      timestamp,
                                                                      symbol,
                                                                      exchange,
                                                                      price,
                                                                      size);
                                assertEquals(id,
                                             event.getMessageId());
                                assertEquals(timestamp,
                                             event.getTimeMillis());
                                assertEquals(symbol,
                                             event.getSymbol());
                                assertEquals(symbol.getFullSymbol(),
                                             event.getSymbolAsString());
                                assertEquals(exchange,
                                             event.getExchange());
                                assertEquals(price,
                                             event.getPrice());
                                assertEquals(size,
                                             event.getSize());
                                assertNotNull(event.toString());
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * Creates an object to use for testing.
     *
     * <p>Subclasses may override this method to provide a different object
     * to test.
     * 
     * @param inMessageID a <code>long</code> value uniquely identifying this event
     * @param inTimestamp a <code>long</code> value containing the number of milliseconds since <code>EPOCH</code>
     *   in GMT
     * @param inSymbol an <code>MSymbol</code> value containing the symbol quoted in this event
     * @param inExchange a <code>String</code> value containing the exchange on which the quote occurred 
     * @param inPrice a <code>BigDecimal</code> value containing the price of this event
     * @param inSize a <code>BigDecimal</code> value containing the size of this event
     * @return a <code>SymbolExchange</code> value
     */
    protected SymbolExchangeEvent getObject(long inMessageID,
                                            long inTimestamp,
                                            MSymbol inSymbol,
                                            String inExchange,
                                            BigDecimal inPrice,
                                            BigDecimal inSize)
    {
        return new MockSymbolExchangeEvent(inMessageID,
                                           inTimestamp,
                                           inSymbol,
                                           inExchange,
                                           inPrice,
                                           inSize);
    }
    /**
     * Test class extending {@link SymbolExchangeEvent}.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.5.0
     */
    public static class MockSymbolExchangeEvent
        extends SymbolExchangeEvent
    {
        /**
         * Create a new MockEvent instance.
         */
        public MockSymbolExchangeEvent()
        {
            this(System.nanoTime(),
                 System.currentTimeMillis(),
                 new MSymbol("METC"),
                 "TEST",
                 BigDecimal.ONE,
                 BigDecimal.TEN);
        }
        /**
         * Create a new MockEvent instance.
         *
         * @param inMessageID a <code>long</code> value uniquely identifying this event
         * @param inTimestamp a <code>long</code> value containing the number of milliseconds since <code>EPOCH</code>
         *   in GMT
         * @param inSymbol an <code>MSymbol</code> value containing the symbol quoted in this event
         * @param inExchange a <code>String</code> value containing the exchange on which the quote occurred 
         * @param inPrice a <code>BigDecimal</code> value containing the price of this event
         * @param inSize a <code>BigDecimal</code> value containing the size of this event
         */
        public MockSymbolExchangeEvent(long inMessageID,
                                       long inTimestamp,
                                       MSymbol inSymbol,
                                       String inExchange,
                                       BigDecimal inPrice,
                                       BigDecimal inSize)
        {
            super(inMessageID,
                  inTimestamp,
                  inSymbol,
                  inExchange,
                  inPrice,
                  inSize);
        }
        private static final long serialVersionUID = 1L;
    }
}
