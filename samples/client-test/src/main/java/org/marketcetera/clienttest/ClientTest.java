package org.marketcetera.clienttest;

import java.math.BigDecimal;

import org.marketcetera.client.Client;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ReportListener;
import org.marketcetera.client.rpc.RpcClientFactory;
import org.marketcetera.client.rpc.RpcClientParameters;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Demonstrates how to connect to MATP order services from an external application.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ClientTest
{
    /**
     * Main run method.
     *
     * @param inArgs a <code>String[]</code> value
     */
    public static void main(String[] inArgs)
    {
        SLF4JLoggerProxy.info(ClientTest.class,
                              "Starting client test");
        // connect server client
        RpcClientParameters parameters = new RpcClientParameters("user",
                                                                 "password".toCharArray(),
                                                                 "tcp://dare.marketcetera.com:61616",
                                                                 "dare.marketcetera.com",
                                                                 8999);
        ClientManager.setClientFactory(new RpcClientFactory());
        ClientManager.init(parameters);
        Client client = ClientManager.getInstance();
        client.reconnect();
        SLF4JLoggerProxy.info(ClientTest.class,
                              "Connected to server: {}",
                              client.isServerAlive());
        client.addReportListener(new ReportListener() {
                @Override
                public void receiveExecutionReport(ExecutionReport inReport)
                {
                    SLF4JLoggerProxy.info(this,
                                          "Received {}",
                                          inReport);
                }
                @Override
                public void receiveCancelReject(OrderCancelReject inReport)
                {
                    SLF4JLoggerProxy.info(this,
                                          "Received {}",
                                          inReport);
                }
            });
        Factory factory = Factory.getInstance();
        OrderSingle testOrder = factory.createOrderSingle();
        testOrder.setInstrument(new Equity("METC"));
        testOrder.setOrderType(OrderType.Limit);
        testOrder.setQuantity(BigDecimal.TEN);
        testOrder.setPrice(BigDecimal.TEN);
        testOrder.setSide(Side.Buy);
        SLF4JLoggerProxy.info(ClientTest.class,
                              "Sending {}",
                              testOrder);
        client.sendOrder(testOrder);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.close();
        SLF4JLoggerProxy.info(ClientTest.class,
                              "Ending client test");
    }
}
