package org.marketcetera.brokers.service;

import java.util.Collection;
import java.util.List;

import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionAttributeDescriptor;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.BrokerID;

import quickfix.SessionID;

/* $License$ */

/**
 * Provides access to FIX sessions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FixSessionProvider
{
    /**
     * Finds the fix session corresponding to the given name.
     *
     * @param inFixSessionName a <code>String<code> value
     * @return a <code>FixSession<code> value or <code>null</code>
     */
    FixSession findFixSessionByName(String inFixSessionName);
    /**
     * Finds the fix session with the given session id.
     *
     * @param inFixSessionListener a <code>FixSessionListener</code> value
     * @return a <code>FixSession</code> value or <code>null</code>
     */
    FixSession findFixSessionBySessionId(SessionID inSessionId);
    /**
     * Gets the fix session attribute descriptors.
     *
     * @return a <code>Collection&lt;FixSessionAttributeDescriptor&gt;</code> value
     */
    Collection<FixSessionAttributeDescriptor> getFixSessionAttributeDescriptors();
    /**
     * Find all FIX sessions.
     *
     * @return a <code>List&lt;FixSession&gt;</code> value
     */
    List<FixSession> findFixSessions();
    /**
     * Find a page of FIX sessions.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponse&lt;FixSession&gt;</code> value
     */
    CollectionPageResponse<FixSession> findFixSessions(PageRequest inPageRequest);
    /**
     * Finds the fix session corresponding to the given broker ID.
     *
     * @param inBrokerId a <code>String<code> value
     * @return a <code>FixSession<code> value or <code>null</code>
     */
    FixSession findFixSessionByBrokerId(BrokerID inBrokerId);
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
     * Saves the given fix session.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @return a <code>FixSession</code> value
     */
    FixSession save(FixSession inFixSession);
    /**
     * Deletes the given fix session.
     *
     * @param inFixSessionId a <code>SessionID</code> value
     */
    void delete(SessionID inFixSessionId);
    /**
     * Disable the given fix session.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @throws IllegalArgumentException if the fix session does not exist
     */
    void disableSession(SessionID inSessionId);
    /**
     * Enable the given fix session.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @throws IllegalArgumentException if the fix session does not exist
     */
    void enableSession(SessionID inSessionId);
    /**
     * Saves the given fix session attribute descriptor.
     *
     * @param inFixSessionAttributeDescriptor a <code>FixSessionAttributeDescriptor</code> value
     * @return a <code>FixSessionAttributeDescriptor</code> value
     */
    FixSessionAttributeDescriptor save(FixSessionAttributeDescriptor inFixSessionAttributeDescriptor);
    /**
     * Stop the given session on the appropriate cluster member.
     *
     * @param inSessionID a <code>SessionID</code> value
     */
    void stopSession(SessionID inSessionID);
    /**
     * Start the given session on the appropriate cluster member.
     *
     * @param inSessionID a <code>SessionID</code> value
     */
    void startSession(SessionID inSessionID);
}
