package org.marketcetera.service;

import java.io.Serializable;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ServiceDataEmitter
{
    public void emit(Serializable inData);
}
