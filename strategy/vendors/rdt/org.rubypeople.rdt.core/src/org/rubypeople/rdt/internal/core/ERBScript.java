package org.rubypeople.rdt.internal.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModelStatusConstants;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.core.util.Util;

public class ERBScript extends RubyScript implements IRubyScript {

	public ERBScript(SourceFolder parent, String name, WorkingCopyOwner owner) {
		super(parent, name, owner);
	}
	
	@Override
	public IRubyElement getElementAt(int position) throws RubyModelException {
		getElementInfo();
		return super.getElementAt(position);
	}
	
	@Override
	protected char[] getCharacters(IProgressMonitor pm, RubyScriptElementInfo unitInfo) throws RubyModelException {
		char[] cs = super.getCharacters(pm, unitInfo);
		return Util.replaceNonRubyCodeWithWhitespace(new String(cs));
	}
	
	protected IStatus validateRubyScript(IResource resource) {
		ISourceFolderRoot root = getSourceFolderRoot();
		// root never null as validation is not done for working copies
		if (resource != null) {
			char[][] inclusionPatterns = ((SourceFolderRoot)root).fullInclusionPatternChars();
			char[][] exclusionPatterns = ((SourceFolderRoot)root).fullExclusionPatternChars();
			if (Util.isExcluded(resource, inclusionPatterns, exclusionPatterns)) 
				return new RubyModelStatus(IRubyModelStatusConstants.ELEMENT_NOT_ON_CLASSPATH, this);
			if (!resource.isAccessible())
				return new RubyModelStatus(IRubyModelStatusConstants.ELEMENT_DOES_NOT_EXIST, this);
		}
		if (name == null) { 
			return new Status(IStatus.ERROR, RubyCore.PLUGIN_ID, -1, Messages.bind(Messages.convention_unit_nullName), null);
		}
		if (!org.rubypeople.rdt.internal.core.util.Util.isERBLikeFileName(name))  {
				return new Status(IStatus.ERROR, RubyCore.PLUGIN_ID, -1, Messages.bind(Messages.convention_unit_notERBName), null);
		}
		IStatus status = ResourcesPlugin.getWorkspace().validateName(name, IResource.FILE);
		if (!status.isOK()) { return status; }
		return RubyModelStatus.VERIFIED_OK;
	}
	
	@Override
	public String getSource() throws RubyModelException {
		String src = super.getSource();		
		return replaceNonRubyCodeWithWhitespace(src);
	}
	
	private String replaceNonRubyCodeWithWhitespace(String source) {
		return new String(Util.replaceNonRubyCodeWithWhitespace(source));
	}
	
}
