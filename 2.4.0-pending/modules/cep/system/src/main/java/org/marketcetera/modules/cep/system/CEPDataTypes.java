package org.marketcetera.modules.cep.system;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.marketcetera.core.Pair;
import org.marketcetera.core.notifications.INotification;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.LogEvent;
import org.marketcetera.event.MarketDataEvent;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.Suggestion;
import org.marketcetera.util.misc.ClassVersion;

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

    public static List<Pair<String, Class<?>>> REQUEST_PRECANNED_TYPES = Collections.unmodifiableList(Arrays.asList(
                    new Pair<String, Class<?>>(CEPDataTypes.MARKET_DATA, MarketDataEvent.class),
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
