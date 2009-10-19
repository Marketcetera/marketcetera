package org.marketcetera.event;

import org.marketcetera.trade.Option;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasOption
        extends HasInstrument
{
    public Option getOption();
}
