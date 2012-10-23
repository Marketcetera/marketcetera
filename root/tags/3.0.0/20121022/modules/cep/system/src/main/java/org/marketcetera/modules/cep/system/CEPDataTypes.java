package org.marketcetera.modules.cep.system;

import java.util.*;

import org.marketcetera.core.Pair;
import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.event.*;
import org.marketcetera.core.trade.*;

/* $License$ */
/**
 *
 * Set of constants naming pre-defined CEP data types
 * @version $Id: CEPDataTypes.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
public class CEPDataTypes {
    public static final String MARKET_DATA  = "mdata";          //$NON-NLS-1$
    public static final String BID          = "bid";            //$NON-NLS-1$
    public static final String ASK          = "ask";            //$NON-NLS-1$
    public static final String TRADE        = "trade";          //$NON-NLS-1$
    public static final String REPORT       = "report";         //$NON-NLS-1$
    public static final String CANCEL_REJECT = "cancelReject";  //$NON-NLS-1$
    public static final String ORDER_SINGLE = "orderSingle";    //$NON-NLS-1$
    public static final String ORDER_REPLACE= "orderReplace";   //$NON-NLS-1$
    public static final String ORDER_CANCEL = "orderCancel";    //$NON-NLS-1$
    public static final String FIX_ORDER    = "fixOrder";       //$NON-NLS-1$
    public static final String SUGGEST      = "suggest";        //$NON-NLS-1$
    public static final String NOTIFICATION = "notif";          //$NON-NLS-1$
    public static final String MAP          = "map";            //$NON-NLS-1$
    public static final String TIME_CARRIER = "timeCarrier";    //$NON-NLS-1$
    public static final String MARKET_STAT = "marketstat";    //$NON-NLS-1$
    public static final String LOG = "log";    //$NON-NLS-1$
    public static final List<Pair<String,Class<?>>> REQUEST_PRECANNED_TYPES;
    static
    {
        List<Pair<String,Class<?>>> values = new ArrayList<Pair<String,Class<?>>>();
        values.add(new Pair<String,Class<?>>(CEPDataTypes.MARKET_DATA,
                                             MarketDataEvent.class));
        values.add(new Pair<String,Class<?>>(CEPDataTypes.BID,
                                             BidEvent.class));
        values.add(new Pair<String,Class<?>>(CEPDataTypes.ASK,
                                             AskEvent.class));
        values.add(new Pair<String,Class<?>>(CEPDataTypes.TRADE,
                                             TradeEvent.class));
        values.add(new Pair<String,Class<?>>(CEPDataTypes.REPORT,
                                             ExecutionReport.class));
        values.add(new Pair<String,Class<?>>(CEPDataTypes.CANCEL_REJECT,
                                             OrderCancelReject.class));
        values.add(new Pair<String,Class<?>>(CEPDataTypes.FIX_ORDER,
                                             FIXOrder.class));
        values.add(new Pair<String,Class<?>>(CEPDataTypes.ORDER_CANCEL,
                                             OrderCancel.class));
        values.add(new Pair<String,Class<?>>(CEPDataTypes.ORDER_REPLACE,
                                             OrderReplace.class));
        values.add(new Pair<String,Class<?>>(CEPDataTypes.ORDER_SINGLE,
                                             OrderSingle.class));
        values.add(new Pair<String,Class<?>>(CEPDataTypes.SUGGEST,
                                             Suggestion.class));
        values.add(new Pair<String,Class<?>>(CEPDataTypes.NOTIFICATION,
                                             INotification.class));
        values.add(new Pair<String,Class<?>>(CEPDataTypes.MARKET_STAT,
                                             MarketstatEvent.class));
        values.add(new Pair<String,Class<?>>(CEPDataTypes.LOG,
                                             LogEvent.class));
        values.add(new Pair<String,Class<?>>(CEPDataTypes.MAP,
                                             Map.class));
        REQUEST_PRECANNED_TYPES = Collections.unmodifiableList(values);
    }
}
