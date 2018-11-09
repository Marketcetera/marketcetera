package com.marketcetera.fix;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.marketcetera.matp.cluster.ClusterData;

import quickfix.SessionID;
import quickfix.SessionSettings;

/* $License$ */

/**
 * Provides services related to FIX sessions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface SessionService
{
    /**
     * Get the name of the given session.
     *
     * <b>This method is intended to be light-weight to be called frequently with minimal trips to the database.
     * As such, it is possible that the value returned may, under certain circumstances, be inaccurate due to caching.
     * This is unlikely to happen, but is possible due to the emphasis on performance.
     * 
     * @param inSessionId a <code>SessionID</code> value
     * @return a <code>String</code> value, never <code>null</code>
     */
    String getSessionName(SessionID inSessionId);
    /**
     * Generates a <code>SessionSettings<code> value based on the given collection of fix sessions.
     *
     * @param inFixSessions a <code>Collection&lt;FixSession&gt;</code> value
     * @return a <code>SessionSettings</code> value
     */
    SessionSettings generateSessionSettings(Collection<FixSession> inFixSessions);
    /**
     * Finds the fix session with the given session id.
     *
     * @param inFixSessionListener a <code>FixSessionListener</code> value
     * @return a <code>FixSession</code> value or <code>null</code>
     */
    FixSession findFixSessionBySessionId(SessionID inSessionId);
    /**
     * Indicates if a given session should be active on the host indicated by the give cluster data.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @param inClusterData a <code>ClusterData</code> value
     * @return a <code>boolean</code> value
     */
    boolean isAffinityMatch(FixSession inFixSession,
                            ClusterData inClusterData);
    /**
     * Finds the fix sessions that match the given criteria.
     * 
     * <p>This method will select the appropriate set of fix sessions for the given connection type
     * and instance number, considering the total number of instances available. For example, if
     * the total number of instances is 1, all sessions with the given connection type will be returned
     * because all session affinities would match. If the total number of instances is 2, and the instance
     * number is 1, sessions with odd affinities would be returned (1,3,5,etc.).
     *
     * @param isAcceptor a <code>boolean</code> value
     * @param inInstance an <code>int</code> value
     * @param inTotalInstances an <code>int</code> value
     * @return a <code>List&lt;FixSession&gt;</code> value
     */
    List<FixSession> findFixSessions(boolean isAcceptor,
                                     int inInstance,
                                     int inTotalInstances);
    /**
     * Get the most recent scheduled start of the given session.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @return a <code>Date</code> value or <code>null</code>
     */
    Date getSessionStart(SessionID inSessionId);
    /**
     * Indicate if the session with the given session ID is expected to be active now.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @return a <code>boolean</code> value
     */
    boolean isSessionTime(SessionID inSessionId);
}
