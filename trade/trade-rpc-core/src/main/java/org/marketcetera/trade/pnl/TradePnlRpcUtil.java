//
// this file is automatically generated
//
package org.marketcetera.trade.pnl;

import java.util.Optional;

import org.marketcetera.admin.UserFactory;
import org.marketcetera.admin.rpc.AdminRpcUtil;
import org.marketcetera.core.Preserve;
import org.marketcetera.rpc.base.BaseRpcUtil;
import org.marketcetera.trading.rpc.TradeRpcUtil;

/* $License$ */

/**
 * Provides common behavior for TradePnl services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Preserve
public abstract class TradePnlRpcUtil
{
    /**
     * Get the RPC object from the given value.
     *
     * @param inPosition a <code>Position</code> value
     * @return a <code>Optional&lt;TradePnlTypesRpc.PnlPosition&gt;</code> value
     */
    public static Optional<TradePnlTypesRpc.PnlPosition> getRpcPnlPosition(Position inPosition)
    {
        if(inPosition == null) {
            return Optional.empty();
        }
        TradePnlTypesRpc.PnlPosition.Builder builder = TradePnlTypesRpc.PnlPosition.newBuilder();
        TradeRpcUtil.getRpcInstrument(inPosition.getInstrument()).ifPresent(value->builder.setInstrument(value));
        AdminRpcUtil.getRpcUser(inPosition.getUser()).ifPresent(value->builder.setUser(value));
        BaseRpcUtil.getRpcQty(inPosition.getPosition()).ifPresent(value->builder.setPosition(value));
        BaseRpcUtil.getTimestampValue(inPosition.getEffectiveDate()).ifPresent(value->builder.setEffectiveDate(value));
        BaseRpcUtil.getRpcQty(inPosition.getWeightedAverageCost()).ifPresent(value->builder.setWeightedAverageCost(value));
        BaseRpcUtil.getRpcQty(inPosition.getRealizedGain()).ifPresent(value->builder.setRealizedGain(value));
        BaseRpcUtil.getRpcQty(inPosition.getUnrealizedGain()).ifPresent(value->builder.setUnrealizedGain(value));
        return Optional.of(builder.build());
    }
    /**
     * Get the RPC object from the given value.
     *
     * @param inCurrentPosition a <code>CurrentPosition</code> value
     * @return an <code>Optional&lt;TradePnlTypesRpc.CurrentPnlPosition&lt;</code> value
     */
    public static Optional<TradePnlTypesRpc.CurrentPnlPosition> getRpcCurrentPnlPosition(CurrentPosition inCurrentPosition)
    {
        if(inCurrentPosition == null) {
            return Optional.empty();
        }
        TradePnlTypesRpc.CurrentPnlPosition.Builder builder = TradePnlTypesRpc.CurrentPnlPosition.newBuilder();
        TradeRpcUtil.getRpcInstrument(inCurrentPosition.getInstrument()).ifPresent(value->builder.setInstrument(value));
        AdminRpcUtil.getRpcUser(inCurrentPosition.getUser()).ifPresent(value->builder.setUser(value));
        BaseRpcUtil.getRpcQty(inCurrentPosition.getPosition()).ifPresent(value->builder.setPosition(value));
        BaseRpcUtil.getRpcQty(inCurrentPosition.getWeightedAverageCost()).ifPresent(value->builder.setWeightedAverageCost(value));
        BaseRpcUtil.getRpcQty(inCurrentPosition.getRealizedGain()).ifPresent(value->builder.setRealizedGain(value));
        BaseRpcUtil.getRpcQty(inCurrentPosition.getUnrealizedGain()).ifPresent(value->builder.setUnrealizedGain(value));
        return Optional.of(builder.build());
    }
    /**
     * Get the RPC object from the given value.
     *
     * @param inProfitAndLoss a <code>ProfitAndLoss</code> value
     * @return an <code>Optional&lt;TradePnlTypesRpc.ProfitAndLoss&gt;</code> value
     */
    public static Optional<TradePnlTypesRpc.ProfitAndLoss> getRpcProfitAndLoss(ProfitAndLoss inProfitAndLoss)
    {
        if(inProfitAndLoss == null) {
            return Optional.empty();
        }
        TradePnlTypesRpc.ProfitAndLoss.Builder builder = TradePnlTypesRpc.ProfitAndLoss.newBuilder();
        TradeRpcUtil.getRpcInstrument(inProfitAndLoss.getInstrument()).ifPresent(value->builder.setInstrument(value));
        AdminRpcUtil.getRpcUser(inProfitAndLoss.getUser()).ifPresent(value->builder.setUser(value));
        BaseRpcUtil.getRpcQty(inProfitAndLoss.getRealizedGain()).ifPresent(value->builder.setRealizedGain(value));
        BaseRpcUtil.getRpcQty(inProfitAndLoss.getUnrealizedGain()).ifPresent(value->builder.setUnrealizedGain(value));
        BaseRpcUtil.getRpcQty(inProfitAndLoss.getBasisPrice()).ifPresent(value->builder.setBasisPrice(value));
        BaseRpcUtil.getRpcQty(inProfitAndLoss.getPosition()).ifPresent(value->builder.setPosition(value));
        return Optional.of(builder.build());
    }
    /**
     * Get the RPC object from the given value.
     *
     * @param inLot a <code>Lot</code> value
     * @return an <code>Optional&lt;TradePnlTypesRpc.Lot&gt;</code> value
     */
    public static Optional<TradePnlTypesRpc.Lot> getRpcLot(Lot inLot)
    {
        if(inLot == null) {
            return Optional.empty();
        }
        TradePnlTypesRpc.Lot.Builder builder = TradePnlTypesRpc.Lot.newBuilder();
        AdminRpcUtil.getRpcUser(inLot.getUser()).ifPresent(value->builder.setUser(value));
        TradePnlRpcUtil.getRpcTrade(inLot.getTrade()).ifPresent(value->builder.setTrade(value));
        TradePnlRpcUtil.getRpcPnlPosition(inLot.getPosition()).ifPresent(value->builder.setPosition(value));
        BaseRpcUtil.getRpcQty(inLot.getQuantity()).ifPresent(value->builder.setQuantity(value));
        BaseRpcUtil.getRpcQty(inLot.getAllocatedQuantity()).ifPresent(value->builder.setAllocatedQuantity(value));
        BaseRpcUtil.getTimestampValue(inLot.getEffectiveDate()).ifPresent(value->builder.setEffectiveDate(value));
        BaseRpcUtil.getRpcQty(inLot.getBasisPrice()).ifPresent(value->builder.setBasisPrice(value));
        BaseRpcUtil.getRpcQty(inLot.getGain()).ifPresent(value->builder.setGain(value));
        BaseRpcUtil.getRpcQty(inLot.getTradePrice()).ifPresent(value->builder.setTradePrice(value));
        return Optional.of(builder.build());
    }
    /**
     * Get the RPC object from the given value.
     *
     * @param inTrade a <code>Trade</code> value
     * @return an <code>Optional&lt;TradePnlTypesRpc.Trade&gt;</code> value
     */
    public static Optional<TradePnlTypesRpc.Trade> getRpcTrade(Trade inTrade)
    {
        if(inTrade == null) {
            return Optional.empty();
        }
        TradePnlTypesRpc.Trade.Builder builder = TradePnlTypesRpc.Trade.newBuilder();
        TradeRpcUtil.getRpcInstrument(inTrade.getInstrument()).ifPresent(value->builder.setInstrument(value));
        TradeRpcUtil.getRpcOrderId(inTrade.getExecutionId()).ifPresent(value->builder.setExecutionId(value));
        BaseRpcUtil.getRpcQty(inTrade.getPrice()).ifPresent(value->builder.setPrice(value));
        BaseRpcUtil.getRpcQty(inTrade.getQuantity()).ifPresent(value->builder.setQuantity(value));
        BaseRpcUtil.getTimestampValue(inTrade.getTransactionTime()).ifPresent(value->builder.setTransactionTime(value));
        return Optional.of(builder.build());
    }
    /**
     * Get the RPC object from the given value.
     *
     * @param inUserTrade a <code>UserTrade</code> value
     * @return an <code>Optional&lt;TradePnlTypesRpc.UserTrade&gt;</code> value
     */
    public static Optional<TradePnlTypesRpc.UserTrade> getRpcUserTrade(UserTrade inUserTrade)
    {
        if(inUserTrade == null) {
            return Optional.empty();
        }
        TradePnlTypesRpc.UserTrade.Builder builder = TradePnlTypesRpc.UserTrade.newBuilder();
        TradePnlRpcUtil.getRpcTrade(inUserTrade.getTrade()).ifPresent(value->builder.setTrade(value));
        AdminRpcUtil.getRpcUser(inUserTrade.getUser()).ifPresent(value->builder.setUser(value));
        builder.setSide(TradeRpcUtil.getRpcSide(inUserTrade.getSide()));
        TradePnlRpcUtil.getRpcProfitAndLoss(inUserTrade.getProfitAndLoss()).ifPresent(value->builder.setProfitAndLoss(value));
        TradeRpcUtil.getRpcOrderId(inUserTrade.getOrderId()).ifPresent(value->builder.setOrderId(value));
        return Optional.of(builder.build());
    }
    /**
     * Get the object from the given RPC value.
     *
     * @param inPosition a <code>TradePnlTypesRpc.PnlPosition</code> value
     * @param inPositionFactory a <code>PositionFactory</code> value
     * @param inUserFactory a <code>UserFactory</code> value
     * @return an <code>Optional&lt;Position&lt;</code> value
     */
    public static Optional<Position> getPnlPosition(TradePnlTypesRpc.PnlPosition inPosition,
                                                    PositionFactory inPositionFactory,
                                                    UserFactory inUserFactory)
    {
        if(inPosition == null) {
            return Optional.empty();
        }
        Position position = inPositionFactory.create();
        TradeRpcUtil.getInstrument(inPosition.getInstrument()).ifPresent(value->position.setInstrument(value));
        AdminRpcUtil.getUser(inPosition.getUser(),inUserFactory).ifPresent(value->position.setUser(value));
        BaseRpcUtil.getScaledQuantity(inPosition.getPosition()).ifPresent(value->position.setPosition(value));
        BaseRpcUtil.getDateValue(inPosition.getEffectiveDate()).ifPresent(value->position.setEffectiveDate(value));
        BaseRpcUtil.getScaledQuantity(inPosition.getWeightedAverageCost()).ifPresent(value->position.setWeightedAverageCost(value));
        BaseRpcUtil.getScaledQuantity(inPosition.getRealizedGain()).ifPresent(value->position.setRealizedGain(value));
        BaseRpcUtil.getScaledQuantity(inPosition.getUnrealizedGain()).ifPresent(value->position.setUnrealizedGain(value));
        return Optional.of(position);
    }
    /**
     * Get the object from the given RPC value.
     *
     * @param inCurrentPosition a <code>TradePnlTypesRpc.CurrentPnlPosition</code> value
     * @param inCurrentPositionFactory a <code>CurrentPositionFactory</code> value
     * @param inUserFactory a <code>UserFactory</code> value
     * @return an <code>Optional&lt;CurrentPosition&gt;</code> value
     */
    public static Optional<CurrentPosition> getCurrentPnlPosition(TradePnlTypesRpc.CurrentPnlPosition inCurrentPosition,
                                                                  CurrentPositionFactory inCurrentPositionFactory,
                                                                  UserFactory inUserFactory)
    {
        if(inCurrentPosition == null) {
            return Optional.empty();
        }
        CurrentPosition currentPosition = inCurrentPositionFactory.create();
        TradeRpcUtil.getInstrument(inCurrentPosition.getInstrument()).ifPresent(value->currentPosition.setInstrument(value));
        AdminRpcUtil.getUser(inCurrentPosition.getUser(),inUserFactory).ifPresent(value->currentPosition.setUser(value));
        BaseRpcUtil.getScaledQuantity(inCurrentPosition.getPosition()).ifPresent(value->currentPosition.setPosition(value));
        BaseRpcUtil.getScaledQuantity(inCurrentPosition.getWeightedAverageCost()).ifPresent(value->currentPosition.setWeightedAverageCost(value));
        BaseRpcUtil.getScaledQuantity(inCurrentPosition.getRealizedGain()).ifPresent(value->currentPosition.setRealizedGain(value));
        BaseRpcUtil.getScaledQuantity(inCurrentPosition.getUnrealizedGain()).ifPresent(value->currentPosition.setUnrealizedGain(value));
        return Optional.of(currentPosition);
    }
    /**
     * Get the object from the given RPC value.
     *
     * @param inProfitAndLoss a <code>TradePnlTypesRpc.ProfitAndLoss</code> value
     * @param inProfitAndLossFactory a <code>ProfitAndLossFactory</code> value
     * @param inUserFactory a <code>UserFactory</code> value
     * @return an <code>Optional&lt;ProfitAndLoss&gt;</code> value
     */
    public static Optional<ProfitAndLoss> getProfitAndLoss(TradePnlTypesRpc.ProfitAndLoss inProfitAndLoss,
                                                           ProfitAndLossFactory inProfitAndLossFactory,
                                                           UserFactory inUserFactory)
    {
        if(inProfitAndLoss == null) {
            return Optional.empty();
        }
        ProfitAndLoss profitAndLoss = inProfitAndLossFactory.create();
        TradeRpcUtil.getInstrument(inProfitAndLoss.getInstrument()).ifPresent(value->profitAndLoss.setInstrument(value));
        AdminRpcUtil.getUser(inProfitAndLoss.getUser(),inUserFactory).ifPresent(value->profitAndLoss.setUser(value));
        BaseRpcUtil.getScaledQuantity(inProfitAndLoss.getRealizedGain()).ifPresent(value->profitAndLoss.setRealizedGain(value));
        BaseRpcUtil.getScaledQuantity(inProfitAndLoss.getUnrealizedGain()).ifPresent(value->profitAndLoss.setUnrealizedGain(value));
        BaseRpcUtil.getScaledQuantity(inProfitAndLoss.getBasisPrice()).ifPresent(value->profitAndLoss.setBasisPrice(value));
        BaseRpcUtil.getScaledQuantity(inProfitAndLoss.getPosition()).ifPresent(value->profitAndLoss.setPosition(value));
        return Optional.of(profitAndLoss);
    }
    /**
     * Get the object from the given RPC value.
     *
     * @param inLot a <code>TradePnlTypesRpc.Lot</code> value
     * @param inLotFactory a <code>LotFactory</code> value
     * @param inProfitAndLossFactory a <code>ProfitAndLossFactory</code> value
     * @param inTradeFactory a <code>TradeFactory</code> value
     * @param inUserFactory a <code>UserFactory</code> value
     * @param inPositionFactory a <code>PositionFactory</code> value
     * @return an <code>Optional&lt;Lot&gt;</code> value
     */
    public static Optional<Lot> getLot(TradePnlTypesRpc.Lot inLot,
                                       LotFactory inLotFactory,
                                       ProfitAndLossFactory inProfitAndLossFactory,
                                       TradeFactory inTradeFactory,
                                       UserFactory inUserFactory,
                                       PositionFactory inPositionFactory)
    {
        if(inLot == null) {
            return Optional.empty();
        }
        Lot lot = inLotFactory.create();
        AdminRpcUtil.getUser(inLot.getUser(),inUserFactory).ifPresent(value->lot.setUser(value));
        TradePnlRpcUtil.getTrade(inLot.getTrade(),inTradeFactory).ifPresent(value->lot.setTrade(value));
        TradePnlRpcUtil.getPnlPosition(inLot.getPosition(),inPositionFactory,inUserFactory).ifPresent(value->lot.setPosition(value));
        BaseRpcUtil.getScaledQuantity(inLot.getQuantity()).ifPresent(value->lot.setQuantity(value));
        BaseRpcUtil.getScaledQuantity(inLot.getAllocatedQuantity()).ifPresent(value->lot.setAllocatedQuantity(value));
        BaseRpcUtil.getDateValue(inLot.getEffectiveDate()).ifPresent(value->lot.setEffectiveDate(value));
        BaseRpcUtil.getScaledQuantity(inLot.getBasisPrice()).ifPresent(value->lot.setBasisPrice(value));
        BaseRpcUtil.getScaledQuantity(inLot.getGain()).ifPresent(value->lot.setGain(value));
        BaseRpcUtil.getScaledQuantity(inLot.getTradePrice()).ifPresent(value->lot.setTradePrice(value));
        return Optional.of(lot);
    }
    /**
     * Get the object from the given RPC value.
     *
     * @param inTrade a <code>TradePnlTypesRpc.Trade</code> value
     * @param inTradeFactory a <code>TradeFactory</code> value
     * @return an <code>Optional&lt;Trade&gt;</code> value
     */
    public static Optional<Trade> getTrade(TradePnlTypesRpc.Trade inTrade,
                                           TradeFactory inTradeFactory)
    {
        if(inTrade == null) {
            return Optional.empty();
        }
        Trade trade = inTradeFactory.create();
        TradeRpcUtil.getInstrument(inTrade.getInstrument()).ifPresent(value->trade.setInstrument(value));
        TradeRpcUtil.getOrderId(inTrade.getExecutionId()).ifPresent(value->trade.setExecutionId(value));
        BaseRpcUtil.getScaledQuantity(inTrade.getPrice()).ifPresent(value->trade.setPrice(value));
        BaseRpcUtil.getScaledQuantity(inTrade.getQuantity()).ifPresent(value->trade.setQuantity(value));
        BaseRpcUtil.getDateValue(inTrade.getTransactionTime()).ifPresent(value->trade.setTransactionTime(value));
        return Optional.of(trade);
    }
    /**
     * Get the object from the given RPC value.
     *
     * @param inUserTrade a <code>TradePnlTypesRpc.UserTrade</code> value
     * @param inUserTradeFactory a <code>UserTradeFactory</code> value
     * @param inTradeFactory a <code>TradeFactory</code> value
     * @param inProfitAndLossFactory a <code>ProfitAndLossFactory</code> value
     * @param inUserFactory a <code>UserFactory</code> value
     * @return an <code>Optional&lt;UserTrade&gt;</code> value
     */
    public static Optional<UserTrade> getUserTrade(TradePnlTypesRpc.UserTrade inUserTrade,
                                                   UserTradeFactory inUserTradeFactory,
                                                   TradeFactory inTradeFactory,
                                                   ProfitAndLossFactory inProfitAndLossFactory,
                                                   UserFactory inUserFactory)
    {
        if(inUserTrade == null) {
            return Optional.empty();
        }
        UserTrade userTrade = inUserTradeFactory.create();
        TradePnlRpcUtil.getTrade(inUserTrade.getTrade(),inTradeFactory).ifPresent(value->userTrade.setTrade(value));
        AdminRpcUtil.getUser(inUserTrade.getUser(),inUserFactory).ifPresent(value->userTrade.setUser(value));
        userTrade.setSide(TradeRpcUtil.getSide(inUserTrade.getSide()));
        TradePnlRpcUtil.getProfitAndLoss(inUserTrade.getProfitAndLoss(),inProfitAndLossFactory,inUserFactory).ifPresent(value->userTrade.setProfitAndLoss(value));
        TradeRpcUtil.getOrderId(inUserTrade.getOrderId()).ifPresent(value->userTrade.setOrderId(value));
        return Optional.of(userTrade);
    }
}
