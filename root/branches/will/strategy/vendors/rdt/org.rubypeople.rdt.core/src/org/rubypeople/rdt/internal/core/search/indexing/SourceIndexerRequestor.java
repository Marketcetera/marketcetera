package org.rubypeople.rdt.internal.core.search.indexing;

import java.util.Stack;

import org.rubypeople.rdt.core.Flags;
import org.rubypeople.rdt.core.compiler.CategorizedProblem;
import org.rubypeople.rdt.internal.compiler.ISourceElementRequestor;
import org.rubypeople.rdt.internal.core.util.Util;

public class SourceIndexerRequestor implements ISourceElementRequestor {

	private SourceIndexer indexer;
	private Stack<TypeInfo> typeStack;

	public SourceIndexerRequestor(SourceIndexer sourceIndexer) {
		this.indexer = sourceIndexer;
		typeStack = new Stack<TypeInfo>();
	}

	public void acceptConstructorReference(String name, int argCount, int offset) {
		indexer.addConstructorReference(name.toCharArray(), argCount);
	}

	public void acceptFieldReference(String name, int offset) {
		indexer.addFieldReference(name.toCharArray());
	}

	public void acceptImport(String value, int startOffset, int endOffset) {
		// TODO Auto-generated method stub

	}

	public void acceptMethodReference(String name, int argCount, int offset) {
		indexer.addMethodReference(name.toCharArray(), argCount);
	}

	public void acceptMixin(String moduleName) {
		indexer.addTypeReference(moduleName.toCharArray());
		TypeInfo info = typeStack.peek();
		char[] simpleName = getSimpleName(info.name);
		char[][] enclosingTypes = getEnclosingTypeNames(info.name);
		indexer.addIncludedModuleReference(info.isModule ? Flags.AccModule : 0, new char[0], simpleName, enclosingTypes, moduleName.toCharArray());
	}

	public void acceptProblem(CategorizedProblem problem) {
		// TODO Auto-generated method stub

	}

	public void acceptTypeReference(String name, int startOffset, int endOffset) {
		indexer.addTypeReference(name.toCharArray());
	}

	public void acceptUnknownReference(String name, int startOffset,
			int endOffset) {
		// TODO Auto-generated method stub

	}

	public void enterConstructor(MethodInfo constructor) {
		indexer.addConstructorDeclaration(constructor.name.toCharArray(), constructor.parameterNames.length);
	}

	public void enterField(FieldInfo field) {
		indexer.addFieldDeclaration(null, field.name.toCharArray());
	}

	public void enterMethod(MethodInfo method) {
		indexer.addMethodDeclaration(method.name.toCharArray(), method.parameterNames.length);
	}

	public void enterScript() {
		// TODO Auto-generated method stub

	}

	public void enterType(TypeInfo type) {		
		String[] modules = type.modules;
		char[][] mod = new char[modules.length][];
		for (int i = 0; i < modules.length; i++) {
			mod[i] = modules[i].toCharArray();
		}
		char[] packName = new char[0]; // XXX We need to know the relative path from the source folder root!
		char[] superclass = new char[0];
		if (type.superclass != null) {
			superclass = type.superclass.toCharArray();
		}
		char[]simpleName = getSimpleName(type.name);
		char[][] enclosingTypes = getEnclosingTypeNames(type.name);
		indexer.addClassDeclaration(type.isModule ? Flags.AccModule : 0, packName, simpleName, enclosingTypes, superclass, mod, type.secondary);
	    typeStack.push(type);
	}

	private char[] getSimpleName(String name) {
		return Util.getSimpleName(name).toCharArray();
	}

	private char[][] getEnclosingTypeNames(String typeName) {
		String[] parts = typeName.split("::");
		
		char[][] names = new char[typeStack.size() + parts.length - 1][];
		int i = 0;
		for (TypeInfo info : typeStack) {
			names[i++] = info.name.toCharArray();
		}
		for (int j = 0; j < parts.length - 1; j++) {
			names[i++] = parts[j].toCharArray();
		}
		return names;
	}

	public void exitConstructor(int endOffset) {
		// TODO Auto-generated method stub

	}

	public void exitField(int endOffset) {
		// TODO Auto-generated method stub

	}

	public void exitMethod(int endOffset) {
		// TODO Auto-generated method stub

	}

	public void exitScript(int endOffset) {
		typeStack.clear();
	}

	public void exitType(int endOffset) {
		typeStack.pop();
	}

	public void acceptMethodVisibilityChange(String methodName, int visibility) {
		// TODO Auto-generated method stub
		
	}

	public void acceptModuleFunction(String function) {
		// TODO Auto-generated method stub
		
	}
	
	public void acceptYield(String name) {
		// TODO Auto-generated method stub		
	}

}
