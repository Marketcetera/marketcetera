package org.rubypeople.rdt.internal.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;
import org.rubypeople.rdt.internal.core.util.MementoTokenizer;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.core.util.Util;

public class SourceFolder extends Openable implements ISourceFolder {

	public String[] names;

	public SourceFolder(SourceFolderRoot parent, String[] names) {
		super(parent);
		this.names = names;
	}
	
	/**
	 * @see IParent 
	 */
	public boolean hasChildren() throws RubyModelException {
		return getChildren().length > 0;
	}

	@Override
	protected boolean buildStructure(OpenableElementInfo info,
			IProgressMonitor pm, Map newElements, IResource underlyingResource)
			throws RubyModelException {
		// check whether this folder can be opened
		if (!underlyingResource.isAccessible()) throw newNotPresentException();
		
		// check that it is not excluded (https://bugs.eclipse.org/bugs/show_bug.cgi?id=138577)
		if (Util.isExcluded(this)) 
			throw newNotPresentException();


		// add ruby scripts from resources
		HashSet vChildren = new HashSet();
		try {
		    SourceFolderRoot root = getSourceFolderRoot();
			char[][] inclusionPatterns = root.fullInclusionPatternChars();
			char[][] exclusionPatterns = root.fullExclusionPatternChars();
			IResource[] members = ((IContainer) underlyingResource).members();
			for (int i = 0, max = members.length; i < max; i++) {
				IResource child = members[i];
				if (child.getType() != IResource.FOLDER
						&& !Util.isExcluded(child, inclusionPatterns, exclusionPatterns)) {
					IRubyElement childElement;
					if (Util.isValidRubyScriptName(child.getName())) {
						childElement = new RubyScript(this, child.getName(), DefaultWorkingCopyOwner.PRIMARY);
						vChildren.add(childElement);
					} else if (Util.isERBLikeFileName(child.getName())) {
						childElement = new ERBScript(this, child.getName(), DefaultWorkingCopyOwner.PRIMARY);
						vChildren.add(childElement);
					}
				}
			}
		} catch (CoreException e) {
			throw new RubyModelException(e);
		}
		
		// add primary compilation units
		IRubyScript[] primaryCompilationUnits = getRubyScripts(DefaultWorkingCopyOwner.PRIMARY);
		for (int i = 0, length = primaryCompilationUnits.length; i < length; i++) {
			IRubyScript primary = primaryCompilationUnits[i];
			vChildren.add(primary);
		}
		
		
		IRubyElement[] children = new IRubyElement[vChildren.size()];
		vChildren.toArray(children);
		info.setChildren(children);
		return true;
	}

	@Override
	protected Object createElementInfo() {
		return new SourceFolderInfo();
	}

	@Override
	public int getElementType() {
		return IRubyElement.SOURCE_FOLDER;
	}
	
	@Override
	public String getElementName() {
		if (names.length == 0) return "";
		return Util.concatWith(this.names, File.separatorChar);
	}

	public boolean containsRubyResources() throws RubyModelException {
		return ((SourceFolderInfo) getElementInfo()).containsRubyResources();
	}

	public IRubyScript createRubyScript(String name, String contents,
			boolean force, IProgressMonitor monitor) throws RubyModelException {
		CreateRubyScriptOperation op= new CreateRubyScriptOperation(this, name, contents, force);
		op.runOperation(monitor);
		return new RubyScript(this, name, DefaultWorkingCopyOwner.PRIMARY);
	}

	public Object[] getNonRubyResources() throws RubyModelException {
		if (this.isDefaultPackage()) {
			// We don't want to show non ruby resources of the default package (see PR #1G58NB8)
			return RubyElementInfo.NO_NON_RUBY_RESOURCES;
		} else {
			return ((SourceFolderInfo) getElementInfo()).getNonRubyResources(getResource(), getSourceFolderRoot());
		}
	}

	public boolean isDefaultPackage() {
		return this.names.length == 0;
	}

	public IRubyScript[] getRubyScripts() throws RubyModelException {
		ArrayList list = getChildrenOfType(SCRIPT);
		IRubyScript[] array= new IRubyScript[list.size()];
		list.toArray(array);
		return array;
	}

	public IRubyScript[] getRubyScripts(WorkingCopyOwner owner)
			throws RubyModelException {
		IRubyScript[] workingCopies = RubyModelManager.getRubyModelManager().getWorkingCopies(owner, false/*don't add primary*/);
		if (workingCopies == null) return RubyModelManager.NO_WORKING_COPY;
		int length = workingCopies.length;
		IRubyScript[] result = new IRubyScript[length];
		int index = 0;
		for (int i = 0; i < length; i++) {
			IRubyScript wc = workingCopies[i];
			if (equals(wc.getParent()) && !Util.isExcluded(wc)) { // 59933 - excluded wc shouldn't be answered back
				result[index++] = wc;
			}
		}
		if (index != length) {
			System.arraycopy(result, 0, result = new IRubyScript[index], 0, index);
		}
		return result;
	}

	public IPath getPath() {
		SourceFolderRoot root = this.getSourceFolderRoot();

		IPath path = root.getPath();
		for (int i = 0, length = this.names.length; i < length; i++) {
			String name = this.names[i];
			path = path.append(name);
		}
		return path;
	}
	
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SourceFolder)) return false;
		
		SourceFolder other = (SourceFolder) o;		
		return Util.equalArraysOrNull(this.names, other.names) &&
				this.parent.equals(other.parent);
	}
	
	public boolean exists() {
		// super.exist() only checks for the parent and the resource existence
		// so also ensure that the package is not exceluded (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=138577)
		return super.exists() && !Util.isExcluded(this); 
	}

	public IResource getResource() {
		SourceFolderRoot root = this.getSourceFolderRoot();
		if (root.isExternal()) {
			return root.getResource();
		}
		int length = this.names.length;
		if (length == 0) {
			return root.getResource();
		}
		IPath path = new Path(this.names[0]);
		for (int i = 1; i < length; i++)
			path = path.append(this.names[i]);
		return ((IContainer) root.getResource()).getFolder(path);
	}

	public IResource getUnderlyingResource() throws RubyModelException {
		IResource rootResource = this.parent.getUnderlyingResource();
		if (rootResource == null) {
			//jar package fragment root that has no associated resource
			return null;
		}
		// the underlying resource may be a folder or a project (in the case that the project folder
		// is atually the package fragment root)
		if (rootResource.getType() == IResource.FOLDER || rootResource.getType() == IResource.PROJECT) {
			IContainer folder = (IContainer) rootResource;
			String[] segs = this.names;
			for (int i = 0; i < segs.length; ++i) {
				IResource child = folder.findMember(segs[i]);
				if (child == null || child.getType() != IResource.FOLDER) {
					throw newNotPresentException();
				}
				folder = (IFolder) child;
			}
			return folder;
		} else {
			return rootResource;
		}
	}

	public IRubyScript getRubyScript(String name) {
		if (org.rubypeople.rdt.internal.core.util.Util.isERBLikeFileName(name)) {
			return new ERBScript(this, name, DefaultWorkingCopyOwner.PRIMARY);
		}
		if (!org.rubypeople.rdt.internal.core.util.Util.isRubyLikeFileName(name)) {
			throw new IllegalArgumentException(Messages.convention_unit_notRubyName); 
		}
		return new RubyScript(this, name, DefaultWorkingCopyOwner.PRIMARY);
	}
	
	/*
	 * @see RubyElement
	 */
	public IRubyElement getHandleFromMemento(String token, MementoTokenizer memento, WorkingCopyOwner owner) {
		switch (token.charAt(0)) {
			case JEM_RUBYSCRIPT:
				if (!memento.hasMoreTokens()) return this;
				String cuName = memento.nextToken();
				RubyElement cu = new RubyScript(this, cuName, owner);
				return cu.getHandleFromMemento(memento, owner);
		}
		return null;
	}
	/**
	 * @see RubyElement#getHandleMementoDelimiter()
	 */
	protected char getHandleMementoDelimiter() {
		return RubyElement.JEM_SOURCE_FOLDER;
	}
	
	/**
	 * @see ISourceFolder#hasSubfolders()
	 */
	public boolean hasSubfolders() throws RubyModelException {
		IRubyElement[] packages= ((ISourceFolderRoot)getParent()).getChildren();
		int namesLength = this.names.length;
		nextPackage: for (int i= 0, length = packages.length; i < length; i++) {
			String[] otherNames = ((SourceFolder) packages[i]).names;
			if (otherNames.length <= namesLength) continue nextPackage;
			for (int j = 0; j < namesLength; j++)
				if (!this.names[j].equals(otherNames[j]))
					continue nextPackage;
			return true;
		}
		return false;
	}

}
