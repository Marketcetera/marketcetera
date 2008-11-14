package org.rubypeople.rdt.internal.core.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.search.SearchDocument;
import org.rubypeople.rdt.core.search.SearchParticipant;
import org.rubypeople.rdt.internal.core.search.processing.JobManager;
import org.rubypeople.rdt.internal.core.util.Util;

public class RubySearchDocument extends SearchDocument {

	private IFile file;
	protected char[] charContents;
	
	public RubySearchDocument(String documentPath, SearchParticipant participant) {
		super(documentPath, participant);
	}
	
	public RubySearchDocument(String documentPath, char[] contents, SearchParticipant participant) {
		this(documentPath, participant);
		this.charContents = contents;
	}

	@Override
	public char[] getCharContents() {
		if (this.charContents != null) return this.charContents;
		try {
			return Util.getResourceContentsAsCharArray(getFile());
		} catch (RubyModelException e) {
			if (BasicSearchEngine.VERBOSE || JobManager.VERBOSE) { // used during search and during indexing
				e.printStackTrace();
			}
			return null;
		}
	}
	
	private IFile getFile() {
		if (this.file == null)
			this.file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(getPath()));
		return this.file;
	}

}
