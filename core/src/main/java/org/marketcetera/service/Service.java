package org.marketcetera.service;

import java.io.Serializable;

import org.springframework.context.Lifecycle;

/* $License$ */

/**
 * Describes a service capable of processing data.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Service
        extends Lifecycle
{
    public void accept(Serializable inData);
    public void setDataEmitter(ServiceDataEmitter inServiceDataEmitter);
}
