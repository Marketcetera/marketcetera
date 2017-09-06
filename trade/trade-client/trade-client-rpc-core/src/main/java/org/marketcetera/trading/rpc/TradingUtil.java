package org.marketcetera.trading.rpc;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.admin.User;
import org.marketcetera.admin.UserFactory;
import org.marketcetera.algo.BrokerAlgo;
import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.algo.BrokerAlgoTagSpec;
import org.marketcetera.brokers.BrokerStatus;
import org.marketcetera.brokers.ClusteredBrokerStatus;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.HasClusterData;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionFactory;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.base.BaseUtil;
import org.marketcetera.symbol.SymbolResolverService;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.FIXResponse;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.HasTradeMessage;
import org.marketcetera.trade.Hierarchy;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.MutableOrderSummary;
import org.marketcetera.trade.MutableOrderSummaryFactory;
import org.marketcetera.trade.MutableReportFactory;
import org.marketcetera.trade.NewOrReplaceOrder;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderBase;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.trade.OrderCapacity;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.PositionEffect;
import org.marketcetera.trade.RelatedOrder;
import org.marketcetera.trade.Report;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.SecurityType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.UserID;
import org.marketcetera.trading.rpc.TradingRpc.BrokerStatusListenerResponse;
import org.marketcetera.trading.rpc.TradingRpc.TradeMessageListenerResponse;
import org.marketcetera.trading.rpc.TradingTypesRpc.FixMessage;

import com.google.common.collect.Maps;
import com.google.protobuf.util.Timestamps;

import quickfix.FieldNotFound;
import quickfix.Message;

/* $License$ */

/**
 * Provides common behaviors for trading RPC services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class TradingUtil
{
    /**
     * Get the RPC hierarchy value from the given value.
     *
     * @param inHierarchy a <code>Hierarchy</code> value
     * @return a <code>TradingTypesRpc.Hierarchy</code> value
     */
    public static TradingTypesRpc.Hierarchy getRpcHierarchy(Hierarchy inHierarchy)
    {
        switch(inHierarchy) {
            case Child:
                return TradingTypesRpc.Hierarchy.ChildHierarchy;
            case Flat:
                return TradingTypesRpc.Hierarchy.FlatHierarchy;
            case Parent:
                return TradingTypesRpc.Hierarchy.ParentHierarchy;
            default:
                throw new UnsupportedOperationException("Unsupported hierarchy: " + inHierarchy);
        }
    }
    /**
     * Get the RPC time in force value for the given MATP time in force value.
     *
     * @param inTimeInForce a <code>TimeInForce</code> value
     * @return a <code>TradingTypesRpc.TimeInForce</code> value
     */
    public static TradingTypesRpc.TimeInForce getRpcTimeInForce(TimeInForce inTimeInForce)
    {
        switch(inTimeInForce) {
            case AtTheClose:
                return TradingTypesRpc.TimeInForce.AtTheClose;
            case AtTheOpening:
                return TradingTypesRpc.TimeInForce.AtTheOpening;
            case Day:
                return TradingTypesRpc.TimeInForce.Day;
            case FillOrKill:
                return TradingTypesRpc.TimeInForce.FillOrKill;
            case GoodTillCancel:
                return TradingTypesRpc.TimeInForce.GoodTillCancel;
            case GoodTillCrossing:
                return TradingTypesRpc.TimeInForce.GoodTillCrossing;
            case GoodTillDate:
                return TradingTypesRpc.TimeInForce.GoodTillDate;
            case ImmediateOrCancel:
                return TradingTypesRpc.TimeInForce.ImmediateOrCancel;
            case Unknown:
                return TradingTypesRpc.TimeInForce.UnknownTimeInForce;
            default:
                throw new UnsupportedOperationException("Unsupported time in force: " + inTimeInForce);
        }
    }
    /**
     * 
     *
     *
     * @param inTimeInForce
     * @return
     */
    public static TimeInForce getTimeInForce(TradingTypesRpc.TimeInForce inTimeInForce)
    {
        switch(inTimeInForce) {
            case AtTheClose:
                return TimeInForce.AtTheClose;
            case AtTheOpening:
                return TimeInForce.AtTheOpening;
            case Day:
                return TimeInForce.Day;
            case FillOrKill:
                return TimeInForce.FillOrKill;
            case GoodTillCancel:
                return TimeInForce.GoodTillCancel;
            case GoodTillCrossing:
                return TimeInForce.GoodTillCrossing;
            case GoodTillDate:
                return TimeInForce.GoodTillDate;
            case ImmediateOrCancel:
                return TimeInForce.ImmediateOrCancel;
            case UNRECOGNIZED:
            case UnknownTimeInForce:
                return TimeInForce.Unknown;
            default:
                throw new UnsupportedOperationException("Unsupported time in force: " + inTimeInForce);
        }
    }
    /**
     * 
     *
     *
     * @param inOrderCapacity
     * @return
     */
    public static TradingTypesRpc.OrderCapacity getRpcOrderCapacity(OrderCapacity inOrderCapacity)
    {
        switch(inOrderCapacity) {
            case Agency:
                return TradingTypesRpc.OrderCapacity.Agency;
            case AgentOtherMember:
                return TradingTypesRpc.OrderCapacity.AgentOtherMember;
            case Individual:
                return TradingTypesRpc.OrderCapacity.Individual;
            case Principal:
                return TradingTypesRpc.OrderCapacity.Principal;
            case Proprietary:
                return TradingTypesRpc.OrderCapacity.Proprietary;
            case RisklessPrincipal:
                return TradingTypesRpc.OrderCapacity.RisklessPrincipal;
            case Unknown:
                return TradingTypesRpc.OrderCapacity.UnknownOrderCapacity;
            default:
                throw new UnsupportedOperationException("Unsupported order capacity: " + inOrderCapacity);
        }
    }
    /**
     * 
     *
     *
     * @param inOrderCapacity
     * @return
     */
    public static OrderCapacity getOrderCapacity(TradingTypesRpc.OrderCapacity inOrderCapacity)
    {
        switch(inOrderCapacity) {
            case Agency:
                return OrderCapacity.Agency;
            case AgentOtherMember:
                return OrderCapacity.AgentOtherMember;
            case Individual:
                return OrderCapacity.Individual;
            case Principal:
                return OrderCapacity.Principal;
            case Proprietary:
                return OrderCapacity.Proprietary;
            case RisklessPrincipal:
                return OrderCapacity.RisklessPrincipal;
            case UNRECOGNIZED:
            case UnknownOrderCapacity:
                return OrderCapacity.Unknown;
            default:
                throw new UnsupportedOperationException("Unsupported order capacity: " + inOrderCapacity);
        }
    }
    /**
     * 
     *
     *
     * @param inPositionEffect
     * @return
     */
    public static TradingTypesRpc.PositionEffect getRpcPositionEffect(PositionEffect inPositionEffect)
    {
        switch(inPositionEffect) {
            case Close:
                return TradingTypesRpc.PositionEffect.Close;
            case Open:
                return TradingTypesRpc.PositionEffect.Open;
            case Unknown:
                return TradingTypesRpc.PositionEffect.UnknownPositionEffect;
            default:
                throw new UnsupportedOperationException("Unsupported position effect: " + inPositionEffect);
        }
    }
    /**
     * 
     *
     *
     * @param inPositionEffect
     * @return
     */
    public static PositionEffect getPositionEffect(TradingTypesRpc.PositionEffect inPositionEffect)
    {
        switch(inPositionEffect) {
            case Close:
                return PositionEffect.Close;
            case Open:
                return PositionEffect.Open;
            case UNRECOGNIZED:
            case UnknownPositionEffect:
                return PositionEffect.Unknown;
            default:
                throw new UnsupportedOperationException("Unsupported position effect: " + inPositionEffect);
        }
    }
    /**
     * Get a MATP order type from an RPC order type.
     *
     * @param inOrderType a <code>TradingTypesRpc.OrderType</code> value
     * @return an <code>OrderType</code> value
     */
    public static OrderType getOrderType(TradingTypesRpc.OrderType inOrderType)
    {
        switch(inOrderType) {
            case ForexLimit:
                return OrderType.ForexLimit;
            case ForexMarket:
                return OrderType.ForexMarket;
            case ForexPreviouslyQuoted:
                return OrderType.ForexPreviouslyQuoted;
            case ForexSwap:
                return OrderType.ForexSwap;
            case Funari:
                return OrderType.Funari;
            case Limit:
                return OrderType.Limit;
            case LimitOnClose:
                return OrderType.LimitOnClose;
            case LimitOrBetter:
                return OrderType.LimitOrBetter;
            case LimitWithOrWithout:
                return OrderType.LimitWithOrWithout;
            case Market:
                return OrderType.Market;
            case MarketOnClose:
                return OrderType.MarketOnClose;
            case OnBasis:
                return OrderType.OnBasis;
            case OnClose:
                return OrderType.OnClose;
            case Pegged:
                return OrderType.Pegged;
            case PreviouslyIndicated:
                return OrderType.PreviouslyIndicated;
            case PreviouslyQuoted:
                return OrderType.PreviouslyQuoted;
            case Stop:
                return OrderType.Stop;
            case StopLimit:
                return OrderType.StopLimit;
            case UnknownOrderType:
                return OrderType.Unknown;
            case WithOrWithout:
                return OrderType.WithOrWithout;
            default:
                throw new UnsupportedOperationException("Unsupported side: " + inOrderType);
        }
    }
    /**
     * Get an order type value from an RPC order type value.
     *
     * @param inOrderType an <code>OrderType</code> value
     * @return a <code>TradingTypesRpc.OrderType</code> value
     */
    public static TradingTypesRpc.OrderType getRpcOrderType(OrderType inOrderType)
    {
        switch(inOrderType) {
            case ForexLimit:
                return TradingTypesRpc.OrderType.ForexLimit;
            case ForexMarket:
                return TradingTypesRpc.OrderType.ForexMarket;
            case ForexPreviouslyQuoted:
                return TradingTypesRpc.OrderType.ForexPreviouslyQuoted;
            case ForexSwap:
                return TradingTypesRpc.OrderType.ForexSwap;
            case Funari:
                return TradingTypesRpc.OrderType.Funari;
            case Limit:
                return TradingTypesRpc.OrderType.Limit;
            case LimitOnClose:
                return TradingTypesRpc.OrderType.LimitOnClose;
            case LimitOrBetter:
                return TradingTypesRpc.OrderType.LimitOrBetter;
            case LimitWithOrWithout:
                return TradingTypesRpc.OrderType.LimitWithOrWithout;
            case Market:
                return TradingTypesRpc.OrderType.Market;
            case MarketOnClose:
                return TradingTypesRpc.OrderType.MarketOnClose;
            case OnBasis:
                return TradingTypesRpc.OrderType.OnBasis;
            case OnClose:
                return TradingTypesRpc.OrderType.OnClose;
            case Pegged:
                return TradingTypesRpc.OrderType.Pegged;
            case PreviouslyIndicated:
                return TradingTypesRpc.OrderType.PreviouslyIndicated;
            case PreviouslyQuoted:
                return TradingTypesRpc.OrderType.PreviouslyQuoted;
            case Stop:
                return TradingTypesRpc.OrderType.Stop;
            case StopLimit:
                return TradingTypesRpc.OrderType.StopLimit;
            case Unknown:
                return TradingTypesRpc.OrderType.UnknownOrderType;
            case WithOrWithout:
                return TradingTypesRpc.OrderType.WithOrWithout;
            default:
                throw new UnsupportedOperationException("Unsupported order type: " + inOrderType);
        }
    }
    /**
     * Get a side value from an RPC side type.
     *
     * @param inSideType a <code>TradingTypesRpc.Side</code> value
     * @return a <code>Side</code> value
     */
    public static Side getSide(TradingTypesRpc.Side inSideType)
    {
        switch(inSideType) {
            case Buy:
                return Side.Buy;
            case BuyMinus:
                return Side.BuyMinus;
            case Cross:
                return Side.Cross;
            case CrossShort:
                return Side.CrossShort;
            case Sell:
                return Side.Sell;
            case SellPlus:
                return Side.SellPlus;
            case SellShort:
                return Side.SellShort;
            case SellShortExempt:
                return Side.SellShortExempt;
            case Undisclosed:
                return Side.Undisclosed;
            case UnknownSide:
                return Side.Unknown;
            default:
                throw new UnsupportedOperationException("Unsupported side value: " + inSideType);
            
        }
    }
    /**
     * Get an RPC side type from a side type.
     *
     * @param inSide a <code>Side</code> value
     * @return a <code>TradingTypesRpc.Side</code> value
     */
    public static TradingTypesRpc.Side getRpcSide(Side inSide)
    {
        switch(inSide) {
            case Buy:
                return TradingTypesRpc.Side.Buy;
            case BuyMinus:
                return TradingTypesRpc.Side.BuyMinus;
            case Cross:
                return TradingTypesRpc.Side.Cross;
            case CrossShort:
                return TradingTypesRpc.Side.CrossShort;
            case Sell:
                return TradingTypesRpc.Side.Sell;
            case SellPlus:
                return TradingTypesRpc.Side.SellPlus;
            case SellShort:
                return TradingTypesRpc.Side.SellShort;
            case SellShortExempt:
                return TradingTypesRpc.Side.SellShortExempt;
            case Undisclosed:
                return TradingTypesRpc.Side.Undisclosed;
            case Unknown:
                return TradingTypesRpc.Side.UnknownSide;
            default:
                throw new UnsupportedOperationException("Unsupported side: " + inSide);
        }
    }
    /**
     * Get a MATP security type from an RPC security type.
     *
     * @param inSecurityType a <code>TradingTypesRpc.SecurityType</code> value
     * @return an <code>SecurityType</code> value
     */
    public static SecurityType getSecurityType(TradingTypesRpc.SecurityType inSecurityType)
    {
        switch(inSecurityType) {
            case CommonStock:
                return org.marketcetera.trade.SecurityType.CommonStock;
            case ConvertibleBond:
                return org.marketcetera.trade.SecurityType.ConvertibleBond;
            case Currency:
                return org.marketcetera.trade.SecurityType.Currency;
            case Future:
                return org.marketcetera.trade.SecurityType.Future;
            case Option:
                return org.marketcetera.trade.SecurityType.Option;
            case UNRECOGNIZED:
            case UnknownSecurityType:
                return org.marketcetera.trade.SecurityType.Unknown;
            default:
                throw new UnsupportedOperationException("Unsupported security type: " + inSecurityType);
        }
    }
    /**
     * Get an order type value from an RPC order type value.
     *
     * @param inSecurityType an <code>SecurityType</code> value
     * @return a <code>TradingTypesRpc.SecurityType</code> value
     */
    public static TradingTypesRpc.SecurityType getRpcSecurityType(SecurityType inSecurityType)
    {
        switch(inSecurityType) {
            case CommonStock:
                return TradingTypesRpc.SecurityType.CommonStock;
            case ConvertibleBond:
                return TradingTypesRpc.SecurityType.ConvertibleBond;
            case Currency:
                return TradingTypesRpc.SecurityType.Currency;
            case Future:
                return TradingTypesRpc.SecurityType.Future;
            case Option:
                return TradingTypesRpc.SecurityType.Option;
            case Unknown:
                return TradingTypesRpc.SecurityType.UnknownSecurityType;
            default:
                throw new UnsupportedOperationException("Unsupported security type: " + inSecurityType);
        }
    }
    /**
     * Set the instrument from the given order on the given builder.
     *
     * @param inOrder an <code>OrderBase</code> value
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setInstrument(OrderBase inOrder,
                                     TradingTypesRpc.OrderBase.Builder inBuilder)
    {
        if(inOrder.getInstrument() == null) {
            return;
        }
        inBuilder.setInstrument(getRpcInstrument(inOrder.getInstrument()));
    }
    /**
     * Get the RPC instrument from the given instrument.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @return a <code>TradingTypesRpc.Instrument</code>value
     */
    public static TradingTypesRpc.Instrument getRpcInstrument(Instrument inInstrument)
    {
        TradingTypesRpc.Instrument.Builder instrumentBuilder = TradingTypesRpc.Instrument.newBuilder();
        instrumentBuilder.setSymbol(inInstrument.getFullSymbol());
        return instrumentBuilder.build();
    }
    /**
     * Get the instrument on the given RPC order base object.
     *
     * @param inRpcOrder a <code>TradingTypeRpc.OrderBase</code> value
     * @return an <code>Instrument</code> value
     */
    private static Instrument getInstrument(TradingTypesRpc.OrderBase inRpcOrder)
    {
        return symbolResolverService.resolveSymbol(inRpcOrder.getInstrument().getSymbol());
    }
    /**
     * Get the instrument on the given RPC order summary object.
     *
     * @param inRpcOrder a <code>TradingTypeRpc.OrderSummary</code> value
     * @return an <code>Instrument</code> value
     */
    private static Instrument getInstrument(TradingTypesRpc.OrderSummary inRpcOrderSummary)
    {
        return symbolResolverService.resolveSymbol(inRpcOrderSummary.getInstrument().getSymbol());
    }
    /**
     * Set the RPC custom fields from the given order.
     *
     * @param inOrder an <code>OrderBase</code> value
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setRpcCustomFields(OrderBase inOrder,
                                          TradingTypesRpc.OrderBase.Builder inBuilder)
    {
        if(inOrder.getCustomFields() == null || inOrder.getCustomFields().isEmpty()) {
            return;
        }
        BaseRpc.Map.Builder mapBuilder = BaseRpc.Map.newBuilder();
        BaseRpc.KeyValuePair.Builder keyValuePairBuilder = BaseRpc.KeyValuePair.newBuilder();
        for(Map.Entry<String,String> entry : inOrder.getCustomFields().entrySet()) {
            keyValuePairBuilder.setKey(entry.getKey());
            keyValuePairBuilder.setKey(entry.getValue());
            mapBuilder.addKeyValuePairs(keyValuePairBuilder.build());
            keyValuePairBuilder.clear();
        }
        inBuilder.setCustomFields(mapBuilder.build());
    }
    /**
     * Set the custom fields from the given RPC order.
     *
     * @param inRpcOrder a <code>TradingTypesRpc.OrderBase</code> value
     * @param inOrder an <code>OrderBase</code> value
     */
    public static void setCustomFields(TradingTypesRpc.OrderBase inRpcOrder,
                                       OrderBase inOrder)
    {
        if(!inRpcOrder.hasCustomFields()) {
            return;
        }
        BaseRpc.Map rpcMap = inRpcOrder.getCustomFields();
        Map<String,String> customFields = inOrder.getCustomFields();
        if(customFields == null) {
            customFields = Maps.newTreeMap();
        }
        for(BaseRpc.KeyValuePair rpcKeyValuePair : rpcMap.getKeyValuePairsList()) {
            customFields.put(rpcKeyValuePair.getKey(),
                             rpcKeyValuePair.getValue());
        }
        inOrder.setCustomFields(customFields);
    }
    /**
     * Set the account value.
     *
     * @param inOrder an <code>OrderBase</code> value
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setAccount(OrderBase inOrder,
                                  TradingTypesRpc.OrderBase.Builder inBuilder)
    {
        String value = StringUtils.trimToNull(inOrder.getAccount());
        if(value == null) {
            return;
        }
        inBuilder.setAccount(value);
    }
    /**
     * Set the account from value the given trade message on the given builder.
     *
     * @param inExecutionReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setAccount(ExecutionReport inExecutionReport,
                                  TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        String value = StringUtils.trimToNull(inExecutionReport.getAccount());
        if(value == null) {
            return;
        }
        inBuilder.setAccount(value);
    }
    /**
     * Set the user ID from value the given trade message on the given builder.
     *
     * @param inReportBase a <code>ReportBase</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setUserId(ReportBase inReportBase,
                                 TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReportBase.getActorID() == null) {
            return;
        }
        inBuilder.setUser(String.valueOf(inReportBase.getActorID()));
    }
    /**
     * Set the user ID from value the given trade message on the given builder.
     *
     * @param inReport a <code>FIXResponse</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setUserId(FIXResponse inReport,
                                 TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getActorID() == null) {
            return;
        }
        inBuilder.setUser(String.valueOf(inReport.getActorID()));
    }
    /**
     *
     *
     * @param inOrder
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setText(OrderBase inOrder,
                               TradingTypesRpc.OrderBase.Builder inBuilder)
    {
        String value = StringUtils.trimToNull(inOrder.getText());
        if(value == null) {
            return;
        }
        inBuilder.setText(value);
    }
    /**
     *
     *
     * @param inRpcOrder
     * @return
     */
    public static String getText(TradingTypesRpc.OrderBase inRpcOrder)
    {
        return StringUtils.trimToNull(inRpcOrder.getText());
    }
    /**
     *
     *
     * @param inOrder
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setBrokerId(OrderBase inOrder,
                                   TradingTypesRpc.OrderBase.Builder inBuilder)
    {
        if(inOrder.getBrokerID() == null) {
            return;
        }
        String value = StringUtils.trimToNull(inOrder.getBrokerID().getValue());
        inBuilder.setBrokerId(value);
    }
    /**
    *
    *
    * @param inOrder
    * @param inBuilder
    */
   public static void setBrokerId(FIXOrder inOrder,
                                  TradingTypesRpc.FIXOrder.Builder inBuilder)
   {
       if(inOrder.getBrokerID() == null) {
           return;
       }
       String value = StringUtils.trimToNull(inOrder.getBrokerID().getValue());
       inBuilder.setBrokerId(value);
   }
    /**
     *
     *
     * @param inOrder
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setOrderId(OrderBase inOrder,
                                  TradingTypesRpc.OrderBase.Builder inBuilder)
    {
        if(inOrder.getOrderID() == null) {
            return;
        }
        String value = StringUtils.trimToNull(inOrder.getOrderID().getValue());
        inBuilder.setOrderId(value);
    }
    /**
     *
     *
     * @param inRpcOrder
     * @return
     */
    public static OrderID getOrderId(TradingTypesRpc.OrderBase inRpcOrder)
    {
        return new OrderID(inRpcOrder.getOrderId());
    }
    /**
     *
     *
     * @param inOrder
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setQuantity(OrderBase inOrder,
                                   TradingTypesRpc.OrderBase.Builder inBuilder)
    {
        if(inOrder.getQuantity() == null) {
            return;
        }
        inBuilder.setQuantity(BaseUtil.getQtyValueFrom(inOrder.getQuantity()));
    }
    /**
     *
     *
     * @param inRpcOrder
     * @return
     */
    public static BigDecimal getQuantity(TradingTypesRpc.OrderBase inRpcOrder)
    {
        return BaseUtil.getScaledQuantity(inRpcOrder.getQuantity());
    }
    /**
     *
     *
     * @param inRpcOrder
     * @return
     */
    public static BigDecimal getPrice(TradingTypesRpc.OrderBase inRpcOrder)
    {
        return BaseUtil.getScaledQuantity(inRpcOrder.getPrice());
    }
    /**
     * Get the display quantity of the given order.
     *
     * @param inRpcOrder a <code>TradingTypesRpc.OrderBase</code> value
     * @return a <code>BigDecimal</code> value or <code>null</code>
     */
    public static BigDecimal getDisplayQuantity(TradingTypesRpc.OrderBase inRpcOrder)
    {
        return BaseUtil.getScaledQuantity(inRpcOrder.getDisplayQuantity());
    }
    /**
     *
     *
     * @param inOrder
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setSide(OrderBase inOrder,
                               TradingTypesRpc.OrderBase.Builder inBuilder)
    {
        if(inOrder.getSide() == null) {
            return;
        }
        inBuilder.setSide(getRpcSide(inOrder.getSide()));
    }
    /**
     *
     *
     * @param inRpcOrder
     * @return
     */
    public static Side getSide(TradingTypesRpc.OrderBase inRpcOrder)
    {
        switch(inRpcOrder.getSide()) {
            case Buy:
                return Side.Buy;
            case BuyMinus:
                return Side.BuyMinus;
            case Cross:
                return Side.Cross;
            case CrossShort:
                return Side.CrossShort;
            case Sell:
                return Side.Sell;
            case SellPlus:
                return Side.SellPlus;
            case SellShort:
                return Side.SellShort;
            case SellShortExempt:
                return Side.SellShortExempt;
            case Undisclosed:
                return Side.Undisclosed;
            case UNRECOGNIZED:
            case UnknownSide:
                return Side.Unknown;
            default:
                throw new UnsupportedOperationException("Unsupported side: " + inRpcOrder.getSide());
            
        }
    }
    /**
     * Set the display quantity from the given order on the given builder.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setDisplayQuantity(NewOrReplaceOrder inOrder,
                                          TradingTypesRpc.OrderBase.Builder inBuilder)
    {
        if(inOrder.getDisplayQuantity() == null || BigDecimal.ZERO.compareTo(inOrder.getDisplayQuantity()) == 0) {
            return;
        }
        inBuilder.setDisplayQuantity(BaseUtil.getQtyValueFrom(inOrder.getDisplayQuantity()));
    }
    /**
     * 
     *
     *
     * @param inOrder
     * @param inRpcOrder
     */
    public static void setDisplayQuantity(NewOrReplaceOrder inOrder,
                                          TradingTypesRpc.OrderBase inRpcOrder)
    {
        if(inRpcOrder.hasDisplayQuantity()) {
            inOrder.setDisplayQuantity(BaseUtil.getScaledQuantity(inRpcOrder.getDisplayQuantity()));
        }
    }
    /**
     * Set the order ID from the given RPC order.
     *
     * @param inOrder an <code>OrderBase</code> value
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setOrderId(OrderBase inOrder,
                                  TradingTypesRpc.OrderBase inRpcOrder)
    {
        String value = StringUtils.trimToNull(inRpcOrder.getOrderId());
        if(value == null) {
            return;
        }
        inOrder.setOrderID(new OrderID(value));
    }
    /**
     * Set the original order ID from the given RPC order.
     *
     * @param inOrder a <code>RelatedOrder</code> value
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setOriginalOrderId(RelatedOrder inOrder,
                                          TradingTypesRpc.OrderBase inRpcOrder)
    {
        String value = StringUtils.trimToNull(inRpcOrder.getOriginalOrderId());
        if(value == null) {
            return;
        }
        inOrder.setOriginalOrderID(new OrderID(value));
    }
    /**
     * Set the broker order ID from the given RPC order.
     *
     * @param inOrder a <code>RelatedOrder</code> value
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setBrokerOrderId(RelatedOrder inOrder,
                                        TradingTypesRpc.OrderBase inRpcOrder)
    {
        String value = StringUtils.trimToNull(inRpcOrder.getBrokerOrderId());
        if(value == null) {
            return;
        }
        inOrder.setBrokerOrderID(value);
    }
    /**
     * Set the broker ID from the given RPC order.
     *
     * @param inOrder an <code>OrderBase</code> value
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setBrokerId(OrderBase inOrder,
                                   TradingTypesRpc.OrderBase inRpcOrder)
    {
        String value = StringUtils.trimToNull(inRpcOrder.getBrokerId());
        if(value == null) {
            return;
        }
        inOrder.setBrokerID(new BrokerID(value));
    }
    /**
     * Set the instrument from the given RPC order.
     *
     * @param inOrder an <code>OrderBase</code> value
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setInstrument(OrderBase inOrder,
                                     TradingTypesRpc.OrderBase inRpcOrder)
    {
        if(inRpcOrder.hasInstrument()) {
            inOrder.setInstrument(getInstrument(inRpcOrder));
        }
    }
    /**
     * Set the account from the given RPC order.
     *
     * @param inOrder an <code>OrderBase</code> value
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setAccount(OrderBase inOrder,
                                  TradingTypesRpc.OrderBase inRpcOrder)
    {
        inOrder.setAccount(StringUtils.trimToNull(inRpcOrder.getAccount()));
    }
    /**
     * Set the text from the given RPC order.
     *
     * @param inOrder an <code>OrderBase</code> value
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setText(OrderBase inOrder,
                               TradingTypesRpc.OrderBase inRpcOrder)
    {
        inOrder.setText(StringUtils.trimToNull(inRpcOrder.getText()));
    }
    /**
     * Set the execution destination from the given RPC order.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setExecutionDestination(NewOrReplaceOrder inOrder,
                                               TradingTypesRpc.OrderBase inRpcOrder)
    {
        inOrder.setExecutionDestination(StringUtils.trimToNull(inRpcOrder.getExecutionDestination()));
    }
    /**
     * Set the execution destination from the given RPC order.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setExecutionDestination(NewOrReplaceOrder inOrder,
                                               TradingTypesRpc.OrderBase.Builder inBuilder)
    {
        String value = StringUtils.trimToNull(inOrder.getExecutionDestination());
        if(value == null) {
            return;
        }
        inBuilder.setExecutionDestination(value);
    }
    /**
     * Set the peg-to-midpoint value from the given RPC order.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setPegToMidpoint(NewOrReplaceOrder inOrder,
                                        TradingTypesRpc.OrderBase inRpcOrder)
    {
        inOrder.setPegToMidpoint(inRpcOrder.getPegToMidpoint());
    }
    /**
     * Set the broker algo from the given RPC order.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setBrokerAlgo(NewOrReplaceOrder inOrder,
                                     TradingTypesRpc.OrderBase inRpcOrder)
    {
        if(inRpcOrder.hasBrokerAlgo()) {
            inOrder.setBrokerAlgo(getBrokerAlgo(inRpcOrder).orElse(null));
        }
    }
    /**
     * Set the price from the given RPC order.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inRpcOrder a <code>TradingTypesRpc.OrderBase</code> value
     */
    public static void setPrice(NewOrReplaceOrder inOrder,
                                TradingTypesRpc.OrderBase inRpcOrder)
    {
        if(inRpcOrder.hasPrice()) {
            inOrder.setPrice(BaseUtil.getScaledQuantity(inRpcOrder.getPrice()));
        }
    }
    /**
     * Set the quantity from the given RPC order.
     *
     * @param inOrder an <code>OrderBase</code> value
     * @param inRpcOrder a <code>TradingTypesRpc.OrderBase</code> value
     */
    public static void setQuantity(OrderBase inOrder,
                                   TradingTypesRpc.OrderBase inRpcOrder)
    {
        if(inRpcOrder.hasQuantity()) {
            inOrder.setQuantity(BaseUtil.getScaledQuantity(inRpcOrder.getQuantity()));
        }
    }
    /**
     * Set the order capacity from the given order on the given builder.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setOrderCapacity(NewOrReplaceOrder inOrder,
                                        TradingTypesRpc.OrderBase.Builder inBuilder)
    {
        if(inOrder.getOrderCapacity() == null) {
            return;
        }
        inBuilder.setOrderCapacity(getRpcOrderCapacity(inOrder.getOrderCapacity()));
    }
    /**
     *
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setOrderType(NewOrReplaceOrder inOrder,
                                    TradingTypesRpc.OrderBase.Builder inBuilder)
    {
        if(inOrder.getOrderType() == null) {
            return;
        }
        inBuilder.setOrderType(getRpcOrderType(inOrder.getOrderType()));
    }
    /**
     *
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setPositionEffect(NewOrReplaceOrder inOrder,
                                         TradingTypesRpc.OrderBase.Builder inBuilder)
    {
        if(inOrder.getPositionEffect() == null) {
            return;
        }
        inBuilder.setPositionEffect(getRpcPositionEffect(inOrder.getPositionEffect()));
    }
    /**
     *
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setPrice(NewOrReplaceOrder inOrder,
                                TradingTypesRpc.OrderBase.Builder inBuilder)
    {
        if(inOrder.getPrice() == null) {
            return;
        }
        inBuilder.setPrice(BaseUtil.getQtyValueFrom(inOrder.getPrice()));
    }
    /**
     * Set the time in force on the given RPC builder from the given order.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setTimeInForce(NewOrReplaceOrder inOrder,
                                      TradingTypesRpc.OrderBase.Builder inBuilder)
    {
        if(inOrder.getTimeInForce() == null) {
            return;
        }
        inBuilder.setTimeInForce(getRpcTimeInForce(inOrder.getTimeInForce()));
    }
    /**
     * Set the time in force value on the given MATP order from the given RPC order.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inRpcOrder a <code>TradingTypesRpc.OrderBase</code> value
     */
    public static void setTimeInForce(NewOrReplaceOrder inOrder,
                                      TradingTypesRpc.OrderBase inRpcOrder)
    {
        TimeInForce matpTif = getTimeInForce(inRpcOrder.getTimeInForce());
        if(matpTif == null || matpTif == TimeInForce.Unknown) {
            return;
        }
        inOrder.setTimeInForce(matpTif);
    }
    /**
     * Set the position effect on the given MATP order from the given RPC order.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inRpcOrder a <code>TradingTypesRpc.OrderBase</code> value
     */
    public static void setPositionEffect(NewOrReplaceOrder inOrder,
                                         TradingTypesRpc.OrderBase inRpcOrder)
    {
        PositionEffect matpPositionEffect = getPositionEffect(inRpcOrder.getPositionEffect());
        if(matpPositionEffect == null || matpPositionEffect == PositionEffect.Unknown) {
            return;
        }
        inOrder.setPositionEffect(matpPositionEffect);
    }
    /**
     * Set the order type on the given MATP order from the given RPC order.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inRpcOrder a <code>TradingTypesRpc.OrderBase</code> value
     */
    public static void setOrderType(NewOrReplaceOrder inOrder,
                                    TradingTypesRpc.OrderBase inRpcOrder)
    {
        OrderType matpOrderType = getOrderType(inRpcOrder.getOrderType());
        if(matpOrderType == null || matpOrderType == OrderType.Unknown) {
            return;
        }
        inOrder.setOrderType(matpOrderType);
    }
    /**
     * Set the order type on the given MATP order from the given RPC order.
     *
     * @param inOrder an <code>OrderBase</code> value
     * @param inRpcOrder a <code>TradingTypesRpc.OrderBase</code> value
     */
    public static void setSide(OrderBase inOrder,
                               TradingTypesRpc.OrderBase inRpcOrder)
    {
        Side matpSide = getSide(inRpcOrder.getSide());
        if(matpSide == null || matpSide == Side.Unknown) {
            return;
        }
        inOrder.setSide(matpSide);
    }
    /**
     * Set the order capacity on the given MATP order from the given RPC order.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inRpcOrder a <code>TradingTypesRpc.OrderBase</code> value
     */
    public static void setOrderCapacity(NewOrReplaceOrder inOrder,
                                        TradingTypesRpc.OrderBase inRpcOrder)
    {
        OrderCapacity matpOrderCapacity = getOrderCapacity(inRpcOrder.getOrderCapacity());
        if(matpOrderCapacity == null || matpOrderCapacity == OrderCapacity.Unknown) {
            return;
        }
        inOrder.setOrderCapacity(matpOrderCapacity);
    }
    /**
     *
     *
     * @param inOrder
     * @param inBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setOriginalOrderId(RelatedOrder inOrder,
                                          TradingTypesRpc.OrderBase.Builder inBuilder)
    {
        if(inOrder.getOriginalOrderID() == null) {
            return;
        }
        String value = StringUtils.trimToNull(inOrder.getOriginalOrderID().getValue());
        inBuilder.setOriginalOrderId(value);
    }
    /**
     * Get the broker ID from the given source.
     *
     * @param inObject an <code>Object</code> value
     * @return an <code>Optional&lt;BrokerID&gt;</code> value
     */
    public static Optional<BrokerID> getBrokerId(Object inObject)
    {
        BrokerID brokerId = null;
        if(inObject instanceof TradingTypesRpc.FIXOrder) {
            String value = StringUtils.trimToNull(((TradingTypesRpc.FIXOrder)inObject).getBrokerId());
            if(value != null) {
                brokerId = new BrokerID(value);
            }
        } else if(inObject instanceof TradingTypesRpc.OrderBase) {
            String value = StringUtils.trimToNull(((TradingTypesRpc.OrderBase)inObject).getBrokerId());
            if(value != null) {
                brokerId = new BrokerID(value);
            }
        } else if(inObject instanceof TradingTypesRpc.TradeMessage) {
            String value = StringUtils.trimToNull(((TradingTypesRpc.TradeMessage)inObject).getBrokerId());
            if(value != null) {
                brokerId = new BrokerID(value);
            }
        } else if(inObject instanceof TradingTypesRpc.OrderSummary) {
            String value = StringUtils.trimToNull(((TradingTypesRpc.OrderSummary)inObject).getBrokerId());
            if(value != null) {
                brokerId = new BrokerID(value);
            }
        }
        if(brokerId == null) {
            return Optional.empty();
        }
        return Optional.of(brokerId);
    }
    /**
     * Get the MATP order value for the given RPC order.
     *
     * @param inRpcOrder a <code>TradingTypesRpc.Order</code> value
     * @return an <code>Order</code> value
     */
    public static Order getOrder(TradingTypesRpc.Order inRpcOrder)
    {
        BrokerID brokerId = getBrokerId(inRpcOrder).orElse(null);
        TradingTypesRpc.OrderBase rpcOrderBase = null;
        if(inRpcOrder.hasOrderBase()) {
            rpcOrderBase = inRpcOrder.getOrderBase();
        }
        // the trade order types overlap to a degree and are a bit confusing
        OrderBase orderBase = null;
        RelatedOrder relatedOrder = null;
        NewOrReplaceOrder newOrReplaceOrder = null;
        switch(inRpcOrder.getMatpOrderType()) {
            case FIXOrderType:
                return Factory.getInstance().createOrder(getFixMessage(inRpcOrder.getFixOrder().getMessage()),
                                                         brokerId);
            case OrderCancelType:
                OrderCancel orderCancel = Factory.getInstance().createOrderCancel(null);
                orderBase = orderCancel;
                relatedOrder = orderCancel;
                break;
            case OrderReplaceType:
                OrderReplace orderReplace = Factory.getInstance().createOrderReplace(null);
                orderBase = orderReplace;
                relatedOrder = orderReplace;
                newOrReplaceOrder = orderReplace;
                break;
            case OrderSingleType:
                OrderSingle orderSingle = Factory.getInstance().createOrderSingle();
                orderBase = orderSingle;
                newOrReplaceOrder = orderSingle;
                break;
            case UNRECOGNIZED:
            default:
                throw new UnsupportedOperationException("Unsupported order type: " + inRpcOrder);
        }
        setAccount(orderBase,
                   rpcOrderBase);
        setBrokerId(orderBase,
                    rpcOrderBase);
        setCustomFields(rpcOrderBase,
                        orderBase);
        setInstrument(orderBase,
                      rpcOrderBase);
        setOrderId(orderBase,
                   rpcOrderBase);
        setQuantity(orderBase,
                    rpcOrderBase);
        setSide(orderBase,
                rpcOrderBase);
        setText(orderBase,
                rpcOrderBase);
        if(relatedOrder != null) {
            setBrokerOrderId(relatedOrder,
                               rpcOrderBase);
            setOriginalOrderId(relatedOrder,
                               rpcOrderBase);
        }
        if(newOrReplaceOrder != null) {
            setBrokerAlgo(newOrReplaceOrder,
                          rpcOrderBase);
            setDisplayQuantity(newOrReplaceOrder,
                               rpcOrderBase);
            setExecutionDestination(newOrReplaceOrder,
                                    rpcOrderBase);
            setOrderCapacity(newOrReplaceOrder,
                             rpcOrderBase);
            setOrderType(newOrReplaceOrder,
                         rpcOrderBase);
            setPegToMidpoint(newOrReplaceOrder,
                             rpcOrderBase);
            setPositionEffect(newOrReplaceOrder,
                              rpcOrderBase);
            setPrice(newOrReplaceOrder,
                     rpcOrderBase);
            setTimeInForce(newOrReplaceOrder,
                           rpcOrderBase);
        }
        return orderBase;
    }
    /**
     * Get the trade message value from the given RPC message.
     *
     * @param inResponse a <code>TradeMessageListenerResponse</code> value
     * @return a <code>TradeMessage</code> value
     */
    public static TradeMessage getTradeMessage(TradeMessageListenerResponse inResponse)
    {
        if(!inResponse.hasTradeMessage()) {
            throw new UnsupportedOperationException();
        }
        return getTradeMessage(inResponse.getTradeMessage());
    }
    /**
     * Get the trade message value from the given RPC trade message.
     *
     * @param inReport a <code>TradingTypesRpc.TradeMessage</code> value
     * @return a <code>TradeMessage</code> value
     */
    public static TradeMessage getTradeMessage(TradingTypesRpc.TradeMessage inRpcTradeMessage)
    {
        TradeMessage tradeMessage = null;
        Message fixMessage = getFixMessage(inRpcTradeMessage.getMessage());
        BrokerID brokerId = getBrokerId(inRpcTradeMessage).orElse(null);
        Originator originator = getOriginator(inRpcTradeMessage);
        UserID userId = getUserId(inRpcTradeMessage).orElse(null);
        switch(inRpcTradeMessage.getTradeMessageType()) {
            case TradeMessageExecutionReport:
                tradeMessage = Factory.getInstance().createExecutionReport(fixMessage,
                                                                           brokerId,
                                                                           originator,
                                                                           userId,
                                                                           userId);
                break;
            case TradeMessageFixResponse:
                tradeMessage = Factory.getInstance().createFIXResponse(fixMessage,
                                                                       brokerId,
                                                                       originator,
                                                                       userId,
                                                                       userId);
                break;
            case TradeMessageOrderCancelReject:
                tradeMessage = Factory.getInstance().createOrderCancelReject(fixMessage,
                                                                             brokerId,
                                                                             originator,
                                                                             userId,
                                                                             userId);
                break;
            case UNRECOGNIZED:
            default:
                throw new UnsupportedOperationException();
        }
        return tradeMessage;
    }
    /**
     * Get the RPC trade message from the given trade message.
     *
     * @param inTradeMessage a <code>TradeMessage</code> value
     * @return a <code>TradingTypesRpc.TradeMessage</code> value
     */
    public static TradingTypesRpc.TradeMessage getRpcTradeMessage(TradeMessage inTradeMessage)
    {
        TradingTypesRpc.TradeMessage.Builder tradeMessageBuilder = TradingTypesRpc.TradeMessage.newBuilder();
        ReportBase reportBase = null;
        ExecutionReport executionReport = null;
        FIXResponse fixResponse = null;
        if(inTradeMessage instanceof ReportBase) {
            reportBase = (ReportBase)inTradeMessage;
        }
        if(inTradeMessage instanceof ExecutionReport) {
            executionReport = (ExecutionReport)inTradeMessage;
            tradeMessageBuilder.setTradeMessageType(TradingTypesRpc.TradeMessageType.TradeMessageExecutionReport);
        } else if(inTradeMessage instanceof OrderCancelReject) {
            tradeMessageBuilder.setTradeMessageType(TradingTypesRpc.TradeMessageType.TradeMessageOrderCancelReject);
        } else if(inTradeMessage instanceof FIXResponse) {
            fixResponse = (FIXResponse)inTradeMessage;
            tradeMessageBuilder.setTradeMessageType(TradingTypesRpc.TradeMessageType.TradeMessageFixResponse);
        } else {
            throw new UnsupportedOperationException();
        }
        if(inTradeMessage instanceof HasFIXMessage) {
            setFixMessage((HasFIXMessage)inTradeMessage,
                          tradeMessageBuilder);
        }
        if(reportBase != null) {
            setBrokerId(reportBase,
                        tradeMessageBuilder);
            setBrokerOrderId(reportBase,
                             tradeMessageBuilder);
            setHierarchy(reportBase,
                         tradeMessageBuilder);
            setOrderId(reportBase,
                       tradeMessageBuilder);
            setOrderStatus(reportBase,
                           tradeMessageBuilder);
            setOriginalOrderId(reportBase,
                               tradeMessageBuilder);
            setOriginator(reportBase,
                          tradeMessageBuilder);
            setReportId(reportBase,
                        tradeMessageBuilder);
            setSendingTime(reportBase,
                           tradeMessageBuilder);
            setText(reportBase,
                    tradeMessageBuilder);
            setUserId(reportBase,
                      tradeMessageBuilder);
        }
        if(executionReport != null) {
            setAccount(executionReport,
                       tradeMessageBuilder);
            setAveragePrice(executionReport,
                            tradeMessageBuilder);
            setCumulativeQuantity(executionReport,
                                  tradeMessageBuilder);
            setExecutionId(executionReport,
                           tradeMessageBuilder);
            setExecutionType(executionReport,
                             tradeMessageBuilder);
            setInstrument(executionReport,
                          tradeMessageBuilder);
            setLastMarket(executionReport,
                          tradeMessageBuilder);
            setLastPrice(executionReport,
                         tradeMessageBuilder);
            setLastQuantity(executionReport,
                            tradeMessageBuilder);
            setLeavesQuantity(executionReport,
                              tradeMessageBuilder);
            setOrderCapacity(executionReport,
                             tradeMessageBuilder);
            setOrderDisplayQuantity(executionReport,
                                    tradeMessageBuilder);
            setOrderQuantity(executionReport,
                             tradeMessageBuilder);
            setOrderType(executionReport,
                         tradeMessageBuilder);
            setPositionEffect(executionReport,
                              tradeMessageBuilder);
            setPrice(executionReport,
                     tradeMessageBuilder);
            setSide(executionReport,
                    tradeMessageBuilder);
            setTimeInForce(executionReport,
                           tradeMessageBuilder);
            setTransactTime(executionReport,
                            tradeMessageBuilder);
        }
        if(fixResponse != null) {
            setBrokerId(fixResponse,
                        tradeMessageBuilder);
            setOriginator(fixResponse,
                          tradeMessageBuilder);
            setUserId(fixResponse,
                      tradeMessageBuilder);
        }
        return tradeMessageBuilder.build();
    }
    /**
     * Set the given trade message on the given builder.
     *
     * @param inTradeMessage a <code>TradeMessage</code> value
     * @param inBuilder a <code>TradingRpc.TradeMessageListenerResponse.Builder</code>
     */
    public static void setTradeMessage(TradeMessage inTradeMessage,
                                       TradingRpc.TradeMessageListenerResponse.Builder inBuilder)
    {
        inBuilder.setTradeMessage(getRpcTradeMessage(inTradeMessage));
    }
    /**
     * Set the FIX message from the given message holder on the given builder.
     *
     * @param inMessageHolder a <code>HasFIXMessage</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setFixMessage(HasFIXMessage inMessageHolder,
                                     TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        TradingTypesRpc.FixMessage.Builder fixMessageBuilder = TradingTypesRpc.FixMessage.newBuilder();
        BaseRpc.Map.Builder mapBuilder = BaseRpc.Map.newBuilder();
        Message fixMessage = inMessageHolder.getMessage();
        setRpcFixMessageGroup(fixMessage.getHeader(),
                              mapBuilder);
        fixMessageBuilder.setHeader(mapBuilder.build());
        mapBuilder.clear();
        setRpcFixMessageGroup(fixMessage,
                              mapBuilder);
        fixMessageBuilder.setBody(mapBuilder.build());
        mapBuilder.clear();
        setRpcFixMessageGroup(fixMessage.getTrailer(),
                              mapBuilder);
        fixMessageBuilder.setFooter(mapBuilder.build());
        inBuilder.setMessage(fixMessageBuilder.build());
    }
    /**
     * Set the transact time from the given report on the given builder.
     *
     * @param inReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setTransactTime(ExecutionReport inReport,
                                       TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getTransactTime() == null) {
            return;
        }
        inBuilder.setTransactTime(Timestamps.fromMillis(inReport.getTransactTime().getTime()));
    }
    /**
     * Set the time in force from the given report on the given builder.
     *
     * @param inReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setTimeInForce(ExecutionReport inReport,
                                      TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getTimeInForce() == null) {
            return;
        }
        inBuilder.setTimeInForce(getRpcTimeInForce(inReport.getTimeInForce()));
    }
    /**
     * Set the side from the given report on the given builder.
     *
     * @param inReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setSide(ExecutionReport inReport,
                               TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getSide() == null) {
            return;
        }
        inBuilder.setSide(getRpcSide(inReport.getSide()));
    }
    /**
     * Set the price value the given trade message on the given builder.
     *
     * @param inExecutionReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setPrice(ExecutionReport inExecutionReport,
                                TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inExecutionReport.getPrice() == null) {
            return;
        }
        inBuilder.setPrice(BaseUtil.getQtyValueFrom(inExecutionReport.getPrice()));
    }
    /**
     * Set the position effect from the given report on the given builder.
     *
     * @param inReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setPositionEffect(ExecutionReport inReport,
                                         TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getPositionEffect() == null) {
            return;
        }
        inBuilder.setPositionEffect(getRpcPositionEffect(inReport.getPositionEffect()));
    }
    /**
     * Set the order type from the given report on the given builder.
     *
     * @param inReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setOrderType(ExecutionReport inReport,
                                    TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getOrderType() == null) {
            return;
        }
        inBuilder.setOrderType(getRpcOrderType(inReport.getOrderType()));
    }
    /**
     * Set the order quantity from value the given trade message on the given builder.
     *
     * @param inExecutionReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setOrderQuantity(ExecutionReport inExecutionReport,
                                        TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inExecutionReport.getOrderQuantity() == null) {
            return;
        }
        inBuilder.setOrderQuantity(BaseUtil.getQtyValueFrom(inExecutionReport.getOrderQuantity()));
    }
    /**
     * Set the order display quantity from value the given trade message on the given builder.
     *
     * @param inExecutionReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setOrderDisplayQuantity(ExecutionReport inExecutionReport,
                                               TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inExecutionReport.getOrderDisplayQuantity() == null) {
            return;
        }
        inBuilder.setOrderDisplayQuantity(BaseUtil.getQtyValueFrom(inExecutionReport.getOrderDisplayQuantity()));
    }
    /**
     * Set the order capacity from the given report on the given builder.
     *
     * @param inReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setOrderCapacity(ExecutionReport inReport,
                                        TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getOrderCapacity() == null) {
            return;
        }
        inBuilder.setOrderCapacity(getRpcOrderCapacity(inReport.getOrderCapacity()));
    }
    /**
     * Set the leaves quantity from value the given trade message on the given builder.
     *
     * @param inExecutionReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setLeavesQuantity(ExecutionReport inExecutionReport,
                                         TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inExecutionReport.getLeavesQuantity() == null) {
            return;
        }
        inBuilder.setLeavesQuantity(BaseUtil.getQtyValueFrom(inExecutionReport.getLeavesQuantity()));
    }
    /**
     * Set the last quantity from value the given trade message on the given builder.
     *
     * @param inExecutionReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setLastQuantity(ExecutionReport inExecutionReport,
                                       TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inExecutionReport.getLastQuantity() == null) {
            return;
        }
        inBuilder.setLastQuantity(BaseUtil.getQtyValueFrom(inExecutionReport.getLastQuantity()));
    }
    /**
     * Set the last price from value the given trade message on the given builder.
     *
     * @param inExecutionReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setLastPrice(ExecutionReport inExecutionReport,
                                    TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inExecutionReport.getLastPrice() == null) {
            return;
        }
        inBuilder.setLastPrice(BaseUtil.getQtyValueFrom(inExecutionReport.getLastPrice()));
    }
    /**
     * Set the last market from the given report on the given builder.
     *
     * @param inExecutionReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setLastMarket(ExecutionReport inExecutionReport,
                                     TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        String value = StringUtils.trimToNull(inExecutionReport.getLastMarket());
        if(value == null) {
            return;
        }
        inBuilder.setLastMarket(value);
    }
    /**
     * Set the instrument from the given report on the given builder.
     *
     * @param inReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setInstrument(ExecutionReport inReport,
                                     TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getInstrument() == null) {
            return;
        }
        inBuilder.setInstrument(getRpcInstrument(inReport.getInstrument()));
    }
    /**
     * Get the RPC execution type value from the given value.
     *
     * @param inExecutionType an <code>ExecutionType</code> value
     * @return a <code>TradingTypesRpc.ExecutionType</code> value
     */
    public static TradingTypesRpc.ExecutionType getRpcExecutionType(ExecutionType inExecutionType)
    {
        switch(inExecutionType) {
            case Calculated:
                return TradingTypesRpc.ExecutionType.CalculatedExecutionType;
            case Canceled:
                return TradingTypesRpc.ExecutionType.CanceledExecutionType;
            case DoneForDay:
                return TradingTypesRpc.ExecutionType.DoneForDayExecutionType;
            case Expired:
                return TradingTypesRpc.ExecutionType.ExpiredExecutionType;
            case Fill:
                return TradingTypesRpc.ExecutionType.FillExecutionType;
            case New:
                return TradingTypesRpc.ExecutionType.NewExecutionType;
            case OrderStatus:
                return TradingTypesRpc.ExecutionType.OrderStatusExecutionType;
            case PartialFill:
                return TradingTypesRpc.ExecutionType.PartialFillExecutionType;
            case PendingCancel:
                return TradingTypesRpc.ExecutionType.PendingCancelExecutionType;
            case PendingNew:
                return TradingTypesRpc.ExecutionType.PendingNewExecutionType;
            case PendingReplace:
                return TradingTypesRpc.ExecutionType.PendingReplaceExecutionType;
            case Rejected:
                return TradingTypesRpc.ExecutionType.RejectedExecutionType;
            case Replace:
                return TradingTypesRpc.ExecutionType.ReplaceExecutionType;
            case Restated:
                return TradingTypesRpc.ExecutionType.RestatedExecutionType;
            case Stopped:
                return TradingTypesRpc.ExecutionType.StoppedExecutionType;
            case Suspended:
                return TradingTypesRpc.ExecutionType.SuspendedExecutionType;
            case Trade:
                return TradingTypesRpc.ExecutionType.TradeCancelExecutionType;
            case TradeCancel:
                return TradingTypesRpc.ExecutionType.TradeCancelExecutionType;
            case TradeCorrect:
                return TradingTypesRpc.ExecutionType.TradeCorrectExecutionType;
            case Unknown:
                return TradingTypesRpc.ExecutionType.UnknownExecutionType;
            default:
                throw new UnsupportedOperationException("Unsupported execution type: " + inExecutionType);
        }
    }
    /**
     * Set the execution type from the given report on the given builder.
     *
     * @param inReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setExecutionType(ExecutionReport inReport,
                                        TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getExecutionType() == null) {
            return;
        }
        inBuilder.setExecutionType(getRpcExecutionType(inReport.getExecutionType()));
    }
    /**
     * Set the execution ID from the given report on the given builder.
     *
     * @param inReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setExecutionId(ExecutionReport inReport,
                                      TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        String value = StringUtils.trimToNull(inReport.getExecutionID());
        if(value == null) {
            return;
        }
        inBuilder.setExecutionId(value);
    }
    /**
     * Set the cumulative quantity from value the given trade message on the given builder.
     *
     * @param inExecutionReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setCumulativeQuantity(ExecutionReport inExecutionReport,
                                             TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inExecutionReport.getCumulativeQuantity() == null) {
            return;
        }
        inBuilder.setCumulativeQuantity(BaseUtil.getQtyValueFrom(inExecutionReport.getCumulativeQuantity()));
    }
    /**
     * Set the average price from value the given trade message on the given builder.
     *
     * @param inExecutionReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setAveragePrice(ExecutionReport inExecutionReport,
                                       TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inExecutionReport.getAveragePrice() == null) {
            return;
        }
        inBuilder.setAveragePrice(BaseUtil.getQtyValueFrom(inExecutionReport.getAveragePrice()));
    }
    /**
     * Set the broker ID from value the given trade message on the given builder.
     *
     * @param inReportBase a <code>ReportBase</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setBrokerId(ReportBase inReportBase,
                                   TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReportBase.getBrokerID() == null) {
            return;
        }
        inBuilder.setBrokerId(String.valueOf(inReportBase.getBrokerID()));
    }
    /**
     * Set the broker ID from value the given trade message on the given builder.
     *
     * @param inReport a <code>FIXResponse</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setBrokerId(FIXResponse inReport,
                                   TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getBrokerID() == null) {
            return;
        }
        inBuilder.setBrokerId(String.valueOf(inReport.getBrokerID()));
    }
    /**
     * Set the broker order ID from the given report on the given builder.
     *
     * @param inReport a <code>ReportBase</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setBrokerOrderId(ReportBase inReport,
                                        TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getBrokerOrderID() == null) {
            return;
        }
        inBuilder.setBrokerOrderId(inReport.getBrokerOrderID());
    }
    /**
     * Set the order ID from the given report on the given builder.
     *
     * @param inReport a <code>ReportBase</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setOrderId(ReportBase inReport,
                                  TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getOrderID() == null) {
            return;
        }
        inBuilder.setOrderId(String.valueOf(inReport.getOrderID()));
    }
    /**
     * Set the report ID from the given report on the given builder.
     *
     * @param inReport a <code>ReportBase</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setReportId(ReportBase inReport,
                                  TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getReportID() == null) {
            return;
        }
        inBuilder.setReportId(String.valueOf(inReport.getReportID()));
    }
    /**
     * Set the sending time from the given report on the given builder.
     *
     * @param inReport a <code>ReportBase</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setSendingTime(ReportBase inReport,
                                      TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getSendingTime() == null) {
            return;
        }
        inBuilder.setSendingTime(Timestamps.fromMillis(inReport.getSendingTime().getTime()));
    }
    /**
     * Set the text from the given report on the given builder.
     *
     * @param inReport a <code>ReportBase</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setText(ReportBase inReport,
                               TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        String value = StringUtils.trimToNull(inReport.getText());
        if(value == null) {
            return;
        }
        inBuilder.setText(value);
    }
    /**
     * Set the original order ID from the given report on the given builder.
     *
     * @param inReport a <code>ReportBase</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setOriginalOrderId(ReportBase inReport,
                                          TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getOriginalOrderID() == null) {
            return;
        }
        inBuilder.setOriginalOrderId(String.valueOf(inReport.getOriginalOrderID()));
    }
    /**
     * Set the hierarchy from the given report on the given builder.
     *
     * @param inReport a <code>ReportBase</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setHierarchy(ReportBase inReport,
                                    TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getHierarchy() == null) {
            return;
        }
        inBuilder.setHierarchy(getRpcHierarchy(inReport.getHierarchy()));
    }
    /**
     * Set the order status from the given report on the given builder.
     *
     * @param inReport a <code>ReportBase</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setOrderStatus(ReportBase inReport,
                                      TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getOrderStatus() == null) {
            return;
        }
        inBuilder.setOrderStatus(getRpcOrderStatus(inReport.getOrderStatus()));
    }
    /**
     * Set the order status from the given RPC value on the given order summary.
     *
     * @param inRpcOrderSummary a <code>TradingTypesRpc.OrderSummary</code> value
     * @param inOrderSummary a <code>MutableOrderSummary</code> value
     */
    public static void setOrderStatus(TradingTypesRpc.OrderSummary inRpcOrderSummary,
                                      MutableOrderSummary inOrderSummary)
    {
        inOrderSummary.setOrderStatus(getOrderStatus(inRpcOrderSummary.getOrderStatus()));
    }
    /**
     * Set the originator from the given report on the given builder.
     *
     * @param inReport a <code>ReportBase</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setOriginator(ReportBase inReport,
                                     TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getOriginator() == null) {
            return;
        }
        inBuilder.setOriginator(getRpcOriginator(inReport.getOriginator()));
    }
    /**
     * Set the originator from the given report on the given builder.
     *
     * @param inReport a <code>FIXResponse</code> value
     * @param inBuilder a <code>TradingTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setOriginator(FIXResponse inReport,
                                     TradingTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getOriginator() == null) {
            return;
        }
        inBuilder.setOriginator(getRpcOriginator(inReport.getOriginator()));
    }
    /**
     * Get the RPC order status value from the given value.
     *
     * @param inOrderStatus an <code>OrderStatus</code> value
     * @return a <code>TradingTypesRpc.OrderStatus</code> value
     */
    public static TradingTypesRpc.OrderStatusType getRpcOrderStatus(OrderStatus inOrderStatus)
    {
        switch(inOrderStatus) {
            case AcceptedForBidding:
                return TradingTypesRpc.OrderStatusType.AcceptedForBidding;
            case Calculated:
                return TradingTypesRpc.OrderStatusType.Calculated;
            case Canceled:
                return TradingTypesRpc.OrderStatusType.Canceled;
            case DoneForDay:
                return TradingTypesRpc.OrderStatusType.DoneForDay;
            case Expired:
                return TradingTypesRpc.OrderStatusType.Expired;
            case Filled:
                return TradingTypesRpc.OrderStatusType.Filled;
            case New:
                return TradingTypesRpc.OrderStatusType.New;
            case PartiallyFilled:
                return TradingTypesRpc.OrderStatusType.PartiallyFilled;
            case PendingCancel:
                return TradingTypesRpc.OrderStatusType.PendingCancel;
            case PendingNew:
                return TradingTypesRpc.OrderStatusType.PendingNew;
            case PendingReplace:
                return TradingTypesRpc.OrderStatusType.PendingReplace;
            case Rejected:
                return TradingTypesRpc.OrderStatusType.Rejected;
            case Replaced:
                return TradingTypesRpc.OrderStatusType.Replaced;
            case Stopped:
                return TradingTypesRpc.OrderStatusType.Stopped;
            case Suspended:
                return TradingTypesRpc.OrderStatusType.Suspended;
            case Unknown:
                return TradingTypesRpc.OrderStatusType.UnknownOrderStatus;
            default:
                throw new UnsupportedOperationException("Unsupported order status: " + inOrderStatus);
        }
    }
    /**
     * Get the order status value from the given RPC value.
     *
     * @param inRpcOrderStatus a <code>TradingTypesRpc.OrderStatusType</code> value
     * @return an <code>OrderStatus</code> value
     */
    public static OrderStatus getOrderStatus(TradingTypesRpc.OrderStatusType inRpcOrderStatus)
    {
        switch(inRpcOrderStatus) {
            case AcceptedForBidding:
                return OrderStatus.AcceptedForBidding;
            case Calculated:
                return OrderStatus.Calculated;
            case Canceled:
                return OrderStatus.Canceled;
            case DoneForDay:
                return OrderStatus.DoneForDay;
            case Expired:
                return OrderStatus.Expired;
            case Filled:
                return OrderStatus.Filled;
            case New:
                return OrderStatus.New;
            case PartiallyFilled:
                return OrderStatus.PartiallyFilled;
            case PendingCancel:
                return OrderStatus.PendingCancel;
            case PendingNew:
                return OrderStatus.PendingNew;
            case PendingReplace:
                return OrderStatus.PendingReplace;
            case Rejected:
                return OrderStatus.Rejected;
            case Replaced:
                return OrderStatus.Replaced;
            case Stopped:
                return OrderStatus.Stopped;
            case Suspended:
                return OrderStatus.Suspended;
            case UnknownOrderStatus:
                return OrderStatus.Unknown;
            case UNRECOGNIZED:
            default:
                throw new UnsupportedOperationException("Unsupported order status: " + inRpcOrderStatus);
        }
    }
    /**
     * Get the RPC originator value from the given value.
     *
     * @param inOriginator an <code>Originator</code> value
     * @return a <code>TradingTypesRpc.Originator</code> value
     */
    public static TradingTypesRpc.Originator getRpcOriginator(Originator inOriginator)
    {
        switch(inOriginator) {
            case Broker:
                return TradingTypesRpc.Originator.BrokerOriginator;
            case Server:
                return TradingTypesRpc.Originator.ServerOriginator;
            default:
                throw new UnsupportedOperationException("Unsupported originator: " + inOriginator);
        }
    }
    /**
     * Get the user ID value from the given RPC message.
     * 
     * @param inRpcTradeMessage a <code>TradingTypesRpc.OrderBase</code> value 
     * @return an <code>Optional&lt;UserID&gt;</code> value
     */
    private static Optional<UserID> getUserId(TradingTypesRpc.TradeMessage inRpcTradeMessage)
    {
        String value = StringUtils.trimToNull(inRpcTradeMessage.getUser());
        if(value == null) {
            return Optional.empty();
        }
        long longValue;
        try {
            longValue = Long.valueOf(value);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
        return Optional.of(new UserID(longValue));
    }
    /**
     * Get the originator value from the given RPC message.
     *
     * @param inRpcTradeMessage a <code>TradingTypesRpc.OrderBase</code> value 
     * @return an <code>Originator</code> value
     */
    private static Originator getOriginator(TradingTypesRpc.TradeMessage inRpcTradeMessage)
    {
        switch(inRpcTradeMessage.getOriginator()) {
            case BrokerOriginator:
                return Originator.Broker;
            case ServerOriginator:
                return Originator.Server;
            case UNRECOGNIZED:
            default:
                throw new UnsupportedOperationException("Unsupported originator: " + inRpcTradeMessage.getOriginator());
        }
    }
    /**
     * Get the broker algo from the given RPC order.
     *
     * @param inRpcOrder a <code>TradingTypesRpc.OrderBase</code> value
     * @return an <code>Optional&lt;BrokerAlgo&gt;</code> value
     */
    public static Optional<BrokerAlgo> getBrokerAlgo(TradingTypesRpc.OrderBase inRpcOrder)
    {
        throw new UnsupportedOperationException();
    }
    /**
     * Get the FIX message from the given RPC FIX message.
     *
     * @param inRpcMessage a <code>FixMessge</code> value
     * @return a <code>Message</code> value
     */
    public static Message getFixMessage(FixMessage inRpcMessage)
    {
        Message fixMessage = new Message();
        if(inRpcMessage.hasHeader()) {
            setFixMessageGroup(inRpcMessage.getHeader(),
                               fixMessage.getHeader());
        }
        if(inRpcMessage.hasBody()) {
            setFixMessageGroup(inRpcMessage.getBody(),
                               fixMessage);
        }
        if(inRpcMessage.hasFooter()) {
            setFixMessageGroup(inRpcMessage.getFooter(),
                               fixMessage.getTrailer());
        }
        // reset chksum and other stuff
        fixMessage.toString();
        // validate message?
        return fixMessage;
    }
    /**
     * Set the order ID from the given order on the given builder.
     *
     * @param inOrder an <code>Order</code> value
     * @param inBuilder a <code>TradingRpc.OrderResponse.Builder</code> value
     */
    public static void setOrderId(Order inOrder,
                                  TradingRpc.OrderResponse.Builder inBuilder)
    {
        if(inOrder instanceof OrderBase) {
            OrderBase order = (OrderBase)inOrder;
            if(order.getOrderID() != null) {
                inBuilder.setOrderid(order.getOrderID().getValue());
            }
        } else if(inOrder instanceof FIXOrder) {
            FIXOrder fixOrder = (FIXOrder)inOrder;
            Message message = fixOrder.getMessage();
            try {
                if(message.isSetField(quickfix.field.ClOrdID.FIELD)) {
                    inBuilder.setOrderid(message.getString(quickfix.field.ClOrdID.FIELD));
                } else if(message.isSetField(quickfix.field.OrderID.FIELD)) {
                    inBuilder.setOrderid(message.getString(quickfix.field.OrderID.FIELD));
                }
            } catch (FieldNotFound e) {
                PlatformServices.handleException(TradingUtil.class,
                                                 "Unable to set order id",
                                                 e);
            }
        }
    }
    /**
     * Set the order id value.
     *
     * @param inRpcOrderSummary a <code>TradingTypesRpc.OrderSummary</code> value
     * @param inOrderSummary a <code>MutableOrderSummary</code> value
     */
    public static void setOrderId(TradingTypesRpc.OrderSummary inRpcOrderSummary,
                                  MutableOrderSummary inOrderSummary)
    {
        String value = StringUtils.trimToNull(inRpcOrderSummary.getOrderId());
        if(value != null) {
            inOrderSummary.setOrderId(new OrderID(value));
        } else {
            inOrderSummary.setOrderId(null);
        }
    }
    /**
     * Set the root order id value.
     *
     * @param inRpcOrderSummary a <code>TradingTypesRpc.OrderSummary</code> value
     * @param inOrderSummary a <code>MutableOrderSummary</code> value
     */
    public static void setRootOrderId(TradingTypesRpc.OrderSummary inRpcOrderSummary,
                                      MutableOrderSummary inOrderSummary)
    {
        String value = StringUtils.trimToNull(inRpcOrderSummary.getRootOrderId());
        if(value != null) {
            inOrderSummary.setRootOrderId(new OrderID(value));
        } else {
            inOrderSummary.setRootOrderId(null);
        }
    }
    /**
     * Get the broker status value from the given response.
     *
     * @param inResponse a <code>BrokerStatusListenerResponse</code> value
     * @return an <code>Optional&lt;BrokerStatus&gt;</code> value
     */
    public static Optional<BrokerStatus> getBrokerStatus(BrokerStatusListenerResponse inResponse)
    {
        BrokerStatus brokerStatus = null;
        if(inResponse.hasBrokerStatus()) {
            TradingTypesRpc.BrokerStatus rpcBrokerStatus = inResponse.getBrokerStatus();
            Map<String,String> settings = Maps.newHashMap();
            if(rpcBrokerStatus.hasSettings()) {
                settings = BaseUtil.getMap(rpcBrokerStatus.getSettings());
            }
            settings.put("id",
                         rpcBrokerStatus.getId());
            settings.put("host",
                         rpcBrokerStatus.getHost());
            settings.put("name",
                         rpcBrokerStatus.getName());
            settings.put("port",
                         String.valueOf(rpcBrokerStatus.getPort()));
            brokerStatus = new ClusteredBrokerStatus(fixSessionFactory.create(settings),
                                                     rpcBrokerStatus.hasClusterData() ? getClusterData(rpcBrokerStatus.getClusterData()).orElse(null) : null,
                                                     getFixSessionStatus(rpcBrokerStatus.getFixSessionStatus()),
                                                     rpcBrokerStatus.getLoggedOn());
//          rpcBrokerStatus.getBrokerAlgosList() TODO
        }
        return(brokerStatus==null ? Optional.empty():Optional.of(brokerStatus));
    }
    /**
     * Get the cluster data value from the given RPC value.
     *
     * @param inRpcClusterData a <code>TradingTypesRpc.ClusterData</code> value
     * @return an <code>Optional&lt;ClusterData&gt;</code> value
     */
    public static Optional<ClusterData> getClusterData(TradingTypesRpc.ClusterData inRpcClusterData)
    {
        ClusterData clusterData = new ClusterData(inRpcClusterData.getTotalInstances(),
                                                  inRpcClusterData.getHostId(),
                                                  inRpcClusterData.getHostNumber(),
                                                  inRpcClusterData.getInstanceNumber(),
                                                  inRpcClusterData.getUuid());
        return(clusterData == null ? Optional.empty() : Optional.of(clusterData));
    }
    /**
     * Set the given broker status value on the given RPC builder.
     *
     * @param inStatus a <code>BrokerStatus</code> value
     * @param inResponseBuilder a <code>TradingRpc.BrokerStatusListenerResponse.Builder</code> value
     */
    public static void setBrokerStatus(BrokerStatus inStatus,
                                       TradingRpc.BrokerStatusListenerResponse.Builder inResponseBuilder)
    {
        if(inStatus == null) {
            return;
        }
        TradingTypesRpc.BrokerStatus.Builder brokerStatusBuilder = TradingTypesRpc.BrokerStatus.newBuilder();
        setBrokerAlgos(inStatus,
                       brokerStatusBuilder);
        brokerStatusBuilder.setHost(inStatus.getHost());
        setBrokerId(inStatus,
                    brokerStatusBuilder);
        brokerStatusBuilder.setLoggedOn(inStatus.getLoggedOn());
        brokerStatusBuilder.setName(inStatus.getName());
        brokerStatusBuilder.setPort(inStatus.getPort());
        brokerStatusBuilder.setSettings(BaseUtil.getRpcMap(inStatus.getSettings()));
        brokerStatusBuilder.setFixSessionStatus(getRpcFixSessionStatus(inStatus.getStatus()));
        if(inStatus instanceof HasClusterData) {
            HasClusterData hasClusterData = (HasClusterData)inStatus;
            TradingTypesRpc.ClusterData.Builder clusterDataBuilder = TradingTypesRpc.ClusterData.newBuilder();
            ClusterData clusterData = hasClusterData.getClusterData();
            clusterDataBuilder.setHostId(clusterData.getHostId());
            clusterDataBuilder.setHostNumber(clusterData.getHostNumber());
            clusterDataBuilder.setInstanceNumber(clusterData.getInstanceNumber());
            clusterDataBuilder.setTotalInstances(clusterData.getTotalInstances());
            clusterDataBuilder.setUuid(clusterData.getUuid());
            brokerStatusBuilder.setClusterData(clusterDataBuilder.build());
        }
        inResponseBuilder.setBrokerStatus(brokerStatusBuilder.build());
    }
    /**
     * Get the FIX session status value from the given RPC value.
     *
     * @param inRpcFixSessionStatus a <code>TradingTypesRpc.FixSessionStatus</code> value
     * @return a <code>FixSessionStatus</code> value
     */
    public static FixSessionStatus getFixSessionStatus(TradingTypesRpc.FixSessionStatus inRpcFixSessionStatus)
    {
        switch(inRpcFixSessionStatus) {
            case AffinityMismatchFixSessionStatus:
                return FixSessionStatus.AFFINITY_MISMATCH;
            case BackupFixSessionStatus:
                return FixSessionStatus.BACKUP;
            case ConnectedFixSessionStatus:
                return FixSessionStatus.CONNECTED;
            case DeletedFixSessionStatus:
                return FixSessionStatus.DELETED;
            case DisabledFixSessionStatus:
                return FixSessionStatus.DISABLED;
            case DisconnectedFixSessionStatus:
                return FixSessionStatus.DISCONNECTED;
            case NotConnectedFixSessionStatus:
                return FixSessionStatus.NOT_CONNECTED;
            case StoppedFixSessionStatus:
                return FixSessionStatus.STOPPED;
            case UnknownFixSessionStatus:
                return FixSessionStatus.UNKNOWN;
            case UNRECOGNIZED:
            default:
                throw new UnsupportedOperationException("Unsupported fix session status: " + inRpcFixSessionStatus);
        }
    }
    /**
     * Get the RPC FIX session status value for the given value.
     *
     * @param inStatus a <code>FixSessionStatus</code> value
     * @return a <code>TradingTypesRpc.FixSessionStatus</code> value
     */
    public static TradingTypesRpc.FixSessionStatus getRpcFixSessionStatus(FixSessionStatus inStatus)
    {
        switch(inStatus) {
            case AFFINITY_MISMATCH:
                return TradingTypesRpc.FixSessionStatus.AffinityMismatchFixSessionStatus;
            case BACKUP:
                return TradingTypesRpc.FixSessionStatus.BackupFixSessionStatus;
            case CONNECTED:
                return TradingTypesRpc.FixSessionStatus.ConnectedFixSessionStatus;
            case DELETED:
                return TradingTypesRpc.FixSessionStatus.DeletedFixSessionStatus;
            case DISABLED:
                return TradingTypesRpc.FixSessionStatus.DisabledFixSessionStatus;
            case DISCONNECTED:
                return TradingTypesRpc.FixSessionStatus.DisconnectedFixSessionStatus;
            case NOT_CONNECTED:
                return TradingTypesRpc.FixSessionStatus.NotConnectedFixSessionStatus;
            case STOPPED:
                return TradingTypesRpc.FixSessionStatus.StoppedFixSessionStatus;
            case UNKNOWN:
                return TradingTypesRpc.FixSessionStatus.UnknownFixSessionStatus;
            default:
                throw new UnsupportedOperationException("Unsupported fix session status: " + inStatus);
        }
    }
    /**
     * Set the given broker ID on the given RPC builder.
     *
     * @param inStatus a <code>BrokerStatus</code> value
     * @param inBrokerStatusBuilder a <code>TradingTypesRpc.BrokerStatus.Builder</code> value
     */
    public static void setBrokerId(BrokerStatus inStatus,
                                   TradingTypesRpc.BrokerStatus.Builder inBrokerStatusBuilder)
    {
        if(inStatus == null || inStatus.getId() == null) {
            return;
        }
        inBrokerStatusBuilder.setId(inStatus.getId().getValue());
    }
    /**
     * Set the broker algos value on the given RPC builder.
     *
     * @param inStatus a <code>BrokerStatus</code> value
     * @param inBrokerStatusBuilder a <code>TradingTypesRpc.BrokerStatus.Builder</code> value
     */
    public static void setBrokerAlgos(BrokerStatus inStatus,
                                      TradingTypesRpc.BrokerStatus.Builder inBrokerStatusBuilder)
    {
        if(inStatus.getBrokerAlgos().isEmpty()) {
            return;
        }
        TradingTypesRpc.BrokerAlgo.Builder brokerAlgoBuilder = TradingTypesRpc.BrokerAlgo.newBuilder();
        TradingTypesRpc.BrokerAlgoTagSpec.Builder brokerAlgoTagSpecBuilder = TradingTypesRpc.BrokerAlgoTagSpec.newBuilder();
        for(BrokerAlgoSpec brokerAlgo : inStatus.getBrokerAlgos()) {
            for(BrokerAlgoTagSpec tagSpec : brokerAlgo.getAlgoTagSpecs()) {
                brokerAlgoTagSpecBuilder.setAdvice(tagSpec.getAdvice());
                brokerAlgoTagSpecBuilder.setDefaultValue(tagSpec.getDefaultValue());
                brokerAlgoTagSpecBuilder.setDescription(tagSpec.getDescription());
                brokerAlgoTagSpecBuilder.setIsReadOnly(tagSpec.isReadOnly());
                brokerAlgoTagSpecBuilder.setLabel(tagSpec.getLabel());
                brokerAlgoTagSpecBuilder.setMandatory(tagSpec.getIsMandatory());
                brokerAlgoTagSpecBuilder.setOptions(BaseUtil.getRpcMap(tagSpec.getOptions()));
                brokerAlgoTagSpecBuilder.setPattern(tagSpec.getPattern());
                brokerAlgoTagSpecBuilder.setTag(tagSpec.getTag());
                brokerAlgoTagSpecBuilder.setValidator(tagSpec.getValidator()==null?null:tagSpec.getValidator().getClass().getName());
                brokerAlgoBuilder.addAlgoTagSpecs(brokerAlgoTagSpecBuilder.build());
                brokerAlgoTagSpecBuilder.clear();
            }
            brokerAlgoBuilder.setName(brokerAlgo.getName());
            inBrokerStatusBuilder.addBrokerAlgos(brokerAlgoBuilder.build());
            brokerAlgoBuilder.clear();
        }
    }
    /**
     * Get an order summary value from the given RPC order summary.
     *
     * @param inRpcOrderSummary a <code>TradingTypesRpc.OrderSummary</code> value
     * @return an <code>Optional&lt;OrderSummary&gt;</code> value
     */
    public static Optional<OrderSummary> getOrderSummary(TradingTypesRpc.OrderSummary inRpcOrderSummary)
    {
        MutableOrderSummary orderSummary = orderSummaryFactory.create();
        orderSummary.setAccount(inRpcOrderSummary.getAccount());;
        if(inRpcOrderSummary.hasUser()) {
            setUser(inRpcOrderSummary.getUser(),
                    orderSummary);
        }
        if(inRpcOrderSummary.hasAveragePrice()) {
            orderSummary.setAveragePrice(BaseUtil.getScaledQuantity(inRpcOrderSummary.getAveragePrice()));
        }
        orderSummary.setBrokerId(getBrokerId(inRpcOrderSummary).orElse(null));
        if(inRpcOrderSummary.hasCumulativeQuantity()) {
            orderSummary.setCumulativeQuantity(BaseUtil.getScaledQuantity(inRpcOrderSummary.getCumulativeQuantity()));
        }
        if(inRpcOrderSummary.hasInstrument()) {
            orderSummary.setInstrument(getInstrument(inRpcOrderSummary));
        }
        if(inRpcOrderSummary.hasLastPrice()) {
            orderSummary.setLastPrice(BaseUtil.getScaledQuantity(inRpcOrderSummary.getLastPrice()));
        }
        if(inRpcOrderSummary.hasLastQuantity()) {
            orderSummary.setLastQuantity(BaseUtil.getScaledQuantity(inRpcOrderSummary.getLastQuantity()));
        }
        if(inRpcOrderSummary.hasLeavesQuantity()) {
            orderSummary.setLeavesQuantity(BaseUtil.getScaledQuantity(inRpcOrderSummary.getLeavesQuantity()));
        }
        setOrderId(inRpcOrderSummary,
                   orderSummary);
        if(inRpcOrderSummary.hasOrderPrice()) {
            orderSummary.setOrderPrice(BaseUtil.getScaledQuantity(inRpcOrderSummary.getOrderPrice()));
        }
        if(inRpcOrderSummary.hasOrderQuantity()) {
            orderSummary.setOrderQuantity(BaseUtil.getScaledQuantity(inRpcOrderSummary.getOrderQuantity()));
        }
        setOrderStatus(inRpcOrderSummary,
                       orderSummary);
        if(inRpcOrderSummary.hasReport()) {
            TradeMessage tradeMessage = getTradeMessage(inRpcOrderSummary.getReport());
            orderSummary.setReport(reportFactory.create(tradeMessage,
                                                        orderSummary.getActor()));
        }
        setRootOrderId(inRpcOrderSummary,
                       orderSummary);
        if(inRpcOrderSummary.hasSendingTime()) {
            orderSummary.setSendingTime(new Date(Timestamps.toMillis(inRpcOrderSummary.getSendingTime())));
        }
        orderSummary.setSide(getSide(inRpcOrderSummary.getSide()));
        if(inRpcOrderSummary.hasTransactTime()) {
            orderSummary.setTransactTime(new Date(Timestamps.toMillis(inRpcOrderSummary.getTransactTime())));
        }
        orderSummary.setViewer(orderSummary.getActor());
        return Optional.of(orderSummary);
    }
    /**
     * Get the RPC order summary value from the given order summary.
     *
     * @param inOrderSummary an <code>OrderSummary</code> value
     * @return a <code>TradingTypesRpc.OrderSummary</code> value
     */
    public static TradingTypesRpc.OrderSummary getRpcOrderSummary(OrderSummary inOrderSummary)
    {
        TradingTypesRpc.OrderSummary.Builder orderSummaryBuilder = TradingTypesRpc.OrderSummary.newBuilder();
        String value = StringUtils.trimToNull(inOrderSummary.getAccount());
        if(value != null) {
            orderSummaryBuilder.setAccount(value);
        }
        if(inOrderSummary.getAveragePrice() != null) {
            orderSummaryBuilder.setAveragePrice(BaseUtil.getQtyValueFrom(inOrderSummary.getAveragePrice()));
        }
        value = StringUtils.trimToNull(inOrderSummary.getBrokerId()==null?null:inOrderSummary.getBrokerId().getValue());
        if(value != null) {
            orderSummaryBuilder.setBrokerId(value);
        }
        if(inOrderSummary.getCumulativeQuantity() != null) {
            orderSummaryBuilder.setCumulativeQuantity(BaseUtil.getQtyValueFrom(inOrderSummary.getCumulativeQuantity()));
        }
        if(inOrderSummary.getInstrument() != null) {
            orderSummaryBuilder.setInstrument(getRpcInstrument(inOrderSummary.getInstrument()));
        }
        if(inOrderSummary.getLastPrice() != null) {
            orderSummaryBuilder.setLastPrice(BaseUtil.getQtyValueFrom(inOrderSummary.getLastPrice()));
        }
        if(inOrderSummary.getLastQuantity() != null) {
            orderSummaryBuilder.setLastQuantity(BaseUtil.getQtyValueFrom(inOrderSummary.getLastQuantity()));
        }
        if(inOrderSummary.getLeavesQuantity() != null) {
            orderSummaryBuilder.setLeavesQuantity(BaseUtil.getQtyValueFrom(inOrderSummary.getLeavesQuantity()));
        }
        value = inOrderSummary.getOrderId()==null?null:inOrderSummary.getOrderId().getValue();
        if(value != null) {
            orderSummaryBuilder.setOrderId(value);
        }
        if(inOrderSummary.getOrderPrice() != null) {
            orderSummaryBuilder.setOrderPrice(BaseUtil.getQtyValueFrom(inOrderSummary.getOrderPrice()));
        }
        if(inOrderSummary.getOrderQuantity() != null) {
            orderSummaryBuilder.setOrderQuantity(BaseUtil.getQtyValueFrom(inOrderSummary.getOrderQuantity()));
        }
        if(inOrderSummary.getOrderStatus() != null) {
            orderSummaryBuilder.setOrderStatus(getRpcOrderStatus(inOrderSummary.getOrderStatus()));
        }
        if(inOrderSummary.getReport() != null) {
            Report report = inOrderSummary.getReport();
            if(report instanceof HasTradeMessage) {
                HasTradeMessage hasTradeMessage = (HasTradeMessage)report;
                orderSummaryBuilder.setReport(getRpcTradeMessage(hasTradeMessage.getTradeMessage()));
            }
        }
        value = inOrderSummary.getRootOrderId()==null?null:inOrderSummary.getRootOrderId().getValue();
        if(value != null) {
            orderSummaryBuilder.setRootOrderId(value);
        }
        if(inOrderSummary.getSendingTime() != null) {
            orderSummaryBuilder.setSendingTime(Timestamps.fromMillis(inOrderSummary.getSendingTime().getTime()));
        }
        if(inOrderSummary.getSide() != null) {
            orderSummaryBuilder.setSide(getRpcSide(inOrderSummary.getSide()));
        }
        if(inOrderSummary.getTransactTime() != null) {
            orderSummaryBuilder.setTransactTime(Timestamps.fromMillis(inOrderSummary.getTransactTime().getTime()));
        }
        if(inOrderSummary.getActor() != null) {
            orderSummaryBuilder.setUser(getRpcUser(inOrderSummary.getActor()));
        }
        return orderSummaryBuilder.build();
    }
    /**
     * Set the user from the given RPC user on the given order summary.
     *
     * @param inRpcUser a <code>BaseRpc.User</code> value
     * @param inOrderSummary a <code>MutableOrderSummary</code> value
     */
    public static void setUser(BaseRpc.User inRpcUser,
                               MutableOrderSummary inOrderSummary)
    {
        User user = userFactory.create(inRpcUser.getName(),
                                       "*******",
                                       inRpcUser.getDescription(),
                                       inRpcUser.getActive());
        inOrderSummary.setActor(user);
        inOrderSummary.setViewer(user);
    }
    /**
     * Get the RPC user from the given user.
     *
     * @param inUser a <code>User</code> value
     * @return a <code>BaseRpc.User</code> value
     */
    public static BaseRpc.User getRpcUser(User inUser)
    {
        BaseRpc.User.Builder userBuilder = BaseRpc.User.newBuilder();
        userBuilder.setActive(inUser.isActive());
        String value = StringUtils.trimToNull(inUser.getDescription());
        if(value != null) {
            userBuilder.setDescription(value);
        }
        value = StringUtils.trimToNull(inUser.getName());
        if(value != null) {
            userBuilder.setName(value);
        }
        return userBuilder.build();
    }
    /**
     * Get the symbolResolverService value.
     *
     * @return a <code>SymbolResolverService</code> value
     */
    public static SymbolResolverService getSymbolResolverService()
    {
        return symbolResolverService;
    }
    /**
     * Sets the symbolResolverService value.
     *
     * @param inSymbolResolverService a <code>SymbolResolverService</code> value
     */
    public static void setSymbolResolverService(SymbolResolverService inSymbolResolverService)
    {
        symbolResolverService = inSymbolResolverService;
    }
    /**
     * Get the fixSessionFactory value.
     *
     * @return a <code>FixSessionFactory</code> value
     */
    public static FixSessionFactory getFixSessionFactory()
    {
        return fixSessionFactory;
    }
    /**
     * Sets the fixSessionFactory value.
     *
     * @param inFixSessionFactory a <code>FixSessionFactory</code> value
     */
    public static void setFixSessionFactory(FixSessionFactory inFixSessionFactory)
    {
        fixSessionFactory = inFixSessionFactory;
    }
    /**
     * Get the orderSummaryFactory value.
     *
     * @return a <code>MutableOrderSummaryFactory</code> value
     */
    public static MutableOrderSummaryFactory getOrderSummaryFactory()
    {
        return orderSummaryFactory;
    }
    /**
     * Sets the orderSummaryFactory value.
     *
     * @param inOrderSummaryFactory a <code>MutableOrderSummaryFactory</code> value
     */
    public static void setOrderSummaryFactory(MutableOrderSummaryFactory inOrderSummaryFactory)
    {
        orderSummaryFactory = inOrderSummaryFactory;
    }
    /**
     * Get the userFactory value.
     *
     * @return a <code>UserFactory</code> value
     */
    public static UserFactory getUserFactory()
    {
        return userFactory;
    }
    /**
     * Sets the userFactory value.
     *
     * @param inUserFactory a <code>UserFactory</code> value
     */
    public static void setUserFactory(UserFactory inUserFactory)
    {
        userFactory = inUserFactory;
    }
    /**
     * Get the reportFactory value.
     *
     * @return a <code>MutableReportFactory</code> value
     */
    public static MutableReportFactory getReportFactory()
    {
        return reportFactory;
    }
    /**
     * Sets the reportFactory value.
     *
     * @param inReportFactory a <code>MutableReportFactory</code> value
     */
    public static void setReportFactory(MutableReportFactory inReportFactory)
    {
        reportFactory = inReportFactory;
    }
    /**
     * Set the values on the given FIX field map from the given RPC map.
     *
     * @param inRpcMap a <code>BaseRpc.Map</code> value
     * @param inFixFieldMap a <code>quickfix.FieldMap</code> value
     */
    private static void setFixMessageGroup(BaseRpc.Map inRpcMap,
                                           quickfix.FieldMap inFixFieldMap)
    {
        for(BaseRpc.KeyValuePair keyValuePair : inRpcMap.getKeyValuePairsList()) {
            String key = keyValuePair.getKey();
            String value = keyValuePair.getValue();
            try {
                inFixFieldMap.setString(Integer.parseInt(key),
                                        value);
            } catch (NumberFormatException e) {
                PlatformServices.handleException(TradingUtil.class,
                                                 "Skipping FIX message key/value pair: " + key + "/" + value,
                                                 e);
            }
        }
    }
    /**
     * Set the values on the given FIX field map from the given RPC map.
     *
     * @param inRpcMap a <code>BaseRpc.Map</code> value
     * @param inFixFieldMap a <code>quickfix.FieldMap</code> value
     */
    private static void setRpcFixMessageGroup(quickfix.FieldMap inFixFieldMap,
                                              BaseRpc.Map.Builder inBuilder)
    {
        Iterator<quickfix.Field<?>> fieldIterator = inFixFieldMap.iterator();
        BaseRpc.KeyValuePair.Builder keyValueBuilder = BaseRpc.KeyValuePair.newBuilder();
        while(fieldIterator.hasNext()) {
            quickfix.Field<?> field = fieldIterator.next();
            keyValueBuilder.setKey(String.valueOf(field.getField()));
            keyValueBuilder.setValue(String.valueOf(field.getObject()));
            inBuilder.addKeyValuePairs(keyValueBuilder.build());
            keyValueBuilder.clear();
        }
    }
    /**
     * provides symbol resolver services
     */
    private static SymbolResolverService symbolResolverService;
    /**
     * creates {@link FixSession} objects
     */
    private static FixSessionFactory fixSessionFactory;
    /**
     * creates {@link MutableOrderSummary} objects
     */
    private static MutableOrderSummaryFactory orderSummaryFactory;
    /**
     * creates {@link User} objects
     */
    private static UserFactory userFactory;
    /**
     * creates {@link MutableReport} objects
     */
    private static MutableReportFactory reportFactory;
}
