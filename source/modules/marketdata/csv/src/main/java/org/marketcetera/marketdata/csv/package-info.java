/**
 *
 * <p>Provides a market data adapter that can read the historical market data from a CSV file (similar to the CSV Emitter
 *   module).
 *  The assumed format is <em>timestamp,symbol,exchan,price,size,eventType</em> where event type can be one of {<em>BID,ASK,TRADE</em>}.
 *  The field order is driven by the {@link org.marketcetera.marketdata.csv.CSVFieldOrder} class,
 *  and the event types come from {@link org.marketcetera.marketdata.csv.EventType} enumeration.
 * </p>
 * <p>The location of the historical market data directory should be specified in the configuration property <em>dataDirectory</em>.
 *     For each subscribed symbol, the data feed will look for a corresponding <em>directory/symbol.csv</em> file.
 *     You can set an optional <em>delayInSecs</em> property specifying the delay (in seconds) between sending each market
 *     data event.
 *
 * <p>One should set the Esper CEP module to <em>external time</em> if using this module to replay historical data.</p>
 *
 * <p>Sample data file is below. Note that there is no header row.
 *     <pre>
 * 12345,GOOG,N,400,100,TRADE
 * 12346,GOOG,N,399,100,BID
 *     </pre>
 * </body>
 *
 */

package org.marketcetera.marketdata.csv;