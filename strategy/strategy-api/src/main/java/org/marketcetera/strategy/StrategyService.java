//
// this file is automatically generated
//
package org.marketcetera.strategy;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

import org.marketcetera.core.Preserve;

/* $License$ */

/**
 * Provides StrategyService services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Preserve
public interface StrategyService
{
    /**
     * Requests loaded strategy instances.
     * 
     * @param inCurrentUserName a <code>String</code> value
     * @returns a <code>Collection&lt;StrategyInstance&gt;</code> value
     */
    Collection<? extends StrategyInstance> getStrategyInstances(String inCurrentUserName);
    /**
     * 
     *
     *
     * @param inStrategyInstanceName
     * @return
     */
    Optional<? extends StrategyInstance> findByName(String inStrategyInstanceName);
    /**
     * Load a new strategy instances.
     *
     * @param inStrategyInstance an <code>StrategyInstance</code> value
     * @returns an <code>StrategyStatus</code> value
     */
    StrategyStatus loadStrategyInstance(StrategyInstance inStrategyInstance);
    /**
     * 
     *
     *
     * @return
     */
    Path getIncomingStrategyDirectory();
    /**
     * 
     *
     *
     * @return
     */
    Path getTemporaryStrategyDirectory();
}
