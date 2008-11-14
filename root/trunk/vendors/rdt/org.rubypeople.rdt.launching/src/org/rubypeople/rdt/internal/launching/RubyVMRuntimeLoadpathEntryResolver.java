/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.launching;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.launching.IRuntimeLoadpathEntry;
import org.rubypeople.rdt.launching.IRuntimeLoadpathEntryResolver;
import org.rubypeople.rdt.launching.IRuntimeLoadpathEntryResolver2;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.RubyRuntime;

/**
 * Resolves for RUBYLIB_VARIABLE and RUBY_CONTAINER
 */
public class RubyVMRuntimeLoadpathEntryResolver implements IRuntimeLoadpathEntryResolver2 {
	
	/**
	 * @see IRuntimeLoadpathEntryResolver#resolveRuntimeLoadpathEntry(IRuntimeLoadpathEntry, ILaunchConfiguration)
	 */
	public IRuntimeLoadpathEntry[] resolveRuntimeLoadpathEntry(IRuntimeLoadpathEntry entry, ILaunchConfiguration configuration) throws CoreException {
		IVMInstall rubyVM = null;
		if (entry.getType() == IRuntimeLoadpathEntry.CONTAINER && entry.getPath().segmentCount() > 1) {
			// a specific VM
			rubyVM = RubyContainerInitializer.resolveInterpreter(entry.getPath()); 
		} else {
			// default VM for config
			rubyVM = RubyRuntime.computeVMInstall(configuration);
		}
		if (rubyVM == null) {
			// cannot resolve Ruby VM
			return new IRuntimeLoadpathEntry[0];
		}
		return resolveLibraryLocations(rubyVM, entry.getLoadpathProperty());
	}
	
	/**
	 * @see IRuntimeLoadpathEntryResolver#resolveRuntimeLoadpathEntry(IRuntimeLoadpathEntry, IRubyProject)
	 */
	public IRuntimeLoadpathEntry[] resolveRuntimeLoadpathEntry(IRuntimeLoadpathEntry entry, IRubyProject project) throws CoreException {
		IVMInstall rubyVM = null;
		if (entry.getType() == IRuntimeLoadpathEntry.CONTAINER && entry.getPath().segmentCount() > 1) {
			// a specific VM
			rubyVM = RubyContainerInitializer.resolveInterpreter(entry.getPath()); 
		} else {
			// default VM for project
			rubyVM = RubyRuntime.getVMInstall(project);
		}
		if (rubyVM == null) {
			// cannot resolve RubyVM
			return new IRuntimeLoadpathEntry[0];
		}		
		return resolveLibraryLocations(rubyVM, entry.getLoadpathProperty());
	}

	/**
	 * Resolves libray locations for the given VM install
	 */
	protected IRuntimeLoadpathEntry[] resolveLibraryLocations(IVMInstall vm, int kind) {
		IPath[] libs = vm.getLibraryLocations();
		IPath[] defaultLibs = vm.getVMInstallType().getDefaultLibraryLocations(vm.getInstallLocation());
		boolean overrideRubydoc = false;
		if (libs == null) {
			// default system libs
			libs = defaultLibs;
			overrideRubydoc = true;
		} else if (!isSameArchives(libs, defaultLibs)) {
			// determine if bootpath should be explicit
			kind = IRuntimeLoadpathEntry.BOOTSTRAP_CLASSES;
		}		
		if (kind == IRuntimeLoadpathEntry.BOOTSTRAP_CLASSES) {
			File vmInstallLocation= vm.getInstallLocation();
			if (vmInstallLocation != null) {
				LibraryInfo libraryInfo= LaunchingPlugin.getLibraryInfo(vm.getVMInstallType(), vmInstallLocation.getAbsolutePath());
				if (libraryInfo != null) {
					// only return endorsed and bootstrap loadpath entries if we have the info
					// libs in the ext dirs are not loaded by the boot class loader
					
					List<IRuntimeLoadpathEntry> resolvedEntries = new ArrayList<IRuntimeLoadpathEntry>(libs.length);
					for (int i = 0; i < libs.length; i++) {
						IPath location = libs[i];
						IPath libraryPath = location;
						String dir = libraryPath.toFile().getParent();
						resolvedEntries.add(resolveLibraryLocation(vm, location, kind, overrideRubydoc));
					}
					return resolvedEntries.toArray(new IRuntimeLoadpathEntry[resolvedEntries.size()]);
				}
			}
		}
		List<IRuntimeLoadpathEntry> resolvedEntries = new ArrayList<IRuntimeLoadpathEntry>(libs.length);
		for (int i = 0; i < libs.length; i++) {
			IPath systemLibraryPath = libs[i];
			if (systemLibraryPath.toFile().exists()) {
				resolvedEntries.add(resolveLibraryLocation(vm, libs[i], kind, overrideRubydoc));
			}
		}
		return resolvedEntries.toArray(new IRuntimeLoadpathEntry[resolvedEntries.size()]);
	}
		
	/**
	 * Return whether the given list of libraries refer to the same archives in the same
	 * order. Only considers the binary archive (not source or javadoc locations). 
	 *  
	 * @param libs
	 * @param defaultLibs
	 * @return whether the given list of libraries refer to the same archives in the same
	 * order
	 */
	public static boolean isSameArchives(IPath[] libs, IPath[] defaultLibs) {
		if (libs.length != defaultLibs.length) {
			return false;
		}
		for (int i = 0; i < defaultLibs.length; i++) {
			IPath def = defaultLibs[i];
			IPath lib = libs[i];
			if (!def.equals(lib)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @see IRuntimeLoadpathEntryResolver#resolveVMInstall(ILoadpathEntry)
	 */
	public IVMInstall resolveVMInstall(ILoadpathEntry entry) {
		switch (entry.getEntryKind()) {
			case ILoadpathEntry.CPE_VARIABLE:
				if (entry.getPath().segment(0).equals(RubyRuntime.RUBYLIB_VARIABLE)) {
					return RubyRuntime.getDefaultVMInstall();
				}
				break;
			case ILoadpathEntry.CPE_CONTAINER:
				if (entry.getPath().segment(0).equals(RubyRuntime.RUBY_CONTAINER)) {
					return RubyContainerInitializer.resolveInterpreter(entry.getPath());
				}
				break;
			default:
				break;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntryResolver2#isVMInstallReference(org.eclipse.jdt.core.ILoadpathEntry)
	 */
	public boolean isVMInstallReference(ILoadpathEntry entry) {
		switch (entry.getEntryKind()) {
			case ILoadpathEntry.CPE_VARIABLE:
				if (entry.getPath().segment(0).equals(RubyRuntime.RUBYLIB_VARIABLE)) {
					return true;
				}
				break;
			case ILoadpathEntry.CPE_CONTAINER:
				if (entry.getPath().segment(0).equals(RubyRuntime.RUBY_CONTAINER)) {
					return true;
				}
				break;
			default:
				break;
		}
		return false;
	}
	
	/**
	 * Returns a runtime classpath entry for the given library in the specified VM.
	 * 
	 * @param vm
	 * @param location
	 * @param kind
	 * @return runtime classpath entry
	 * @since 0.9.0
	 */
	private IRuntimeLoadpathEntry resolveLibraryLocation(IVMInstall vm, IPath location, int kind, boolean overrideRubyDoc) {
		IPath libraryPath = location;				
		ILoadpathEntry cpe = RubyCore.newLibraryEntry(libraryPath, false);
		IRuntimeLoadpathEntry resolved = new RuntimeLoadpathEntry(cpe);
		resolved.setLoadpathProperty(kind);
		return resolved;
	}

}
