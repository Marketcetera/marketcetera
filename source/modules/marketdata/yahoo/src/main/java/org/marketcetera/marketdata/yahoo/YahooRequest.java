package org.marketcetera.marketdata.yahoo;

import static org.marketcetera.marketdata.Content.DIVIDEND;
import static org.marketcetera.marketdata.Content.LATEST_TICK;
import static org.marketcetera.marketdata.Content.MARKET_STAT;
import static org.marketcetera.marketdata.Content.TOP_OF_BOOK;
import static org.marketcetera.marketdata.yahoo.YahooField.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
class YahooRequest
{
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof YahooRequest)) {
            return false;
        }
        YahooRequest other = (YahooRequest) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }
    /**
     * Create a new YahooRequest instance.
     *
     * @param inSymbol
     * @param inContent
     */
    YahooRequest(MarketDataRequest inRequest)
    {
        id = counter.incrementAndGet();
        request = inRequest;
    }
    /**
     * Get the handle value.
     *
     * @return a <code>String</code> value
     */
    String getHandle()
    {
        return handle;
    }
    /**
     * Sets the handle value.
     *
     * @param inHandle a <code>String</code> value
     */
    void setHandle(String inHandle)
    {
        handle = inHandle;
    }
    /**
     * 
     *
     *
     * @return
     */
    String getQuery()
    {
        StringBuilder query = new StringBuilder();
        query.append("?s=");
        boolean delimiterNeeded = false;
        for(String symbol : request.getSymbols()) {
            if(delimiterNeeded) {
                query.append('+');
            }
            query.append(symbol);
            if(request.getExchange() != null) {
                query.append('.').append(request.getExchange());
            }
            delimiterNeeded = true;
        }
        // request string now has all the symbols, add the fields according to content type
        query.append("&f=");
        // add fields based on content
        for(Content content : request.getContent()) {
            query.append(getFieldsFor(content));
        }
        // add fixed fields (used for every request)
        for(YahooField field : commonFields) {
            query.append(field.getCode()).append(",");
        }
        return query.toString();
    }
    /**
     * Get the request value.
     *
     * @return a <code>MarketDataRequest</code> value
     */
    MarketDataRequest getRequest()
    {
        return request;
    }
    /**
     * 
     *
     *
     * @param inContent
     * @return
     */
    private String getFieldsFor(Content inContent)
    {
        synchronized(fields) {
            if(fields.isEmpty()) {
                fields.putAll(DIVIDEND,
                              Arrays.asList(new YahooField[] { EXPECTED_DIVIDEND_DATE,DIVIDEND_PAY_DATE,DIVIDEND_YIELD } ));
                fields.putAll(LATEST_TICK,
                              Arrays.asList(new YahooField[] { LAST_TRADE_DATE,LAST_TRADE_SIZE,LAST_TRADE_PRICE_ONLY,LAST_TRADE_TIME } ));
                fields.putAll(MARKET_STAT,
                              Arrays.asList(new YahooField[] { DAY_LOW,DAY_HIGH,HIGH_LIMIT,LOW_LIMIT,DAY_RANGE,REAL_TIME_DAY_RANGE,OPEN,PREVIOUS_CLOSE,VOLUME } ));
                fields.putAll(TOP_OF_BOOK,
                              Arrays.asList(new YahooField[] { REAL_TIME_BID,REAL_TIME_ASK,BID_SIZE,ASK_SIZE } ));
            }
        }
        StringBuilder builder = new StringBuilder();
        for(YahooField field : fields.get(inContent)) {
            builder.append(field.getCode()).append(",");
        }
        return builder.toString();
    }
    /**
     * 
     */
    private static final List<YahooField> commonFields = Arrays.asList(new YahooField[] { STOCK_EXCHANGE,ERROR_INDICATION,SYMBOL } );
    /**
     * 
     */
    @GuardedBy("fields")
    private static final Multimap<Content,YahooField> fields = HashMultimap.create();
    /**
     * 
     */
    private final MarketDataRequest request;
    /**
     * 
     */
    private final int id;
    /**
     * 
     */
    private volatile String handle;
    /**
     * counter used to allocate unique identifiers
     */
    private static final AtomicInteger counter = new AtomicInteger(0);
}
