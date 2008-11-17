package org.rubypeople.rdt.internal.core.search.matching;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.jruby.ast.CallNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.IArgumentNode;
import org.jruby.ast.Node;
import org.jruby.ast.VCallNode;
import org.jruby.ast.types.INameNode;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.RubyScript;
import org.rubypeople.rdt.internal.core.parser.InOrderVisitor;
import org.rubypeople.rdt.internal.core.parser.RubyParser;

public class MethodLocator extends PatternLocator {

	private MethodPattern pattern;

	public MethodLocator(MethodPattern pattern) {
		super(pattern);
		this.pattern = pattern;
	}

	@Override
	public void reportMatches(RubyScript script, MatchLocator locator) {
		if (!this.pattern.findReferences) {
			reportMatches((IParent) script, locator);
		} else {
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
				public Instruction visitVCallNode(VCallNode iVisited) {
					match(iVisited, 0);
					return super.visitVCallNode(iVisited);
				}
				
				@Override
				public Instruction visitFCallNode(FCallNode iVisited) {
					match(iVisited, getArgumentsFromFunctionCall(iVisited).size());
					return super.visitFCallNode(iVisited);
				}
				
				@Override
				public Instruction visitCallNode(CallNode iVisited) {
					match(iVisited, getArgumentsFromFunctionCall(iVisited).size());
					return super.visitCallNode(iVisited);
				}
				
				@Override
				public Instruction visitDefnNode(DefnNode iVisited) {
					if (findDeclarations) matchDeclaration(iVisited, iVisited.getArgsNode().getRequiredArgsCount());
					return super.visitDefnNode(iVisited);
				}
				
				@Override
				public Instruction visitDefsNode(DefsNode iVisited) {
					if (findDeclarations) matchDeclaration(iVisited, iVisited.getArgsNode().getRequiredArgsCount());
					return super.visitDefsNode(iVisited);
				}
				
				private void match(Node iVisited, int arity) {
					String name = ((INameNode)iVisited).getName();
					int accuracy = getAccuracy(name, arity);
					if (accuracy != IMPOSSIBLE_MATCH) {
						try {
							IRubyElement element = script.getElementAt(iVisited.getPosition().getStartOffset());
							if (element == null) element = script;
							if (locator.encloses(element)) {
								IRubyElement binding = resolve(element, iVisited);
								int start = iVisited.getPosition().getStartOffset();
								int length = iVisited.getPosition().getEndOffset() - start;
								boolean isConstructor = false;
								if (name.equals("new")) {
									isConstructor = true;
								}
								List<String> args = new ArrayList<String>();
								if (iVisited instanceof IArgumentNode) {
									args = getArgumentsFromFunctionCall((IArgumentNode) iVisited);
								}
								locator.report(locator.newMethodReferenceMatch(element, binding, args, accuracy, 
									start, length, isConstructor, iVisited));
							}
						} catch (CoreException e) {
							RubyCore.log(e);
						}
					}
				}
				
				private IRubyElement resolve(IRubyElement element, Node visited) {
					// TODO resolve a method call to it's declaration!
					return element;
				}

				private void matchDeclaration(Node iVisited, int arity) {
						String name = ((INameNode)iVisited).getName();
						int accuracy = getAccuracy(name, arity);
						if (accuracy != IMPOSSIBLE_MATCH) {
							try {
								IRubyElement element = script.getElementAt(iVisited.getPosition().getStartOffset());
								if (element == null) element = script;
								if (locator.encloses(element)) {
									int start = iVisited.getPosition().getStartOffset();
									int length = iVisited.getPosition().getEndOffset() - start;
									locator.report(locator.newDeclarationMatch(element, accuracy, 
										start, length));
								}
							} catch (CoreException e) {
								RubyCore.log(e);
							}
						}
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
				if (child.isType(IRubyElement.METHOD) && locator.encloses(child)) {		
					IMethod method = (IMethod) child;					
					int accuracy = getAccuracy(method);
					if (accuracy != IMPOSSIBLE_MATCH) {
						IMember member = (IMember) child;
						ISourceRange range = member.getSourceRange();
						try {
							locator.report(locator.newDeclarationMatch(child, accuracy, range.getOffset(), range.getLength()));
						} catch (CoreException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				if (child instanceof IParent) {
					IParent parentTwo = (IParent) child;
					reportMatches(parentTwo, locator);
				}
			}
		} catch (RubyModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private int getAccuracy(IMethod method) throws RubyModelException {
		int accuracy = getAccuracy(method.getElementName(), method.getParameterNames().length);
		if (accuracy == IMPOSSIBLE_MATCH) return accuracy;
		
		// Compare declaring type
		IType type = method.getDeclaringType();
		char[] declaringTypeName = new char[0];
		if (type != null)
		{
			declaringTypeName = type.getElementName().toCharArray();
		}
		if (pattern.declaringSimpleName != null && !matchesName(pattern.declaringSimpleName, declaringTypeName))
			return IMPOSSIBLE_MATCH;
		
		return ACCURATE_MATCH;
	}

	private int getAccuracy(String name, int arity) {
		// Verify method name
		if (!matchesName(this.pattern.selector, name.toCharArray()))
			return IMPOSSIBLE_MATCH;

		// Verify parameter count
		if (this.pattern.parameterNames != null) {
			int length = this.pattern.parameterNames.length;
			if (length != arity)
				return IMPOSSIBLE_MATCH;
		}

		// Method may match pattern
		return ACCURATE_MATCH;
	}

}
