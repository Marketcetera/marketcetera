package org.rubypeople.rdt.internal.core.search;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.core.Openable;
import org.rubypeople.rdt.internal.core.RubyModel;
import org.rubypeople.rdt.internal.core.RubyModelManager;
import org.rubypeople.rdt.internal.core.SourceFolderRoot;
import org.rubypeople.rdt.internal.core.util.CharOperation;
import org.rubypeople.rdt.internal.core.util.HashtableOfArrayToObject;
import org.rubypeople.rdt.internal.core.util.Util;

public class HandleFactory {
	
	/**
	 * Cache package fragment root information to optimize speed performance.
	 */
	private String lastSrcFolderRootPath;
	private ISourceFolderRoot lastSrcFolderRoot;

	/**
	 * Cache package handles to optimize memory.
	 */
	private HashtableOfArrayToObject folderHandles;
	
	private RubyModel rubyModel;

	public HandleFactory() {
		this.rubyModel = RubyModelManager.getRubyModelManager().getRubyModel();
	}

	/**
	 * Returns the source folder root that contains the given resource path.
	 */
	private ISourceFolderRoot getSourceFolderRoot(String pathString) {

		IPath path = new Path(pathString);
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i = 0, max = projects.length; i < max; i++) {
			try {
				IProject project = projects[i];
				if (!project.isAccessible() || !project.hasNature(RubyCore.NATURE_ID))
					continue;
				IRubyProject rubyProject = this.rubyModel.getRubyProject(project);
				ISourceFolderRoot[] roots = rubyProject.getSourceFolderRoots();
				for (int j = 0, rootCount = roots.length; j < rootCount; j++) {
					SourceFolderRoot root = (SourceFolderRoot) roots[j];
					if (root.getPath().isPrefixOf(path) && !Util.isExcluded(path, root.fullInclusionPatternChars(), root.fullExclusionPatternChars(), false)) {
						return root;
					}
				}
			} catch (CoreException e) {
				// CoreException from hasNature - should not happen since we
				// check that the project is accessible
				// RubyModelException from getPackageFragmentRoots - a problem
				// occured while accessing project: nothing we can do, ignore
			}
		}
		return null;
	}

	/**
	 * Creates an Openable handle from the given resource path. The resource
	 * path can be a path to a file in the workbench.
	 */
	public Openable createOpenable(String resourcePath) {
		// path to a file in a directory
		// Optimization: cache source folder root handle and package handles
		int rootPathLength = -1;
		if (this.lastSrcFolderRootPath == null || !(resourcePath.startsWith(this.lastSrcFolderRootPath) && (rootPathLength = this.lastSrcFolderRootPath.length()) > 0 && resourcePath.charAt(rootPathLength) == '/')) {
			ISourceFolderRoot root = this.getSourceFolderRoot(resourcePath);
			if (root == null)
				return null; // match is outside loadpath
			this.lastSrcFolderRoot = root;
			this.lastSrcFolderRootPath = this.lastSrcFolderRoot.getPath().toString();
			this.folderHandles = new HashtableOfArrayToObject(5);
		}
		// create handle
		resourcePath = resourcePath.substring(this.lastSrcFolderRootPath.length() + 1);
		String[] simpleNames = new Path(resourcePath).segments();
		String[] pkgName;
		int length = simpleNames.length - 1;
		if (length > 0) {
			pkgName = new String[length];
			System.arraycopy(simpleNames, 0, pkgName, 0, length);
		} else {
			pkgName = CharOperation.NO_STRINGS;
		}
		ISourceFolder pkgFragment = (ISourceFolder) this.folderHandles.get(pkgName);
		if (pkgFragment == null) {
			pkgFragment = ((SourceFolderRoot) this.lastSrcFolderRoot).getSourceFolder(pkgName);
			this.folderHandles.put(pkgName, pkgFragment);
		}
		String simpleName = simpleNames[length];
		if (org.rubypeople.rdt.internal.core.util.Util.isRubyOrERBLikeFileName(simpleName)) {
			IRubyScript unit = pkgFragment.getRubyScript(simpleName);
			return (Openable) unit;
		}
		return null;
	}
}
