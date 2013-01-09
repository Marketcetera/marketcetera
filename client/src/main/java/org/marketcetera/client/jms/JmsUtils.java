package org.marketcetera.client.jms;

import org.marketcetera.client.Service;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.tags.SessionId;

/**
 * Client JMS utilities.
 *
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public final class JmsUtils
{

    /**
     * Returns the topic name for replies sent from the ORS to the
     * ORS client, given the session ID.
     *
     * @param id The ID.
     *
     * @return The topic name.
     */

    public static String getReplyTopicName
        (SessionId id)
    {
        return Service.REPLY_TOPIC_PREFIX+id.getValue();
    }


    // CONSTRUCTOR.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private JmsUtils() {}
}
