package org.marketcetera.brokers;

import java.util.List;
import java.util.Set;

import org.marketcetera.admin.User;
import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.fix.FixSession;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.BrokerID;

import quickfix.DataDictionary;
import quickfix.SessionID;

/* $License$ */

/**
 * Represents a FIX connection available to the system.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Broker
{
    /**
     * Get the broker name value.
     *
     * @return a <code>String</code> value
     */
    String getName();
    /**
     * Get the FIX session ID value.
     *
     * @return a <code>SessionID</code> value
     */
    SessionID getSessionId();
    /**
     * Get the broker ID value.
     *
     * @return a <code>BrokerID</code> value
     */
    BrokerID getBrokerId();
    /**
     * Get the underlying FIX session.
     *
     * @return a <code>FixSession</code> value
     */
    FixSession getFixSession();
    /**
     * Get the order modifiers value.
     *
     * @return a <code>List&lt;MessageModifier&gt;</code> value
     */
    List<MessageModifier> getOrderModifiers();
    /**
     * Get the response modifiers value.
     *
     * @return a <code>List&lt;MessageModifier&gt;</code> value
     */
    List<MessageModifier> getResponseModifiers();
    /**
     * Get the FIX version value.
     *
     * @return a <code>FIXVersion</code> value
     */
    FIXVersion getFIXVersion();
    /**
     * Get the available broker algo values.
     *
     * @return a <code>Set&lt;BrokerAlgoSpec&gt;</code> value
     */
    Set<BrokerAlgoSpec> getBrokerAlgos();
    /**
     * Get the userWhitelist value.
     *
     * @return a <code>Set&lt;User&gt;</code> value or <code>null</code>
     */
    Set<User> getUserWhitelist();
    /**
     * Get the userBlacklist value.
     *
     * @return a <code>Set&lt;User&gt;</code> value or <code>null</code>
     */
    Set<User> getUserBlacklist();
    /**
     * Get the underlying FIX message factory value.
     *
     * @return a <code>FIXMessageFactory</code> value
     */
    FIXMessageFactory getFIXMessageFactory();
    /**
     * Get the underlying data dictionary value.
     *
     * @return a <code>DataDictionary</code> value
     */
    DataDictionary getDataDictionary();
    /**
     * Get the underlying FIX data dictionary value.
     *
     * @return a <code>FIXDataDictionary</code> value
     */
    FIXDataDictionary getFIXDataDictionary();
    /**
     * Get the mapped broker ID value.
     *
     * @return a <code>BrokerID</code> value or <code>null</code> if this broker is not a virtual broker
     */
    BrokerID getMappedBrokerId();
}
