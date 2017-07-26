package org.marketcetera.cluster;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* $License$ */

/**
 * Indicates that an annotated class is a member of a cluster work unit.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ClusterWorkUnit.java 16666 2015-12-10 01:01:34Z colin $
 * @since 2.5.0
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClusterWorkUnit
{
    /**
     * Indicates how the cluster should handle this work unit.
     *
     * @return a <code>ClusterWorkUnitType</code> value
     */
    ClusterWorkUnitType type() default ClusterWorkUnitType.REPLICATED;
    /**
     * Uniquely identifies this cluster work unit in the system domain.
     *
     * @return a <code>String</code> value
     */
    String id();
}
