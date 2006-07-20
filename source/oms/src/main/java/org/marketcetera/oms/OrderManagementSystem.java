package org.marketcetera.oms;

import java.io.FileNotFoundException;
import java.util.Properties;

import javax.jms.JMSException;

import org.marketcetera.core.*;
import org.marketcetera.jcyclone.JMSOutputInfo;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.QuickFIXInitiator;
import quickfix.SessionID;


/**
 * OrderManagementSystem
 * Main entrypoint for sending orders and receiving responses from a FIX engine
 *
 * The OMS consists of the following JCyclone configuration:
 *
 * FIXSessionAdapterSource  ------|
 *                                |--> OrderManager ---> OutputStage
 * JMSAdapterSource         ------|
 *
 * @author gmiller
 * $Id$
 */
@ClassVersion("$Id$")
public class OrderManagementSystem extends ApplicationBase implements OrderManagementSystemMBean {

    private static final String LOGGER_NAME = OrderManagementSystem.class.getName();
    protected static final String CONFIG_FILE_NAME = "oms";

    protected static OrderManagementSystem sOMS = null;
    protected ConfigData mainProps;
    private JMSOutputInfo jmsOutputInfo = null;        // used to store the info for OrderManager to find
    private SessionID defaultSessionID;         // used to store the SessionID so that FIX sender can find it 

    enum JMSPorts { INCOMING_COMMANDS, OUTGOING_MESSAGES }

    protected OrderManagementSystem(String inCfgFile) throws ConfigFileLoadingException {
        super(inCfgFile);
    }

    public void init() throws Exception {
        super.init();
        Properties props = ConfigPropertiesLoader.loadProperties(mCfgFileName);
        mainProps = new PropertiesConfigData(props);
        FIXDataDictionaryManager.setFIXVersion(QuickFIXInitiator.FIX_VERSION_DEFAULT);
    }

    public static OrderManagementSystem createOMS(String cfgFile) throws ConfigFileLoadingException {
        if(sOMS == null){
            sOMS = new OrderManagementSystem(cfgFile);
        }
        return sOMS;
    }

    public static void main(String [] args) throws ConfigFileLoadingException
    {
        String configFile = CONFIG_FILE_NAME;
        if(args.length == 1) {
            configFile = args[0];
        }
        sOMS = createOMS(configFile);

        try {
            sOMS.init();
            LoggerAdapter.info("Starting.", LOGGER_NAME);
            sOMS.run();
            Thread.currentThread().join();
        } catch (JMSException jmse) {
            LoggerAdapter.error("JMS error", jmse, LOGGER_NAME);
        } catch (quickfix.ConfigError ce) {
            LoggerAdapter.error("Config error", ce, LOGGER_NAME);
        } catch (FileNotFoundException fnf) {
            LoggerAdapter.error("Config file not found: ", fnf, LOGGER_NAME);
        } catch (ClassNotFoundException cnfe) {
            LoggerAdapter.error("Class not found ", cnfe, LOGGER_NAME);
        } catch (Exception ex) {
            LoggerAdapter.error("Error", ex, LOGGER_NAME);
        } finally {
            LoggerAdapter.info("Exiting application.", LOGGER_NAME);
        }
    }

    public static OrderManagementSystem getOMS() { return sOMS; }

    public ConfigData getInitProps() { return mainProps; }

    public void registerOutgoingJMSInfo(JMSOutputInfo inJMSOutputInfo)
    {
        jmsOutputInfo = inJMSOutputInfo;
    }

    /** Sets the default session that's actually created in {@link QuickFIXInitiator} */
    public void registerDefaultSessionID(SessionID inSessionID)
    {
        defaultSessionID = inSessionID;
    }

    public JMSOutputInfo getJmsOutputInfo() {
        return jmsOutputInfo;
    }

    public SessionID getDefaultSessionID() {
        return defaultSessionID;
    }
}

