package org.rubypeople.rdt.internal.core.builder;

import java.util.ArrayList;
import java.util.List;

import org.jruby.ast.RootNode;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.core.IRubyModelMarker;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.compiler.BuildContext;
import org.rubypeople.rdt.core.compiler.CategorizedProblem;
import org.rubypeople.rdt.core.compiler.CompilationParticipant;
import org.rubypeople.rdt.core.compiler.ReconcileContext;
import org.rubypeople.rdt.core.parser.warnings.DelegatingVisitor;
import org.rubypeople.rdt.internal.core.parser.RubyParser;

public class RubyCodeAnalyzer extends CompilationParticipant {
	
	@Override
	public boolean isActive(IRubyProject project) {
		return true;
	}
	
	@Override
	public void buildStarting(BuildContext[] files, boolean isBatch) {
		for (int i = 0; i < files.length; i++) {
			BuildContext context = files[i];
		
			String contents = new String(context.getContents());
			List<CategorizedProblem> problems = parse(RubyCore.create(context.getFile()), contents, null);	
			context.recordNewProblems(problems.toArray(new CategorizedProblem[problems.size()]));			
		}
	}
	
	private List<CategorizedProblem> parse(IRubyScript script, String contents, RootNode ast) {
		if (ast == null) {
			try {
				RubyParser parser = new RubyParser();
				ast = (RootNode) parser.parse(script.getElementName(), contents).getAST();
			} catch (SyntaxException e) { // ignore syntax exceptions
				List<CategorizedProblem> problems = new ArrayList<CategorizedProblem>();
				problems.add(SyntaxExceptionHandler.handle(e, contents));
				return problems;
			}
		}
		DelegatingVisitor visitor = new DelegatingVisitor(DelegatingVisitor.createVisitors(script, contents));
		ast.accept(visitor);
		return visitor.getProblems();			
	}
	
	@Override
	public void reconcile(ReconcileContext context) {
		try {
			List<CategorizedProblem> problems = parse(context.getWorkingCopy(), context.getWorkingCopy().getSource(), context.getAST());	
			addProblems(context, IRubyModelMarker.RUBY_MODEL_PROBLEM_MARKER, problems);
		} catch (RubyModelException e) {
			RubyCore.log(e);
		}
	}
}
