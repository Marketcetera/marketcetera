package org.marketcetera.client;

import javax.xml.bind.JAXBException;
import org.marketcetera.client.jms.JmsManager;
import org.marketcetera.client.jms.JmsUtils;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.SessionFactory;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.tags.SessionId;
import org.springframework.jms.core.JmsOperations;

/**
 * A mock session factory.
 *
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class MockSessionFactory
    implements SessionFactory<Object>
{

    // INSTANCE DATA.

    private final JmsManager mJmsManager;
    private final MockMessageHandler mHandler;


    // CONSTRUCTORS.

    public MockSessionFactory
        (JmsManager jmsManager,
         MockMessageHandler handler)
    {
        mJmsManager=jmsManager;
        mHandler=handler;
    }


    // SessionFactory.

    @Override
    public Object createSession
        (StatelessClientContext context,
         String user,
         SessionId id)
    {
        JmsOperations replySender;
        try {
            replySender=mJmsManager.getOutgoingJmsFactory().createJmsTemplateX
                (JmsUtils.getReplyTopicName(id),true);
        } catch (JAXBException ex) {
            throw new IllegalStateException
                ("Cannot initialize trade message reply topic",ex);
        }
        mHandler.setReplySender(replySender);
        return null;
    }

    @Override
    public void removedSession(Object session)
    {
        mHandler.setReplySender(null);
    }
}
