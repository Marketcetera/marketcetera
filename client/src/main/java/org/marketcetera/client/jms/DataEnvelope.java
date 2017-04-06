package org.marketcetera.client.jms;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.event.Event;
import org.marketcetera.trade.FIXOrderImpl;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderBaseImpl;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.tags.SessionId;

/* $License$ */

/**
 * A trade message envelope, used to send an {@link Order} instance
 * over JMS as part of an existing Web Services session.
 *
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@ClassVersion("$Id$")
public class DataEnvelope
        implements Serializable
{
    /**
     * Creates a new envelope with the given order (which must be
     * either a {@link OrderBaseImpl} or a {@link FIXOrderImpl}) and
     * session ID.
     *
     * @param order The order.
     * @param sessionId The session ID.
     */
    public DataEnvelope(Order order,
                        SessionId sessionId)
    {
        event = null;
        mOrder=order;
        mSessionId=sessionId;
    }
    /**
     * Create a new DataEnvelope instance.
     *
     * @param inEvent an <code>Event</code> value
     * @param inSessionId a <code>SessionId</code> value
     */
    public DataEnvelope(Event inEvent,
                        SessionId inSessionId)
    {
        event = inEvent;
        mOrder = null;
        mSessionId = inSessionId;
    }
    /**
     * Creates a new envelope. This empty constructor is intended for
     * use by JAXB.
     */
    protected DataEnvelope()
    {
        event = null;
        mOrder=null;
        mSessionId=null;
    }
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
    /**
     * Get the event value.
     *
     * @return an <code>Event</code> value
     */
    public Event getEvent()
    {
        return event;
    }
    @Override
    public String toString()
    {
        return Messages.DATA_ENVELOPE_TO_STRING.getText(String.valueOf(getOrder()),
                                                        String.valueOf(getEvent()),
                                                        String.valueOf(getSessionId()));
    }
    @XmlElementRefs(value={@XmlElementRef(type=OrderBaseImpl.class),@XmlElementRef(type=FIXOrderImpl.class)})
    private final Order mOrder;
    private final Event event;
    private final SessionId mSessionId;
    private static final long serialVersionUID = -8590418015063473239L;
}
