package org.rubypeople.rdt.internal.core;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModelStatusConstants;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;
import org.rubypeople.rdt.internal.core.util.CharOperation;
import org.rubypeople.rdt.internal.core.util.MementoTokenizer;
import org.rubypeople.rdt.internal.core.util.Util;

public class SourceFolderRoot extends Openable implements ISourceFolderRoot {

	/**
	 * The resource associated with this root.
	 * (an IResource or a java.io.File (for external libraries only))
	 */
	protected Object resource;
	
	/**
	 * Constructs a package fragment root which is the root of the ruby package
	 * directory hierarchy.
	 */
	protected SourceFolderRoot(IResource resource, RubyProject project) {
		super(project);
		this.resource = resource;
	}
	
	/**
	 * @see IParent 
	 */
	public boolean hasChildren() throws RubyModelException {
		// a source folder root always has the default location (itself) as a child
		return true;
	}
	
	@Override
	protected boolean buildStructure(OpenableElementInfo info,
			IProgressMonitor pm, Map newElements, IResource underlyingResource)
			throws RubyModelException {
		
		// check whether this source folder root can be opened
		IStatus status = validateOnLoadpath();
		if (!status.isOK()) throw newRubyModelException(status);
		if (!resourceExists()) throw newNotPresentException();

		return computeChildren(info, newElements);
	}
	
	/**
	 * Compares two objects for equality;
	 * for <code>PackageFragmentRoot</code>s, equality is having the
	 * same parent, same resources, and occurrence count.
	 *
	 */
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof SourceFolderRoot))
			return false;
		SourceFolderRoot other = (SourceFolderRoot) o;
		return this.resource.equals(other.resource) && 
				this.parent.equals(other.parent);
	}

	/*
	 * Validate whether this package fragment root is on the classpath of its project.
	 */
	protected IStatus validateOnLoadpath() {
		
		IPath path = this.getPath();
		try {
			// check package fragment root on classpath of its project
			RubyProject project = (RubyProject) getRubyProject();
			ILoadpathEntry[] classpath = project.getResolvedLoadpath(true/*ignoreUnresolvedEntry*/, false/*don't generateMarkerOnError*/, false/*don't returnResolutionInProgress*/);	
			for (int i = 0, length = classpath.length; i < length; i++) {
				ILoadpathEntry entry = classpath[i];
				if (entry.getPath().equals(path)) {
					return Status.OK_STATUS;
				}
			}
		} catch(RubyModelException e){
			// could not read classpath, then assume it is outside
			return e.getRubyModelStatus();
		}
		return new RubyModelStatus(IRubyModelStatusConstants.ELEMENT_NOT_ON_CLASSPATH, this);
	}
	
	/*
	 * Returns the exclusion patterns from the classpath entry associated with this root.
	 */
	public char[][] fullExclusionPatternChars() {
		try {
//			if (this.isOpen() && this.getKind() != ISourceFolderRoot.K_SOURCE) return null;
			LoadpathEntry entry = (LoadpathEntry)getRawLoadpathEntry();
			if (entry == null) {
				return null;
			} else {
				return entry.fullExclusionPatternChars();
			}
		} catch (RubyModelException e) { 
			return null;
		}
	}		

	public ILoadpathEntry getRawLoadpathEntry() throws RubyModelException {
		ILoadpathEntry rawEntry = null;
		RubyProject project = (RubyProject)this.getRubyProject();
		project.getResolvedLoadpath(true/*ignoreUnresolvedEntry*/, false/*don't generateMarkerOnError*/, false/*don't returnResolutionInProgress*/); // force the reverse rawEntry cache to be populated
		RubyModelManager.PerProjectInfo perProjectInfo = project.getPerProjectInfo();
		if (perProjectInfo != null && perProjectInfo.resolvedPathToRawEntries != null) {
			rawEntry = (ILoadpathEntry) perProjectInfo.resolvedPathToRawEntries.get(this.getPath());
		}
		return rawEntry;
	}

	/*
	 * Returns the inclusion patterns from the classpath entry associated with this root.
	 */
	public char[][] fullInclusionPatternChars() {
		try {
//			if (this.isOpen() && this.getKind() != ISourceFolderRoot.K_SOURCE) return null;
			LoadpathEntry entry = (LoadpathEntry)getRawLoadpathEntry();
			if (entry == null) {
				return null;
			} else {
				return entry.fullInclusionPatternChars();
			}
		} catch (RubyModelException e) { 
			return null;
		}
	}		
	
	/**
	 * Compute the source folder children of this source folder root.
	 * 
	 * @exception RubyModelException  The resource associated with this source folder root does not exist
	 */
	protected boolean computeChildren(OpenableElementInfo info, Map newElements) throws RubyModelException {
		try {
			// the underlying resource may be a folder or a project (in the case that the project folder
			// is actually the source folder root)
			IResource underlyingResource = getResource();
			if (underlyingResource.getType() == IResource.FOLDER || underlyingResource.getType() == IResource.PROJECT) {
				ArrayList vChildren = new ArrayList(5);
				IContainer rootFolder = (IContainer) underlyingResource;
				computeFolderChildren(rootFolder, CharOperation.NO_STRINGS, vChildren);
				IRubyElement[] children = new IRubyElement[vChildren.size()];
				vChildren.toArray(children);
				info.setChildren(children);
			}
		} catch (RubyModelException e) {
			//problem resolving children; structure remains unknown
			info.setChildren(new IRubyElement[]{});
			throw e;
		}
		return true;
	}

	@Override
	protected Object createElementInfo() {
		return new SourceFolderRootInfo();
	}

	@Override
	public int getElementType() {
		return IRubyElement.SOURCE_FOLDER_ROOT;
	}
	
	public String getElementName() {
		if (this.resource instanceof IFolder)
			return ((IFolder) this.resource).getName();
		return ""; //$NON-NLS-1$
	}

    public ISourceFolder createSourceFolder(String names, boolean force, IProgressMonitor monitor) throws RubyModelException {
    	CreateSourceFolderOperation op = new CreateSourceFolderOperation(this, names, force);
    	op.runOperation(monitor);
    	return getSourceFolder(op.pkgName);
    }
    
    /**
	 * Starting at this folder, create package fragments and add the fragments that are not exclused
	 * to the collection of children.
	 * 
	 * @exception RubyModelException  The resource associated with this package fragment does not exist
	 */
	protected void computeFolderChildren(IContainer folder, String[] pkgName, ArrayList vChildren) throws RubyModelException {
		    ISourceFolder pkg = getSourceFolder(pkgName);
			vChildren.add(pkg); // add ourself

		try {
			RubyProject rubyProject = (RubyProject)getRubyProject();
			RubyModelManager manager = RubyModelManager.getRubyModelManager();
			IResource[] members = folder.members();

			for (int i = 0, max = members.length; i < max; i++) {
				IResource member = members[i];
				String memberName = member.getName();
				
				switch(member.getType()) {				    
				    case IResource.FOLDER:
							if (rubyProject.contains(member)) {
								String[] newNames = Util.arrayConcat(pkgName, manager.intern(memberName));
								computeFolderChildren((IFolder) member, newNames, vChildren);
							}
				    	break;
				    case IResource.FILE:
				        // inclusion filter may only include files, in which case we still want to include the immediate parent package (lazily)
				        break;
				}
			}
		} catch(IllegalArgumentException e){
			throw new RubyModelException(e, IRubyModelStatusConstants.ELEMENT_DOES_NOT_EXIST); // could be thrown by ElementTree when path is not found
		} catch (CoreException e) {
			throw new RubyModelException(e);
		}
	}

	public void delete(int updateResourceFlags, int updateModelFlags,
			IProgressMonitor monitor) throws RubyModelException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see IRubyElement
	 */
	public boolean exists() {
		return super.exists() && validateOnLoadpath().isOK();
	}
	
	public SourceFolder getSourceFolder(String[] names) {
		return new SourceFolder(this, names);
	}

	public boolean isExternal() {
		return false;
	}

	public IPath getPath() {
		return getResource().getFullPath();
	}

	public IResource getResource() {
		return (IResource)this.resource;
	}

	public IResource getUnderlyingResource() throws RubyModelException {
		if (!exists()) throw newNotPresentException();
		return getResource();
	}

	public boolean isArchive() {
		return false;
	}
	
	/**
	 * Returns an array of non-ruby resources contained in the receiver.
	 */
	public Object[] getNonRubyResources() throws RubyModelException {
		return ((SourceFolderRootInfo) getElementInfo()).getNonRubyResources(getRubyProject(), getResource(), this);
	}

	public ISourceFolder getSourceFolder(String packName) {
		String[] names = Util.getTrimmedSimpleNames(packName);
		return getSourceFolder(names);
	}
	
	/**
	 * @see RubyElement#getHandleMemento()
	 */
	protected char getHandleMementoDelimiter() {
		return RubyElement.JEM_SOURCEFOLDERROOT;
	}
	/*
	 * @see RubyElement
	 */
	public IRubyElement getHandleFromMemento(String token, MementoTokenizer memento, WorkingCopyOwner owner) {
		switch (token.charAt(0)) {
			case JEM_SOURCE_FOLDER:
				String pkgName;
				if (memento.hasMoreTokens()) {
					pkgName = memento.nextToken();
					char firstChar = pkgName.charAt(0);
					if (firstChar == JEM_RUBYSCRIPT || firstChar == JEM_COUNT) {
						token = pkgName;
						pkgName = ISourceFolder.DEFAULT_PACKAGE_NAME;
					} else {
						token = null;
					}
				} else {
					pkgName = ISourceFolder.DEFAULT_PACKAGE_NAME;
					token = null;
				}
				RubyElement pkg = (RubyElement)getSourceFolder(pkgName);
				if (token == null) {
					return pkg.getHandleFromMemento(memento, owner);
				} else {
					return pkg.getHandleFromMemento(token, memento, owner);
				}
		}
		return null;
	}
	/**
	 * @see RubyElement#getHandleMemento(StringBuffer)
	 */
	protected void getHandleMemento(StringBuffer buff) {
		IPath path;
		IResource underlyingResource = getResource();
		if (underlyingResource != null) {
			// internal jar or regular root
			if (getResource().getProject().equals(getRubyProject().getProject())) {
				path = underlyingResource.getProjectRelativePath();
			} else {
				path = underlyingResource.getFullPath();
			}
		} else {
			// external jar
			path = getPath();
		}
		((RubyElement)getParent()).getHandleMemento(buff);
		buff.append(getHandleMementoDelimiter());
		escapeMementoName(buff, path.toString()); 
	}
}
