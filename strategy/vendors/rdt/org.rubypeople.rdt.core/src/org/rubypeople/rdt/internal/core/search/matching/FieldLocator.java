package org.rubypeople.rdt.internal.core.search.matching;

import org.eclipse.core.runtime.CoreException;
import org.jruby.ast.ClassVarAsgnNode;
import org.jruby.ast.ClassVarNode;
import org.jruby.ast.ConstDeclNode;
import org.jruby.ast.GlobalAsgnNode;
import org.jruby.ast.GlobalVarNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.Node;
import org.jruby.ast.types.INameNode;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.RubyScript;
import org.rubypeople.rdt.internal.core.parser.InOrderVisitor;
import org.rubypeople.rdt.internal.core.parser.RubyParser;

public class FieldLocator extends PatternLocator {

	private FieldPattern pattern;

	public FieldLocator(FieldPattern pattern) {
		super(pattern);
		this.pattern = pattern;
	}
	
	@Override
	public void reportMatches(final RubyScript script, final MatchLocator locator) {
		if (!this.pattern.findReferences) { // just traverse our own model
			reportMatches((IParent) script, locator);
		} else { // they want references too, so we need to traverse the AST
			reportASTMatches(script, locator);
		}		
	}

	private void reportASTMatches(final RubyScript script, final MatchLocator locator) {
		try {
			Node ast = script.lastGoodAST;		
			if (ast == null) {
				ast = new RubyParser().parse(script.getElementName(), script.getSource()).getAST();
			}
			final boolean findDeclarations = this.pattern.findDeclarations;
			new InOrderVisitor() {
			
				@Override
				public Instruction visitInstVarNode(InstVarNode iVisited) {
					match(iVisited);
					return super.visitInstVarNode(iVisited);
				}

				// XXX Handle constant references!
			
				@Override
				public Instruction visitGlobalVarNode(GlobalVarNode iVisited) {
					match(iVisited);
					return super.visitGlobalVarNode(iVisited);
				}
				
				@Override
				public Instruction visitConstDeclNode(ConstDeclNode iVisited) {
					if (findDeclarations) match(iVisited);
					return super.visitConstDeclNode(iVisited);
				}
			
				@Override
				public Instruction visitGlobalAsgnNode(GlobalAsgnNode iVisited) {
					match(iVisited); // TODO check whether we want to find write references
					return super.visitGlobalAsgnNode(iVisited);
				}
				
				@Override
				public Instruction visitClassVarNode(ClassVarNode iVisited) {					
					match(iVisited);
					return super.visitClassVarNode(iVisited);
				}
				
				@Override
				public Instruction visitClassVarAsgnNode(ClassVarAsgnNode iVisited) {
					match(iVisited); // TODO check whether we want to find write references
					return super.visitClassVarAsgnNode(iVisited);
				}
				
				@Override
				public Instruction visitInstAsgnNode(InstAsgnNode iVisited) {
					match(iVisited); // TODO check whether we want to find write references
					return super.visitInstAsgnNode(iVisited);
				}
				
				private void match(Node iVisited) {
					int accuracy = getAccuracy(((INameNode)iVisited).getName());
					if (accuracy != IMPOSSIBLE_MATCH) {
						try {
							IRubyElement element = script.getElementAt(iVisited.getPosition().getStartOffset());
							if (element == null) element = script;
							if (locator.encloses(element)) {
								IRubyElement binding = resolve(element, ((INameNode)iVisited).getName());
								locator.report(locator.newFieldReferenceMatch(element, binding, accuracy, 
									iVisited.getPosition().getStartOffset(), iVisited.getPosition().getEndOffset() - iVisited.getPosition().getStartOffset(), iVisited));
							}
						} catch (CoreException e) {
							RubyCore.log(e);
						}
					}
				}

				private IRubyElement resolve(IRubyElement element, String name) {
					if (element instanceof IMember) {
						IMember member = (IMember) element;
						IType type = member.getDeclaringType();
						return type.getField(name);
					}
					return null;
				}
				
			
			}.acceptNode(ast);
		} catch(RubyModelException e) {
			RubyCore.log(e);
		}
	}

	private void reportMatches(IParent parent, MatchLocator locator) {
		try {
			IRubyElement[] children = parent.getChildren();
			for (int i = 0; i < children.length; i++) {
				IRubyElement child = children[i];
				if ((child.isType(IRubyElement.FIELD) || 
						child.isType(IRubyElement.GLOBAL) ||
						child.isType(IRubyElement.CONSTANT) ||
						child.isType(IRubyElement.CLASS_VAR) ||
						child.isType(IRubyElement.INSTANCE_VAR)) && (locator.encloses(child))) {
					int accuracy = getAccuracy(child.getElementName());
					if (accuracy != IMPOSSIBLE_MATCH) {
						IMember member = (IMember) child;
						ISourceRange range = member.getSourceRange();
						try {
							locator.report(locator.newDeclarationMatch(child, accuracy, range.getOffset(), range.getLength()));
						} catch (CoreException e) {
							RubyCore.log(e);
						}
					}
				}
				if (child instanceof IParent) {
					IParent parentTwo = (IParent) child;
					reportMatches(parentTwo, locator);
				}
			}
		} catch (RubyModelException e) {
			RubyCore.log(e);
		}
	}

	private int getAccuracy(String name) {
		if (this.pattern.findReferences)
			if (matchesName(this.pattern.name, name.toCharArray()))
				return ACCURATE_MATCH;

		if (this.pattern.findDeclarations) {
			if (matchesName(this.pattern.name, name.toCharArray()))
				return ACCURATE_MATCH;

		}
		return IMPOSSIBLE_MATCH;
	}

}
