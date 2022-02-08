package org.marketcetera.util.ws.stateful;

import javax.jws.WebService;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateless.StatelessServiceBase;

/**
 * The base interface for all stateful web services. It is a tagging
 * interface.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@WebService
@ClassVersion("$Id$")
public interface ServiceBase
    extends StatelessServiceBase {}
