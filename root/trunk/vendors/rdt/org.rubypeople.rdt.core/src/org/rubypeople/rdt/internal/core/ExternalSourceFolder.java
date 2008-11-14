package org.rubypeople.rdt.internal.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.LocalFileStorage;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;
import org.rubypeople.rdt.internal.core.util.MementoTokenizer;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.core.util.Util;

public class ExternalSourceFolder extends SourceFolder {

	public ExternalSourceFolder(SourceFolderRoot parent, String[] names) {
		super(parent, names);
	}
	
	/*
	 * @see RubyElement#generateInfos
	 */
	protected void generateInfos(Object info, HashMap newElements, IProgressMonitor pm) throws RubyModelException {
		// Open my folder: this creates all the pkg infos
		Openable openableParent = (Openable)this.parent;
		if (!openableParent.isOpen()) {
			openableParent.generateInfos(openableParent.createElementInfo(), newElements, pm);
		}
	}
	
	public boolean isReadOnly() {
		return true;
	}
	
	protected Object[] storedNonRubyResources() throws RubyModelException {
		return ((ExternalSourceFolderInfo) getElementInfo()).getNonRubyResources();
	}

	protected boolean computeChildren(OpenableElementInfo info) {
		ArrayList<IRubyElement> vChildren = new ArrayList<IRubyElement>();
		File file = getPath().toFile();
		File[] members = file.listFiles();
		List<LocalFileStorage> files = new ArrayList<LocalFileStorage>();
		for (int i = 0, max = members.length; i < max; i++) {
			File child = members[i];
			if (!child.isDirectory()) {
				IRubyElement childElement;
				if (Util.isValidRubyScriptName(child.getName())) {
					childElement = new ExternalRubyScript(this, child.getName(), DefaultWorkingCopyOwner.PRIMARY);
					vChildren.add(childElement);
				} else {
					files.add(new LocalFileStorage(child));
				}
			}
		}
		if (info instanceof SourceFolderInfo) {
			SourceFolderInfo duh = (SourceFolderInfo) info;
			duh.setNonRubyResources(files.toArray(new Object[files.size()]));
		}		
		IRubyElement[] children= new IRubyElement[vChildren.size()];
		vChildren.toArray(children);
		info.setChildren(children);
		return true;		
	}
	
	public IRubyScript getRubyScript(String name) {
		if (!org.rubypeople.rdt.internal.core.util.Util.isRubyLikeFileName(name)) {
			throw new IllegalArgumentException(Messages.convention_unit_notRubyName); 
		}
		return new ExternalRubyScript(this, name, DefaultWorkingCopyOwner.PRIMARY);
	}
	
	/*
	 * @see RubyElement
	 */
	public IRubyElement getHandleFromMemento(String token, MementoTokenizer memento, WorkingCopyOwner owner) {
		switch (token.charAt(0)) {
			case JEM_RUBYSCRIPT:
				if (!memento.hasMoreTokens()) return this;
				String classFileName = memento.nextToken();
				RubyElement classFile = new ExternalRubyScript(this, classFileName, owner);
				return classFile.getHandleFromMemento(memento, owner);
		}
		return null;
	}
	
	/**
	 * Returns an array of non-ruby resources contained in the receiver.
	 */
	public Object[] getNonRubyResources() throws RubyModelException {
		if (this.isDefaultPackage()) {
			// We don't want to show non ruby resources of the default package (see PR #1G58NB8)
			return RubyElementInfo.NO_NON_RUBY_RESOURCES;
		} else {
			return this.storedNonRubyResources();
		}
	}

}
