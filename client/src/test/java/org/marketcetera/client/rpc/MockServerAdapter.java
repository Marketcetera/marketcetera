package org.marketcetera.client.rpc;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.ws.wrappers.MapWrapper;

import quickfix.Message;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MockServerAdapter
        implements RpcServerAdapter
{
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getNextOrderID()
     */
    @Override
    public String getNextOrderID()
    {
        return String.valueOf(System.nanoTime());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getReportsSince(java.lang.String, java.util.Date)
     */
    @Override
    public ReportBaseImpl[] getReportsSince(String inUsername,
                                            Date inOrigin)
    {
        return reportsSince.toArray(new ReportBaseImpl[reportsSince.size()]);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getOpenOrders(java.lang.String)
     */
    @Override
    public List<ReportBaseImpl> getOpenOrders(String inUsername)
    {
        return openOrders;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getBrokersStatus(java.lang.String)
     */
    @Override
    public BrokersStatus getBrokersStatus(String inUser)
    {
        return brokersStatus;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getEquityPositionAsOf(java.lang.String, java.util.Date, org.marketcetera.trade.Equity)
     */
    @Override
    public BigDecimal getEquityPositionAsOf(String inUser,
                                            Date inOrigin,
                                            Equity inInstrument)
    {
        for(Map.Entry<PositionKey<Equity>,BigDecimal> entry : equityPositions.entrySet()) {
            if(entry.getKey().getInstrument().equals(inInstrument)) {
                return entry.getValue();
            }
        }
        return BigDecimal.ZERO;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getOptionPositionAsOf(java.lang.String, java.util.Date, org.marketcetera.trade.Option)
     */
    @Override
    public BigDecimal getOptionPositionAsOf(String inUser,
                                            Date inOrigin,
                                            Option inInstrument)
    {
        for(Map.Entry<PositionKey<Option>,BigDecimal> entry : optionPositions.entrySet()) {
            if(entry.getKey().getInstrument().equals(inInstrument)) {
                return entry.getValue();
            }
        }
        return BigDecimal.ZERO;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getFuturePositionAsOf(java.lang.String, java.util.Date, org.marketcetera.trade.Future)
     */
    @Override
    public BigDecimal getFuturePositionAsOf(String inUser,
                                            Date inOrigin,
                                            Future inInstrument)
    {
        for(Map.Entry<PositionKey<Future>,BigDecimal> entry : futurePositions.entrySet()) {
            if(entry.getKey().getInstrument().equals(inInstrument)) {
                return entry.getValue();
            }
        }
        return BigDecimal.ZERO;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getCurrencyPositionAsOf(java.lang.String, java.util.Date, org.marketcetera.trade.Currency)
     */
    @Override
    public BigDecimal getCurrencyPositionAsOf(String inUser,
                                              Date inOrigin,
                                              Currency inInstrument)
    {
        for(Map.Entry<PositionKey<Currency>,BigDecimal> entry : currencyPositions.entrySet()) {
            if(entry.getKey().getInstrument().equals(inInstrument)) {
                return entry.getValue();
            }
        }
        return BigDecimal.ZERO;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getAllCurrencyPositionsAsOf(java.lang.String, java.util.Date)
     */
    @Override
    public MapWrapper<PositionKey<Currency>,BigDecimal> getAllCurrencyPositionsAsOf(String inUser,
                                                                                    Date inDate)
    {
        return new MapWrapper<>(currencyPositions);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getAllFuturePositionsAsOf(java.lang.String, java.util.Date)
     */
    @Override
    public MapWrapper<PositionKey<Future>,BigDecimal> getAllFuturePositionsAsOf(String inUser,
                                                                                Date inDate)
    {
        return new MapWrapper<>(futurePositions);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getAllEquityPositionsAsOf(java.lang.String, java.util.Date)
     */
    @Override
    public MapWrapper<PositionKey<Equity>,BigDecimal> getAllEquityPositionsAsOf(String inUser,
                                                                                Date inDate)
    {
        return new MapWrapper<>(equityPositions);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getAllOptionPositionsAsOf(java.lang.String, java.util.Date)
     */
    @Override
    public MapWrapper<PositionKey<Option>,BigDecimal> getAllOptionPositionsAsOf(String inUser,
                                                                                Date inDate)
    {
        return new MapWrapper<>(optionPositions);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getOptionPositionsAsOf(java.lang.String, java.util.Date, java.lang.String[])
     */
    @Override
    public MapWrapper<PositionKey<Option>,BigDecimal> getOptionPositionsAsOf(String inUser,
                                                                             Date inDate,
                                                                             String[] inArray)
    {
        return new MapWrapper<>(optionPositions);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getUserInfo(org.marketcetera.trade.UserID)
     */
    @Override
    public UserInfo getUserInfo(UserID inUserID)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getUnderlying(java.lang.String)
     */
    @Override
    public String getUnderlying(String inSymbol)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getOptionRoots(java.lang.String)
     */
    @Override
    public Collection<String> getOptionRoots(String inSymbol)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#resolveSymbol(java.lang.String)
     */
    @Override
    public Instrument resolveSymbol(String inSymbol)
    {
        return instrumentsToResolve.get(inSymbol);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getRootOrderIdFor(org.marketcetera.trade.OrderID)
     */
    @Override
    public OrderID getRootOrderIdFor(OrderID inOrderID)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getUserData(java.lang.String)
     */
    @Override
    public String getUserData(String inUser)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#setUserData(java.lang.String, java.lang.String)
     */
    @Override
    public void setUserData(String inUser,
                            String inString)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#addReport(java.lang.String, quickfix.Message, org.marketcetera.trade.BrokerID)
     */
    @Override
    public void addReport(String inUser,
                          Message inMessage,
                          BrokerID inBrokerID)
    {
        addedReports.add(inMessage);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#deleteReport(java.lang.String, org.marketcetera.trade.ExecutionReport)
     */
    @Override
    public void deleteReport(String inUser,
                             ExecutionReport inMessage)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * Resets data.
     */
    public void reset()
    {
        instrumentsToResolve.clear();
        futurePositions.clear();
        equityPositions.clear();
        optionPositions.clear();
        currencyPositions.clear();
        reportsSince.clear();
        addedReports.clear();
        openOrders.clear();
        brokersStatus = null;
    }
    /**
     * Get the addedReports value.
     *
     * @return a <code>List&lt;Message&gt;</code> value
     */
    public List<Message> getAddedReports()
    {
        return addedReports;
    }
    /**
     * Sets the addedReports value.
     *
     * @param inAddedReports a <code>List&lt;Message&gt;</code> value
     */
    public void setAddedReports(List<Message> inAddedReports)
    {
        addedReports = inAddedReports;
    }
    /**
     * Get the reportsSince value.
     *
     * @return a <code>List&lt;ReportBaseImpl&gt;</code> value
     */
    public List<ReportBaseImpl> getReportsSince()
    {
        return reportsSince;
    }
    /**
     * Sets the reportsSince value.
     *
     * @param inReportsSince a <code>List&lt;ReportBaseImpl&gt;</code> value
     */
    public void setReportsSince(List<ReportBaseImpl> inReportsSince)
    {
        reportsSince = inReportsSince;
    }
    /**
     * Get the instrumentsToResolve value.
     *
     * @return a <code>Map&lt;String,Instrument&gt;</code> value
     */
    public Map<String,Instrument> getInstrumentsToResolve()
    {
        return instrumentsToResolve;
    }
    /**
     * Sets the instrumentsToResolve value.
     *
     * @param inInstrumentsToResolve a <code>Map&lt;String,Instrument&gt;</code> value
     */
    public void setInstrumentsToResolve(Map<String,Instrument> inInstrumentsToResolve)
    {
        instrumentsToResolve = inInstrumentsToResolve;
    }
    /**
     * Get the futurePositions value.
     *
     * @return a <code>Map&lt;PositionKey&lt;Future&gt;,BigDecimal&gt;</code> value
     */
    public Map<PositionKey<Future>,BigDecimal> getFuturePositions()
    {
        return futurePositions;
    }
    /**
     * Get the currencyPositions value.
     *
     * @return a <code>Map&lt;PositionKey&lt;Currency&gt;,BigDecimal&gt;</code> value
     */
    public Map<PositionKey<Currency>,BigDecimal> getCurrencyPositions()
    {
        return currencyPositions;
    }
    /**
     * Get the equityPositions value.
     *
     * @return a <code>Map&lt;PositionKey&lt;Equity&gt;,BigDecimal&gt;</code> value
     */
    public Map<PositionKey<Equity>,BigDecimal> getEquityPositions()
    {
        return equityPositions;
    }
    /**
     * Get the optionPositions value.
     *
     * @return a <code>Map&lt;PositionKey&lt;Option&gt;,BigDecimal&gt;</code> value
     */
    public Map<PositionKey<Option>,BigDecimal> getOptionPositions()
    {
        return optionPositions;
    }
    /**
     * Get the openOrders value.
     *
     * @return a <code>List&lt;ReportBaseImpl&gt;</code> value
     */
    public List<ReportBaseImpl> getOpenOrders()
    {
        return openOrders;
    }
    /**
     * Sets the openOrders value.
     *
     * @param inOpenOrders a <code>List<ReportBaseImpl></code> value
     */
    public void setOpenOrders(List<ReportBaseImpl> inOpenOrders)
    {
        openOrders = inOpenOrders;
    }
    /**
     * Get the brokersStatus value.
     *
     * @return a <code>BrokersStatus</code> value
     */
    public BrokersStatus getBrokersStatus()
    {
        return brokersStatus;
    }
    /**
     * Sets the brokersStatus value.
     *
     * @param inBrokersStatus a <code>BrokersStatus</code> value
     */
    public void setBrokersStatus(BrokersStatus inBrokersStatus)
    {
        brokersStatus = inBrokersStatus;
    }
    /**
     * 
     */
    private BrokersStatus brokersStatus;
    /**
     * 
     */
    private List<ReportBaseImpl> openOrders = Lists.newArrayList();
    /**
     * 
     */
    private List<Message> addedReports = Lists.newArrayList();
    /**
     * 
     */
    private List<ReportBaseImpl> reportsSince = Lists.newArrayList();
    /**
     * contains instruments to resolve
     */
    private Map<String,Instrument> instrumentsToResolve = Maps.newHashMap();
    /**
     * 
     */
    private final Map<PositionKey<Future>,BigDecimal> futurePositions = Maps.newHashMap();
    /**
     * 
     */
    private final Map<PositionKey<Currency>,BigDecimal> currencyPositions = Maps.newHashMap();
    /**
     * 
     */
    private final Map<PositionKey<Equity>,BigDecimal> equityPositions = Maps.newHashMap();
    /**
     * 
     */
    private final Map<PositionKey<Option>,BigDecimal> optionPositions = Maps.newHashMap();
}
