package org.rubypeople.rdt.internal.core;

import java.io.CharArrayReader;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.jruby.ast.Node;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.core.IBuffer;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;
import org.rubypeople.rdt.core.util.Util;
import org.rubypeople.rdt.internal.core.buffer.BufferManager;
import org.rubypeople.rdt.internal.core.parser.RubyParser;

public class ExternalRubyScript extends RubyScript {

	public ExternalRubyScript(ExternalSourceFolder parent, String name, WorkingCopyOwner owner) {
		super(parent, name, owner);
	}
	
	@Override
	protected boolean buildStructure(OpenableElementInfo info, IProgressMonitor pm, Map newElements, IResource underlyingResource) throws RubyModelException {
		RubyScriptElementInfo unitInfo = (RubyScriptElementInfo) info;
		// get buffer contents
		IBuffer buffer = getBufferManager().getBuffer(this);
		if (buffer == null) {
			buffer = openBuffer(pm, unitInfo); // open buffer independently
			// from the info, since we are
			// building the info
		}
		final char[] contents = buffer == null ? null : buffer.getCharacters();
		try {
			RubyScriptStructureBuilder visitor = new RubyScriptStructureBuilder(this, unitInfo, newElements);
			SourceElementParser sp = new SourceElementParser(visitor);
			sp.parse(contents, null);
			unitInfo.setIsStructureKnown(true);
		} catch (SyntaxException e) {
			unitInfo.setIsStructureKnown(false);
			unitInfo.setSyntaxException(e) ;
		} catch (Exception e) {
			RubyCore.log(e);
		}
		return unitInfo.isStructureKnown();
	}
	
	@Override
	public boolean exists() {
		return getFile().exists();
	}
	
	/**
	 * Opens and returns buffer on the source code associated with this class file.
	 * Maps the source code to the children elements of this class file.
	 * If no source code is associated with this class file, 
	 * <code>null</code> is returned.
	 * 
	 * @see Openable
	 */
	protected IBuffer openBuffer(IProgressMonitor pm, Object info) throws RubyModelException {
		char[] contents = findSource();
		if (contents != null) {
			// create buffer
			IBuffer buffer = getBufferManager().createBuffer(this);
			if (buffer == null) return null;
			BufferManager bufManager = getBufferManager();
			bufManager.addBuffer(buffer);
			
			// set the buffer source
			if (buffer.getCharacters() == null){
				buffer.setContents(contents);
			}
			
			// listen to buffer changes
			buffer.addBufferChangedListener(this);	
							
			return buffer;
		}
		return null;
	}
	
	public File getFile() {
		ExternalSourceFolder parent = (ExternalSourceFolder) getParent();
		IPath parentPath = parent.getPath();
		return parentPath.append(name).toFile();
	}

	private char[] findSource() {
		String source = null;
		try {
			source = getSource();			
		} catch (RubyModelException e) {
			RubyCore.log(e);
		}
		if (source == null) return new char[0];
		return source.toCharArray();
	}
	
	@Override
	public IResource getResource() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IPath getPath() {
		return getParent().getPath().append(getElementName());
	}
	
	@Override
	public String getSource() throws RubyModelException {
		File file = getFile();
		byte[] bytes;
		try {
			bytes = Util.getFileByteContent(file);
		} catch (IOException e) {
			RubyCore.log(e);
			return null;
		}
		return new String(bytes);
	}
}
