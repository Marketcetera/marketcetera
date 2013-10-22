package org.marketcetera.trade;

import java.io.Serializable;

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
public interface AlgoTagSpec
        extends Serializable
{
    public int getTag();
    public String getDescription();
    public boolean isMandatory();
    public String getPattern();
}
