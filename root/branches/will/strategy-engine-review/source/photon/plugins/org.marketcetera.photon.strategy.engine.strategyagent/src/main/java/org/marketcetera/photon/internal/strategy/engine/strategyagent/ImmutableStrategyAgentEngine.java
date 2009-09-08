package org.marketcetera.photon.internal.strategy.engine.strategyagent;

import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection;
import org.marketcetera.photon.strategy.engine.model.strategyagent.StrategyAgentEngine;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * An immutable version of {@link StrategyAgentEngine}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ImmutableStrategyAgentEngine {

    private final StrategyEngineConnection mConnection;
    private final ConnectionState mConnectionState;
    private final String mName;
    private final String mDescription;
    private final String mJmsUrl;
    private final String mWebServiceHostname;
    private final Integer mWebServicePort;

    /**
     * Constructor.
     * 
     * @param engine
     *            the mutable {@link StrategyAgentEngine} from which to
     *            initialize the object
     */
    public ImmutableStrategyAgentEngine(StrategyAgentEngine engine) {
        mConnection = engine.getConnection();
        mConnectionState = engine.getConnectionState();
        mName = engine.getName();
        mDescription = engine.getDescription();
        mJmsUrl = engine.getJmsUrl();
        mWebServiceHostname = engine.getWebServiceHostname();
        mWebServicePort = engine.getWebServicePort();
    }

    /**
     * Returns the connection.
     * 
     * @return the connection
     */
    public StrategyEngineConnection getConnection() {
        return mConnection;
    }

    /**
     * Returns the connection state.
     * 
     * @return the connection state
     */
    public ConnectionState getConnectionState() {
        return mConnectionState;
    }

    /**
     * Returns the name.
     * 
     * @return the name
     */
    public String getName() {
        return mName;
    }

    /**
     * Returns the description.
     * 
     * @return the description
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Returns the JMS URL.
     * 
     * @return the JMS URL
     */
    public String getJmsUrl() {
        return mJmsUrl;
    }

    /**
     * Returns the web service hostname.
     * 
     * @return the web service hostname
     */
    public String getWebServiceHostname() {
        return mWebServiceHostname;
    }

    /**
     * Returns the web service port.
     * 
     * @return the web service port
     */
    public Integer getWebServicePort() {
        return mWebServicePort;
    }

}
