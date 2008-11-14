/**
 * Copyright (c) 2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl -v10.html. If redistributing this code,
 * this entire header must remain intact.
 *
 * This file is based on a JDT equivalent:
 *******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.wizards.buildpaths;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.ide.IDE;
import org.rubypeople.rdt.core.ILoadpathContainer;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.LoadpathContainerInitializer;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.viewsupport.RubyElementImageProvider;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.ui.ISharedImages;
import org.rubypeople.rdt.ui.RubyElementImageDescriptor;
import org.rubypeople.rdt.ui.RubyElementLabels;
import org.rubypeople.rdt.ui.RubyUI;
import org.rubypeople.rdt.ui.viewsupport.ImageDescriptorRegistry;

public class CPListLabelProvider extends LabelProvider {
		
	private String fNewLabel, fClassLabel, fCreateLabel;
		
	private ImageDescriptorRegistry fRegistry;
	private ISharedImages fSharedImages;

	private ImageDescriptor fProjectImage;
	
	public CPListLabelProvider() {
		fNewLabel= NewWizardMessages.CPListLabelProvider_new; 
		fClassLabel= NewWizardMessages.CPListLabelProvider_classcontainer; 
		fCreateLabel= NewWizardMessages.CPListLabelProvider_willbecreated; 
		fRegistry= RubyPlugin.getImageDescriptorRegistry();
	
		fSharedImages= RubyUI.getSharedImages();

		IWorkbench workbench= RubyPlugin.getDefault().getWorkbench();
		
		fProjectImage= workbench.getSharedImages().getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT);
	}
	
	public String getText(Object element) {
		if (element instanceof CPListElement) {
			return getCPListElementText((CPListElement) element);
		} else if (element instanceof CPListElementAttribute) {
			CPListElementAttribute attribute= (CPListElementAttribute) element;
			String text= getCPListElementAttributeText(attribute);
			if (attribute.isInNonModifiableContainer()) {
				return Messages.format(NewWizardMessages.CPListLabelProvider_non_modifiable_attribute, text); 
			}
			return text;
		} else if (element instanceof CPUserLibraryElement) {
			return getCPUserLibraryText((CPUserLibraryElement) element);
		}
		return super.getText(element);
	}
	
	public String getCPUserLibraryText(CPUserLibraryElement element) {
		String name= element.getName();
		if (element.isSystemLibrary()) {
			name= Messages.format(NewWizardMessages.CPListLabelProvider_systemlibrary, name); 
		}
		return name;
	}

	public String getCPListElementAttributeText(CPListElementAttribute attrib) {
		String notAvailable= NewWizardMessages.CPListLabelProvider_none; 
		String key= attrib.getKey();
		if (key.equals(CPListElement.EXCLUSION)) {
			String arg= null;
			IPath[] patterns= (IPath[]) attrib.getValue();
			if (patterns != null && patterns.length > 0) {
				int patternsCount= 0;
				StringBuffer buf= new StringBuffer();
				for (int i= 0; i < patterns.length; i++) {
					String pattern= patterns[i].toString();
					if (pattern.length() > 0) {
						if (patternsCount > 0) {
							buf.append(NewWizardMessages.CPListLabelProvider_exclusion_filter_separator); 
						}
						buf.append(pattern);
						patternsCount++;
					}
				}
				if (patternsCount > 0) {
					arg= buf.toString();
				} else {
					arg= notAvailable;
				}
			} else {
				arg= notAvailable;
			}
			return Messages.format(NewWizardMessages.CPListLabelProvider_exclusion_filter_label, new String[] { arg }); 
		} else if (key.equals(CPListElement.INCLUSION)) {
			String arg= null;
			IPath[] patterns= (IPath[]) attrib.getValue();
			if (patterns != null && patterns.length > 0) {
				int patternsCount= 0;
				StringBuffer buf= new StringBuffer();
				for (int i= 0; i < patterns.length; i++) {
					String pattern= patterns[i].toString();
					if (pattern.length() > 0) {
						if (patternsCount > 0) {
							buf.append(NewWizardMessages.CPListLabelProvider_inclusion_filter_separator);
						}
						buf.append(pattern);
						patternsCount++;
					}					
				}
				if (patternsCount > 0) {
					arg= buf.toString();
				} else {
					arg= notAvailable;
				}
			} else {
				arg= NewWizardMessages.CPListLabelProvider_all; 
			}
			return Messages.format(NewWizardMessages.CPListLabelProvider_inclusion_filter_label, new String[] { arg });
		}
		return notAvailable;
	}
	
	public String getCPListElementText(CPListElement cpentry) {
		IPath path= cpentry.getPath();
		switch (cpentry.getEntryKind()) {
			case ILoadpathEntry.CPE_LIBRARY: {
				IResource resource= cpentry.getResource();
				if (resource instanceof IContainer) {
					StringBuffer buf= new StringBuffer(path.makeRelative().toString());
					IPath linkTarget= cpentry.getLinkTarget();
					if (linkTarget != null) {
						buf.append(RubyElementLabels.CONCAT_STRING);
						buf.append(linkTarget.toOSString());
					}
					buf.append(' ');
					buf.append(fClassLabel);
					if (!resource.exists()) {
						buf.append(' ');
						if (cpentry.isMissing()) {
							buf.append(fCreateLabel);
						} else {
							buf.append(fNewLabel);
						}
					}
					return buf.toString();
//				} else if (ArchiveFileFilter.isArchivePath(path)) {
//					return getPathString(path, resource == null);
				}
				// should not get here
				return path.makeRelative().toString();
			}
			case ILoadpathEntry.CPE_VARIABLE: {
				return getVariableString(path);
			}
			case ILoadpathEntry.CPE_PROJECT:
				return path.lastSegment();
			case ILoadpathEntry.CPE_CONTAINER:
				try {
					ILoadpathContainer container= RubyCore.getLoadpathContainer(path, cpentry.getRubyProject());
					if (container != null) {
						return container.getDescription();
					}
					LoadpathContainerInitializer initializer= RubyCore.getLoadpathContainerInitializer(path.segment(0));
					if (initializer != null) {
						String description= initializer.getDescription(path, cpentry.getRubyProject());
						return Messages.format(NewWizardMessages.CPListLabelProvider_unbound_library, description); 
					}
				} catch (RubyModelException e) {
	
				}
				return path.toString();
			case ILoadpathEntry.CPE_SOURCE: {
				StringBuffer buf= new StringBuffer(path.makeRelative().toString());
				IPath linkTarget= cpentry.getLinkTarget();
				if (linkTarget != null) {
					buf.append(RubyElementLabels.CONCAT_STRING);
					buf.append(linkTarget.toOSString());
				}
				IResource resource= cpentry.getResource();
				if (resource != null && !resource.exists()) {
					buf.append(' ');
					if (cpentry.isMissing()) {
						buf.append(fCreateLabel);
					} else {
						buf.append(fNewLabel);
					}
				} else if (cpentry.getOrginalPath() == null) {
					buf.append(' ');
					buf.append(fNewLabel);
				}
				return buf.toString();
			}
			default:
				// pass
		}
		return NewWizardMessages.CPListLabelProvider_unknown_element_label; 
	}
	
	private String getPathString(IPath path, boolean isExternal) {
//		if (ArchiveFileFilter.isArchivePath(path)) {
//			IPath appendedPath= path.removeLastSegments(1);
//			String appended= isExternal ? appendedPath.toOSString() : appendedPath.makeRelative().toString();
//			return Messages.format(NewWizardMessages.CPListLabelProvider_twopart, new String[] { path.lastSegment(), appended }); 
//		} else {
			return isExternal ? path.toOSString() : path.makeRelative().toString();
//		}
	}
	
	private String getVariableString(IPath path) {
		String name= path.makeRelative().toString();
		IPath[] entryPath= RubyCore.getLoadpathVariable(path.segment(0));
		if (entryPath != null) {
			String appended= entryPath[0].append(path.removeFirstSegments(1)).toOSString();
			return Messages.format(NewWizardMessages.CPListLabelProvider_twopart, new String[] { name, appended }); 
		} else {
			return name;
		}
	}
	
	private ImageDescriptor getCPListElementBaseImage(CPListElement cpentry) {
		switch (cpentry.getEntryKind()) {
			case ILoadpathEntry.CPE_SOURCE:
				if (cpentry.getPath().segmentCount() == 1) {
					return fProjectImage;
				} else {
					return fSharedImages.getImageDescriptor(ISharedImages.IMG_OBJS_SOURCE_FOLDER_ROOT);
				}
			case ILoadpathEntry.CPE_LIBRARY:
				IResource res= cpentry.getResource();
				if (res == null) {
					return fSharedImages.getImageDescriptor(ISharedImages.IMG_OBJS_EXTERNAL_ARCHIVE_WITH_SOURCE);
//				} else if (res instanceof IFile) {
//					return fSharedImages.getImageDescriptor(ISharedImages.IMG_OBJS_JAR_WITH_SOURCE);
				} else {
					return fSharedImages.getImageDescriptor(ISharedImages.IMG_OBJS_SOURCE_FOLDER_ROOT);
				}
			case ILoadpathEntry.CPE_PROJECT:
				return fProjectImage;
			case ILoadpathEntry.CPE_VARIABLE:
				return fSharedImages.getImageDescriptor(ISharedImages.IMG_OBJS_LOADPATH_VAR_ENTRY);
			case ILoadpathEntry.CPE_CONTAINER:
				return fSharedImages.getImageDescriptor(ISharedImages.IMG_OBJS_LIBRARY);
			default:
				return null;
		}
	}			
		
	public Image getImage(Object element) {
		if (element instanceof CPListElement) {
			CPListElement cpentry= (CPListElement) element;
			ImageDescriptor imageDescriptor= getCPListElementBaseImage(cpentry);
			if (imageDescriptor != null) {
				if (cpentry.isMissing()) {
					imageDescriptor= new RubyElementImageDescriptor(imageDescriptor, RubyElementImageDescriptor.WARNING, RubyElementImageProvider.SMALL_SIZE);
				}
				return fRegistry.get(imageDescriptor);
			}
		} else if (element instanceof CPListElementAttribute) {
			String key= ((CPListElementAttribute) element).getKey();
			if (key.equals(CPListElement.EXCLUSION)) {
				return fRegistry.get(RubyPluginImages.DESC_OBJS_EXCLUSION_FILTER_ATTRIB);
			} else if (key.equals(CPListElement.INCLUSION)) {
				return fRegistry.get(RubyPluginImages.DESC_OBJS_INCLUSION_FILTER_ATTRIB);
			}
			return  fSharedImages.getImage(ISharedImages.IMG_OBJS_LOADPATH_VAR_ENTRY);
		} else if (element instanceof CPUserLibraryElement) {
			return  fSharedImages.getImage(ISharedImages.IMG_OBJS_LIBRARY);
		}
		return null;
	}


}	
