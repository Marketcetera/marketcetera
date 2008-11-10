package org.rubypeople.rdt.internal.launching;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;

public class RubyLaunchableTester extends PropertyTester {
	
	/**
	 * name for the "has method" property
	 */
	private static final String PROPERTY_HAS_METHOD = "hasMethod"; //$NON-NLS-1$	
	
	/**
	 * name for the "extends class" property
	 */
	private static final String PROPERTY_EXTENDS_CLASS = "extendsClass"; //$NON-NLS-1$
	
	/**
	 * "is container" property
	 */
	private static final String PROPERTY_IS_CONTAINER = "isContainer"; //$NON-NLS-1$
	
	/**
	 * name for the PROPERTY_PROJECT_NATURE property
	 */
	private static final String PROPERTY_PROJECT_NATURE = "hasProjectNature"; //$NON-NLS-1$

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (PROPERTY_IS_CONTAINER.equals(property)) {
			if (receiver instanceof IAdaptable) {
				IResource resource = (IResource)((IAdaptable)receiver).getAdapter(IResource.class);
				if (resource != null) {
					return resource instanceof IContainer;
				}
			}
			return false;
		}
		if(PROPERTY_PROJECT_NATURE.equals(property)) {
			if (receiver instanceof IAdaptable) {
				IResource resource = (IResource)((IAdaptable)receiver).getAdapter(IResource.class);
				if (resource != null) {
					return hasProjectNature(resource, (String)args[0]);
				}
			}
		}
		IRubyElement element = null;
		if (receiver instanceof IAdaptable) {
			element = (IRubyElement) ((IAdaptable)receiver).getAdapter(IRubyElement.class);
			if(element != null) {
				if(!element.exists()) {
					return false;
				}
			}
		}
		if (PROPERTY_HAS_METHOD.equals(property)) {
			return hasMethod(element, args);
		}
		if(PROPERTY_EXTENDS_CLASS.equals(property)) {
			return hasSuperclass(element, (String)args[0]);
		}
		if(PROPERTY_PROJECT_NATURE.equals(property)) {
			return hasProjectNature(element, (String)args[0]);
		}
		return false;
	}
	
	private boolean hasProjectNature(IResource resource, String ntype) {
		try {
			if(resource != null) {
			    IProject proj = resource.getProject();
				return proj.isAccessible() && proj.hasNature(ntype);
			}
		} catch (CoreException e) {}
        return false;
	}

	/**
	 * Determines is the ruby element contains a specific method.
     * <p>
     * The syntax for the property tester is of the form: methodname,
     * signature, modifiers.
	 * </p>
     * <ol>
     * <li>methodname - case sensitive method name, required. For example,
     *  <code>toString</code>.</li>
     * <li>signature - JLS style method signature, required. For example,
     *  <code>(QString;)V</code>.</li>
     * <li>modifiers - optional space seperated list of modifiers, for
     *  example, <code>public static</code>.</li>
     * </ol>
	 * @param element the element to check for the method 
	 * @param args first arg is method name, secondary args are parameter types signatures
	 * @return true if the method is found in the element, false otherwise
	 */
	private boolean hasMethod(IRubyElement element, Object[] args) {
		try {
			if (args.length > 1) {
	            IType type = getType(element);
				if (type != null && type.exists()) {
					String name = (String) args[0];
					String signature = (String) args[1];
                    String[] parms = signature.split(",");
					IMethod candidate = type.getMethod(name, parms);
					if (candidate.exists()) {
                        // check return type                        
                            // check modifiers
                            if (args.length > 2) {
                                String modifierText = (String) args[2];
                                String[] modifiers = modifierText.split(" "); //$NON-NLS-1$
                                for (int j = 0; j < modifiers.length; j++) {
                                    String modifier = modifiers[j];
                                    if (modifier.equals("public") && !candidate.isPublic())
                                    	return false;
                                    else if (modifier.equals("private") && !candidate.isPrivate())
                                    	return false;
                                    else if (modifier.equals("protected") && !candidate.isProtected())
                                    	return false;
                                    else if (modifier.equals("singleton") && !candidate.isSingleton())
                                    	return false;
                                }
                                return true;                                                   
                        }
					}
				}
			}
		}
		catch (RubyModelException e) {}
		return false;
	}
	
	/**
	 * Determines if the element has qname as a parent class
	 * @param element the element to check for the parent class definition
	 * @param qname the fully qualified name of the (potential) parent class
	 * @return true if qname is a parent class, false otherwise
	 */
	private boolean hasSuperclass(IRubyElement element, String qname) {
		try {
			IType type = getType(element);
			if(type != null) {
				IType[] stypes = type.newSupertypeHierarchy(new NullProgressMonitor()).getAllSuperclasses(type);
				for(int i = 0; i < stypes.length; i++) {
					if(stypes[i].getFullyQualifiedName().equals(qname) || stypes[i].getElementName().equals(qname)) {
						return true;
					}
				}
			} 
		}
		catch(RubyModelException e) {}
		return false; 
	}
	
	/**
     * determines if the project selected has the specified nature
     * @param resource the resource to get the project for
     * @param ntype the specified nature type
     * @return true if the specified nature matches the project, false otherwise
     */
    private boolean hasProjectNature(IRubyElement element, String ntype) {
    	if(element != null) {
    		IResource resource = element.getResource();
    		if (resource == null) {
    			resource = element.getRubyProject().getProject();
    		}
    		if(resource != null) {
    			return hasProjectNature(resource, ntype);
	    	}    		
    	}
	    return false;
    }
    
    /**
	 * gets the type of the IRubyElement
	 * @param element the element to inspect
	 * @return the type
	 * @throws RubyModelException
	 */
	private IType getType(IRubyElement element) throws RubyModelException {
        IType type = null;
        if (element instanceof IRubyScript) {
            type= ((IRubyScript) element).findPrimaryType();
        }
        else if (element instanceof IType) {
            type = (IType) element;
        }
        else if (element instanceof IMember) {
            type = ((IMember)element).getDeclaringType();
        }
        return type;
    }

}
