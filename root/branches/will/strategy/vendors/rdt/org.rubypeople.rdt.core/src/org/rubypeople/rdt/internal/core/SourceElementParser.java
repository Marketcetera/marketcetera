/*
 * Author: C.Williams
 * 
 * Copyright (c) 2004 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. You
 * can get copy of the GPL along with further information about RubyPeople and
 * third party software bundled with RDT in the file
 * org.rubypeople.rdt.core_x.x.x/RDT.license or otherwise at
 * http://www.rubypeople.org/RDT.license.
 * 
 * RDT is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * RDT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * RDT; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package org.rubypeople.rdt.internal.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jruby.ast.AliasNode;
import org.jruby.ast.ArgsNode;
import org.jruby.ast.ArgumentNode;
import org.jruby.ast.ArrayNode;
import org.jruby.ast.AssignableNode;
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
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.GlobalAsgnNode;
import org.jruby.ast.GlobalVarNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.IterNode;
import org.jruby.ast.ListNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.LocalVarNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.Node;
import org.jruby.ast.RootNode;
import org.jruby.ast.SClassNode;
import org.jruby.ast.SelfNode;
import org.jruby.ast.SplatNode;
import org.jruby.ast.StrNode;
import org.jruby.ast.VCallNode;
import org.jruby.ast.YieldNode;
import org.jruby.evaluator.Instruction;
import org.jruby.runtime.Visibility;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.internal.compiler.ISourceElementRequestor;
import org.rubypeople.rdt.internal.compiler.ISourceElementRequestor.FieldInfo;
import org.rubypeople.rdt.internal.compiler.ISourceElementRequestor.MethodInfo;
import org.rubypeople.rdt.internal.compiler.ISourceElementRequestor.TypeInfo;
import org.rubypeople.rdt.internal.core.parser.InOrderVisitor;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.core.util.ASTUtil;

/**
 * @author Chris
 * 
 */
public class SourceElementParser extends InOrderVisitor {

	private static final String MODULE_FUNCTION = "module_function";
	private static final String PROTECTED = "protected";
	private static final String PRIVATE = "private";
	private static final String PUBLIC = "public";
	private static final String INCLUDE = "include";
	private static final String LOAD = "load";
	private static final String REQUIRE = "require";
	private static final String ALIAS = "alias :";
	private static final String MODULE = "Module";
	private static final String CONSTRUCTOR_NAME = "initialize";
	private static final String OBJECT = "Object";
	
	private List<Visibility> visibilities = new ArrayList<Visibility>();
	private boolean inSingletonClass;
	public ISourceElementRequestor requestor;
	private boolean inModuleFunction;
	private char[] source;

	private String typeName;
	
	/**
	 * 
	 * @param requestor The {@link ISourceElementRequestor} that wants to be notified of the source structure
	 */
	public SourceElementParser(ISourceElementRequestor requestor) {
		super();
		this.requestor = requestor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jruby.ast.visitor.NodeVisitor#visitClassNode(org.jruby.ast.ClassNode)
	 */
	public Instruction visitClassNode(ClassNode iVisited) {
		// This resets the visibility when opening or declaring a class to
		// public
		pushVisibility(Visibility.PUBLIC);

		TypeInfo typeInfo = new TypeInfo();
		typeInfo.name = ASTUtil.getFullyQualifiedName(iVisited.getCPath());
		typeInfo.declarationStart = iVisited.getPosition().getStartOffset();
		typeInfo.nameSourceStart = iVisited.getCPath().getPosition().getStartOffset();
		typeInfo.nameSourceEnd = iVisited.getCPath().getPosition().getEndOffset() - 1;
		if (!typeInfo.name.equals(OBJECT)) {
		  String superClass = ASTUtil.getSuperClassName(iVisited.getSuperNode());
		  typeInfo.superclass = superClass;
		}
		typeInfo.isModule = false;
		typeInfo.modules = new String[0];
		typeInfo.secondary = false; // TODO Set secondary to true if we're enclosed by another type?
		typeName = typeInfo.name;
		requestor.enterType(typeInfo);
		
		Instruction ins = super.visitClassNode(iVisited);
		popVisibility();
		requestor.exitType(iVisited.getPosition().getEndOffset() - 2);
		return ins;
	}
	
	@Override
	public Instruction visitConstNode(ConstNode iVisited) {
		// FIXME ConstNode could be a reference to a type, or a constant(field)!
		requestor.acceptTypeReference(iVisited.getName(), iVisited.getPosition().getStartOffset(), iVisited.getPosition().getEndOffset());
		return super.visitConstNode(iVisited);
	}
	
	@Override
	public Instruction visitModuleNode(ModuleNode iVisited) {
		pushVisibility(Visibility.PUBLIC);
		TypeInfo typeInfo = new TypeInfo();
		typeInfo.name = ASTUtil.getFullyQualifiedName(iVisited.getCPath());
		typeInfo.declarationStart = iVisited.getPosition().getStartOffset();
		typeInfo.nameSourceStart = iVisited.getCPath().getPosition().getStartOffset();
		typeInfo.nameSourceEnd = iVisited.getCPath().getPosition().getEndOffset() - 1;
		typeInfo.superclass = MODULE; // FIXME Is this really true? Should it be null?
		typeInfo.isModule = true;
		typeInfo.modules = new String[0];
		typeInfo.secondary = false; // TODO Set secondary to true if we're enclosed by another type?
		typeName = typeInfo.name;
		requestor.enterType(typeInfo);
		
		Instruction ins = super.visitModuleNode(iVisited);
		
		popVisibility();
		requestor.exitType(iVisited.getPosition().getEndOffset() - 2);
		inModuleFunction = false;
		return ins;
	}
	
	@Override
	public Instruction visitDefnNode(DefnNode iVisited) {
		Visibility visibility = getCurrentVisibility();		
		MethodInfo methodInfo = new MethodInfo();
		methodInfo.declarationStart = iVisited.getPosition().getStartOffset();
		methodInfo.name = iVisited.getName();
		methodInfo.nameSourceStart = iVisited.getNameNode().getPosition().getStartOffset();
		methodInfo.nameSourceEnd = iVisited.getNameNode().getPosition().getEndOffset() - 1;
		if (methodInfo.name.equals(CONSTRUCTOR_NAME)) {
			visibility = Visibility.PROTECTED;
			methodInfo.isConstructor = true;
		} else {
			methodInfo.isConstructor = false;
		}
		methodInfo.isClassLevel = inSingletonClass || inModuleFunction;
		methodInfo.visibility = convertVisibility(visibility);
		methodInfo.parameterNames = ASTUtil.getArgs(iVisited.getArgsNode(), iVisited.getScope());
		
		if (methodInfo.isConstructor) {
			requestor.enterConstructor(methodInfo);
		} else {
			requestor.enterMethod(methodInfo);
		}
		
		Instruction ins = super.visitDefnNode(iVisited); // now traverse it's body
		int end = iVisited.getPosition().getEndOffset() - 2;
		if (methodInfo.isConstructor) {
			requestor.exitConstructor(end);
		} else {
			requestor.exitMethod(end);
		}
		return ins;
	}
	
	@Override
	public Instruction visitArgsNode(ArgsNode iVisited) {
		// Add args as local vars!
		ListNode list = iVisited.getArgs();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				Node arg = list.get(i);
				FieldInfo field = new FieldInfo();
				field.declarationStart = arg.getPosition().getStartOffset();
				field.nameSourceStart = arg.getPosition().getStartOffset();
				String name = ASTUtil.getNameReflectively(arg);
				field.nameSourceEnd = arg.getPosition().getStartOffset()
						+ name.length() - 1;
				field.name = name;
				requestor.enterField(field);
				requestor.exitField(arg.getPosition().getEndOffset() - 1);
			}
		}
		ArgumentNode arg = iVisited.getRestArgNode();
		if (arg != null) {
			FieldInfo field = new FieldInfo();	
			field.declarationStart = arg.getPosition().getStartOffset() + 1;
			field.nameSourceStart = arg.getPosition().getStartOffset() + 1;
			String name = ASTUtil.getNameReflectively(arg);
			field.nameSourceEnd = arg.getPosition().getStartOffset() + name.length();			
			field.name = name;
			requestor.enterField(field);
			requestor.exitField(arg.getPosition().getEndOffset());		
		}
		return super.visitArgsNode(iVisited);
	}
	
	@Override
	public Instruction visitDefsNode(DefsNode iVisited) {
		MethodInfo methodInfo = new MethodInfo();
		methodInfo.declarationStart = iVisited.getPosition().getStartOffset();
		methodInfo.name = iVisited.getName();
		methodInfo.nameSourceStart = iVisited.getNameNode().getPosition().getStartOffset();
		methodInfo.nameSourceEnd = iVisited.getNameNode().getPosition().getEndOffset() - 1;
		methodInfo.isConstructor = false;
		methodInfo.isClassLevel = true;
		methodInfo.visibility = convertVisibility(getCurrentVisibility());
		methodInfo.parameterNames = ASTUtil.getArgs(iVisited.getArgsNode(), iVisited.getScope());
		requestor.enterMethod(methodInfo);

		Instruction ins = super.visitDefsNode(iVisited); // now traverse it's body
		
		requestor.exitMethod(iVisited.getPosition().getEndOffset() - 2);
		return ins;
	}
	
	/**
	 * @param visibility
	 * @return
	 */
	private int convertVisibility(Visibility visibility) {
		// FIXME What about the module function and public-protected
		// visibilities?
		if (visibility == Visibility.PUBLIC)
			return IMethod.PUBLIC;
		if (visibility == Visibility.PROTECTED)
			return IMethod.PROTECTED;
		return IMethod.PRIVATE;
	}
	
	@Override
	public Instruction visitRootNode(RootNode iVisited) {
		requestor.enterScript();
		pushVisibility(Visibility.PUBLIC);
		Instruction ins = super.visitRootNode(iVisited);
		popVisibility();
		requestor.exitScript(iVisited.getPosition().getEndOffset());
		return ins;
	}
	
	private void popVisibility() {
		visibilities.remove(visibilities.size() - 1);		
	}
	
	@Override
	public Instruction visitConstDeclNode(ConstDeclNode iVisited) {			
		FieldInfo field = createFieldInfo(iVisited);
		field.name = iVisited.getName();
		requestor.enterField(field);
		exitField(iVisited);
		return super.visitConstDeclNode(iVisited);
	}
	
	public Instruction visitClassVarAsgnNode(ClassVarAsgnNode iVisited) {
		FieldInfo field = createFieldInfo(iVisited);
		field.name = iVisited.getName();
		requestor.enterField(field);
		exitField(iVisited);
		return super.visitClassVarAsgnNode(iVisited);
	}
	
	@Override
	public Instruction visitClassVarDeclNode(ClassVarDeclNode iVisited) {
		FieldInfo field = createFieldInfo(iVisited);
		field.name = iVisited.getName();
		requestor.enterField(field);
		exitField(iVisited);
		return super.visitClassVarDeclNode(iVisited);
	}
	
	@Override
	public Instruction visitClassVarNode(ClassVarNode iVisited) {
		requestor.acceptFieldReference(iVisited.getName(), iVisited.getPosition().getStartOffset());
		return super.visitClassVarNode(iVisited);
	}
	
	public Instruction visitLocalAsgnNode(LocalAsgnNode iVisited) {	
		FieldInfo field = createFieldInfo(iVisited);
		field.name = iVisited.getName();
		requestor.enterField(field);
		exitField(iVisited);
		return super.visitLocalAsgnNode(iVisited);
	}
	
	@Override
	public Instruction visitInstAsgnNode(InstAsgnNode iVisited) {
		FieldInfo field = createFieldInfo(iVisited);
		field.name = iVisited.getName();
		requestor.enterField(field);
		exitField(iVisited);
		return super.visitInstAsgnNode(iVisited);
	}
	
	@Override
	public Instruction visitInstVarNode(InstVarNode iVisited) {
		requestor.acceptFieldReference(iVisited.getName(), iVisited.getPosition().getStartOffset());
		return super.visitInstVarNode(iVisited);
	}
	
	@Override
	public Instruction visitGlobalAsgnNode(GlobalAsgnNode iVisited) {
		FieldInfo field = createFieldInfo(iVisited);
		field.name = iVisited.getName();
		requestor.enterField(field);
		exitField(iVisited);
		return super.visitGlobalAsgnNode(iVisited);
	}
	
	@Override
	public Instruction visitGlobalVarNode(GlobalVarNode iVisited) {
		requestor.acceptFieldReference(iVisited.getName(), iVisited.getPosition().getStartOffset());
		return super.visitGlobalVarNode(iVisited);
	}

	private void exitField(AssignableNode iVisited) {
		requestor.exitField(iVisited.getPosition().getEndOffset() - 1);
	}

	private FieldInfo createFieldInfo(AssignableNode iVisited) {
		FieldInfo field = new FieldInfo();	
		field.declarationStart = iVisited.getPosition().getStartOffset();
		field.nameSourceStart = iVisited.getPosition().getStartOffset();
		String name = ASTUtil.getNameReflectively(iVisited);
		field.nameSourceEnd = iVisited.getPosition().getStartOffset() + name.length() - 1;
		return field;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jruby.ast.visitor.NodeVisitor#visitIterNode(org.jruby.ast.IterNode)
	 */
	public Instruction visitIterNode(IterNode iVisited) {
//		RubyBlock block = new RubyBlock(modelStack.peek()); FIXME Add method to notify of blocks?
		return super.visitIterNode(iVisited);
	}
	

	@Override
	public Instruction visitDAsgnNode(DAsgnNode iVisited) {
		FieldInfo field = createFieldInfo(iVisited);
		field.name = iVisited.getName();
		field.isDynamic = true;
		requestor.enterField(field);
		exitField(iVisited);
		return super.visitDAsgnNode(iVisited);
	}
	
	@Override
	public Instruction visitSClassNode(SClassNode iVisited) {
		Node receiver = iVisited.getReceiverNode();
		if (receiver instanceof SelfNode) {
			inSingletonClass = true;
		}
		pushVisibility(Visibility.PUBLIC);
		Instruction ins = super.visitSClassNode(iVisited);		
		popVisibility();
		if (receiver instanceof SelfNode) {
			inSingletonClass = false;
		}
		return ins;
	}
	
	public Instruction visitFCallNode(FCallNode iVisited) {
		String name = iVisited.getName();
		List<String> arguments = getArgumentsFromFunctionCall(iVisited);
		if (name.equals(REQUIRE) || name.equals(LOAD)) {
			addImport(iVisited);
		} else if (name.equals(INCLUDE)) { // Collect included mixins
			includeModule(iVisited); 
		} 
		if (name.equals(PUBLIC)) {
			for (String methodName : arguments) {
				requestor.acceptMethodVisibilityChange(methodName, convertVisibility(Visibility.PUBLIC));
			}			
		} else if (name.equals(PRIVATE)) {
			for (String methodName : arguments) {
				requestor.acceptMethodVisibilityChange(methodName, convertVisibility(Visibility.PRIVATE));
			}
		} else if (name.equals(PROTECTED)) {
			for (String methodName : arguments) {
				requestor.acceptMethodVisibilityChange(methodName, convertVisibility(Visibility.PROTECTED));
			}
		} else if (name.equals(MODULE_FUNCTION)) {			
			for (String methodName : arguments) {
				requestor.acceptModuleFunction(methodName);
			}
		} 
		if (name.equals("alias_method")) {
			String newName = arguments.get(0).substring(1);	
			int nameStart = iVisited.getPosition().getStartOffset() + "alias_method :".length();
			addAliasMethod(newName, iVisited.getPosition().getStartOffset(), iVisited.getPosition().getEndOffset(), nameStart);
		}
		if (name.equals("attr")) {
			List<Node> nodes = ASTUtil.getArgumentNodesFromFunctionCall(iVisited);
			generateReadMethod(arguments.get(0), nodes.get(0));
			// make writable?
			if (arguments.size() == 2 && arguments.get(1).equals("true")) {
				Node node = nodes.get(0);
				int start = node.getPosition().getEndOffset() + 2;
				generateWriteMethod(arguments.get(0), start, start + arguments.get(1).length() - 1);
			}
		}
		
		if (name.equals("attr_reader") || name.equals("attr_accessor")) {	
			List<Node> nodes = ASTUtil.getArgumentNodesFromFunctionCall(iVisited);
			for (int i = 0; i < arguments.size(); i++) {
				generateReadMethod(arguments.get(i), nodes.get(i));
			}			
		} 
		if (name.equals("attr_writer") || name.equals("attr_accessor")) {	
			List<Node> nodes = ASTUtil.getArgumentNodesFromFunctionCall(iVisited);
			for (int i = 0; i < arguments.size(); i++) {
				generateWriteMethod(arguments.get(i), nodes.get(i));
			}			
		}
		if (name.equals("attr") || name.equals("attr_accessor") || name.equals("attr_reader") || name.equals("attr_writer")) {
			List<Node> nodes = ASTUtil.getArgumentNodesFromFunctionCall(iVisited);
			for (int i = 0; i < arguments.size(); i++) {
				FieldInfo field = new FieldInfo();
				Node node = nodes.get(i);
				field.declarationStart = node.getPosition().getStartOffset() + 1;
				field.name = "@" + arguments.get(i);
				field.nameSourceStart = node.getPosition().getStartOffset() + 1;
				field.nameSourceEnd = node.getPosition().getEndOffset() - 1;
				requestor.enterField(field);
				requestor.exitField(node.getPosition().getEndOffset() - 1);
			}
		}
		
		requestor.acceptMethodReference(name, arguments.size(), iVisited.getPosition().getStartOffset());
		return super.visitFCallNode(iVisited);
	}

	private void addAliasMethod(String name, int start, int end, int nameStart) {
		MethodInfo method = new MethodInfo();
		// TODO Use the visibility for the original method that this is aliasing?
		Visibility visibility = getCurrentVisibility();
		if (name.equals(CONSTRUCTOR_NAME)) {
			visibility = Visibility.PROTECTED;
			method.isConstructor = true;
		} else {
			method.isConstructor = false;
		}
		method.declarationStart = start;
		method.isClassLevel = inSingletonClass;
		method.name = name;
		method.visibility = convertVisibility(visibility);		
		method.nameSourceStart = nameStart;
		method.nameSourceEnd = nameStart + name.length() - 1;
		method.parameterNames = new String[0]; // TODO Find the existing method and steal it's parameter names	
		requestor.enterMethod(method);
		requestor.exitMethod(end);	
	}
	
	private void generateWriteMethod(String argument, Node node) {
		generateWriteMethod(argument, node.getPosition().getStartOffset(), node.getPosition().getEndOffset() - 1);
	}
	
	private void generateWriteMethod(String argument, int start, int end) {
		if (argument.startsWith(":")) {
			argument = argument.substring(1);
		}
		MethodInfo info = new MethodInfo();
		info.declarationStart = start;
		info.isClassLevel = false;
		info.isConstructor = false;
		info.name = argument + "=";
		info.nameSourceStart = start;
		info.nameSourceEnd = end;
		info.visibility = IMethod.PUBLIC;
		info.parameterNames = new String[] {"new_value"};
		requestor.enterMethod(info);
		requestor.exitMethod(end);		
	}

	private void generateReadMethod(String argument, Node node) {
		if (argument.startsWith(":")) {
			argument = argument.substring(1);
		}
		MethodInfo info = new MethodInfo();
		info.declarationStart = node.getPosition().getStartOffset();
		info.isClassLevel = false;
		info.isConstructor = false;
		info.name = argument;
		info.nameSourceStart = node.getPosition().getStartOffset();
		info.nameSourceEnd = node.getPosition().getEndOffset() - 1;
		info.visibility = IMethod.PUBLIC;
		info.parameterNames = new String[0];
		requestor.enterMethod(info);
		requestor.exitMethod(node.getPosition().getEndOffset() - 1);
	}
	
	private void addImport(FCallNode iVisited) {
		ArrayNode node = (ArrayNode) iVisited.getArgsNode();
		String arg = getString(node);
		if (arg != null) {
			requestor.acceptImport(arg, iVisited.getPosition().getStartOffset(), iVisited.getPosition().getEndOffset());
		}
	}
	
	/**
	 * @param node
	 * @return
	 */
	private String getString(ArrayNode node) {
		Object tmp = node.childNodes().iterator().next();
		if (tmp instanceof DStrNode) {
			DStrNode dstrNode = (DStrNode) tmp;
			tmp = dstrNode.childNodes().iterator().next();
		}
		if (tmp instanceof StrNode) {
			StrNode strNode = (StrNode) tmp;
			return strNode.getValue().toString();
		}
		return null;
	}

	private void includeModule(FCallNode iVisited) {
		List<String> mixins = new LinkedList<String>();
		Node argsNode = iVisited.getArgsNode();
		Iterator iter = null;
		if (argsNode instanceof SplatNode) {
			SplatNode splat = (SplatNode) argsNode;
			iter = splat.childNodes().iterator();
		} else if (argsNode instanceof ArrayNode) {
			ArrayNode arrayNode = (ArrayNode) iVisited.getArgsNode();
			iter = arrayNode.childNodes().iterator();
		}
		for (; iter.hasNext();) {
			Node mixinNameNode = (Node) iter.next();
			if (mixinNameNode instanceof StrNode) {
				mixins.add(((StrNode) mixinNameNode).getValue().toString());
			}
			if (mixinNameNode instanceof DStrNode) {
				Node next = (Node) ((DStrNode) mixinNameNode).childNodes().iterator().next();
				if (next instanceof StrNode) {
					mixins.add(((StrNode) next).getValue().toString());
				}
			}
			if (mixinNameNode instanceof ConstNode) {
				mixins.add(((ConstNode) mixinNameNode).getName());
			}
			if (mixinNameNode instanceof Colon2Node) {
				mixins.add(ASTUtil.getFullyQualifiedName((Colon2Node) mixinNameNode));
			}
		}
		for (String string : mixins) {
			requestor.acceptMixin(string);
		}
	}
	
	public Instruction visitVCallNode(VCallNode iVisited) {
		String functionName = iVisited.getName();
		if (functionName.equals(PUBLIC)) {
			setVisibility(Visibility.PUBLIC);
		} else if (functionName.equals(PRIVATE)) {
			setVisibility(Visibility.PRIVATE);
		} else if (functionName.equals(PROTECTED)) {
			setVisibility(Visibility.PROTECTED);
		} else if (functionName.equals(MODULE_FUNCTION)) {
			inModuleFunction = true;
		}
		requestor.acceptMethodReference(functionName, 0, iVisited.getPosition().getStartOffset());
		return super.visitVCallNode(iVisited);
	}
	
	private void setVisibility(Visibility visibility) {
		popVisibility();
		pushVisibility(visibility);
	}
	
	private void pushVisibility(Visibility visibility) {
		visibilities.add(visibility);		
	}

	@Override
	public Instruction visitCallNode(CallNode iVisited) {
		String name = iVisited.getName();
		List<String> arguments = getArgumentsFromFunctionCall(iVisited);
		if (name.equals(PUBLIC)) {
			for (String methodName : arguments) {
				requestor.acceptMethodVisibilityChange(methodName, convertVisibility(Visibility.PUBLIC));
			}			
		} else if (name.equals(PRIVATE)) {
			for (String methodName : arguments) {
				requestor.acceptMethodVisibilityChange(methodName, convertVisibility(Visibility.PRIVATE));
			}
		} else if (name.equals(PROTECTED)) {
			for (String methodName : arguments) {
				requestor.acceptMethodVisibilityChange(methodName, convertVisibility(Visibility.PROTECTED));
			}
		} else if (name.equals(MODULE_FUNCTION)) {
			for (String methodName : arguments) {
				requestor.acceptModuleFunction(methodName);
			}
		} else if (name.equals("class_eval")) {
			Node receiver = iVisited.getReceiverNode();
			if (receiver instanceof ConstNode || receiver instanceof Colon2Node) {			
			String receiverName = null;
				if (receiver instanceof Colon2Node) {
					receiverName = ASTUtil
							.getFullyQualifiedName((Colon2Node) receiver);
				} else {
					receiverName = ASTUtil.getNameReflectively(receiver);
				}
				requestor.acceptMethodReference(name, arguments.size(),
						iVisited.getPosition().getStartOffset());

				pushVisibility(Visibility.PUBLIC);

				TypeInfo typeInfo = new TypeInfo();
				typeInfo.name = receiverName;
				typeInfo.declarationStart = iVisited.getPosition()
						.getStartOffset();
				typeInfo.nameSourceStart = receiver.getPosition()
						.getStartOffset();
				typeInfo.nameSourceEnd = receiver.getPosition().getEndOffset() - 1;
				typeInfo.isModule = false;
				typeInfo.modules = new String[0];
				typeInfo.secondary = false;
				requestor.enterType(typeInfo);

				Instruction ins = super.visitCallNode(iVisited);
				popVisibility();
				requestor.exitType(iVisited.getPosition().getEndOffset() - 2);

				return ins;		
			}
		}
		requestor.acceptMethodReference(name, arguments.size(), iVisited.getPosition().getStartOffset());
		return super.visitCallNode(iVisited);
	}

	public Instruction visitAliasNode(AliasNode iVisited) {
		String name = iVisited.getNewName();		
		int nameStart = iVisited.getPosition().getStartOffset() + ALIAS.length() - 1;
		addAliasMethod(name, iVisited.getPosition().getStartOffset(), iVisited.getPosition().getEndOffset(), nameStart);		
		return super.visitAliasNode(iVisited);
	}

	private Visibility getCurrentVisibility() {
		return visibilities.get(visibilities.size() - 1);
	}

	public void parse(char[] source, char[] name) {
		RubyParser p = new RubyParser();
		this.source = source;
		if (name == null) name = new char[0];
		Node ast = p.parse(new String(name), new String(source)).getAST();
		acceptNode(ast);		
	}
	
	@Override
	public Instruction visitYieldNode(YieldNode iVisited) {
		Node argsNode = iVisited.getArgsNode();
		if (argsNode instanceof LocalVarNode) {
			requestor.acceptYield(((LocalVarNode) argsNode).getName());
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
			requestor.acceptYield(name);
		}
		return super.visitYieldNode(iVisited);
	}
}