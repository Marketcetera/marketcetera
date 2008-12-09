package org.marketcetera.orderloader;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ClientParameters;
import org.marketcetera.client.ClientInitException;
import org.marketcetera.client.ConnectionException;

/* $License$ */
/**
 * An order processor that sends orders to the server.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ServerOrderProcessor implements OrderProcessor {
    /**
     * Creates an instance.
     *
     * @param inParameter the parameters to connect to the server.
     *
     * @throws ClientInitException if there were unexpected issues initializing
     * the client.
     * @throws ConnectionException if there were network issues initializing
     * the client.
     */
    public ServerOrderProcessor(ClientParameters inParameter)
            throws ClientInitException, ConnectionException {
        ClientManager.init(inParameter);
    }

    @Override
    public void processOrder(Order inOrder, int inOrderIndex) throws Exception {
        if(inOrder instanceof OrderSingle) {
            ClientManager.getInstance().sendOrder((OrderSingle)inOrder);
        } else if(inOrder instanceof FIXOrder) {
            ClientManager.getInstance().sendOrderRaw((FIXOrder)inOrder);
        } else {
            throw new OrderParsingException(new I18NBoundMessage1P(
                    Messages.UNEXPECTED_ORDER_TYPE, inOrder));
        }
    }

    @Override
    public void done() {
        if (ClientManager.isInitialized()) {
            try {
                ClientManager.getInstance().close();
            } catch (ClientInitException ignore) {
            }
        }
    }
}
