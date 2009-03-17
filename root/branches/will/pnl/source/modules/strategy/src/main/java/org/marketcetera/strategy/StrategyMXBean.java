package org.marketcetera.strategy;

import javax.management.MXBean;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.Util;
import org.marketcetera.module.DataReceiver;
import org.marketcetera.module.DisplayName;
import org.marketcetera.module.ModuleURN;

/* $License$ */

/**
 * MXBean interface for Strategies.
 * 
 * <p>Any changes made using the setters in this interface require the implementing module to be
 * restarted before the changes take effect.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
@MXBean(true)
@DisplayName("Management Interface for Strategy")
public interface StrategyMXBean
{
    /**
     * Sets the strategy parameters. 
     *
     * <p>This method assumes that the <code>String</code> consists of a series of key/value pairs separated by
     * the {@link Util#KEY_VALUE_DELIMITER}.  The key/value pairs themselves are separated by the {@link Util#KEY_VALUE_SEPARATOR}.
     * Any malformed entries are discarded.  A best-effort will be made to retain as many key/value pairs as possible.
     *
     * @param inParameters a <code>String</code> value or null.  If null or of zero-length, the current parameters are removed.
     *   All current values are replaced by the values specifed in <code>inParameters</code>.
     */
    @DisplayName("A set of key/value pairs to provide to the strategy")
    public void setParameters(String inParameters);
    /**
     * Returns the current strategy parameters.
     *
     * <p>This method returns a <code>String</code> containing a series of key/value pairs representing the strategy
     * parameters.  Each key/value pair is separated by the {@link Util#KEY_VALUE_DELIMITER}.
     * The pairs themselves are separated by {@link Util#KEY_VALUE_SEPARATOR}.
     * 
     * <p>Note that if any of the keys or values of the <code>Properties</code> object contains either the
     * {@link Util#KEY_VALUE_DELIMITER} or the {@link Util#KEY_VALUE_SEPARATOR} character, the resulting
     * <code>String</code> will not be paresable with {@link #setParameters(String)}.
     *
     * @return a <code>String</code> value containing the current parameter settings or null if the current settings are empty.
     */
    @DisplayName("A set of key/value pairs to provide to the strategy")
    public String getParameters();
    /**
     * Sets the destination for output created by this strategy.
     * 
     * <p>The value passed must be a representation of a {@link ModuleURN} corresponding to a valid, started module
     * which implements {@link DataReceiver}.  If <code>inDestination</code> is null or empty, the current
     * destination, if any, is disconnected.
     * 
     * <p>Note that this method will have no effect on any data flows established externally.
     *
     * @param inDestination a <code>String</code> value containing a valid, started {@link DataReceiver} {@link ModuleURN} or null
     * @throws IllegalArgumentException if <code>inDestination</code> cannot be parsed as a {@link ModuleURN}
     */
    @DisplayName("The destination to which to send output created by the strategy")
    public void setOutputDestination(String inDestination);
    /**
     * Returns the current destination for output created by this strategy.
     *
     * @return a <code>String</code> value containing a <code>String</code> representation of a {@link ModuleURN} or
     *   null if no destination has been established.
     */
    @DisplayName("The destination to which to send output created by the strategy")
    public String getOutputDestination();
    /**
     * Indicates if the strategy is configured to route orders to the ORS.
     *
     * @return a <code>boolean</code> value
     */
    @DisplayName("Indicates if this strategy is currently set to route orders to the ORS")
    public boolean isRoutingOrdersToORS();
    /**
     * Determines if the strategy is configured to route orders to the ORS.
     *
     * @param inValue a <code>boolean</code> value
     */
    @DisplayName("Determines if this strategy is currently set to route orders to the ORS")
    public void setIsRountingOrdersToORS(boolean inValue);
    /**
     * Gets the strategy status. 
     *
     * @return a <code>String</code> value corresponding to a value in {@link Status}.
     */
    @DisplayName("Gets the strategy status")
    public String getStatus();
    /**
     * Attempts to interrupt and halt a running strategy.
     *
     * <p>Interrupting a strategy makes a best-effort attempt to stop it.  Under certain circumstances, this
     * method may not stop a running strategy, e.g. if the strategy deliberately ignores thread interrupts.
     * This method may be executed more than once, if necessary.
     */
    @DisplayName("Makes a best-effort to interrupt and halt a strategy")
    public void interrupt();
}
