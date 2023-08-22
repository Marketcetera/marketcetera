//
// this file is automatically generated
//
package org.marketcetera.trade.pnl;

import org.marketcetera.core.Factory;
import org.marketcetera.core.Preserve;

/* $License$ */

/**
 * Creates new {@link CurrentPosition} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Preserve
public interface CurrentPositionFactory
        extends Factory<CurrentPosition>
{
    /**
     * Create a new <code>CurrentPosition</code> instance.
     *
     * @return a <code>CurrentPosition</code> value
     */
    @Override
    CurrentPosition create();
    /**
     * Create a new <code>CurrentPosition</code> instance from the given object.
     *
     * @param inCurrentPosition a <code>CurrentPosition</code> value
     * @return a <code>CurrentPosition</code> value
     */
    @Override
    CurrentPosition create(CurrentPosition inCurrentPosition);
}
