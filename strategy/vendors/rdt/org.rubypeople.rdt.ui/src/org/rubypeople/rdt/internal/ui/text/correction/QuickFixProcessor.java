package org.rubypeople.rdt.internal.ui.text.correction;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.jruby.ast.Node;
import org.jruby.ast.visitor.rewriter.FormatHelper;
import org.jruby.ast.visitor.rewriter.ReWriteVisitor;
import org.jruby.ast.visitor.rewriter.utils.ReWriterContext;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.compiler.IProblem;
import org.rubypeople.rdt.core.formatter.EditableFormatHelper;
import org.rubypeople.rdt.internal.core.util.ASTUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.RubyUI;
import org.rubypeople.rdt.ui.text.correction.CorrectionProposal;
import org.rubypeople.rdt.ui.text.ruby.IInvocationContext;
import org.rubypeople.rdt.ui.text.ruby.IProblemLocation;
import org.rubypeople.rdt.ui.text.ruby.IQuickFixProcessor;
import org.rubypeople.rdt.ui.text.ruby.IRubyCompletionProposal;

public class QuickFixProcessor implements IQuickFixProcessor {

	public IRubyCompletionProposal[] getCorrections(IInvocationContext context, IProblemLocation[] locations) throws CoreException {
		if (locations == null || locations.length == 0) {
			return null;
		}

		HashSet<Integer> handledProblems = new HashSet<Integer>(locations.length);
		ArrayList<IRubyCompletionProposal> resultingCollections = new ArrayList<IRubyCompletionProposal>();
		for (int i = 0; i < locations.length; i++) {
			IProblemLocation curr = locations[i];
			Integer id = new Integer(curr.getProblemId());
			if (handledProblems.add(id)) {
				process(context, curr, resultingCollections);
			}
		}
		return (IRubyCompletionProposal[]) resultingCollections.toArray(new IRubyCompletionProposal[resultingCollections.size()]);
	}

	private void process(IInvocationContext context, IProblemLocation problem, Collection<IRubyCompletionProposal> proposals) throws CoreException {
		int id = problem.getProblemId();
		if (id == 0) { // no proposals for none-problem locations
			return;
		}
		switch (id) {
		case IProblem.UnusedPrivateMethod:
		case IProblem.UnusedPrivateField:
		case IProblem.LocalVariableIsNeverUsed:
		case IProblem.ArgumentIsNeverUsed:
			addUnusedMemberProposal(context, problem, proposals);
			break;
		case IProblem.MultineCommentNotAtFirstColumn:
			addShiftMultilineCommentProposal(context, problem, proposals);
			break;
		case IProblem.ParenthesizeArguments:
			addParenthesizeArgumentsProposal(context, problem, proposals);
			break;			
		case IProblem.HashCommaSyntax:
			addFixHashSyntaxProposal(context, problem, proposals);
			break;
		case IProblem.ColonAfterWhenStatement:
			addFixWhenStatementProposal(context, problem, proposals);
			break;			
		default:
			addIgnoreWarningFix(context, problem, proposals);
		}
	}
	
	private void addFixWhenStatementProposal(IInvocationContext context, IProblemLocation problem, Collection<IRubyCompletionProposal> proposals) {
		String corrected = "then";
		Image image= RubyUI.getSharedImages().getImage(org.rubypeople.rdt.ui.ISharedImages.IMG_OBJS_CORRECTION_CHANGE);		
		CorrectionProposal proposal = new CorrectionProposal(corrected, problem.getOffset(), 1, image, "Replace with 'then'", 100);
		proposals.add(proposal);
	}

	private void addFixHashSyntaxProposal(IInvocationContext context, IProblemLocation problem, Collection<IRubyCompletionProposal> proposals) {
		HashSyntaxCorrectionProposal proposal = new HashSyntaxCorrectionProposal(context, problem, 100);
		proposals.add(proposal);
	}

	private void addIgnoreWarningFix(IInvocationContext context, IProblemLocation problem, Collection<IRubyCompletionProposal> proposals) {
		proposals.add(new IgnoreWarningProposal(context, problem));		
	}

	private void addParenthesizeArgumentsProposal(IInvocationContext context, IProblemLocation problem, Collection<IRubyCompletionProposal> proposals) throws RubyModelException {
		Image image= RubyPlugin.getDefault().getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE);
		StringWriter out = new StringWriter();
		String src = getSource(context);
		ReWriterContext config = new ReWriterContext(out, getSource(context), getFormatHelper());
		ReWriteVisitor visitor = new ReWriteVisitor(config);
		Node covering = problem.getCoveredNode(context.getASTRoot());
		String corrected = ASTUtil.stringRepresentation(covering) + "(";
		ISourcePosition pos = covering.getPosition();
		covering.accept(visitor);
		corrected += out.toString() + ")\n";
		
		CorrectionProposal proposal = new CorrectionProposal(corrected, pos.getStartOffset(), corrected.length(), image, "Insert missing parentheses", 100);
		proposals.add(proposal);
		
	}

	private void addShiftMultilineCommentProposal(IInvocationContext context, IProblemLocation problem, Collection<IRubyCompletionProposal> proposals) throws RubyModelException {
		Image image= RubyPlugin.getDefault().getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE);
		String contents = getSource(context);
		String doc = contents.substring(problem.getOffset(), problem.getOffset() + problem.getLength());
		// get where the first column is before the offset
		String before = contents.substring(0, problem.getOffset());
		int replaceOffset = before.lastIndexOf("\n") + 1;
		int shift = problem.getOffset() - replaceOffset;
		String[] lines = doc.split("\n");
		String indent = "";
		for (int i = 0; i < shift; i++) {
			indent += " ";
		}
		StringBuffer correctedDoc = new StringBuffer();
		for (int j = 0; j < lines.length; j++) {
			String line = lines[j]; 
			if (line.startsWith(indent)) {
				line = line.substring(indent.length());
			}
			correctedDoc.append(line);
		}		
		CorrectionProposal proposal = new CorrectionProposal(correctedDoc.toString(), replaceOffset, problem.getLength() + shift, image, "Shift multiline comment to first column", 100);
		proposals.add(proposal);
	}

	private String getSource(IInvocationContext context) throws RubyModelException {
		return context.getRubyScript().getBuffer().getContents();
	}

	protected FormatHelper getFormatHelper() {
		// FIXME Hook these settings up to format prefs
		EditableFormatHelper helper = new EditableFormatHelper();
		helper.setAlwaysParanthesizeMethodCalls(true);
		helper.setAlwaysParanthesizeMethodDefs(true);
		helper.setSpacesAroundHashAssignment(true);
		return helper;
	}

	public boolean hasCorrections(IRubyScript unit, int problemId) {
		return true; // we always have at least the "ignore this warning" option
	}
	
	public static void addUnusedMemberProposal(IInvocationContext context, IProblemLocation problem,  Collection<IRubyCompletionProposal> proposals) {
		Image image= RubyPlugin.getDefault().getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE);
		CorrectionProposal proposal = new CorrectionProposal("", problem.getOffset(), problem.getLength(), image, "clean up unused code", 100);
		proposals.add(proposal);
	}
}
