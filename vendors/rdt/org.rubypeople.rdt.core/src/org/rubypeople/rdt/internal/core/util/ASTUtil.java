package org.rubypeople.rdt.internal.core.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jruby.ast.ArgsNode;
import org.jruby.ast.ArgumentNode;
import org.jruby.ast.ArrayNode;
import org.jruby.ast.AttrAssignNode;
import org.jruby.ast.CallNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.ClassVarAsgnNode;
import org.jruby.ast.ClassVarDeclNode;
import org.jruby.ast.ClassVarNode;
import org.jruby.ast.Colon2Node;
import org.jruby.ast.ConstDeclNode;
import org.jruby.ast.ConstNode;
import org.jruby.ast.DAsgnNode;
import org.jruby.ast.DStrNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.FalseNode;
import org.jruby.ast.FixnumNode;
import org.jruby.ast.GlobalAsgnNode;
import org.jruby.ast.GlobalVarNode;
import org.jruby.ast.HashNode;
import org.jruby.ast.IArgumentNode;
import org.jruby.ast.IScopingNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.IterNode;
import org.jruby.ast.ListNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.MultipleAsgnNode;
import org.jruby.ast.NilNode;
import org.jruby.ast.Node;
import org.jruby.ast.SelfNode;
import org.jruby.ast.SplatNode;
import org.jruby.ast.StrNode;
import org.jruby.ast.SymbolNode;
import org.jruby.ast.TrueNode;
import org.jruby.ast.ZArrayNode;
import org.jruby.ast.types.INameNode;
import org.jruby.lexer.yacc.ISourcePosition;
import org.jruby.parser.StaticScope;
import org.rubypeople.rdt.internal.ti.util.ClosestSpanningNodeLocator;
import org.rubypeople.rdt.internal.ti.util.INodeAcceptor;

public abstract class ASTUtil {
	private static final boolean VERBOSE = false;

	private static final String NAMESPACE_DELIMETER = "::";
	private static final String OBJECT = "Object";
	private static final String EMPTY_STRING = "";
	
	/**
	 * @param argsNode
	 * @param bodyNode 
	 * @return
	 */
	public static String[] getArgs(Node argsNode, StaticScope bodyNode) {
		if (argsNode == null) return new String[0];
		ArgsNode args = (ArgsNode) argsNode;
		boolean hasRest = false;
		if (args.getRestArg() != -1)
			hasRest = true;
		
		boolean hasBlock = false;
		if (args.getBlockArgNode() != null)
			hasBlock = true;

		int optArgCount = 0;
		if (args.getOptArgs() != null)
			optArgCount = args.getOptArgs().size();
		List<String> arguments = getArguments(args.getArgs());
		if (optArgCount > 0) {
			arguments.addAll(getArguments(args.getOptArgs()));
		}
		if (hasRest) {
			String restName = "*";
			if (args.getRestArg() != -2) {
				restName += bodyNode.getVariables()[args.getRestArg()];
			}
			arguments.add(restName);
		}
		if (hasBlock)
			arguments.add("&" + bodyNode.getVariables()[args.getBlockArgNode().getCount()]);
		return stringListToArray(arguments);
	}
	
	private static String[] stringListToArray(List<String> list) {
		String[] array = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		return array;
	}
	
	private static List<String> getArguments(ListNode argList) {
		if (argList == null) return new ArrayList<String>();
		List<String> arguments = new ArrayList<String>();
		for (Iterator iter = argList.childNodes().iterator(); iter.hasNext();) {
			Object node = iter.next();
			if (node instanceof ArgumentNode) {
				arguments.add(((ArgumentNode) node).getName());
			} else if (node instanceof LocalAsgnNode) {
				LocalAsgnNode local = (LocalAsgnNode) node;
				String argString = local.getName();
				argString += " = ";
				argString += stringRepresentation(local.getValueNode());
				arguments.add(argString);
			} else {
					System.err
						.println("Reached argument node type we can't handle");
			}
		}
		return arguments;
	}
	
	public static String stringRepresentation(Node node) {
		if (node == null) return "";
		if (node instanceof HashNode)
			return "{}";
		if (node instanceof SelfNode)
			return "self";
		if (node instanceof NilNode)
			return "nil";
		if (node instanceof TrueNode)
			return "true";
		if (node instanceof FalseNode)
			return "false";
		if (node instanceof SymbolNode)
			return ':' + ((SymbolNode) node).getName();
		if (node instanceof INameNode)
			return ((INameNode)node).getName();
		if (node instanceof ZArrayNode)
			return "[]";
		if (node instanceof FixnumNode)
			return "" + ((FixnumNode) node).getValue();
		if (node instanceof DStrNode)
			return stringRepresentation((DStrNode) node);
		if (node instanceof StrNode)
			return '"' + ((StrNode) node).getValue().toString() + '"';		
		log("Reached node type we don't know how to represent: "
				+ node.getClass().getName());
		return node.toString();
	}

	private static void log(String string) {
		if (VERBOSE) System.out.println(string);		
	}

	private static String stringRepresentation(DStrNode node) {
		List children = node.childNodes();
		StringBuffer buffer = new StringBuffer();
		buffer.append("\"");
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			Node child = (Node) iter.next();
			buffer.append(stringRepresentation(child));
		}
		buffer.append("\"");
		return buffer.toString();
	}

	/**
	 * Gets the name of a node by reflectively invoking "getName()" on it;
	 * helper method just to cut many "instanceof/cast" pairs.
	 * 
	 * @param node
	 * @return name or null
	 */
	public static String getNameReflectively(Node node) {
		if (node == null) return "";
		if (node instanceof ClassNode) {
			ClassNode classNode = (ClassNode) node;
			return getNameReflectively(classNode.getCPath());
		}
		if (node instanceof ModuleNode) {
			ModuleNode moduleNode = (ModuleNode) node;
			return getNameReflectively(moduleNode.getCPath());
		}
		if (node instanceof INameNode) {
			return ((INameNode)node).getName();
		}
		try {
			Method getNameMethod = node.getClass().getMethod("getName", new Class[] {});
			Object name = getNameMethod.invoke(node, new Object[0]);
			return (String) name;
		} catch (Exception e) {
			return null;
		}
	}

	public static String getFullyQualifiedName(Colon2Node node) {
		StringBuffer name = new StringBuffer();
		Node left = node.getLeftNode();
		if (left instanceof Colon2Node) {
			name.append(getFullyQualifiedName((Colon2Node)left));
		} else if (left instanceof ConstNode) {
			name.append(((ConstNode)left).getName());
		}
		name.append("::");
		name.append(node.getName());
		return name.toString();
	}

	public static boolean isAssignment(Node node) {
		return (node  instanceof LocalAsgnNode) || (node instanceof ClassVarAsgnNode) 
			|| (node instanceof InstAsgnNode) || (node instanceof GlobalAsgnNode)
			|| (node instanceof AttrAssignNode);
	}
	
	public static String getSource(String contents, Node node) {
		if (node == null || contents == null) return null;
		ISourcePosition pos = node.getPosition();
		if (pos == null) return null;
		if (pos.getStartOffset() >= contents.length()) return null; // position is past end of our source
		if (pos.getEndOffset() > contents.length()) return null; // end is past end of source
		return contents.substring(pos.getStartOffset(), pos.getEndOffset());
	}

	public static boolean isVariable(Node node) {
		return (node instanceof GlobalAsgnNode) || (node instanceof GlobalVarNode) 
			|| (node instanceof InstAsgnNode) || (node instanceof InstVarNode)
			|| (node instanceof ConstDeclNode) || (node instanceof ConstNode)
			|| (node instanceof ClassVarAsgnNode) || (node instanceof ClassVarDeclNode)
			|| (node instanceof ClassVarNode);
	}

	public static List<String> getArgumentsFromFunctionCall(IArgumentNode iVisited) {
		List<String> arguments = new ArrayList<String>();
		List<Node> nodes = getArgumentNodesFromFunctionCall(iVisited);
		for (Node node : nodes) {
			if (node instanceof DAsgnNode) {
				DAsgnNode dasgn = (DAsgnNode) node;
				arguments.add(dasgn.getName());
			} else {
				arguments.add(stringRepresentation(node));
			}
		}
		return arguments;
	}

	public static List<Node> getArgumentNodesFromFunctionCall(IArgumentNode iVisited) {
		List<Node> arguments = new ArrayList<Node>();
		Node argsNode = iVisited.getArgsNode();
		Iterator iter = null;
		if (argsNode instanceof SplatNode) {
			SplatNode splat = (SplatNode) argsNode;
			iter = splat.childNodes().iterator();
		} else if (argsNode instanceof ArrayNode) {
			ArrayNode arrayNode = (ArrayNode) iVisited.getArgsNode();
			iter = arrayNode.childNodes().iterator();
		} else if (argsNode == null) {
			// Block?
			Node iterNode = null;
			if (iVisited instanceof FCallNode) {
				FCallNode fcall = (FCallNode) iVisited;
				iterNode = fcall.getIterNode();
			} else if (iVisited instanceof CallNode) {
				CallNode call = (CallNode) iVisited;	
				iterNode = call.getIterNode();
			}
			if (iterNode == null) return arguments;
			if (iterNode instanceof IterNode) { // yup, it has a block
				IterNode yeah = (IterNode) iterNode;
				Node varNode = yeah.getVarNode();
				if (varNode instanceof DAsgnNode) { // single variable in block
					DAsgnNode dassgn = (DAsgnNode) varNode;
					arguments.add(dassgn);
				} else if (varNode instanceof MultipleAsgnNode) { // multiple variables in block
					MultipleAsgnNode multi = (MultipleAsgnNode) varNode;
					ListNode list = multi.getHeadNode();
					if (list != null)
						iter = list.childNodes().iterator();
					else {
						Node multiArgsNode = multi.getArgsNode();
						if (multiArgsNode instanceof DAsgnNode) { // single variable in block
							DAsgnNode dassgn = (DAsgnNode) multiArgsNode;
							arguments.add(dassgn);
						}
					}
				}
			}
		}
		if (iter == null) return arguments;
		for (; iter.hasNext();) {
			Node argument = (Node) iter.next();
			arguments.add(argument);
		}
		return arguments;
	}
	
	/**
	 * Build up the fully qualified name of the super class for a class
	 * declaration
	 * 
	 * @param superNode
	 * @return
	 */
	public static String getSuperClassName(Node superNode) {
		if (superNode == null)
			return OBJECT;
		return getFullyQualifiedName(superNode);
	}
	
	public static String getFullyQualifiedName(Node node) {
		if (node == null)
			return EMPTY_STRING;
		if (node instanceof ConstNode) {
			ConstNode constNode = (ConstNode) node;
			return constNode.getName();
		}
		if (node instanceof Colon2Node) {
			Colon2Node colonNode = (Colon2Node) node;
			String prefix = getFullyQualifiedName(colonNode.getLeftNode());
			if (prefix.length() > 0)
				prefix = prefix + NAMESPACE_DELIMETER;
			return prefix + colonNode.getName();
		}
		return getNameReflectively(node);
	}
	
	public static String getFullyQualifiedTypeName(Node rootNode, Node typeNode) {
		return ASTUtil.getNamespace(rootNode, typeNode.getPosition().getStartOffset());
	}

	public static String getNamespace(Node root, int offset) {
		List<Node> surrounding = new ArrayList<Node>();
		Node typeNode = ClosestSpanningNodeLocator.Instance().findClosestSpanner(root, offset, new INodeAcceptor() {
		
			public boolean doesAccept(Node node) {
				return node instanceof ModuleNode || node instanceof ClassNode;
			}
		
		});
		if (typeNode == null) return "";
		if (offset < ((IScopingNode)typeNode).getCPath().getPosition().getEndOffset()) {
			typeNode = ClosestSpanningNodeLocator.Instance().findClosestSpanner(root, typeNode.getPosition().getStartOffset() - 1, new INodeAcceptor() {
				
				public boolean doesAccept(Node node) {
					return node instanceof ModuleNode || node instanceof ClassNode;
				}
			
			});
		}
		
		while (typeNode != null) {
			surrounding.add(0, typeNode);
			typeNode = ClosestSpanningNodeLocator.Instance().findClosestSpanner(root, typeNode.getPosition().getStartOffset() - 1, new INodeAcceptor() {
				
				public boolean doesAccept(Node node) {
					return node instanceof ModuleNode || node instanceof ClassNode;
				}
			
			});
		}
		StringBuffer buffer = new StringBuffer();
		boolean first = true;
		for (Node node : surrounding) {
			if (!first) {
				buffer.append("::");
			}
			buffer.append(getNameReflectively(node));
			if (first) {
				first = false;
			}
		}
		return buffer.toString();
	}

}
