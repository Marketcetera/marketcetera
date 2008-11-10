package org.rubypeople.rdt.internal.core.search;

import org.rubypeople.rdt.core.search.SearchParticipant;
import org.rubypeople.rdt.internal.core.util.Util;

public class ERBSearchDocument extends RubySearchDocument {

	private char[] fContents;

	public ERBSearchDocument(String absolutePath, char[] contents, SearchParticipant participant) {
		super(absolutePath, contents, participant);
	}
	
	public ERBSearchDocument(String documentPath, SearchParticipant participant) {
		super(documentPath, participant);
	}

	@Override
	public char[] getCharContents() {
		if (fContents == null) {
			char[] contents = super.getCharContents();		
			fContents = Util.replaceNonRubyCodeWithWhitespace(new String(contents));
		}		
		return fContents;
	}
	
}
