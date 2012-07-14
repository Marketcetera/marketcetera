package org.marketcetera.ors.filters;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.misc.ClassVersion;
import quickfix.Message;

/**
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public interface OrderFilter
{
    void assertAccepted
        (Message message)
        throws CoreException;
}
