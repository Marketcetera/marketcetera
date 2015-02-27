package org.marketcetera.modules.remote.receiver;

import java.util.EnumSet;

import org.marketcetera.event.LogEvent;
import org.marketcetera.event.LogEventLevel;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataReceiver;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ReceiveDataException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.spring.SpringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;

/* $License$ */
/**
 * A module that can receive any kind of data and emit it on a
 * remotely accessible messaging topic so that the data can be received
 * by {@link org.marketcetera.modules.remote.receiver remote recipients}.
 * <br/> 
 * <table>
 * <tr><th>Capabilities</th><td>Data Receiver</td></tr>
 * <tr><th>Stops data flows</th><td>No</td></tr>
 * <tr><th>Emits data flow errors</th><td>Yes, when it's unable to
 * serialize or send the object over JMS</td></tr>
 * <tr><th>Start Operation</th><td>Starts the embedded JMS broker and
 * connects to it.</td></tr>
 * <tr><th>Stop Operation</th><td>Stops the embedded broker.</td></tr>
 * <tr><th>Management Interface</th><td>{@link ReceiverModuleMXBean}</td></tr>
 * <tr><th>Factory</th><td>{@link ReceiverFactory}</td></tr>
 * </table>
 * <br/>
 * <b>Remoting Mechanism</b>
 * <p>
 * The module embeds a JMS message broker to transmit the messages to its
 * clients. The message broker is configured with a single topic on which
 * all the messages are broadcast.
 * The broker is started when the module is started and stopped when
 * the module is stopped.
 * <br/>
 * <b>Received Object Handling</b>
 * <p>
 * The module will transmit all the received objects serialized as
 * {@link javax.jms.ObjectMessage}. If the received object is not
 * serializable, the module generates an error receiving that object
 * which is handled and logged by the module framework. The module
 * ignores null objects.
 * <br/>
 * <b>Authentication</b>
 * <p>
 * The remote clients of this module need to authenticate themselves to
 * the message broker in order to connect. This module depends on the
 * {@link org.marketcetera.client client} module for authentication.
 * The client module needs to be connected to the server for the
 * authentication to succeed and the credentials supplied must be the same
 * as used by the client to connect to the server.
 * <p>
 * Do note that this module programmatically sets up the JAAS
 * {@link javax.security.auth.login.Configuration} to make it easy to use.
 * However, such a setup may conflict with other JAAS clients within the same
 * JVM. If that happens, you can set the variable
 * {@link #setSkipJAASConfiguration(boolean)} to false and make sure that
 * you setup the JAAS configuration yourself so that this module can work.
 * Here's the JAAS configuration needed for authentication to work.
 * <pre>
 * remoting-amq-domain {
 *    org.marketcetera.modulews.remote.receiver.ClientLoginModule required;
 * };
 * </pre>
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class ReceiverModule extends Module
        implements DataReceiver, ReceiverModuleMXBean {
    /**
     * Creates an instance.
     *
     * @param inURN the module URN.
     */
    ReceiverModule(ModuleURN inURN) {
        super(inURN, true);
        //Set the log level to the default value
        setLogLevel(LogEventLevel.WARN);
    }

    @Override
    protected void preStart() throws ModuleException {
        //Check if the broker URL is supplied
        String url = getURL();
        if(url == null || url.trim().isEmpty()) {
            //If no URL specified do not perform remoting.
            Messages.NO_URL_SPECIFIED_LOG.info(this);
            return;
        }
        if (!mSkipJAASConfiguration) {
            //Setup the JAAS configuration
            JaasConfiguration.setup();
            mDoneJaasConfiguration = true;
        }
        //Create spring contexts to initialize the broker and messaging topic
        try {
            StaticApplicationContext parent = new StaticApplicationContext();
            SpringUtils.addStringBean(parent, "brokerURI", url);  //$NON-NLS-1$
            SpringUtils.addStringBean(parent, "userName",  //$NON-NLS-1$ 
                    ClientLoginHelper.getUserName());
            SpringUtils.addStringBean(parent, "password",  //$NON-NLS-1$
                    ClientLoginHelper.getPassword());
            parent.refresh();
            mContext  =
                    new ClassPathXmlApplicationContext(new String[]{
                            "remoting_server.xml"}, parent);  //$NON-NLS-1$
            mContext.start();
            mSender = (JmsTemplate) mContext.getBean("sender",  //$NON-NLS-1$
                    JmsTemplate.class);
            Messages.RECIEVER_REMOTING_CONFIGURED.info(this, url);
        } catch(Exception e) {
            throw new ModuleException(e, Messages.ERROR_STARTING_MODULE);
        }
    }

    @Override
    protected void preStop() throws ModuleException {
        //Stop & destroy the broker.
        if (mContext != null) {
            try {
                mContext.close();
                mContext = null;
            } catch (Exception e) {
                Messages.ERROR_STOPPING_MODULE_LOG.warn(this, e);
            }
        }
        mSender = null;
    }

    @Override
    public void receiveData(DataFlowID inFlowID, Object inData)
            throws ReceiveDataException {
        if (mSender != null && inData != null) {
            if(inData instanceof LogEvent) {
                //Skip the log event if its level is below the current log level
                if(((LogEvent)inData).getLevel().ordinal()
                        < mLogLevel.ordinal()) {
                    return;
                }
            }
            try {
                mSender.convertAndSend(inData);
            } catch (JmsException e) {
                throw new ReceiveDataException(e,
                        new I18NBoundMessage1P(Messages.ERROR_WHEN_TRANSMITTING,
                                String.valueOf(inData)));
            }
        }
    }

    @Override
    public String getURL() {
        return mURL;
    }

    @Override
    public void setURL(String inURL) {
        failIfStarted(Messages.ILLEGAL_STATE_SET_URL);
        mURL = inURL;
    }

    @Override
    public LogEventLevel getLogLevel() {
        return mLogLevel;
    }

    @Override
    public void setLogLevel(LogEventLevel inLevel) {
        if(inLevel == null) {
            throw new IllegalArgumentException(Messages.NULL_LEVEL_VALUE.getText(EnumSet.allOf(LogEventLevel.class)));
        }
        mLogLevel = inLevel;
    }

    @Override
    public boolean isSkipJAASConfiguration() {
        return mSkipJAASConfiguration;
    }

    @Override
    public void setSkipJAASConfiguration(boolean inSkipJAASConfiguration) {
        if(mDoneJaasConfiguration) {
            throw new IllegalArgumentException(
                    Messages.ILLEGAL_STATE_SET_SKIP_JAAS.getText());
        }
        mSkipJAASConfiguration = inSkipJAASConfiguration;
    }
    /**
     * Verifies if the module is not started.
     *
     * @param inMessage the message to use when the module is started.
     *
     * @throws IllegalStateException if the module is started.
     */
    private void failIfStarted(I18NMessage0P inMessage) {
        if(getState().isStarted()) {
            throw new IllegalStateException(inMessage.getText());
        }
    }

    private volatile String mURL;
    private volatile ClassPathXmlApplicationContext mContext;
    private volatile JmsTemplate mSender;
    private volatile LogEventLevel mLogLevel;
    private volatile boolean mSkipJAASConfiguration = false;
    private volatile boolean mDoneJaasConfiguration;
}
