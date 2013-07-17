package org.marketcetera.photon.module;

import org.marketcetera.module.ModuleConfigurationProvider;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * This interface provides access to module and module factory attribute
 * defaults. These are the values set on a module or a module factory
 * immediately after creation.
 * 
 * TODO: add interface for flushing/persisting the values
 * 
 * @see ModuleConfigurationProvider
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.1.0
 */
@ClassVersion("$Id$")
public interface IModuleAttributeDefaults {

	/**
	 * Special string used to identify module instance defaults.
	 */
	String INSTANCE_DEFAULTS_IDENTIFIER = "%InstanceDefaults%"; //$NON-NLS-1$

	/**
	 * Returns the default value for the module attribute (if an instance URN is
	 * given) or module factory attribute (if a provider URN is given).
	 * 
	 * If an instance URN is given and no value exists, the provider's instance
	 * defaults are checked as well.
	 * 
	 * @param urn
	 *            the module's instance URN or the factory's provider URN
	 * @param attribute
	 *            the attribute being queried
	 * @return the string representation of the default value of the attribute,
	 *         or <code>null</code> if none exists
	 */
	String getDefaultFor(ModuleURN urn, String attribute);

	/**
	 * Sets the default value for the module or module factory attribute. The
	 * default takes effect the next time the module is created or the factory
	 * is initialized.
	 * 
	 * The value should not be <code>null</code>. Use
	 * {@link #removeDefaultFor(ModuleURN, String)} to remove an attribute
	 * default.
	 * 
	 * @param urn
	 *            the module's instance URN or the factory's provider URN
	 * @param attribute
	 *            the attribute being set
	 * @param value
	 *            the string representation of the new default value, must not
	 *            be <code>null</code>
	 */
	void setDefaultFor(ModuleURN urn, String attribute, String value);

	/**
	 * Sets the instance default value for a module attribute. This is a
	 * provider level attribute that is used when there is no specific default
	 * for a particular module. This default takes effect the next time a module
	 * is created.
	 * 
	 * The value should not be <code>null</code>. Use
	 * {@link #removeInstanceDefaultFor(ModuleURN, String)} to remove an
	 * attribute default.
	 * 
	 * @param urn
	 *            the factory's provider URN
	 * @param attribute
	 *            the attribute being set
	 * @param value
	 *            the string representation of the new default value, must not
	 *            be <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the urn is not a valid provider URN
	 */
	void setInstanceDefaultFor(ModuleURN urn, String attribute, String value);

	/**
	 * Removes the default value for the module or module factory attribute.
	 * 
	 * @param urn
	 *            the module's instance URN or the factory's provider URN
	 * @param attribute
	 *            the attribute to remove
	 */
	void removeDefaultFor(ModuleURN urn, String attribute);

	/**
	 * Removes the instance default value for the module attribute.
	 * 
	 * @param urn
	 *            the factory's provider URN
	 * @param attribute
	 *            the attribute to remove
	 * @throws IllegalArgumentException
	 *             if the urn is not a valid provider URN
	 */
	void removeInstanceDefaultFor(ModuleURN urn, String attribute);

	/**
	 * Flushes/persists the default values. Note that this operation will be
	 * performed automatically when the plug-in shuts down, but clients can call
	 * this manually to force a flush attempt at any time.
	 * 
	 * It is not guaranteed that the operation will succeed (the backing store
	 * may be unaccessible, etc.).
	 */
	void flush();

}
