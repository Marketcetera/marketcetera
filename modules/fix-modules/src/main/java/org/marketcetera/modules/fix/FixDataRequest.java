package org.marketcetera.modules.fix;

import java.util.Set;

import com.google.common.collect.Sets;

import quickfix.SessionID;

/* $License$ */

/**
 * Indicates the desired contents of a FIX data flow for one or more FIX sessions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixDataRequest
{
    /**
     * Get the includeAdmin value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getIncludeAdmin()
    {
        return includeAdmin;
    }
    /**
     * Sets the includeAdmin value.
     *
     * @param inIncludeAdmin a <code>boolean</code> value
     */
    public void setIncludeAdmin(boolean inIncludeAdmin)
    {
        includeAdmin = inIncludeAdmin;
    }
    /**
     * Get the includeApp value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getIncludeApp()
    {
        return includeApp;
    }
    /**
     * Sets the includeApp value.
     *
     * @param inIncludeApp a <code>boolean</code> value
     */
    public void setIncludeApp(boolean inIncludeApp)
    {
        includeApp = inIncludeApp;
    }
    /**
     * Get the requestedSessionIds value.
     *
     * @return a <code>Set&lt;SessionID&gt;</code> value
     */
    public Set<SessionID> getRequestedSessionIds()
    {
        return requestedSessionIds;
    }
    /**
     * Get the messageWhiteList value.
     *
     * @return a <code>Set&lt;String&gt;</code> value
     */
    public Set<String> getMessageWhiteList()
    {
        return messageWhiteList;
    }
    /**
     * Get the messageBlackList value.
     *
     * @return a <code>Set&lt;String&gt;</code> value
     */
    public Set<String> getMessageBlackList()
    {
        return messageBlackList;
    }
    /**
     * session ids 
     */
    private final Set<SessionID> requestedSessionIds = Sets.newHashSet();
    /**
     * indicates whether this data flow request should include admin messages
     */
    private boolean includeAdmin = false;
    /**
     * indicates whether this data flow request should include app messages
     */
    private boolean includeApp = true;
    /**
     * indicates the set of message types explicitly included in the data flow
     */
    private final Set<String> messageWhiteList = Sets.newHashSet();
    /**
     * indicates the set of message types explicitly excluded in the data flow
     */
    private final Set<String> messageBlackList = Sets.newHashSet(quickfix.field.MsgType.LOGON,quickfix.field.MsgType.HEARTBEAT,quickfix.field.MsgType.TEST_REQUEST,quickfix.field.MsgType.RESEND_REQUEST,quickfix.field.MsgType.LOGOUT);
}
