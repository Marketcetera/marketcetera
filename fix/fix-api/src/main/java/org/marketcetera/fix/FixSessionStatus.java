package org.marketcetera.fix;

import java.util.EnumSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

/* $License$ */

/**
 * Describes the status of a Fix session.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement
public enum FixSessionStatus
{
    /**
     * this session will never appear on this host because it is targeted via session affinity to another instance in this cluster
     */
    AFFINITY_MISMATCH,
    /**
     * session is potentially active, but, on this host, is serving as a warm backup
     */
    BACKUP,
    /**
     * session is active and connected
     */
    CONNECTED,
    /**
     * session has been deleted; essentially invisible to the application
     */
    DELETED,
    /**
     * session is disabled, and does not have an active FIX session; essentially invisible to the application
     */
    DISABLED,
    /**
     * session exists and is enabled, but now is not a current session time
     */
    DISCONNECTED,
    /**
     * session exists and now is a current session time. for reasons unknown, the session is not currently connected. the system should be actively trying to connect this session.
     */
    NOT_CONNECTED,
    /**
     * session exists, but has been manually halted. will not try to connect until started or the system restarts
     */
    STOPPED,
    /**
     * session status is unknown
     */
    UNKNOWN;
    /**
     * Indicate if the status for the session is a status for a primary session or not.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isPrimary()
    {
        return primary.contains(this);
    }
    /**
     * Indicate if the session is started or not.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isStarted()
    {
        return started.contains(this);
    }
    /**
     * Indicate if the session is enabled or not.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isEnabled()
    {
        return enabled.contains(this);
    }
    /**
     * Indicate if the session is logged on or not.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isLoggedOn()
    {
        return CONNECTED.equals(this);
    }
    /**
     * contains the statuses that indicate if a session is started or not
     */
    private static final Set<FixSessionStatus> started = EnumSet.of(CONNECTED,NOT_CONNECTED,DISCONNECTED);
    /**
     * contains the statuses that indicate if a session is enabled or not
     */
    private static final Set<FixSessionStatus> enabled = EnumSet.of(AFFINITY_MISMATCH,CONNECTED,DISCONNECTED,NOT_CONNECTED,STOPPED);
    /**
     * contains the statuses that indicate if a session is primary or not
     */
    private static final Set<FixSessionStatus> primary = EnumSet.of(STOPPED,DISCONNECTED,NOT_CONNECTED,CONNECTED);
}
