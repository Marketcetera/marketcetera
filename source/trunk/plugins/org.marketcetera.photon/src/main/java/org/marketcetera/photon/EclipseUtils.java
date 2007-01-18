package org.marketcetera.photon;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;

public class EclipseUtils {
	
	public static IPath getWorkspacePath()
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath fullPath = workspace.getRoot().getRawLocation();
		return fullPath;
	}

	public static IProject[] getProjects(){
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		return root.getProjects();
	}
	
	public static IPath getPluginPath(Plugin plugin){
		URL pluginBaseURL = plugin.getBundle().getEntry("/");
		URL fileURL = null;
		try {
			fileURL = FileLocator.toFileURL(pluginBaseURL);
		} catch (IOException e) {
			return null;
		}
		return Path.fromOSString(fileURL.getFile());
	}

}
