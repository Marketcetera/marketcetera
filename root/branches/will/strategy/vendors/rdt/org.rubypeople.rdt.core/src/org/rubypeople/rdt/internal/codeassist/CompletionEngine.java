package org.rubypeople.rdt.internal.codeassist;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jruby.ast.ClassNode;
import org.jruby.ast.ClassVarAsgnNode;
import org.jruby.ast.ClassVarDeclNode;
import org.jruby.ast.ClassVarNode;
import org.jruby.ast.Colon2Node;
import org.jruby.ast.ConstDeclNode;
import org.jruby.ast.ConstNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.IterNode;
import org.jruby.ast.LocalVarNode;
import org.jruby.ast.MethodDefNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.Node;
import org.jruby.ast.RootNode;
import org.jruby.ast.SelfNode;
import org.jruby.ast.YieldNode;
import org.jruby.evaluator.Instruction;
import org.jruby.lexer.yacc.SyntaxException;
import org.jruby.parser.StaticScope;
import org.rubypeople.rdt.core.CompletionProposal;
import org.rubypeople.rdt.core.CompletionRequestor;
import org.rubypeople.rdt.core.Flags;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IOpenable;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.ITypeHierarchy;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.search.CollectingSearchRequestor;
import org.rubypeople.rdt.core.search.IRubySearchConstants;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchMatch;
import org.rubypeople.rdt.core.search.SearchParticipant;
import org.rubypeople.rdt.core.search.SearchPattern;
import org.rubypeople.rdt.internal.core.LogicalType;
import org.rubypeople.rdt.internal.core.RubyConstant;
import org.rubypeople.rdt.internal.core.RubyElement;
import org.rubypeople.rdt.internal.core.RubyType;
import org.rubypeople.rdt.internal.core.SourceElementParser;
import org.rubypeople.rdt.internal.core.parser.InOrderVisitor;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.core.search.BasicSearchEngine;
import org.rubypeople.rdt.internal.core.util.ASTUtil;
import org.rubypeople.rdt.internal.core.util.Util;
import org.rubypeople.rdt.internal.ti.BasicTypeGuess;
import org.rubypeople.rdt.internal.ti.DefaultTypeInferrer;
import org.rubypeople.rdt.internal.ti.ITypeGuess;
import org.rubypeople.rdt.internal.ti.ITypeInferrer;
import org.rubypeople.rdt.internal.ti.util.AttributeLocator;
import org.rubypeople.rdt.internal.ti.util.ClosestSpanningNodeLocator;
import org.rubypeople.rdt.internal.ti.util.INodeAcceptor;
import org.rubypeople.rdt.internal.ti.util.ScopedNodeLocator;

public class CompletionEngine {
	private static final String OBJECT = "Object";
	private static final String CONSTRUCTOR_INVOKE_NAME = "new";
	private static final String CONSTRUCTOR_DEFINITION_NAME = "initialize";
	
	private CompletionRequestor fRequestor;
	private CompletionContext fContext;
	private Set<IType> fVisitedTypes;
	/**
	 * temporary place to hold the original type we're completing for. Used to determine if we should be showing private methods.
	 */
	private IType fOriginalType;

	public CompletionEngine(CompletionRequestor requestor) {
		this.fRequestor = requestor;
	}

	public void complete(IRubyScript script, int offset) throws RubyModelException {
		this.fRequestor.beginReporting();		
		fContext = new CompletionContext(script, offset);
		if (fContext.emptyPrefix()) { // no prefix, so we could suggest anything
			suggestMethodsForEnclosingType(script);
			getDocumentsRubyElementsInScope();
			suggestGlobals();
		} else {
			if (fContext.isDoubleSemiColon()) {				
				String prefix = fContext.getFullPrefix();
				String typeName = prefix.substring(0, prefix.lastIndexOf("::"));
				RubyElementRequestor requestor = new RubyElementRequestor(script);
				Map<String, CompletionProposal> proposals = new HashMap<String, CompletionProposal>();
				if (fContext.isBroken()) {
					Map<IMethod, String> astMethods = addASTProposals(typeName);
					for (IMethod method : astMethods.keySet()) {
						if (!method.isSingleton()) continue;
						CompletionProposal proposal = suggestMethod(method, astMethods.get(method), 100);
						if (proposal == null) continue;
						proposals.put(proposal.getName(), proposal);
					}
					// FIXME Add constants!
					addASTTypeConstants(typeName);
				}
				IType[] types = requestor.findType(typeName);
				for (int i = 0; i < types.length; i++) {
					IType type = types[i];
					proposals.putAll(suggestTypesConstants(type));
					// Suggest nested types
					proposals.putAll(suggestNestedTypes(type));
					// Suggest class level methods
					proposals.putAll(suggestMethods(100, type, false));
				}
				List<CompletionProposal> list = new ArrayList<CompletionProposal>(proposals.values());
				Collections.sort(list, new CompletionProposalComparator());
				for (CompletionProposal proposal : list) {
					if (proposal.getCompletion().startsWith(fContext.getPartialPrefix()))
						fRequestor.accept(proposal);
				}				
				
				this.fRequestor.endReporting();
				fContext = null;
				return;
			}
			if (fContext.isConstant()) { // type or constant
				suggestTypeNames();
				suggestConstantNames();
				return;
			} 
			if (fContext.isGlobal()) { // looks like a global
				suggestGlobals();
				return;
			}
			if (fContext.isExplicitMethodInvokation()) {
				ITypeInferrer inferrer = new DefaultTypeInferrer();
				Collection<ITypeGuess> guesses = inferrer.infer(fContext.getCorrectedSource(), fContext.getOffset());
				if (guesses.isEmpty()) {
					guesses.add(new BasicTypeGuess(OBJECT, 100));
				}
				List<CompletionProposal> list = new ArrayList<CompletionProposal>();
				RubyElementRequestor requestor = new RubyElementRequestor(script);
				for (ITypeGuess guess : guesses) {
					final String name = guess.getType();
					if (fContext.isBroken()) {
						Map<IMethod, String> astMethods = addASTProposals(name);
						for (IMethod method : astMethods.keySet()) { // FIXME Don't suggest instance method if we're invoking on the actual typename/constant!
							CompletionProposal proposal = suggestMethod(method, astMethods.get(method), 100);
							if (proposal == null) continue;
							list.add(proposal);
						}			
					}					
					IType[] types = requestor.findType(name);
					// if we don't find type (probably because it's only in current working copy), find Object because all types stems from there
					if (types == null || types.length == 0) {
						types = requestor.findType(OBJECT);
					}
					Map<String, CompletionProposal> mapAll = new HashMap<String, CompletionProposal>();
					if (types != null && types.length > 0) {
						LogicalType type = new LogicalType(types);
						mapAll.putAll(suggestMethods(guess.getConfidence(), type, true));
					}				
					
					// if there isn't one, add a "new" constructor
					if (!mapAll.containsKey("new") && fContext.fullPrefixIsConstant() && "new".startsWith(fContext.getPartialPrefix())) {
						CompletionProposal proposal = new CompletionProposal(CompletionProposal.METHOD_REF, "new", 100);
						proposal.setDeclaringType(name);
						proposal.setFlags(Flags.AccPublic | Flags.AccStatic);
						proposal.setReplaceRange(fContext.getReplaceStart(), fContext.getReplaceStart() + 3);
						proposal.setName("new");
						mapAll.put("new", proposal);
					}
					list.addAll(mapAll.values());		
				}
				// Only search for all methods matching prefix if we're unsure of type (multiple guesses, or none)
				if (guesses.size() > 1 || (guesses.size() == 1 && guesses.iterator().next().getType().equals(OBJECT))) {
					list.addAll(suggestAllMethodsMatchingPrefix(script)); 
				}
				
				Collections.sort(list, new CompletionProposalComparator());
				for (CompletionProposal proposal : list) {
					fRequestor.accept(proposal);
				}				
			} else {
				// FIXME If we're invoked on the class declaration (it's super class) don't do this!
				// FIXME Traverse the IRubyElement model, not nodes (and don't reparse)?
				if (fContext.isMethodInvokationOrLocal()) {
					suggestMethodsForEnclosingType(script);		
					List<CompletionProposal> proposals = suggestAllMethodsMatchingPrefix(script);
					for (CompletionProposal proposal : proposals) {
						fRequestor.accept(proposal);
					}
				}
				getDocumentsRubyElementsInScope();
			}
		}
		this.fRequestor.endReporting();
		fContext = null;
	}

	private void addASTTypeConstants(String typeName) {
		Collection<Node> typeNodes = getASTTypeNodesFromName(typeName);
		for (Node typeNode : typeNodes) {
//			 Get instance and class variables available in the enclosing type
			List<Node> constants = ScopedNodeLocator.Instance().findNodesInScope(typeNode, new INodeAcceptor() {
				public boolean doesAccept(Node node) {
					return (node instanceof ConstDeclNode) || (node instanceof ClassNode) || (node instanceof ModuleNode);
				}
			});
			Set<String> fields = new HashSet<String>();
			if (constants != null) {
				for (Node varNode : constants) {
					if (varNode.equals(typeNode)) continue;
					Node spanner = ClosestSpanningNodeLocator.Instance().findClosestSpanner(typeNode, varNode.getPosition().getStartOffset() - 1, new INodeAcceptor() {
					
						public boolean doesAccept(Node node) {
							return node instanceof ClassNode || node instanceof ModuleNode;
						}
					
					});
					if (spanner == null || !spanner.equals(typeNode)) continue;
					String name = ASTUtil.getNameReflectively(varNode);
					if (!fContext.prefixStartsWith(name))
						continue;
					fields.add(name);
				}
			}
			for (String field : fields) {
				CompletionProposal proposal = createProposal(fContext.getReplaceStart(), CompletionProposal.CONSTANT_REF, field);
				proposal.setDeclaringType(typeName);
				proposal.setName(field);
				fRequestor.accept(proposal);
			}		
		}
	}

	private Map<IMethod, String> addASTProposals(final String name) {
		Map<IMethod, String> list = new HashMap<IMethod, String>();
		Collection<Node> typeNodes = getASTTypeNodesFromName(name);
		for (Node typeNode : typeNodes) {
			Collection<IMethod> duh = addASTMethodsInScope(typeNode, name);
			for (IMethod method : duh) {
				list.put(method, name);
			}
		}
		// Handle included methods from other modules inside same script
		ASTSourceRequestor srcRequestor = new ASTSourceRequestor();
		SourceElementParser srcParser = new SourceElementParser(srcRequestor);
		srcParser.acceptNode(fContext.getRootNode());
		List<String> mixins = srcRequestor.getMixins(name);
		for (String mixin : mixins) {
			list.putAll(srcRequestor.getMethods(mixin));
		}
		return list;
	}

	private Collection<Node> getASTTypeNodesFromName(final String name) {
		final Node rootNode = fContext.getRootNode();
		return ScopedNodeLocator.Instance().findNodesInScope(rootNode, new INodeAcceptor() {
		
			public boolean doesAccept(Node node) {
				if (!(node instanceof ModuleNode) && !(node instanceof ClassNode)) return false;
				return ASTUtil.getFullyQualifiedTypeName(rootNode, node).equals(name);
			}
		
		});
	}

	private Collection<IMethod> addASTMethodsInScope(Node typeNode, String name) {
		List<IMethod> list = new ArrayList<IMethod>();
		if (typeNode == null) return list;
		List<Node> methods = ScopedNodeLocator.Instance().findNodesInScope(typeNode, new INodeAcceptor() {
			
			public boolean doesAccept(Node node) {
				return (node instanceof DefnNode) || (node instanceof DefsNode);
			}
		
		});
		for (Node methodNode : methods) {
			Node scoping = findNearestScope(typeNode, methodNode.getPosition().getStartOffset() - 1);
			if (scoping == null || !scoping.equals(typeNode)) continue;
			MethodDefNode methodDef = (MethodDefNode) methodNode;
			NodeMethod method = new NodeMethod(methodDef, fContext.getScript());
			list.add(method);
		}
		return list;
	}

	private Map<String, CompletionProposal> suggestTypesConstants(IType type) throws RubyModelException {
		Map<String, CompletionProposal> proposals = new HashMap<String, CompletionProposal>();
		SearchPattern pattern = SearchPattern.createPattern(IRubyElement.CONSTANT, "*", IRubySearchConstants.DECLARATIONS, SearchPattern.R_PATTERN_MATCH);
		IRubySearchScope scope = BasicSearchEngine.createRubySearchScope(new IRubyElement[] {type});
		List<SearchMatch> results = search(pattern, scope);
		for (SearchMatch match: results) {
			IRubyElement element = (IRubyElement) match.getElement();
			if (element.getElementType() != IRubyElement.CONSTANT) continue; // XXX we shouldn't have to do this
			// Add proposal
			CompletionProposal proposal = createProposal(fContext.getReplaceStart(), CompletionProposal.CONSTANT_REF, element.getElementName(), element);
			proposal.setType(type.getFullyQualifiedName());
			proposal.setName(element.getElementName());
			proposals.put(element.getElementName(), proposal);
		}
		return proposals;
	}
	
	private Map<String, CompletionProposal> suggestNestedTypes(IType type) throws RubyModelException {
		Map<String, CompletionProposal> proposals = new HashMap<String, CompletionProposal>();
		SearchPattern pattern = SearchPattern.createPattern(IRubyElement.TYPE, "*", IRubySearchConstants.DECLARATIONS, SearchPattern.R_PATTERN_MATCH);
		IRubySearchScope scope = BasicSearchEngine.createRubySearchScope(new IRubyElement[] {type});
		List<SearchMatch> results = search(pattern, scope);
		for (SearchMatch match: results) {
			IType aType = (IType) match.getElement();
			String fullname = aType.getFullyQualifiedName();
			if (fullname.equals(type.getFullyQualifiedName())) continue; // don't return exact match to prefix
			if (!fullname.startsWith(type.getFullyQualifiedName())) continue; // only return those nested underneath prefix
			String[] parts = Util.getTypeNameParts(fullname);
//			 Don't add if it's not the directly nested child (and is instead the grandchild)
			if (parts.length != Util.getTypeNameParts(type.getFullyQualifiedName()).length + 1) continue;
			// Add proposal						
			CompletionProposal proposal = createProposal(fContext.getReplaceStart(), CompletionProposal.TYPE_REF, aType.getElementName());
			proposal.setType(aType.getFullyQualifiedName());
			proposal.setName(aType.getElementName());
			proposals.put(aType.getElementName(), proposal);
		}
		return proposals;
	}

	private List<CompletionProposal> suggestAllMethodsMatchingPrefix(IRubyScript script) {
		List< CompletionProposal> list = new ArrayList<CompletionProposal>();
		if (fContext.getPartialPrefix() == null || fContext.getPartialPrefix().trim().length() == 0) return list;
		IRubySearchScope scope = BasicSearchEngine.createRubySearchScope(new IRubyElement[] {script.getRubyProject()});
		SearchParticipant participant = BasicSearchEngine.getDefaultSearchParticipant();
		CollectingSearchRequestor searchRequestor = new CollectingSearchRequestor();
		SearchPattern pattern = SearchPattern.createPattern(IRubyElement.METHOD, fContext.getPartialPrefix(), IRubySearchConstants.DECLARATIONS, SearchPattern.R_PREFIX_MATCH);
		try {
			new BasicSearchEngine().search(pattern, new SearchParticipant[] {participant}, scope, searchRequestor, null);
		} catch (CoreException e) {
			RubyCore.log(e);
		}
		List<SearchMatch> matches = searchRequestor.getResults();
		for (SearchMatch match : matches) {
			IMethod element = (IMethod) match.getElement();
			IType type = element.getDeclaringType();
			String typeName = "";
			if (type != null)
				typeName = type.getElementName();
			CompletionProposal proposal = suggestMethod(element, typeName, 50); // TODO Base confidence on accuracy in match?
		    if (proposal != null) {
		    	list.add(proposal);
		    }
		}
		return list;
	}

	private void suggestMethodsForEnclosingType(IRubyScript script) throws RubyModelException {
		IMember element = (IMember) script.getElementAt(fContext.getOffset());
		boolean includeInstance = !fContext.inTypeDefinition();
		IType[] types;
		if (element == null) {
			// We're in the top level, so we're in "Object"
		    RubyElementRequestor requestor = new RubyElementRequestor(script);
		    IType[] tmpTypes = requestor.findType(OBJECT);
		    List<IType> filtered = new ArrayList<IType>();
		    for (int i = 0; i < tmpTypes.length; i++) {
				// FIXME We shouldn't be getting these types with bad fully qualified names anyhow, should we?
				if (!tmpTypes[i].getFullyQualifiedName().equals(OBJECT)) continue;
				filtered.add(tmpTypes[i]);
			}
		    types = filtered.toArray(new IType[filtered.size()]);
		    includeInstance = false;
		} else if (element instanceof IType) {
			IType type = (IType) element;			
			RubyElementRequestor requestor = new RubyElementRequestor(script);
			types = requestor.findType(type.getFullyQualifiedName());
		} else {
			types = new IType[] {element.getDeclaringType()};
		}
		if (types == null || types.length < 1) return;
		Map<String, CompletionProposal> map = new HashMap<String, CompletionProposal>();
		for (int i = 0; i < types.length; i++) {
			if (types[i] == null) continue; // FIXME SHouldn't get this ever, but we sometimes do!
			map.putAll(suggestMethods(100, types[i], includeInstance));
		}
		List<CompletionProposal> list = sort(map);
		for (CompletionProposal proposal : list) {
			fRequestor.accept(proposal);
		}
	}
	
	/**
	 * Wrap beginning of recursion to suggest methods for a type. We keep track of types visited so that we can avoid inifnite loops.
	 * 
	 * @param confidence
	 * @param type
	 * @param includeInstanceMethods
	 * @return
	 * @throws RubyModelException
	 */
	private Map<String, CompletionProposal> suggestMethods(int confidence, IType type, boolean includeInstanceMethods) throws RubyModelException {
		if (fVisitedTypes == null) fVisitedTypes = new HashSet<IType>();
		fOriginalType = type;
		// FIXME We want to avoid visiting the same types across the guesses too!
		Map<String, CompletionProposal> proposals = new HashMap<String, CompletionProposal>();		
		IType[] superTypes = getSuperTypes(type);
		for (int j = 0; j < superTypes.length; j++) {
			IType currentType = superTypes[j];
			if (fVisitedTypes.contains(currentType)) continue;
			fVisitedTypes.add(currentType);
			IMethod[] methods = currentType.getMethods();
			if (methods == null) continue;
			for (int k = 0; k < methods.length; k++) {
				if (methods[k] == null) continue;
				CompletionProposal proposal = suggestMethod(methods[k], currentType.getElementName(), confidence);
				if (proposal != null && !proposals.containsKey(proposal.getName())) {
					proposals.put(proposal.getName(), proposal); // If a method name matches an existing suggestion (i.e. its overriden in the subclass), don't suggest it again!
				}
			}		
		}
		fOriginalType = null;
		fVisitedTypes.clear();
		return proposals;		
	}

	private IType[] getSuperTypes(IType type) throws RubyModelException {
		ITypeHierarchy hierarchy = type.newSupertypeHierarchy(new NullProgressMonitor());
		if (hierarchy == null) return new IType[] {type};
		IType[] superTypes = hierarchy.getAllSupertypes(type);
		if (superTypes == null || superTypes.length == 0) return new IType[] {type};
		
		IType[] modules = hierarchy.getAllSuperModules(type);
		if (modules == null || modules.length == 0) {
			int length =  superTypes.length;
			IType[] all = new IType[length + 1]; // Apparently getAllTypes is returning types that are in files that are related to type hierarchy, but aren't supertypes of focus! So I had to switch to getAllSupertypes(focus);
			all[0] = type;
			System.arraycopy(superTypes, 0, all, 1, length);
			return all;
		}
		
		int length =  superTypes.length + modules.length;
		IType[] all = new IType[length + 1]; // Apparently getAllTypes is returning types that are in files that are related to type hierarchy, but aren't supertypes of focus! So I had to switch to getAllSupertypes(focus);
		all[0] = type;
		System.arraycopy(superTypes, 0, all, 1, superTypes.length);
		System.arraycopy(modules, 0, all, superTypes.length + 1, modules.length);
		return all;
	}

	private List<CompletionProposal> sort(Map<String, CompletionProposal> proposals) {
		List<CompletionProposal> list = new ArrayList<CompletionProposal>(proposals.values());
		Collections.sort(list, new CompletionProposalComparator());
		return list;
	}
	
	private void suggestGlobals() {
		SearchPattern pattern = SearchPattern.createPattern(IRubyElement.GLOBAL, "$*", IRubySearchConstants.DECLARATIONS, SearchPattern.R_PATTERN_MATCH);
		IRubySearchScope scope = BasicSearchEngine.createRubySearchScope(new IRubyElement[] {fContext.getScript().getRubyProject()});
		List<SearchMatch> results = search(pattern, scope);		
		Set<String> names = new HashSet<String>();
		for (SearchMatch match: results) {
			IRubyElement element = (IRubyElement) match.getElement();
			String name = element.getElementName();
			if (names.contains(name)) continue;
			names.add(name);
			CompletionProposal proposal = createProposal(fContext.getReplaceStart(), CompletionProposal.GLOBAL_REF, name, element);
			proposal.setType(name);
			fRequestor.accept(proposal);
		}
	}

	private void suggestTypeNames() {	
		SearchPattern pattern = SearchPattern.createPattern(IRubyElement.TYPE, fContext.getPartialPrefix() + "*", IRubySearchConstants.DECLARATIONS, SearchPattern.R_PATTERN_MATCH);
		IRubySearchScope scope = BasicSearchEngine.createRubySearchScope(new IRubyElement[] {fContext.getScript().getRubyProject()});
		List<SearchMatch> results = search(pattern, scope);		
		Set<String> names = new HashSet<String>();
		for (SearchMatch match: results) {
			IRubyElement element = (IRubyElement) match.getElement();
			String name = element.getElementName();
			if (names.contains(name)) continue;
			names.add(name);
			CompletionProposal proposal = createProposal(fContext.getReplaceStart(), CompletionProposal.TYPE_REF, name, element);
			proposal.setType(name);
			fRequestor.accept(proposal);
		}
	}
	
	private List<SearchMatch> search(SearchPattern pattern, IRubySearchScope scope) {
		BasicSearchEngine engine = new BasicSearchEngine();
		SearchParticipant[] participants = new SearchParticipant[] { BasicSearchEngine.getDefaultSearchParticipant() };
		CollectingSearchRequestor requestor = new CollectingSearchRequestor();
		try {
			engine.search(pattern, participants, scope, requestor, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return requestor.getResults();
	}

	private CompletionProposal createProposal(int replaceStart, int type, String name) {
		return createProposal(replaceStart, type, name, 100, null);
	}
	private CompletionProposal createProposal(int replaceStart, int type, String name, IRubyElement element) {
		return createProposal(replaceStart, type, name, 100, element);
	}
	private CompletionProposal createProposal(int replaceStart, int type, String name, int confidence, IRubyElement element) {
		CompletionProposal proposal = new CompletionProposal(type, name, confidence);
		proposal.setReplaceRange(replaceStart, replaceStart + name.length());
		proposal.setElement(element);
		return proposal;
	}

	private void suggestConstantNames() {
		SearchPattern pattern = SearchPattern.createPattern(IRubyElement.CONSTANT, fContext.getPartialPrefix()+ "*", IRubySearchConstants.DECLARATIONS, SearchPattern.R_PATTERN_MATCH);
		IRubySearchScope scope = BasicSearchEngine.createRubySearchScope(new IRubyElement[] {fContext.getScript()});
		List<SearchMatch> results = search(pattern, scope);
		for (SearchMatch match: results) {
			IRubyElement element = (IRubyElement) match.getElement();
			String name = element.getElementName();
			CompletionProposal proposal = createProposal(fContext.getReplaceStart(), CompletionProposal.CONSTANT_REF, name, element);
			proposal.setType(name);
			fRequestor.accept(proposal);
		}
		if ("ARGV".startsWith(fContext.getPartialPrefix())) {
			IRubyElement element = new RubyConstant(null, "ARGV");
			CompletionProposal proposal = createProposal(fContext.getReplaceStart(), CompletionProposal.CONSTANT_REF, "ARGV", element);
			proposal.setType("Array");
			fRequestor.accept(proposal);
		}
	}

	private CompletionProposal suggestMethod(IMethod method, String typeName, int confidence) {
		try {
			int start = fContext.getReplaceStart();
			String name = method.getElementName();
			int flags = Flags.AccDefault;
			if (method.isSingleton()) {
				flags |= Flags.AccStatic;
				if (method.isConstructor())
					name = CONSTRUCTOR_INVOKE_NAME;
				else {
					if (name.startsWith(typeName)) {
						name = name.substring(typeName.length() + 1);
					}
				}
			} else {
				// Don't show instance methods if the thing we're working on is a class' name!
				// FIXME We do want to show if it is a constant, but not a class name
				if (fContext.fullPrefixIsConstant()) return null;
			}
			if (!fContext.prefixStartsWith(name))
				return null;
			
			try {
				switch (method.getVisibility()) {
				case IMethod.PRIVATE:
					flags |= Flags.AccPrivate;
					if (fOriginalType != null && !fOriginalType.getElementName().equals(typeName)) return null; // FIXME We should do a comparison of types, not names
					if (fContext.hasReceiver()) return null; // can't invoke a private method on a receiver
					break;
				case IMethod.PUBLIC:
					flags |= Flags.AccPublic; // FIXME Check if receiver is of same class as method's declaring type, if not, skip this method. (so we can invoke with no receiver inside same class, with explicit self as receiver, or with receiver who has same class).
					break;
				case IMethod.PROTECTED:
					flags |= Flags.AccProtected;
					break;
				default:
					break;
				}
			} catch (RubyModelException e) {
				RubyCore.log(e);
				flags |= Flags.AccPublic;
			}
			CompletionProposal proposal = createProposal(start, CompletionProposal.METHOD_REF, name, confidence, method);
			proposal.setReplaceRange(start, start + name.length());
			proposal.setFlags(flags);
			proposal.setName(name);
			IType declaringType = method.getDeclaringType();
			String declaringName = typeName;
			if (declaringType != null)
				declaringName = declaringType.getFullyQualifiedName();
			proposal.setDeclaringType(declaringName);
			return proposal;
		} catch (RuntimeException e) {
			RubyCore.log(e);
			return null;
		}
	}

	/**
	 * Gets all the distinct elements in the current RubyScript
	 * 
	 * @param offset
	 * @param replaceStart
	 * 
	 * @return a List of the names of all the elements in the current RubyScript
	 */
	private void getDocumentsRubyElementsInScope() {
		try {
			// FIXME Try to stop all the multiple re-parsing of the source! Can
			// we parse once and pass the root node around?
			// Parse
			Node rootNode = fContext.getRootNode();
			if (rootNode == null) {
				return;
			}

			// Grab enclosing scope
			Node enclosingNode = findNearestScope(rootNode, fContext.getOffset());
			if (enclosingNode == null) enclosingNode = rootNode;
			// Add variables in this scope
			Collection<String> variables = addVariablesinScope(getScope(enclosingNode));
			for (String variable : variables) {
				int type = CompletionProposal.LOCAL_VARIABLE_REF;
				if (variable.startsWith("$")) {
					type = CompletionProposal.GLOBAL_REF;
				}
				CompletionProposal proposal = createProposal(fContext.getReplaceStart(), type, variable);
				fRequestor.accept(proposal);
			}			

			// Add methods in this scope
			Node enclosingTypeNode = ClosestSpanningNodeLocator.Instance().findClosestSpanner(rootNode, fContext.getOffset(), new INodeAcceptor() {
				public boolean doesAccept(Node node) {
					return (node instanceof ClassNode || node instanceof ModuleNode || node instanceof RootNode);
				}
			});
			if (enclosingTypeNode == null) enclosingTypeNode = rootNode;
			Collection<IMethod> methods = addASTMethodsInScope(enclosingTypeNode, "");
			for (IMethod method : methods) {
				CompletionProposal proposal = suggestMethod(method, "", 100);
				if (proposal == null) continue;
				fRequestor.accept(proposal);
			}

			// Find the enclosing type (class or module) to get instance and
			// classvars from
			enclosingTypeNode = ClosestSpanningNodeLocator.Instance().findClosestSpanner(rootNode, fContext.getOffset(), new INodeAcceptor() {
				public boolean doesAccept(Node node) {
					return (node instanceof ClassNode || node instanceof ModuleNode);
				}
			});

			// Add members from enclosing type
			if (enclosingTypeNode != null) {
				getMembersAvailableInsideType(enclosingTypeNode);
			}
		} catch (RubyModelException rme) {
			RubyCore.log(rme);
			RubyCore.log("RubyModelException in CompletionEngine::getElementsInScope()");
		} catch (SyntaxException se) {
			RubyCore.log(se);
			RubyCore.log("SyntaxError in CompletionEngine::getElementsInScope()");
		}
	}

	private Node findNearestScope(Node scopeNode, int offset) {
		if (offset == -1) return scopeNode;
		Node scope = ClosestSpanningNodeLocator.Instance().findClosestSpanner(scopeNode, offset, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				return (node instanceof DefnNode || node instanceof DefsNode || node instanceof ClassNode || node instanceof ModuleNode || node instanceof RootNode || node instanceof IterNode);
			}
		});
		if (scope == null) return scopeNode;
		return scope;
	}

	private Set<String> addVariablesinScope(StaticScope scope) {
		Set<String> matches = new HashSet<String>();
		if (scope == null) return matches;
		String[] variables = scope.getVariables();
		for(int i = 0; i < variables.length; i++) {
			String local = variables[i];
			if (!fContext.prefixStartsWith(local))
				continue;
			matches.add(local);
		}
		matches.addAll(addVariablesinScope(scope.getEnclosingScope()));
		return matches;
	}

	private StaticScope getScope(Node enclosingNode) {
		if (enclosingNode == null) return ((RootNode)fContext.getRootNode()).getStaticScope();
		if (enclosingNode instanceof RootNode) {
			RootNode root = (RootNode) enclosingNode;
			return root.getStaticScope();
		}
		try {
			Method getScopeMethod = enclosingNode.getClass().getMethod("getScope", new Class[] {});
			Object scope = getScopeMethod.invoke(enclosingNode, new Object[0]);
			return (StaticScope) scope;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets the members available inside a type node (ModuleNode, ClassNode): -
	 * Instance variables - Class variables - Methods
	 * 
	 * @param typeNode
	 * @return
	 */
	private void getMembersAvailableInsideType(Node typeNode) throws RubyModelException {
		if (typeNode == null) {
			return;
		}

		String typeName = getTypeName(typeNode);
		if (typeName == null) {
			return;
		}

		// Get superclass and add its public members
		List<Node> superclassNodes = getSuperclassNodes(typeNode);
		for (Node superclassNode : superclassNodes) {
			getMembersAvailableInsideType(superclassNode);
		}

		// Get public members of mixins
		List<String> mixinNames = getIncludedMixinNames(typeName);
		for (String mixinName : mixinNames) {
			List<Node> mixinDeclarations = getTypeDeclarationNodes(mixinName);
			for (Node mixinDeclaration : mixinDeclarations) {
				getMembersAvailableInsideType(mixinDeclaration);
			}
		}

		// Get method names defined by DefnNodes and DefsNodes
		List<Node> methodDefinitions = ScopedNodeLocator.Instance().findNodesInScope(typeNode, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				return (node instanceof DefnNode) || (node instanceof DefsNode);
			}
		});
		for (Node methodDefinition : methodDefinitions) {
			String name = null;
			if (methodDefinition instanceof DefnNode) {
				name = ((DefnNode) methodDefinition).getName();
			}
			if (methodDefinition instanceof DefsNode) {
				name = ((DefsNode) methodDefinition).getName();
			}
			if (!fContext.prefixStartsWith(name))
				continue;
			NodeMethod method = new NodeMethod((MethodDefNode)methodDefinition, fContext.getScript());
			suggestMethod(method, typeName, 100);
		}
		addTypesVariables(typeNode);
	}

	private String getTypeName(Node typeNode) {
		// Get type name
		String typeName = null;
		if (typeNode instanceof ClassNode) {
			typeName = ((Colon2Node) ((ClassNode) typeNode).getCPath()).getName();
		}
		if (typeNode instanceof ModuleNode) {
			typeName = ((Colon2Node) ((ModuleNode) typeNode).getCPath()).getName();
		}
		return typeName;
	}

	private void addTypesVariables(Node typeNode) {
		// Get instance and class variables available in the enclosing type
		List<Node> instanceAndClassVars = ScopedNodeLocator.Instance().findNodesInScope(typeNode, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				return (node instanceof ConstDeclNode || node instanceof InstVarNode || node instanceof InstAsgnNode || node instanceof ClassVarNode || node instanceof ClassVarDeclNode || node instanceof ClassVarAsgnNode);
			}
		});
		Set<String> fields = new HashSet<String>();
		if (instanceAndClassVars != null) {
			// Get the unique names of instance and class variables
			for (Node varNode : instanceAndClassVars) {
				String name = ASTUtil.getNameReflectively(varNode);
				if (!fContext.prefixStartsWith(name))
					continue;
				fields.add(name);
			}
		}
		// Get instance and class vars defined by [c]attr_* calls
		List<String> attrs = AttributeLocator.Instance().findInstanceAttributesInScope(typeNode);
		for (Iterator iter = attrs.iterator(); iter.hasNext();) {
			String attr = (String) iter.next();
			if (!fContext.prefixStartsWith(attr))
				continue;
			fields.add(attr);
		}
		for (String field : fields) {
			CompletionProposal proposal = createProposal(fContext.getReplaceStart(), CompletionProposal.CONSTANT_REF, field);
			fRequestor.accept(proposal);
		}
	}

	/**
	 * Finds all nodes that declare a type that is a superclass of the specified
	 * node. Example:
	 * 
	 * """ class Klass;def meth_1;1;end;end class Klass;def meth_2;2;end;end
	 * 
	 * class SubKlass < Klass;end """
	 * 
	 * Issuing getSuperClassNodes() on the ClassNode declaring SubKlass would
	 * return two ClassNodes; one for each definition of Klass.
	 * 
	 * @param typeNode
	 *            Node to find superclass nodes of
	 * @return List of ClassNode or ModuleNode
	 */
	private List<Node> getSuperclassNodes(Node typeNode) {
		if (typeNode instanceof ClassNode) {
			Node superNode = ((ClassNode) typeNode).getSuperNode();
			if (superNode instanceof ConstNode) {
				String superclassName = ((ConstNode) superNode).getName();
				return getTypeDeclarationNodes(superclassName);
			}
		}
		return new ArrayList<Node>();
	}

	/** Lookup type declaration nodes */
	private List<Node> getTypeDeclarationNodes(String typeName) {
		// Find the named type
		RubyElementRequestor requestor = new RubyElementRequestor(fContext.getScript());
		IType[] types = requestor.findType(typeName);
		if (types == null || types.length == 0) return new ArrayList<Node>(0);
		IType type = types[0];

		try {
			if (type instanceof RubyType) {

				// FIXME This feels a little hacky and backwards -
				// RubyType.getSource() and then parse... consider reworking the
				// clients to this method to accept RubyTypes or something
				// similar?
				// Find source and parse
				RubyType rubyType = (RubyType) type;
				String source = rubyType.getSource();
				if (source == null) return new ArrayList<Node>(0);

				// FIXME Why does the parser balk on \r chars?
				source = source.replace('\r', ' ');
				Node rootNode = (new RubyParser()).parse(type.getRubyScript().getElementName(), source).getAST();

				// Bail if the parse fails
				if (rootNode == null) {
					return new ArrayList<Node>();
				}

				// Return any type declaration nodes in included source
				return ScopedNodeLocator.Instance().findNodesInScope(rootNode, new INodeAcceptor() {
					public boolean doesAccept(Node node) {
						return (node instanceof ClassNode) || (node instanceof ModuleNode);
					}
				});
			}

		} catch (RubyModelException rme) {
			rme.printStackTrace();
		}

		return new ArrayList<Node>(0);
	}

	private List<String> getIncludedMixinNames(String typeName) {
		IType rubyType = new RubyType((RubyElement)fContext.getScript(), typeName);

		try {
			String[] includedModuleNames = rubyType.getIncludedModuleNames();
			if (includedModuleNames != null) {
				return Arrays.asList(rubyType.getIncludedModuleNames());
			} 
			return new ArrayList<String>(0);
		} catch (RubyModelException e) {
			return new ArrayList<String>(0);
		}
	}
	
	private class NodeMethod implements IMethod {
		private MethodDefNode node;
		private IRubyScript fScript;

		public NodeMethod(MethodDefNode methodDefinition, IRubyScript script) {
			this.node = methodDefinition;
			this.fScript = script;
		}

		public String[] getParameterNames() throws RubyModelException {
			return ASTUtil.getArgs(node.getArgsNode(), node.getScope());
		}
		
		public int getNumberOfParameters() throws RubyModelException {
			return getParameterNames().length;
		}

		public int getVisibility() throws RubyModelException {
			return IMethod.PUBLIC;
		}

		public boolean isConstructor() {
			return node.getName().equals(CONSTRUCTOR_DEFINITION_NAME);
		}

		public boolean isSingleton() {
			return isConstructor() || node instanceof DefsNode;
		}

		public boolean exists() {
			return false;
		}

		public IRubyElement getAncestor(int ancestorType) {
			return null;
		}

		public IResource getCorrespondingResource() throws RubyModelException {
			return null;
		}

		public String getElementName() {
			return node.getName();
		}

		public int getElementType() {
			return IRubyElement.METHOD;
		}

		public IOpenable getOpenable() {
			return fScript;
		}

		public IRubyElement getParent() {
			return null;
		}

		public IPath getPath() {
			return null;
		}

		public IRubyElement getPrimaryElement() {
			return null;
		}

		public IResource getResource() {
			return null;
		}

		public IRubyModel getRubyModel() {
			return null;
		}

		public IRubyProject getRubyProject() {
			return null;
		}

		public IResource getUnderlyingResource() throws RubyModelException {
			return null;
		}

		public boolean isReadOnly() {
			return false;
		}

		public boolean isStructureKnown() throws RubyModelException {
			return false;
		}

		public boolean isType(int type) {
			return type == IRubyElement.METHOD;
		}

		public Object getAdapter(Class adapter) {
			return null;
		}

		public IType getDeclaringType() {
			return null;
		}

		public ISourceRange getNameRange() throws RubyModelException {
			return null;
		}

		public IRubyScript getRubyScript() {
			return fScript;
		}

		public IType getType(String name, int occurrenceCount) {
			return null;
		}

		public String getSource() throws RubyModelException {
			return null;
		}

		public ISourceRange getSourceRange() throws RubyModelException {
			return null;
		}

		public IRubyElement[] getChildren() throws RubyModelException {
			return null;
		}

		public boolean hasChildren() throws RubyModelException {
			return false;
		}

		public String getHandleIdentifier() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isPrivate() throws RubyModelException {
			return false;
		}

		public boolean isProtected() throws RubyModelException {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isPublic() throws RubyModelException {
			// TODO Auto-generated method stub
			return false;
		}

		public String[] getBlockParameters() throws RubyModelException {
			final Set<String> vars = new HashSet<String>();
			InOrderVisitor visitor = new InOrderVisitor() {
				private String typeName;
				@Override
				public Instruction visitClassNode(ClassNode iVisited) {
					typeName = ASTUtil.getFullyQualifiedName(iVisited.getCPath());
					return super.visitClassNode(iVisited);
				}
				
				@Override
				public Instruction visitModuleNode(ModuleNode iVisited) {
					typeName = ASTUtil.getFullyQualifiedName(iVisited.getCPath());
					return super.visitModuleNode(iVisited);
				}
				
				@Override
				public Instruction visitYieldNode(YieldNode iVisited) {
					Node argsNode = iVisited.getArgsNode();
					if (argsNode instanceof LocalVarNode) {
						vars.add(((LocalVarNode) argsNode).getName());
					} else if (argsNode instanceof SelfNode) {
						String name = null;
						if (typeName == null) {
							name = "var";
						} else {
							name = typeName.toLowerCase();
							if (name.indexOf("::") > -1) {
							name = name.substring(name.lastIndexOf("::") + 2);
							}
						}
						vars.add(name);
					}
					return super.visitYieldNode(iVisited);
				}
			
			};
			this.node.accept(visitor);
			return vars.toArray(new String[vars.size()]);
		}

	}
}
