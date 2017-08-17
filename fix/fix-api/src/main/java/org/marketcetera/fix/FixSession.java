package org.marketcetera.fix;

import java.util.Map;

import org.marketcetera.persist.SummaryNDEntityBase;

/* $License$ */

/**
 * Represents the attributes of a FIX session.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FixSession
        extends SummaryNDEntityBase
{
    /**
     * Get the affinity value.
     *
     * @return an <code>int</code> value
     */
    int getAffinity();
    /**
     * Set the affinity value.
     *
     * @param inAffinity an <code>int</code> value
     */
    void setAffinity(int inAffinity);
    /**
     * Get the broker ID value.
     *
     * @return a <code>String</code> value
     */
    String getBrokerId();
    /**
     * Set the broker ID value.
     *
     * @param inBrokerId a <code>String</code> value
     */
    void setBrokerId(String inBrokerId);
    /**
     * Get the optional mapped broker ID value.
     *
     * @return a <code>String</code> value
     */
    String getMappedBrokerId();
    /**
     * Set the mapped broker ID value. 
     *
     * @param inBrokerId a <code>String</code> value
     */
    void setMappedBrokerId(String inBrokerId);
    /**
     * Get the session ID value.
     *
     * @return a <code>String</code> value
     */
    String getSessionId();
    /**
     * Set the session ID value.
     *
     * @param inSessionId a <code>String</code> value
     */
    void setSessionId(String inSessionId);
    /**
     * Set the name value.
     *
     * @param inName a <code>String</code> value
     */
    void setName(String inName);
    /**
     * Set the description value.
     *
     * @param inDescription a <code>String</code> value
     */
    void setDescription(String inDescription);
    /**
     * Indicates if the session is an acceptor session or an initiator session.
     *
     * @return a <code>boolean</code> value
     */
    boolean isAcceptor();
    /**
     * Sets the session acceptor setting. 
     *
     * @param inIsAcceptor a <code>boolean</code> value
     */
    void setIsAcceptor(boolean inIsAcceptor);
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
     * Set the disabled value.
     *
     * @param inIsEnabled a <code>boolean</code> value
     */
    void setIsEnabled(boolean inIsEnabled);
    /**
     * Get the port value.
     *
     * @return an <code>int</code> value
     */
    int getPort();
    /**
     * Set the port value.
     *
     * @param inPort an <code>int</code> value
     */
    void setPort(int inPort);
    /**
     * Get the host value.
     *
     * @return a <code>String</code> value
     */
    String getHost();
    /**
     * Set the host value.
     *
     * @param inHost a <code>String</code> value
     */
    void setHost(String inHost);
    /**
     * Get the session settings value.
     *
     * @return a <code>Map&lt;String,String&gt;</code> value
     */
    Map<String,String> getSessionSettings();
}
