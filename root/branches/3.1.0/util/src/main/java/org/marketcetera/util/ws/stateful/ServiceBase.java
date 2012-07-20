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
 * @version $Id: ServiceBase.java 82384 2012-07-20 19:09:59Z colin $
 */

/* $License$ */

@WebService
@ClassVersion("$Id: ServiceBase.java 82384 2012-07-20 19:09:59Z colin $")
public interface ServiceBase
    extends StatelessServiceBase {}
