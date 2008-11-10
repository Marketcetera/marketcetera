package org.rubypeople.rdt.internal.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModelStatusConstants;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.util.CharOperation;
import org.rubypeople.rdt.internal.core.util.Util;

public class ExternalSourceFolderRoot extends SourceFolderRoot implements ISourceFolderRoot {

	public final static ArrayList EMPTY_LIST = new ArrayList();

	protected final IPath folderPath;

	protected ExternalSourceFolderRoot(IPath resource, RubyProject project) {
		super(null, project);
		this.folderPath = resource;
	}
	
	public String getElementName() {
		return this.folderPath.toPortableString();
	}
	
	/**
	 * Returns an array of non-ruby resources contained in the receiver.
	 */
	public Object[] getNonRubyResources() throws RubyModelException {
		// We want to show non ruby resources of the default src folder at the root (see PR #1G58NB8)
		return ((ExternalSourceFolder) getSourceFolder(CharOperation.NO_STRINGS)).storedNonRubyResources();
	}

	@Override
	protected boolean computeChildren(OpenableElementInfo info, Map newElements) throws RubyModelException {
		try {
			// the underlying resource may be a folder or a project (in the case
			// that the project folder
			// is actually the source folder root)
			Object target = RubyModel.getTarget(ResourcesPlugin.getWorkspace().getRoot(), this.folderPath, false);
			if (target instanceof File) {
				ArrayList vChildren = new ArrayList(5);
				computeFolderChildren((File) target, CharOperation.NO_STRINGS, vChildren);
				IRubyElement[] children = new IRubyElement[vChildren.size()];
				vChildren.toArray(children);
				info.setChildren(children);
				
				// Now go through every SourceFolder and set it's children!
				for (int i = 0; i < children.length; i++) {
					ExternalSourceFolder packFrag = (ExternalSourceFolder) children[i];
				    ExternalSourceFolderInfo fragInfo = new ExternalSourceFolderInfo();
				    packFrag.computeChildren(fragInfo);
					newElements.put(packFrag, fragInfo);
				}
				
			}
		} catch (RubyModelException e) {
			// problem resolving children; structure remains unknown
			info.setChildren(new IRubyElement[] {});
			throw e;
		}
		return true;
	}

	protected void computeFolderChildren(File folder, String[] pkgName, ArrayList vChildren) throws RubyModelException {
		ISourceFolder pkg = getSourceFolder(pkgName);
		vChildren.add(pkg);

		try {
			RubyModelManager manager = RubyModelManager.getRubyModelManager();
			File[] members = folder.listFiles();		
			if (members == null) return;
			for (int i = 0, max = members.length; i < max; i++) {
				File member = members[i];
				String memberName = member.getName();
				if (member.isDirectory()) {
					String[] newNames = Util.arrayConcat(pkgName, manager.intern(memberName));
					computeFolderChildren(member, newNames, vChildren);
				} else if (member.isFile()) {
					// do nothing
				}
			}
		} catch (IllegalArgumentException e) {
			throw new RubyModelException(e, IRubyModelStatusConstants.ELEMENT_DOES_NOT_EXIST); // could
																								// be
																								// thrown
																								// by
																								// ElementTree
																								// when
																								// path
																								// is
																								// not
																								// found
		} catch (CoreException e) {
			throw new RubyModelException(e);
		}
	}
	
	public SourceFolder getSourceFolder(String[] pkgName) {
		return new ExternalSourceFolder(this, pkgName);
	}

	@Override
	public IPath getPath() {
		return folderPath;
	}

	@Override
	public boolean isExternal() {
		return true;
	}

	public int hashCode() {
		return this.folderPath.hashCode();
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	/**
	 * Returns true if this handle represents the same folder as the given
	 * handle.
	 * 
	 * @see Object#equals
	 */
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof ExternalSourceFolderRoot) {
			ExternalSourceFolderRoot other = (ExternalSourceFolderRoot) o;
			return this.folderPath.equals(other.folderPath);
		}
		return false;
	}

	/**
	 * @see IRubyElement
	 */
	public IResource getUnderlyingResource() throws RubyModelException {
		if (isExternal()) {
			if (!exists())
				throw newNotPresentException();
			return null;
		}
		return super.getUnderlyingResource();
	}

	/**
	 * Returns a new element info for this element.
	 */
	protected Object createElementInfo() {
		return new ExternalSourceFolderRootInfo();
	}

	public IResource getResource() {
		if (this.resource == null) {
			this.resource = RubyModel.getTarget(ResourcesPlugin.getWorkspace().getRoot(), this.folderPath, false);
		}
		// FIXME We need to turn this File into an IResource somehow!
		if (this.resource instanceof IResource) {
			return super.getResource();
		}
		return null;
	}
	
	@Override
	protected IStatus validateOnLoadpath() { // FIXME This is a HACK. Override so all external roots are said to be on loadpath. This is done so opening external file through File > Open File.. shows content in outline page.
		return Status.OK_STATUS;
	}

	protected boolean resourceExists() {
		if (this.isExternal()) {
			return RubyModel.getTarget(ResourcesPlugin.getWorkspace().getRoot(), this.getPath(), // don't
																									// make
																									// the
																									// path
																									// relative
																									// as
																									// this
																									// is
																									// an
																									// external
																									// archive
					true) != null;
		}
		return super.resourceExists();
	}
}
