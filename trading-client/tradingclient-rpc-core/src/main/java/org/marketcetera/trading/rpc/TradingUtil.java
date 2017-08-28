package org.marketcetera.trading.rpc;

import org.marketcetera.trade.Side;
import org.marketcetera.trading.rpc.TradingRpc.OrderType;
import org.marketcetera.trading.rpc.TradingRpc.SideType;

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
     * Get a MATP order type from an RPC order type.
     *
     * @param inOrderType an <code>OrderType</code> value
     * @return an <code>org.marketcetera.trade.OrderType</code> value
     */
    public static org.marketcetera.trade.OrderType getOrderTypeFromRpcOrderType(OrderType inOrderType)
    {
        switch(inOrderType) {
            case ForexLimit:
                return org.marketcetera.trade.OrderType.ForexLimit;
            case ForexMarket:
                return org.marketcetera.trade.OrderType.ForexMarket;
            case ForexPreviouslyQuoted:
                return org.marketcetera.trade.OrderType.ForexPreviouslyQuoted;
            case ForexSwap:
                return org.marketcetera.trade.OrderType.ForexSwap;
            case Funari:
                return org.marketcetera.trade.OrderType.Funari;
            case Limit:
                return org.marketcetera.trade.OrderType.Limit;
            case LimitOnClose:
                return org.marketcetera.trade.OrderType.LimitOnClose;
            case LimitOrBetter:
                return org.marketcetera.trade.OrderType.LimitOrBetter;
            case LimitWithOrWithout:
                return org.marketcetera.trade.OrderType.LimitWithOrWithout;
            case Market:
                return org.marketcetera.trade.OrderType.Market;
            case MarketOnClose:
                return org.marketcetera.trade.OrderType.MarketOnClose;
            case OnBasis:
                return org.marketcetera.trade.OrderType.OnBasis;
            case OnClose:
                return org.marketcetera.trade.OrderType.OnClose;
            case Pegged:
                return org.marketcetera.trade.OrderType.Pegged;
            case PreviouslyIndicated:
                return org.marketcetera.trade.OrderType.PreviouslyIndicated;
            case PreviouslyQuoted:
                return org.marketcetera.trade.OrderType.PreviouslyQuoted;
            case Stop:
                return org.marketcetera.trade.OrderType.Stop;
            case StopLimit:
                return org.marketcetera.trade.OrderType.StopLimit;
            case UnknownOrderType:
                return org.marketcetera.trade.OrderType.Unknown;
            case WithOrWithout:
                return org.marketcetera.trade.OrderType.WithOrWithout;
            default:
                throw new UnsupportedOperationException("Unsupported side: " + inOrderType);
        }
    }
    /**
     * Get an order type value from an RPC order type value.
     *
     * @param inOrderType an <code>org.marketcetera.trade.OrderType</code> value
     * @return an <code>OrderType</code> value
     */
    public static OrderType getOrderTypeFromOrderType(org.marketcetera.trade.OrderType inOrderType)
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
            case Unknown:
                return OrderType.UnknownOrderType;
            case WithOrWithout:
                return OrderType.WithOrWithout;
            default:
                throw new UnsupportedOperationException("Unsupported order type: " + inOrderType);
        }
    }
    /**
     * Get a side value from an RPC side type.
     *
     * @param inSideType a <code>ParamountRpc.SideType</code> value
     * @return a <code>Side</code> value
     */
    public static Side getSideFromSideType(SideType inSideType)
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
     * @return a <code>SideType</code> value
     */
    public static SideType getSideTypeFromSide(Side inSide)
    {
        switch(inSide) {
            case Buy:
                return SideType.Buy;
            case BuyMinus:
                return SideType.BuyMinus;
            case Cross:
                return SideType.Cross;
            case CrossShort:
                return SideType.CrossShort;
            case Sell:
                return SideType.Sell;
            case SellPlus:
                return SideType.SellPlus;
            case SellShort:
                return SideType.SellShort;
            case SellShortExempt:
                return SideType.SellShortExempt;
            case Undisclosed:
                return SideType.Undisclosed;
            case Unknown:
                return SideType.UnknownSide;
            default:
                throw new UnsupportedOperationException("Unsupported side: " + inSide);
        }
    }
}
