package org.marketcetera.server.ws;

import javax.jws.WebParam;

import org.marketcetera.util.ws.stateless.StatelessServiceBase;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MatpService
        extends StatelessServiceBase
{
    public void test(@WebParam(name="Value")String inValue);
}
