package org.marketcetera.jms;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.MessageKey;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Creates a connection to a JMS queue.
 * Has hooks for setting up either a queue or a topic listener
 * Optionally drains the queue before connecting to it.
 * @author gmiller
 * $Id$
 */
@ClassVersion("$Id$")
public class JMSAdapter {
    private Map<String, QueueSession>    mOutgoingQueueSessions;
    private Map<String, Queue>           mOutgoingQueues;
    private Map<String, QueueSender>     mOutgoingQueueSenders;

    private Map<String, QueueSession>    mIncomingQueueSessions;
    private Map<String, Queue>           mIncomingQueues;
    private Map<String, QueueReceiver>   mIncomingQueueReceivers;

    private Map<String, TopicSession>    mOutgoingTopicSessions;
    private Map<String, Topic>           mOutgoingTopics;
    private Map<String, TopicPublisher>  mOutgoingTopicPublishers;

    private Map<String, TopicSession>    mIncomingTopicSessions;
    private Map<String, Topic>           mIncomingTopics;
    private Map<String, TopicSubscriber> mIncomingTopicSubscribers;

    private Map<String,Connection> mConnections = new HashMap<String,Connection>();

    private Context mJNDIContext;
    private String mInitialContextFactoryName;
    private String mProviderURL;
    private String mConnectionFactoryName;
    private boolean mExplicitlyCreateDestinations;
    private static final String LOGGER_CAT = JMSAdapter.class.getName();

    /** Creates a new instance of JMSAdapter */
    //com.ubermq.jms.client.JMSInitialContextFactory
    //ubermq://127.0.0.1
    //connectionFactory
    public JMSAdapter(String initialContextFactoryName, String providerURL,
                      String connectionFactoryName) {
        this(initialContextFactoryName, providerURL, connectionFactoryName, false);
    }

    public JMSAdapter(String initialContextFactoryName, String providerURL,
                      String connectionFactoryName, boolean explicitDestinationCreation) {
        mOutgoingQueueSessions = new HashMap<String, QueueSession>();
        mOutgoingQueues = new HashMap<String, Queue>();
        mOutgoingQueueSenders = new HashMap<String, QueueSender>();

        mIncomingQueueSessions = new HashMap<String, QueueSession>();
        mIncomingQueues = new HashMap<String, Queue>();
        mIncomingQueueReceivers = new HashMap<String, QueueReceiver>();

        mOutgoingTopicSessions = new HashMap<String, TopicSession>();
        mOutgoingTopics = new HashMap<String, Topic>();
        mOutgoingTopicPublishers = new HashMap<String, TopicPublisher>();

        mIncomingTopicSessions = new HashMap<String, TopicSession>();
        mIncomingTopics = new HashMap<String, Topic>();
        mIncomingTopicSubscribers = new HashMap<String, TopicSubscriber>();

        mInitialContextFactoryName = initialContextFactoryName;
        mProviderURL = providerURL;
        mConnectionFactoryName = connectionFactoryName;

        mExplicitlyCreateDestinations = explicitDestinationCreation;
        LoggerAdapter.debug("creating a JMSAdapter for url " + mProviderURL, this);
    }

    public Queue getIncomingQueue(String name) { return mIncomingQueues.get(name); }
    public QueueSession getIncomingQueueSession(String name) { return mIncomingQueueSessions.get(name); }
    public QueueReceiver getIncomingQueueReceiver(String name) { return mIncomingQueueReceivers.get(name); }
    public Queue getOutgoingQueue(String name) { return mOutgoingQueues.get(name); }
    public QueueSession getOutgoingQueueSession(String name) { return mOutgoingQueueSessions.get(name); }
    public QueueSender getOutgoingQueueSender(String name) { return mOutgoingQueueSenders.get(name); }

    public Topic getIncomingTopic(String name) { return mIncomingTopics.get(name); }
    public TopicSession getIncomingTopicSession(String name) { return mIncomingTopicSessions.get(name); }
    public TopicSubscriber getIncomingTopicSubscriber(String name) { return mIncomingTopicSubscribers.get(name); }
    public Topic getOutgoingTopic(String name) { return mOutgoingTopics.get(name); }
    public TopicSession getOutgoingTopicSession(String name) { return mOutgoingTopicSessions.get(name); }
    public TopicPublisher getOutgoingTopicPublisher(String name) { return mOutgoingTopicPublishers.get(name); }

    /** Connect to the incoming queue name, clearing the queueu contents */
    public void connectIncomingQueue(String name, String queueName, int sessionOptions) throws JMSException{
        connectIncomingQueue(name, queueName, sessionOptions, true);
    }

    /** Connnects to the named queue
     * @param name  Type name that distinguishes this queue - usually an enum
     * @param queueName The name of the JMS queue
     * @param sessionOptions    JMS session options (auto-acknowledge, etc)
     * @param clearQueue    Flag on whether or not to drain the queueu of pending messages
     * before setting up a connection
     * @throws JMSException
     */
    public void connectIncomingQueue(String name, String queueName, int sessionOptions, boolean clearQueue) throws JMSException{
        if (mJNDIContext == null) {
            mJNDIContext = getJNDIContext(mInitialContextFactoryName, mProviderURL);
        }
        if (clearQueue){
            try {
                QueueConnectionFactory queueConnectionFactory =
                        (QueueConnectionFactory)mJNDIContext.lookup(mConnectionFactoryName);

                drainQueue(queueConnectionFactory, queueName);
            } catch (Exception ex) {
                LoggerAdapter.error(MessageKey.JMS_CLEAR_ERROR.getLocalizedMessage(queueName), ex, this);
            }
        }

        getQueueHelper(name, queueName, mIncomingQueues, mIncomingQueueSessions, sessionOptions);
        QueueSession session = mIncomingQueueSessions.get(name);
        Queue queue = mIncomingQueues.get(name);
        QueueReceiver receiver = session.createReceiver(queue);
        mIncomingQueueReceivers.put(name, receiver);
        if(LoggerAdapter.isDebugEnabled(this)) {LoggerAdapter.debug("connecting incoming queue "+queueName, this);}
    }

    /** Creates a browser to count the # of outstanding messages on the Queue and
     * synchronously waits (and discards) all the messages
     * @param inFactory    The connection factory from which to get a connection to the queue
     * @param queueName
     * @throws JMSException
     */
    public static void drainQueue(QueueConnectionFactory inFactory, String queueName) throws JMSException
    {
        int count = 0;

        QueueConnection inConn = inFactory.createQueueConnection();
        QueueSession sess = inConn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue aQueue = sess.createQueue(queueName);
        QueueReceiver receiver = sess.createReceiver(aQueue);
        inConn.start();

        while(receiver.receive(100) != null) {
            if(LoggerAdapter.isDebugEnabled(LOGGER_CAT)) {
                LoggerAdapter.debug("Drained a message off the queue: "+count++, LOGGER_CAT);
            }
        }

        receiver.close();
        sess.close();
        inConn.stop();
        if(LoggerAdapter.isDebugEnabled(LOGGER_CAT)) { LoggerAdapter.debug("Finished attempt to drain "+queueName, LOGGER_CAT); }
    }

    public void connectOutgoingQueue(String name, String queueName, int sessionOptions) throws JMSException{
        if (mJNDIContext == null) {
            mJNDIContext = getJNDIContext(mInitialContextFactoryName, mProviderURL);
        }
        getQueueHelper(name, queueName, mOutgoingQueues, mOutgoingQueueSessions, sessionOptions);
        QueueSession session = mOutgoingQueueSessions.get(name);
        Queue queue = mOutgoingQueues.get(name);
        mOutgoingQueueSenders.put(name, session.createSender(queue));
        if(LoggerAdapter.isDebugEnabled(this)) {LoggerAdapter.debug("connecting outgoing queue "+queueName, this);}
    }

    public void connectIncomingTopic(String name, String topicName, int sessionOptions) throws JMSException{
        if (mJNDIContext == null) {
            mJNDIContext = getJNDIContext(mInitialContextFactoryName, mProviderURL);
        }
        getTopicHelper(name, topicName, mIncomingTopics, mIncomingTopicSessions, sessionOptions);
        TopicSession session = mIncomingTopicSessions.get(name);
        Topic topic = mIncomingTopics.get(name);
        mIncomingTopicSubscribers.put(name, session.createSubscriber(topic));
        if(LoggerAdapter.isDebugEnabled(this)) {LoggerAdapter.debug("adding a listener for topic "+topic, this);}
    }

    public void connectOutgoingTopic(String name, String topicName, int sessionOptions) throws JMSException{
        if (mJNDIContext == null) {
            mJNDIContext = getJNDIContext(mInitialContextFactoryName, mProviderURL);

        }
        getTopicHelper(name, topicName, mOutgoingTopics, mOutgoingTopicSessions, sessionOptions);
        TopicSession session = mOutgoingTopicSessions.get(name);
        Topic topic = mOutgoingTopics.get(name);
        mOutgoingTopicPublishers.put(name, session.createPublisher(topic));
        if(LoggerAdapter.isDebugEnabled(this)) {LoggerAdapter.debug("connecting outgoing topic "+topicName, this);}
    }


    protected void getQueueHelper(String name, String queueName, Map<String, Queue> queueMap, Map<String,
            QueueSession> sessionMap, int sessionOptions) throws JMSException {
        QueueConnectionFactory  queueConnectionFactory = null;
        QueueConnection         queueConnection = null;

        /*
        * Look up connection factory and queue.  If either does
        * not exist, exit.
        */
        QueueSession sess = null;
        try {
            queueConnectionFactory =
                    (QueueConnectionFactory)mJNDIContext.lookup(mConnectionFactoryName);
            queueConnection = queueConnectionFactory.createQueueConnection();
            queueConnection.start();
            sessionMap.put(name, sess = queueConnection.createQueueSession(false, sessionOptions));
            if (mExplicitlyCreateDestinations){
                queueMap.put(name, sess.createQueue(queueName));
            } else {
                queueMap.put(name, (Queue) mJNDIContext.lookup(queueName));
            }
            mConnections.put(name, queueConnection);
        } catch (NamingException e) {
            if (queueConnectionFactory != null)
            {
                queueMap.put(name, sess.createQueue(queueName));
            } else {
                JMSException exception = new JMSException(MessageKey.JMS_QUEUE_DNE.getLocalizedMessage(queueName));
                exception.setLinkedException(e);
                throw exception;
            }
        } catch (JMSException e) {
            JMSException exception = new JMSException(MessageKey.ERROR_WITH_DETAILS.getLocalizedMessage(e.getMessage()));
            exception.setLinkedException(e);
            throw exception;
        }

    }

    protected void getTopicHelper(String name, String topicName, Map<String, Topic> topicMap, Map<String,
            TopicSession> sessionMap, int sessionOptions) throws JMSException {
        TopicConnectionFactory  topicConnectionFactory = null;
        TopicConnection topicConnection = null;

        TopicSession sess = null;
        try {
            topicConnectionFactory =
                    (TopicConnectionFactory)mJNDIContext.lookup(mConnectionFactoryName);
            topicConnection = topicConnectionFactory.createTopicConnection();
            topicConnection.start();
            sessionMap.put(name, sess = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE));
            if (mExplicitlyCreateDestinations){
                topicMap.put(name, sess.createTopic(topicName));
            } else {
                topicMap.put(name, (Topic) mJNDIContext.lookup(topicName));
            }
            mConnections.put(name, topicConnection);
        } catch (NamingException e) {
            if (topicConnectionFactory != null)
            {
                topicMap.put(name, sess.createTopic(topicName));
            } else {
                JMSException exception = new JMSException(MessageKey.JMS_TOPIC_DNE.getLocalizedMessage(topicName));
                exception.setLinkedException(e);
                throw exception;
            }
        } catch (JMSException e) {
            JMSException exception = new JMSException(MessageKey.ERROR_WITH_DETAILS.getLocalizedMessage(e.getMessage()));
            exception.setLinkedException(e);
            throw exception;
        }
    }

    @SuppressWarnings( { "unchecked" })
    protected Context getJNDIContext(String initialContextFactoryName, String providerURL) throws JMSException {
        Context jndiContext = null;
        Hashtable env = new Hashtable();
        env.put(InitialContext.INITIAL_CONTEXT_FACTORY,
                initialContextFactoryName);
        env.put(InitialContext.PROVIDER_URL, providerURL);
        try {
            jndiContext = new InitialContext(env);
        } catch (NamingException e) {
            JMSException jmsEx = new JMSException(MessageKey.ERROR_JNDI_CREATE.getLocalizedMessage(e.getMessage()));
            LoggerAdapter.error(MessageKey.ERROR_JNDI_CREATE.getLocalizedMessage(), e, this);
            jmsEx.setLinkedException(e);
            throw jmsEx;
        }
        return jndiContext;
    }

    public void start() {
        for (Connection aConnection : mConnections.values()) {
            try {
                aConnection.start();
                LoggerAdapter.debug("Adapter for "+mProviderURL + " started for client "+aConnection.getClientID(), this);
            } catch (JMSException ex) {
                LoggerAdapter.error(MessageKey.JMS_CONNECTION_START_ERROR.getLocalizedMessage(), ex, this);
            }
        }
    }

    public void shutdown() {
        closeSessions(mIncomingQueueSessions.values());
        closeSessions(mOutgoingQueueSessions.values());
        closeSessions(mIncomingTopicSessions.values());
        closeSessions(mOutgoingTopicSessions.values());

        closeConsumers(mIncomingQueueReceivers.values());
        closeConsumers(mIncomingTopicSubscribers.values());
        closeProducers(mOutgoingQueueSenders.values());
        closeProducers(mOutgoingTopicPublishers.values());

        closeConnections(mConnections.values());
        try {
            mJNDIContext.close();
        } catch(NamingException ex) {
            LoggerAdapter.error(MessageKey.ERROR_JNDI_CLOSE.getLocalizedMessage(), ex, this);
        }
    }

    private <String extends Session> void closeSessions(Collection<String> sessions) {
        for (Session aSession : sessions) {
            try {
                aSession.close();
            } catch (JMSException ex) {
                LoggerAdapter.error(MessageKey.JMS_CONNECTION_CLOSE_ERROR.getLocalizedMessage(), ex, this);
            }
        }
    }

    private <String extends MessageProducer> void closeProducers(Collection<String> producers) {
        for (MessageProducer aProducer : producers) {
            try {
                aProducer.close();
            } catch (JMSException ex) {
                LoggerAdapter.error(MessageKey.JMS_CONNECTION_CLOSE_ERROR.getLocalizedMessage(), ex, this);
            }
        }
    }

    private <String extends MessageConsumer> void closeConsumers(Collection<String> consumers) {
        for (MessageConsumer aConsumer : consumers) {
            try {
                aConsumer.close();
            } catch (JMSException ex) {
                LoggerAdapter.error(MessageKey.JMS_CONNECTION_CLOSE_ERROR.getLocalizedMessage(), ex, this);
            }
        }
    }

    private void closeConnections(Collection<Connection> connections) {
        for (Connection aConnection : connections) {
            try {
                aConnection.close();
            } catch (JMSException ex) {
                LoggerAdapter.error(MessageKey.JMS_CONNECTION_CLOSE_ERROR.getLocalizedMessage(), ex, this);
            }
        }
    }


}
