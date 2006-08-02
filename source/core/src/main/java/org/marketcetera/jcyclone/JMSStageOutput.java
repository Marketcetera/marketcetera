package org.marketcetera.jcyclone;

import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.ClassVersion;

import javax.jms.JMSException;

/**
 * Wrapper on a JMS message that needs to travel through the JCyclone pipe
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class JMSStageOutput extends OutputElement{
    JMSOutputInfo jmsOutputInfo;

    public JMSStageOutput(quickfix.Message msg, JMSOutputInfo inJMSOutputInfo) {
        super(msg);
        jmsOutputInfo = inJMSOutputInfo;
    }

    public void output() throws MarketceteraException {
        quickfix.Message qfMessage = (quickfix.Message)getElement();
        try {
            javax.jms.Message jmsMessage = jmsOutputInfo.getSession().createTextMessage(qfMessage.toString());
            if(LoggerAdapter.isDebugEnabled(this)) {
                LoggerAdapter.debug("JMS output<"+jmsOutputInfo.getJmsName()+">: "+jmsMessage, this); 
            }
            jmsOutputInfo.getMessageProducer().send(jmsMessage);
        } catch (JMSException ex) {
            LoggerAdapter.error("failed sending message "+qfMessage, ex, this);
            throw new MarketceteraException(ex);
        }
    }
}
