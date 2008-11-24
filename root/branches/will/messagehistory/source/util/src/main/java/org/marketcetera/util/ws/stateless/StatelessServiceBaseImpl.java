package org.marketcetera.util.ws.stateless;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.wrappers.MarshalledLocale;

/**
 * The base class for all web services, which implements the no-op
 * methods of the base interface {@link StatelessServiceBase}.
 * 
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class StatelessServiceBaseImpl
    implements StatelessServiceBase
{

    // StatelessServiceIfaceBase.

    @Override
    public void noop
        (MarshalledLocale d0) {}
}
