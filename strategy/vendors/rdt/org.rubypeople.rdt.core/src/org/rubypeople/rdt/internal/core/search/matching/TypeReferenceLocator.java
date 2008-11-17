package org.rubypeople.rdt.internal.core.search.matching;

import org.eclipse.core.runtime.CoreException;
import org.jruby.ast.ConstNode;
import org.jruby.ast.Node;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.search.SearchPattern;
import org.rubypeople.rdt.internal.core.RubyScript;
import org.rubypeople.rdt.internal.core.parser.InOrderVisitor;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.core.util.CharOperation;


public class TypeReferenceLocator extends PatternLocator {

	private TypeReferencePattern pattern;

	public TypeReferenceLocator(TypeReferencePattern pattern) {
		super(pattern);

		this.pattern = pattern;
	}

	
	@Override
	public void reportMatches(RubyScript script, MatchLocator locator) {
		Node ast = script.lastGoodAST;
		if (ast == null) {			
			try {
				ast = new RubyParser().parse(script.getSource()).getAST();
			} catch (RubyModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		InOrderVisitor visitor = new TypeRefASTVisitor(script, pattern, locator);
		visitor.acceptNode(ast);
	}
	
	private class TypeRefASTVisitor extends InOrderVisitor {

		private TypeReferencePattern pattern;
		private MatchLocator locator;
		private RubyScript script;

		public TypeRefASTVisitor(RubyScript script, TypeReferencePattern pattern, MatchLocator locator) {
			this.script = script;
			this.pattern = pattern;
			this.locator = locator;
		}
		
		private int resolveLevel(char[] sourceName) {
			char[] qualifiedPattern = getQualifiedPattern(pattern.simpleName, pattern.qualification);			
			if (sourceName == null) return IMPOSSIBLE_MATCH;
			if ((pattern.matchMode & SearchPattern.R_PREFIX_MATCH) != 0) {
				if (CharOperation.prefixEquals(qualifiedPattern, sourceName, pattern.isCaseSensitive)) {
					return ACCURATE_MATCH;
				}
			}
			if (pattern.isCamelCase) {
				if (!pattern.isCaseSensitive || (qualifiedPattern.length>0 && sourceName.length>0 && qualifiedPattern[0] == sourceName[0])) {
					if (CharOperation.camelCaseMatch(qualifiedPattern, sourceName)) {
						return ACCURATE_MATCH;
					}
				}
				if (pattern.matchMode == SearchPattern.R_EXACT_MATCH) {
					boolean matchPattern = CharOperation.prefixEquals(qualifiedPattern, sourceName, pattern.isCaseSensitive);
					return matchPattern ? ACCURATE_MATCH : IMPOSSIBLE_MATCH;
				}
			}
			boolean matchPattern = CharOperation.match(qualifiedPattern, sourceName, pattern.isCaseSensitive);
			return matchPattern ? ACCURATE_MATCH : IMPOSSIBLE_MATCH;
		}
		
		protected char[] getQualifiedPattern(char[] simpleNamePattern, char[] qualificationPattern) {
			// NOTE: if case insensitive search then simpleNamePattern & qualificationPattern are assumed to be lowercase
			if (simpleNamePattern == null) {
				if (qualificationPattern == null) return null;
				return CharOperation.concat(qualificationPattern, ONE_STAR, "::");
			} else if (qualificationPattern == null) {
				return simpleNamePattern;
			} else {
				return CharOperation.concat(qualificationPattern, simpleNamePattern, "::");
			}
		}
		
		@Override
		public Instruction visitConstNode(ConstNode iVisited) {
			String constantName = iVisited.getName();
			int accuracy = resolveLevel(constantName.toCharArray());
			if (accuracy != IMPOSSIBLE_MATCH) {				
				try {
					IRubyElement enclosingElement = script.getElementAt(iVisited.getPosition().getStartOffset());
					// FIXME Fix the accuracy level to A_ACCURATE OR A_INACCURATE
					locator.report(locator.newTypeReferenceMatch(enclosingElement, accuracy, iVisited.getPosition().getStartOffset(), iVisited.getPosition().getEndOffset() - iVisited.getPosition().getStartOffset()));
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return super.visitConstNode(iVisited);
		}

	}
}
