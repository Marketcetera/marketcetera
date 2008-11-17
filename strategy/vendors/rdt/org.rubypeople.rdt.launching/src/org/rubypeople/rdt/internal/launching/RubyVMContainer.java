package org.rubypeople.rdt.internal.launching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.core.ILoadpathContainer;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IVMInstallChangedListener;
import org.rubypeople.rdt.launching.PropertyChangeEvent;
import org.rubypeople.rdt.launching.RubyRuntime;

public class RubyVMContainer implements ILoadpathContainer {

	private static Map fgLoadpathEntries;
	private IVMInstall fInterpreter;
	private IPath fPath;

	public RubyVMContainer(IVMInstall interpreter, IPath path) {
		fInterpreter = interpreter;
		fPath = path;
	}

	public String getDescription() {
		return "Ruby System Library";
	}

	public ILoadpathEntry[] getLoadpathEntries() {
		return getLoadpathEntries(fInterpreter);
	}
	
	/**
	 * Returns the loadpath entries associated with the given VM.
	 * 
	 * @param vm
	 * @return loadpath entries
	 */
	private static ILoadpathEntry[] getLoadpathEntries(IVMInstall vm) {
		if (fgLoadpathEntries == null) {
			fgLoadpathEntries = new HashMap(10);
			// add a listener to clear cached value when a VM changes or is removed
			IVMInstallChangedListener listener = new IVMInstallChangedListener() {
				public void defaultVMInstallChanged(IVMInstall previous, IVMInstall current) {
				}

				public void vmChanged(PropertyChangeEvent event) {
					if (event.getSource() != null) {
						fgLoadpathEntries.remove(event.getSource());
					}
				}

				public void vmAdded(IVMInstall newVm) {
				}

				public void vmRemoved(IVMInstall removedVm) {
					fgLoadpathEntries.remove(removedVm);
				}
			};
			RubyRuntime.addVMInstallChangedListener(listener);
		}
		ILoadpathEntry[] entries = (ILoadpathEntry[])fgLoadpathEntries.get(vm);
		if (entries == null) {
			entries = computeLoadpathEntries(vm);
			fgLoadpathEntries.put(vm, entries);
		}
		return entries;
	}
	
	/**
	 * Computes the loadpath entries associated with a VM - one entry per library.
	 * 
	 * @param vm
	 * @return loadpath entries
	 */
	private static ILoadpathEntry[] computeLoadpathEntries(IVMInstall vm) {
		IPath[] libs = vm.getLibraryLocations();
		if (libs == null) {
			libs = RubyRuntime.getLibraryLocations(vm);
		}
		List entries = new ArrayList(libs.length);
		for (int i = 0; i < libs.length; i++) {
			entries.add(RubyCore.newLibraryEntry(libs[i], false));
		}
		return (ILoadpathEntry[])entries.toArray(new ILoadpathEntry[entries.size()]);		
	}
	
	/**
	 * @see ILoadpathContainer#getKind()
	 */
	public int getKind() {
		return ILoadpathContainer.K_DEFAULT_SYSTEM;
	}

	/**
	 * @see ILoadpathContainer#getPath()
	 */
	public IPath getPath() {
		return fPath;
	}
}
