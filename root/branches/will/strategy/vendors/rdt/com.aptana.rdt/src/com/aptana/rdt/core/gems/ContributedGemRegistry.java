package com.aptana.rdt.core.gems;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.rubypeople.rdt.launching.RubyRuntime;

import com.aptana.rdt.AptanaRDTPlugin;

public class ContributedGemRegistry {
	
	private static Collection<Gem> fContributed;

	private ContributedGemRegistry() { }
	
	public static Collection<Gem> getContributedGems() {
		if (fContributed == null) {
			Collection<Gem> gems = new ArrayList<Gem>();
			IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
					.getExtensionPoint(AptanaRDTPlugin.PLUGIN_ID,
							AptanaRDTPlugin.EXTENSION_POINT_GEMS);
			IConfigurationElement[] configs = extensionPoint
					.getConfigurationElements();
			for (int i = 0; i < configs.length; i++) {
				IConfigurationElement element = configs[i];
				if ("gem".equals(element.getName())) { //$NON-NLS-1$
					try {
						String path = element.getAttribute("path"); //$NON-NLS-1$				
						Bundle bundle = Platform.getBundle(element
								.getContributor().getName());
						URL url = FileLocator
								.find(bundle, new Path(path), null);
						if (url == null) {
							AptanaRDTPlugin
									.log("Unable to generate a URL for the gem with path: "
											+ path
											+ " in the "
											+ element.getContributor()
													.getName() + " plugin");
							continue;
						}

						boolean install = false;
						String autoInstall = element
								.getAttribute("auto-install"); //$NON-NLS-1$
						if (autoInstall != null
								&& autoInstall.trim().length() > 0) {
							install = Boolean.parseBoolean(autoInstall);
						}
						if (!install)
							continue;

						String name = element.getAttribute("name"); //$NON-NLS-1$
						LocalFileGem gem = null;
						if (name == null || name.trim().length() == 0)
							gem = LocalFileGem.create(url);
						else {
							String version = element.getAttribute("version"); //$NON-NLS-1$
							if (version == null)
								version = "";
							String platform = element.getAttribute("platform"); //$NON-NLS-1$
							if (platform == null
									|| platform.trim().length() == 0)
								platform = Gem.RUBY_PLATFORM;
							gem = new LocalFileGem(url, name, version, "",
									platform);
						}
						String compiles = element.getAttribute("compiles");
						gem.setCompiles(Boolean.parseBoolean(compiles));

						IConfigurationElement[] dependencies = element
								.getChildren("dependency"); //$NON-NLS-1$
						for (int j = 0; j < dependencies.length; j++) {
							String dependency = dependencies[j]
									.getAttribute("name"); //$NON-NLS-1$
							gem.addDependency(dependency);
						}
						gems.add(gem);
					} catch (InvalidRegistryObjectException e) {
						AptanaRDTPlugin.log(e);
					}
				}
			}
			fContributed = gems;
		}
		return fContributed;
	}

	public static Gem getGem(String name) {
		Collection<Gem> gems = filterByPlatform(getContributedGems());
		for (Gem gem : gems) {
			if (gem.getName().equals(name)) { // TODO Find latest version of gem
				return gem;
			}
		}
		return null;
	}
	
	public static Collection<Gem> filterByPlatform(Collection<Gem> gems) {
		Map<String, Gem> map = new HashMap<String, Gem>();
		for (Gem gem : gems) {
			if (map.containsKey(gem.getName())) {
				// If we're using JRuby and this is the JRuby specific gem, use
				// it
				if (RubyRuntime.currentVMIsJRuby()
						&& gem.getPlatform().equals(Gem.JRUBY_PLATFORM)) {
					map.put(gem.getName(), gem);
				}
				// if we're on windows, not using JRuby, and this is the windows
				// specific gem, use it
				if (!RubyRuntime.currentVMIsJRuby()
						&& Platform.getOS().equals(Platform.OS_WIN32)
						&& gem.getPlatform().equals(Gem.MSWIN32_PLATFORM)) {
					// Don't use mswin32 if we're running a cygwin VM
					if (RubyRuntime.currentVMIsCygwin())
						continue;
					map.put(gem.getName(), gem);
				}
			} else {
				// Dont't install a windows gem on a non windows platform or on
				// cygwin interpreter
				if (gem.getPlatform().equals(Gem.MSWIN32_PLATFORM)) {
					if (!Platform.getOS().equals(Platform.OS_WIN32))
						continue;
					if (RubyRuntime.currentVMIsJRuby())
						continue;
					if (RubyRuntime.currentVMIsCygwin())
						continue;
				}
				// Don't install jruby gems unless we're running a JRuby VM
				if (!RubyRuntime.currentVMIsJRuby()
						&& gem.getPlatform().equals(Gem.JRUBY_PLATFORM))
					continue;
				// Don't install non java gems that have to compile native code
				if (gem instanceof LocalFileGem) {
					LocalFileGem localGem = (LocalFileGem) gem;
					if (RubyRuntime.currentVMIsJRuby()
							&& !gem.getPlatform().equals(Gem.JRUBY_PLATFORM)
							&& localGem.compiles())
						continue;
				}
				map.put(gem.getName(), gem);
			}
		}
		return map.values();
	}
	
	public static List<Gem> sortByDependency(Collection<Gem> gems) {
		List<Gem> sorted = new ArrayList<Gem>();
		while (sorted.size() < gems.size()) {
			for (Gem gem : gems) {
				if (sorted.size() == gems.size())
					return sorted; // shortcut, when we're done
				if (sorted.contains(gem))
					continue; // already in sorted list, skip it
				boolean add = true;
				if (gem instanceof LocalFileGem) {
					LocalFileGem local = (LocalFileGem) gem;
					Set<String> dependencies = local.getDependencies();
					for (String dependency : dependencies) {
						if (getGemManager().gemInstalled(dependency))
							continue; // already installed in system, so we're
										// ok
						if (!contains(sorted, dependency)) { // if it's not
																// ahead of us
																// in list to be
																// installed,
																// skip us for
																// now
							add = false;
							if (!contains(gems, dependency)) { // it's not
																// installed,
																// ahead of us,
																// or available
																// - we're
																// screwed,
																// remove this
																// gem from list
																// to be
																// installed
								AptanaRDTPlugin
										.log("Unable to install "
												+ local.toString()
												+ " because we were unable to satisfy it's dependencies!");
								gems.remove(gem);
							}
							break;
						}
					}
				}
				if (add)
					sorted.add(gem);
			}
		}
		return sorted;
	}
	
	private static boolean contains(Collection<Gem> gems, String name) {
		for (Gem gem : gems) {
			if (gem.getName().equals(name))
				return true;
		}
		return false;
	}

	private static IGemManager getGemManager() {
		return AptanaRDTPlugin.getDefault().getGemManager();
	}
}
