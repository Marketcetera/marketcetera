package org.marketcetera.trade.pnl;

import java.math.BigDecimal;

import javax.annotation.PostConstruct;

import org.marketcetera.admin.User;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.Preserve;
import org.marketcetera.eventbus.EventBusService;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.TradeMessageListener;
import org.marketcetera.trade.UserID;
import org.marketcetera.trade.pnl.dao.LotDao;
import org.marketcetera.trade.pnl.dao.PersistentProfitAndLoss;
import org.marketcetera.trade.pnl.dao.PersistentProfitAndLossFactory;
import org.marketcetera.trade.pnl.dao.PersistentTrade;
import org.marketcetera.trade.pnl.dao.PersistentTradeFactory;
import org.marketcetera.trade.pnl.dao.PersistentUserTrade;
import org.marketcetera.trade.pnl.dao.PersistentUserTradeFactory;
import org.marketcetera.trade.pnl.dao.ProfitAndLossDao;
import org.marketcetera.trade.pnl.dao.TradeDao;
import org.marketcetera.trade.pnl.dao.UserTradeDao;
import org.marketcetera.trade.pnl.event.PositionChangedEvent;
import org.marketcetera.trade.service.TradeService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.eventbus.Subscribe;

/* $License$ */

/**
 * Provides TradePnlServiceImpl services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Preserve
@Component
public class TradePnlServiceImpl
        implements TradePnlService, TradeMessageListener
{
    /**
     * Requests positions for a user.
     *
     * @param inUserId an <code>UserID</code> value
     * @param innull an <code>PageRequest</code> value
     * @returns an <code>CurrentPosition</code> value
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public CollectionPageResponse<CurrentPosition> getCurrentPositions(UserID inUserId,PageRequest innull)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * Requests profit and loss for a user and an instrument.
     *
     * @param inUserId an <code>UserID</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @param inPageRequest a <code>PageRequest</code> value
     * @returns a <code>CollectionPageResponse&lt;ProfitAndLoss&gt;</code> value
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public CollectionPageResponse<ProfitAndLoss> getProfitAndLoss(UserID inUserId,
                                                                  Instrument inInstrument,
                                                                  PageRequest inPageRequest)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * Accept incoming PositionChangedEvent values.
     */
    @Subscribe
    public void accept(PositionChangedEvent inPositionChangedEvent)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.TradeMessageListener#receiveTradeMessage(org.marketcetera.trade.TradeMessage)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void receiveTradeMessage(TradeMessage inTradeMessage)
    {
        if(inTradeMessage instanceof ExecutionReport) {
            ExecutionReport executionReport = (ExecutionReport)inTradeMessage;
            if(executionReport.getExecutionType() != null && executionReport.getExecutionType().isFill()) {
                SLF4JLoggerProxy.info(this,
                                      "Received {}",
                                      executionReport);
                PersistentTrade trade = tradeFactory.create();
                trade.setExecutionId(new OrderID(executionReport.getExecutionID()));
                trade.setInstrument(executionReport.getInstrument());
                trade.setPrice(executionReport.getLastPrice());
                trade.setQuantity(executionReport.getLastQuantity());
                trade.setTransactionTime(executionReport.getTransactTime());
                trade = tradeDao.save(trade);
                Position position = positionEngine.createPosition(executionReport,
                                                                  trade);
                ProfitAndLoss profitAndLoss = createProfitAndLoss(executionReport,
                                                                  position,
                                                                  trade);
                PersistentUserTrade userTrade = userTradeFactory.create();
                userTrade.setOrderId(executionReport.getOrderID());
                userTrade.setProfitAndLoss(null);
                userTrade.setTrade(trade);
                User user = userService.findByUserId(executionReport.getActorID());
                // TODO if user is null
                userTrade.setUser(user);
                userTrade = userTradeDao.save(userTrade);
            }
        } else {
            SLF4JLoggerProxy.info(this,
                                  "Ignored {}",
                                  inTradeMessage);
        }
    }
    /**
     *
     *
     * @param inExecutionReport
     * @param inPosition
     * @param inTrade
     * @return
     */
    private PersistentProfitAndLoss createProfitAndLoss(ExecutionReport inExecutionReport,
                                                        Position inPosition,
                                                        PersistentTrade inTrade)
    {
        BigDecimal realizedGain = lotDao.findPnlGain(inTrade);
        BigDecimal basis = BigDecimal.ZERO;
        if(inExecutionReport.getSide().isBuy()) {
            // basis price for a buy is simply the price of this trade
            basis = inTrade.getPrice();
        } else {
            // basis price for a sell is the volume-weighted average cost of the lots used to purchase it
            basis = inPosition.getWeightedAverageCost();
        }
        PersistentProfitAndLoss profitAndLoss = profitAndLossFactory.create();
        profitAndLoss.setBasisPrice(basis);
        profitAndLoss.setInstrument(inExecutionReport.getInstrument());
        profitAndLoss.setPosition(inPosition.getPosition());
        profitAndLoss.setRealizedGain(realizedGain);
        profitAndLoss.setUser(inPosition.getUser());
//        profitAndLoss.setEffectiveDate(inTrade.getTransactionTime());
        return profitAndLossDao.save(profitAndLoss);
    }
    @Autowired
    private LotDao lotDao;
    @Autowired
    private ProfitAndLossDao profitAndLossDao;
    @Autowired
    private PersistentProfitAndLossFactory profitAndLossFactory;
    @Autowired
    private PositionEngine positionEngine;
    @Autowired
    private UserTradeDao userTradeDao;
    @Autowired
    private TradeDao tradeDao;
    @Autowired
    private UserService userService;
    @Autowired
    private PersistentUserTradeFactory userTradeFactory;
    @Autowired
    private PersistentTradeFactory tradeFactory;
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.info(this,
                              "Starting {}",
                              PlatformServices.getServiceName(getClass()));
        eventBusService.register(this);
        tradeService.addTradeMessageListener(this);
    }
    /**
     * provides access to event services
     */
    @Autowired
    private EventBusService eventBusService;
    /**
     * provides access to trade services
     */
    @Autowired
    private TradeService tradeService;
}
