package org.marketcetera.trading.rpc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.admin.AdminRpc;
import org.marketcetera.admin.User;
import org.marketcetera.admin.UserFactory;
import org.marketcetera.admin.rpc.AdminRpcUtil;
import org.marketcetera.algo.BrokerAlgo;
import org.marketcetera.algo.BrokerAlgoTag;
import org.marketcetera.algo.BrokerAlgoTagSpec;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.Validator;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.PositionKeyFactory;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.fix.FixAdminRpc;
import org.marketcetera.fix.FixRpcUtil;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.options.OptionUtils;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.base.BaseRpcUtil;
import org.marketcetera.trade.AverageFillPrice;
import org.marketcetera.trade.AverageFillPriceFactory;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ConvertibleBond;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.ExecutionReportSummary;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.FIXResponse;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.HasTradeMessage;
import org.marketcetera.trade.Hierarchy;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.MutableExecutionReportSummary;
import org.marketcetera.trade.MutableExecutionReportSummaryFactory;
import org.marketcetera.trade.MutableOrderSummary;
import org.marketcetera.trade.MutableOrderSummaryFactory;
import org.marketcetera.trade.MutableReport;
import org.marketcetera.trade.MutableReportFactory;
import org.marketcetera.trade.NewOrReplaceOrder;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
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
import org.marketcetera.trade.ReportID;
import org.marketcetera.trade.ReportType;
import org.marketcetera.trade.SecurityType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.UserID;
import org.marketcetera.trade.rpc.TradeRpc;
import org.marketcetera.trade.rpc.TradeRpc.TradeMessageListenerResponse;
import org.marketcetera.trade.rpc.TradeTypesRpc;

import com.google.common.collect.Maps;
import com.google.protobuf.Timestamp;

/* $License$ */

/**
 * Provides common behaviors for trading RPC services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class TradeRpcUtil
{
    /**
     * Get the RPC hierarchy value from the given value.
     *
     * @param inHierarchy a <code>Hierarchy</code> value
     * @return a <code>TradeTypesRpc.Hierarchy</code> value
     */
    public static TradeTypesRpc.Hierarchy getRpcHierarchy(Hierarchy inHierarchy)
    {
        switch(inHierarchy) {
            case Child:
                return TradeTypesRpc.Hierarchy.ChildHierarchy;
            case Flat:
                return TradeTypesRpc.Hierarchy.FlatHierarchy;
            case Parent:
                return TradeTypesRpc.Hierarchy.ParentHierarchy;
            default:
                throw new UnsupportedOperationException("Unsupported hierarchy: " + inHierarchy);
        }
    }
    /**
     * Get the hierarchy value from the given RPC hierarchy.
     *
     * @param inHierarchy a <code>TradeTypesRpc.Hierarchy</code> value
     * @return a <code>Hierarchy</code> value
     */
    public static Hierarchy getHierarchy(TradeTypesRpc.Hierarchy inHierarchy)
    {
        switch(inHierarchy) {
            case ChildHierarchy:
                return Hierarchy.Child;
            case FlatHierarchy:
                return Hierarchy.Flat;
            case ParentHierarchy:
                return Hierarchy.Parent;
            case UNRECOGNIZED:
            default:
                throw new UnsupportedOperationException();
        }
    }
    /**
     * Get the RPC time in force value for the given MATP time in force value.
     *
     * @param inTimeInForce a <code>TimeInForce</code> value
     * @return an <code>Optional&lt;TradeTypesRpc.TimeInForce&gt;</code> value
     */
    public static Optional<TradeTypesRpc.TimeInForce> getRpcTimeInForce(TimeInForce inTimeInForce)
    {
        TradeTypesRpc.TimeInForce result = null;
        if(inTimeInForce != null) {
            switch(inTimeInForce) {
                case AtTheClose:
                    result = TradeTypesRpc.TimeInForce.AtTheClose;
                    break;
                case AtTheOpening:
                    result = TradeTypesRpc.TimeInForce.AtTheOpening;
                    break;
                case Day:
                    result = TradeTypesRpc.TimeInForce.Day;
                    break;
                case FillOrKill:
                    result = TradeTypesRpc.TimeInForce.FillOrKill;
                    break;
                case GoodTillCancel:
                    result = TradeTypesRpc.TimeInForce.GoodTillCancel;
                    break;
                case GoodTillCrossing:
                    result = TradeTypesRpc.TimeInForce.GoodTillCrossing;
                    break;
                case GoodTillDate:
                    result = TradeTypesRpc.TimeInForce.GoodTillDate;
                    break;
                case ImmediateOrCancel:
                    result = TradeTypesRpc.TimeInForce.ImmediateOrCancel;
                    break;
                case Unknown:
                    result = TradeTypesRpc.TimeInForce.UnknownTimeInForce;
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported time in force: " + inTimeInForce);
            }
        }
        return Optional.ofNullable(result);
    }
    /**
     * Get the time in force value from the given RPC value.
     *
     * @param inTimeInForce a <code>TradingTypeRpc.TimeInForce</code> value
     * @return an <code>Optional&lt;TimeInForce&gt;</code> value
     */
    public static Optional<TimeInForce> getTimeInForce(TradeTypesRpc.TimeInForce inTimeInForce)
    {
        TimeInForce result = null;
        if(inTimeInForce != null) {
            switch(inTimeInForce) {
                case AtTheClose:
                    result = TimeInForce.AtTheClose;
                    break;
                case AtTheOpening:
                    result = TimeInForce.AtTheOpening;
                    break;
                case Day:
                    result = TimeInForce.Day;
                    break;
                case FillOrKill:
                    result = TimeInForce.FillOrKill;
                    break;
                case GoodTillCancel:
                    result = TimeInForce.GoodTillCancel;
                    break;
                case GoodTillCrossing:
                    result = TimeInForce.GoodTillCrossing;
                    break;
                case GoodTillDate:
                    result = TimeInForce.GoodTillDate;
                    break;
                case ImmediateOrCancel:
                    result = TimeInForce.ImmediateOrCancel;
                    break;
                case UnknownTimeInForce:
                case UNRECOGNIZED:
                    // null value
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported time in force: " + inTimeInForce);
            }
        }
        return Optional.ofNullable(result);
    }
    /**
     * Get the RPC value from the given value.
     *
     * @param inOrderCapacity an <code>OrderCapacity</code> value
     * @return a <code>TradeTypeRpc.OrderCapacity</code> value
     */
    public static TradeTypesRpc.OrderCapacity getRpcOrderCapacity(OrderCapacity inOrderCapacity)
    {
        switch(inOrderCapacity) {
            case Agency:
                return TradeTypesRpc.OrderCapacity.Agency;
            case AgentOtherMember:
                return TradeTypesRpc.OrderCapacity.AgentOtherMember;
            case Individual:
                return TradeTypesRpc.OrderCapacity.Individual;
            case Principal:
                return TradeTypesRpc.OrderCapacity.Principal;
            case Proprietary:
                return TradeTypesRpc.OrderCapacity.Proprietary;
            case RisklessPrincipal:
                return TradeTypesRpc.OrderCapacity.RisklessPrincipal;
            case Unknown:
                return TradeTypesRpc.OrderCapacity.UnknownOrderCapacity;
            default:
                throw new UnsupportedOperationException("Unsupported order capacity: " + inOrderCapacity);
        }
    }
    /**
     * Get the order capacity value from the given RPC order capacity.
     *
     * @param inOrderCapacity a <code>TradeTypesRpc.OrderCapacity</code> value
     * @return an <code>OrderCapacity</code> value
     */
    public static OrderCapacity getOrderCapacity(TradeTypesRpc.OrderCapacity inOrderCapacity)
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
     * Get the RPC position effect from the given position effect value.
     *
     * @param inPositionEffect a <code>PositionEffect</code> value
     * @return a <code>TradeTypesRpc.PositionEffect</code> value
     */
    public static TradeTypesRpc.PositionEffect getRpcPositionEffect(PositionEffect inPositionEffect)
    {
        switch(inPositionEffect) {
            case Close:
                return TradeTypesRpc.PositionEffect.Close;
            case Open:
                return TradeTypesRpc.PositionEffect.Open;
            case Unknown:
                return TradeTypesRpc.PositionEffect.UnknownPositionEffect;
            default:
                throw new UnsupportedOperationException("Unsupported position effect: " + inPositionEffect);
        }
    }
    /**
     * Get the position effect value from the given RPC position effect value.
     *
     * @param inPositionEffect a <code>TradeTypesRpc.PositionEffect</code> value
     * @return a <code>PositionEffect</code> value
     */
    public static PositionEffect getPositionEffect(TradeTypesRpc.PositionEffect inPositionEffect)
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
     * @param inOrderType a <code>TradeTypesRpc.OrderType</code> value
     * @return an <code>Optional&lt;OrderType&gt;</code> value
     */
    public static Optional<OrderType> getOrderType(TradeTypesRpc.OrderType inOrderType)
    {
        OrderType result = null;
        if(inOrderType != null) {
            switch(inOrderType) {
                case ForexLimit:
                    result = OrderType.ForexLimit;
                    break;
                case ForexMarket:
                    result = OrderType.ForexMarket;
                    break;
                case ForexPreviouslyQuoted:
                    result = OrderType.ForexPreviouslyQuoted;
                    break;
                case ForexSwap:
                    result = OrderType.ForexSwap;
                    break;
                case Funari:
                    result = OrderType.Funari;
                    break;
                case Limit:
                    result = OrderType.Limit;
                    break;
                case LimitOnClose:
                    result = OrderType.LimitOnClose;
                    break;
                case LimitOrBetter:
                    result = OrderType.LimitOrBetter;
                    break;
                case LimitWithOrWithout:
                    result = OrderType.LimitWithOrWithout;
                    break;
                case Market:
                    result = OrderType.Market;
                    break;
                case MarketOnClose:
                    result = OrderType.MarketOnClose;
                    break;
                case OnBasis:
                    result = OrderType.OnBasis;
                    break;
                case OnClose:
                    result = OrderType.OnClose;
                    break;
                case Pegged:
                    result = OrderType.Pegged;
                    break;
                case PreviouslyIndicated:
                    result = OrderType.PreviouslyIndicated;
                    break;
                case PreviouslyQuoted:
                    result = OrderType.PreviouslyQuoted;
                    break;
                case Stop:
                    result = OrderType.Stop;
                    break;
                case StopLimit:
                    result = OrderType.StopLimit;
                    break;
                case UnknownOrderType:
                    break;
                case WithOrWithout:
                    result = OrderType.WithOrWithout;
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported side: " + inOrderType);
            }
        }
        return Optional.ofNullable(result);
    }
    /**
     * Get an order type value from an RPC order type value.
     *
     * @param inOrderType an <code>OrderType</code> value
     * @return an <code>Optional&lt;TradeTypesRpc.OrderType&gt;</code> value
     */
    public static Optional<TradeTypesRpc.OrderType> getRpcOrderType(OrderType inOrderType)
    {
        TradeTypesRpc.OrderType result = null;
        if(inOrderType != null) {
            switch(inOrderType) {
                case ForexLimit:
                    result = TradeTypesRpc.OrderType.ForexLimit;
                    break;
                case ForexMarket:
                    result = TradeTypesRpc.OrderType.ForexMarket;
                    break;
                case ForexPreviouslyQuoted:
                    result = TradeTypesRpc.OrderType.ForexPreviouslyQuoted;
                    break;
                case ForexSwap:
                    result = TradeTypesRpc.OrderType.ForexSwap;
                    break;
                case Funari:
                    result = TradeTypesRpc.OrderType.Funari;
                    break;
                case Limit:
                    result = TradeTypesRpc.OrderType.Limit;
                    break;
                case LimitOnClose:
                    result = TradeTypesRpc.OrderType.LimitOnClose;
                    break;
                case LimitOrBetter:
                    result = TradeTypesRpc.OrderType.LimitOrBetter;
                    break;
                case LimitWithOrWithout:
                    result = TradeTypesRpc.OrderType.LimitWithOrWithout;
                    break;
                case Market:
                    result = TradeTypesRpc.OrderType.Market;
                    break;
                case MarketOnClose:
                    result = TradeTypesRpc.OrderType.MarketOnClose;
                    break;
                case OnBasis:
                    result = TradeTypesRpc.OrderType.OnBasis;
                    break;
                case OnClose:
                    result = TradeTypesRpc.OrderType.OnClose;
                    break;
                case Pegged:
                    result = TradeTypesRpc.OrderType.Pegged;
                    break;
                case PreviouslyIndicated:
                    result = TradeTypesRpc.OrderType.PreviouslyIndicated;
                    break;
                case PreviouslyQuoted:
                    result = TradeTypesRpc.OrderType.PreviouslyQuoted;
                    break;
                case Stop:
                    result = TradeTypesRpc.OrderType.Stop;
                    break;
                case StopLimit:
                    result = TradeTypesRpc.OrderType.StopLimit;
                    break;
                case Unknown:
                    result = TradeTypesRpc.OrderType.UnknownOrderType;
                    break;
                case WithOrWithout:
                    result = TradeTypesRpc.OrderType.WithOrWithout;
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported order type: " + inOrderType);
            }
        }
        return Optional.ofNullable(result);
    }
    /**
     * Get a side value from an RPC side type.
     *
     * @param inSideType a <code>TradeTypesRpc.Side</code> value
     * @return a <code>Side</code> value
     */
    public static Side getSide(TradeTypesRpc.Side inSideType)
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
     * @return a <code>TradeTypesRpc.Side</code> value
     */
    public static TradeTypesRpc.Side getRpcSide(Side inSide)
    {
        switch(inSide) {
            case Buy:
                return TradeTypesRpc.Side.Buy;
            case BuyMinus:
                return TradeTypesRpc.Side.BuyMinus;
            case Cross:
                return TradeTypesRpc.Side.Cross;
            case CrossShort:
                return TradeTypesRpc.Side.CrossShort;
            case Sell:
                return TradeTypesRpc.Side.Sell;
            case SellPlus:
                return TradeTypesRpc.Side.SellPlus;
            case SellShort:
                return TradeTypesRpc.Side.SellShort;
            case SellShortExempt:
                return TradeTypesRpc.Side.SellShortExempt;
            case Undisclosed:
                return TradeTypesRpc.Side.Undisclosed;
            case Unknown:
                return TradeTypesRpc.Side.UnknownSide;
            default:
                throw new UnsupportedOperationException("Unsupported side: " + inSide);
        }
    }
    /**
     * Get a MATP security type from an RPC security type.
     *
     * @param inSecurityType a <code>TradeTypesRpc.SecurityType</code> value
     * @return an <code>SecurityType</code> value
     */
    public static SecurityType getSecurityType(TradeTypesRpc.SecurityType inSecurityType)
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
     * @return a <code>TradeTypesRpc.SecurityType</code> value
     */
    public static TradeTypesRpc.SecurityType getRpcSecurityType(SecurityType inSecurityType)
    {
        switch(inSecurityType) {
            case CommonStock:
                return TradeTypesRpc.SecurityType.CommonStock;
            case ConvertibleBond:
                return TradeTypesRpc.SecurityType.ConvertibleBond;
            case Currency:
                return TradeTypesRpc.SecurityType.Currency;
            case Future:
                return TradeTypesRpc.SecurityType.Future;
            case Option:
                return TradeTypesRpc.SecurityType.Option;
            case Unknown:
                return TradeTypesRpc.SecurityType.UnknownSecurityType;
            default:
                throw new UnsupportedOperationException("Unsupported security type: " + inSecurityType);
        }
    }
    /**
     * Set the instrument from the given order on the given builder.
     *
     * @param inOrder an <code>OrderBase</code> value
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setInstrument(OrderBase inOrder,
                                     TradeTypesRpc.OrderBase.Builder inBuilder)
    {
        if(inOrder.getInstrument() == null) {
            return;
        }
        getRpcInstrument(inOrder.getInstrument()).ifPresent(value->inBuilder.setInstrument(value));
    }
    /**
     * Set the instrument value on the given builder.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inBuilder a <code>TradeRpc.ResolveSymbolResponse.Builder</code> value
     */
    public static void setInstrument(Instrument inInstrument,
                                     TradeRpc.ResolveSymbolResponse.Builder inBuilder)
    {
        if(inInstrument == null) {
            return;
        }
        getRpcInstrument(inInstrument).ifPresent(value->inBuilder.setInstrument(value));
    }
    /**
     * Get the RPC instrument from the given instrument.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @return an <code>Optional&lt;TradeTypesRpc.Instrument&gt;</code>value
     */
    public static Optional<TradeTypesRpc.Instrument> getRpcInstrument(Instrument inInstrument)
    {
        if(inInstrument == null) {
            return Optional.empty();
        }
        TradeTypesRpc.Instrument.Builder instrumentBuilder = TradeTypesRpc.Instrument.newBuilder();
        instrumentBuilder.setSymbol(inInstrument.getFullSymbol());
        instrumentBuilder.setSecurityType(getRpcSecurityType(inInstrument.getSecurityType()));
        return Optional.of(instrumentBuilder.build());
    }
    /**
     * Get the RPC instrument from the given attributes.
     *
     * @param inSymbol a <code>String</code> value
     * @param inSecurityType a <code>SecurityType</code> value
     * @param inOptionType an <code>OptionType</code> value
     * @param inExpiry a <code>String</code> value
     * @param inStrikePrice a <code>BigDecimal</code> value
     * @return an <code>Optional&lt;TradeTypesRpc.Instrument&gt;</code>value
     */
    public static Optional<TradeTypesRpc.Instrument> getRpcInstrument(String inSymbol,
                                                                      SecurityType inSecurityType,
                                                                      OptionType inOptionType,
                                                                      String inExpiry,
                                                                      BigDecimal inStrikePrice)
    {
        if(inSymbol == null || inSecurityType == null) {
            return Optional.empty();
        }
        Instrument instrument;
        switch(inSecurityType) {
            case CommonStock:
                instrument = new Equity(inSymbol);
                break;
            case ConvertibleBond:
                instrument = new ConvertibleBond(inSymbol);
                break;
            case Currency:
                instrument = new Currency(inSymbol);
                break;
            case Future:
                instrument = Future.fromString(inSymbol);
                break;
            case Option:
                instrument = new Option(inSymbol,
                                        inExpiry,
                                        inStrikePrice,
                                        inOptionType);
                break;
            case Unknown:
            default:
                throw new UnsupportedOperationException("Unsupported security type: " + inSecurityType);
            
        }
        TradeTypesRpc.Instrument.Builder builder = TradeTypesRpc.Instrument.newBuilder();
        builder.setSymbol(instrument.getFullSymbol());
        builder.setSecurityType(getRpcSecurityType(inSecurityType));
        return Optional.of(builder.build());
    }
    /**
     * Get the instrument on the given RPC order base object.
     *
     * @param inRpcOrder a <code>TradingTypeRpc.OrderBase</code> value
     * @return an <code>Optional&lt;Instrument&gt;</code> value
     */
    private static Optional<Instrument> getInstrument(TradeTypesRpc.OrderBase inRpcOrder)
    {
        if(!inRpcOrder.hasInstrument() || inRpcOrder.getInstrument().getSymbol() == null) {
            return Optional.empty();
        }
        return getInstrument(inRpcOrder.getInstrument());
    }
    /**
     * Get the instrument value from the given RPC instrument object.
     *
     * @param inRpcInstrument a <code>TradeTypesRpc.Instrument</code> value
     * @return an <code>Optional&lt;Instrument&gt;</code> value
     */
    public static Optional<Instrument> getInstrument(TradeTypesRpc.Instrument inRpcInstrument)
    {
        if(inRpcInstrument == null) {
            return Optional.empty();
        }
        switch(inRpcInstrument.getSecurityType()) {
            case CommonStock:
                return Optional.of(new Equity(inRpcInstrument.getSymbol()));
            case ConvertibleBond:
                return Optional.of(new ConvertibleBond(inRpcInstrument.getSymbol()));
            case Currency:
                return Optional.of(new Currency(inRpcInstrument.getSymbol()));
            case Future:
                return Optional.of(Future.fromString(inRpcInstrument.getSymbol()));
            case Option:
                return Optional.of(OptionUtils.getOsiOptionFromString(inRpcInstrument.getSymbol()));
            default:
            case UnknownSecurityType:
            case UNRECOGNIZED:
                throw new UnsupportedOperationException("Unknown security type: " + inRpcInstrument.getSecurityType());
        }
    }
    /**
     * Get the instrument on the given RPC order summary object.
     *
     * @param inRpcOrderSummary a <code>TradingTypeRpc.OrderSummary</code> value
     * @return an <code>Optional&lt;Instrument&gt;</code> value
     */
    private static Optional<Instrument> getInstrument(TradeTypesRpc.OrderSummary inRpcOrderSummary)
    {
        if(!inRpcOrderSummary.hasInstrument() || inRpcOrderSummary.getInstrument().getSymbol() == null) {
            return Optional.empty();
        }
        return getInstrument(inRpcOrderSummary.getInstrument());
    }
    /**
     * Set the RPC custom fields from the given order.
     *
     * @param inOrder an <code>OrderBase</code> value
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setRpcCustomFields(OrderBase inOrder,
                                          TradeTypesRpc.OrderBase.Builder inBuilder)
    {
        if(inOrder.getCustomFields() == null || inOrder.getCustomFields().isEmpty()) {
            return;
        }
        BaseRpc.Map.Builder mapBuilder = BaseRpc.Map.newBuilder();
        BaseRpc.KeyValuePair.Builder keyValuePairBuilder = BaseRpc.KeyValuePair.newBuilder();
        for(Map.Entry<String,String> entry : inOrder.getCustomFields().entrySet()) {
            keyValuePairBuilder.setKey(entry.getKey());
            keyValuePairBuilder.setValue(entry.getValue());
            mapBuilder.addKeyValuePairs(keyValuePairBuilder.build());
            keyValuePairBuilder.clear();
        }
        inBuilder.setCustomFields(mapBuilder.build());
    }
    /**
     * Set the custom fields from the given RPC order.
     *
     * @param inRpcOrder a <code>TradeTypesRpc.OrderBase</code> value
     * @param inOrder an <code>OrderBase</code> value
     */
    public static void setCustomFields(TradeTypesRpc.OrderBase inRpcOrder,
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
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setAccount(OrderBase inOrder,
                                  TradeTypesRpc.OrderBase.Builder inBuilder)
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
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setAccount(ExecutionReport inExecutionReport,
                                  TradeTypesRpc.TradeMessage.Builder inBuilder)
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
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setUserId(ReportBase inReportBase,
                                 TradeTypesRpc.TradeMessage.Builder inBuilder)
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
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setUserId(FIXResponse inReport,
                                 TradeTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getActorID() == null) {
            return;
        }
        inBuilder.setUser(String.valueOf(inReport.getActorID()));
    }
    /**
     * Set the text value on the given RPC builder from the given order.
     *
     * @param inOrder an <code>OrderBase</code>value
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setText(OrderBase inOrder,
                               TradeTypesRpc.OrderBase.Builder inBuilder)
    {
        String value = StringUtils.trimToNull(inOrder.getText());
        if(value == null) {
            return;
        }
        inBuilder.setText(value);
    }
    /**
     * Get the text value from the given RPC order.
     *
     * @param inRpcOrder a <code>TradeTypesRpc.OrderBase</code> value
     * @return a <code>String</code> value
     */
    public static String getText(TradeTypesRpc.OrderBase inRpcOrder)
    {
        return StringUtils.trimToNull(inRpcOrder.getText());
    }
    /**
     * Set the broker ID from the given order on the given RPC builder.
     *
     * @param inOrder an <code>OrderBase</code> value
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setBrokerId(OrderBase inOrder,
                                   TradeTypesRpc.OrderBase.Builder inBuilder)
    {
        if(inOrder.getBrokerID() == null) {
            return;
        }
        String value = StringUtils.trimToNull(inOrder.getBrokerID().getValue());
        inBuilder.setBrokerId(value);
    }
    /**
     * Set the broker ID from the given report on the given RPC builder.
     *
     * @param inReport a <code>Report</code> value
     * @param inBuilder a <code>TradeTypesRpc.Report.Builder</code> value
     */
    public static void setBrokerId(Report inReport,
                                   TradeTypesRpc.Report.Builder inBuilder)
    {
        if(inReport.getBrokerID() == null) {
            return;
        }
        String value = StringUtils.trimToNull(inReport.getBrokerID().getValue());
        inBuilder.setBrokerId(value);
    }
    /**
     * Set the broker ID from the given order on the given RPC builder.
     *
     * @param inOrder a <code>FIXOrder</code> value
     * @param inBuilder a <code>TradeTypesRpc.FIXOrder.Builder</code> value
     */
    public static void setBrokerId(FIXOrder inOrder,
                                   TradeTypesRpc.FIXOrder.Builder inBuilder)
    {
        if(inOrder.getBrokerID() == null) {
            return;
        }
        String value = StringUtils.trimToNull(inOrder.getBrokerID().getValue());
        inBuilder.setBrokerId(value);
    }
    /**
     * Set the order ID from the given order on the given RPC builder.
     *
     * @param inOrder an <code>OrderBase</code> value
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setOrderId(OrderBase inOrder,
                                  TradeTypesRpc.OrderBase.Builder inBuilder)
    {
        if(inOrder.getOrderID() == null) {
            return;
        }
        String value = StringUtils.trimToNull(inOrder.getOrderID().getValue());
        inBuilder.setOrderId(value);
    }
    /**
     * Get the broker order id value from the given message, if possible.
     *
     * @param inRpcExecutionReportSummary a <code>TradeTypesRpc.ExecutionReportSummary</code> value
     * @return an <code>Optional&lt;OrderID&gt;</code> value
     */
    public static Optional<OrderID> getBrokerOrderId(TradeTypesRpc.ExecutionReportSummary inRpcExecutionReportSummary)
    {
        return getOrderId(inRpcExecutionReportSummary.getBrokerOrderId());
    }
    /**
     * Get the order id value from the given message, if possible.
     *
     * @param inRpcExecutionReportSummary a <code>TradeTypesRpc.ExecutionReportSummary</code> value
     * @return an <code>Optional&lt;OrderID&gt;</code> value
     */
    public static Optional<OrderID> getOrderId(TradeTypesRpc.ExecutionReportSummary inRpcExecutionReportSummary)
    {
        return getOrderId(inRpcExecutionReportSummary.getOrderId());
    }
    /**
     * Get the root order id value from the given message, if possible.
     *
     * @param inRpcExecutionReportSummary a <code>TradeTypesRpc.ExecutionReportSummary</code> value
     * @return an <code>Optional&lt;OrderID&gt;</code> value
     */
    public static Optional<OrderID> getRootOrderId(TradeTypesRpc.ExecutionReportSummary inRpcExecutionReportSummary)
    {
        return getOrderId(inRpcExecutionReportSummary.getRootOrderId());
    }
    /**
     * Get the original order id value from the given message, if possible.
     *
     * @param inRpcExecutionReportSummary a <code>TradeTypesRpc.ExecutionReportSummary</code> value
     * @return an <code>Optional&lt;OrderID&gt;</code> value
     */
    public static Optional<OrderID> getOriginalOrderId(TradeTypesRpc.ExecutionReportSummary inRpcExecutionReportSummary)
    {
        return getOrderId(inRpcExecutionReportSummary.getOriginalOrderId());
    }
    /**
     * Get the order id value from the given value, if possible.
     *
     * @param inValue a <code>String</code> value
     * @return an <code>Optional&lt;OrderID&gt;</code> value
     */
    public static Optional<OrderID> getOrderId(String inValue)
    {
        OrderID orderId = null;
        if(inValue != null) {
            orderId = new OrderID(inValue);
        }
        return Optional.ofNullable(orderId);
    }
    /**
     * Get the RPC order id value from the given value, if possible.
     *
     * @param inValue an <code>OrderID</code> value
     * @return an <code>Optional&lt;OrderID&gt;</code> value
     */
    public static Optional<String> getRpcOrderId(OrderID inValue)
    {
        String orderId = null;
        if(inValue != null) {
            orderId = inValue.getValue();
        }
        return Optional.ofNullable(orderId);
    }
    /**
     * Get the order ID from the given RPC order.
     *
     * @param inRpcOrder a <code>TradeTypesRpc.OrderBase</code> value
     * @return an <code>OrderID</code> value
     */
    public static OrderID getOrderId(TradeTypesRpc.OrderBase inRpcOrder)
    {
        return new OrderID(inRpcOrder.getOrderId());
    }
    /**
     * Set the order quantity from the given order on the given RPC builder.
     *
     * @param inOrder an <code>OrderBase</code> value
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setQuantity(OrderBase inOrder,
                                   TradeTypesRpc.OrderBase.Builder inBuilder)
    {
        BaseRpcUtil.getRpcQty(inOrder.getQuantity()).ifPresent(qty->inBuilder.setQuantity(qty));
    }
    /**
     * Get the order quantity from the given RPC order.
     *
     * @param inRpcOrder a <code>TradeTypesRpc.OrderBase</code> value
     * @return an <code>Optional&lt;BigDecimal&gt;</code> value
     */
    public static Optional<BigDecimal> getQuantity(TradeTypesRpc.OrderBase inRpcOrder)
    {
        return BaseRpcUtil.getScaledQuantity(inRpcOrder.getQuantity());
    }
    /**
     * Get the order price from the given RPC order.
     *
     * @param inRpcOrder a <code>TradeTypesRpc.OrderBase</code> value
     * @return an <code>Optional&lt;BigDecimal&gt;</code> value
     */
    public static Optional<BigDecimal> getPrice(TradeTypesRpc.OrderBase inRpcOrder)
    {
        return BaseRpcUtil.getScaledQuantity(inRpcOrder.getPrice());
    }
    /**
     * Get the display quantity of the given order.
     *
     * @param inRpcOrder a <code>TradeTypesRpc.OrderBase</code> value
     * @return an <code>Optional&lt;BigDecimal&gt;</code> value
     */
    public static Optional<BigDecimal> getDisplayQuantity(TradeTypesRpc.OrderBase inRpcOrder)
    {
        return BaseRpcUtil.getScaledQuantity(inRpcOrder.getDisplayQuantity());
    }
    /**
     * Set the side from the given order on the given RPC builder.
     *
     * @param inOrder an <code>OrderBase</code> value
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setSide(OrderBase inOrder,
                               TradeTypesRpc.OrderBase.Builder inBuilder)
    {
        if(inOrder.getSide() == null) {
            return;
        }
        inBuilder.setSide(getRpcSide(inOrder.getSide()));
    }
    /**
     * Get the side from the given RPC order.
     *
     * @param inRpcOrder a <code>TradeTypesRpc.OrderBase</code> value
     * @return a <code>Side</code> value
     */
    public static Side getSide(TradeTypesRpc.OrderBase inRpcOrder)
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
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setDisplayQuantity(NewOrReplaceOrder inOrder,
                                          TradeTypesRpc.OrderBase.Builder inBuilder)
    {
        if(inOrder.getDisplayQuantity() == null || BigDecimal.ZERO.compareTo(inOrder.getDisplayQuantity()) == 0) {
            return;
        }
        BaseRpcUtil.getRpcQty(inOrder.getDisplayQuantity()).ifPresent(qty->inBuilder.setDisplayQuantity(qty));
    }
    /**
     * Set the display quantity from the given RPC order on the given order.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inRpcOrder a <code>TradeTypesRpc.OrderBase</code> value
     */
    public static void setDisplayQuantity(NewOrReplaceOrder inOrder,
                                          TradeTypesRpc.OrderBase inRpcOrder)
    {
        BaseRpcUtil.getScaledQuantity(inRpcOrder.getDisplayQuantity()).ifPresent(value->inOrder.setDisplayQuantity(value));
    }
    /**
     * Set the order ID from the given RPC order.
     *
     * @param inOrder an <code>OrderBase</code> value
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setOrderId(OrderBase inOrder,
                                  TradeTypesRpc.OrderBase inRpcOrder)
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
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setOriginalOrderId(RelatedOrder inOrder,
                                          TradeTypesRpc.OrderBase inRpcOrder)
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
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setBrokerOrderId(RelatedOrder inOrder,
                                        TradeTypesRpc.OrderBase inRpcOrder)
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
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setBrokerId(OrderBase inOrder,
                                   TradeTypesRpc.OrderBase inRpcOrder)
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
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setInstrument(OrderBase inOrder,
                                     TradeTypesRpc.OrderBase inRpcOrder)
    {
        if(inRpcOrder.hasInstrument()) {
            inOrder.setInstrument(getInstrument(inRpcOrder).orElse(null));
        }
    }
    /**
     * Set the account from the given RPC order.
     *
     * @param inOrder an <code>OrderBase</code> value
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setAccount(OrderBase inOrder,
                                  TradeTypesRpc.OrderBase inRpcOrder)
    {
        String value = StringUtils.trimToNull(inRpcOrder.getAccount());
        if(value != null) {
            inOrder.setAccount(value);
        }
    }
    /**
     * Set the text from the given RPC order.
     *
     * @param inOrder an <code>OrderBase</code> value
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setText(OrderBase inOrder,
                               TradeTypesRpc.OrderBase inRpcOrder)
    {
        String value = StringUtils.trimToNull(inRpcOrder.getText());
        if(value != null) {
            inOrder.setText(value);
        }
    }
    /**
     * Set the execution destination from the given RPC order.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setExecutionDestination(NewOrReplaceOrder inOrder,
                                               TradeTypesRpc.OrderBase inRpcOrder)
    {
        String value = StringUtils.trimToNull(inRpcOrder.getExecutionDestination());
        if(value != null) {
            inOrder.setExecutionDestination(value);
        }
    }
    /**
     * Set the execution destination from the given RPC order.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setExecutionDestination(NewOrReplaceOrder inOrder,
                                               TradeTypesRpc.OrderBase.Builder inBuilder)
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
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setPegToMidpoint(NewOrReplaceOrder inOrder,
                                        TradeTypesRpc.OrderBase inRpcOrder)
    {
        inOrder.setPegToMidpoint(inRpcOrder.getPegToMidpoint());
    }
    /**
     * Set the broker algo from the given RPC order.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setBrokerAlgo(NewOrReplaceOrder inOrder,
                                     TradeTypesRpc.OrderBase inRpcOrder)
    {
        if(inRpcOrder.hasBrokerAlgo()) {
            inOrder.setBrokerAlgo(getBrokerAlgo(inRpcOrder).orElse(null));
        }
    }
    /**
     * Set the price from the given RPC order.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inRpcOrder a <code>TradeTypesRpc.OrderBase</code> value
     */
    public static void setPrice(NewOrReplaceOrder inOrder,
                                TradeTypesRpc.OrderBase inRpcOrder)
    {
        BaseRpcUtil.getScaledQuantity(inRpcOrder.getPrice()).ifPresent(value->inOrder.setPrice(value));
    }
    /**
     * Set the quantity from the given RPC order.
     *
     * @param inOrder an <code>OrderBase</code> value
     * @param inRpcOrder a <code>TradeTypesRpc.OrderBase</code> value
     */
    public static void setQuantity(OrderBase inOrder,
                                   TradeTypesRpc.OrderBase inRpcOrder)
    {
        BaseRpcUtil.getScaledQuantity(inRpcOrder.getQuantity()).ifPresent(value->inOrder.setQuantity(value));
    }
    /**
     * Set the order capacity from the given order on the given builder.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setOrderCapacity(NewOrReplaceOrder inOrder,
                                        TradeTypesRpc.OrderBase.Builder inBuilder)
    {
        if(inOrder.getOrderCapacity() == null) {
            return;
        }
        inBuilder.setOrderCapacity(getRpcOrderCapacity(inOrder.getOrderCapacity()));
    }
    /**
     * Set the order type value from the given order on the given RPC builder.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setOrderType(NewOrReplaceOrder inOrder,
                                    TradeTypesRpc.OrderBase.Builder inBuilder)
    {
        getRpcOrderType(inOrder.getOrderType()).ifPresent(rpcOrderType->inBuilder.setOrderType(rpcOrderType));
    }
    /**
     * Set the position effect from the given order on the given RPC builder.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setPositionEffect(NewOrReplaceOrder inOrder,
                                         TradeTypesRpc.OrderBase.Builder inBuilder)
    {
        if(inOrder.getPositionEffect() == null) {
            return;
        }
        inBuilder.setPositionEffect(getRpcPositionEffect(inOrder.getPositionEffect()));
    }
    /**
     * Set the order price from the given order on the given RPC builder.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setPrice(NewOrReplaceOrder inOrder,
                                TradeTypesRpc.OrderBase.Builder inBuilder)
    {
        BaseRpcUtil.getRpcQty(inOrder.getPrice()).ifPresent(qty->inBuilder.setPrice(qty));
    }
    /**
     * Set the time in force on the given RPC builder from the given order.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setTimeInForce(NewOrReplaceOrder inOrder,
                                      TradeTypesRpc.OrderBase.Builder inBuilder)
    {
        getRpcTimeInForce(inOrder.getTimeInForce()).ifPresent(rpcTimeInForce->inBuilder.setTimeInForce(rpcTimeInForce));
    }
    /**
     * Set the time in force value on the given MATP order from the given RPC order.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inRpcOrder a <code>TradeTypesRpc.OrderBase</code> value
     */
    public static void setTimeInForce(NewOrReplaceOrder inOrder,
                                      TradeTypesRpc.OrderBase inRpcOrder)
    {
        getTimeInForce(inRpcOrder.getTimeInForce()).ifPresent(timeInForce->inOrder.setTimeInForce(timeInForce));
    }
    /**
     * Set the position effect on the given MATP order from the given RPC order.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     * @param inRpcOrder a <code>TradeTypesRpc.OrderBase</code> value
     */
    public static void setPositionEffect(NewOrReplaceOrder inOrder,
                                         TradeTypesRpc.OrderBase inRpcOrder)
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
     * @param inRpcOrder a <code>TradeTypesRpc.OrderBase</code> value
     */
    public static void setOrderType(NewOrReplaceOrder inOrder,
                                    TradeTypesRpc.OrderBase inRpcOrder)
    {
        getOrderType(inRpcOrder.getOrderType()).ifPresent(orderType->inOrder.setOrderType(orderType));
    }
    /**
     * Set the order type on the given MATP order from the given RPC order.
     *
     * @param inOrder an <code>OrderBase</code> value
     * @param inRpcOrder a <code>TradeTypesRpc.OrderBase</code> value
     */
    public static void setSide(OrderBase inOrder,
                               TradeTypesRpc.OrderBase inRpcOrder)
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
     * @param inRpcOrder a <code>TradeTypesRpc.OrderBase</code> value
     */
    public static void setOrderCapacity(NewOrReplaceOrder inOrder,
                                        TradeTypesRpc.OrderBase inRpcOrder)
    {
        OrderCapacity matpOrderCapacity = getOrderCapacity(inRpcOrder.getOrderCapacity());
        if(matpOrderCapacity == null || matpOrderCapacity == OrderCapacity.Unknown) {
            return;
        }
        inOrder.setOrderCapacity(matpOrderCapacity);
    }
    /**
     * Set the original order ID from the given order on the given RPC builder.
     *
     * @param inOrder a <code>RelatedOrder</code> value
     * @param inBuilder a <code>TradeTypesRpc.OrderBase.Builder</code> value
     */
    public static void setOriginalOrderId(RelatedOrder inOrder,
                                          TradeTypesRpc.OrderBase.Builder inBuilder)
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
        if(inObject instanceof TradeTypesRpc.FIXOrder) {
            String value = StringUtils.trimToNull(((TradeTypesRpc.FIXOrder)inObject).getBrokerId());
            if(value != null) {
                brokerId = new BrokerID(value);
            }
        } else if(inObject instanceof TradeTypesRpc.OrderBase) {
            String value = StringUtils.trimToNull(((TradeTypesRpc.OrderBase)inObject).getBrokerId());
            if(value != null) {
                brokerId = new BrokerID(value);
            }
        } else if(inObject instanceof TradeTypesRpc.TradeMessage) {
            String value = StringUtils.trimToNull(((TradeTypesRpc.TradeMessage)inObject).getBrokerId());
            if(value != null) {
                brokerId = new BrokerID(value);
            }
        } else if(inObject instanceof TradeTypesRpc.OrderSummary) {
            String value = StringUtils.trimToNull(((TradeTypesRpc.OrderSummary)inObject).getBrokerId());
            if(value != null) {
                brokerId = new BrokerID(value);
            }
        } else if(inObject instanceof TradeRpc.AddReportRequest) {
            String value = StringUtils.trimToNull(((TradeRpc.AddReportRequest)inObject).getBrokerId());
            if(value != null) {
                brokerId = new BrokerID(value);
            }
        } else if(inObject instanceof TradeTypesRpc.Report) {
            String value = StringUtils.trimToNull(((TradeTypesRpc.Report)inObject).getBrokerId());
            if(value != null) {
                brokerId = new BrokerID(value);
            }
        }
        return Optional.ofNullable(brokerId);
    }
    /**
     * Get the broker order ID from the given RPC report.
     *
     * @param inObject a <code>TradeTypesRpc.Report</code> value
     * @return an <code>Optional&lt;OrderID&gt;</code> value
     */
    public static Optional<OrderID> getBrokerOrderId(TradeTypesRpc.Report inReport)
    {
        OrderID brokerOrderId = null;
        String value = StringUtils.trimToNull(((TradeTypesRpc.Report)inReport).getBrokerId());
        if(value != null) {
            brokerOrderId = new OrderID(value);
        }
        return Optional.ofNullable(brokerOrderId);
    }
    /**
     * Get the MATP order value for the given RPC order.
     *
     * @param inRpcOrder a <code>TradeTypesRpc.Order</code> value
     * @return an <code>Order</code> value
     */
    public static Order getOrder(TradeTypesRpc.Order inRpcOrder)
    {
        BrokerID brokerId = getBrokerId(inRpcOrder).orElse(null);
        TradeTypesRpc.OrderBase rpcOrderBase = null;
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
     * @param inReport a <code>TradeTypesRpc.TradeMessage</code> value
     * @return a <code>TradeMessage</code> value
     */
    public static TradeMessage getTradeMessage(TradeTypesRpc.TradeMessage inRpcTradeMessage)
    {
        TradeMessage tradeMessage = null;
        quickfix.Message fixMessage = getFixMessage(inRpcTradeMessage.getMessage());
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
     * Get the RPC report from the given report.
     *
     * @param inReport a <code>Report</code> value
     * @return a <code>TradeTypesRpc.Report</code> value
     */
    public static TradeTypesRpc.Report getRpcReport(Report inReport)
    {
        TradeTypesRpc.Report.Builder reportBuilder = TradeTypesRpc.Report.newBuilder();
        AdminRpcUtil.getRpcUser(inReport.getActor()).ifPresent(rpcActor->reportBuilder.setActor(rpcActor));
        if(inReport.getBrokerID() != null) {
            reportBuilder.setBrokerId(inReport.getBrokerID().getValue());
        }
        reportBuilder.setHierarchy(getRpcHierarchy(inReport.getHierarchy()));
        if(inReport.getFixMessage() != null) {
            try {
                reportBuilder.setMessage(getRpcFixMessage(new quickfix.Message(inReport.getFixMessage())));
            } catch (quickfix.InvalidMessage e) {
                throw new IllegalArgumentException(e);
            }
        }
        reportBuilder.setMsgSeqNum(inReport.getMsgSeqNum());
        if(inReport.getOrderID() != null) {
            reportBuilder.setOrderId(inReport.getOrderID().getValue());
        }
        reportBuilder.setOriginator(getRpcOriginator(inReport.getOriginator()));
        if(inReport.getReportID() != null) {
            reportBuilder.setReportId(inReport.getReportID().longValue());
        }
        reportBuilder.setReportType(getRpcReportType(inReport.getReportType()));
        if(inReport.getText() != null) {
            reportBuilder.setText(inReport.getText());
        }
        BaseRpcUtil.getTimestampValue(inReport.getSendingTime()).ifPresent(rpcTimestamp->reportBuilder.setSendingTime(rpcTimestamp));
        if(inReport.getSessionId() != null) {
            reportBuilder.setSessionId(inReport.getSessionId().toString());
        }
        BaseRpcUtil.getTimestampValue(inReport.getTransactTime()).ifPresent(rpcTimestamp->reportBuilder.setTransactTime(rpcTimestamp));
        AdminRpcUtil.getRpcUser(inReport.getViewer()).ifPresent(rpcViewer->reportBuilder.setViewer(rpcViewer));
        return reportBuilder.build();
    }
    /**
     * Get the average fill price value from the given RPC value.
     *
     * @param inRpcAverageFillPrice a <code>TradeTypesRpc.AverageFillPrice</code> value
     * @return an <code>AverageFillPrice</code> value
     */
    public static AverageFillPrice getAverageFillPrice(TradeTypesRpc.AverageFillPrice inRpcAverageFillPrice,
                                                       AverageFillPriceFactory inAverageFillPriceFactory)
    {
        Instrument instrument = getInstrument(inRpcAverageFillPrice.getInstrument()).orElse(null);
        Side side = getSide(inRpcAverageFillPrice.getSide());
        BigDecimal cumulativeQuantity = BaseRpcUtil.getScaledQuantity(inRpcAverageFillPrice.getCumulativeQuantity()).orElse(BigDecimal.ZERO);
        BigDecimal averagePrice = BaseRpcUtil.getScaledQuantity(inRpcAverageFillPrice.getAveragePrice()).orElse(BigDecimal.ZERO);
        AverageFillPrice executionReportSummary = inAverageFillPriceFactory.create(instrument,
                                                                                   side,
                                                                                   cumulativeQuantity,
                                                                                   averagePrice);
        return executionReportSummary;
    }
    /**
     * Get the RPC average fill price value from the given value.
     *
     * @param inAverageFillPrice an <code>AverageFillPrice</code> value
     * @return a <code>TradeTypesRpc.AverageFillPrice</code> value
     */
    public static TradeTypesRpc.AverageFillPrice getRpcAverageFillPrice(AverageFillPrice inAverageFillPrice)
    {
        TradeTypesRpc.AverageFillPrice.Builder builder = TradeTypesRpc.AverageFillPrice.newBuilder();
        BaseRpcUtil.getRpcQty(inAverageFillPrice.getAveragePrice()).ifPresent(value->builder.setAveragePrice(value));
        BaseRpcUtil.getRpcQty(inAverageFillPrice.getCumulativeQuantity()).ifPresent(value->builder.setCumulativeQuantity(value));
        getRpcInstrument(inAverageFillPrice.getInstrument()).ifPresent(rpcInstrument->builder.setInstrument(rpcInstrument));
        builder.setSide(getRpcSide(inAverageFillPrice.getSide()));
        return builder.build();
    }
    /**
     * Get the execution report summary from the given RPC value.
     *
     * @param inRpcExecutionReport a <code>TradeTypesRpc.ExecutionReportSummary</code> value
     * @param inExecutionReportSummaryFactory a <code>MutableExecutionReportSummaryFactory</code> value
     * @param inReportFactory a <code>MutableReportFactory</code> value
     * @param inUserFactory a <code>UserFactory</code> value
     * @return an <code>ExecutionReportSummary</code> value
     */
    public static ExecutionReportSummary getExecutionReportSummary(TradeTypesRpc.ExecutionReportSummary inRpcExecutionReport,
                                                                   MutableExecutionReportSummaryFactory inExecutionReportSummaryFactory,
                                                                   MutableReportFactory inReportFactory,
                                                                   UserFactory inUserFactory)
    {
        MutableExecutionReportSummary executionReportSummary = inExecutionReportSummaryFactory.create();
        executionReportSummary.setAccount(inRpcExecutionReport.getAccount());
        AdminRpcUtil.getUser(inRpcExecutionReport.getActor(),
                             inUserFactory).ifPresent(actor->executionReportSummary.setActor(actor));
        BaseRpcUtil.getScaledQuantity(inRpcExecutionReport.getAveragePrice()).ifPresent(qty->executionReportSummary.setAveragePrice(qty));
        getBrokerOrderId(inRpcExecutionReport).ifPresent(brokerOrderId->executionReportSummary.setBrokerOrderId(brokerOrderId));
        BaseRpcUtil.getScaledQuantity(inRpcExecutionReport.getCumulativeQuantity()).ifPresent(qty->executionReportSummary.setCumulativeQuantity(qty));
        BaseRpcUtil.getScaledQuantity(inRpcExecutionReport.getEffectiveCumulativeQuantity()).ifPresent(qty->executionReportSummary.setEffectiveCumulativeQuantity(qty));
        executionReportSummary.setExecutionId(inRpcExecutionReport.getExecutionId());
        executionReportSummary.setExecutionType(getExecutionType(inRpcExecutionReport.getExecutionType()));
        getInstrument(inRpcExecutionReport.getInstrument()).ifPresent(instrument->executionReportSummary.setInstrument(instrument));
        BaseRpcUtil.getScaledQuantity(inRpcExecutionReport.getLastPrice()).ifPresent(qty->executionReportSummary.setLastPrice(qty));
        BaseRpcUtil.getScaledQuantity(inRpcExecutionReport.getLastQuantity()).ifPresent(qty->executionReportSummary.setLastQuantity(qty));
        BaseRpcUtil.getScaledQuantity(inRpcExecutionReport.getLeavesQuantity()).ifPresent(qty->executionReportSummary.setLeavesQuantity(qty));
        getOrderId(inRpcExecutionReport).ifPresent(orderId->executionReportSummary.setOrderID(orderId));
        BaseRpcUtil.getScaledQuantity(inRpcExecutionReport.getOrderQuantity()).ifPresent(qty->executionReportSummary.setOrderQuantity(qty));
        executionReportSummary.setOrderStatus(getOrderStatus(inRpcExecutionReport.getOrderStatus()));
        getOrderType(inRpcExecutionReport.getOrderType()).ifPresent(orderType->executionReportSummary.setOrderType(orderType));
        getOriginalOrderId(inRpcExecutionReport).ifPresent(orderId->executionReportSummary.setOriginalOrderID(orderId));
        BaseRpcUtil.getScaledQuantity(inRpcExecutionReport.getPrice()).ifPresent(qty->executionReportSummary.setPrice(qty));
        executionReportSummary.setReport(getReport(inRpcExecutionReport.getReport(),
                                                   inReportFactory,
                                                   inUserFactory));
        getRootOrderId(inRpcExecutionReport).ifPresent(orderId->executionReportSummary.setRootOrderID(orderId));
        BaseRpcUtil.getDateValue(inRpcExecutionReport.getSendingTime()).ifPresent(sendingTime->executionReportSummary.setSendingTime(sendingTime));
        executionReportSummary.setSide(getSide(inRpcExecutionReport.getSide()));
        getTimeInForce(inRpcExecutionReport.getTimeInForce()).ifPresent(timeInForce->executionReportSummary.setTimeInForce(timeInForce));
        AdminRpcUtil.getUser(inRpcExecutionReport.getViewer(),
                             inUserFactory).ifPresent(viewer->executionReportSummary.setViewer(viewer));
        return executionReportSummary;
    }
    /**
     * Get the RPC execution report summary from the given value.
     *
     * @param inExecutionReportSummary an <code>ExecutionReportSummary</code> value
     * @return a <code>TradeTypesRpc.ExecutionReportSummary</code> value
     */
    public static TradeTypesRpc.ExecutionReportSummary getRpcExecutionReportSummary(ExecutionReportSummary inExecutionReportSummary)
    {
        TradeTypesRpc.ExecutionReportSummary.Builder builder = TradeTypesRpc.ExecutionReportSummary.newBuilder();
        if(inExecutionReportSummary.getAccount() != null) {
            builder.setAccount(inExecutionReportSummary.getAccount());
        }
        AdminRpcUtil.getRpcUser(inExecutionReportSummary.getActor()).ifPresent(value->builder.setActor(value));
        BaseRpcUtil.getRpcQty(inExecutionReportSummary.getAveragePrice()).ifPresent(value->builder.setAveragePrice(value));
        getRpcOrderId(inExecutionReportSummary.getBrokerOrderId()).ifPresent(value->builder.setBrokerOrderId(value));
        BaseRpcUtil.getRpcQty(inExecutionReportSummary.getCumulativeQuantity()).ifPresent(value->builder.setCumulativeQuantity(value));
        BaseRpcUtil.getRpcQty(inExecutionReportSummary.getEffectiveCumulativeQuantity()).ifPresent(value->builder.setEffectiveCumulativeQuantity(value));
        if(inExecutionReportSummary.getExecutionId() != null) {
            builder.setExecutionId(inExecutionReportSummary.getExecutionId());
        }
        builder.setExecutionType(getRpcExecutionType(inExecutionReportSummary.getExecutionType()));
        getRpcInstrument(inExecutionReportSummary.getSymbol(),
                         inExecutionReportSummary.getSecurityType(),
                         inExecutionReportSummary.getOptionType(),
                         inExecutionReportSummary.getExpiry(),
                         inExecutionReportSummary.getStrikePrice()).ifPresent(rpcInstrument->builder.setInstrument(rpcInstrument));
        BaseRpcUtil.getRpcQty(inExecutionReportSummary.getLastPrice()).ifPresent(value->builder.setLastPrice(value));
        BaseRpcUtil.getRpcQty(inExecutionReportSummary.getLastQuantity()).ifPresent(value->builder.setLastQuantity(value));
        BaseRpcUtil.getRpcQty(inExecutionReportSummary.getLeavesQuantity()).ifPresent(value->builder.setLeavesQuantity(value));
        getRpcOrderId(inExecutionReportSummary.getOrderID()).ifPresent(value->builder.setOrderId(value));
        BaseRpcUtil.getRpcQty(inExecutionReportSummary.getOrderQuantity()).ifPresent(value->builder.setOrderQuantity(value));
        builder.setOrderStatus(getRpcOrderStatus(inExecutionReportSummary.getOrderStatus()));
        getRpcOrderType(inExecutionReportSummary.getOrderType()).ifPresent(rpcOrderType->builder.setOrderType(rpcOrderType));
        getRpcOrderId(inExecutionReportSummary.getOriginalOrderID()).ifPresent(value->builder.setOriginalOrderId(value));
        BaseRpcUtil.getRpcQty(inExecutionReportSummary.getPrice()).ifPresent(value->builder.setPrice(value));
        builder.setReport(getRpcReport(inExecutionReportSummary.getReport()));
        getRpcOrderId(inExecutionReportSummary.getRootOrderID()).ifPresent(value->builder.setRootOrderId(value));
        BaseRpcUtil.getTimestampValue(inExecutionReportSummary.getSendingTime()).ifPresent(value->builder.setSendingTime(value));
        builder.setSide(getRpcSide(inExecutionReportSummary.getSide()));
        getRpcTimeInForce(inExecutionReportSummary.getTimeInForce()).ifPresent(rpcTimeInForce->builder.setTimeInForce(rpcTimeInForce));
        AdminRpcUtil.getRpcUser(inExecutionReportSummary.getViewer()).ifPresent(value->builder.setViewer(value));
        return builder.build();
    }
    /**
     * Get the report from the given RPC report.
     *
     * @param inRpcReport a <code>TradeTypesRpc.Report</code> value
     * @param inReportFactory a <code>MutableReportFactory</code> value
     * @param inUserFactory a <code>UserFactory</code> value
     * @return a <code>Report</code> value
     */
    public static Report getReport(TradeTypesRpc.Report inRpcReport,
                                   MutableReportFactory inReportFactory,
                                   UserFactory inUserFactory)
    {
        MutableReport report = inReportFactory.create();
        if(inRpcReport.hasActor()) {
            AdminRpcUtil.getUser(inRpcReport.getActor(),
                                 inUserFactory).ifPresent(actor->report.setActor(actor));
        }
        getBrokerId(inRpcReport).ifPresent(brokerId->report.setBrokerID(brokerId));
        report.setFixMessage(getFixMessage(inRpcReport.getMessage()).toString());
        report.setHierarchy(getHierarchy(inRpcReport.getHierarchy()));
        report.setMsgSeqNum(inRpcReport.getMsgSeqNum());
        report.setOrderID(new OrderID(inRpcReport.getOrderId()));
        report.setOriginator(getOriginator(inRpcReport.getOriginator()));
        report.setReportID(new ReportID(inRpcReport.getReportId()));
        report.setReportType(getReportType(inRpcReport.getReportType()));
        BaseRpcUtil.getDateValue(inRpcReport.getSendingTime()).ifPresent(sendingTime->report.setSendingTime(sendingTime));
        report.setSessionId(new quickfix.SessionID(inRpcReport.getSessionId()));
        report.setText(inRpcReport.getText());
        BaseRpcUtil.getDateValue(inRpcReport.getTransactTime()).ifPresent(transactTime->report.setTransactTime(transactTime));
        if(inRpcReport.hasViewer()) {
            AdminRpcUtil.getUser(inRpcReport.getViewer(),
                                 inUserFactory).ifPresent(viewer->report.setViewer(viewer));
        }
        return report;
    }
    /**
     * Get the RPC trade message from the given trade message.
     *
     * @param inTradeMessage a <code>TradeMessage</code> value
     * @return a <code>TradeTypesRpc.TradeMessage</code> value
     */
    public static TradeTypesRpc.TradeMessage getRpcTradeMessage(TradeMessage inTradeMessage)
    {
        TradeTypesRpc.TradeMessage.Builder tradeMessageBuilder = TradeTypesRpc.TradeMessage.newBuilder();
        ReportBase reportBase = null;
        ExecutionReport executionReport = null;
        FIXResponse fixResponse = null;
        if(inTradeMessage instanceof ReportBase) {
            reportBase = (ReportBase)inTradeMessage;
        }
        if(inTradeMessage instanceof ExecutionReport) {
            executionReport = (ExecutionReport)inTradeMessage;
            tradeMessageBuilder.setTradeMessageType(TradeTypesRpc.TradeMessageType.TradeMessageExecutionReport);
        } else if(inTradeMessage instanceof OrderCancelReject) {
            tradeMessageBuilder.setTradeMessageType(TradeTypesRpc.TradeMessageType.TradeMessageOrderCancelReject);
        } else if(inTradeMessage instanceof FIXResponse) {
            fixResponse = (FIXResponse)inTradeMessage;
            tradeMessageBuilder.setTradeMessageType(TradeTypesRpc.TradeMessageType.TradeMessageFixResponse);
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
     * @param inBuilder a <code>TradeRpc.TradeMessageListenerResponse.Builder</code>
     */
    public static void setTradeMessage(TradeMessage inTradeMessage,
                                       TradeRpc.TradeMessageListenerResponse.Builder inBuilder)
    {
        inBuilder.setTradeMessage(getRpcTradeMessage(inTradeMessage));
    }
    /**
     * Set the FIX message from the given message holder on the given builder.
     *
     * @param inMessageHolder a <code>HasFIXMessage</code> value
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setFixMessage(HasFIXMessage inMessageHolder,
                                     TradeTypesRpc.TradeMessage.Builder inBuilder)
    {
        TradeTypesRpc.FixMessage.Builder fixMessageBuilder = TradeTypesRpc.FixMessage.newBuilder();
        BaseRpc.Map.Builder mapBuilder = BaseRpc.Map.newBuilder();
        quickfix.Message fixMessage = inMessageHolder.getMessage();
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
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setTransactTime(ExecutionReport inReport,
                                       TradeTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getTransactTime() == null) {
            return;
        }
        Instant time = inReport.getTransactTime().toInstant();
        inBuilder.setTransactTime(Timestamp.newBuilder().setSeconds(time.getEpochSecond()).setNanos(time.getNano()).build());
    }
    /**
     * Set the time in force from the given report on the given builder.
     *
     * @param inReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setTimeInForce(ExecutionReport inReport,
                                      TradeTypesRpc.TradeMessage.Builder inBuilder)
    {
        getRpcTimeInForce(inReport.getTimeInForce()).ifPresent(rpcTimeInForce->inBuilder.setTimeInForce(rpcTimeInForce));
    }
    /**
     * Set the side from the given report on the given builder.
     *
     * @param inReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setSide(ExecutionReport inReport,
                               TradeTypesRpc.TradeMessage.Builder inBuilder)
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
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setPrice(ExecutionReport inExecutionReport,
                                TradeTypesRpc.TradeMessage.Builder inBuilder)
    {
        BaseRpcUtil.getRpcQty(inExecutionReport.getPrice()).ifPresent(qty->inBuilder.setPrice(qty));
    }
    /**
     * Set the position effect from the given report on the given builder.
     *
     * @param inReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setPositionEffect(ExecutionReport inReport,
                                         TradeTypesRpc.TradeMessage.Builder inBuilder)
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
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setOrderType(ExecutionReport inReport,
                                    TradeTypesRpc.TradeMessage.Builder inBuilder)
    {
        getRpcOrderType(inReport.getOrderType()).ifPresent(rpcOrderType->inBuilder.setOrderType(rpcOrderType));
    }
    /**
     * Set the order quantity from value the given trade message on the given builder.
     *
     * @param inExecutionReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setOrderQuantity(ExecutionReport inExecutionReport,
                                        TradeTypesRpc.TradeMessage.Builder inBuilder)
    {
        BaseRpcUtil.getRpcQty(inExecutionReport.getOrderQuantity()).ifPresent(qty->inBuilder.setOrderQuantity(qty));
    }
    /**
     * Set the order display quantity from value the given trade message on the given builder.
     *
     * @param inExecutionReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setOrderDisplayQuantity(ExecutionReport inExecutionReport,
                                               TradeTypesRpc.TradeMessage.Builder inBuilder)
    {
        BaseRpcUtil.getRpcQty(inExecutionReport.getOrderDisplayQuantity()).ifPresent(qty->inBuilder.setOrderDisplayQuantity(qty));
    }
    /**
     * Set the order capacity from the given report on the given builder.
     *
     * @param inReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setOrderCapacity(ExecutionReport inReport,
                                        TradeTypesRpc.TradeMessage.Builder inBuilder)
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
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setLeavesQuantity(ExecutionReport inExecutionReport,
                                         TradeTypesRpc.TradeMessage.Builder inBuilder)
    {
        BaseRpcUtil.getRpcQty(inExecutionReport.getLeavesQuantity()).ifPresent(qty->inBuilder.setLeavesQuantity(qty));
    }
    /**
     * Set the last quantity from value the given trade message on the given builder.
     *
     * @param inExecutionReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setLastQuantity(ExecutionReport inExecutionReport,
                                       TradeTypesRpc.TradeMessage.Builder inBuilder)
    {
        BaseRpcUtil.getRpcQty(inExecutionReport.getLastQuantity()).ifPresent(qty->inBuilder.setLastQuantity(qty));
    }
    /**
     * Set the last price from value the given trade message on the given builder.
     *
     * @param inExecutionReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setLastPrice(ExecutionReport inExecutionReport,
                                    TradeTypesRpc.TradeMessage.Builder inBuilder)
    {
        BaseRpcUtil.getRpcQty(inExecutionReport.getLastPrice()).ifPresent(qty->inBuilder.setLastPrice(qty));
    }
    /**
     * Set the last market from the given report on the given builder.
     *
     * @param inExecutionReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setLastMarket(ExecutionReport inExecutionReport,
                                     TradeTypesRpc.TradeMessage.Builder inBuilder)
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
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setInstrument(ExecutionReport inReport,
                                     TradeTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getInstrument() == null) {
            return;
        }
        getRpcInstrument(inReport.getInstrument()).ifPresent(value->inBuilder.setInstrument(value));
    }
    /**
     * Get the RPC execution type value from the given value.
     *
     * @param inExecutionType an <code>ExecutionType</code> value
     * @return a <code>TradeTypesRpc.ExecutionType</code> value
     */
    public static TradeTypesRpc.ExecutionType getRpcExecutionType(ExecutionType inExecutionType)
    {
        switch(inExecutionType) {
            case Calculated:
                return TradeTypesRpc.ExecutionType.CalculatedExecutionType;
            case Canceled:
                return TradeTypesRpc.ExecutionType.CanceledExecutionType;
            case DoneForDay:
                return TradeTypesRpc.ExecutionType.DoneForDayExecutionType;
            case Expired:
                return TradeTypesRpc.ExecutionType.ExpiredExecutionType;
            case Fill:
                return TradeTypesRpc.ExecutionType.FillExecutionType;
            case New:
                return TradeTypesRpc.ExecutionType.NewExecutionType;
            case OrderStatus:
                return TradeTypesRpc.ExecutionType.OrderStatusExecutionType;
            case PartialFill:
                return TradeTypesRpc.ExecutionType.PartialFillExecutionType;
            case PendingCancel:
                return TradeTypesRpc.ExecutionType.PendingCancelExecutionType;
            case PendingNew:
                return TradeTypesRpc.ExecutionType.PendingNewExecutionType;
            case PendingReplace:
                return TradeTypesRpc.ExecutionType.PendingReplaceExecutionType;
            case Rejected:
                return TradeTypesRpc.ExecutionType.RejectedExecutionType;
            case Replace:
                return TradeTypesRpc.ExecutionType.ReplaceExecutionType;
            case Restated:
                return TradeTypesRpc.ExecutionType.RestatedExecutionType;
            case Stopped:
                return TradeTypesRpc.ExecutionType.StoppedExecutionType;
            case Suspended:
                return TradeTypesRpc.ExecutionType.SuspendedExecutionType;
            case Trade:
                return TradeTypesRpc.ExecutionType.TradeExecutionType;
            case TradeCancel:
                return TradeTypesRpc.ExecutionType.TradeCancelExecutionType;
            case TradeCorrect:
                return TradeTypesRpc.ExecutionType.TradeCorrectExecutionType;
            case Unknown:
                return TradeTypesRpc.ExecutionType.UnknownExecutionType;
            default:
                throw new UnsupportedOperationException("Unsupported execution type: " + inExecutionType);
        }
    }
    /**
     * Get the RPC execution type value from the given value.
     *
     * @param inExecutionType a <code>TradeTypesRpc.ExecutionType</code> value
     * @return an <code>ExecutionType</code> value
     */
    public static ExecutionType getExecutionType(TradeTypesRpc.ExecutionType inExecutionType)
    {
        switch(inExecutionType) {
            case CalculatedExecutionType:
                return ExecutionType.Calculated;
            case CanceledExecutionType:
                return ExecutionType.Canceled;
            case DoneForDayExecutionType:
                return ExecutionType.DoneForDay;
            case ExpiredExecutionType:
                return ExecutionType.Expired;
            case FillExecutionType:
                return ExecutionType.Fill;
            case NewExecutionType:
                return ExecutionType.New;
            case OrderStatusExecutionType:
                return ExecutionType.OrderStatus;
            case PartialFillExecutionType:
                return ExecutionType.PartialFill;
            case PendingCancelExecutionType:
                return ExecutionType.PendingCancel;
            case PendingNewExecutionType:
                return ExecutionType.PendingNew;
            case PendingReplaceExecutionType:
                return ExecutionType.PendingReplace;
            case RejectedExecutionType:
                return ExecutionType.Rejected;
            case ReplaceExecutionType:
                return ExecutionType.Replace;
            case RestatedExecutionType:
                return ExecutionType.Restated;
            case StoppedExecutionType:
                return ExecutionType.Stopped;
            case SuspendedExecutionType:
                return ExecutionType.Suspended;
            case TradeCancelExecutionType:
                return ExecutionType.TradeCancel;
            case TradeCorrectExecutionType:
                return ExecutionType.TradeCorrect;
            case TradeExecutionType:
                return ExecutionType.Trade;
            case UNRECOGNIZED:
            case UnknownExecutionType:
                return ExecutionType.Unknown;
            default:
                throw new UnsupportedOperationException("Unsupported execution type: " + inExecutionType);
        }
    }
    /**
     * Set the execution type from the given report on the given builder.
     *
     * @param inReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setExecutionType(ExecutionReport inReport,
                                        TradeTypesRpc.TradeMessage.Builder inBuilder)
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
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setExecutionId(ExecutionReport inReport,
                                      TradeTypesRpc.TradeMessage.Builder inBuilder)
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
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setCumulativeQuantity(ExecutionReport inExecutionReport,
                                             TradeTypesRpc.TradeMessage.Builder inBuilder)
    {
        BaseRpcUtil.getRpcQty(inExecutionReport.getCumulativeQuantity()).ifPresent(qty->inBuilder.setCumulativeQuantity(qty));
    }
    /**
     * Set the average price from value the given trade message on the given builder.
     *
     * @param inExecutionReport an <code>ExecutionReport</code> value
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setAveragePrice(ExecutionReport inExecutionReport,
                                       TradeTypesRpc.TradeMessage.Builder inBuilder)
    {
        BaseRpcUtil.getRpcQty(inExecutionReport.getAveragePrice()).ifPresent(qty->inBuilder.setAveragePrice(qty));
    }
    /**
     * Set the broker ID from value the given trade message on the given builder.
     *
     * @param inReportBase a <code>ReportBase</code> value
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setBrokerId(ReportBase inReportBase,
                                   TradeTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReportBase.getBrokerId() == null) {
            return;
        }
        inBuilder.setBrokerId(String.valueOf(inReportBase.getBrokerId()));
    }
    /**
     * Set the broker ID from value the given trade message on the given builder.
     *
     * @param inReport a <code>FIXResponse</code> value
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setBrokerId(FIXResponse inReport,
                                   TradeTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getBrokerId() == null) {
            return;
        }
        inBuilder.setBrokerId(String.valueOf(inReport.getBrokerId()));
    }
    /**
     * Set the broker order ID from the given report on the given builder.
     *
     * @param inReport a <code>ReportBase</code> value
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setBrokerOrderId(ReportBase inReport,
                                        TradeTypesRpc.TradeMessage.Builder inBuilder)
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
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setOrderId(ReportBase inReport,
                                  TradeTypesRpc.TradeMessage.Builder inBuilder)
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
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setReportId(ReportBase inReport,
                                  TradeTypesRpc.TradeMessage.Builder inBuilder)
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
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setSendingTime(ReportBase inReport,
                                      TradeTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getSendingTime() == null) {
            return;
        }
        Instant time = inReport.getSendingTime().toInstant();
        inBuilder.setSendingTime(Timestamp.newBuilder().setSeconds(time.getEpochSecond()).setNanos(time.getNano()).build());
    }
    /**
     * Set the text from the given report on the given builder.
     *
     * @param inReport a <code>ReportBase</code> value
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setText(ReportBase inReport,
                               TradeTypesRpc.TradeMessage.Builder inBuilder)
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
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setOriginalOrderId(ReportBase inReport,
                                          TradeTypesRpc.TradeMessage.Builder inBuilder)
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
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setHierarchy(ReportBase inReport,
                                    TradeTypesRpc.TradeMessage.Builder inBuilder)
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
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setOrderStatus(ReportBase inReport,
                                      TradeTypesRpc.TradeMessage.Builder inBuilder)
    {
        if(inReport.getOrderStatus() == null) {
            return;
        }
        inBuilder.setOrderStatus(getRpcOrderStatus(inReport.getOrderStatus()));
    }
    /**
     * Set the order status from the given RPC value on the given order summary.
     *
     * @param inRpcOrderSummary a <code>TradeTypesRpc.OrderSummary</code> value
     * @param inOrderSummary a <code>MutableOrderSummary</code> value
     */
    public static void setOrderStatus(TradeTypesRpc.OrderSummary inRpcOrderSummary,
                                      MutableOrderSummary inOrderSummary)
    {
        inOrderSummary.setOrderStatus(getOrderStatus(inRpcOrderSummary.getOrderStatus()));
    }
    /**
     * Set the originator from the given report on the given builder.
     *
     * @param inReport a <code>ReportBase</code> value
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setOriginator(ReportBase inReport,
                                     TradeTypesRpc.TradeMessage.Builder inBuilder)
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
     * @param inBuilder a <code>TradeTypesRpc.TradeMessage.Builder</code> value
     */
    public static void setOriginator(FIXResponse inReport,
                                     TradeTypesRpc.TradeMessage.Builder inBuilder)
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
     * @return a <code>TradeTypesRpc.OrderStatus</code> value
     */
    public static TradeTypesRpc.OrderStatusType getRpcOrderStatus(OrderStatus inOrderStatus)
    {
        switch(inOrderStatus) {
            case AcceptedForBidding:
                return TradeTypesRpc.OrderStatusType.AcceptedForBidding;
            case Calculated:
                return TradeTypesRpc.OrderStatusType.Calculated;
            case Canceled:
                return TradeTypesRpc.OrderStatusType.Canceled;
            case DoneForDay:
                return TradeTypesRpc.OrderStatusType.DoneForDay;
            case Expired:
                return TradeTypesRpc.OrderStatusType.Expired;
            case Filled:
                return TradeTypesRpc.OrderStatusType.Filled;
            case New:
                return TradeTypesRpc.OrderStatusType.New;
            case PartiallyFilled:
                return TradeTypesRpc.OrderStatusType.PartiallyFilled;
            case PendingCancel:
                return TradeTypesRpc.OrderStatusType.PendingCancel;
            case PendingNew:
                return TradeTypesRpc.OrderStatusType.PendingNew;
            case PendingReplace:
                return TradeTypesRpc.OrderStatusType.PendingReplace;
            case Rejected:
                return TradeTypesRpc.OrderStatusType.Rejected;
            case Replaced:
                return TradeTypesRpc.OrderStatusType.Replaced;
            case Stopped:
                return TradeTypesRpc.OrderStatusType.Stopped;
            case Suspended:
                return TradeTypesRpc.OrderStatusType.Suspended;
            case Unknown:
                return TradeTypesRpc.OrderStatusType.UnknownOrderStatus;
            default:
                throw new UnsupportedOperationException("Unsupported order status: " + inOrderStatus);
        }
    }
    /**
     * Get the order status value from the given RPC value.
     *
     * @param inRpcOrderStatus a <code>TradeTypesRpc.OrderStatusType</code> value
     * @return an <code>OrderStatus</code> value
     */
    public static OrderStatus getOrderStatus(TradeTypesRpc.OrderStatusType inRpcOrderStatus)
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
     * @return a <code>TradeTypesRpc.Originator</code> value
     */
    public static TradeTypesRpc.Originator getRpcOriginator(Originator inOriginator)
    {
        switch(inOriginator) {
            case Broker:
                return TradeTypesRpc.Originator.BrokerOriginator;
            case Server:
                return TradeTypesRpc.Originator.ServerOriginator;
            default:
                throw new UnsupportedOperationException("Unsupported originator: " + inOriginator);
        }
    }
    /**
     * Get the user ID value from the given RPC message.
     * 
     * @param inRpcTradeMessage a <code>TradeTypesRpc.OrderBase</code> value 
     * @return an <code>Optional&lt;UserID&gt;</code> value
     */
    private static Optional<UserID> getUserId(TradeTypesRpc.TradeMessage inRpcTradeMessage)
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
     * @param inRpcTradeMessage a <code>TradeTypesRpc.TradeMessage</code> value 
     * @return an <code>Originator</code> value
     */
    private static Originator getOriginator(TradeTypesRpc.TradeMessage inRpcTradeMessage)
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
     * Get the originator value from the given RPC message.
     *
     * @param inRpcOriginator a <code>TradeTypesRpc.Originator</code> value 
     * @return an <code>Originator</code> value
     */
    private static Originator getOriginator(TradeTypesRpc.Originator inRpcOriginator)
    {
        switch(inRpcOriginator) {
            case BrokerOriginator:
                return Originator.Broker;
            case ServerOriginator:
                return Originator.Server;
            case UNRECOGNIZED:
            default:
                throw new UnsupportedOperationException("Unsupported originator: " + inRpcOriginator);
        }
    }
    /**
     * Get the report type value from the given RPC report type.
     *
     * @param inRpcReportType a <code>TradeTypesRpc.ReportType</code> value 
     * @return a <code>ReportType</code> value
     */
    private static ReportType getReportType(TradeTypesRpc.ReportType inRpcReportType)
    {
        switch(inRpcReportType) {
            case CancelReject:
                return ReportType.CancelReject;
            case ExecutionReport:
                return ReportType.ExecutionReport;
            case UNRECOGNIZED:
            default:
                throw new UnsupportedOperationException("Unsupported report type: " + inRpcReportType);
        }
    }
    /**
     * Get the RPC report type value from the given report type.
     *
     * @param inReportType a <code>ReportType</code> value 
     * @return a <code>TradeTypesRpc.ReportType</code> value
     */
    private static TradeTypesRpc.ReportType getRpcReportType(ReportType inReportType)
    {
        switch(inReportType) {
            case CancelReject:
                return TradeTypesRpc.ReportType.CancelReject;
            case ExecutionReport:
                return TradeTypesRpc.ReportType.ExecutionReport;
            default:
                throw new UnsupportedOperationException("Unsupported report type: " + inReportType);
        }
    }
    /**
     * Get the broker algo from the given RPC order.
     *
     * @param inRpcOrder a <code>TradeTypesRpc.OrderBase</code> value
     * @return an <code>Optional&lt;BrokerAlgo&gt;</code> value
     */
    public static Optional<BrokerAlgo> getBrokerAlgo(TradeTypesRpc.OrderBase inRpcOrder)
    {
        if(!inRpcOrder.hasBrokerAlgo()) {
            return Optional.empty();
        }
        BrokerAlgo brokerAlgo = new BrokerAlgo();
        if(inRpcOrder.getBrokerAlgo().hasBrokerAlgoSpec()) {
            FixRpcUtil.getBrokerAlgoSpec(inRpcOrder.getBrokerAlgo().getBrokerAlgoSpec()).ifPresent(brokerAlgoSpec->brokerAlgo.setAlgoSpec(brokerAlgoSpec));
        }
        inRpcOrder.getBrokerAlgo().getBrokerAlgoTagsList().stream().forEach(rpcBrokerAlgoTag->FixRpcUtil.getBrokerAlgoTag(rpcBrokerAlgoTag).ifPresent(brokerAlgoTag->brokerAlgo.getAlgoTags().add(brokerAlgoTag)));
        return Optional.of(brokerAlgo);
    }
    /**
     * Get the broker algo tag spec from the given RPC broker algo tag spec.
     *
     * @param inRpcAlgoTagSpec a <code>FixAdminRpc.BrokerAlgoTagSpec</code> value
     * @return a <code>BrokerAlgoTagSpec</code> value
     */
    public static BrokerAlgoTagSpec getBrokerTagSpec(FixAdminRpc.BrokerAlgoTagSpec inRpcAlgoTagSpec)
    {
        BrokerAlgoTagSpec brokerAlgoTagSpec = new BrokerAlgoTagSpec();
        brokerAlgoTagSpec.setAdvice(inRpcAlgoTagSpec.getAdvice());
        brokerAlgoTagSpec.setDefaultValue(inRpcAlgoTagSpec.getDefaultValue());
        brokerAlgoTagSpec.setDescription(inRpcAlgoTagSpec.getDescription());
        brokerAlgoTagSpec.setIsMandatory(inRpcAlgoTagSpec.getMandatory());
        brokerAlgoTagSpec.setIsReadOnly(inRpcAlgoTagSpec.getIsReadOnly());
        brokerAlgoTagSpec.setLabel(inRpcAlgoTagSpec.getLabel());
        brokerAlgoTagSpec.setOptions(BaseRpcUtil.getMap(inRpcAlgoTagSpec.getOptions()));
        brokerAlgoTagSpec.setPattern(inRpcAlgoTagSpec.getPattern());
        brokerAlgoTagSpec.setTag(inRpcAlgoTagSpec.getTag());
        String validatorValue = inRpcAlgoTagSpec.getValidator();
        if(validatorValue != null) {
            try {
                @SuppressWarnings("unchecked")
                Validator<BrokerAlgoTag> validator = (Validator<BrokerAlgoTag>)Class.forName(validatorValue).newInstance();
                brokerAlgoTagSpec.setValidator(validator);
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | ClassCastException e) {
                PlatformServices.handleException(TradeRpcUtil.class,
                                                 "Cannot construct validator",
                                                 e);
            }
        }
        return brokerAlgoTagSpec;
    }
    /**
     * Get the FIX message from the given RPC FIX message.
     *
     * @param inRpcMessage a <code>TradeTypesRpc.FixMessage</code> value
     * @return a <code>quickfix.Message</code> value
     */
    public static quickfix.Message getFixMessage(TradeTypesRpc.FixMessage inRpcMessage)
    {
        quickfix.Message fixMessage = new quickfix.Message();
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
     * Get the RPC FIX message from the given FIX message.
     *
     * @param inMessage a <code>quickfix.Message</code> value
     * @return a <code>TradeTypesRpc.FixMessage</code> value
     */
    public static TradeTypesRpc.FixMessage getRpcFixMessage(quickfix.Message inMessage)
    {
        TradeTypesRpc.FixMessage.Builder fixMessageBuilder = TradeTypesRpc.FixMessage.newBuilder();
        Map<String,String> fields = Maps.newHashMap();
        setFieldMap(inMessage.getHeader(),
                    fields);
        fixMessageBuilder.setHeader(BaseRpcUtil.getRpcMap(fields));
        fields.clear();
        setFieldMap(inMessage,
                    fields);
        fixMessageBuilder.setBody(BaseRpcUtil.getRpcMap(fields));
        fields.clear();
        setFieldMap(inMessage.getTrailer(),
                    fields);
        fixMessageBuilder.setFooter(BaseRpcUtil.getRpcMap(fields));
        return fixMessageBuilder.build();
    }
    /**
     * Set the order ID from the given order on the given builder.
     *
     * @param inOrder an <code>Order</code> value
     * @param inBuilder a <code>TradeRpc.OrderResponse.Builder</code> value
     */
    public static void setOrderId(Order inOrder,
                                  TradeRpc.OrderResponse.Builder inBuilder)
    {
        if(inOrder instanceof OrderBase) {
            OrderBase order = (OrderBase)inOrder;
            if(order.getOrderID() != null) {
                inBuilder.setOrderid(order.getOrderID().getValue());
            }
        } else if(inOrder instanceof FIXOrder) {
            FIXOrder fixOrder = (FIXOrder)inOrder;
            quickfix.Message message = fixOrder.getMessage();
            try {
                if(message.isSetField(quickfix.field.ClOrdID.FIELD)) {
                    inBuilder.setOrderid(message.getString(quickfix.field.ClOrdID.FIELD));
                } else if(message.isSetField(quickfix.field.OrderID.FIELD)) {
                    inBuilder.setOrderid(message.getString(quickfix.field.OrderID.FIELD));
                }
            } catch (quickfix.FieldNotFound e) {
                PlatformServices.handleException(TradeRpcUtil.class,
                                                 "Unable to set order id",
                                                 e);
            }
        }
    }
    /**
     * Set the order id value.
     *
     * @param inRpcOrderSummary a <code>TradeTypesRpc.OrderSummary</code> value
     * @param inOrderSummary a <code>MutableOrderSummary</code> value
     */
    public static void setOrderId(TradeTypesRpc.OrderSummary inRpcOrderSummary,
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
     * @param inRpcOrderSummary a <code>TradeTypesRpc.OrderSummary</code> value
     * @param inOrderSummary a <code>MutableOrderSummary</code> value
     */
    public static void setRootOrderId(TradeTypesRpc.OrderSummary inRpcOrderSummary,
                                      MutableOrderSummary inOrderSummary)
    {
        String value = StringUtils.trimToNull(inRpcOrderSummary.getRootOrderId());
        if(value != null) {
            inOrderSummary.setRootOrderId(new OrderID(value));
        } else {
            inOrderSummary.setRootOrderId(null);
        }
    }
//    /**
//     * Get the broker status value from the given response.
//     *
//     * @param inResponse a <code>BrokerStatusListenerResponse</code> value
//     * @return an <code>Optional&lt;BrokerStatus&gt;</code> value
//     */
//    public static Optional<BrokerStatus> getBrokerStatus(FixAdminRpc.BrokerStatusListenerResponse inResponse)
//    {
//        BrokerStatus brokerStatus = null;
//        if(inResponse.hasBrokerStatus()) {
//            FixAdminRpc.BrokerStatus rpcBrokerStatus = inResponse.getBrokerStatus();
//            brokerStatus = getBrokerStatus(rpcBrokerStatus).orElse(null);
//        }
//        return(brokerStatus==null ? Optional.empty():Optional.of(brokerStatus));
//    }
//    /**
//     * Get the broker status for the given RPC value.
//     *
//     * @param inRpcBrokerStatus a <code>FixAdminRpc.BrokerStatus</code> value
//     * @return an <code>Optional&lt;BrokerStatus&gt;</code> value
//     */
//    public static Optional<BrokerStatus> getBrokerStatus(FixAdminRpc.BrokerStatus inRpcBrokerStatus)
//    {
//        Map<String,String> settings = Maps.newHashMap();
//        if(inRpcBrokerStatus.hasSettings()) {
//            settings = BaseRpcUtil.getMap(inRpcBrokerStatus.getSettings());
//        }
//        settings.put("id",
//                     inRpcBrokerStatus.getId());
//        settings.put("host",
//                     inRpcBrokerStatus.getHost());
//        settings.put("name",
//                     inRpcBrokerStatus.getName());
//        settings.put("port",
//                     String.valueOf(inRpcBrokerStatus.getPort()));
//        return Optional.of(new ClusteredBrokerStatus(fixSessionFactory.create(settings),
//                                                     inRpcBrokerStatus.hasClusterData() ? getClusterData(inRpcBrokerStatus.getClusterData()).orElse(null) : null,
//                                                     getFixSessionStatus(inRpcBrokerStatus.getFixSessionStatus()),
//                                                     inRpcBrokerStatus.getLoggedOn()));
//    }
//    /**
//     * Set the RPC brokers status on the given builder with the given value.
//     *
//     * @param inBrokersStatus a <code>BrokersStatus</code> value
//     * @param inBuilder a <code>FixAdminRpc.BrokersStatusResponse.Builder</code> value
//     */
//    public static void setBrokersStatus(BrokersStatus inBrokersStatus,
//                                        FixAdminRpc.BrokersStatusResponse.Builder inBuilder)
//    {
//        for(BrokerStatus brokerStatus : inBrokersStatus.getActiveFixSessions()) {
//            FixAdminRpc.BrokerStatus.Builder brokerStatusBuilder = FixAdminRpc.BrokerStatus.newBuilder();
//            setBrokerStatus(brokerStatus,
//                            brokerStatusBuilder);
//            inBuilder.addBrokerStatus(brokerStatusBuilder.build());
//            brokerStatusBuilder.clear();
//        }
//    }
//    /**
//     * Set the broker status on the given RPC broker status builder.
//     *
//     * @param inStatus a <code>BrokerStatus</code> value
//     * @param inBuilder a <code>FixAdminRpc.BrokerStatus.Builder</code> value
//     */
//    public static void setBrokerStatus(BrokerStatus inStatus,
//                                       FixAdminRpc.BrokerStatus.Builder inBuilder)
//    {
//        setBrokerAlgos(inStatus,
//                       inBuilder);
//        inBuilder.setHost(inStatus.getHost());
//        setBrokerId(inStatus,
//                    inBuilder);
//        inBuilder.setLoggedOn(inStatus.getLoggedOn());
//        inBuilder.setName(inStatus.getName());
//        inBuilder.setPort(inStatus.getPort());
//        inBuilder.setSettings(BaseRpcUtil.getRpcMap(inStatus.getSettings()));
//        inBuilder.setFixSessionStatus(getRpcFixSessionStatus(inStatus.getStatus()));
//        if(inStatus instanceof HasClusterData) {
//            HasClusterData hasClusterData = (HasClusterData)inStatus;
//            ClusterRpc.ClusterData.Builder clusterDataBuilder = ClusterRpc.ClusterData.newBuilder();
//            ClusterData clusterData = hasClusterData.getClusterData();
//            clusterDataBuilder.setHostId(clusterData.getHostId());
//            clusterDataBuilder.setHostNumber(clusterData.getHostNumber());
//            clusterDataBuilder.setInstanceNumber(clusterData.getInstanceNumber());
//            clusterDataBuilder.setTotalInstances(clusterData.getTotalInstances());
//            clusterDataBuilder.setUuid(clusterData.getUuid());
//            inBuilder.setClusterData(clusterDataBuilder.build());
//        }
//    }
//    /**
//     * Set the given broker status value on the given RPC builder.
//     *
//     * @param inStatus a <code>BrokerStatus</code> value
//     * @param inResponseBuilder a <code>FixAdminRpc.BrokerStatusListenerResponse.Builder</code> value
//     */
//    public static void setBrokerStatus(BrokerStatus inStatus,
//                                       FixAdminRpc.BrokerStatusListenerResponse.Builder inResponseBuilder)
//    {
//        if(inStatus == null) {
//            return;
//        }
//        FixAdminRpc.BrokerStatus.Builder brokerStatusBuilder = FixAdminRpc.BrokerStatus.newBuilder();
//        setBrokerStatus(inStatus,
//                        brokerStatusBuilder);
//        inResponseBuilder.setBrokerStatus(brokerStatusBuilder.build());
//    }
    /**
     * Get the FIX session status value from the given RPC value.
     *
     * @param inRpcFixSessionStatus a <code>FixAdminRpc.FixSessionStatus</code> value
     * @return a <code>FixSessionStatus</code> value
     */
    public static FixSessionStatus getFixSessionStatus(FixAdminRpc.FixSessionStatus inRpcFixSessionStatus)
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
     * @return a <code>FixAdminRpc.FixSessionStatus</code> value
     */
    public static FixAdminRpc.FixSessionStatus getRpcFixSessionStatus(FixSessionStatus inStatus)
    {
        switch(inStatus) {
            case AFFINITY_MISMATCH:
                return FixAdminRpc.FixSessionStatus.AffinityMismatchFixSessionStatus;
            case BACKUP:
                return FixAdminRpc.FixSessionStatus.BackupFixSessionStatus;
            case CONNECTED:
                return FixAdminRpc.FixSessionStatus.ConnectedFixSessionStatus;
            case DELETED:
                return FixAdminRpc.FixSessionStatus.DeletedFixSessionStatus;
            case DISABLED:
                return FixAdminRpc.FixSessionStatus.DisabledFixSessionStatus;
            case DISCONNECTED:
                return FixAdminRpc.FixSessionStatus.DisconnectedFixSessionStatus;
            case NOT_CONNECTED:
                return FixAdminRpc.FixSessionStatus.NotConnectedFixSessionStatus;
            case STOPPED:
                return FixAdminRpc.FixSessionStatus.StoppedFixSessionStatus;
            case UNKNOWN:
                return FixAdminRpc.FixSessionStatus.UnknownFixSessionStatus;
            default:
                throw new UnsupportedOperationException("Unsupported fix session status: " + inStatus);
        }
    }
    /**
     * Get an order summary value from the given RPC order summary.
     *
     * @param inRpcOrderSummary a <code>TradeTypesRpc.OrderSummary</code> value
     * @param inOrderSummaryFactory a <code>MutableOrderSummaryFactory</code> value
     * @param inUserFactory a <code>UserFactory</code> value
     * @param inReportFactory a <code>MutableReportFactory</code> value
     * @return an <code>Optional&lt;OrderSummary&gt;</code> value
     */
    public static Optional<OrderSummary> getOrderSummary(TradeTypesRpc.OrderSummary inRpcOrderSummary,
                                                         MutableOrderSummaryFactory inOrderSummaryFactory,
                                                         UserFactory inUserFactory,
                                                         MutableReportFactory inReportFactory)
    {
        MutableOrderSummary orderSummary = inOrderSummaryFactory.create();
        orderSummary.setAccount(inRpcOrderSummary.getAccount());;
        if(inRpcOrderSummary.hasUser()) {
            setUser(inRpcOrderSummary.getUser(),
                    orderSummary,
                    inUserFactory);
        }
        BaseRpcUtil.getScaledQuantity(inRpcOrderSummary.getAveragePrice()).ifPresent(value->orderSummary.setAveragePrice(value));
        orderSummary.setBrokerId(getBrokerId(inRpcOrderSummary).orElse(null));
        BaseRpcUtil.getScaledQuantity(inRpcOrderSummary.getCumulativeQuantity()).ifPresent(value->orderSummary.setCumulativeQuantity(value));
        if(inRpcOrderSummary.hasInstrument()) {
            orderSummary.setInstrument(getInstrument(inRpcOrderSummary).orElse(null));
        }
        BaseRpcUtil.getScaledQuantity(inRpcOrderSummary.getLastPrice()).ifPresent(value->orderSummary.setLastPrice(value));
        BaseRpcUtil.getScaledQuantity(inRpcOrderSummary.getLastQuantity()).ifPresent(value->orderSummary.setLastQuantity(value));
        BaseRpcUtil.getScaledQuantity(inRpcOrderSummary.getLeavesQuantity()).ifPresent(value->orderSummary.setLeavesQuantity(value));
        setOrderId(inRpcOrderSummary,
                   orderSummary);
        BaseRpcUtil.getScaledQuantity(inRpcOrderSummary.getOrderPrice()).ifPresent(value->orderSummary.setOrderPrice(value));
        BaseRpcUtil.getScaledQuantity(inRpcOrderSummary.getOrderQuantity()).ifPresent(value->orderSummary.setOrderQuantity(value));
        setOrderStatus(inRpcOrderSummary,
                       orderSummary);
        if(inRpcOrderSummary.hasReport()) {
            TradeMessage tradeMessage = getTradeMessage(inRpcOrderSummary.getReport());
            orderSummary.setReport(inReportFactory.create(tradeMessage,
                                                        orderSummary.getActor()));
        }
        setRootOrderId(inRpcOrderSummary,
                       orderSummary);
        if(inRpcOrderSummary.hasSendingTime()) {
            orderSummary.setSendingTime(Date.from(Instant.ofEpochSecond(inRpcOrderSummary.getSendingTime().getSeconds(),
                                                                        inRpcOrderSummary.getSendingTime().getNanos())));
        }
        orderSummary.setSide(getSide(inRpcOrderSummary.getSide()));
        if(inRpcOrderSummary.hasTransactTime()) {
            orderSummary.setTransactTime(Date.from(Instant.ofEpochSecond(inRpcOrderSummary.getTransactTime().getSeconds(),
                                                                         inRpcOrderSummary.getTransactTime().getNanos())));
        }
        orderSummary.setViewer(orderSummary.getActor());
        return Optional.of(orderSummary);
    }
    /**
     * Get the RPC order summary value from the given order summary.
     *
     * @param inOrderSummary an <code>OrderSummary</code> value
     * @return a <code>TradeTypesRpc.OrderSummary</code> value
     */
    public static TradeTypesRpc.OrderSummary getRpcOrderSummary(OrderSummary inOrderSummary)
    {
        TradeTypesRpc.OrderSummary.Builder orderSummaryBuilder = TradeTypesRpc.OrderSummary.newBuilder();
        String value = StringUtils.trimToNull(inOrderSummary.getAccount());
        if(value != null) {
            orderSummaryBuilder.setAccount(value);
        }
        BaseRpcUtil.getRpcQty(inOrderSummary.getAveragePrice()).ifPresent(qty->orderSummaryBuilder.setAveragePrice(qty));
        value = StringUtils.trimToNull(inOrderSummary.getBrokerId()==null?null:inOrderSummary.getBrokerId().getValue());
        if(value != null) {
            orderSummaryBuilder.setBrokerId(value);
        }
        BaseRpcUtil.getRpcQty(inOrderSummary.getCumulativeQuantity()).ifPresent(qty->orderSummaryBuilder.setCumulativeQuantity(qty));
        getRpcInstrument(inOrderSummary.getInstrument()).ifPresent(instrument->orderSummaryBuilder.setInstrument(instrument));
        BaseRpcUtil.getRpcQty(inOrderSummary.getLastPrice()).ifPresent(qty->orderSummaryBuilder.setLastPrice(qty));
        BaseRpcUtil.getRpcQty(inOrderSummary.getLastQuantity()).ifPresent(qty->orderSummaryBuilder.setLastQuantity(qty));
        BaseRpcUtil.getRpcQty(inOrderSummary.getLeavesQuantity()).ifPresent(qty->orderSummaryBuilder.setLeavesQuantity(qty));
        value = inOrderSummary.getOrderId()==null?null:inOrderSummary.getOrderId().getValue();
        if(value != null) {
            orderSummaryBuilder.setOrderId(value);
        }
        BaseRpcUtil.getRpcQty(inOrderSummary.getOrderPrice()).ifPresent(qty->orderSummaryBuilder.setOrderPrice(qty));
        BaseRpcUtil.getRpcQty(inOrderSummary.getOrderQuantity()).ifPresent(qty->orderSummaryBuilder.setOrderQuantity(qty));
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
            Instant time = inOrderSummary.getSendingTime().toInstant();
            Timestamp timestamp = Timestamp.newBuilder().setSeconds(time.getEpochSecond()).setNanos(time.getNano()).build();
            orderSummaryBuilder.setSendingTime(timestamp);
        }
        if(inOrderSummary.getSide() != null) {
            orderSummaryBuilder.setSide(getRpcSide(inOrderSummary.getSide()));
        }
        if(inOrderSummary.getTransactTime() != null) {
            Instant time = inOrderSummary.getTransactTime().toInstant();
            Timestamp timestamp = Timestamp.newBuilder().setSeconds(time.getEpochSecond()).setNanos(time.getNano()).build();
            orderSummaryBuilder.setTransactTime(timestamp);
        }
        if(inOrderSummary.getActor() != null) {
            orderSummaryBuilder.setUser(getRpcUser(inOrderSummary.getActor()));
        }
        return orderSummaryBuilder.build();
    }
    /**
     * Set the user from the given RPC user on the given order summary.
     *
     * @param inRpcUser a <code>AdminRpc.User</code> value
     * @param inOrderSummary a <code>MutableOrderSummary</code> value
     * @param inUserFactory a <code>UserFactory</code> value
     */
    public static void setUser(AdminRpc.User inRpcUser,
                               MutableOrderSummary inOrderSummary,
                               UserFactory inUserFactory)
    {
        User user = inUserFactory.create(inRpcUser.getName(),
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
     * @return a <code>AdminRpc.User</code> value
     */
    public static AdminRpc.User getRpcUser(User inUser)
    {
        AdminRpc.User.Builder userBuilder = AdminRpc.User.newBuilder();
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
     * Get the position key value from the given RPC position key.
     *
     * @param inRpcPositionKey a <code>TradeTypesRpc.PositionKey</code> value
     * @return a <code>PositionKey&lt;? extends Instrument&gt;</code> value
     */
    public static PositionKey<? extends Instrument> getPositionKey(TradeTypesRpc.PositionKey inRpcPositionKey)
    {
        return PositionKeyFactory.createKey(getInstrument(inRpcPositionKey.getInstrument()).orElse(null),
                                            StringUtils.trimToNull(inRpcPositionKey.getAccount()),
                                            StringUtils.trimToNull(inRpcPositionKey.getTraderId()));
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
                PlatformServices.handleException(TradeRpcUtil.class,
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
     * Set the values on the given field map and put them in the given map.
     *
     * @param inFieldMap a <code>quickfix.FieldMap</code>value
     * @param inFields a <code>Map&lt;String,String&gt;</code> value
     */
    private static void setFieldMap(quickfix.FieldMap inFieldMap,
                                    Map<String,String> inFields)
    {
        Iterator<quickfix.Field<?>> fieldIterator = inFieldMap.iterator();
        while(fieldIterator.hasNext()) {
            quickfix.Field<?> field = fieldIterator.next();
            inFields.put(String.valueOf(field.getTag()),
                         String.valueOf(field.getObject()));
        }
    }
}
