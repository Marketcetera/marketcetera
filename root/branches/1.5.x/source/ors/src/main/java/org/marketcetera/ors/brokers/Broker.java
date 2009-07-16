package org.marketcetera.ors.brokers;

import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.ors.filters.MessageModifierManager;
import org.marketcetera.ors.filters.MessageRouteManager;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.quickfix.AnalyzedMessage;
import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;

/**
 * The in-memory representation of a single broker.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class Broker
{

    // CLASS DATA

    private static final String HEARTBEAT_CATEGORY=
        Broker.class.getName()+".HEARTBEATS"; //$NON-NLS-1$


    // INSTANCE DATA.

    private final SpringBroker mSpringBroker;
    private final BrokerID mBrokerID;
    private FIXDataDictionary mDataDictionary;
    private boolean mLoggedOn;


    // CONSTRUCTORS.

    /**
     * Creates a new broker based on the given configuration. Its
     * message modifiers are configured to rely on the given report
     * history services provider for persistence operations.
     *
     * @param springBroker The configuration.
     * @param historyServices The report history services provider.
     */

    public Broker
        (SpringBroker springBroker,
         ReportHistoryServices historyServices)
    {
        mSpringBroker=springBroker;
        mBrokerID=new BrokerID(getSpringBroker().getId());
        if (getModifiers()!=null) {
            getModifiers().setMessageFactory(getFIXMessageFactory());
            getModifiers().setHistoryServices(historyServices);
        }
        if (getPreSendModifiers()!=null) {
            getPreSendModifiers().setMessageFactory(getFIXMessageFactory());
            getPreSendModifiers().setHistoryServices(historyServices);
        }
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's configuration.
     *
     * @return The configuration.
     */

    public SpringBroker getSpringBroker()
    {
        return mSpringBroker;
    }

    /**
     * Returns the receiver's status.
     *
     * @return The status.
     */

    public BrokerStatus getStatus()
    {
        return new BrokerStatus(getName(),getBrokerID(),getLoggedOn());
    }

    /**
     * Returns the receiver's name.
     *
     * @return The name.
     */

    public String getName()
    {
        return getSpringBroker().getName();
    }

    /**
     * Returns the receiver's broker ID.
     *
     * @return The ID.
     */

    public BrokerID getBrokerID()
    {
        return mBrokerID;
    }

    /**
     * Returns the receiver's QuickFIX/J session ID.
     *
     * @return The ID.
     */

    public SessionID getSessionID()
    {
        return getSpringBroker().getDescriptor().getQSessionID();
    }

    /**
     * Returns the receiver's QuickFIX/J session.
     *
     * @return The session.
     */

    public Session getSession()
    {
        return Session.lookupSession(getSessionID());
    }

    /**
     * Returns the receiver's QuickFIX/J data dictionary.
     *
     * @return The dictionary.
     */

    public DataDictionary getDataDictionary()
    {
        return getSession().getDataDictionary();
    }

    /**
     * Returns the receiver's message modifier manager.
     *
     * @return The manager. It may be null.
     */

    public MessageModifierManager getModifiers()
    {
        return getSpringBroker().getModifiers();
    }

    /**
     * Returns the receiver's route manager.
     *
     * @return The manager. It may be null.
     */

    public MessageRouteManager getRoutes()
    {
        return getSpringBroker().getRoutes();
    }

    /**
     * Returns the receiver's pre-sending message modifier manager.
     *
     * @return The manager. It may be null.
     */

    public MessageModifierManager getPreSendModifiers()
    {
        return getSpringBroker().getPreSendModifiers();
    }

    /**
     * Returns the receiver's FIX version.
     *
     * @return The version.
     */

    public FIXVersion getFIXVersion()
    {
        return FIXVersion.getFIXVersion(getSessionID().getBeginString());
    }

    /**
     * Returns the receiver's FIX message factory.
     *
     * @return The factory.
     */

    public FIXMessageFactory getFIXMessageFactory()
    {
        return getFIXVersion().getMessageFactory();
    }

    /**
     * Returns the receiver's FIX data dictionary.
     *
     * @return The dictionary.
     */

    public synchronized FIXDataDictionary getFIXDataDictionary()
    {
        if (mDataDictionary==null) {
            mDataDictionary=new FIXDataDictionary(getDataDictionary());
        }
        return mDataDictionary;
    }

    /**
     * Returns the receiver's FIX message augmentor.
     *
     * @return The augmentor.
     */

    public FIXMessageAugmentor getFIXMessageAugmentor()
    {
        return getFIXMessageFactory().getMsgAugmentor();
    }

    /**
     * Sets the receiver's logon flag to the given value. This method
     * is synchronized to ensure that all threads will see the most
     * up-to-date value for the flag.
     *
     * @param loggedOn The flag.
     */

    public synchronized void setLoggedOn
        (boolean loggedOn)
    {
        mLoggedOn=loggedOn;
    }

    /**
     * Returns the receiver's logon flag. This method is synchronized
     * to ensure that all threads will see the most up-to-date value
     * for the flag.
     *
     * @return The flag.
     */

    public synchronized boolean getLoggedOn()
    {
        return mLoggedOn;
    }

    /**
     * Logs the given message, analyzed using the receiver's data
     * dictionary, at the debugging level.
     *
     * @param msg The message.
     */

    public void logMessage
        (Message msg)
    {
        Object category=(FIXMessageUtil.isHeartbeat(msg)?
                         HEARTBEAT_CATEGORY:this);
        if (SLF4JLoggerProxy.isDebugEnabled(category)) {
            Messages.ANALYZED_MESSAGE.debug
                (category,
                 new AnalyzedMessage(getDataDictionary(),msg).toString());
        }        
    }


    // Object.

    public String toString()
    {
        return Messages.BROKER_STRING.getText
            (getBrokerID().getValue(),getName(),getSessionID());
    }
}
