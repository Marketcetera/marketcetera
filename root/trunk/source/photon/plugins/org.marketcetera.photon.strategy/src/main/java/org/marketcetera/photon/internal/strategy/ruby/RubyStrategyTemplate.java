package org.marketcetera.photon.internal.strategy.ruby;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Creates a new Ruby strategy script from a template.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class RubyStrategyTemplate {

	private static final String RUBY_STRATEGY_TEMPLATE = "RubyStrategyTemplate.rb"; //$NON-NLS-1$
	private static final String CLASS_NAME_PLACEHOLDER = "__TEMPLATE_CLASS_NAME__"; //$NON-NLS-1$

	/**
	 * Returns an input stream containing a new script with a class named
	 * according to the provided parameter.
	 * 
	 * @param className
	 *            the name for the new strategy class
	 * @return an input stream with the new script
	 */
	public InputStream createNewScript(String className) {
		InputStream stream = getClass().getResourceAsStream(
				RUBY_STRATEGY_TEMPLATE);
		String string = ""; //$NON-NLS-1$
		try {
			string = IOUtils.toString(stream);
			string = string.replace(CLASS_NAME_PLACEHOLDER, className);
		} catch (IOException e) {
			// Ignore and return the empty string
			ExceptUtils.swallow(e);
		}
		return new ByteArrayInputStream(string.getBytes());
	}
}
