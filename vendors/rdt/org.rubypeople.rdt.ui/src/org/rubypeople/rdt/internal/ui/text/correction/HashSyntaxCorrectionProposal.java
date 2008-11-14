package org.rubypeople.rdt.internal.ui.text.correction;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.jruby.ast.ArrayNode;
import org.jruby.ast.Node;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.ui.RubyUI;
import org.rubypeople.rdt.ui.text.ruby.IInvocationContext;
import org.rubypeople.rdt.ui.text.ruby.IProblemLocation;

public class HashSyntaxCorrectionProposal extends CUCorrectionProposal {

	private static final String NAME = "Fix Hash Syntax";
	
	private IInvocationContext context;
	private IProblemLocation problem;
	
	public HashSyntaxCorrectionProposal(IInvocationContext context, IProblemLocation problem, int relevance) {
		super(NAME, context.getRubyScript(), relevance, RubyUI.getSharedImages().getImage(org.rubypeople.rdt.ui.ISharedImages.IMG_OBJS_CORRECTION_CHANGE));
		this.context = context;
		this.problem = problem;
	}	
	
	@Override
	protected void addEdits(IDocument document, TextEdit editRoot) throws CoreException {	
		String src = getRubyScript().getSource();
		ArrayNode covering = (ArrayNode) problem.getCoveringNode(context.getASTRoot());
		List<Node> children = covering.childNodes();
		for (int i = 0; i < children.size(); i += 2) { // for each key value pair, grab the comma between and oncvetr it to =>
			if (children.size() <= (1 + 1)) break;
			Node key = children.get(i);
			if (key == null) continue;
			Node value = children.get(i + 1);
			if (value == null) continue;
			ISourcePosition pos = key.getPosition();
			String between = src.substring(pos.getEndOffset(), value.getPosition().getStartOffset());
			String corrected = "";
			if (!between.startsWith(" ")) corrected += " ";
			corrected += "=>";
			if (!between.endsWith(" ")) corrected += " ";
			ReplaceEdit edit = new ReplaceEdit(pos.getEndOffset() + between.indexOf(","), 1, corrected);
			editRoot.addChild(edit);
		}
	}
}
