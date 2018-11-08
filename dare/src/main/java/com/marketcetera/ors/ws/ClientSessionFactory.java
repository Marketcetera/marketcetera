package com.marketcetera.ors.ws;

import javax.xml.bind.JAXBException;

import org.marketcetera.client.jms.JmsManager;
import org.marketcetera.client.jms.JmsUtils;
import org.marketcetera.util.except.I18NRuntimeException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.SessionFactory;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.tags.SessionId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsOperations;

import com.marketcetera.ors.UserManager;
import com.marketcetera.ors.dao.UserService;
import com.marketcetera.ors.info.SystemInfo;
import com.marketcetera.ors.security.SimpleUser;

/**
 * A session factory.
 *
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id: ClientSessionFactory.java 16468 2014-05-12 00:36:56Z colin $
 */

/* $License$ */

@ClassVersion("$Id: ClientSessionFactory.java 16468 2014-05-12 00:36:56Z colin $")
public class ClientSessionFactory
        implements SessionFactory<ClientSession>
{

    // INSTANCE DATA.

    private final SystemInfo mSystemInfo;
    private final JmsManager mJmsManager;
    private final UserManager mUserManager;


    // CONSTRUCTORS.

    /**
     * Creates a new session factory which uses the given system
     * information to create session information, which uses the given
     * JMS manager to create reply topics, and which notifies the
     * given user manager when sessions are added/removed.
     *
     * @param systemInfo The system information.
     * @param jmsManager The JMS manager.
     * @param userManager The user manager.
     */

    public ClientSessionFactory(SystemInfo systemInfo,
                                JmsManager jmsManager,
                                UserManager userManager)
    {
        mSystemInfo=systemInfo;
        mJmsManager=jmsManager;
        mUserManager=userManager;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's system information.
     *
     * @return The information.
     */

    public SystemInfo getSystemInfo()
    {
        return mSystemInfo;
    }

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
    public ClientSession createSession(StatelessClientContext context,
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
        dbUser=(userService.findByName(user));
        ClientSession session=new ClientSession
            (getSystemInfo(),id,dbUser,jmsOps);
        getUserManager().addSession(session);
        return session;
    }

    @Override
    public void removedSession(ClientSession session)
    {
        getUserManager().removedSession(session);
    }
    
    /**
     * Get the userService value.
     *
     * @return a <code>UserService</code> value
     */
    public UserService getUserService()
    {
        return userService;
    }
    /**
     * Sets the userService value.
     *
     * @param inUserService a <code>UserService</code> value
     */
    @Autowired
    public void setUserService(UserService inUserService)
    {
        userService = inUserService;
    }
    /**
     * provides access to user objects
     */
    private UserService userService;
}
