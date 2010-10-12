package org.marketcetera.client.jms;

import java.util.ArrayList;
import java.util.List;

/**
 * A sample message handler. It cannot be an inner class.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class SampleReceiveOnlyHandler<T>
    implements ReceiveOnlyHandler<T>
{

    // INSTANCE DATA.

    private List<T> mReceived=new ArrayList<T>(JmsManagerTest.TEST_COUNT);


    // INSTANCE METHODS.

    List<T> getReceived()
    {
        return mReceived;
    }


    // ReceiveOnlyHandler.

    @Override
    public void receiveMessage
        (T msg)
    {
        getReceived().add(msg);
    }
}
