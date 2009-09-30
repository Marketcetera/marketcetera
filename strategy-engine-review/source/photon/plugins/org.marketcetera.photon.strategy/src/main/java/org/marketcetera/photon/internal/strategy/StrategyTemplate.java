package org.marketcetera.photon.internal.strategy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Creates a new strategy script from a template.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class StrategyTemplate {

    private static final String CLASS_NAME_PLACEHOLDER = "__TEMPLATE_CLASS_NAME__"; //$NON-NLS-1$

    /**
     * Returns an input stream containing a new script with a class named
     * according to the provided parameter.
     * <p>
     * Note the returned input stream will be empty if an error occurs while
     * reading the template.
     * 
     * @param templateFile
     *            the name of the template file
     * @param className
     *            the name for the new strategy class
     * @return an input stream with the new script
     * @throws IllegalArgumentException
     *             if any parameter is null
     */
    public static InputStream createNewScript(String templateFile,
            String className) {
        Validate.notNull(templateFile, "templateFile", //$NON-NLS-1$
                className, "className"); //$NON-NLS-1$
        InputStream stream = StrategyTemplate.class
                .getResourceAsStream(templateFile);
        String string = ""; //$NON-NLS-1$
        if (stream != null) {
            try {
                string = IOUtils.toString(stream);
                string = string.replace(CLASS_NAME_PLACEHOLDER, className);
            } catch (IOException e) {
                // Ignore and return the empty string
                ExceptUtils.swallow(e);
            }
        }
        return new ByteArrayInputStream(string.getBytes());
    }
}
