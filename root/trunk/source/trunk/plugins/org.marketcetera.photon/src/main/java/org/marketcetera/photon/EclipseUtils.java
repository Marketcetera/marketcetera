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
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

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

	/**
	 * Determines the size of a text area based on possible hints.  If there is a 
	 * charWidthHint greater than 0, that will be used to calcualate the width,
	 * otherwise the defaultString will be used.
	 * 
	 * @param aComposite 
	 * @param defaultString
	 * @param charWidthHint
	 * @param heightFactorHint
	 * @return
	 */
	public static Point getTextAreaSize(Control aComposite, String defaultString, int charWidthHint, double heightFactorHint) {
		Point sizeHint = new Point(0,0);
		GC gc = new GC(aComposite);
		gc.setFont(aComposite.getFont());
		FontMetrics fm = gc.getFontMetrics();
		if (charWidthHint > 0) {
			int averageCharWidth = fm.getAverageCharWidth();
			sizeHint.x = averageCharWidth * charWidthHint;
		} else {
			Point extent = gc.textExtent(defaultString);
			sizeHint.x = extent.x;
		}
		sizeHint.y = (int) (fm.getHeight() * heightFactorHint);
		gc.dispose();
		return sizeHint;
	}

}
