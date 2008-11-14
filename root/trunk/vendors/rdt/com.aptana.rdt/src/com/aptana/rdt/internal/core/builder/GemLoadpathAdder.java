package com.aptana.rdt.internal.core.builder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jruby.ast.FCallNode;
import org.jruby.ast.Node;
import org.jruby.evaluator.Instruction;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.compiler.BuildContext;
import org.rubypeople.rdt.core.compiler.CompilationParticipant;
import org.rubypeople.rdt.internal.core.parser.InOrderVisitor;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.core.util.ASTUtil;

import com.aptana.rdt.AptanaRDTPlugin;
import com.aptana.rdt.core.gems.Gem;

public class GemLoadpathAdder extends CompilationParticipant {
	
	private IRubyProject project;

	@Override
	public int aboutToBuild(IRubyProject project) {
		this.project = project;
		return super.aboutToBuild(project);
	}
	
	@Override
	public void buildStarting(BuildContext[] files, boolean isBatch) {
		RubyParser parser = new RubyParser();
		Collection<String> gems = new HashSet<String>();
		for (int i = 0; i < files.length; i++) {
			gems.addAll(getGemNames(parser, files[i]));			
		}
		for (String gemName : gems) {
			if (gemName.equals("rails")) continue; // HACK don't do this for rails
			try {
				AptanaRDTPlugin.addGemLoadPath(project, new Gem(gemName, "", ""), null);
			} catch (RubyModelException e) {
				AptanaRDTPlugin.log(e);
			}
		}
	}
	
	private Collection<String> getGemNames(RubyParser parser, BuildContext context) {
		try {
			char[] contents = context.getContents();			
			Node root = parser.parse(new String(contents)).getAST();
			GemVisitor visitor = new GemVisitor();
			root.accept(visitor);
			return visitor.getGems();
		} catch (SyntaxException e) {
			// ignore
		} catch (Exception e) {
			RubyCore.log(e);
		}			
		return Collections.emptyList();
	}
	
	@Override
	public boolean isActive(IRubyProject project) {
		return true;
	}
	
	private static class GemVisitor extends InOrderVisitor {
		
		private Set<String> gems;
		
		public GemVisitor() {
			gems = new HashSet<String>();
		}
		
		@Override
		public Instruction visitFCallNode(FCallNode iVisited) {
			String name = iVisited.getName();
			if (name.equals("gem") || name.equals("require_gem")) {
				List<String> args = ASTUtil.getArgumentsFromFunctionCall(iVisited);
				if (args != null && !args.isEmpty()) {
					String gemName = args.get(0);
					if (gemName.startsWith("\"") || gemName.startsWith("'")) {
						gemName = gemName.substring(1, gemName.length() - 1);
					}
					gems.add(gemName);
				}
				
			}
			return super.visitFCallNode(iVisited);
		}
		
		public Collection<String> getGems() {
			return gems;
		}
	}

}
