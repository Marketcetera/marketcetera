package org.rubypeople.rdt.internal.core.builder;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.compiler.BuildContext;

public class BuildContextCollector implements IResourceProxyVisitor {

	private static final String RUBY_SOURCE_CONTENT_TYPE_ID = "org.rubypeople.rdt.core.rubySource";
    private final List<BuildContext> files;
	private HashSet<String> visitedLinks;

    public BuildContextCollector(List<BuildContext> files) {
        this.files = files;
        this.visitedLinks = new HashSet<String>();
    }

    public boolean visit(IResourceProxy proxy) throws CoreException {
        switch (proxy.getType()) {
        case IResource.FILE:
            if (org.rubypeople.rdt.internal.core.util.Util.isRubyLikeFileName(proxy.getName())) {
                files.add(new BuildContext(getFile(proxy)));
                return false;
            }
            if (isERB(proxy.getName())) {
            	files.add(new ERBBuildContext(getFile(proxy)));
            	return false;
            }            
            IFile file = getFile(proxy);
            if (isRubySourceContentType(file)) {
            	 files.add(new BuildContext(file));
            	 return false;
            }
            
            // 
            return false;
        case IResource.FOLDER:
        	try { // Avoid recursive symlinks!
				IPath path = proxy.requestResource().getLocation();
				String unique = path.toFile().getCanonicalPath();
				if (visitedLinks.contains(unique))
					return false;
				visitedLinks.add(unique);
			} catch (IOException e) {
				RubyCore.log(e);
				return false;
			}
        }
        return true;
    }

	private IFile getFile(IResourceProxy proxy) {
		IResource resource;
		resource = proxy.requestResource();
		IFile file = (IFile) resource;
		return file;
	}

	public static boolean isERB(String name) {
		return name.endsWith(".erb") || name.endsWith(".rhtml");
	}

	private boolean isRubySourceContentType(IFile file) throws CoreException {
		IContentDescription contentDescription = file.getContentDescription();
		if (contentDescription != null) {
		    IContentType type = contentDescription.getContentType();
		    if (type != null)
		        if (type.getId().equals(RUBY_SOURCE_CONTENT_TYPE_ID)) 
		        	return true;
		}
		return false;
	}

}
