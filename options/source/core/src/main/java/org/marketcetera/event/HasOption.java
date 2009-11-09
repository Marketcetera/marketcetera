package org.marketcetera.event;

import org.marketcetera.trade.Option;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Has an {@link Option} attribute.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface HasOption
        extends HasInstrument
{
    /**
     * Gets the instrument value.
     *
     * @return an <code>Option</code> value
     */
    @Override
    public Option getInstrument();
}
