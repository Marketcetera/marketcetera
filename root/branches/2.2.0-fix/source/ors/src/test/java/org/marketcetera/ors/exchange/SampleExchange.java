package org.marketcetera.ors.exchange;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.quickfix.SpringSessionSettings;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import quickfix.Acceptor;
import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.SocketAcceptor;

/**
 * A miniscule exchange.
 *
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id$
 */

/* $License$ */

public class SampleExchange
    implements Application
{

    // INSTANCE DATA.

    private final AbstractApplicationContext mContext;
    private final Acceptor mAcceptor;
    private final BlockingQueue<Event> mEvents;


    // CONSTRUCTORS.

    public SampleExchange
        (String configFile)
        throws ConfigError
    {
        mEvents=new LinkedBlockingQueue<Event>();
        mContext=new FileSystemXmlApplicationContext
            (new String[] {"file:"+configFile});
        SpringSessionSettings settings=
            (SpringSessionSettings)mContext.getBean("x_settings");
        mAcceptor=new SocketAcceptor
            (this,settings.getQMessageStoreFactory(),
             settings.getQSettings(),settings.getQLogFactory(),
             new DefaultMessageFactory());
    }


    // Application.

    @Override
    public void onCreate
        (SessionID sessionId)
    {
        add(new CreateEvent(sessionId));
    }

    @Override
    public void onLogon
        (SessionID sessionId)
    {
        add(new LogonEvent(sessionId));
    }

    @Override
    public void onLogout
        (SessionID sessionId)
    {
        add(new LogoutEvent(sessionId));
    }

    @Override
    public void toAdmin
        (Message message,
         SessionID sessionId)
    {
        add(new ToAdminEvent(sessionId,message));
    }

    @Override
    public void toApp
        (Message message,
         SessionID sessionId)
    {
        add(new ToAppEvent(sessionId,message));
    }

    @Override
    public void fromAdmin
        (Message message,
         SessionID sessionId)
    {
        add(new FromAdminEvent(sessionId,message));
    }

    @Override
    public void fromApp
        (Message message,
         SessionID sessionId)
    {
        add(new FromAppEvent(sessionId,message));
    }        


    // INSTANCE METHODS.

    public void start()
        throws ConfigError
    {
        mContext.registerShutdownHook();
        mContext.start();
        mAcceptor.start();
        SLF4JLoggerProxy.debug(this,"Exchange started");
    }

    public synchronized void stop()
    {
        SLF4JLoggerProxy.debug(this,"Exchange stopping");
        mAcceptor.stop();
        mContext.close();
        SLF4JLoggerProxy.debug(this,"Exchange stopped");
    }

    public void add
        (Event event)
    {
        SLF4JLoggerProxy.debug(this,"Adding event {}",event);
        mEvents.add(event);
    }

    public Event getNext()
        throws InterruptedException 
    {
        return mEvents.take();
    }
    
    public void clear()
    {
        mEvents.clear();
    }

    public static void sendMessage
        (Message message,
         SessionID sessionID)
        throws SessionNotFound
    {
        Session.sendToTarget(message,sessionID);
    }
}
