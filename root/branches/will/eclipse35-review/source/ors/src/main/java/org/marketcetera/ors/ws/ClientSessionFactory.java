package org.marketcetera.ors.ws;

import javax.xml.bind.JAXBException;
import org.marketcetera.client.jms.JmsManager;
import org.marketcetera.client.jms.JmsUtils;
import org.marketcetera.ors.UserManager;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.ors.security.SingleSimpleUserQuery;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.util.except.I18NRuntimeException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.SessionFactory;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.tags.SessionId;
import org.springframework.jms.core.JmsOperations;

/**
 * A session factory.
 *
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class ClientSessionFactory
    implements SessionFactory<ClientSession>
{

    // INSTANCE DATA.

    private final JmsManager mJmsManager;
    private final UserManager mUserManager;


    // CONSTRUCTORS.

    /**
     * Creates a new session factory which uses the given JMS manager
     * to create reply topics, and which notifies the given user
     * manager when sessions are added/removed.
     *
     * @param jmsManager The JMS manager.
     * @param userManager The user manager.
     */

    public ClientSessionFactory
        (JmsManager jmsManager,
         UserManager userManager)
    {
        mJmsManager=jmsManager;
        mUserManager=userManager;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's JMS manager.
     *
     * @return The manager.
     */

    private JmsManager getJmsManager()
    {
        return mJmsManager;
    }

    /**
     * Returns the receiver's user manager.
     *
     * @return The manager.
     */

    private UserManager getUserManager()
    {
        return mUserManager;
    }


    // SessionFactory.

    @Override
    public ClientSession createSession
        (StatelessClientContext context,
         String user,
         SessionId id)
    {
        JmsOperations jmsOps;
        SimpleUser dbUser;
        String topicName=JmsUtils.getReplyTopicName(id);
        try {
            jmsOps=getJmsManager().getOutgoingJmsFactory().createJmsTemplateX
                (topicName,true);
        } catch (JAXBException ex) {
            throw new I18NRuntimeException
                (ex,new I18NBoundMessage1P
                 (Messages.CANNOT_CREATE_REPLY_TOPIC,topicName));
        }
        try {
            dbUser=(new SingleSimpleUserQuery(user)).fetch();
        } catch (PersistenceException ex) {
            throw new I18NRuntimeException
                (ex,new I18NBoundMessage1P
                 (Messages.CANNOT_RETRIEVE_USER,user));
        }
        ClientSession session=new ClientSession(id,dbUser,jmsOps);
        getUserManager().addSession(session);
        return session;
    }

    @Override
    public void removedSession(ClientSession session)
    {
        getUserManager().removedSession(session);
    }
}
