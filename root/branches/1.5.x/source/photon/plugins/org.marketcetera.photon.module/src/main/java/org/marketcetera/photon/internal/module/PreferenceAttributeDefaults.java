package org.marketcetera.photon.internal.module;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.marketcetera.module.InvalidURNException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.URNUtils;
import org.marketcetera.photon.module.IModuleAttributeDefaults;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/* $License$ */

/**
 * Supports module attribute defaults using Eclipse preferences.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.1.0
 */
@ClassVersion("$Id$")
public class PreferenceAttributeDefaults implements IModuleAttributeDefaults {

	private static final String ROOT_NODE = "ModuleAttributeDefaults"; //$NON-NLS-1$

	@Override
	public synchronized String getDefaultFor(ModuleURN urn, String attribute) {
		Preferences[] nodes;
		if (urn.instanceURN()) {
			nodes = new Preferences[] {
					getNode(urn),
					getDefaultScopeNode(urn),
					getNode(urn).parent().node(INSTANCE_DEFAULTS_IDENTIFIER),
					getDefaultScopeNode(urn).parent().node(
							INSTANCE_DEFAULTS_IDENTIFIER) };
		} else {
			nodes = new Preferences[] { getNode(urn), getDefaultScopeNode(urn) };
		}
		return Platform.getPreferencesService().get(attribute, null, nodes);
	}

	@Override
	public synchronized void setDefaultFor(ModuleURN urn, String attribute,
			String value) {
		Preferences node = getNode(urn);
		node.put(attribute, value);
	}

	@Override
	public synchronized void setInstanceDefaultFor(ModuleURN urn,
			String attribute, String value) {
		try {
			URNUtils.validateProviderURN(urn);
		} catch (InvalidURNException e) {
			throw new IllegalArgumentException(urn.toString(), e);
		}
		getNode(urn).node(INSTANCE_DEFAULTS_IDENTIFIER).put(attribute, value);
	}

	@Override
	public synchronized void removeDefaultFor(ModuleURN urn, String attribute) {
		Preferences node = getNode(urn);
		node.remove(attribute);
	}

	@Override
	public synchronized void removeInstanceDefaultFor(ModuleURN urn,
			String attribute) {
		try {
			URNUtils.validateProviderURN(urn);
		} catch (InvalidURNException e) {
			throw new IllegalArgumentException(urn.toString(), e);
		}
		getNode(urn).node(INSTANCE_DEFAULTS_IDENTIFIER).remove(attribute);
	}

	private Preferences getNode(ModuleURN urn) {
		return getNode(urn, getRootNode());
	}

	private Preferences getRootNode() {
		return new InstanceScope().getNode(Activator.PLUGIN_ID).node(ROOT_NODE);
	}

	private Preferences getDefaultScopeNode(ModuleURN urn) {
		return getNode(urn, new DefaultScope().getNode(Activator.PLUGIN_ID)
				.node(ROOT_NODE));
	}

	private Preferences getNode(ModuleURN urn, Preferences baseNode) {
		Preferences node = baseNode.node(urn.providerType()).node(
				urn.providerName());
		if (urn.instanceURN()) {
			node = node.node(urn.instanceName());
		}
		return node;
	}

	@Override
	public void flush() {
		try {
			getRootNode().flush();
		} catch (BackingStoreException e) {
			Messages.PREFERENCE_ATTRIBUTE_DEFAULTS_FAILED_TO_SAVE_PREFERENCES
					.error(this, e);
		}
	}
}
