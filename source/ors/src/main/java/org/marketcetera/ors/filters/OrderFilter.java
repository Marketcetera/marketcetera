package org.marketcetera.ors.filters;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.misc.ClassVersion;
import quickfix.FieldNotFound;
import quickfix.Message;

/**
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public interface OrderFilter
{
    void assertAccepted
        (Message message)
        throws CoreException;
}
