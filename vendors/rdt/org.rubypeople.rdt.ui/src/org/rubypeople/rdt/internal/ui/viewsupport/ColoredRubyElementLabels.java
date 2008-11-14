/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.viewsupport;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.rubypeople.rdt.core.IField;
import org.rubypeople.rdt.core.ILoadpathContainer;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.ILocalVariable;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.LoadpathContainerInitializer;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyUIMessages;
import org.rubypeople.rdt.internal.ui.packageview.LoadPathContainer;
import org.rubypeople.rdt.internal.ui.viewsupport.ColoredString.Style;
import org.rubypeople.rdt.launching.RubyRuntime;
import org.rubypeople.rdt.ui.RubyElementLabels;

public class ColoredRubyElementLabels {

	public static final Style QUALIFIER_STYLE= new Style(ColoredViewersManager.QUALIFIER_COLOR_NAME); 
	public static final Style COUNTER_STYLE= new Style(ColoredViewersManager.COUNTER_COLOR_NAME); 
	public static final Style DECORATIONS_STYLE= new Style(ColoredViewersManager.DECORATIONS_COLOR_NAME); 
	
	private static final Style APPENDED_TYPE_STYLE= DECORATIONS_STYLE; 
	
	public final static long COLORIZE= 1L << 55;
	
	private final static long QUALIFIER_FLAGS= RubyElementLabels.P_COMPRESSED | RubyElementLabels.USE_RESOLVED;
	

	private static final boolean getFlag(long flags, long flag) {
		return (flags & flag) != 0;
	}
	
	/**
	 * Returns the label of the given object. The object must be of type {@link IRubyElement} or adapt to {@link IWorkbenchAdapter}. The empty string is returned
	 * if the element type is not known.
	 * @param obj Object to get the label from.
	 * @param flags The rendering flags
	 * @return Returns the label or the empty string if the object type is not supported.
	 */
	public static ColoredString getTextLabel(Object obj, long flags) {
		if (obj instanceof IRubyElement) {
			return getElementLabel((IRubyElement) obj, flags);
		} else if (obj instanceof IResource) {
			return new ColoredString(((IResource) obj).getName());
		} else if (obj instanceof LoadPathContainer) {
			LoadPathContainer container= (LoadPathContainer) obj;
			return getContainerEntryLabel(container.getLoadpathEntry().getPath(), container.getRubyProject());
		}
		return new ColoredString(RubyElementLabels.getTextLabel(obj, flags));
	}
				
	/**
	 * Returns the label for a Ruby element with the flags as defined by this class.
	 * @param element The element to render.
	 * @param flags The rendering flags.
	 * @return the label of the Ruby element
	 */
	public static ColoredString getElementLabel(IRubyElement element, long flags) {
		ColoredString result= new ColoredString();
		getElementLabel(element, flags, result);
		return result;
	}
	
	/**
	 * Returns the label for a Ruby element with the flags as defined by this class.
	 * @param element The element to render.
	 * @param flags The rendering flags.
	 * @param result The buffer to append the resulting label to.
	 */
	public static void getElementLabel(IRubyElement element, long flags, ColoredString result) {
		int type= element.getElementType();
		ISourceFolderRoot root= null;
		
		if (type != IRubyElement.RUBY_MODEL && type != IRubyElement.RUBY_PROJECT && type != IRubyElement.SOURCE_FOLDER_ROOT)
			root= RubyModelUtil.getSourceFolderRoot(element);
		if (root != null && getFlag(flags, RubyElementLabels.PREPEND_ROOT_PATH)) {
			getSourceFolderRootLabel(root, RubyElementLabels.ROOT_QUALIFIED, result);
			result.append(RubyElementLabels.CONCAT_STRING);
		}		
		
		switch (type) {
			case IRubyElement.METHOD:
				getMethodLabel((IMethod) element, flags, result);
				break;
			case IRubyElement.FIELD: 
				getFieldLabel((IField) element, flags, result);
				break;
			case IRubyElement.LOCAL_VARIABLE: 
				getLocalVariableLabel((ILocalVariable) element, flags, result);
				break;				
			case IRubyElement.TYPE: 
				getTypeLabel((IType) element, flags, result);
				break;			
			case IRubyElement.SCRIPT: 
				getCompilationUnitLabel((IRubyScript) element, flags, result);
				break;	
			case IRubyElement.SOURCE_FOLDER: 
				getSourceFolderLabel((ISourceFolder) element, flags, result);
				break;
			case IRubyElement.SOURCE_FOLDER_ROOT: 
				getSourceFolderRootLabel((ISourceFolderRoot) element, flags, result);
				break;
			case IRubyElement.IMPORT_CONTAINER:
			case IRubyElement.IMPORT_DECLARATION:
				getDeclarationLabel(element, flags, result);
				break;
			case IRubyElement.RUBY_PROJECT:
			case IRubyElement.RUBY_MODEL:
				result.append(element.getElementName());
				break;
			default:
				result.append(element.getElementName());
		}
		
		if (root != null && getFlag(flags, RubyElementLabels.APPEND_ROOT_PATH)) {
			int offset= result.length();
			result.append(RubyElementLabels.CONCAT_STRING);
			getSourceFolderRootLabel(root, RubyElementLabels.ROOT_QUALIFIED, result);
			
			if (getFlag(flags, COLORIZE)) {
				result.colorize(offset, result.length() - offset, QUALIFIER_STYLE);
			}
			
		}
	}

	/**
	 * Appends the label for a method to a {@link ColoredString}. Considers the M_* flags.
	 * 	@param method The element to render.
	 * @param flags The rendering flags. Flags with names starting with 'M_' are considered.
	 * @param result The buffer to append the resulting label to.
	 */		
	public static void getMethodLabel(IMethod method, long flags, ColoredString result) {
		try {
			
			// qualification
			if (getFlag(flags, RubyElementLabels.M_FULLY_QUALIFIED)) {
				getTypeLabel(method.getDeclaringType(), RubyElementLabels.T_NAME_FULLY_QUALIFIED | (flags & QUALIFIER_FLAGS), result);
				result.append('.');
			}
				
			result.append(method.getElementName());
			
			// parameters
			result.append('(');
			if (getFlag(flags, RubyElementLabels.M_PARAMETER_NAMES)) {

				int nParams= 0;
				boolean renderVarargs= false;
				String[] names= null;
				if (getFlag(flags, RubyElementLabels.M_PARAMETER_NAMES) && method.exists()) {
					names= method.getParameterNames();
					nParams= names.length;					
				}
				
				for (int i= 0; i < nParams; i++) {
					if (i > 0) {
						result.append(RubyElementLabels.COMMA_STRING);
					}
					if (names != null) {
						result.append(names[i]);
					}
				}
			}
			result.append(')');			

			// category
			if (getFlag(flags, RubyElementLabels.M_CATEGORY) && method.exists()) 
				getCategoryLabel(method, result);
			
			// post qualification
			if (getFlag(flags, RubyElementLabels.M_POST_QUALIFIED)) {
				int offset= result.length();
				result.append(RubyElementLabels.CONCAT_STRING);
				getTypeLabel(method.getDeclaringType(), RubyElementLabels.T_NAME_FULLY_QUALIFIED | (flags & QUALIFIER_FLAGS), result);
				if (getFlag(flags, COLORIZE)) {
					result.colorize(offset, result.length() - offset, QUALIFIER_STYLE);
				}
			}
			
		} catch (RubyModelException e) {
			RubyPlugin.log(e); // NotExistsException will not reach this point
		}
	}

	private static void getCategoryLabel(IMember member, ColoredString result) throws RubyModelException {
//		String[] categories= member.getCategories();
//		if (categories.length > 0) {
//			ColoredString categoriesBuf= new ColoredString();
//			for (int i= 0; i < categories.length; i++) {
//				if (i > 0)
//					categoriesBuf.append(RubyUIMessages.RubyElementLabels_category_separator_string);
//				categoriesBuf.append(categories[i]);
//			}
//			result.append(RubyElementLabels.CONCAT_STRING);
//			result.append(Messages.format(RubyUIMessages.RubyElementLabels_category , categoriesBuf.toString()));
//		}
	}
		
	/**
	 * Appends the label for a field to a {@link ColoredString}. Considers the F_* flags.
	 * 	@param field The element to render.
	 * @param flags The rendering flags. Flags with names starting with 'F_' are considered.
	 * @param result The buffer to append the resulting label to.
	 */	
	public static void getFieldLabel(IField field, long flags, ColoredString result) {
		try {			
			// qualification
			if (getFlag(flags, RubyElementLabels.F_FULLY_QUALIFIED)) {
				getTypeLabel(field.getDeclaringType(), RubyElementLabels.T_FILENAME_QUALIFIED | (flags & QUALIFIER_FLAGS), result);
				result.append('.');
			}
			result.append(field.getElementName());

			// category
			if (getFlag(flags, RubyElementLabels.F_CATEGORY) && field.exists())
				getCategoryLabel(field, result);

			// post qualification
			if (getFlag(flags, RubyElementLabels.F_POST_QUALIFIED)) {
				int offset= result.length();
				result.append(RubyElementLabels.CONCAT_STRING);
				getTypeLabel(field.getDeclaringType(), RubyElementLabels.T_FILENAME_QUALIFIED | (flags & QUALIFIER_FLAGS), result);
				if (getFlag(flags, COLORIZE)) {
					result.colorize(offset, result.length() - offset, QUALIFIER_STYLE);
				}
			}

		} catch (RubyModelException e) {
			RubyPlugin.log(e); // NotExistsException will not reach this point
		}			
	}
	
	/**
	 * Appends the label for a local variable to a {@link ColoredString}.
	 * 	@param localVariable The element to render.
	 * @param flags The rendering flags. Flags with names starting with 'F_' are considered.
	 * @param result The buffer to append the resulting label to.
	 */	
	public static void getLocalVariableLabel(ILocalVariable localVariable, long flags, ColoredString result) {		
		if (getFlag(flags, RubyElementLabels.F_FULLY_QUALIFIED)) {
			getElementLabel(localVariable.getParent(), RubyElementLabels.M_FULLY_QUALIFIED | RubyElementLabels.T_FILENAME_QUALIFIED | (flags & QUALIFIER_FLAGS), result);
			result.append('.');
		}
		
		result.append(localVariable.getElementName());
		
		// post qualification
		if (getFlag(flags, RubyElementLabels.F_POST_QUALIFIED)) {
			result.append(RubyElementLabels.CONCAT_STRING);
			getElementLabel(localVariable.getParent(), RubyElementLabels.M_FULLY_QUALIFIED | RubyElementLabels.T_FILENAME_QUALIFIED | (flags & QUALIFIER_FLAGS), result);
		}
	}

	/**
	 * Appends the label for a type to a {@link ColoredString}. Considers the T_* flags.
	 * 	@param type The element to render.
	 * @param flags The rendering flags. Flags with names starting with 'T_' are considered.
	 * @param result The buffer to append the resulting label to.
	 */		
	public static void getTypeLabel(IType type, long flags, ColoredString result) {
		
		if (getFlag(flags, RubyElementLabels.T_FILENAME_QUALIFIED)) {
			ISourceFolder folder = type.getSourceFolder();
			if (!folder.isDefaultPackage()) {
				getSourceFolderLabel(folder, (flags & QUALIFIER_FLAGS), result);
				result.append('/');
			}
			getCompilationUnitLabel(type.getRubyScript(), (flags & QUALIFIER_FLAGS), result);
			result.append(':');
		}
		if (getFlag(flags, RubyElementLabels.T_FILENAME_QUALIFIED | RubyElementLabels.T_NAME_FULLY_QUALIFIED)) {
			IType declaringType= type.getDeclaringType();
			if (declaringType != null) {
				getTypeLabel(declaringType, RubyElementLabels.T_NAME_FULLY_QUALIFIED | (flags & QUALIFIER_FLAGS), result);
				result.append("::");
			}
			int parentType= type.getParent().getElementType();
			if (parentType == IRubyElement.METHOD || parentType == IRubyElement.FIELD) { // anonymous or local
				getElementLabel(type.getParent(), 0, result);
				result.append('.');
			}
		}
		
		String typeName= type.getElementName();
		if (typeName.length() == 0) { // anonymous
			try {
				String supertypeName = type.getSuperclassName();
//				String[] superInterfaceNames= type.getIncludedModuleNames();
//				if (superInterfaceNames.length > 0) {
//					supertypeName= Signature.getSimpleName(superInterfaceNames[0]);
//				} else {
//					supertypeName= Signature.getSimpleName(type.getSuperclassName());
//				}
				typeName= Messages.format(RubyUIMessages.RubyElementLabels_anonym_type , supertypeName); 
			} catch (RubyModelException e) {
				//ignore
				typeName= RubyUIMessages.RubyElementLabels_anonym; 
			}
		}
		result.append(typeName);
		
		// category
		if (getFlag(flags, RubyElementLabels.T_CATEGORY) && type.exists()) {
			try {
				getCategoryLabel(type, result);
			} catch (RubyModelException e) {
				// ignore
			}
		}

		// post qualification
		if (getFlag(flags, RubyElementLabels.T_POST_QUALIFIED)) {
			int offset= result.length();
			result.append(RubyElementLabels.CONCAT_STRING);
			IType declaringType= type.getDeclaringType();
			if (declaringType != null) {
				getTypeLabel(declaringType, RubyElementLabels.T_NAME_FULLY_QUALIFIED | (flags & QUALIFIER_FLAGS), result);
				int parentType= type.getParent().getElementType();
				if (parentType == IRubyElement.METHOD || parentType == IRubyElement.FIELD) { // anonymous or local
					result.append('.');
					getElementLabel(type.getParent(), 0, result);
				}
				 ISourceFolder folder = type.getSourceFolder();
					if (!folder.isDefaultPackage()) {
						getSourceFolderLabel(folder, flags & QUALIFIER_FLAGS, result);
						result.append('/');
					}
					getCompilationUnitLabel(type.getRubyScript(), (flags & QUALIFIER_FLAGS),
							result);
					try {
						int other = type.getNameRange().getOffset();
						result.append(", offset: " + other);
					} catch (RubyModelException e) {
						RubyPlugin.log(e);
					} 
			} else {
				getSourceFolderLabel(type.getSourceFolder(), flags & QUALIFIER_FLAGS, result);
			}
			if (getFlag(flags, COLORIZE)) {
				result.colorize(offset, result.length() - offset, QUALIFIER_STYLE);
			}
		}
	}

	/**
	 * Appends the label for a import container, import or package declaration to a {@link ColoredString}. Considers the D_* flags.
	 * 	@param declaration The element to render.
	 * @param flags The rendering flags. Flags with names starting with 'D_' are considered.
	 * @param result The buffer to append the resulting label to.
	 */	
	public static void getDeclarationLabel(IRubyElement declaration, long flags, ColoredString result) {
		if (getFlag(flags, RubyElementLabels.D_QUALIFIED)) {
			IRubyElement openable= (IRubyElement) declaration.getOpenable();
			if (openable != null) {
				result.append(getElementLabel(openable, RubyElementLabels.CF_QUALIFIED | RubyElementLabels.CU_QUALIFIED | (flags & QUALIFIER_FLAGS)));
				result.append('/');
			}	
		}
		if (declaration.getElementType() == IRubyElement.IMPORT_CONTAINER) {
			result.append(RubyUIMessages.RubyElementLabels_import_container); 
		} else {
			result.append(declaration.getElementName());
		}
		// post qualification
		if (getFlag(flags, RubyElementLabels.D_POST_QUALIFIED)) {
			int offset= result.length();
			IRubyElement openable= (IRubyElement) declaration.getOpenable();
			if (openable != null) {
				result.append(RubyElementLabels.CONCAT_STRING);
				result.append(getElementLabel(openable, RubyElementLabels.CF_QUALIFIED | RubyElementLabels.CU_QUALIFIED | (flags & QUALIFIER_FLAGS)));
			}
			if (getFlag(flags, COLORIZE)) {
				result.colorize(offset, result.length() - offset, QUALIFIER_STYLE);
			}
		}
	}	
	
	/**
	 * Appends the label for a compilation unit to a {@link ColoredString}. Considers the CU_* flags.
	 * 	@param cu The element to render.
	 * @param flags The rendering flags. Flags with names starting with 'CU_' are considered.
	 * @param result The buffer to append the resulting label to.
	 */
	public static void getCompilationUnitLabel(IRubyScript cu, long flags, ColoredString result) {
		if (getFlag(flags, RubyElementLabels.CU_QUALIFIED)) {
			ISourceFolder pack= (ISourceFolder) cu.getParent();
			if (!pack.isDefaultPackage()) {
				getSourceFolderLabel(pack, (flags & QUALIFIER_FLAGS), result);
				result.append('.');
			}
		}
		result.append(cu.getElementName());
		
		if (getFlag(flags, RubyElementLabels.CU_POST_QUALIFIED)) {
			int offset= result.length();
			result.append(RubyElementLabels.CONCAT_STRING);
			getSourceFolderLabel((ISourceFolder) cu.getParent(), flags & QUALIFIER_FLAGS, result);
			if (getFlag(flags, COLORIZE)) {
				result.colorize(offset, result.length() - offset, QUALIFIER_STYLE);
			}
		}		
	}

	/**
	 * Appends the label for a package fragment to a {@link ColoredString}. Considers the P_* flags.
	 * 	@param pack The element to render.
	 * @param flags The rendering flags. Flags with names starting with P_' are considered.
	 * @param result The buffer to append the resulting label to.
	 */	
	public static void getSourceFolderLabel(ISourceFolder pack, long flags, ColoredString result) {
		if (getFlag(flags, RubyElementLabels.P_QUALIFIED)) {
			getSourceFolderRootLabel((ISourceFolderRoot) pack.getParent(), RubyElementLabels.ROOT_QUALIFIED, result);
			result.append('/');
		}
		if (pack.isDefaultPackage()) {
			result.append(RubyElementLabels.DEFAULT_PACKAGE);
		} else if (getFlag(flags, RubyElementLabels.P_COMPRESSED)) {
			StringBuffer buf= new StringBuffer();
			RubyElementLabels.getSourceFolderLabel(pack, RubyElementLabels.P_COMPRESSED, buf);
			result.append(buf.toString());
		} else {
			result.append(pack.getElementName());
		}
		if (getFlag(flags, RubyElementLabels.P_POST_QUALIFIED)) {
			int offset= result.length();
			result.append(RubyElementLabels.CONCAT_STRING);
			getSourceFolderRootLabel((ISourceFolderRoot) pack.getParent(), RubyElementLabels.ROOT_QUALIFIED, result);
			if (getFlag(flags, COLORIZE)) {
				result.colorize(offset, result.length() - offset, QUALIFIER_STYLE);
			}
		}
	}

	/**
	 * Appends the label for a package fragment root to a {@link ColoredString}. Considers the ROOT_* flags.
	 * 	@param root The element to render.
	 * @param flags The rendering flags. Flags with names starting with ROOT_' are considered.
	 * @param result The buffer to append the resulting label to.
	 */	
	public static void getSourceFolderRootLabel(ISourceFolderRoot root, long flags, ColoredString result) {
		if (root.isArchive())
			getArchiveLabel(root, flags, result);
		else
			getFolderLabel(root, flags, result);
	}
	
	private static void getArchiveLabel(ISourceFolderRoot root, long flags, ColoredString result) {
		// Handle variables different	
		if (getFlag(flags, RubyElementLabels.ROOT_VARIABLE) && getVariableLabel(root, flags, result))
			return;
		boolean external= root.isExternal();
		if (external)
			getExternalArchiveLabel(root, flags, result);
		else
			getInternalArchiveLabel(root, flags, result);
	}
	
	private static boolean getVariableLabel(ISourceFolderRoot root, long flags, ColoredString result) {
		try {
			ILoadpathEntry rawEntry= root.getRawLoadpathEntry();
			if (rawEntry != null && rawEntry.getEntryKind() == ILoadpathEntry.CPE_VARIABLE) {
				IPath path= rawEntry.getPath().makeRelative();
				int offset= result.length();
				if (getFlag(flags, RubyElementLabels.REFERENCED_ROOT_POST_QUALIFIED)) {
					int segements= path.segmentCount();
					if (segements > 0) {
						result.append(path.segment(segements - 1));
						if (segements > 1) {
							result.append(RubyElementLabels.CONCAT_STRING);
							result.append(path.removeLastSegments(1).toOSString());
						}
					} else {
						result.append(path.toString());
					}
				} else {
					result.append(path.toString());
				}
				result.append(RubyElementLabels.CONCAT_STRING);
				if (root.isExternal())
					result.append(root.getPath().toOSString());
				else
					result.append(root.getPath().makeRelative().toString());
				
				if (getFlag(flags, COLORIZE)) {
					result.colorize(offset, result.length() - offset, QUALIFIER_STYLE);
				}
				return true;
			}
		} catch (RubyModelException e) {
			RubyPlugin.log(e); // problems with class path
		}
		return false;
	}

	private static void getExternalArchiveLabel(ISourceFolderRoot root, long flags, ColoredString result) {
		IPath path= root.getPath();
		if (getFlag(flags, RubyElementLabels.REFERENCED_ROOT_POST_QUALIFIED)) {
			int segements= path.segmentCount();
			if (segements > 0) {
				result.append(path.segment(segements - 1));
				int offset= result.length();
				if (segements > 1 || path.getDevice() != null) {
					result.append(RubyElementLabels.CONCAT_STRING);
					result.append(path.removeLastSegments(1).toOSString());
				}
				if (getFlag(flags, COLORIZE)) {
					result.colorize(offset, result.length() - offset, QUALIFIER_STYLE);
				}
			} else {
				result.append(path.toOSString());
			}
		} else {
			result.append(path.toOSString());
		}
	}

	private static void getInternalArchiveLabel(ISourceFolderRoot root, long flags, ColoredString result) {
		IResource resource= root.getResource();
		boolean rootQualified= getFlag(flags, RubyElementLabels.ROOT_QUALIFIED);
		boolean referencedQualified= getFlag(flags, RubyElementLabels.REFERENCED_ROOT_POST_QUALIFIED) && isReferenced(root);
		if (rootQualified) {
			result.append(root.getPath().makeRelative().toString());
		} else {
			result.append(root.getElementName());
			int offset= result.length();
			if (referencedQualified) {
				result.append(RubyElementLabels.CONCAT_STRING);
				result.append(resource.getParent().getFullPath().makeRelative().toString());
			} else if (getFlag(flags, RubyElementLabels.ROOT_POST_QUALIFIED)) {
				result.append(RubyElementLabels.CONCAT_STRING);
				result.append(root.getParent().getPath().makeRelative().toString());
			} else {
				return;
			}
			if (getFlag(flags, COLORIZE)) {
				result.colorize(offset, result.length() - offset, QUALIFIER_STYLE);
			}
		}
	}

	private static void getFolderLabel(ISourceFolderRoot root, long flags, ColoredString result) {
		IResource resource= root.getResource();
		boolean rootQualified= getFlag(flags, RubyElementLabels.ROOT_QUALIFIED);
		boolean referencedQualified= getFlag(flags, RubyElementLabels.REFERENCED_ROOT_POST_QUALIFIED) && isReferenced(root);
		if (rootQualified) {
			result.append(root.getPath().makeRelative().toString());
		} else {
			if (resource != null) {
				IPath projectRelativePath= resource.getProjectRelativePath();
				if (projectRelativePath.segmentCount() == 0) {
					result.append(resource.getName());
					referencedQualified= false;
				} else {
					result.append(projectRelativePath.toString());
				}
			} else
				result.append(root.getElementName());
			int offset= result.length();
			if (referencedQualified) {
				result.append(RubyElementLabels.CONCAT_STRING);
				result.append(resource.getProject().getName());
			} else if (getFlag(flags, RubyElementLabels.ROOT_POST_QUALIFIED)) {
				result.append(RubyElementLabels.CONCAT_STRING);
				result.append(root.getParent().getElementName());
			} else {
				return;
			}
			if (getFlag(flags, COLORIZE)) {
				result.colorize(offset, result.length() - offset, QUALIFIER_STYLE);
			}
		}
	}
	
	/**
	 * @param root
	 * @return <code>true</code> if the given package fragment root is
	 * referenced. This means it is owned by a different project but is referenced
	 * by the root's parent. Returns <code>false</code> if the given root
	 * doesn't have an underlying resource.
	 */
	private static boolean isReferenced(ISourceFolderRoot root) {
		IResource resource= root.getResource();
		if (resource != null) {
			IProject jarProject= resource.getProject();
			IProject container= root.getRubyProject().getProject();
			return !container.equals(jarProject);
		}
		return false;
	}
		
	/**
	 * Returns the label of a classpath container
	 * @param containerPath The path of the container.
	 * @param project The project the container is resolved in.
	 * @return Returns the label of the classpath container
	 */
	public static ColoredString getContainerEntryLabel(IPath containerPath, IRubyProject project) {
		try {
			ILoadpathContainer container= RubyCore.getLoadpathContainer(containerPath, project);
			String description= null;
			if (container != null) {
				description= container.getDescription();
			}
			if (description == null) {
				LoadpathContainerInitializer initializer= RubyCore.getLoadpathContainerInitializer(containerPath.segment(0));
				if (initializer != null) {
					description= initializer.getDescription(containerPath, project);
				}
			}
			if (description != null) {
				ColoredString str= new ColoredString(description);
				if (containerPath.segmentCount() > 0 && RubyRuntime.RUBY_CONTAINER.equals(containerPath.segment(0))) {
					int index= description.indexOf('[');
					if (index != -1) {
						str.colorize(index, description.length() - index, DECORATIONS_STYLE); 
					}
				}
				return str;
			}
		} catch (RubyModelException e) {
			// ignore
		}
		return new ColoredString(containerPath.toString());
	}

	public static ColoredString decorateColoredString(ColoredString string, String decorated, Style color) {
		String label= string.getString();
		int originalStart= decorated.indexOf(label);
		if (originalStart == -1) {
			return new ColoredString(decorated); // the decorator did something wild
		}
		if (originalStart > 0) {
			ColoredString newString= new ColoredString(decorated.substring(0, originalStart), color);
			newString.append(string);
			string= newString;
		}
		if (decorated.length() > originalStart + label.length()) { // decorator appended something
			return string.append(decorated.substring(originalStart + label.length()), color);
		}
		return string; // no change
	}
	
}
