/**
 * 
 */
package org.rubypeople.rdt.internal.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.jruby.ast.RootNode;
import org.jruby.common.IRubyWarnings;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.core.IRubyModelMarker;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.compiler.CategorizedProblem;
import org.rubypeople.rdt.core.parser.warnings.DelegatingVisitor;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;
import org.rubypeople.rdt.internal.core.builder.SyntaxExceptionHandler;
import org.rubypeople.rdt.internal.core.parser.RdtWarnings;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.core.parser.TaskParser;
import org.rubypeople.rdt.internal.core.parser.TaskTag;

/**
 * @author Chris
 * 
 */
public class RubyScriptProblemFinder {

    // DSC convert to ImmediateWarnings
    public static RootNode process(RubyScript script, char[] charContents, HashMap problems, IProgressMonitor pm) {
        RdtWarnings warnings = new RdtWarnings(script.getElementName());
        String contents = new String(charContents);

        List<CategorizedProblem> generatedProblems = new ArrayList<CategorizedProblem>();
        RootNode ast = null;
        try {
        	ast = parse(script, contents, warnings);
        	generatedProblems = runLint(script, ast, contents); // FIXME Make all these compilationParticipants
        } catch (SyntaxException e) {
        	generatedProblems.add(SyntaxExceptionHandler.handle(e, contents));
        }
        TaskParser taskParser = new TaskParser(script.getRubyProject().getOptions(true));
        taskParser.parse(contents);
        generatedProblems.addAll(warnings.getWarnings());
        List<TaskTag> tasks = taskParser.getTasks();
        problems.put(IRubyModelMarker.RUBY_MODEL_PROBLEM_MARKER, generatedProblems.toArray(new CategorizedProblem[generatedProblems.size()]));
        problems.put(IRubyModelMarker.TASK_MARKER, tasks.toArray(new CategorizedProblem[tasks.size()]));
        
        return ast;
    }

	private static RootNode parse(RubyScript script, String contents, IRubyWarnings warnings) {
	    try {
	    	RubyParser parser = new RubyParser(warnings);
	    	return (RootNode) parser.parse((IFile) script.getUnderlyingResource(), contents).getAST();
	    } catch (CoreException e) {
			RubyCore.log(e);
		}
	    return null;		
	}

	private static List<CategorizedProblem> runLint(RubyScript script, RootNode node, String contents) {
    	if (node == null) return new ArrayList<CategorizedProblem>();
        List<RubyLintVisitor> visitors = DelegatingVisitor.createVisitors(script, contents);
        DelegatingVisitor visitor = new DelegatingVisitor(visitors);
        node.accept(visitor);
        return visitor.getProblems();
	}
}
