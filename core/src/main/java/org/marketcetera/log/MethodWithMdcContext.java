package org.marketcetera.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* $License$ */

/**
 * Used to mark methods in which before calling it, the function information should be set into the MDC context. All logs of subsequent method calls
 * on any bean will contain the MDC information.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodWithMdcContext
{
    String functionName() default "";
}
