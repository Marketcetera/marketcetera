package org.marketcetera.jcyclone;

import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * Small struct to hold the information necessary for the
 * {@link JMSStageOutput} to know how to send the data to JMS

 * @author Toli Kuznets
 * @version $Id$
 */
public class JMSOutputInfo {
    private MessageProducer producer;
    private Session session;
    private String jmsName;

    public JMSOutputInfo(MessageProducer inProducer, Session inSession, String name) {
        producer = inProducer;
        session = inSession;
        jmsName = name;
    }

    public MessageProducer getMessageProducer() {
        return producer;
    }

    public Session getSession() {
        return session;
    }

    /** Get the name of the queue or topic we are outputting to */
    public String getJmsName() {
        return jmsName;
    }
}
