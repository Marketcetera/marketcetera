package org.marketcetera.api.attributes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to retain subversion &#x24;Id&#x24; during runtime. Use
 * by prefixing each class, interface, and enum with
 * <code>@ClassVersion("&#x24;Id&#x24;")</code>.
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id: ClassVersion.java 16063 2012-01-31 18:21:55Z colin $
 */

/* $License$ */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassVersion
{
    String value();
}
