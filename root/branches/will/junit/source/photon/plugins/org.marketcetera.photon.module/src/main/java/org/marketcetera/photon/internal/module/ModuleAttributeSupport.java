package org.marketcetera.photon.internal.module;

import javax.management.Attribute;
import javax.management.ObjectName;

import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.module.IModuleAttributeDefaults;
import org.marketcetera.photon.module.IModuleAttributeSupport;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * This implementation of {@link IModuleAttributeSupport} decorates a separate
 * {@link IModuleAttributeDefaults} provider, adding JMX support.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.1.0
 */
@ClassVersion("$Id$")
public class ModuleAttributeSupport implements IModuleAttributeSupport {

	private final IModuleAttributeDefaults mModuleAttributeDefaults;

	/**
	 * Constructor.
	 * 
	 * @param moduleAttributeDefaults
	 *            default support provider to extend
	 */
	public ModuleAttributeSupport(
			IModuleAttributeDefaults moduleAttributeDefaults) {
		mModuleAttributeDefaults = moduleAttributeDefaults;
	}

	@Override
	public String getDefaultFor(ModuleURN urn, String attribute) {
		return mModuleAttributeDefaults.getDefaultFor(urn, attribute);
	}

	@Override
	public void setDefaultFor(ModuleURN urn, String attribute, String value) {
		mModuleAttributeDefaults.setDefaultFor(urn, attribute, value);
	}

	@Override
	public void setInstanceDefaultFor(ModuleURN urn, String attribute,
			String value) {
		mModuleAttributeDefaults.setInstanceDefaultFor(urn, attribute, value);
	}

	@Override
	public void removeDefaultFor(ModuleURN urn, String attribute) {
		mModuleAttributeDefaults.removeDefaultFor(urn, attribute);
	}

	@Override
	public void removeInstanceDefaultFor(ModuleURN urn, String attribute) {
		mModuleAttributeDefaults.removeInstanceDefaultFor(urn, attribute);
	}
	
	@Override
	public void flush() {
		mModuleAttributeDefaults.flush();
	}

	@Override
	public Object getModuleAttribute(ModuleURN urn, String attribute) {
		try {
			ObjectName objectName = urn.toObjectName();
			return ModuleSupport.getMBeanServerConnection().getAttribute(
					objectName, attribute);
		} catch (Exception e) {
			Messages.MODULE_ATTRIBUTE_SUPPORT_FAILED_GET_ATTRIBUTE.error(this,
					attribute, urn);
			return null;
		}
	}

	@Override
	public void setModuleAttribute(ModuleURN urn, String attribute, Object value) {
		try {
			ObjectName objectName = urn.toObjectName();
			ModuleSupport.getMBeanServerConnection().setAttribute(objectName,
					new Attribute(attribute, value));
		} catch (Exception e) {
			Messages.MODULE_ATTRIBUTE_SUPPORT_FAILED_SET_ATTRIBUTE.error(this,
					attribute, urn);
		}
	}
}
