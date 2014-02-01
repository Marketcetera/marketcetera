package org.marketcetera.core;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides validation services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface Validator<Clazz>
{
    /**
     * Validates the given data.
     *
     * @param inData a <code>Clazz</code> value
     * @throws CoreException if a validation exception occurs
     */
    public void validate(Clazz inData);
}
