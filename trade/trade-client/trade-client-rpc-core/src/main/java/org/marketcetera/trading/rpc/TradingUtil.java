package org.marketcetera.trading.rpc;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.algo.BrokerAlgo;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.base.BaseUtil;
import org.marketcetera.symbol.SymbolResolverService;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.NewOrReplaceOrder;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderBase;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderCapacity;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.PositionEffect;
import org.marketcetera.trade.RelatedOrder;
import org.marketcetera.trade.SecurityType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.trading.rpc.TradingTypesRpc.FixMessage;

import com.google.common.collect.Maps;

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
     * 
     *
     *
     * @param inOrder
     * @param inOrderBuilder
     */
    public static void setInstrument(OrderBase inOrder,
                                     TradingTypesRpc.OrderBase.Builder inOrderBuilder)
    {
        if(inOrder.getInstrument() == null) {
            return;
        }
        inOrderBuilder.setInstrument(getRpcInstrument(inOrder.getInstrument()));
    }
    /**
     *
     *
     * @param inInstrument
     * @return
     */
    public static org.marketcetera.trading.rpc.TradingTypesRpc.Instrument getRpcInstrument(Instrument inInstrument)
    {
        TradingTypesRpc.Instrument.Builder instrumentBuilder = TradingTypesRpc.Instrument.newBuilder();
        instrumentBuilder.setSymbol(inInstrument.getFullSymbol());
        return instrumentBuilder.build();
    }
    /**
     *
     *
     * @param inRpcOrder
     * @return
     */
    private static Instrument getInstrument(TradingTypesRpc.OrderBase inRpcOrder)
    {
        return symbolResolverService.resolveSymbol(inRpcOrder.getInstrument().getSymbol());
    }
    /**
     * 
     *
     *
     * @param inOrder
     * @param inOrderBuilder
     */
    public static void setRpcCustomFields(OrderBase inOrder,
                                          TradingTypesRpc.OrderBase.Builder inOrderBuilder)
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
        inOrderBuilder.setCustomFields(mapBuilder.build());
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
     *
     *
     * @param inOrder
     * @param inOrderBuilder
     */
    public static void setAccount(OrderBase inOrder,
                                  TradingTypesRpc.OrderBase.Builder inOrderBuilder)
    {
        String value = StringUtils.trimToNull(inOrder.getAccount());
        if(value == null) {
            return;
        }
        inOrderBuilder.setAccount(value);
    }
    /**
     *
     *
     * @param inOrder
     * @param inOrderBuilder
     */
    public static void setText(OrderBase inOrder,
                               TradingTypesRpc.OrderBase.Builder inOrderBuilder)
    {
        String value = StringUtils.trimToNull(inOrder.getText());
        if(value == null) {
            return;
        }
        inOrderBuilder.setText(value);
    }
    /**
     *
     *
     * @param inRpcOrder
     * @return
     */
    public static String getText(org.marketcetera.trading.rpc.TradingTypesRpc.OrderBase inRpcOrder)
    {
        return StringUtils.trimToNull(inRpcOrder.getText());
    }
    /**
     *
     *
     * @param inOrder
     * @param inOrderBuilder
     */
    public static void setBrokerId(OrderBase inOrder,
                                   TradingTypesRpc.OrderBase.Builder inOrderBuilder)
    {
        if(inOrder.getBrokerID() == null) {
            return;
        }
        String value = StringUtils.trimToNull(inOrder.getBrokerID().getValue());
        inOrderBuilder.setBrokerId(value);
    }
    /**
    *
    *
    * @param inOrder
    * @param inOrderBuilder
    */
   public static void setBrokerId(FIXOrder inOrder,
                                  TradingTypesRpc.FIXOrder.Builder inOrderBuilder)
   {
       if(inOrder.getBrokerID() == null) {
           return;
       }
       String value = StringUtils.trimToNull(inOrder.getBrokerID().getValue());
       inOrderBuilder.setBrokerId(value);
   }
    /**
     *
     *
     * @param inOrder
     * @param inOrderBuilder
     */
    public static void setOrderId(OrderBase inOrder,
                                  TradingTypesRpc.OrderBase.Builder inOrderBuilder)
    {
        if(inOrder.getOrderID() == null) {
            return;
        }
        String value = StringUtils.trimToNull(inOrder.getOrderID().getValue());
        inOrderBuilder.setOrderId(value);
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
     * @param inOrderBuilder
     */
    public static void setQuantity(OrderBase inOrder,
                                   TradingTypesRpc.OrderBase.Builder inOrderBuilder)
    {
        if(inOrder.getQuantity() == null) {
            return;
        }
        inOrderBuilder.setQuantity(BaseUtil.getQtyValueFrom(inOrder.getQuantity()));
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
     * @param inOrderBuilder
     */
    public static void setSide(OrderBase inOrder,
                               TradingTypesRpc.OrderBase.Builder inOrderBuilder)
    {
        if(inOrder.getSide() == null) {
            return;
        }
        inOrderBuilder.setSide(getRpcSide(inOrder.getSide()));
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
     * @param inOrderBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setDisplayQuantity(NewOrReplaceOrder inOrder,
                                          TradingTypesRpc.OrderBase.Builder inOrderBuilder)
    {
        if(inOrder.getDisplayQuantity() == null || BigDecimal.ZERO.compareTo(inOrder.getDisplayQuantity()) == 0) {
            return;
        }
        inOrderBuilder.setDisplayQuantity(BaseUtil.getQtyValueFrom(inOrder.getDisplayQuantity()));
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
     * @param inOrderBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
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
     * @param inOrderBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
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
     * @param inOrderBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
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
     * @param inOrderBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
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
     * @param inOrderBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
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
     * @param inOrderBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
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
     * @param inOrderBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
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
     * @param inOrderBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setExecutionDestination(NewOrReplaceOrder inOrder,
                                               TradingTypesRpc.OrderBase inRpcOrder)
    {
        inOrder.setExecutionDestination(StringUtils.trimToNull(inRpcOrder.getExecutionDestination()));
    }
    /**
     * Set the peg-to-midpoint value from the given RPC order.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inOrderBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
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
     * @param inOrderBuilder a <code>TradingTypesRpc.OrderBase.Builder</code> value
     */
    public static void setBrokerAlgo(NewOrReplaceOrder inOrder,
                                     TradingTypesRpc.OrderBase inRpcOrder)
    {
        if(inRpcOrder.hasBrokerAlgo()) {
            inOrder.setBrokerAlgo(getBrokerAlgo(inRpcOrder));
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
     *
     *
     * @param inOrder
     * @param inOrderBuilder
     */
    public static void setOrderCapacity(NewOrReplaceOrder inOrder,
                                        TradingTypesRpc.OrderBase.Builder inOrderBuilder)
    {
        if(inOrder.getOrderCapacity() == null) {
            return;
        }
        inOrderBuilder.setOrderCapacity(getRpcOrderCapacity(inOrder.getOrderCapacity()));
    }
    /**
     *
     *
     * @param inOrder
     * @param inOrderBuilder
     */
    public static void setOrderType(NewOrReplaceOrder inOrder,
                                    TradingTypesRpc.OrderBase.Builder inOrderBuilder)
    {
        if(inOrder.getOrderType() == null) {
            return;
        }
        inOrderBuilder.setOrderType(getRpcOrderType(inOrder.getOrderType()));
    }
    /**
     *
     *
     * @param inOrder
     * @param inOrderBuilder
     */
    public static void setPositionEffect(NewOrReplaceOrder inOrder,
                                         TradingTypesRpc.OrderBase.Builder inOrderBuilder)
    {
        if(inOrder.getPositionEffect() == null) {
            return;
        }
        inOrderBuilder.setPositionEffect(getRpcPositionEffect(inOrder.getPositionEffect()));
    }
    /**
     *
     *
     * @param inOrder
     * @param inOrderBuilder
     */
    public static void setPrice(NewOrReplaceOrder inOrder,
                                TradingTypesRpc.OrderBase.Builder inOrderBuilder)
    {
        if(inOrder.getPrice() == null) {
            return;
        }
        inOrderBuilder.setPrice(BaseUtil.getQtyValueFrom(inOrder.getPrice()));
    }
    /**
     *
     *
     * @param inOrder
     * @param inOrderBuilder
     */
    public static void setTimeInForce(NewOrReplaceOrder inOrder,
                                      TradingTypesRpc.OrderBase.Builder inOrderBuilder)
    {
        if(inOrder.getTimeInForce() == null) {
            return;
        }
        inOrderBuilder.setTimeInForce(getRpcTimeInForce(inOrder.getTimeInForce()));
    }
    /**
     * Set the time in force value on the given MATP order from the given RPC order.
     *
     * @param inNewOrReplaceOrder a <code>NewOrReplaceOrder</code> value
     * @param inRpcOrderBase a <code>TradingTypesRpc.OrderBase</code> value
     */
    public static void setTimeInForce(NewOrReplaceOrder inNewOrReplaceOrder,
                                      TradingTypesRpc.OrderBase inRpcOrderBase)
    {
        TimeInForce matpTif = getTimeInForce(inRpcOrderBase.getTimeInForce());
        if(matpTif == null || matpTif == TimeInForce.Unknown) {
            return;
        }
        inNewOrReplaceOrder.setTimeInForce(matpTif);
    }
    /**
     * Set the position effect on the given MATP order from the given RPC order.
     *
     * @param inNewOrReplaceOrder a <code>NewOrReplaceOrder</code> value
     * @param inRpcOrderBase a <code>TradingTypesRpc.OrderBase</code> value
     */
    public static void setPositionEffect(NewOrReplaceOrder inNewOrReplaceOrder,
                                         TradingTypesRpc.OrderBase inRpcOrderBase)
    {
        PositionEffect matpPositionEffect = getPositionEffect(inRpcOrderBase.getPositionEffect());
        if(matpPositionEffect == null || matpPositionEffect == PositionEffect.Unknown) {
            return;
        }
        inNewOrReplaceOrder.setPositionEffect(matpPositionEffect);
    }
    /**
     * Set the order type on the given MATP order from the given RPC order.
     *
     * @param inNewOrReplaceOrder a <code>NewOrReplaceOrder</code> value
     * @param inRpcOrderBase a <code>TradingTypesRpc.OrderBase</code> value
     */
    public static void setOrderType(NewOrReplaceOrder inNewOrReplaceOrder,
                                    TradingTypesRpc.OrderBase inRpcOrderBase)
    {
        OrderType matpOrderType = getOrderType(inRpcOrderBase.getOrderType());
        if(matpOrderType == null || matpOrderType == OrderType.Unknown) {
            return;
        }
        inNewOrReplaceOrder.setOrderType(matpOrderType);
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
     * @param inNewOrReplaceOrder a <code>NewOrReplaceOrder</code> value
     * @param inRpcOrderBase a <code>TradingTypesRpc.OrderBase</code> value
     */
    public static void setOrderCapacity(NewOrReplaceOrder inNewOrReplaceOrder,
                                        TradingTypesRpc.OrderBase inRpcOrderBase)
    {
        OrderCapacity matpOrderCapacity = getOrderCapacity(inRpcOrderBase.getOrderCapacity());
        if(matpOrderCapacity == null || matpOrderCapacity == OrderCapacity.Unknown) {
            return;
        }
        inNewOrReplaceOrder.setOrderCapacity(matpOrderCapacity);
    }
    /**
     *
     *
     * @param inOrder
     * @param inOrderBuilder
     */
    public static void setOriginalOrderId(RelatedOrder inOrder,
                                          TradingTypesRpc.OrderBase.Builder inOrderBuilder)
    {
        if(inOrder.getOriginalOrderID() == null) {
            return;
        }
        String value = StringUtils.trimToNull(inOrder.getOriginalOrderID().getValue());
        inOrderBuilder.setOriginalOrderId(value);
    }
    /**
     * 
     *
     *
     * @param inObject
     * @return
     */
    public static BrokerID getBrokerId(Object inObject)
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
        }
        return brokerId;
    }
    /**
     * Get the MATP order value for the given RPC order.
     *
     * @param inRpcOrder a <code>TradingTypesRpc.Order</code> value
     * @return an <code>Order</code> value
     */
    public static Order getOrder(TradingTypesRpc.Order inRpcOrder)
    {
        BrokerID brokerId = getBrokerId(inRpcOrder);
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
     * 
     *
     *
     * @param inRpcOrder
     * @return
     */
    public static BrokerAlgo getBrokerAlgo(TradingTypesRpc.OrderBase inRpcOrder)
    {
        // TODO
        return null;
    }
    /**
     *
     *
     * @param inRpcMessage
     * @return
     */
    public static Message getFixMessage(FixMessage inRpcMessage)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /**
     *
     *
     * @param inOrder
     * @param inOrderResponseBuilder
     */
    public static void setOrderId(Order inOrder,
                                  TradingRpc.OrderResponse.Builder inOrderResponseBuilder)
    {
        if(inOrder instanceof OrderBase) {
            OrderBase order = (OrderBase)inOrder;
            if(order.getOrderID() != null) {
                inOrderResponseBuilder.setOrderid(order.getOrderID().getValue());
            }
        } else if(inOrder instanceof FIXOrder) {
            FIXOrder fixOrder = (FIXOrder)inOrder;
            Message message = fixOrder.getMessage();
            try {
                if(message.isSetField(quickfix.field.ClOrdID.FIELD)) {
                    inOrderResponseBuilder.setOrderid(message.getString(quickfix.field.ClOrdID.FIELD));
                } else if(message.isSetField(quickfix.field.OrderID.FIELD)) {
                    inOrderResponseBuilder.setOrderid(message.getString(quickfix.field.OrderID.FIELD));
                }
            } catch (FieldNotFound e) {
                PlatformServices.handleException(TradingUtil.class,
                                                 "Unable to set order id",
                                                 e);
            }
        }
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
     * provides symbol resolver services
     */
    private static SymbolResolverService symbolResolverService;
}
