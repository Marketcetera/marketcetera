package org.rubypeople.rdt.internal.codeassist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.jruby.ast.AliasNode;
import org.jruby.ast.ArgumentNode;
import org.jruby.ast.CallNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.ClassVarAsgnNode;
import org.jruby.ast.ClassVarDeclNode;
import org.jruby.ast.ClassVarNode;
import org.jruby.ast.Colon2Node;
import org.jruby.ast.ConstDeclNode;
import org.jruby.ast.ConstNode;
import org.jruby.ast.DAsgnNode;
import org.jruby.ast.DVarNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.LocalVarNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.Node;
import org.jruby.ast.RootNode;
import org.jruby.ast.StrNode;
import org.jruby.ast.VCallNode;
import org.jruby.ast.types.INameNode;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.ITypeHierarchy;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.codeassist.CodeResolver;
import org.rubypeople.rdt.core.codeassist.ResolveContext;
import org.rubypeople.rdt.core.search.CollectingSearchRequestor;
import org.rubypeople.rdt.core.search.IRubySearchConstants;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchEngine;
import org.rubypeople.rdt.core.search.SearchMatch;
import org.rubypeople.rdt.core.search.SearchParticipant;
import org.rubypeople.rdt.core.search.SearchPattern;
import org.rubypeople.rdt.internal.core.RubyScript;
import org.rubypeople.rdt.internal.core.search.BasicSearchEngine;
import org.rubypeople.rdt.internal.core.util.ASTUtil;
import org.rubypeople.rdt.internal.core.util.Util;
import org.rubypeople.rdt.internal.ti.DefaultTypeInferrer;
import org.rubypeople.rdt.internal.ti.ITypeGuess;
import org.rubypeople.rdt.internal.ti.ITypeInferrer;
import org.rubypeople.rdt.internal.ti.util.ClosestSpanningNodeLocator;
import org.rubypeople.rdt.internal.ti.util.FirstPrecursorNodeLocator;
import org.rubypeople.rdt.internal.ti.util.INodeAcceptor;
import org.rubypeople.rdt.internal.ti.util.OffsetNodeLocator;

public class RubyCodeResolver extends CodeResolver {

	private HashSet<IType> fVisitedTypes;

	@Override
	public void select(ResolveContext context) throws RubyModelException {
		IRubyScript script = context.getScript();
		int start = context.getStartOffset();
		RootNode root = context.getAST();
		
		Node selected = OffsetNodeLocator.Instance().getNodeAtOffset(root, start);
		if (selected instanceof StrNode) { // Go to file in a 'require' or 'load' call
			StrNode string = (StrNode) selected;			
			FCallNode fcall = (FCallNode) ClosestSpanningNodeLocator.Instance().findClosestSpanner(root, string.getPosition().getStartOffset(), new INodeAcceptor() {
			
				public boolean doesAccept(Node node) {
					return node instanceof FCallNode;
				}
			
			});
			if (fcall == null) return;
			if (fcall.getName().equals("require") || fcall.getName().equals("load")) {
				String value = string.getValue().toString();
				if (!value.endsWith(".rb")) {
					value += ".rb";
				}
				ILoadpathEntry[] entries = script.getRubyProject().getResolvedLoadpath(true);
				for (int i = 0; i < entries.length; i++) {
					IPath path = entries[i].getPath().append(value);
					if (path.toFile().exists()) {
						// If it's in the workspace, it's relatively easy...
						IFile file = null;
						if (path.isAbsolute()) {
							file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
						} else {
							file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
						}
						if (file != null) {
							context.putResolved(new IRubyElement[] { RubyCore.create(file) });
							return;
						}
						// ...Otherwise we need to deal with opening an external ruby script by traversing the model of the project
						ISourceFolderRoot sfRoot = script.getRubyProject().getSourceFolderRoot(entries[i].getPath().toPortableString());
						String[] parts = value.split("[\\|/]");
						String[] minusFileName;
						if (parts.length == 1) {
							minusFileName = new String[0];
						} else {
							minusFileName = new String[parts.length - 1];
							System.arraycopy(parts, 0, minusFileName, 0, minusFileName.length);
						}
						ISourceFolder folder = sfRoot.getSourceFolder(minusFileName);
						
						context.putResolved(new IRubyElement[] { folder.getRubyScript(path.lastSegment()) });
						return;
					}
				}
			}
		}
		
		if (selected instanceof AliasNode) {
			// figure out if we're pointing at new name or old name.
			AliasNode aliasNode = (AliasNode) selected;
			int startOffset = aliasNode.getPosition().getStartOffset();
			int diff = start - startOffset;
			if (diff < (6 + aliasNode.getNewName().length() + 1)) return; // if we're not over the old name, don't resolve this to anything! FIXME Resolve it to the new method!
			String methodName = aliasNode.getOldName();
			// FIXME Only search within the current class/module scope!			
			// do a global search for method declarations matching this name	
			List<IRubyElement> possible = new ArrayList<IRubyElement>();
			try {
				List<SearchMatch> results = search(IRubyElement.METHOD, methodName, IRubySearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
				for (SearchMatch match : results) {
					IRubyElement element = (IRubyElement) match.getElement();
					possible.add(element);
				}
			} catch (CoreException e) {
				RubyCore.log(e);
			}			
			context.putResolved( possible.toArray(new IRubyElement[possible.size()]) );
			return;
		}
		if (selected instanceof Colon2Node) {
			String simpleName = ((Colon2Node)selected).getName();
			String fullyQualifiedName = ASTUtil.getFullyQualifiedName((Colon2Node) selected);
			IRubyElement element = findChild(simpleName, IRubyElement.TYPE, script);
			if (element != null && Util.parentsMatch((IType)element, fullyQualifiedName)) {
				context.putResolved( new IRubyElement[] { element } );
				return;
			}
			RubyElementRequestor completer = new RubyElementRequestor(script);
			context.putResolved( completer.findType(fullyQualifiedName) );
			return;
		} 
		if (selected instanceof DVarNode) {
			final String name = ((DVarNode) selected).getName();
			Node assignment = FirstPrecursorNodeLocator.Instance().findFirstPrecursor(root, start, new INodeAcceptor() {
			
				public boolean doesAccept(Node node) {
					// TODO Auto-generated method stub
					return (node instanceof DAsgnNode) && ((DAsgnNode) node).getName().equals(name);
				}
			
			});
			context.putResolved( new IRubyElement[] { script.getElementAt(assignment.getPosition().getStartOffset()) } );
			return;
		}
		if (selected instanceof ConstNode) {			
			ConstNode constNode = (ConstNode) selected;
			String name = constNode.getName();
			// Try to find a matching constant in this script
			// TODO Use convention of all caps versus camelcase to decided which to search for first?
			try {
				IRubySearchScope scope = SearchEngine.createRubySearchScope(new IRubyElement[] {script});
				List<SearchMatch> matches = search(scope, IRubyElement.CONSTANT, name, IRubySearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
				for (SearchMatch match : matches) {
					IRubyElement element = (IRubyElement) match.getElement();
					if (element != null) {
						context.putResolved( new IRubyElement[] { element } );
						return;
					}
				}
			} catch (CoreException e) {
				RubyCore.log(e);
			}
			// Now search for a type in this script
			try {
				IRubySearchScope scope = SearchEngine.createRubySearchScope(new IRubyElement[] {script});				
				List<SearchMatch> matches = search(scope, IRubyElement.TYPE, name, IRubySearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
				for (SearchMatch match : matches) {
					IRubyElement element = (IRubyElement) match.getElement();
					if (element != null) {
						context.putResolved( new IRubyElement[] { element } );	
						return;
					}
				}
			} catch (CoreException e) {
				RubyCore.log(e);
			}
			RubyElementRequestor completer = new RubyElementRequestor(script);
			String fullyQualifiedName = getFullyQualifiedName(root, constNode.getPosition().getStartOffset(), name);
			if (fullyQualifiedName != null) {
				IType[] types = completer.findType(fullyQualifiedName);
				if (types != null && types.length > 0) {
					context.putResolved( types );
					return;
				}
			}
			context.putResolved( completer.findType(name) );
			return;
		}
		if (isLocalVarRef(selected)) {
			IRubyElement spanner = script.getElementAt(selected.getPosition().getStartOffset());
			List<IRubyElement> possible = new ArrayList<IRubyElement>();
			if (spanner instanceof IParent) {
				IParent parent = (IParent) spanner;
				possible = getChildrenWithName(parent.getChildren(), IRubyElement.LOCAL_VARIABLE,
						getName(selected));
			}
			if (possible.isEmpty()) {
				possible = getChildrenWithName(script.getChildren(), IRubyElement.LOCAL_VARIABLE,
					getName(selected));
			}
			context.putResolved( possible.toArray(new IRubyElement[possible.size()]) );
			return;
		}
		if (isInstanceVarRef(selected)) {
			List<IRubyElement> possible = getChildrenWithName(script
					.getChildren(), IRubyElement.INSTANCE_VAR,
					getName(selected));
			context.putResolved( possible.toArray(new IRubyElement[possible.size()]) );
			return;
		}
		if (isClassVarRef(selected)) {
			List<IRubyElement> possible = getChildrenWithName(script
					.getChildren(), IRubyElement.CLASS_VAR, getName(selected));
			context.putResolved( possible.toArray(new IRubyElement[possible.size()]) );
			return;
		}
		// We're already on the declaration, just return it
		if ((selected instanceof DefnNode) || (selected instanceof DefsNode) ||
				(selected instanceof ConstDeclNode) || (selected instanceof ClassNode) || 
				(selected instanceof ModuleNode) || (selected instanceof ClassVarDeclNode)) {
			IRubyElement element = ((RubyScript)script).getElementAt(start);
			context.putResolved( new IRubyElement[] {element} );
			return;
		}
		if (isMethodCall(selected)) {
			String methodName = getName(selected);
			Set<IRubyElement> possible = new HashSet<IRubyElement>();
			IType[] types = getReceiver(script, selected, root, start);
			for (int i = 0; i < types.length; i++) {
				IType type = types[i];
				if (fVisitedTypes == null) { fVisitedTypes = new HashSet<IType>(); } // keep track of types so we don't get into infinite loop
				Collection<IMethod> methods = suggestMethods(type);
				fVisitedTypes.clear();
				for (IMethod method : methods) {
					if (method.getElementName().equals(methodName))
						possible.add(method);
				}
			}
			if (possible.isEmpty()) {
				// do a global search for method declarations matching this name				
				try {
					List<SearchMatch> results = search(IRubyElement.METHOD, methodName, IRubySearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
					for (SearchMatch match : results) {
						IRubyElement element = (IRubyElement) match.getElement();
						possible.add(element);
					}
				} catch (CoreException e) {
					RubyCore.log(e);
				}
			}
			context.putResolved( possible.toArray(new IRubyElement[possible.size()]) );
			return;
		}
	}
	
	private String getFullyQualifiedName(Node root, int offset, String name) {
		String namespace = ASTUtil.getNamespace(root, offset);
		if (namespace == null || namespace.trim().length() == 0) {
			return name;
		}
		return namespace + "::" + name;
	}

	private List<SearchMatch> search(int type, String patternString, int limitTo, int matchRule) throws CoreException {
		return search(SearchEngine.createWorkspaceScope(), type, patternString, limitTo, matchRule);
	}
	
	private List<SearchMatch> search(IRubySearchScope scope, int type, String patternString, int limitTo, int matchRule) throws CoreException {
		SearchEngine engine = new SearchEngine();
		SearchPattern pattern = SearchPattern.createPattern(type, patternString, limitTo, matchRule);
		SearchParticipant[] participants = new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()};
		CollectingSearchRequestor requestor = new CollectingSearchRequestor();
		engine.search(pattern, participants, scope, requestor, null);
		return requestor.getResults();
	}
	
	
	private IType[] getReceiver(IRubyScript script, Node selected, Node root, int start) {
		
		List<IType> types = new ArrayList<IType>();
		if ((selected instanceof FCallNode) || (selected instanceof VCallNode)) {
			Node receiver = null;
			IRubySearchScope scope = null;

				receiver = ClosestSpanningNodeLocator.Instance()
				.findClosestSpanner(root, start, new INodeAcceptor() {
					public boolean doesAccept(Node node) {
						return (node instanceof ClassNode || node instanceof ModuleNode);
					}
				});
				scope = SearchEngine.createRubySearchScope(new IRubyElement[] { script });
			
			
			String typeName = ASTUtil.getNameReflectively(receiver);
			if (typeName == null) typeName = "Object";
			CollectingSearchRequestor requestor = new CollectingSearchRequestor();
			SearchPattern pattern = SearchPattern.createPattern(
					IRubyElement.TYPE, typeName,
					IRubySearchConstants.DECLARATIONS,
					SearchPattern.R_EXACT_MATCH);
			SearchParticipant[] participants = { BasicSearchEngine.getDefaultSearchParticipant() };
			try {
				new BasicSearchEngine().search(pattern, participants, scope, requestor, null);
			} catch (CoreException e) {
				RubyCore.log(e);
			}
			List<SearchMatch> matches = requestor.getResults();
			if (matches == null || matches.isEmpty()) return new IType[0]; // TODO Check up the type hierarchy!
			for (SearchMatch match : matches) {
				types.add((IType) match.getElement());
			}
		} else {
			ITypeInferrer inferrer = new DefaultTypeInferrer();
			Collection<ITypeGuess> guesses = new ArrayList<ITypeGuess>();
			try {
				guesses = inferrer.infer(script.getSource(), start);
			} catch (RubyModelException e1) {
				RubyCore.log(e1);
			}
			// TODO If guesses are empty, just do a global search for this method?
			if (guesses.isEmpty()) {
				String methodName = ASTUtil.getNameReflectively(selected);
				IRubySearchScope scope = SearchEngine.createRubySearchScope(new IRubyElement[] { script.getRubyProject() });
				CollectingSearchRequestor requestor = new CollectingSearchRequestor();
				SearchPattern pattern = SearchPattern.createPattern(
						IRubyElement.METHOD, methodName,
						IRubySearchConstants.DECLARATIONS,
						SearchPattern.R_EXACT_MATCH);
				SearchParticipant[] participants = { BasicSearchEngine.getDefaultSearchParticipant() };
				try {
					new BasicSearchEngine().search(pattern, participants, scope, requestor, null);
				} catch (CoreException e) {
					RubyCore.log(e);
				}
				List<SearchMatch> matches = requestor.getResults();
				if (matches == null || matches.isEmpty()) return new IType[0];
				for (SearchMatch match : matches) {
					IMethod method = (IMethod) match.getElement();
					types.add(method.getDeclaringType());
				}
			} else {
			RubyElementRequestor requestor = new RubyElementRequestor(
					script);
			for (ITypeGuess guess : guesses) {
				String name = guess.getType();
				IType[] tmpTypes = requestor.findType(name);
				for (int i = 0; i < tmpTypes.length; i++) {
					types.add(tmpTypes[i]);
				}
			}
			}
		}
		return types.toArray(new IType[types.size()]);
	}

	// FIXME Just create the type heirarchy, the add all the types into a search scope and search for an exact match to the method name!
	private Collection<IMethod> suggestMethods(IType type) throws RubyModelException {
		if (type == null) return Collections.emptyList();
		if (fVisitedTypes == null) fVisitedTypes = new HashSet<IType>();
		// FIXME We want to avoid visiting the same types across the guesses too!
		List<IMethod> proposals = new ArrayList<IMethod>();		
		ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
		IType[] all = hierarchy.getAllSupertypes(type); // Apparently getAllTypes is returning types that are in files chedk that are related to type hierarchy, but aren't supertypes of focus! So I had to switch to getAllSupertypes(focus);
		for (int j = 0; j < all.length; j++) {
			IType currentType = all[j];
			if (fVisitedTypes.contains(currentType)) continue;
			fVisitedTypes.add(currentType);
			IMethod[] methods = currentType.getMethods();
			if (methods != null) {
				for (int k = 0; k < methods.length; k++) {
					if (methods[k] == null) continue;
					proposals.add(methods[k]);
				}		
			}
		}
		fVisitedTypes.clear();
		return proposals;		
	}

	private IRubyElement findChild(String name, int type, IParent parent) {
		try {
			IRubyElement[] children = parent.getChildren();
			for (int j = 0; j < children.length; j++) {
				IRubyElement child = children[j];
				if (child.getElementName().equals(name) && child.isType(type))
				  return child;
			    if (child instanceof IParent) {
			    	IRubyElement found = findChild(name, type, (IParent) child);
			    	if (found != null) return found;
			    }
			}
		} catch (RubyModelException e) {
			RubyCore.log(e);
		}
		return null;
	}

	private boolean isMethodCall(Node selected) {
		return (selected instanceof VCallNode) || (selected instanceof FCallNode) || (selected instanceof CallNode);
	}

	private List<IRubyElement> getChildrenWithName(IRubyElement[] children,
			int type, String name) throws RubyModelException {
		List<IRubyElement> possible = new ArrayList<IRubyElement>();
		for (int i = 0; i < children.length; i++) {
			IRubyElement child = children[i];
			if (child.getElementType() == type) {
				if (child.getElementName().equals(name))
					possible.add(child);
			}
			if (child instanceof IParent) {
				possible.addAll(getChildrenWithName(((IParent) child)
						.getChildren(), type, name));
			}
		}
		return possible;
	}

	private String getName(Node node) {
		if (node instanceof INameNode) {
			return ((INameNode) node).getName();
		}
		if (node instanceof ClassVarNode) {
			return ((ClassVarNode) node).getName();
		}
		return "";
	}

	private boolean isInstanceVarRef(Node node) {
		return ((node instanceof InstAsgnNode) || (node instanceof InstVarNode));
	}

	private boolean isClassVarRef(Node node) {
		return ((node instanceof ClassVarAsgnNode) || (node instanceof ClassVarNode));
	}

	private boolean isLocalVarRef(Node node) {
		return ((node instanceof LocalAsgnNode)
				|| (node instanceof ArgumentNode) || (node instanceof LocalVarNode));
	}
}
