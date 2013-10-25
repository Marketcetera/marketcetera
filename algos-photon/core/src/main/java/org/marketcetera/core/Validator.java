package org.marketcetera.core;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface Validator<Clazz>
{
    /**
     * 
     *
     *
     * @param inData
     * @throws CoreException if a validation exception occurs
     */
    public void validate(Clazz inData);
}
