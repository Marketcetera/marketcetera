package org.marketcetera.trade;

import java.util.Set;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface BoundAlgo
{
    public AlgoSpec getAlgoSpec();
    public Set<BoundAlgoTag> getBoundAlgoTags();
}
