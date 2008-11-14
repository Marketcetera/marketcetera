package org.rubypeople.rdt.core.codeassist;

import org.eclipse.core.resources.IFile;
import org.jruby.ast.Node;
import org.jruby.ast.RootNode;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.RubyScript;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.ti.util.OffsetNodeLocator;

public class ResolveContext {

	private IRubyScript script;
	private int start;
	private int end;
	private RootNode root;
	private Node selected;
	private IRubyElement[] resolved = new IRubyElement[0];

	public ResolveContext(IRubyScript script, int start, int end) {
		this.script = script;
		this.start = start;
		this.end = end;
	}
	
	public RootNode getAST() throws RubyModelException {
		if (root == null) {
			try {
				RubyParser parser = new RubyParser();
				root = (RootNode) parser.parse((IFile) script.getResource(), script.getSource()).getAST();
			} catch (SyntaxException e) {
				root = (RootNode) ((RubyScript)script).lastGoodAST;
			}
		}
		return root;
	}
	
	public Node getSelectedNode() throws RubyModelException {
		if (selected == null) {
			selected = OffsetNodeLocator.Instance().getNodeAtOffset(getAST(), start);
		}
		return selected;
	}
	
	public IRubyScript getScript() {
		return script;
	}
	
	public int getStartOffset() {
		return start;
	}
	
	public int getEndOffset() {
		return end;
	}

	public IRubyElement[] getResolved() {
		return resolved;
	}
	
	public void putResolved(IRubyElement[] resolved) {
		this.resolved = resolved;
	}
}
