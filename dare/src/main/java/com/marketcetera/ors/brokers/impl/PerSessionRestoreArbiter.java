package com.marketcetera.ors.brokers.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import quickfix.SessionID;

import com.marketcetera.fix.SessionRestoreArbiter;
import com.marketcetera.ors.brokers.BrokerService;

/* $License$ */

/**
 * Decides whether to enable session restore on a session-by-session basis.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
public class PerSessionRestoreArbiter
        implements SessionRestoreArbiter
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.SessionRestoreArbiter#shouldRestore(quickfix.SessionID)
     */
    @Override
    public boolean shouldRestore(SessionID inSessionId)
    {
        boolean result = defaultValue;
        if(enableRestore.containsKey(inSessionId)) {
            result = enableRestore.get(inSessionId);
        }
        SLF4JLoggerProxy.debug(FIXMessageUtil.FIX_RESTORE_LOGGER_NAME,
                               "Invoking restore arbiter {} on {}, returning {}",
                               getClass().getSimpleName(),
                               brokerService.getSessionName(inSessionId),
                               result);
        return result;
    }
    /**
     * Get the enableRestore value.
     *
     * @return a <code>Map&lt;SessionID,Boolean&gt;</code> value
     */
    public Map<SessionID,Boolean> getEnableRestore()
    {
        return enableRestore;
    }
    /**
     * Sets the enableRestore value.
     *
     * @param inEnableRestore a <code>Map&lt;SessionID,Boolean&gt;</code> value
     */
    public void setEnableRestore(Map<SessionID,Boolean> inEnableRestore)
    {
        enableRestore.clear();
        if(inEnableRestore != null) {
            enableRestore.putAll(inEnableRestore);
        }
    }
    /**
     * Get the defaultValue value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getDefaultValue()
    {
        return defaultValue;
    }
    /**
     * Sets the defaultValue value.
     *
     * @param inDefaultValue a <code>boolean</code> value
     */
    public void setDefaultValue(boolean inDefaultValue)
    {
        defaultValue = inDefaultValue;
    }
    /**
     * Get the brokerService value.
     *
     * @return a <code>BrokerService</code> value
     */
    public BrokerService getBrokerService()
    {
        return brokerService;
    }
    /**
     * Sets the brokerService value.
     *
     * @param inBrokerService a <code>BrokerService</code> value
     */
    public void setBrokerService(BrokerService inBrokerService)
    {
        brokerService = inBrokerService;
    }
    /**
     * provides access to broker services
     */
    @Autowired
    private BrokerService brokerService;
    /**
     * if a given session is not specified, return this value
     */
    private boolean defaultValue = true;
    /**
     * stores the flag settings by session id
     */
    private final Map<SessionID,Boolean> enableRestore = new HashMap<>();
}
