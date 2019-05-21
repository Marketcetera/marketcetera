package org.marketcetera.admin.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.fix.AcceptorSessionAttributes;
import org.marketcetera.fix.ClusteredBrokerStatus;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionAttributeDescriptor;
import org.marketcetera.fix.FixSessionListener;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.fix.SessionNameProvider;
import org.marketcetera.ors.brokers.Broker;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.BrokerID;

import quickfix.SessionID;
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
        extends SessionNameProvider
{
    /**
     * Reports the status of the given broker.
     *
     * @param inBrokerStatus a <code>BrokerStatus</code> value
     */
    void reportBrokerStatus(BrokerStatus inBrokerStatus);
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
     * @return a <code>ClusteredBrokerStatus</code> value or <code>null</code>
     */
    ClusteredBrokerStatus getBrokerStatus(BrokerID inBrokerId);
    /**
     * Gets the status of all brokers.
     *
     * @return a <code>BrokerStatus</code> value
     */
    BrokersStatus getBrokersStatus();
    /**
     * Finds the fix session corresponding to the given name.
     *
     * @param inFixSessionName a <code>String<code> value
     * @return a <code>FixSession<code> value or <code>null</code>
     */
    FixSession findFixSessionByName(String inFixSessionName);
    /**
     * Find the fix session corresponding to the given id.
     *
     * @param inId a <code>long<code> value
     * @return a <code>FixSession<code> value or <code>null</code>
     */
    FixSession findFixSessionById(long inId);
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
     * Gets the fix session attribute descriptors.
     *
     * @return a <code>Collection&lt;FixSessionAttributeDescriptor&gt;</code> value
     */
    Collection<FixSessionAttributeDescriptor> getFixSessionAttributeDescriptors();
    /**
     * Saves the given fix session attribute descriptor.
     *
     * @param inFixSessionAttributeDescriptor a <code>FixSessionAttributeDescriptor</code> value
     * @return a <code>FixSessionAttributeDescriptor</code> value
     */
    FixSessionAttributeDescriptor save(FixSessionAttributeDescriptor inFixSessionAttributeDescriptor);
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
     * Generate a <code>BrokerStatus</code> value from the given attributes.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @param inClusterData a <code>ClusterData</code> value
     * @param inStatus a <code>FixSessionStatus</code> value
     * @param inIsLoggedOn a <code>boolean</code> value
     * @return a <code>ClusteredBrokerStatus</code> value
     */
    ClusteredBrokerStatus generateBrokerStatus(FixSession inFixSession,
                                               ClusterData inClusterData,
                                               FixSessionStatus inStatus,
                                               boolean inIsLoggedOn);
    /**
     * Generate a <code>Broker</code> value from the given <code>FixSession</code> value.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @return a <code>Broker</code> value
     */
    Broker generateBroker(FixSession inFixSession);
    /**
     * Get a <code>Broker</code> value for the given <code>BrokerID</code> value.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @return a <code>Broker</code> value or <code>null</code>
     */
    Broker getBroker(BrokerID inBrokerId);
    /**
     * Get a <code>Broker</code>value for the given <code>SessionID</code> value.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @return a <code>Broker</code> value or <code>null</code>
     */
    Broker getBroker(SessionID inSessionId);
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
    /**
     * Get the most recent scheduled start of the given session.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @return a <code>Date</code> value or <code>null</code>
     */
    Date getSessionStart(SessionID inSessionId);
    /**
     * Get the next scheduled start of the given session.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @return a <code>Date</code> value or <code>null</code>
     */
    Date getNextSessionStart(SessionID inSessionId);
    /**
     * Get the most recent actual start of the given session.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @return a <code>Date</code> value or <code>null</code>
     */
    Date getActualSessionStart(SessionID inSessionId);
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
}
