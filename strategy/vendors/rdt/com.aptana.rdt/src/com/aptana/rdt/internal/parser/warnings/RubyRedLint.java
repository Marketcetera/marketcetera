package com.aptana.rdt.internal.parser.warnings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jruby.ast.RootNode;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.core.IRubyModelMarker;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.compiler.BuildContext;
import org.rubypeople.rdt.core.compiler.CategorizedProblem;
import org.rubypeople.rdt.core.compiler.CompilationParticipant;
import org.rubypeople.rdt.core.compiler.ReconcileContext;
import org.rubypeople.rdt.core.parser.warnings.DelegatingVisitor;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;
import org.rubypeople.rdt.internal.core.parser.RubyParser;

import com.aptana.rdt.AptanaRDTPlugin;

public class RubyRedLint extends CompilationParticipant {
	
	@Override
	public void reconcile(ReconcileContext context) {		
		try {
			List<CategorizedProblem> problems = parse(context.getWorkingCopy().getElementName(), context.getWorkingCopy().getSource(), context.getAST());	
			addProblems(context, IRubyModelMarker.RUBY_MODEL_PROBLEM_MARKER, problems);
		} catch (RubyModelException e) {
			AptanaRDTPlugin.log(e);
		}
	}
	
	private List<CategorizedProblem> parse(String name, String contents, RootNode ast) {
		if (ast == null) {
			try {
				RubyParser parser = new RubyParser();
				ast = (RootNode) parser.parse(name, contents).getAST();
			} catch (SyntaxException e) { // ignore syntax exceptions
				return Collections.EMPTY_LIST;
			}
		}
		DelegatingVisitor visitor = new DelegatingVisitor(createLintVisitors(contents));
		ast.accept(visitor);
		return visitor.getProblems();			
	}
	
	@Override
	public void buildStarting(BuildContext[] files, boolean isBatch) {
		super.buildStarting(files, isBatch);
		for (int i = 0; i < files.length; i++ ) { // parse and analyze each file
			BuildContext context = files[i];
			String contents = new String(context.getContents());
			List<CategorizedProblem> problems = parse(context.getFile().getName(), contents, null);
			context.recordNewProblems(problems.toArray(new CategorizedProblem[problems.size()]));
		}
	}
	
	@Override
	public boolean isActive(IRubyProject project) {
		return true;
	}

	private List<RubyLintVisitor> createLintVisitors(String contents) {
		List<RubyLintVisitor> visitors = new ArrayList<RubyLintVisitor>();
		visitors.add(new AccidentalBooleanAssignmentVisitor(contents));
		visitors.add(new UnusedPrivateMethodVisitor(contents));
		visitors.add(new MisspelledConstructorVisitor(contents));
		visitors.add(new LocalsMaskingMethodsVisitor(contents));
		visitors.add(new UnusedParameterVisitor(contents));
		visitors.add(new UnecessaryElseVisitor(contents));
		visitors.add(new TooManyLocalsVisitor(contents));
		visitors.add(new TooManyLinesVisitor(contents));
		visitors.add(new TooManyBranchesVisitor(contents));
		visitors.add(new TooManyArgumentsVisitor(contents));
		visitors.add(new TooManyReturnsVisitor(contents));
		visitors.add(new SimilarVariableNameVisitor(contents));
		visitors.add(new SubclassCallsSuper(contents));
		visitors.add(new ComparableInclusionVisitor(contents));
		visitors.add(new EnumerableInclusionVisitor(contents));
		visitors.add(new AndOrUsedOnRighthandAssignment(contents));
		visitors.add(new ConstantNamingConvention(contents));
		visitors.add(new MethodMissingWithoutRespondTo(contents));
		visitors.add(new DynamicVariableAliasesLocal(contents));
		visitors.add(new LocalVariablePossibleAttributeAccess(contents));
		visitors.add(new LocalAndMethodNamingConvention(contents));
		visitors.add(new UnusedLocalVariable(contents));
		visitors.add(new RequireGemChecker(contents));
		visitors.add(new RetryOutsideRescueBodyChecker(contents));
		List<RubyLintVisitor> filtered = new ArrayList<RubyLintVisitor>();
		for (RubyLintVisitor visitor : visitors) {
			if (visitor.isIgnored()) continue;
			filtered.add(visitor);
		}
		return filtered;
	}
}
