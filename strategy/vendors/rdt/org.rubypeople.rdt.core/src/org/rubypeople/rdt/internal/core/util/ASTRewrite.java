package org.rubypeople.rdt.internal.core.util;

import java.util.Map;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditGroup;
import org.jruby.ast.Node;
import org.jruby.lexer.yacc.ISourcePosition;

public class ASTRewrite {

	private Node ast;
	private TextEdit currentEdit;
	private String lineDelim;

	protected ASTRewrite(Node ast, IDocument document) {
		this.ast = ast;
		TextEdit edit = new MultiTextEdit();
		this.lineDelim= TextUtilities.getDefaultLineDelimiter(document);
		this.currentEdit = edit;		
	}

	public static ASTRewrite create(Node ast, IDocument document) {
		return new ASTRewrite(ast, document);
	}

	public TextEdit rewriteAST(IDocument document, Map options) {		
		return currentEdit;
	}

	final void doTextInsert(int offset, String insertString) {
		if (insertString.length() > 0) {
			if (!insertString.startsWith(getLineDelimiter())) {
				TextEdit edit = new InsertEdit(offset, getLineDelimiter()); // add a line delimiter
				addEdit(edit);
			}
			TextEdit edit = new InsertEdit(offset, insertString);
			addEdit(edit);
		}
	}

	private String getLineDelimiter() {
		return lineDelim;
	}

	public void insertBefore(String source, Node insert, Node element,
			TextEditGroup group) {
		// FIXME We're taking in source just because we can't spit out source
		// from AST structure alone right now
		ISourcePosition pos = element.getPosition();
		doTextInsert(pos.getStartOffset(), source);
	}

	final void addEdit(TextEdit edit) {
		this.currentEdit.addChild(edit);
	}

	public void insertAfter(String source, Node insert, Node element,
			TextEditGroup group) {
		// FIXME We're taking in source just because we can't spit out source
		// from AST structure alone right now
		ISourcePosition pos = element.getPosition();
		doTextInsert(pos.getEndOffset() + 1, source);
	}

	public void insertLast(String source, Node insert, TextEditGroup group) {
		// FIXME We're taking in source just because we can't spit out source
		// from AST structure alone right now
		ISourcePosition pos = ast.getPosition();
		// TODO We should probably do something more intelligent for figuring out where to place the insert from the end of a node
		doTextInsert(pos.getEndOffset() - "end".length(), source);
	}

}
