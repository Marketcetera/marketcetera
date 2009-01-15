package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

import javax.management.DescriptorKey;
import java.lang.annotation.*;

/* $License$ */
/**
 * Annotation that is used to supply user-friendly
 * descriptions for the MXBeans, their attributes, methods
 * and the parameters.
 *
 * The value of this annotation is available as the value of attribute
 * <code>name</code> within the MBean's
 * {@link javax.management.Descriptor}
 *
 * Do note that currently this annotation allows for non-localized names.
 *
 * Going forward, we need to add the attributes for
 * <code>descriptionResourceBundleBaseName</code> and
 * <code>descriptionResourceKey</code> fields in the descriptor,
 * to allow for localizable descriptions. This has not been done
 * currently as the client may not have the resource bundles
 * available and that the current i18n classes do not allow for easy
 * extraction of these values so that they can be embedded into these
 * annotations.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
@Target({
        ElementType.TYPE,
        ElementType.METHOD,
        ElementType.PARAMETER
        })
@Retention(RetentionPolicy.RUNTIME)
public @interface DisplayName {
    /**
     * Provides the text describing the interface, method or the parameter.
     *
     * @return text describing the mbean element.
     */
    @DescriptorKey("name")  //$NON-NLS-1$
    String value();
}
