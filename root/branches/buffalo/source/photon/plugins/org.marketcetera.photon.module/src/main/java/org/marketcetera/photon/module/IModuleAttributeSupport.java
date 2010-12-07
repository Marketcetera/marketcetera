package org.marketcetera.photon.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.MXBeanOperationException;
import org.marketcetera.module.ModuleURN;

/* $License$ */

/**
 * This interface extends {@link IModuleAttributeDefaults} with additional features to interact with
 * a module's or module factory's attributes at runtime.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.1.0
 */
@ClassVersion("$Id$")
public interface IModuleAttributeSupport extends IModuleAttributeDefaults {

	/**
	 * Sets the value of a module or module factory attribute. This operation modifies the running
	 * module or module factory. It is not specified what the module/module factory will do with the
	 * new value. See the respective documentation for further details.
	 * 
	 * @param urn
	 *            the module's instance URN or the factory's provider URN
	 * @param attribute
	 *            the attribute to set
	 * @param value
	 *            the new value
	 * @throws MXBeanOperationException
	 *             if the operation fails
	 */
	void setModuleAttribute(ModuleURN urn, String attribute, Object value)
			throws MXBeanOperationException;

	/**
	 * Returns the value of a module or module factory attribute. This operation returns the current
	 * value of the running module or module factory, which may not be the same as the value
	 * returned by {@link #getDefaultFor(ModuleURN, String)} if the attribute value has been changed
	 * via {@link #setModuleAttribute(ModuleURN, String, Object)} or by external means.
	 * 
	 * @param urn
	 *            the module's instance URN or the factory's provider URN
	 * @param attribute
	 *            the attribute being queried
	 * @return the value of the attribute
	 * @throws MXBeanOperationException
	 *             if the operation fails
	 */
	Object getModuleAttribute(ModuleURN urn, String attribute) throws MXBeanOperationException;

}
