package org.rubypeople.rdt.internal.corext.template.ruby;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.SimpleTemplateVariableResolver;
import org.eclipse.jface.text.templates.TemplateContext;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

public class RubyContextType extends RubyScriptContextType {

    public static final String NAME = "ruby"; //$NON-NLS-1$

    /**
     * Creates a ruby context type.
     */
    public RubyContextType() {
        super(NAME);

        // global variables
        addResolver(new GlobalTemplateVariables.Cursor());
        addResolver(new GlobalTemplateVariables.WordSelection());
        addResolver(new GlobalTemplateVariables.LineSelection());
        addResolver(new GlobalTemplateVariables.Dollar());
        addResolver(new GlobalTemplateVariables.Date());
        addResolver(new GlobalTemplateVariables.Year());
        addResolver(new GlobalTemplateVariables.Time());
        addResolver(new GlobalTemplateVariables.User());
        // ruby specific template variables
        addResolver(new File());
        addResolver(new Path());
        addResolver(new Class());
        addResolver(new ClassFullyQualifiedName());        
        addResolver(new Method());
        addResolver(new MethodFullyQualifiedName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rubypeople.rdt.internal.corext.template.ruby.RubyFileContextType#createContext(org.eclipse.jface.text.IDocument,
     *      int, int, org.rubypeople.rdt.core.IRubyScript)
     */
    public RubyScriptContext createContext(IDocument document, int offset, int length,
            IRubyScript script) {
        return new RubyContext(this, document, offset, length, script);
    }

    /**
	 * The file variable evaluates to the current filename.
	 */
	public static class File extends SimpleTemplateVariableResolver {
		/**
		 * Creates a new file name variable
		 */
		public File() {
			super("file", "Expands to the current filename, such as foo.rb"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		/**
		 * {@inheritDoc}
		 */
		protected String resolve(TemplateContext context) {
			if (context instanceof RubyScriptContext) {
				RubyScriptContext rsContext = (RubyScriptContext) context;
				IRubyScript script = rsContext.getRubyScript();
				return script.getElementName();
			}
			return "";
		}
	}
	
	 /**
	 * The path variable evaluates to the current filename path.
	 */
	public static class Path extends SimpleTemplateVariableResolver {
		/**
		 * Creates a new path name variable
		 */
		public Path() {
			super("path", "Expands to the name of the current full file path, such as /foo/bar.rb"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		/**
		 * {@inheritDoc}
		 */
		protected String resolve(TemplateContext context) {
			if (context instanceof RubyScriptContext) {
				RubyScriptContext rsContext = (RubyScriptContext) context;
				IRubyScript script = rsContext.getRubyScript();
				IPath path = script.getPath();
				if (path.segmentCount() > 0 && path.segment(0).equals(script.getRubyProject().getElementName())) {
					path = path.removeFirstSegments(1);
				}
				return path.toPortableString();
			}
			return "";
		}
	}
    
    // TODO Refactor all the common code shared by these variable resolvers (tehre's quite a bit here)
	/**
	 * The class variable evaluates to the current surrounding type name.
	 */
	public static class Class extends SimpleTemplateVariableResolver {
		/**
		 * Creates a new class name variable
		 */
		public Class() {
			super("class", "Expands to the name of the class surrounding the template expansion location, such as Foo"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		/**
		 * {@inheritDoc}
		 */
		protected String resolve(TemplateContext context) {
			try {
				if (context instanceof RubyScriptContext) {
					RubyScriptContext rsContext = (RubyScriptContext) context;
					IRubyScript script = rsContext.getRubyScript();
					IRubyElement element = script.getElementAt(rsContext.getStart());
					if (element == null) return "";
					IType type = null;
					if (element.isType(IRubyElement.TYPE)) {
						type = (IType) element;
					} else {
						type = (IType) element.getAncestor(IRubyElement.TYPE);
					}
					if (type != null) return type.getElementName();
				}
			} catch (RubyModelException e) {
				RubyPlugin.log(e);
			}
			return "";
		}
	}
	
	/**
	 * The classfqn variable evaluates to the current surrounding type's fully qualified name.
	 */
	public static class ClassFullyQualifiedName extends SimpleTemplateVariableResolver {
		/**
		 * Creates a new classfqn name variable
		 */
		public ClassFullyQualifiedName() {
			super("classfqn", "Expands to the fully qualified name of the class surrounding the template expansion location, such as Foo::Bar"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		/**
		 * {@inheritDoc}
		 */
		protected String resolve(TemplateContext context) {
			try {
				if (context instanceof RubyScriptContext) {
					RubyScriptContext rsContext = (RubyScriptContext) context;
					IRubyScript script = rsContext.getRubyScript();
					IRubyElement element = script.getElementAt(rsContext.getStart());
					if (element == null) return "";
					IType type = null;
					if (element.isType(IRubyElement.TYPE)) {
						type = (IType) element;
					} else {
						type = (IType) element.getAncestor(IRubyElement.TYPE);
					}
					if (type != null) return type.getFullyQualifiedName();
				}
			} catch (RubyModelException e) {
				RubyPlugin.log(e);
			}
			return "";
		}
	}
	
	/**
	 * The method variable evaluates to the current surrounding method name.
	 */
	public static class Method extends SimpleTemplateVariableResolver {
		/**
		 * Creates a new method name variable
		 */
		public Method() {
			super("method", "Expands to the name of the method surrounding the template expansion location, such as foo"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		/**
		 * {@inheritDoc}
		 */
		protected String resolve(TemplateContext context) {
			try {
				if (context instanceof RubyScriptContext) {
					RubyScriptContext rsContext = (RubyScriptContext) context;
					IRubyScript script = rsContext.getRubyScript();
					IRubyElement element = script.getElementAt(rsContext.getStart());
					if (element == null) return "";
					IMethod method = null;
					if (element.isType(IRubyElement.METHOD)) {
						method = (IMethod) element;
					} else {
						method = (IMethod) element.getAncestor(IRubyElement.METHOD);
					}
					if (method != null) return method.getElementName();
				}
			} catch (RubyModelException e) {
				RubyPlugin.log(e);
			}
			return "";
		}
	}
	
	/**
	 * The methodfqn variable evaluates to the current surrounding method's fully qualified name.
	 */
	public static class MethodFullyQualifiedName extends SimpleTemplateVariableResolver {
		/**
		 * Creates a new methodfqn name variable
		 */
		public MethodFullyQualifiedName() {
			super("methodfqn", "Expands to the fully qualified name of the method surrounding the template expansion location, such as Foo::Bar#foo"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		/**
		 * {@inheritDoc}
		 */
		protected String resolve(TemplateContext context) {
			try {
				if (context instanceof RubyScriptContext) {
					RubyScriptContext rsContext = (RubyScriptContext) context;
					IRubyScript script = rsContext.getRubyScript();
					IRubyElement element = script.getElementAt(rsContext.getStart());
					if (element == null) return "";
					IMethod method = null;
					if (element.isType(IRubyElement.METHOD)) {
						method = (IMethod) element;
					} else {
						method = (IMethod) element.getAncestor(IRubyElement.METHOD);
					}
					if (method != null) {
						IType type = method.getDeclaringType();
						String name = "";
						if (type != null) {
							name += type.getFullyQualifiedName();
						}
						if (method.isSingleton()) {
							name += "::";
						} else {
							name += "#";
						}
						name += method.getElementName();
						return name;
					}
				}
			} catch (RubyModelException e) {
				RubyPlugin.log(e);
			}
			return "";
		}
	}
}
