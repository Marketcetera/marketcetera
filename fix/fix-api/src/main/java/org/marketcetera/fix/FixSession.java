package org.marketcetera.fix;

import java.util.Map;

import org.marketcetera.core.DomainObject;
import org.marketcetera.core.HasMutableView;

/* $License$ */

/**
 * Represents the attributes of a FIX session.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FixSession
        extends HasMutableView<MutableFixSession>,DomainObject
{
    /**
     * Get the name value.
     *
     * @return a <code>String</code> value
     */
    String getName();
    /**
     * Get the description value.
     *
     * @return a <code>String</code> value
     */
    String getDescription();
    /**
     * Get the affinity value.
     *
     * @return an <code>int</code> value
     */
    int getAffinity();
    /**
     * Get the broker ID value.
     *
     * @return a <code>String</code> value
     */
    String getBrokerId();
    /**
     * Get the optional mapped broker ID value.
     *
     * @return a <code>String</code> value
     */
    String getMappedBrokerId();
    /**
     * Get the session ID value.
     *
     * @return a <code>String</code> value
     */
    String getSessionId();
    /**
     * Indicates if the session is an acceptor session or an initiator session.
     *
     * @return a <code>boolean</code> value
     */
    boolean isAcceptor();
    /**
     * Indicates if the session is enabled or not.
     *
     * @return a <code>boolean</code> value
     */
    boolean isEnabled();
    /**
     * Indicate if the session is deleted or not.
     *
     * @return a <code>boolean</code> value
     */
    boolean isDeleted();
    /**
     * Get the port value.
     *
     * @return an <code>int</code> value
     */
    int getPort();
    /**
     * Get the host value.
     *
     * @return a <code>String</code> value
     */
    String getHost();
    /**
     * Get the session settings value.
     *
     * @return a <code>Map&lt;String,String&gt;</code> value
     */
    Map<String,String> getSessionSettings();
}
