package org.marketcetera.fix;

import org.marketcetera.core.MutableDomainObject;

/* $License$ */

/**
 * Provides a mutable {@link FixSession} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableFixSession
        extends FixSession,MutableDomainObject<FixSession>
{
    /**
     * Set the affinity value.
     *
     * @param inAffinity an <code>int</code> value
     */
    void setAffinity(int inAffinity);
    /**
     * Set the broker ID value.
     *
     * @param inBrokerId a <code>String</code> value
     */
    void setBrokerId(String inBrokerId);
    /**
     * Set the mapped broker ID value. 
     *
     * @param inBrokerId a <code>String</code> value
     */
    void setMappedBrokerId(String inBrokerId);
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
     * Sets the session acceptor setting. 
     *
     * @param inIsAcceptor a <code>boolean</code> value
     */
    void setIsAcceptor(boolean inIsAcceptor);
    /**
     * Set the disabled value.
     *
     * @param inIsEnabled a <code>boolean</code> value
     */
    void setIsEnabled(boolean inIsEnabled);
    /**
     * Set the port value.
     *
     * @param inPort an <code>int</code> value
     */
    void setPort(int inPort);
    /**
     * Set the host value.
     *
     * @param inHost a <code>String</code> value
     */
    void setHost(String inHost);
}
