package org.marketcetera.util.ws.stateless;

import javax.jws.WebService;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.wrappers.MarshalledLocale;

/**
 * The base interface for all web services. Its single, no-op method
 * is used to force JAXB to support classes which JAXB cannot
 * determine at run-time, such as classes that are supplied as type
 * argument to generic wrappers (see the package documentation for
 * more details on this pitfall).
 * 
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@WebService
@ClassVersion("$Id$") //$NON-NLS-1$
public interface StatelessServiceBase
{
    /**
     * A no-op method. Its declaration forces JAXB to support the
     * classes supplied as arguments.
     */

    void noop
        (MarshalledLocale d0);
}
