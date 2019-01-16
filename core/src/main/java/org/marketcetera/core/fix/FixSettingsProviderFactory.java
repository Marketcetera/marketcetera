package org.marketcetera.core.fix;

/* $License$ */

/**
 * Creates {@link FixSettingsProvider} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FixSettingsProviderFactory
{
    /**
     * Creates a <code>FixSettingsProvider</code> object.
     *
     * @return a <code>FixSettingsProvider</code> value
     */
    FixSettingsProvider create();
}
