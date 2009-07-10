package org.marketcetera.modules.cep.system;

import org.marketcetera.core.Pair;
import org.marketcetera.core.notifications.INotification;
import org.marketcetera.event.*;
import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/* $License$ */
/**
 *
 * Set of constants naming pre-defined CEP data types
 * @author toli@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
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
    public static final String TIME         = "time";           //$NON-NLS-1$

    public static List<Pair<String, Class<?>>> REQUEST_PRECANNED_TYPES = Collections.unmodifiableList(Arrays.asList(
                    new Pair<String, Class<?>>(CEPDataTypes.MARKET_DATA, SymbolExchangeEvent.class),
                    new Pair<String, Class<?>>(CEPDataTypes.BID, BidEvent.class),
                    new Pair<String, Class<?>>(CEPDataTypes.ASK, AskEvent.class),
                    new Pair<String, Class<?>>(CEPDataTypes.TRADE, TradeEvent.class),
                    new Pair<String, Class<?>>(CEPDataTypes.REPORT, ExecutionReport.class),
                    new Pair<String, Class<?>>(CEPDataTypes.CANCEL_REJECT, OrderCancelReject.class),
                    new Pair<String, Class<?>>(CEPDataTypes.FIX_ORDER, FIXOrder.class),
                    new Pair<String, Class<?>>(CEPDataTypes.ORDER_CANCEL, OrderCancel.class),
                    new Pair<String, Class<?>>(CEPDataTypes.ORDER_REPLACE, OrderReplace.class),
                    new Pair<String, Class<?>>(CEPDataTypes.ORDER_SINGLE, OrderSingle.class),
                    new Pair<String, Class<?>>(CEPDataTypes.SUGGEST, Suggestion.class),
                    new Pair<String, Class<?>>(CEPDataTypes.NOTIFICATION, INotification.class),
                    new Pair<String, Class<?>>(CEPDataTypes.MARKET_STAT, MarketstatEvent.class),
                    new Pair<String, Class<?>>(CEPDataTypes.LOG, LogEvent.class),
                    new Pair<String, Class<?>>(CEPDataTypes.MAP, Map.class)));

}
