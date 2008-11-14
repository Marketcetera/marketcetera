/*
 * Created on Jan 12, 2005
 *
 */
package org.rubypeople.rdt.internal.ui.text.folding;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.text.folding.IRubyFoldingStructureProvider;

/**
 * @author cawilliams
 * 
 */
public class RubyFoldingStructureProviderRegistry {

    private static final String EXTENSION_POINT = "foldingStructureProviders"; //$NON-NLS-1$

    /** The map of descriptors, indexed by their identifiers. */
    private Map fDescriptors;

    /**
     * Creates a new instance.
     */
    public RubyFoldingStructureProviderRegistry() {
    }

    /**
     * Returns an array of <code>IRubyFoldingProviderDescriptor</code>
     * describing all extension to the <code>foldingProviders</code> extension
     * point.
     * 
     * @return the list of extensions to the
     *         <code>quickDiffReferenceProvider</code> extension point.
     */
    public RubyFoldingStructureProviderDescriptor[] getFoldingProviderDescriptors() {
        synchronized (this) {
            ensureRegistered();
            return (RubyFoldingStructureProviderDescriptor[]) fDescriptors.values().toArray(
                    new RubyFoldingStructureProviderDescriptor[fDescriptors.size()]);
        }
    }

    /**
     * Returns the folding provider with identifier <code>id</code> or
     * <code>null</code> if no such provider is registered.
     * 
     * @param id
     *            the identifier for which a provider is wanted
     * @return the corresponding provider, or <code>null</code> if none can be
     *         found
     */
    public RubyFoldingStructureProviderDescriptor getFoldingProviderDescriptor(String id) {
        synchronized (this) {
            ensureRegistered();
            return (RubyFoldingStructureProviderDescriptor) fDescriptors.get(id);
        }
    }

    /**
     * Instantiates and returns the provider that is currently configured in the
     * preferences.
     * 
     * @return the current provider according to the preferences
     */
    public IRubyFoldingStructureProvider getCurrentFoldingProvider() {
        String id = RubyPlugin.getDefault().getPreferenceStore().getString(
                PreferenceConstants.EDITOR_FOLDING_PROVIDER);
        RubyFoldingStructureProviderDescriptor desc = getFoldingProviderDescriptor(id);
        if (desc != null) {
            try {
                return desc.createProvider();
            } catch (CoreException e) {
                RubyPlugin.log(e);
            }
        }
        return null;
    }

    /**
     * Ensures that the extensions are read and stored in
     * <code>fDescriptors</code>.
     */
    private void ensureRegistered() {
        if (fDescriptors == null) reloadExtensions();
    }

    /**
     * Reads all extensions.
     * <p>
     * This method can be called more than once in order to reload from a
     * changed extension registry.
     * </p>
     */
    public void reloadExtensions() {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        Map map = new HashMap();

        IConfigurationElement[] elements = registry.getConfigurationElementsFor(RubyPlugin
                .getPluginId(), EXTENSION_POINT);
        for (int i = 0; i < elements.length; i++) {
            RubyFoldingStructureProviderDescriptor desc = new RubyFoldingStructureProviderDescriptor(
                    elements[i]);
            map.put(desc.getId(), desc);
        }

        synchronized (this) {
            fDescriptors = Collections.unmodifiableMap(map);
        }
    }

}
