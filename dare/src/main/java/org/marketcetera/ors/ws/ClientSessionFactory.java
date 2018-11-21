package org.marketcetera.ors.ws;

import org.marketcetera.admin.service.UserService;
import org.marketcetera.ors.info.SystemInfo;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.SessionFactory;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.tags.SessionId;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final SystemInfo mSystemInfo;
//    private final JmsManager mJmsManager;
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
//                                JmsManager jmsManager,
                                UserManager userManager)
    {
        mSystemInfo=systemInfo;
//        mJmsManager=jmsManager;
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

//    /**
//     * Returns the receiver's JMS manager.
//     *
//     * @return The manager.
//     */
//
//    private JmsManager getJmsManager()
//    {
//        return mJmsManager;
//    }

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
//        JmsOperations jmsOps;
//        User dbUser;
//        String topicName=JmsUtils.getReplyTopicName(id);
//        try {
//            jmsOps=getJmsManager().getOutgoingJmsFactory().createJmsTemplateX
//                (topicName,true);
//        } catch (JAXBException ex) {
//            throw new I18NRuntimeException
//                (ex,new I18NBoundMessage1P
//                 (Messages.CANNOT_CREATE_REPLY_TOPIC,topicName));
//        }
//        dbUser=(userService.findByName(user));
//        ClientSession session = new ClientSession(getSystemInfo(),
//                                                  id,
//                                                  dbUser,
//                                                  jmsOps);
//        getUserManager().addSession(session);
//        return session;
        throw new UnsupportedOperationException();
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
