/**
 * Provides a market data adapter that can read the historical market data from a CSV file (similar to the CSV Emitter
 * module).
 * 
 * <p>There is no assumed order or format for the CSV file.  Instead, users should implement a subclass of {@link CSVFeedEventTranslator}
 * that knows how to parse the data according to the particulars of a given CSV file.  The subclass can be compiled and placed on the classpath
 * or an instantiated object can be constructed and passed to the {@link CSVFeedCredentials} constructor.</p>
 * 
 * <p>To use the <code>CSVFeed</code>, create a {@link MarketDataRequest} and pass it to the feed similarly to how other feeds process
 * market data requests.  This can be via the Strategy API, in which case the request should use 
 * {@link org.marketcetera.marketdata.MarketDataRequestBuilder#withProvider(String)} and specify {@link CSVFeedModuleFactory#getProviderDescription()}
 * or by using the module framework and passing a <code>MarketDataRequest</code> to {@link CSVFeedModule#requestData(org.marketcetera.module.DataRequest, org.marketcetera.module.DataEmitterSupport)}.</p>
 * 
 * <p>To specify a file to read from, pass the absolute or relative path of the file as a symbol in a market data request with
 * {@link org.marketcetera.marketdata.MarketDataRequestBuilder#withSymbols(String)}.  Each "symbol" in this context should be a distinct file
 * name.  All files will be read more or less simultaneously and the lines passed to the event translator described above.  The market data
 * request will stop when all files have been read.</p>
 * 
 * <p>One should set the Esper CEP module to <em>external time</em> if using this module to replay historical data.</p>
 */
package org.marketcetera.marketdata.csv;