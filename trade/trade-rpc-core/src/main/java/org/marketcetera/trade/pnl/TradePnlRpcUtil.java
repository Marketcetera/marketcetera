//
// this file is automatically generated
//
package org.marketcetera.trade.pnl;

/* $License$ */

/**
 * Provides common behavior for TradePnl services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class TradePnlRpcUtil
{
    /**
     * Get the RPC object from the given value.
     *
     * @param inPosition a <code>org.marketcetera.trade.pnl.Position</code> value
     * @return a java.util.Optional<TradePnlTypesRpc.PnlPosition> value
     */
    public static java.util.Optional<TradePnlTypesRpc.PnlPosition> getRpcPnlPosition(org.marketcetera.trade.pnl.Position inPosition)
    {
        if(inPosition == null) {
            return java.util.Optional.empty();
        }
        TradePnlTypesRpc.PnlPosition.Builder builder = TradePnlTypesRpc.PnlPosition.newBuilder();
        org.marketcetera.trading.rpc.TradeRpcUtil.getRpcInstrument(inPosition.getInstrument()).ifPresent(value->builder.setInstrument(value));
        org.marketcetera.admin.rpc.AdminRpcUtil.getRpcUser(inPosition.getUser()).ifPresent(value->builder.setUser(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getRpcQty(inPosition.getPosition()).ifPresent(value->builder.setPosition(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getTimestampValue(inPosition.getEffectiveDate()).ifPresent(value->builder.setEffectiveDate(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getRpcQty(inPosition.getWeightedAverageCost()).ifPresent(value->builder.setWeightedAverageCost(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getRpcQty(inPosition.getRealizedGain()).ifPresent(value->builder.setRealizedGain(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getRpcQty(inPosition.getUnrealizedGain()).ifPresent(value->builder.setUnrealizedGain(value));
        return java.util.Optional.of(builder.build());
    }
    /**
     * Get the RPC object from the given value.
     *
     * @param inCurrentPosition a <code>org.marketcetera.trade.pnl.CurrentPosition</code> value
     * @return a java.util.Optional<TradePnlTypesRpc.CurrentPnlPosition> value
     */
    public static java.util.Optional<TradePnlTypesRpc.CurrentPnlPosition> getRpcCurrentPnlPosition(org.marketcetera.trade.pnl.CurrentPosition inCurrentPosition)
    {
        if(inCurrentPosition == null) {
            return java.util.Optional.empty();
        }
        TradePnlTypesRpc.CurrentPnlPosition.Builder builder = TradePnlTypesRpc.CurrentPnlPosition.newBuilder();
        org.marketcetera.trading.rpc.TradeRpcUtil.getRpcInstrument(inCurrentPosition.getInstrument()).ifPresent(value->builder.setInstrument(value));
        org.marketcetera.admin.rpc.AdminRpcUtil.getRpcUser(inCurrentPosition.getUser()).ifPresent(value->builder.setUser(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getRpcQty(inCurrentPosition.getPosition()).ifPresent(value->builder.setPosition(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getRpcQty(inCurrentPosition.getWeightedAverageCost()).ifPresent(value->builder.setWeightedAverageCost(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getRpcQty(inCurrentPosition.getRealizedGain()).ifPresent(value->builder.setRealizedGain(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getRpcQty(inCurrentPosition.getUnrealizedGain()).ifPresent(value->builder.setUnrealizedGain(value));
        return java.util.Optional.of(builder.build());
    }
    /**
     * Get the RPC object from the given value.
     *
     * @param inProfitAndLoss a <code>org.marketcetera.trade.pnl.ProfitAndLoss</code> value
     * @return a java.util.Optional<TradePnlTypesRpc.ProfitAndLoss> value
     */
    public static java.util.Optional<TradePnlTypesRpc.ProfitAndLoss> getRpcProfitAndLoss(org.marketcetera.trade.pnl.ProfitAndLoss inProfitAndLoss)
    {
        if(inProfitAndLoss == null) {
            return java.util.Optional.empty();
        }
        TradePnlTypesRpc.ProfitAndLoss.Builder builder = TradePnlTypesRpc.ProfitAndLoss.newBuilder();
        org.marketcetera.trading.rpc.TradeRpcUtil.getRpcInstrument(inProfitAndLoss.getInstrument()).ifPresent(value->builder.setInstrument(value));
        org.marketcetera.admin.rpc.AdminRpcUtil.getRpcUser(inProfitAndLoss.getUser()).ifPresent(value->builder.setUser(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getRpcQty(inProfitAndLoss.getRealizedGain()).ifPresent(value->builder.setRealizedGain(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getRpcQty(inProfitAndLoss.getUnrealizedGain()).ifPresent(value->builder.setUnrealizedGain(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getRpcQty(inProfitAndLoss.getBasisPrice()).ifPresent(value->builder.setBasisPrice(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getRpcQty(inProfitAndLoss.getPosition()).ifPresent(value->builder.setPosition(value));
        return java.util.Optional.of(builder.build());
    }
    /**
     * Get the RPC object from the given value.
     *
     * @param inLot a <code>org.marketcetera.trade.pnl.Lot</code> value
     * @return a java.util.Optional<TradePnlTypesRpc.Lot> value
     */
    public static java.util.Optional<TradePnlTypesRpc.Lot> getRpcLot(org.marketcetera.trade.pnl.Lot inLot)
    {
        if(inLot == null) {
            return java.util.Optional.empty();
        }
        TradePnlTypesRpc.Lot.Builder builder = TradePnlTypesRpc.Lot.newBuilder();
        org.marketcetera.admin.rpc.AdminRpcUtil.getRpcUser(inLot.getUser()).ifPresent(value->builder.setUser(value));
        org.marketcetera.trade.pnl.TradePnlRpcUtil.getRpcTrade(inLot.getTrade()).ifPresent(value->builder.setTrade(value));
        org.marketcetera.trade.pnl.TradePnlRpcUtil.getRpcPnlPosition(inLot.getPosition()).ifPresent(value->builder.setPosition(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getRpcQty(inLot.getQuantity()).ifPresent(value->builder.setQuantity(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getRpcQty(inLot.getAllocatedQuantity()).ifPresent(value->builder.setAllocatedQuantity(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getTimestampValue(inLot.getEffectiveDate()).ifPresent(value->builder.setEffectiveDate(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getRpcQty(inLot.getBasisPrice()).ifPresent(value->builder.setBasisPrice(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getRpcQty(inLot.getGain()).ifPresent(value->builder.setGain(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getRpcQty(inLot.getTradePrice()).ifPresent(value->builder.setTradePrice(value));
        return java.util.Optional.of(builder.build());
    }
    /**
     * Get the RPC object from the given value.
     *
     * @param inTrade a <code>org.marketcetera.trade.pnl.Trade</code> value
     * @return a java.util.Optional<TradePnlTypesRpc.Trade> value
     */
    public static java.util.Optional<TradePnlTypesRpc.Trade> getRpcTrade(org.marketcetera.trade.pnl.Trade inTrade)
    {
        if(inTrade == null) {
            return java.util.Optional.empty();
        }
        TradePnlTypesRpc.Trade.Builder builder = TradePnlTypesRpc.Trade.newBuilder();
        org.marketcetera.trading.rpc.TradeRpcUtil.getRpcInstrument(inTrade.getInstrument()).ifPresent(value->builder.setInstrument(value));
        org.marketcetera.trading.rpc.TradeRpcUtil.getRpcOrderId(inTrade.getExecutionId()).ifPresent(value->builder.setExecutionId(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getRpcQty(inTrade.getPrice()).ifPresent(value->builder.setPrice(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getRpcQty(inTrade.getQuantity()).ifPresent(value->builder.setQuantity(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getTimestampValue(inTrade.getTransactionTime()).ifPresent(value->builder.setTransactionTime(value));
        return java.util.Optional.of(builder.build());
    }
    /**
     * Get the RPC object from the given value.
     *
     * @param inUserTrade a <code>org.marketcetera.trade.pnl.UserTrade</code> value
     * @return a java.util.Optional<TradePnlTypesRpc.UserTrade> value
     */
    public static java.util.Optional<TradePnlTypesRpc.UserTrade> getRpcUserTrade(org.marketcetera.trade.pnl.UserTrade inUserTrade)
    {
        if(inUserTrade == null) {
            return java.util.Optional.empty();
        }
        TradePnlTypesRpc.UserTrade.Builder builder = TradePnlTypesRpc.UserTrade.newBuilder();
        org.marketcetera.trade.pnl.TradePnlRpcUtil.getRpcTrade(inUserTrade.getTrade()).ifPresent(value->builder.setTrade(value));
        org.marketcetera.admin.rpc.AdminRpcUtil.getRpcUser(inUserTrade.getUser()).ifPresent(value->builder.setUser(value));
        builder.setSide(org.marketcetera.trading.rpc.TradeRpcUtil.getRpcSide(inUserTrade.getSide()));
        org.marketcetera.trade.pnl.TradePnlRpcUtil.getRpcProfitAndLoss(inUserTrade.getProfitAndLoss()).ifPresent(value->builder.setProfitAndLoss(value));
        org.marketcetera.trading.rpc.TradeRpcUtil.getRpcOrderId(inUserTrade.getOrderId()).ifPresent(value->builder.setOrderId(value));
        return java.util.Optional.of(builder.build());
    }
    /**
     * Get the object from the given RPC value.
     *
     * @param inPosition a <code>org.marketcetera.trade.pnl.TradePnlTypesRpc.PnlPosition</code> value
     * @param inPositionFactory a <code>org.marketcetera.trade.pnl.PositionFactory</code> value
     * @param inUserFactory a <code>org.marketcetera.admin.UserFactory</code> value
     * @return a org.marketcetera.trade.pnl.Position value
     */
    public static java.util.Optional<org.marketcetera.trade.pnl.Position> getPnlPosition(org.marketcetera.trade.pnl.TradePnlTypesRpc.PnlPosition inPosition,org.marketcetera.trade.pnl.PositionFactory inPositionFactory,org.marketcetera.admin.UserFactory inUserFactory)
    {
        if(inPosition == null) {
            return java.util.Optional.empty();
        }
        org.marketcetera.trade.pnl.Position position = inPositionFactory.create();
        org.marketcetera.trading.rpc.TradeRpcUtil.getInstrument(inPosition.getInstrument()).ifPresent(value->position.setInstrument(value));
        org.marketcetera.admin.rpc.AdminRpcUtil.getUser(inPosition.getUser(),inUserFactory).ifPresent(value->position.setUser(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getScaledQuantity(inPosition.getPosition()).ifPresent(value->position.setPosition(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getDateValue(inPosition.getEffectiveDate()).ifPresent(value->position.setEffectiveDate(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getScaledQuantity(inPosition.getWeightedAverageCost()).ifPresent(value->position.setWeightedAverageCost(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getScaledQuantity(inPosition.getRealizedGain()).ifPresent(value->position.setRealizedGain(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getScaledQuantity(inPosition.getUnrealizedGain()).ifPresent(value->position.setUnrealizedGain(value));
        return java.util.Optional.of(position);
    }
    /**
     * Get the object from the given RPC value.
     *
     * @param inCurrentPosition a <code>org.marketcetera.trade.pnl.TradePnlTypesRpc.CurrentPnlPosition</code> value
     * @param inCurrentPositionFactory a <code>org.marketcetera.trade.pnl.CurrentPositionFactory</code> value
     * @param inUserFactory a <code>org.marketcetera.admin.UserFactory</code> value
     * @return a org.marketcetera.trade.pnl.CurrentPosition value
     */
    public static java.util.Optional<org.marketcetera.trade.pnl.CurrentPosition> getCurrentPnlPosition(org.marketcetera.trade.pnl.TradePnlTypesRpc.CurrentPnlPosition inCurrentPosition,org.marketcetera.trade.pnl.CurrentPositionFactory inCurrentPositionFactory,org.marketcetera.admin.UserFactory inUserFactory)
    {
        if(inCurrentPosition == null) {
            return java.util.Optional.empty();
        }
        org.marketcetera.trade.pnl.CurrentPosition currentPosition = inCurrentPositionFactory.create();
        org.marketcetera.trading.rpc.TradeRpcUtil.getInstrument(inCurrentPosition.getInstrument()).ifPresent(value->currentPosition.setInstrument(value));
        org.marketcetera.admin.rpc.AdminRpcUtil.getUser(inCurrentPosition.getUser(),inUserFactory).ifPresent(value->currentPosition.setUser(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getScaledQuantity(inCurrentPosition.getPosition()).ifPresent(value->currentPosition.setPosition(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getScaledQuantity(inCurrentPosition.getWeightedAverageCost()).ifPresent(value->currentPosition.setWeightedAverageCost(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getScaledQuantity(inCurrentPosition.getRealizedGain()).ifPresent(value->currentPosition.setRealizedGain(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getScaledQuantity(inCurrentPosition.getUnrealizedGain()).ifPresent(value->currentPosition.setUnrealizedGain(value));
        return java.util.Optional.of(currentPosition);
    }
    /**
     * Get the object from the given RPC value.
     *
     * @param inProfitAndLoss a <code>org.marketcetera.trade.pnl.TradePnlTypesRpc.ProfitAndLoss</code> value
     * @param inProfitAndLossFactory a <code>org.marketcetera.trade.pnl.ProfitAndLossFactory</code> value
     * @param inUserFactory a <code>org.marketcetera.admin.UserFactory</code> value
     * @return a org.marketcetera.trade.pnl.ProfitAndLoss value
     */
    public static java.util.Optional<org.marketcetera.trade.pnl.ProfitAndLoss> getProfitAndLoss(org.marketcetera.trade.pnl.TradePnlTypesRpc.ProfitAndLoss inProfitAndLoss,org.marketcetera.trade.pnl.ProfitAndLossFactory inProfitAndLossFactory,org.marketcetera.admin.UserFactory inUserFactory)
    {
        if(inProfitAndLoss == null) {
            return java.util.Optional.empty();
        }
        org.marketcetera.trade.pnl.ProfitAndLoss profitAndLoss = inProfitAndLossFactory.create();
        org.marketcetera.trading.rpc.TradeRpcUtil.getInstrument(inProfitAndLoss.getInstrument()).ifPresent(value->profitAndLoss.setInstrument(value));
        org.marketcetera.admin.rpc.AdminRpcUtil.getUser(inProfitAndLoss.getUser(),inUserFactory).ifPresent(value->profitAndLoss.setUser(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getScaledQuantity(inProfitAndLoss.getRealizedGain()).ifPresent(value->profitAndLoss.setRealizedGain(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getScaledQuantity(inProfitAndLoss.getUnrealizedGain()).ifPresent(value->profitAndLoss.setUnrealizedGain(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getScaledQuantity(inProfitAndLoss.getBasisPrice()).ifPresent(value->profitAndLoss.setBasisPrice(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getScaledQuantity(inProfitAndLoss.getPosition()).ifPresent(value->profitAndLoss.setPosition(value));
        return java.util.Optional.of(profitAndLoss);
    }
    /**
     * Get the object from the given RPC value.
     *
     * @param inLot a <code>org.marketcetera.trade.pnl.TradePnlTypesRpc.Lot</code> value
     * @param inLotFactory a <code>org.marketcetera.trade.pnl.LotFactory</code> value
     * @param inProfitAndLossFactory a <code>org.marketcetera.trade.pnl.ProfitAndLossFactory</code> value
     * @param inTradeFactory a <code>org.marketcetera.trade.pnl.TradeFactory</code> value
     * @param inUserFactory a <code>org.marketcetera.admin.UserFactory</code> value
     * @param inPositionFactory a <code>org.marketcetera.trade.pnl.PositionFactory</code> value
     * @return a org.marketcetera.trade.pnl.Lot value
     */
    public static java.util.Optional<org.marketcetera.trade.pnl.Lot> getLot(org.marketcetera.trade.pnl.TradePnlTypesRpc.Lot inLot,org.marketcetera.trade.pnl.LotFactory inLotFactory,org.marketcetera.trade.pnl.ProfitAndLossFactory inProfitAndLossFactory,org.marketcetera.trade.pnl.TradeFactory inTradeFactory,org.marketcetera.admin.UserFactory inUserFactory,org.marketcetera.trade.pnl.PositionFactory inPositionFactory)
    {
        if(inLot == null) {
            return java.util.Optional.empty();
        }
        org.marketcetera.trade.pnl.Lot lot = inLotFactory.create();
        org.marketcetera.admin.rpc.AdminRpcUtil.getUser(inLot.getUser(),inUserFactory).ifPresent(value->lot.setUser(value));
        org.marketcetera.trade.pnl.TradePnlRpcUtil.getTrade(inLot.getTrade(),inTradeFactory).ifPresent(value->lot.setTrade(value));
        org.marketcetera.trade.pnl.TradePnlRpcUtil.getPnlPosition(inLot.getPosition(),inPositionFactory,inUserFactory).ifPresent(value->lot.setPosition(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getScaledQuantity(inLot.getQuantity()).ifPresent(value->lot.setQuantity(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getScaledQuantity(inLot.getAllocatedQuantity()).ifPresent(value->lot.setAllocatedQuantity(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getDateValue(inLot.getEffectiveDate()).ifPresent(value->lot.setEffectiveDate(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getScaledQuantity(inLot.getBasisPrice()).ifPresent(value->lot.setBasisPrice(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getScaledQuantity(inLot.getGain()).ifPresent(value->lot.setGain(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getScaledQuantity(inLot.getTradePrice()).ifPresent(value->lot.setTradePrice(value));
        return java.util.Optional.of(lot);
    }
    /**
     * Get the object from the given RPC value.
     *
     * @param inTrade a <code>org.marketcetera.trade.pnl.TradePnlTypesRpc.Trade</code> value
     * @param inTradeFactory a <code>org.marketcetera.trade.pnl.TradeFactory</code> value
     * @return a org.marketcetera.trade.pnl.Trade value
     */
    public static java.util.Optional<org.marketcetera.trade.pnl.Trade> getTrade(org.marketcetera.trade.pnl.TradePnlTypesRpc.Trade inTrade,org.marketcetera.trade.pnl.TradeFactory inTradeFactory)
    {
        if(inTrade == null) {
            return java.util.Optional.empty();
        }
        org.marketcetera.trade.pnl.Trade trade = inTradeFactory.create();
        org.marketcetera.trading.rpc.TradeRpcUtil.getInstrument(inTrade.getInstrument()).ifPresent(value->trade.setInstrument(value));
        org.marketcetera.trading.rpc.TradeRpcUtil.getOrderId(inTrade.getExecutionId()).ifPresent(value->trade.setExecutionId(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getScaledQuantity(inTrade.getPrice()).ifPresent(value->trade.setPrice(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getScaledQuantity(inTrade.getQuantity()).ifPresent(value->trade.setQuantity(value));
        org.marketcetera.rpc.base.BaseRpcUtil.getDateValue(inTrade.getTransactionTime()).ifPresent(value->trade.setTransactionTime(value));
        return java.util.Optional.of(trade);
    }
    /**
     * Get the object from the given RPC value.
     *
     * @param inUserTrade a <code>org.marketcetera.trade.pnl.TradePnlTypesRpc.UserTrade</code> value
     * @param inUserTradeFactory a <code>org.marketcetera.trade.pnl.UserTradeFactory</code> value
     * @param inTradeFactory a <code>org.marketcetera.trade.pnl.TradeFactory</code> value
     * @param inProfitAndLossFactory a <code>org.marketcetera.trade.pnl.ProfitAndLossFactory</code> value
     * @param inUserFactory a <code>org.marketcetera.admin.UserFactory</code> value
     * @return a org.marketcetera.trade.pnl.UserTrade value
     */
    public static java.util.Optional<org.marketcetera.trade.pnl.UserTrade> getUserTrade(org.marketcetera.trade.pnl.TradePnlTypesRpc.UserTrade inUserTrade,org.marketcetera.trade.pnl.UserTradeFactory inUserTradeFactory,org.marketcetera.trade.pnl.TradeFactory inTradeFactory,org.marketcetera.trade.pnl.ProfitAndLossFactory inProfitAndLossFactory,org.marketcetera.admin.UserFactory inUserFactory)
    {
        if(inUserTrade == null) {
            return java.util.Optional.empty();
        }
        org.marketcetera.trade.pnl.UserTrade userTrade = inUserTradeFactory.create();
        org.marketcetera.trade.pnl.TradePnlRpcUtil.getTrade(inUserTrade.getTrade(),inTradeFactory).ifPresent(value->userTrade.setTrade(value));
        org.marketcetera.admin.rpc.AdminRpcUtil.getUser(inUserTrade.getUser(),inUserFactory).ifPresent(value->userTrade.setUser(value));
        userTrade.setSide(org.marketcetera.trading.rpc.TradeRpcUtil.getSide(inUserTrade.getSide()));
        org.marketcetera.trade.pnl.TradePnlRpcUtil.getProfitAndLoss(inUserTrade.getProfitAndLoss(),inProfitAndLossFactory,inUserFactory).ifPresent(value->userTrade.setProfitAndLoss(value));
        org.marketcetera.trading.rpc.TradeRpcUtil.getOrderId(inUserTrade.getOrderId()).ifPresent(value->userTrade.setOrderId(value));
        return java.util.Optional.of(userTrade);
    }
}
