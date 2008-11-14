package org.rubypeople.rdt.internal.ui.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.search.ui.text.Match;
import org.jruby.ast.ArgumentNode;
import org.jruby.ast.BlockArgNode;
import org.jruby.ast.BlockNode;
import org.jruby.ast.CallNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.ClassVarAsgnNode;
import org.jruby.ast.ClassVarDeclNode;
import org.jruby.ast.ClassVarNode;
import org.jruby.ast.Colon2Node;
import org.jruby.ast.Colon3Node;
import org.jruby.ast.ConstDeclNode;
import org.jruby.ast.ConstNode;
import org.jruby.ast.DAsgnNode;
import org.jruby.ast.DVarNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.GlobalAsgnNode;
import org.jruby.ast.GlobalVarNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.LocalVarNode;
import org.jruby.ast.MethodDefNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.Node;
import org.jruby.ast.ReturnNode;
import org.jruby.ast.SymbolNode;
import org.jruby.ast.VCallNode;
import org.jruby.ast.types.INameNode;
import org.jruby.lexer.yacc.IDESourcePosition;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.internal.core.util.ASTUtil;
import org.rubypeople.rdt.internal.ti.util.FirstPrecursorNodeLocator;
import org.rubypeople.rdt.internal.ti.util.INodeAcceptor;
import org.rubypeople.rdt.internal.ti.util.OffsetNodeLocator;
import org.rubypeople.rdt.internal.ti.util.ScopedNodeLocator;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

/**
 * Implements "Mark Occurences" feature
 * 
 * @author Jason Morrison
 * 
 */
public class OccurrencesFinder extends AbstractOccurencesFinder {

	// Root of the document to search
	private Node root;

	// Originating node; corresponds to cursor selection
	private Node fSelectedNode;

	private List<Node> fUsages = new ArrayList<Node>();
	private List<Node> fWriteUsages = new ArrayList<Node>();

	public String getJobLabel() {
		return SearchMessages.OccurrencesFinder_searchfor;
	}

	public String getUnformattedPluralLabel() {
		return SearchMessages.OccurrencesFinder_label_plural;
	}

	public String getUnformattedSingularLabel() {
		return SearchMessages.OccurrencesFinder_label_singular;
	}

	public void collectOccurrenceMatches(IRubyElement element,
			IDocument document, Collection resultingMatches) {
		HashMap lineToGroup = new HashMap();

		for (Iterator iter = fUsages.iterator(); iter.hasNext();) {
			Node node = (Node) iter.next();
			ISourcePosition position;
			try {
				position = getPositionOfName(node);
			} catch (RuntimeException e) {
				RubyPlugin.log(e);
				continue;
			}
			if (position == null)
				continue;
			int startPosition = position.getStartOffset();
			if (startPosition < 0)
				continue;
			int length = position.getEndOffset() - position.getStartOffset();
			try {
				boolean isWriteAccess = fWriteUsages.contains(node);
				int line = document.getLineOfOffset(startPosition);
				Integer lineInteger = new Integer(line);
				OccurrencesGroupKey groupKey = (OccurrencesGroupKey) lineToGroup
						.get(lineInteger);
				if (groupKey == null) {
					IRegion region = document.getLineInformation(line);
					String lineContents = document.get(region.getOffset(),
							region.getLength()).trim();
					groupKey = new OccurrencesGroupKey(element, line,
							lineContents, isWriteAccess,
							isVariable(fSelectedNode));
					lineToGroup.put(lineInteger, groupKey);
				} else if (isWriteAccess) {
					// a line with read an write access is considered as write
					// access:
					groupKey.setWriteAccess(true);
				}
				Match match = new Match(groupKey, startPosition, length);
				resultingMatches.add(match);
			} catch (BadLocationException e) {
				// nothing
			}
		}
	}

	private boolean isVariable(Node node) {
		return ASTUtil.isVariable(node);
	}

	public String initialize(Node root, int offset, int length) {
		if (root == null) {
			return null;
		}
		this.root = root;
		this.fSelectedNode = OffsetNodeLocator.Instance().getNodeAtOffset(root,
				offset);
		if (fSelectedNode == null) {
			return SearchMessages.OccurrencesFinder_no_element;
		}
		// if (fSelectedNode.getPosition().getEndOffset() > offset + length) {
		// // Selection spans nodes; not handling that for now.
		// return "Selection spans nodes; can only search for a single node.";
		// }
		fUsages.clear();
		fWriteUsages.clear();
		return null;
	}

	/**
	 * Determines the kind of originating node, and collects occurrences
	 * accordingly
	 */
	public List<Position> perform() {
		// Mark no occurrences if root is null (AST couldn't be parsed
		// correctly.)
		if (root == null)
			return new LinkedList<Position>();
		if (fSelectedNode == null)
			return new LinkedList<Position>();

		if (fMarkLocalVariableOccurrences && isLocalVarRef(fSelectedNode)) {
			pushLocalVarRefs(root, fSelectedNode, fUsages);
		}

		if (fMarkLocalVariableOccurrences && isDVarRef(fSelectedNode)) {
			pushDVarRefs(root, fSelectedNode, fUsages);
		}

		if (fMarkLocalVariableOccurrences && isInstanceVarRef(fSelectedNode)) {
			pushInstVarRefs(root, fSelectedNode, fUsages);
		}

		if (fMarkLocalVariableOccurrences && isClassVarRef(fSelectedNode)) {
			pushClassVarRefs(root, fSelectedNode, fUsages);
		}

		if (fMarkLocalVariableOccurrences && isGlobalVarRef(fSelectedNode)) {
			pushGlobalVarRefs(root, fSelectedNode, fUsages);
		}

		if (fMarkConstantOccurrences && fSelectedNode instanceof SymbolNode) {
			pushSymbolRefs(root, fSelectedNode, fUsages);
		}

		if (fMarkMethodOccurrences
				&& (isMethodRefNode(fSelectedNode) || isMethodDefNode(fSelectedNode))) {
			pushMethodRefs(root, fSelectedNode, fUsages);
		}

		if (fMarkConstantOccurrences && isConstRef(fSelectedNode)) {
			pushConstRefs(root, fSelectedNode, fUsages);
		}

		if (fMarkTypeOccurrences && isTypeRef(fSelectedNode)) {
			pushTypeRefs(root, fSelectedNode, fUsages);
		}

		if (fMarkMethodExitPoints) {
			pushReturns(root, fSelectedNode, fUsages);
		}

		// Convert ISourcePosition to IPosition
		List<Position> positions = new LinkedList<Position>();
		for (Node node : fUsages) {
			try {
				ISourcePosition occurrence = getPositionOfName(node);
				if (occurrence == null)
					continue;
				Position position = new Position(occurrence.getStartOffset(),
						occurrence.getEndOffset() - occurrence.getStartOffset());
				positions.add(position);
			} catch (RuntimeException re) {
				RubyPlugin.log(re);
			}
		}

		// Uniqueify positions
		positions = new LinkedList<Position>(new HashSet<Position>(positions));

		return positions;
	}

	private boolean isMethodRefNode(Node selectedNode) {
		return selectedNode instanceof VCallNode
				|| selectedNode instanceof FCallNode
				|| selectedNode instanceof CallNode;
	}

	// ****************************************************************************
	// *
	// * Reference kind definitions
	// *
	// ****************************************************************************

	/**
	 * Determines whether a given node is a local variable reference
	 * 
	 * @param node
	 * @return
	 */
	private boolean isLocalVarRef(Node node) {
		return ((node instanceof LocalAsgnNode)
				|| (node instanceof ArgumentNode) || (node instanceof LocalVarNode));
	}

	/**
	 * Determines whether a given node is a dynamic variable reference
	 * 
	 * @param node
	 * @return
	 */
	private boolean isDVarRef(Node node) {
		return ((node instanceof DVarNode) || (node instanceof DAsgnNode));
	}

	/**
	 * Determines whether a given node is an instance variable reference
	 * 
	 * @param node
	 * @return
	 */
	private boolean isInstanceVarRef(Node node) {
		return ((node instanceof InstAsgnNode) || (node instanceof InstVarNode));
	}

	/**
	 * Determines whether a given node is a class variable reference
	 * 
	 * @param node
	 * @return
	 */
	private boolean isClassVarRef(Node node) {
		return ((node instanceof ClassVarNode)
				|| (node instanceof ClassVarAsgnNode) || (node instanceof ClassVarDeclNode));
	}

	/**
	 * Determines whether a given node is a global variable reference
	 * 
	 * @param node
	 * @return
	 */
	private boolean isGlobalVarRef(Node node) {
		return ((node instanceof GlobalAsgnNode) || (node instanceof GlobalVarNode));
	}

	/**
	 * Determines whether a given node is a constant reference (constant)
	 * 
	 * @param node
	 * @return
	 */
	private boolean isConstRef(Node node) {
		return (node instanceof ConstNode) || (node instanceof ConstDeclNode);
	}

	/**
	 * Determines whether a given node is a type reference (class, module)
	 * 
	 * @param node
	 * @return
	 */
	private boolean isTypeRef(Node node) {
		// TODO: Classes can be referred to as a ConstNode; i.e. "class
		// Klass;end; k = Klass.new" the last reference is a ConstNode, not a
		// ClassNode. Special way to handle this?
		return ((node instanceof ClassNode) || (node instanceof ModuleNode) || (node instanceof ConstNode));
	}

	// ****************************************************************************
	// *
	// * Worker methods - handles delegation of occurrence searches
	// *
	// ****************************************************************************

	/**
	 * Collects all corresponding local variable occurrences
	 * 
	 * @param root
	 *            Root node to search
	 * @param fSelectedNode
	 *            Originating node
	 * @param occurrences
	 */
	private void pushLocalVarRefs(Node root, Node orig, List<Node> occurrences) {
		// Find the search space
		Node searchSpace = FirstPrecursorNodeLocator.Instance()
				.findFirstPrecursor(root, orig.getPosition().getStartOffset(),
						new INodeAcceptor() {
							public boolean doesAccept(Node node) {
								return ((node instanceof DefnNode) || (node instanceof DefsNode)); // TODO:
								// Block
								// Body?
							}
						});

		// If no enclosing node found, search the entire space
		if (searchSpace == null) {
			searchSpace = root;
		}

		// Finalize searchSpace because Java's scoping rules are the awesome
		final Node finalSearchSpace = searchSpace;

		// Get name of local variable reference
		final String origName = ASTUtil.getNameReflectively(orig);

		// Find all pertinent nodes
		List<Node> searchResults = ScopedNodeLocator.Instance()
				.findNodesInScope(searchSpace, new INodeAcceptor() {
					public boolean doesAccept(Node node) {
						String name = ASTUtil.getNameReflectively(node);
						return (name != null && name.equals(origName));
					}
				});

		// Scrape position from pertinent nodes
		for (Node searchResult : searchResults) {
			occurrences.add(searchResult);
			if (searchResult instanceof LocalAsgnNode)
				fWriteUsages.add(searchResult);
		}
	}

	/**
	 * Collects all corresponding dynamic variable occurrences
	 * 
	 * @param root
	 *            Root node to search
	 * @param fSelectedNode
	 *            Originating node
	 * @param occurrences
	 */
	private void pushDVarRefs(Node root, Node orig, List<Node> occurrences) {
		// Find the search space
		Node searchSpace = FirstPrecursorNodeLocator.Instance()
				.findFirstPrecursor(root, orig.getPosition().getStartOffset(),
						new INodeAcceptor() {
							public boolean doesAccept(Node node) {
								return ((node instanceof DefnNode) || (node instanceof DefsNode)); // TODO:
								// Block
								// Body?
							}
						});

		// If no enclosing node found, search the entire space
		if (searchSpace == null) {
			searchSpace = root;
		}

		// Get name of local variable reference
		final String origName = ASTUtil.getNameReflectively(orig);

		// Find all pertinent nodes
		List<Node> searchResults = ScopedNodeLocator.Instance()
				.findNodesInScope(searchSpace, new INodeAcceptor() {
					public boolean doesAccept(Node node) {
						if (isDVarRef(node)) {
							String name = ASTUtil.getNameReflectively(node);
							return (name != null && name.equals(origName));
						}
						return false;
					}
				});

		// Scrape position from pertinent nodes
		for (Node searchResult : searchResults) {
			occurrences.add(searchResult);
			if (searchResult instanceof DAsgnNode)
				fWriteUsages.add(searchResult);
		}
	}

	/**
	 * Collects all instance variable occurrences
	 * 
	 * @param root
	 * @param fSelectedNode
	 * @param occurrences
	 */
	private void pushInstVarRefs(Node root, Node orig, List<Node> occurrences) {
		Node searchSpace = determineSearchSpace(root, orig);

		// Finalize searchSpace because Java's scoping rules are the awesome
		// TODO: not needed?
		// final Node finalSearchSpace = searchSpace;

		// Get name of local variable reference
		final String origName = ASTUtil.getNameReflectively(orig);

		// Find all pertinent nodes
		List<Node> searchResults = ScopedNodeLocator.Instance()
				.findNodesInScope(searchSpace, new INodeAcceptor() {
					public boolean doesAccept(Node node) {
						if (isInstanceVarRef(node)) {
							String name = ASTUtil.getNameReflectively(node);
							return (name != null && name.equals(origName));
						}
						return false;
					}
				});

		// Scrape position from pertinent nodes
		for (Node searchResult : searchResults) {
			occurrences.add(searchResult);
			if (searchResult instanceof InstAsgnNode)
				fWriteUsages.add(searchResult);
		}

	}

	private Node determineSearchSpace(Node root, Node orig) {

		// Find the name of the enclosing class
		ClassNode enclosingClass = (ClassNode) FirstPrecursorNodeLocator
				.Instance().findFirstPrecursor(root,
						orig.getPosition().getStartOffset(),
						new INodeAcceptor() {
							public boolean doesAccept(Node node) {
								return (node instanceof ClassNode);
							}
						});

		// If no enclosing class is identified, search root.
		if (enclosingClass == null) {
			return root;
		}
		// Find the search space - all ClassNodes for that name within root
		// scope
		else {
			final String className = getClassNodeName(enclosingClass);
			List<Node> classNodes = ScopedNodeLocator.Instance()
					.findNodesInScope(root, new INodeAcceptor() {
						public boolean doesAccept(Node node) {
							if (node instanceof ClassNode) {
								return getClassNodeName((ClassNode) node)
										.equals(className);
							}
							return false;
						}
					});
			BlockNode blockNode = new BlockNode(new IDESourcePosition());
			for (Node classNode : classNodes) {
				blockNode.add(classNode);
			}
			return blockNode;
		}
	}

	/**
	 * Collects all class variable occurrences
	 * 
	 * @param root
	 * @param fSelectedNode
	 * @param occurrences
	 */
	private void pushClassVarRefs(Node root, Node orig, List<Node> occurrences) {
		Node searchSpace = determineSearchSpace(root, orig);

		// Finalize searchSpace because Java's scoping rules are the awesome
		// todo: not needed?
		// final Node finalSearchSpace = searchSpace;

		// Get name of local variable reference
		final String origName = ASTUtil.getNameReflectively(orig);

		// Find all pertinent nodes
		List<Node> searchResults = ScopedNodeLocator.Instance()
				.findNodesInScope(searchSpace, new INodeAcceptor() {
					public boolean doesAccept(Node node) {
						if (isClassVarRef(node)) {
							String name = ASTUtil.getNameReflectively(node);
							return (name != null && name.equals(origName));
						}
						return false;
					}
				});

		// Scrape position from pertinent nodes
		for (Node searchResult : searchResults) {
			occurrences.add(searchResult);
			if ((searchResult instanceof ClassVarAsgnNode)
					|| (searchResult instanceof ClassVarDeclNode))
				fWriteUsages.add(searchResult);
		}

	}

	/**
	 * Collects all global variable occurrences
	 * 
	 * @param root
	 * @param fSelectedNode
	 * @param occurrences
	 */
	private void pushGlobalVarRefs(Node root, Node orig, List<Node> occurrences) {
		final Node searchSpace = root;
		final String origName = ASTUtil.getNameReflectively(orig);

		// Find all pertinent nodes
		List<Node> searchResults = ScopedNodeLocator.Instance()
				.findNodesInScope(searchSpace, new INodeAcceptor() {
					public boolean doesAccept(Node node) {
						return isGlobalVarRef(node)
								&& ASTUtil.getNameReflectively(node).equals(
										origName);
					}
				});

		// Scrape position from pertinent nodes
		for (Node searchResult : searchResults) {
			occurrences.add(searchResult);
			if (searchResult instanceof GlobalAsgnNode)
				fWriteUsages.add(searchResult);
		}
	}

	/**
	 * Collects all symbol occurrences
	 * 
	 * @param root
	 * @param fSelectedNode
	 * @param occurrences
	 */
	private void pushSymbolRefs(Node root, Node orig, List<Node> occurrences) {
		final Node searchSpace = root;
		final String origName = ((SymbolNode) orig).getName();

		// Find all pertinent nodes
		List<Node> searchResults = ScopedNodeLocator.Instance()
				.findNodesInScope(searchSpace, new INodeAcceptor() {
					public boolean doesAccept(Node node) {
						return (node instanceof SymbolNode)
								&& ((SymbolNode) node).getName().equals(
										origName);
					}
				});

		// Scrape position from pertinent nodes
		for (Node searchResult : searchResults) {
			occurrences.add(searchResult);
		}
	}

	// todo: complete
	// private void pushMethodRefs( Node root, Node fSelectedNode,
	// List<ISourcePosition>
	// occurrences) {
	//	
	// // DefnNode DefsNode CallNode VCallNode
	//		
	// System.out.println("Finding occurrences for method reference node " +
	// fSelectedNode.toString() );
	//		
	// final Node searchSpace = root;
	// String origName = getMethodRefName(fSelectedNode);
	//		
	// // If fSelectedNode is a method definition, find all occurrences to that
	// selector
	// for the fSelectedNode's enclosing type
	// if ( fSelectedNode instanceof DefnNode || fSelectedNode instanceof
	// DefsNode )
	// {
	// ((DefnNode)fSelectedNode).g
	// }
	//		
	// Node receiver = getMethodReceiver(fSelectedNode);
	// }

	/**
	 * Collects all pertinent const occurrences
	 */
	private void pushConstRefs(Node root, Node orig, List<Node> occurrences) {
		if (!isConstRef(orig)) {
			return;
		}

		final String matchName = ASTUtil.getNameReflectively(orig);
		List<Node> searchResults = ScopedNodeLocator.Instance()
				.findNodesInScope(root, new INodeAcceptor() {
					public boolean doesAccept(Node node) {
						if (isConstRef(node)) {
							return ASTUtil.getNameReflectively(node).equals(
									matchName);
						}
						return false;
					}
				});

		for (Node searchResult : searchResults) {
			occurrences.add(searchResult);
			if (searchResult instanceof ConstDeclNode)
				fWriteUsages.add(searchResult);
		}
	}

	/**
	 * Collects all pertinent method calls
	 */
	private void pushMethodRefs(Node root, Node orig, List<Node> occurrences) {
		if (!isMethodRefNode(orig) && !isMethodDefNode(orig)) {
			return;
		}

		final String matchName = ASTUtil.getNameReflectively(orig);
		List<Node> searchResults = ScopedNodeLocator.Instance()
				.findNodesInScope(root, new INodeAcceptor() {
					public boolean doesAccept(Node node) {
						if (isMethodRefNode(node) || isMethodDefNode(node)) {
							return ASTUtil.getNameReflectively(node).equals(
									matchName);
						}
						return false;
					}
				});

		for (Node searchResult : searchResults) {
			occurrences.add(searchResult);
		}
	}

	protected boolean isMethodDefNode(Node node) {
		return node instanceof MethodDefNode;
	}

	/**
	 * Collects all pertinent type ref occurrences
	 */
	private void pushTypeRefs(Node root, Node orig, List<Node> occurrences) {
		if (!isTypeRef(orig)) {
			return;
		}

		final String matchName = ASTUtil.getNameReflectively(orig);
		List<Node> searchResults = ScopedNodeLocator.Instance()
				.findNodesInScope(root, new INodeAcceptor() {
					public boolean doesAccept(Node node) {
						if (isTypeRef(node)) {
							return getTypeRefName(node).equals(matchName);
						}
						return false;
					}
				});

		for (Node searchResult : searchResults) {
			occurrences.add(searchResult);
		}
	}

	private void pushReturns(Node root, Node orig, List<Node> occurrences) {
		// TODO Combine most of this stuff with the stuff in pushLocalVarRefs
		// Find the search space
		Node searchSpace = FirstPrecursorNodeLocator.Instance()
				.findFirstPrecursor(root, orig.getPosition().getStartOffset(),
						new INodeAcceptor() {
							public boolean doesAccept(Node node) {
								return ((node instanceof DefnNode) || (node instanceof DefsNode)); // TODO:
								// Block
								// Body?
							}
						});

		// If no enclosing node found, search the entire space
		if (searchSpace == null) {
			searchSpace = root;
		}
		// Find all return nodes
		List<Node> searchResults = ScopedNodeLocator.Instance()
				.findNodesInScope(searchSpace, new INodeAcceptor() {
					public boolean doesAccept(Node node) {
						return (node instanceof ReturnNode);
					}
				});

		// Scrape position from pertinent nodes
		for (Node searchResult : searchResults) {
			occurrences.add(searchResult);
		}
	}

	// ****************************************************************************
	// *
	// * Utility methods
	// *
	// ****************************************************************************

	/**
	 * Gets the position of the name for the specified node.
	 * 
	 * @param node
	 *            Node that responds to getName() or some variant
	 * @return ISourcePosition that holds the name of the node
	 */
	private ISourcePosition getPositionOfName(Node node) {
		ISourcePosition pos = node.getPosition();

		// TODO refactor the getting-of-name
		String name = null;
		if (node instanceof ReturnNode) {
			return node.getPosition();
		} else if (isLocalVarRef(node) || isDVarRef(node)
				|| isInstanceVarRef(node) || isGlobalVarRef(node)
				|| isClassVarRef(node) || isConstRef(node)
				|| node instanceof BlockArgNode) {
			name = ASTUtil.getNameReflectively(node);
		} else if (node instanceof ClassNode) {
			return ((ClassNode) node).getCPath().getPosition();
		} else if (node instanceof ModuleNode) {
			return ((ModuleNode) node).getCPath().getPosition();
		} else if (node instanceof SymbolNode) {
			// XXX: This is a hack to get around improper offsets in my JRuby
			// copy; ":foo" returns offset for ":fo", so compensate by adding
			// one
			name = ((SymbolNode) node).getName();
			return new IDESourcePosition(pos.getFile(), pos.getStartLine(), pos
					.getEndLine(), pos.getStartOffset(), pos.getStartOffset()
					+ name.length() + 1);
		} else if (node instanceof CallNode) {
			CallNode vcall = (CallNode) node;
			name = vcall.getName();
			Node receiver = vcall.getReceiverNode();
			int start = receiver.getPosition().getEndOffset() + 1;
			return new IDESourcePosition(pos.getFile(), pos.getStartLine(), pos
					.getEndLine(), start, start + name.length());
		} else if (node instanceof MethodDefNode) {
			MethodDefNode def = (MethodDefNode) node;
			return def.getNameNode().getPosition();
		} else if (node instanceof INameNode) {
			INameNode vcall = (INameNode) node;
			name = vcall.getName();
			return new IDESourcePosition(pos.getFile(), pos.getStartLine(), pos
					.getEndLine(), pos.getStartOffset(), pos.getStartOffset()
					+ name.length());
		}
		if (name == null) {
			throw new RuntimeException("Couldn't get the name for: "
					+ node.toString());
		}
		return new IDESourcePosition(pos.getFile(), pos.getStartLine(), pos
				.getEndLine(), pos.getStartOffset(), pos.getStartOffset()
				+ name.length());
	}

	/**
	 * Helper method to get the class name froma ClassNode
	 * 
	 * @param classNode
	 * @return
	 */
	private String getClassNodeName(ClassNode classNode) {
		if (classNode.getCPath() instanceof Colon2Node) {
			Colon2Node c2node = (Colon2Node) classNode.getCPath();
			return c2node.getName();
		} else if (classNode.getCPath() instanceof Colon3Node) {
			Colon3Node c2node = (Colon3Node) classNode.getCPath();
			return c2node.getName();
		}
		throw new RuntimeException(
				"ClassNode.getCPath() returned other than Colon2Node: "
						+ classNode.getCPath().toString());
	}

	/**
	 * Helper method to get the class name from a ModuleNode
	 * 
	 * @param classNode
	 * @return
	 */
	private String getModuleNodeName(ModuleNode moduleNode) {
		if (moduleNode.getCPath() instanceof Colon2Node) {
			Colon2Node c2node = (Colon2Node) moduleNode.getCPath();
			return c2node.getName();
		} else if (moduleNode.getCPath() instanceof Colon3Node) {
			Colon3Node c2node = (Colon3Node) moduleNode.getCPath();
			return c2node.getName();
		}
		throw new RuntimeException(
				"ModuleNode.getCPath() returned other than Colon2Node: "
						+ moduleNode.getCPath().toString());
	}

	/**
	 * Helper method to get the class name from a const ref node (Class/Module)
	 * 
	 * @param node
	 * @return
	 */
	private String getTypeRefName(Node node) {
		if (node instanceof ClassNode) {
			return getClassNodeName((ClassNode) node);
		}
		if (node instanceof ModuleNode) {
			return getModuleNodeName((ModuleNode) node);
		}
		return ASTUtil.getNameReflectively(node);
	}

	public String getElementName() {
		if (fSelectedNode != null) {
			return ASTUtil.stringRepresentation(fSelectedNode);
		}
		return null;
	}

}
