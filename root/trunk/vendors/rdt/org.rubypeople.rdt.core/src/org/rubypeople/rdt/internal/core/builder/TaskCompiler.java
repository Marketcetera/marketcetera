package org.rubypeople.rdt.internal.core.builder;

import java.util.List;

import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.compiler.BuildContext;
import org.rubypeople.rdt.core.compiler.CategorizedProblem;
import org.rubypeople.rdt.core.compiler.CompilationParticipant;
import org.rubypeople.rdt.internal.core.parser.TaskParser;
import org.rubypeople.rdt.internal.core.parser.TaskTag;

public class TaskCompiler extends CompilationParticipant {
	
	 private TaskParser taskParser;
	 	 
	@Override
	public int aboutToBuild(IRubyProject project) {
		taskParser = new TaskParser(project.getOptions(true));
		return super.aboutToBuild(project);
	}
	
	@Override
	public boolean isActive(IRubyProject project) {
		return true;
	}
	
	@Override
	public void buildStarting(BuildContext[] files, boolean isBatch) {
		for (int i = 0; i < files.length; i++) {
			BuildContext context = files[i];
			taskParser.parse(new String(context.getContents()));			
			List<TaskTag> tasks = taskParser.getTasks();
			context.recordNewProblems(tasks.toArray(new CategorizedProblem[tasks.size()]));
			taskParser.clear();
		}
	}

}
