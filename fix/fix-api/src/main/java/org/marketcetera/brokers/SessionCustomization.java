package org.marketcetera.brokers;

import java.util.List;
import java.util.Set;

import org.marketcetera.algo.BrokerAlgoSpec;

/* $License$ */

/**
 * Provides a customization for one or more brokers.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface SessionCustomization
{
    /**
     * Get the session customization name value.
     *
     * @return a <code>String</code> value
     */
    String getName();
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
     * Get the available broker algo values.
     *
     * @return a <code>Set&lt;BrokerAlgoSpec&gt;</code> value
     */
    Set<BrokerAlgoSpec> getBrokerAlgos();
    /**
     * Get the userWhitelist value.
     *
     * @return a <code>Set&lt;String&gt;</code> value or <code>null</code>
     */
    Set<String> getUserWhitelist();
    /**
     * Get the userBlacklist value.
     *
     * @return a <code>Set&lt;String&gt;</code> value or <code>null</code>
     */
    Set<String> getUserBlacklist();
}
