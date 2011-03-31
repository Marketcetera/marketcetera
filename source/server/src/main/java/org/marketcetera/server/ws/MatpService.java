package org.marketcetera.server.ws;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.marketcetera.saclient.SAService;
import org.marketcetera.trade.FIXOrderImpl;
import org.marketcetera.trade.OrderCancelImpl;
import org.marketcetera.trade.OrderReplaceImpl;
import org.marketcetera.trade.OrderSingleImpl;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
@WebService(targetNamespace = "matp")
@ClassVersion("$Id$")
public interface MatpService
        extends SAService,org.marketcetera.client.Service
{
    public void sendOrderSingle(@WebParam(name="context")ClientContext inContext,
                                @WebParam(name="order")OrderSingleImpl inOrderSingle);
    public void sendOrderReplace(@WebParam(name="context")ClientContext inContext,
                                 @WebParam(name="replace")OrderReplaceImpl inOrderReplace);
    public void sendOrderCancel(@WebParam(name="context")ClientContext inContext,
                                @WebParam(name="cancel")OrderCancelImpl inOrderCancel);
    public void sendOrderRaw(@WebParam(name="context")ClientContext inContext,
                             @WebParam(name="order")FIXOrderImpl inFIXOrder);
}
