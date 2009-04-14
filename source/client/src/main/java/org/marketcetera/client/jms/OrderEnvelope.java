package org.marketcetera.client.jms;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import org.marketcetera.trade.FIXOrderImpl;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderBaseImpl;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.tags.SessionId;

/**
 * A trade message envelope, used to send an {@link Order} instance
 * over JMS as part of an existing Web Services session.
 *
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */

/* $License$ */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@ClassVersion("$Id$")
public class OrderEnvelope
    implements Serializable
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // INSTANCE DATA.

    @XmlElementRefs(value={
        @XmlElementRef(type=OrderBaseImpl.class),
        @XmlElementRef(type=FIXOrderImpl.class)
    })
    private final Order mOrder;
    private final SessionId mSessionId;


    // CONSTRUCTORS.

    /**
     * Creates a new envelope with the given order (which must be
     * either a {@link OrderBaseImpl} or a {@link FIXOrderImpl}) and
     * session ID.
     *
     * @param order The order.
     * @param sessionId The session ID.
     */

    public OrderEnvelope
        (Order order,
         SessionId sessionId)
    {
        mOrder=order;
        mSessionId=sessionId;
    }

    /**
     * Creates a new envelope. This empty constructor is intended for
     * use by JAXB.
     */

    protected OrderEnvelope()
    {
        mOrder=null;
        mSessionId=null;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's order.
     *
     * @return The order.
     */

    public Order getOrder()
    {
        return mOrder;
    }

    /**
     * Returns the receiver's session ID.
     *
     * @return The ID.
     */

    public SessionId getSessionId()
    {
        return mSessionId;
    }


    // Object.

    @Override
    public String toString()
    {
        return Messages.ORDER_ENVELOPE_TO_STRING.getText
            (String.valueOf(getOrder()),
             String.valueOf(getSessionId()));
    }
}
