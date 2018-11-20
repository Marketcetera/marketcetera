package org.marketcetera.cluster;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* $License$ */

/**
 * Indicates the method that should be invoked when a clusterable unit is activated.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.5.0
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface ClusterActivateWorkUnit
{
}
