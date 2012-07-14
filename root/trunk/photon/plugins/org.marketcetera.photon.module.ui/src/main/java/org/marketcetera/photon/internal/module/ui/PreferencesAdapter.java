package org.marketcetera.photon.internal.module.ui;

import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.module.IModuleAttributeDefaults;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/* $License$ */

/**
 * Adapts the module preferences provided by the
 * <code>org.marketcetera.photon.module</code> plug-in to a
 * {@link PropertiesTree} suitable for {@link ModulePropertiesPreferencePage}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.1.0
 */
@ClassVersion("$Id$")
public class PreferencesAdapter {

	private static final String ROOT_NODE = "ModuleAttributeDefaults"; //$NON-NLS-1$

	private static final String MODULE_PLUGIN_ID = "org.marketcetera.photon.module"; //$NON-NLS-1$

	private final IModuleAttributeDefaults mSupport;

	/**
	 * Constructor.
	 * 
	 * @param support
	 *            support for modify attribute defaults
	 */
	public PreferencesAdapter(IModuleAttributeDefaults support) {
		this.mSupport = support;
	}

	public PropertiesTree toTree() {
		final PropertiesTree defaults = new PropertiesTree();
		IVisitor visitor = new IVisitor() {
			@Override
			public void visitNode(Preferences node, String key,
					String combinedKey) {
				defaults.put(combinedKey, node.get(key, null));
			}
		};
		visit(new DefaultScope().getNode(MODULE_PLUGIN_ID).node(ROOT_NODE),
				visitor);
		visit(new InstanceScope().getNode(MODULE_PLUGIN_ID).node(ROOT_NODE),
				visitor);
		return defaults;
	}

	public void fromTree(final PropertiesTree defaults) {
		for (Map.Entry<String, String> entry : defaults.entrySet()) {
			String[] parts = entry.getKey().split("\\."); //$NON-NLS-1$
			String value = entry.getValue();
			ModuleURN providerURN = new ModuleURN(MessageFormat.format(
					"metc:{0}:{1}", parts[0], parts[1])); //$NON-NLS-1$
			if (parts.length == 3) {
				mSupport.setDefaultFor(providerURN, parts[2], value);
			} else if (parts.length == 4) {
				if (parts[2]
						.equals(IModuleAttributeDefaults.INSTANCE_DEFAULTS_IDENTIFIER)) {
					mSupport
							.setInstanceDefaultFor(providerURN, parts[3], value);
				} else {
					mSupport.setDefaultFor(
							new ModuleURN(providerURN, parts[2]), parts[3],
							value);
				}
			}
		}
		visit(new InstanceScope().getNode(MODULE_PLUGIN_ID).node(ROOT_NODE),
				new IVisitor() {
					@Override
					public void visitNode(Preferences node, String key,
							String combinedKey) {
						if (!defaults.containsKey(combinedKey)) {
							node.remove(key);
						}
					}
				});
	}

	private void visit(Preferences rootNode, IVisitor visitor) {
		try {
			for (String providerType : rootNode.childrenNames()) {
				// cheat for now - exclude mdata and remote since there is custom UI
				if (providerType.equals("mdata") || providerType.equals("remote")) { //$NON-NLS-1$
					continue;
				}
				Preferences providerTypeNode = rootNode.node(providerType);
				for (String provider : providerTypeNode.childrenNames()) {
					Preferences providerNode = providerTypeNode.node(provider);
					for (String key : providerNode.keys()) {
						visitor.visitNode(providerNode, key, MessageFormat
								.format("{0}.{1}.{2}", providerType, provider, //$NON-NLS-1$
										key));
					}
					for (String instance : providerNode.childrenNames()) {
						Preferences node = providerNode.node(instance);
						for (String key : node.keys()) {
							visitor.visitNode(node, key, MessageFormat.format(
									"{0}.{1}.{2}.{3}", providerType, provider, //$NON-NLS-1$
									instance, key));
						}
					}
				}
			}
		} catch (BackingStoreException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Internal interface for walking the tree.
	 *
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since 1.1.0
	 */
	@ClassVersion("$Id$")
	private static interface IVisitor {
		void visitNode(Preferences node, String key, String combinedKey);
	}
}
