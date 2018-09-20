package org.marketcetera.fix;

import java.util.Collection;
import java.util.List;

import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.brokers.BrokerStatusPublisher;
import org.marketcetera.core.BaseClient;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;

/* $License$ */

/**
 * Provides a client implementation for FIX admin actions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FixAdminClient
        extends BaseClient,BrokerStatusPublisher
{
    /**
     * Create the given FIX session.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @return a <code>FixSession</code> value
     */
    FixSession createFixSession(FixSession inFixSession);
    /**
     * Get current FIX sessions with their status.
     *
     * @return a <code>List&lt;ActiveFixSession&gt;</code> value
     */
    List<ActiveFixSession> readFixSessions();
    /**
     * Read a page of FIX sessions.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponse&lt;ActiveFixSession&gt;</code> value
     */
    CollectionPageResponse<ActiveFixSession> readFixSessions(PageRequest inPageRequest);
    /**
     * Update the given FIX session with the given original name.
     *
     * @param inIncomingName a <code>String</code> value
     * @param inFixSession a <code>FixSession</code> value
     */
    void updateFixSession(String inIncomingName,
                          FixSession inFixSession);
    /**
     * Enable the FIX session with the given name.
     *
     * @param inName a <code>String</code> value
     */
    void enableFixSession(String inName);
    /**
     * Disable the FIX session with the given name.
     *
     * @param inName a <code>String</code> value
     */
    void disableFixSession(String inName);
    /**
     * Delete the FIX session with the given name.
     *
     * @param inName a <code>String</code> value
     */
    void deleteFixSession(String inName);
    /**
     * Stop the FIX session with the given name.
     *
     * @param inName a <code>String</code> value
     */
    void stopFixSession(String inName);
    /**
     * Start the FIX session with the given name.
     *
     * @param inName a <code>String</code> value
     */
    void startFixSession(String inName);
    /**
     * Add the given broker status listener.
     *
     * @param inBrokerStatusListener a <code>BrokerStatusListener</code> value
     */
    void addBrokerStatusListener(BrokerStatusListener inBrokerStatusListener);
    /**
     * Remove the given broker status listener.
     *
     * @param inBrokerStatusListener a <code>BrokerStatusListener</code> value
     */
    void removeBrokerStatusListener(BrokerStatusListener inBrokerStatusListener);
    /**
     * Get the FIX session attribute descriptors.
     *
     * @return a <code>Collection&lt;FixSessionAttributeDescriptor&gt;</code> value
     */
    Collection<FixSessionAttributeDescriptor> getFixSessionAttributeDescriptors();
    /**
     * Update sender and target sequence numbers for the given session.
     *
     * @param inSessionName a <code>String</code> value
     * @param inSenderSequenceNumber an <code>int</code> value
     * @param inTargetSequenceNumber an <code>int</code> value
     */
    void updateSequenceNumbers(String inSessionName,
                               int inSenderSequenceNumber,
                               int inTargetSequenceNumber);
    /**
     * Update the sender sequence number for the given session.
     *
     * @param inSessionName a <code>String</code> value
     * @param inSenderSequenceNumber an <code>int</code> value
     */
    void updateSenderSequenceNumber(String inSessionName,
                                    int inSenderSequenceNumber);
    /**
     * Update the target sequence number for the given session.
     *
     * @param inSessionName a <code>String</code> value
     * @param inTargetSequenceNumber an <code>int</code> value
     */
    void updateTargetSequenceNumber(String inSessionName,
                                    int inTargetSequenceNumber);
    /**
     * Get the instance data for the given affinity.
     *
     * @param inAffinity an <code>int</code> value
     * @return a <code>FixSessionInstanceData</code> value
     */
    FixSessionInstanceData getFixSessionInstanceData(int inAffinity);
}
