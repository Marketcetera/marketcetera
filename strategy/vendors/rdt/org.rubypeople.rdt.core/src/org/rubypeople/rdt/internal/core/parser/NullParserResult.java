package org.rubypeople.rdt.internal.core.parser;

import java.util.Collections;
import java.util.List;

import org.jruby.ast.CommentNode;
import org.jruby.ast.Node;
import org.jruby.parser.RubyParserResult;
import org.jruby.runtime.DynamicScope;

public class NullParserResult extends RubyParserResult {

	@Override
	public Node getAST() {
		return null;
	}
	
	@Override
	public List<Node> getBeginNodes() {
		return Collections.emptyList();
	}
	
	@Override
	public int getEndOffset() {
		return 0;
	}
	
	@Override
	public DynamicScope getScope() {
		return null;
	}
	
	@Override
	public List<CommentNode> getCommentNodes() {
		return Collections.emptyList();
	}
}
