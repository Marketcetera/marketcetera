package org.marketcetera.server.ws;

import javax.jws.WebService;

import org.marketcetera.saclient.SAService;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
@WebService(targetNamespace = "matp")
@ClassVersion("$Id$")
public interface MatpService
        extends SAService
{
}
