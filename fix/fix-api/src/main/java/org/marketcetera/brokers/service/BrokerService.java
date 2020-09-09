package org.marketcetera.brokers.service;

import java.time.LocalDateTime;
import java.util.Collection;

import org.marketcetera.admin.User;
import org.marketcetera.brokers.BrokerStatusPublisher;
import org.marketcetera.brokers.SessionCustomization;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.fix.AcceptorSessionAttributes;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionListener;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.fix.ServerFixSession;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.BrokerID;

import quickfix.SessionSettings;

/* $License$ */

/**
 * Provides access to broker services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface BrokerService
        extends BrokerStatusPublisher
{
    /**
     * Get the active FIX session for the given session id.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     * @return an <code>ActiveFixSession</code> value or <code>null</code>
     */
    ActiveFixSession getActiveFixSession(quickfix.SessionID inSessionId);
    /**
     * Get an <code>ActiveFixSession</code> value for the given <code>BrokerID</code> value.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @return an <code>ActiveFixSession</code> value or <code>null</code>
     */
    ActiveFixSession getActiveFixSession(BrokerID inBrokerId);
    /**
     * Get a page of active FIX sessions.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponse&lt;ActiveFixSession&gt;</code> value
     */
    CollectionPageResponse<ActiveFixSession> getActiveFixSessions(PageRequest inPageRequest);
    /**
     * Get the server FIX session for the given session id.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     * @return a <code>ServerFixSession</code> value or <code>null</code>
     */
    ServerFixSession getServerFixSession(quickfix.SessionID inSessionId);
    /**
     * Get the server FIX session for the given broker id.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @return a <code>ServerFixSession</code> value or <code>null</code>
     */
    ServerFixSession getServerFixSession(BrokerID inBrokerId);
    /**
     * Get all server FIX session values.
     *
     * @return a <code>ServerFixSession</code> value
     */
    Collection<ServerFixSession> getServerFixSessions();
    /**
     * Get all active FIX session values.
     *
     * @return a <code>Collection&lt;ActiveFixSession&gt;</code> value
     */
    Collection<ActiveFixSession> getActiveFixSessions();
    /**
     * Get the available FIX initiator sessions.
     *
     * @return a <code>Collection&lt;ActiveFixSession&gt;</code> value
     */
    Collection<ActiveFixSession> getAvailableFixInitiatorSessions();
    /**
     * Reports the status of the given broker.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @param inFixSessionStatus a <code>FixSessionStatus</code> value
     */
    void reportBrokerStatus(BrokerID inBrokerId,
                            FixSessionStatus inFixSessionStatus);
    /**
     * Reports the given status for the given session from all cluster members.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @param inStatusToReport a <code>FixSessionStatus</code> value
     */
    void reportBrokerStatusFromAll(FixSession inFixSession,
                                   FixSessionStatus inStatusToReport);
    /**
     * Gets the status of the given broker.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @return a <code>FixSessionStatus</code> value or <code>null</code>
     */
    FixSessionStatus getFixSessionStatus(BrokerID inBrokerId);
    /**
     * Generate a <code>Broker</code> value from the given <code>FixSession</code> value.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @return an <code>ActiveFixSession</code> value
     */
    ActiveFixSession generateBroker(FixSession inFixSession);
    /**
     * Adds the given fix session listener to receive future fix session status updated.
     *
     * @param inFixSessionListener a <code>FixSessionListener</code> value
     */
    void addFixSessionListener(FixSessionListener inFixSessionListener);
    /**
     * Removes the given fix session listener.
     *
     * @param inFixSessionListener a <code>FixSessionListener</code> value
     */
    void removeFixSessionListener(FixSessionListener inFixSessionListener);
    /**
     * Get the fix session listeners.
     *
     * @return a <code>Collection&lt;FixSessionListener&gt;</code> value
     */
    Collection<FixSessionListener> getFixSessionListeners();
    /**
     * Finds the fix session corresponding to the given broker ID.
     *
     * @param inBrokerId a <code>String<code> value
     * @return a <code>FixSession<code> value or <code>null</code>
     */
    FixSession findFixSessionByBrokerId(BrokerID inBrokerId);
    /**
     * Get the most recent scheduled start of the given session.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     * @return a <code>LocalDateTime</code> value or <code>null</code>
     */
    LocalDateTime getSessionStart(quickfix.SessionID inSessionId);
    /**
     * Get the next scheduled start of the given session.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     * @return a <code>LocalDateTime</code> value or <code>null</code>
     */
    LocalDateTime getNextSessionStart(quickfix.SessionID inSessionId);
    /**
     * Get the most recent actual start of the given session.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     * @return a <code>LocalDateTime</code> value or <code>null</code>
     */
    LocalDateTime getActualSessionStart(quickfix.SessionID inSessionId);
    /**
     * Get the FIX settings provider for the given affinity.
     *
     * @param inAffinity an <code>int</code> value
     * @return an <code>AcceptorSessionAttributes</code> value
     */
    AcceptorSessionAttributes getFixSettingsFor(int inAffinity);
    /**
     * Generates a <code>SessionSettings<code> value based on the given collection of fix sessions.
     *
     * @param inFixSessions a <code>Collection&lt;FixSession&gt;</code> value
     * @return a <code>SessionSettings</code> value
     */
    SessionSettings generateSessionSettings(Collection<FixSession> inFixSessions);
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
     * Indicate if a given session should be active on the host indicated by the give cluster data.
     *
     * @param inClusterData a <code>ClusterData</code> value
     * @param inAffinity an <code>int</code> value
     * @return a <code>boolean</code> value
     */
    boolean isAffinityMatch(ClusterData inClusterData,
                            int inAffinity);
    /**
     * Indicate if the given user is allowed to use the given broker.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @param inUser a <code>User</code> value
     * @return a <code>boolean</code> value
     */
    boolean isUserAllowed(BrokerID inBrokerId,
                          User inUser);
    /**
     * Indicate if the session with the given id should be active at this time.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @return a <code>boolean</code> value
     */
    boolean isSessionTime(quickfix.SessionID inSessionId);
    /**
     * Get the session customization for the given session.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @return a <code>SessionCustomization</code> value
     */
    SessionCustomization getSessionCustomization(FixSession inFixSession);
    /**
     * Get the human-readable name of the session with the given id.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     * @return a <code>String</code> value
     */
    String getSessionName(quickfix.SessionID inSessionId);
}
