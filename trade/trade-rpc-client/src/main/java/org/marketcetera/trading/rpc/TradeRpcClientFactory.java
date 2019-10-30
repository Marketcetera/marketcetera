package org.marketcetera.trading.rpc;

import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.ClusterDataFactory;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.MutableActiveFixSessionFactory;
import org.marketcetera.fix.MutableFixSessionFactory;
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
        TradeRpcClient TradeRpcClient = new TradeRpcClient(inParameters);
        TradeRpcClient.setActiveFixSessionFactory(activeFixSessionFactory);
        TradeRpcClient.setFixSessionFactory(fixSessionFactory);
        TradeRpcClient.setClusterDataFactory(clusterDataFactory);
        return TradeRpcClient;
    }
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
