package org.marketcetera.trade;

import javax.xml.bind.annotation.XmlRootElement;
import org.marketcetera.util.misc.ClassVersion;
import quickfix.Message;

/**
 * An ORS response that wraps a generic FIX message which cannot be
 * wrapped by any other FIX Agnostic wrapper. This class is public for
 * the sake of JAXB and is not intended for general use.
 *
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id$
 */

/* $License$ */

@XmlRootElement
@ClassVersion("$Id$")
public class FIXResponseImpl
    extends FIXMessageWrapper
    implements FIXResponse
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // INSTANCE DATA.

    private final BrokerID mBrokerID;
    private final Originator mOriginator;
    private final UserID mActorID;
    private final UserID mViewerID;


    // CONSTRUCTORS.

    /**
     * Creates a new ORS response that wraps the given generic FIX
     * message. The message originated at the given originator and is
     * associated with the given broker (identified by its ID). The
     * message's actor and viewer users have the given IDs.
     *
     * @param msg The FIX Message.
     * @param brokerID The broker ID. It may be null.
     * @param originator The originator.
     * @param actorID The ID of the actor user. It may be null.
     * @param viewerID The ID of the viewer user. It may be null.
     */

    FIXResponseImpl
        (Message msg,
         BrokerID brokerID,
         Originator originator,
         UserID actorID,
         UserID viewerID)
     {
         super(msg);
         if (originator==null) {
             throw new NullPointerException();
         }
         mBrokerID=brokerID;
         mOriginator=originator;
         mActorID=actorID;
         mViewerID=viewerID;
    }

    /**
     * Creates a new empty ORS response. This empty constructor is
     * intended for use by JAXB.
     */

    private FIXResponseImpl()
    {
        mBrokerID=null;
        mOriginator=null;
        mActorID=null;
        mViewerID=null;
    }


    // FIXResponse.

    @Override
    public BrokerID getBrokerID()
    {
        return mBrokerID;
    }

    @Override
    public Originator getOriginator()
    {
        return mOriginator;
    }

    @Override
    public UserID getActorID()
    {
        return mActorID;
    }

    @Override
    public UserID getViewerID()
    {
        return mViewerID;
    }

    @Override
    public synchronized String toString()
    {
        return Messages.FIX_RESPONSE_TO_STRING.getText
            (String.valueOf(getBrokerID()),
             String.valueOf(getOriginator()),
             String.valueOf(getMessage()),
             String.valueOf(getActorID()),
             String.valueOf(getViewerID()));
    }
}
