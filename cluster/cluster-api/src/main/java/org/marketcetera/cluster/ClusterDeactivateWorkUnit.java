package org.marketcetera.cluster;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* $License$ */

/**
 * Indicates the method to be invoked when a clusterable unit is deactivated.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ClusterDeactivateWorkUnit.java 16534 2015-02-10 20:17:33Z colin $
 * @since 2.5.0
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ClusterDeactivateWorkUnit
{
}
