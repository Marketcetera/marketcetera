package org.rubypeople.rdt.internal.codeassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IOpenable;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.compiler.CategorizedProblem;
import org.rubypeople.rdt.internal.compiler.ISourceElementRequestor;
import org.rubypeople.rdt.internal.compiler.ISourceElementRequestor.MethodInfo;

public class ASTSourceRequestor implements ISourceElementRequestor {
	
	private Map<TypeInfo, List<MethodInfo>> types = new HashMap<TypeInfo, List<MethodInfo>>();
	private Map<TypeInfo, List<String>> mixins = new HashMap<TypeInfo, List<String>>();
	private List<TypeInfo> typeStack = new ArrayList<TypeInfo>();
	private Map<String, TypeInfo> fullTypeNames = new HashMap<String, TypeInfo>();
	private TypeInfo topLevel;
	private MethodInfo latestMethod;
	
	public ASTSourceRequestor() {
		topLevel = new TypeInfo();
		topLevel.declarationStart = 0;
		topLevel.isModule = false;
		topLevel.name = "Object";
		topLevel.nameSourceStart = 0;
		topLevel.nameSourceEnd = 0;
		topLevel.secondary = false;
		topLevel.superclass = null;
	}

	public void acceptConstructorReference(String name, int argCount, int offset) {
		// TODO Auto-generated method stub

	}

	public void acceptFieldReference(String name, int offset) {
		// TODO Auto-generated method stub

	}

	public void acceptImport(String value, int startOffset, int endOffset) {
		// TODO Auto-generated method stub

	}

	public void acceptMethodReference(String name, int argCount, int offset) {
		// TODO Auto-generated method stub

	}

	public void acceptMethodVisibilityChange(String methodName, int visibility) {
		// TODO Auto-generated method stub

	}

	public void acceptMixin(String string) {
		TypeInfo info = getCurrentType();
		List<String> duh = mixins.get(info);
		if (duh == null) {
			duh = new ArrayList<String>();
		}
		duh.add(string);
		mixins.put(info, duh);
	}

	public void acceptModuleFunction(String function) {
		// TODO Auto-generated method stub

	}

	public void acceptProblem(CategorizedProblem problem) {
		// TODO Auto-generated method stub

	}

	public void acceptTypeReference(String name, int startOffset, int endOffset) {
		// TODO Auto-generated method stub

	}

	public void acceptUnknownReference(String name, int startOffset,
			int endOffset) {
		// TODO Auto-generated method stub

	}

	public void enterConstructor(MethodInfo constructor) {
		// TODO Auto-generated method stub

	}

	public void enterField(FieldInfo field) {
		// TODO Auto-generated method stub

	}

	public void enterMethod(MethodInfo method) {
		TypeInfo info = getCurrentType();
		List<MethodInfo> methods = types.get(info);
		if (methods == null) methods = new ArrayList<MethodInfo>();
		methods.add(method);
		types.put(info, methods);
		latestMethod = method;
	}
	
	public void acceptYield(String name) {
		latestMethod.blockVars = new String[] { name };		
	}

	private TypeInfo getCurrentType() {
		if (typeStack.isEmpty()) return topLevel;
		TypeInfo info = typeStack.get(typeStack.size() - 1);
		return info;
	}

	public void enterScript() {
		// TODO Auto-generated method stub

	}

	public void enterType(TypeInfo type) {
		types.put(type, new ArrayList<MethodInfo>());
		typeStack.add(type);
		mixins.put(type, new ArrayList<String>());
		String fullName = getNamespace() + type.name;
		fullTypeNames.put(fullName, type);
	}

	private String getNamespace() {
		StringBuffer buffer = new StringBuffer();
		List<TypeInfo> newTypeStack = new ArrayList<TypeInfo>(typeStack);
		newTypeStack.remove(newTypeStack.size() - 1);
		for (TypeInfo type : newTypeStack) {
			buffer.append(type.name);
			buffer.append("::");
		}
		return buffer.toString();
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
		// TODO Auto-generated method stub

	}

	public void exitType(int endOffset) {
		typeStack.remove(typeStack.size() - 1);
	}

	public List<String> getMixins(String name) {
		for (String fullName : fullTypeNames.keySet()) {
			if (fullName.equals(name)) {
				TypeInfo info = fullTypeNames.get(fullName);
				return mixins.get(info);
			}
		}
		return Collections.emptyList();
	}

	public Map<IMethod, String> getMethods(String mixin) {
		Map<IMethod, String> duh = new HashMap<IMethod, String>();
		for (String fullName : fullTypeNames.keySet()) {
			if (fullName.equals(mixin)) {
				TypeInfo info = fullTypeNames.get(fullName);
				List<MethodInfo> methods = types.get(info);
				for (MethodInfo methodInfo : methods) {
					duh.put(new MethodInfoMethod(methodInfo), mixin);
				}
				return duh;
			}
		}
		return duh;		
	}
	
	private static class MethodInfoMethod implements IMethod {
		
		private MethodInfo info;

		public MethodInfoMethod(MethodInfo info) {
			this.info = info;
		}

		public int getNumberOfParameters() throws RubyModelException {
			return info.parameterNames.length;
		}

		public String[] getParameterNames() throws RubyModelException {
			return info.parameterNames;
		}

		public int getVisibility() throws RubyModelException {
			return info.visibility;
		}

		public boolean isConstructor() {
			return info.isConstructor;
		}

		public boolean isPrivate() throws RubyModelException {
			return info.visibility == IMethod.PRIVATE;
		}

		public boolean isProtected() throws RubyModelException {
			return info.visibility == IMethod.PROTECTED;
		}

		public boolean isPublic() throws RubyModelException {
			return info.visibility == IMethod.PUBLIC;
		}

		public boolean isSingleton() {
			return info.isClassLevel;
		}

		public boolean exists() {
			return false;
		}

		public IRubyElement getAncestor(int ancestorType) {
			// TODO Auto-generated method stub
			return null;
		}

		public IResource getCorrespondingResource() throws RubyModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public String getElementName() {
			return info.name;
		}

		public int getElementType() {
			return IRubyElement.METHOD;
		}

		public String getHandleIdentifier() {
			// TODO Auto-generated method stub
			return null;
		}

		public IOpenable getOpenable() {
			// TODO Auto-generated method stub
			return null;
		}

		public IRubyElement getParent() {
			// TODO Auto-generated method stub
			return null;
		}

		public IPath getPath() {
			// TODO Auto-generated method stub
			return null;
		}

		public IRubyElement getPrimaryElement() {
			// TODO Auto-generated method stub
			return null;
		}

		public IResource getResource() {
			// TODO Auto-generated method stub
			return null;
		}

		public IRubyModel getRubyModel() {
			// TODO Auto-generated method stub
			return null;
		}

		public IRubyProject getRubyProject() {
			// TODO Auto-generated method stub
			return null;
		}

		public IResource getUnderlyingResource() throws RubyModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isReadOnly() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isStructureKnown() throws RubyModelException {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isType(int type) {
			return type == IRubyElement.METHOD;
		}

		public Object getAdapter(Class adapter) {
			// TODO Auto-generated method stub
			return null;
		}

		public IType getDeclaringType() {
			// TODO Auto-generated method stub
			return null;
		}

		public ISourceRange getNameRange() throws RubyModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public IRubyScript getRubyScript() {
			// TODO Auto-generated method stub
			return null;
		}

		public IType getType(String name, int occurrenceCount) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getSource() throws RubyModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public ISourceRange getSourceRange() throws RubyModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public IRubyElement[] getChildren() throws RubyModelException {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean hasChildren() throws RubyModelException {
			// TODO Auto-generated method stub
			return false;
		}

		public String[] getBlockParameters() throws RubyModelException {
			return info.blockVars;
		}
		
	}
}
