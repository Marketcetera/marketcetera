package org.marketcetera.systemmodel;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since $Release$
 */
@ClassVersion("$Id: $") //$NON-NLS-1$
public interface TradeSuggestion
{
    public Order getOrder();
    public Float getScore();
    public String getDescription();
}
