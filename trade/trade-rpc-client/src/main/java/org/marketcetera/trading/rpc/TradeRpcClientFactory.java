package org.marketcetera.trading.rpc;

import org.marketcetera.admin.User;
import org.marketcetera.admin.UserFactory;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.ClusterDataFactory;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.MutableActiveFixSessionFactory;
import org.marketcetera.fix.MutableFixSessionFactory;
import org.marketcetera.trade.MutableOrderSummary;
import org.marketcetera.trade.MutableOrderSummaryFactory;
import org.marketcetera.trade.MutableReport;
import org.marketcetera.trade.MutableReportFactory;
import org.marketcetera.trade.client.TradeClient;
import org.marketcetera.trade.client.TradeClientFactory;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Creates RPC {@link TradeClient} implementations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TradeRpcClientFactory
        implements TradeClientFactory<TradeRpcClientParameters>
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientFactory#create(org.marketcetera.rpc.client.RpcClientParameters)
     */
    @Override
    public TradeRpcClient create(TradeRpcClientParameters inParameters)
    {
        TradeRpcClient tradeRpcClient = new TradeRpcClient(inParameters);
        tradeRpcClient.setActiveFixSessionFactory(activeFixSessionFactory);
        tradeRpcClient.setFixSessionFactory(fixSessionFactory);
        tradeRpcClient.setClusterDataFactory(clusterDataFactory);
        tradeRpcClient.setOrderSummaryFactory(orderSummaryFactory);
        tradeRpcClient.setUserFactory(userFactory);
        tradeRpcClient.setReportFactory(reportFactory);
        return tradeRpcClient;
    }
    /**
     * creates {@link MutableOrderSummary} objects
     */
    @Autowired
    private MutableOrderSummaryFactory orderSummaryFactory;
    /**
     * creates {@link User} objects
     */
    @Autowired
    private UserFactory userFactory;
    /**
     * creates {@link MutableReport} objects
     */
    @Autowired
    private MutableReportFactory reportFactory;
    /**
     * creates {@link ActiveFixSession} objects
     */
    @Autowired
    private MutableActiveFixSessionFactory activeFixSessionFactory;
    /**
     * creates {@link FixSession} objects
     */
    @Autowired
    private MutableFixSessionFactory fixSessionFactory;
    /**
     * creates {@link ClusterData} objects
     */
    @Autowired
    private ClusterDataFactory clusterDataFactory;
}
