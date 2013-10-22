package org.marketcetera.trade;

import java.io.Serializable;
import java.util.Set;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface AlgoSpec
        extends Serializable
{
    public String getName();
    public Set<AlgoTagSpec> getAlgoTagSpecs();
}
