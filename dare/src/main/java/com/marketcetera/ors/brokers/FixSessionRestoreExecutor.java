package com.marketcetera.ors.brokers;

import com.marketcetera.fix.SessionRestorePayload;
import com.marketcetera.fix.SessionRestorePayloadHandler;

import quickfix.SessionID;

/* $License$ */

/**
 * Restores FixSessions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FixSessionRestoreExecutor<T1 extends SessionRestorePayload,T2 extends SessionRestorePayload>
{
    /**
     * Indicates a session create event.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @param inMessageHandler
     */
    void sessionCreate(SessionID inSessionId,
                       SessionRestorePayloadHandler<T1> inMessageHandler);
    /**
     * Indicates a session logon event.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @param inMessageHandler a <code>SessionRestorePayloadHandler&lt;T2&gt;</code> value
     */
    void sessionLogon(SessionID inSessionId,
                      SessionRestorePayloadHandler<T2> inMessageHandler);
    /**
     * Indicate a session logout event.
     *
     * @param inSessionId a <code>SessionID</code> value
     */
    void sessionLogout(SessionID inSessionId);
    /**
     * Indicates if the given session has been created.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @return a <code>boolean</code> value
     */
    boolean isSessionCreated(SessionID inSessionId);
    /**
     * Indicates if the given session has been logged on.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @return a <code>boolean</code> value
     */
    boolean isSessionLoggedOn(SessionID inSessionId);
}
