package org.rubypeople.rdt.internal.core.search.indexing;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.search.SearchDocument;
import org.rubypeople.rdt.internal.core.RubyModelManager;
import org.rubypeople.rdt.internal.core.SourceElementParser;
import org.rubypeople.rdt.internal.core.search.matching.ConstructorPattern;
import org.rubypeople.rdt.internal.core.search.matching.FieldPattern;
import org.rubypeople.rdt.internal.core.search.matching.MethodPattern;
import org.rubypeople.rdt.internal.core.search.matching.SuperTypeReferencePattern;
import org.rubypeople.rdt.internal.core.search.matching.TypeDeclarationPattern;
import org.rubypeople.rdt.internal.core.search.processing.JobManager;
import org.rubypeople.rdt.internal.core.util.CharOperation;

public class SourceIndexer implements IIndexConstants {

	private SearchDocument document;

	public SourceIndexer(SearchDocument document) {
		this.document = document;
	}

	public void indexDocument() {
		// Create a new Parser
		SourceIndexerRequestor requestor = new SourceIndexerRequestor(this);
		String documentPath = this.document.getPath();
		SourceElementParser parser = ((InternalSearchDocument) this.document).parser;
		if (parser == null) {
			IPath path = new Path(documentPath);
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(path.segment(0));
			parser = RubyModelManager.getRubyModelManager().getIndexManager().getSourceElementParser(RubyCore.create(project), requestor);
		} else {
			parser.requestor = requestor;
		}

		// Launch the parser
		char[] source = null;
		char[] name = null;
		try {
			source = document.getCharContents();
			name = documentPath.toCharArray();
		} catch (Exception e) {
			// ignore
		}
		if (source == null || name == null)
			return; // could not retrieve document info (e.g. resource was
		// discarded)
		try {
			parser.parse(source, name);
		} catch (Exception e) {
			if (JobManager.VERBOSE) {
				e.printStackTrace();
			}
		}
	}

	public void addClassDeclaration(int modifiers, char[] packageName, char[] name, char[][] enclosingTypeNames, char[] superclass, char[][] superinterfaces, boolean secondary) {
		char[] indexKey = TypeDeclarationPattern.createIndexKey(modifiers, name, packageName, enclosingTypeNames, secondary);
		addIndexEntry(TYPE_DECL, indexKey);

		if (superclass != null && !superclass.equals("Object")) {
			addTypeReference(superclass);
		}
		
		addIndexEntry(SUPER_REF, SuperTypeReferencePattern.createIndexKey(modifiers, packageName, name, enclosingTypeNames, CLASS_SUFFIX, superclass, CLASS_SUFFIX));
		if (superinterfaces != null) {
			for (int i = 0, max = superinterfaces.length; i < max; i++) {
				char[] superinterface = superinterfaces[i];
				addTypeReference(superinterface);
				addIncludedModuleReference(modifiers, packageName, name, enclosingTypeNames, superinterface);
			}
		}
	}

	public void addIncludedModuleReference(int modifiers, char[] packageName, char[] name, char[][] enclosingTypeNames, char[] superinterface) {
		addIndexEntry(SUPER_REF, SuperTypeReferencePattern.createIndexKey(modifiers, packageName, name, enclosingTypeNames, CLASS_SUFFIX, superinterface, MODULE_SUFFIX));
	}
	
	public void addFieldDeclaration(char[] typeName, char[] fieldName) {
		addIndexEntry(FIELD_DECL, FieldPattern.createIndexKey(fieldName));
		if (typeName != null) addTypeReference(typeName);
	}
	public void addFieldReference(char[] fieldName) {
		addNameReference(fieldName);
	}

	public void addMethodDeclaration(char[] methodName, int arity) {
		addIndexEntry(METHOD_DECL, MethodPattern.createIndexKey(methodName, arity));
	}

	public void addMethodReference(char[] methodName, int argCount) {
		addIndexEntry(METHOD_REF, MethodPattern.createIndexKey(methodName, argCount));
	}

	public void addNameReference(char[] name) {
		addIndexEntry(REF, name);
	}

	public void addTypeReference(char[] typeName) {
		addNameReference(CharOperation.lastSegment(typeName, "::"));
	}

	protected void addIndexEntry(char[] category, char[] key) {
		this.document.addIndexEntry(category, key);
	}

	public void addConstructorDeclaration(char[] typeName, int argCount) {
		addIndexEntry(CONSTRUCTOR_DECL, ConstructorPattern.createIndexKey(CharOperation.lastSegment(typeName, "::"), argCount));
	}

	public void addConstructorReference(char[] typeName, int argCount) {
		char[] simpleTypeName = CharOperation.lastSegment(typeName, "::");
		addTypeReference(simpleTypeName);
		addIndexEntry(CONSTRUCTOR_REF, ConstructorPattern.createIndexKey(simpleTypeName, argCount));
	}

}
