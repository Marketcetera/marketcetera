package org.rubypeople.rdt.core.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.Openable;
import org.rubypeople.rdt.internal.core.index.EntryResult;
import org.rubypeople.rdt.internal.core.search.HandleFactory;
import org.rubypeople.rdt.internal.core.search.indexing.IIndexConstants;
import org.rubypeople.rdt.internal.core.search.indexing.InternalSearchDocument;

public abstract class SearchDocument extends InternalSearchDocument {
	
	private static HandleFactory factory = new HandleFactory();
	private IRubyScript script;
		
	private String documentPath;
	private SearchParticipant participant;

	public SearchDocument(String documentPath, SearchParticipant participant) {
		this.documentPath = documentPath;
		this.participant = participant;
	}
	
	public void addIndexEntry(char[] category, char[] key) {
		super.addIndexEntry(category, key);
	}
	
	/**
	 * Removes all index entries from the index for the given document.
	 * This method must be called from 
	 * {@link SearchParticipant#indexDocument(SearchDocument document, org.eclipse.core.runtime.IPath indexPath)}.
	 */
	public void removeAllIndexEntries() {
		super.removeAllIndexEntries();
	}

	public Set<String> getElementNamesOfType(int type) {
		Set<String> names = new HashSet<String>();
		try {
			EntryResult[] results = index.query(new char[][] {getCategory(type)}, new char[] {'*'}, SearchPattern.R_PATTERN_MATCH);
			for (int i = 0; i < results.length; i++) {
				String name = new String(results[i].getWord());
				names.add(name);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return names;
	}

	public List<IRubyElement> getElementsOfType(int type) {
		IRubyScript script = getScript();
		return getChildrenOfType(script, type);
	}

	private IRubyScript getScript() {
		if (this.script == null) {
			Openable openable = factory.createOpenable(documentPath);
			this.script = (IRubyScript) openable;
		}
		return this.script;
	}	
	
	/**
	 * Returns the path to the original document to publicly mention in index
	 * or search results. This path is a string that uniquely identifies the document.
	 * Most of the time it is a workspace-relative path, but it can also be a file system path, 
	 * or a path inside a zip file.
	 * 
	 * @return the path to the document
	 */	
	public final String getPath() {
		return this.documentPath;
	}

	private List<IRubyElement> getChildrenOfType(IParent parent, int type) {
		List<IRubyElement> elements = new ArrayList<IRubyElement>();
		if (parent == null) return elements;
		try {
			IRubyElement[] children = parent.getChildren();
			if (children == null)
				return elements;
			for (int i = 0; i < children.length; i++) {
				if (children[i].isType(type))
					elements.add(children[i]);
				if (children[i] instanceof IParent) {
					IParent childParent = (IParent) children[i];
					elements.addAll(getChildrenOfType(childParent, type));
				}
			}
		} catch (RubyModelException e) {
			// ignore
		}
		return elements;
	}

	public void removeElement(IRubyElement element) {
		// FIXME Rebuild the index?!
	}

	public void addElement(IRubyElement element) {
		addIndexEntry(getCategory(element), element.getElementName().toCharArray());
	}

	private char[] getCategory(IRubyElement element) {
		return getCategory(element.getElementType());
	}

	private char[] getCategory(int elementType) {
		switch (elementType) {
		case IRubyElement.TYPE:
			return IIndexConstants.TYPE_DECL;
		case IRubyElement.METHOD:
			return IIndexConstants.METHOD_DECL;
		case IRubyElement.CONSTANT:
		case IRubyElement.GLOBAL:
		case IRubyElement.CLASS_VAR:
		case IRubyElement.INSTANCE_VAR:
		case IRubyElement.LOCAL_VARIABLE:
			return IIndexConstants.FIELD_DECL;
		default:
			return new char[0];
		}
	}

	public IType findType(String name) {
		return (IType) findElement(IRubyElement.TYPE, name);
	}

	private IRubyElement findElement(int type, String name) {
		IRubyScript script = getScript();
		List<IRubyElement> children = getChildrenOfType(script, type);
		for (IRubyElement element : children) {
			if (element.getElementName().equals(name))
				return element;
		}
		return null;
	}

	/**
	 * Returns the contents of this document.
	 * Contents may be different from actual resource at corresponding document
	 * path due to preprocessing.
	 * <p>
	 * This method must be implemented in subclasses.
	 * </p><p>
	 * Note: some implementation may choose to cache the contents directly on the
	 * document for performance reason. However, this could induce scalability issues due
	 * to the fact that collections of documents are manipulated throughout the search
	 * operation, and cached contents would then consume lots of memory until they are 
	 * all released at once in the end.
	 * </p>
	 * 
	 * @return the contents of this document,
	 * or <code>null</code> if none
	 */
	public abstract char[] getCharContents();

	/**
	 * Returns the participant that created this document.
	 * 
	 * @return the participant that created this document
	 */
	public final SearchParticipant getParticipant() {
		return this.participant;
	}
}
