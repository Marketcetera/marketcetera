package org.marketcetera.photon.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.ModuleURN;

/* $License$ */

/**
 * This interface extends {@link IModuleAttributeDefaults} with additional
 * features to interact with a module's or module factory's attributes at
 * runtime.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.1.0
 */
@ClassVersion("$Id$")
public interface IModuleAttributeSupport extends IModuleAttributeDefaults {

	/**
	 * Sets the value of a module or module factory attribute. This operation
	 * modifies the running module or module factory. It is not specified what
	 * the module/module factory will do with the new value. See the respective
	 * documentation for further details.
	 * 
	 * Note that no feedback is currently provided if the operation fails
	 * (except an error log). It is recommended to check if the value was
	 * accepted by calling {@link #getModuleAttribute(ModuleURN, String)}.
	 * 
	 * TODO: provide better feedback in case of errors
	 * 
	 * @param urn
	 *            the module's instance URN or the factory's provider URN
	 * @param attribute
	 *            the attribute to set
	 * @param value
	 *            the new value
	 */
	void setModuleAttribute(ModuleURN urn, String attribute, Object value);

	/**
	 * Returns the value of a module or module factory attribute. This operation
	 * returns the current value of the running module or module factory, which
	 * may not be the same as the value returned by
	 * {@link #getDefaultFor(ModuleURN, String)} if the attribute value has been
	 * changed via {@link #setModuleAttribute(ModuleURN, String, Object)} or by
	 * external means.
	 * 
	 * @param urn
	 *            the module's instance URN or the factory's provider URN
	 * @param attribute
	 *            the attribute being queried
	 * @return the value of the attribute, or <code>null</code> if it could not
	 *         be determined
	 */
	Object getModuleAttribute(ModuleURN urn, String attribute);

}
