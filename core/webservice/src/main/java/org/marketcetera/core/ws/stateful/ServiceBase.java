package org.marketcetera.core.ws.stateful;

import javax.jws.WebService;
import org.marketcetera.core.ws.stateless.StatelessServiceBase;

/**
 * The base interface for all stateful web services. It is a tagging
 * interface.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: ServiceBase.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

@WebService
public interface ServiceBase
    extends StatelessServiceBase {}
