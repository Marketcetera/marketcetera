package org.rubypeople.rdt.internal.core.parser.warnings;

import java.util.List;

import org.jruby.ast.HashNode;
import org.jruby.ast.ListNode;
import org.jruby.ast.Node;
import org.jruby.evaluator.Instruction;
import org.jruby.lexer.yacc.IDESourcePosition;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.compiler.IProblem;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;

public class Ruby19HashCommaSyntax extends RubyLintVisitor {

	public Ruby19HashCommaSyntax(String contents) {
		super(contents);
	}

	@Override
	protected String getOptionKey() {
		return RubyCore.COMPILER_PB_RUBY_19_HASH_COMMA_SYTNAX;
	}
	
	@Override
	protected String getSeverity() {
		// TODO Return error on 1.9!
		return super.getSeverity();
	}
	
	@Override
	public Instruction visitHashNode(HashNode iVisited) {
		ListNode list = iVisited.getListNode();
		List<Node> children = list.childNodes();
		for (int i = 0; i < children.size(); i += 2) {			
			if (children.size() <= (i + 1)) break;
			Node key = children.get(i);
			if (key == null) continue;
			Node value = children.get(i + 1);
			if (value == null) continue;
			ISourcePosition pos = key.getPosition();
			String between = getSource(pos.getEndOffset(), value.getPosition().getStartOffset());			
			if (between != null && between.trim().equals(",")) {
				int start = pos.getEndOffset() + between.indexOf(",");
				createProblem(new IDESourcePosition("", pos.getEndLine(), pos.getEndLine(), start, start + 1), "',' not allowed to separate keys and values in hashes for Ruby 1.9. Please use '=>'.");
			}
		}
		return super.visitHashNode(iVisited);
	}
	
	@Override
	protected int getProblemID() {
		return IProblem.HashCommaSyntax;
	}

}
