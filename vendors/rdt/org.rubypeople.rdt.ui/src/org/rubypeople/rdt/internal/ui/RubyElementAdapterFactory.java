/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.search.ui.ISearchPageScoreComputer;
import org.eclipse.ui.IContainmentAdapter;
import org.eclipse.ui.IContributorResourceAdapter;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.ide.IContributorResourceAdapter2;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.FilePropertySource;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.ResourcePropertySource;
import org.eclipse.ui.views.tasklist.ITaskListResourceAdapter;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.internal.corext.util.RubyElementResourceMapping;
import org.rubypeople.rdt.internal.ui.rubyeditor.IRubyScriptEditorInput;
import org.rubypeople.rdt.internal.ui.search.RubySearchPageScoreComputer;
import org.rubypeople.rdt.internal.ui.search.SearchUtil;

/**
 * Implements basic UI support for Ruby elements.
 * Implements handle to persistent support for Ruby elements.
 */
public class RubyElementAdapterFactory implements IAdapterFactory, IContributorResourceAdapter, IContributorResourceAdapter2 {
	
	private static Class[] PROPERTIES= new Class[] {
		IPropertySource.class,
		IResource.class,
		IWorkbenchAdapter.class,
		IResourceLocator.class,
		IPersistableElement.class,
		IContributorResourceAdapter.class,
		IContributorResourceAdapter2.class,
		ITaskListResourceAdapter.class,
		IContainmentAdapter.class
	};
	
	/*
	 * Do not use real type since this would cause
	 * the Search plug-in to be loaded.
	 */
	private Object fSearchPageScoreComputer;
	private static IResourceLocator fgResourceLocator;
	private static RubyWorkbenchAdapter fgRubyWorkbenchAdapter;
	private static ITaskListResourceAdapter fgTaskListAdapter;
	private static RubyElementContainmentAdapter fgRubyElementContainmentAdapter;
	
	public Class[] getAdapterList() {
		updateLazyLoadedAdapters();
		return PROPERTIES;
	}
	
	public Object getAdapter(Object element, Class key) {
		updateLazyLoadedAdapters();
		IRubyElement java= getRubyElement(element);
		
		if (IPropertySource.class.equals(key)) {
			return getProperties(java);
		} if (IResource.class.equals(key)) {
			return getResource(java);
		} if (fSearchPageScoreComputer != null && ISearchPageScoreComputer.class.equals(key)) {
			return fSearchPageScoreComputer;
		} if (IWorkbenchAdapter.class.equals(key)) {
			return getRubyWorkbenchAdapter();
		} if (IResourceLocator.class.equals(key)) {
			return getResourceLocator();
		} if (IPersistableElement.class.equals(key)) {
			return new PersistableRubyElementFactory(java);
		} if (IContributorResourceAdapter.class.equals(key)) {
			return this;
		} if (IContributorResourceAdapter2.class.equals(key)) {
			return this;
		} if (ITaskListResourceAdapter.class.equals(key)) {
			return getTaskListAdapter();
		} if (IContainmentAdapter.class.equals(key)) {
			return getRubyElementContainmentAdapter();
		}
		return null; 
	}
	
	private IResource getResource(IRubyElement element) {
		// can't use IRubyElement.getResource directly as we are interested in the
		// corresponding resource
		switch (element.getElementType()) {
			case IRubyElement.TYPE:
				// top level types behave like the CU
				IRubyElement parent= element.getParent();
				if (parent instanceof IRubyScript) {
					return ((IRubyScript) parent).getPrimary().getResource();
				}
				return null;
			case IRubyElement.SCRIPT:
				return ((IRubyScript) element).getPrimary().getResource();
			case IRubyElement.SOURCE_FOLDER:
				// test if external
				ISourceFolderRoot root= (ISourceFolderRoot) element.getAncestor(IRubyElement.SOURCE_FOLDER_ROOT);
				if (!root.isExternal()) {
					return element.getResource();
				}
				return null;
			case IRubyElement.SOURCE_FOLDER_ROOT:
			case IRubyElement.RUBY_PROJECT:
			case IRubyElement.RUBY_MODEL:
				return element.getResource();
			default:
				return null;
		}		
    }

    public IResource getAdaptedResource(IAdaptable adaptable) {
    	IRubyElement je= getRubyElement(adaptable);
    	if (je != null)
    		return getResource(je);

    	return null;
    }
    
    public ResourceMapping getAdaptedResourceMapping(IAdaptable adaptable) {
    	IRubyElement je= getRubyElement(adaptable);
    	if (je != null)
    		return RubyElementResourceMapping.create(je);

    	return null;
    }
    
	private IRubyElement getRubyElement(Object element) {
		if (element instanceof IRubyElement)
			return (IRubyElement)element;
		if (element instanceof IRubyScriptEditorInput)
			return ((IRubyScriptEditorInput)element).getRubyScript().getPrimaryElement();

		return null;
	}
	
	private IPropertySource getProperties(IRubyElement element) {
		IResource resource= getResource(element);
		if (resource == null)
			return new RubyElementProperties(element);
		if (resource.getType() == IResource.FILE)
			return new FilePropertySource((IFile) resource);
		return new ResourcePropertySource(resource);
	}

	private void updateLazyLoadedAdapters() {
		if (fSearchPageScoreComputer == null && SearchUtil.isSearchPlugInActivated())
			createSearchPageScoreComputer();
	}

	private void createSearchPageScoreComputer() {
		fSearchPageScoreComputer= new RubySearchPageScoreComputer();
		PROPERTIES= new Class[] {
			IPropertySource.class,
			IResource.class,
			ISearchPageScoreComputer.class,
			IWorkbenchAdapter.class,
			IResourceLocator.class,
			IPersistableElement.class,
			IProject.class,
			IContributorResourceAdapter.class,
			IContributorResourceAdapter2.class,
			ITaskListResourceAdapter.class,
			IContainmentAdapter.class
		};
	}

	private static IResourceLocator getResourceLocator() {
		if (fgResourceLocator == null)
			fgResourceLocator= new ResourceLocator();
		return fgResourceLocator;
	}
	
	private static RubyWorkbenchAdapter getRubyWorkbenchAdapter() {
		if (fgRubyWorkbenchAdapter == null) 
			fgRubyWorkbenchAdapter= new RubyWorkbenchAdapter();
		return fgRubyWorkbenchAdapter;
	}

	private static ITaskListResourceAdapter getTaskListAdapter() {
		if (fgTaskListAdapter == null)
			fgTaskListAdapter= new RubyTaskListAdapter();
		return fgTaskListAdapter;
	}

	private static RubyElementContainmentAdapter getRubyElementContainmentAdapter() {
		if (fgRubyElementContainmentAdapter == null)
			fgRubyElementContainmentAdapter= new RubyElementContainmentAdapter();
		return fgRubyElementContainmentAdapter;
	}
}
