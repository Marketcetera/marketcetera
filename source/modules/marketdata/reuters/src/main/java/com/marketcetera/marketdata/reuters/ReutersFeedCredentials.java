package com.marketcetera.marketdata.reuters;

import org.marketcetera.marketdata.AbstractMarketDataFeedCredentials;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import com.reuters.utility.ObjectId;
import com.reuters.utility.config.ConfigDb;
import com.reuters.utility.config.ConfigVariable;

/* $License$ */

/**
 * Credentials required to access Reuters data.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ReutersFeedCredentials.java 82351 2012-05-04 21:46:58Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: ReutersFeedCredentials.java 82351 2012-05-04 21:46:58Z colin $")
public class ReutersFeedCredentials
        extends AbstractMarketDataFeedCredentials
        implements ConfigDb
{
    /**
     * Create a new ReutersFeedCredentials instance.
     * 
     * @param inServerType a <code>String</code> value
     * @param inServerList a <code>String</code> value
     * @param inPortNumber an <code>int</code> value
     * @param inUsername a <code>String</code> value
     */
    public ReutersFeedCredentials(String inServerType,
                                  String inServerList,
                                  int inPortNumber,
                                  String inUsername)
    {
        serverType = inServerType;
        serverList = inServerList;
        portNumber = inPortNumber;
        username = inUsername;
    }
    /* (non-Javadoc)
     * @see com.reuters.utility.config.ConfigDb#has(com.reuters.utility.ObjectId, java.lang.String)
     */
    @Override
    public boolean has(ObjectId inObjectId,
                       String inVariableName)
    {
        SLF4JLoggerProxy.debug(ReutersFeedCredentials.class,
                               "Does {} have {}?",
                               inObjectId,
                               inVariableName);
        return false; // TODO
    }
    /* (non-Javadoc)
     * @see com.reuters.utility.config.ConfigDb#variable(com.reuters.utility.ObjectId, java.lang.String)
     */
    @Override
    public ConfigVariable variable(ObjectId inObjectId,
                                   String inVariableName)
    {
        ConfigVariable response = resolveConfigVariable(inVariableName,
                                                        null);
        SLF4JLoggerProxy.debug(ReutersFeedCredentials.class,
                               "Returning {} in reponse to query for {} on {}",
                               response,
                               inVariableName,
                               inObjectId);
        return response;
    }
    /* (non-Javadoc)
     * @see com.reuters.utility.config.ConfigDb#variable(com.reuters.utility.ObjectId, java.lang.String, java.lang.String)
     */
    @Override
    public ConfigVariable variable(ObjectId inObjectId,
                                   String inVariableName,
                                   String inDefaultValue)
    {
        ConfigVariable response = resolveConfigVariable(inVariableName,
                                                        inDefaultValue);
        // TODO set response value
        SLF4JLoggerProxy.debug(ReutersFeedCredentials.class,
                               "Returning {} in reponse to query for {} with default {} on {}",
                               response,
                               inVariableName,
                               inDefaultValue,
                               inObjectId);
        return response;
    }
    /**
     * Get the serverType value.
     *
     * @return a <code>String</code> value
     */
    public String getServerType()
    {
        return serverType;
    }
    /**
     * Get the serverList value.
     *
     * @return a <code>String</code> value
     */
    public String getServerList()
    {
        return serverList;
    }
    /**
     * 
     *
     *
     * @param inVariableName
     * @param inDefaultValue
     * @return
     */
    private ConfigVariable resolveConfigVariable(String inVariableName,
                                                 String inDefaultValue)
    {
        if(inVariableName.equals("serverType")) {
            return new ConfigVariable(serverType);
        } else if(inVariableName.equals("traceSelector")) {
            return new ConfigVariable("info");
        } else if(inVariableName.equals("eventMechanism")) {
            return new ConfigVariable("poll");
        } else if(inVariableName.equals("transportProtocol")) {
            return new ConfigVariable("tcp");
        } else if(inVariableName.equals("serverList")) {
            return new ConfigVariable(serverList);
        } else if(inVariableName.equals("portNumber")) {
            return new ConfigVariable(portNumber);
        } else if(inVariableName.equals("username")) {
            return new ConfigVariable(username);
        } else if(inVariableName.equals("application")) {
            // TODO unknown
        } else if(inVariableName.equals("useJni")) {
            return new ConfigVariable("false");
        } else if(inVariableName.equals("dictFromFile")) {
            return new ConfigVariable("false");
        } else if(inVariableName.equals("mountTrace")) {
            return new ConfigVariable("false");
        } else if(inVariableName.equals("ipcTraceFlags")) {
            return new ConfigVariable(0);
        } else if(inVariableName.equals("connectionTimeout")) {
            return new ConfigVariable(10);
        } else if(inVariableName.equals("pingInterval")) {
            return new ConfigVariable(20);
        } else if(inVariableName.equals("connectionRetryInterval")) {
            return new ConfigVariable(5);
        } else if(inVariableName.equals("connectMaxRetryDelay")) {
            return new ConfigVariable(10);
        } else if(inVariableName.equals("mountVersion")) {
            return new ConfigVariable("MOUNT_VERSION_AUTO"); // TODO this might be defined somewhere)
        } else if(inVariableName.equals("masterFidFile")) {
            return new ConfigVariable("/var/triarch/appendix_a");
        } else if(inVariableName.equals("enumTypeFile")) {
            return new ConfigVariable("/var/triarch/enumtype.def");
        }
        return new ConfigVariable(inDefaultValue);
    }
    /**
     * server type value
     */
    private final String serverType;
    /**
     * 
     */
    private final String serverList;
    /**
     * 
     */
    private final int portNumber;
    /**
     * 
     */
    private final String username;
}
