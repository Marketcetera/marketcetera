package org.rubypeople.rdt.internal.core.parser.warnings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.jruby.ast.ClassNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.Node;
import org.jruby.ast.RootNode;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;
import org.rubypeople.rdt.core.search.CollectingSearchRequestor;
import org.rubypeople.rdt.core.search.IRubySearchConstants;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchEngine;
import org.rubypeople.rdt.core.search.SearchMatch;
import org.rubypeople.rdt.core.search.SearchParticipant;
import org.rubypeople.rdt.core.search.SearchPattern;
import org.rubypeople.rdt.internal.core.util.ASTUtil;

public class CoreClassReOpening extends RubyLintVisitor {

	private List<Node> typeStack;
	private RootNode rootNode;
	private IRubyScript script;
	private static Set<String> coreTypes = new HashSet<String>();

	static {
		coreTypes.add("Array");
		coreTypes.add("Bignum");
		coreTypes.add("Class");
		coreTypes.add("Complex");
		coreTypes.add("Date");
		coreTypes.add("DateTime");
		coreTypes.add("Enumerable");
		coreTypes.add("FalseClass");
		coreTypes.add("Fixnum");		
		coreTypes.add("Float");
		coreTypes.add("NilClass");
		coreTypes.add("Numeric");
		coreTypes.add("Rational");
		coreTypes.add("Regexp");
		coreTypes.add("Set");
		coreTypes.add("String");
		coreTypes.add("Time");
		coreTypes.add("TrueClass");
	}
	
	public CoreClassReOpening(IRubyScript script, String contents) {
		super(contents);
		this.script = script;
		typeStack = new ArrayList<Node>();
	}

	@Override
	protected String getOptionKey() {
		return RubyCore.COMPILER_PB_REDEFINITION_CORE_CLASS_METHOD;
	}
	
	@Override
	public Instruction visitDefnNode(DefnNode iVisited) {
		String typeName = getCurrentTypeName();
		if (isCoreClass(typeName)) {
			String methodName = iVisited.getName();
			if (methodExistsOnType(typeName, methodName))
				createProblem(iVisited.getPosition(), "Redefinition of a Ruby Core class method is dangerous");
		}
		return super.visitDefnNode(iVisited);
	}
	
	private boolean methodExistsOnType(String typeName, String methodName) {
		IType type = getType(typeName);
		if (type == null) return false;
		try {
			SearchParticipant[] participants = new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant()};
			SearchEngine engine = new SearchEngine();
			IRubySearchScope scope = SearchEngine.createRubySearchScope(new IRubyElement[] { type });
			CollectingSearchRequestor requestor = new CollectingSearchRequestor();
			SearchPattern pattern = SearchPattern.createPattern(IRubyElement.METHOD, methodName, IRubySearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
			engine.search(pattern, participants, scope, requestor, null);
			List<SearchMatch> matches = requestor.getResults();
			if (matches == null || matches.isEmpty()) return false;
			return true;
		} catch (RubyModelException e) {
			RubyCore.log(e);
		} catch (CoreException e) {
			RubyCore.log(e);
		}
		return false;
	}

	private IType getType(String typeName) {
		try {
			SearchParticipant[] participants = new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant()};
			SearchEngine engine = new SearchEngine();
			ISourceFolderRoot[] roots = script.getRubyProject().getAllSourceFolderRoots();
			ISourceFolderRoot stubs = findCoreStubsRoot(roots);
			IRubySearchScope scope = SearchEngine.createRubySearchScope(new IRubyElement[] { stubs });
			CollectingSearchRequestor requestor = new CollectingSearchRequestor();
			SearchPattern pattern = SearchPattern.createPattern(IRubyElement.TYPE, typeName, IRubySearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
			engine.search(pattern, participants, scope, requestor, null);
			List<SearchMatch> matches = requestor.getResults();
			if (matches == null || matches.isEmpty()) return null;
			return (IType) matches.get(0).getElement();
		} catch (RubyModelException e) {
			RubyCore.log(e);
		} catch (CoreException e) {
			RubyCore.log(e);
		}
		return null;
	}

	private ISourceFolderRoot findCoreStubsRoot(ISourceFolderRoot[] roots) {
		for (int i = 0; i < roots.length; i++) {
			ISourceFolderRoot root = roots[i];
			IPath path = root.getPath();
			if (path.segmentCount() < 3) continue;
			String segment = path.segment(path.segmentCount() - 3);
			if (segment.equals("org.rubypeople.rdt.launching")) {
				return root;
			}
		}
		return null;
	}

	private boolean isCoreClass(String typeName) {
		return coreTypes.contains(typeName);
	}

	@Override
	public Instruction visitRootNode(RootNode iVisited) {
		this.rootNode = iVisited;
		return super.visitRootNode(iVisited);
	}

	private String getCurrentTypeName() {
		Node typeNode = typeStack.get(typeStack.size() - 1);
		String typeName = ASTUtil.getFullyQualifiedTypeName(rootNode, typeNode);
		return typeName;
	}

	@Override
	public Instruction visitClassNode(ClassNode iVisited) {
		push(iVisited);
		return super.visitClassNode(iVisited);
	}
	
	@Override
	public void exitClassNode(ClassNode iVisited) {
		pop();
		super.exitClassNode(iVisited);
	}
	
	@Override
	public Instruction visitModuleNode(ModuleNode iVisited) {
		push(iVisited);
		return super.visitModuleNode(iVisited);
	}

	@Override
	public void exitModuleNode(ModuleNode iVisited) {
		pop();
		super.exitModuleNode(iVisited);
	}
	
	private void pop() {
		typeStack.remove(typeStack.size() - 1);
	}
	
	private void push(Node visited) {
		typeStack.add(visited);		
	}
}
