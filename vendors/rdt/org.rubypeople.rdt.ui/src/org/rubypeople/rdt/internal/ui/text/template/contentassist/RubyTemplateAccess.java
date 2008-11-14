/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.text.template.contentassist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.rubypeople.rdt.internal.corext.template.ruby.RubyContextType;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.extensions.IRubyTemplateProvider;


public class RubyTemplateAccess {
	/** Key to store custom templates. */
	private static final String CUSTOM_TEMPLATES_KEY= "org.rubypeople.rdt.ui.customtemplates"; //$NON-NLS-1$
	
	/** The shared instance. */
	private static RubyTemplateAccess fgInstance;
	
	/** The template store. */
	private TemplateStore fStore;
	
	/** The context type registry. */
	private ContributionContextTypeRegistry fContextTypeRegistry;
	
	private RubyTemplateAccess() {}

	/**
	 * Returns the shared instance.
	 * 
	 * @return the shared instance
	 */
	public static RubyTemplateAccess getDefault() {
		if (fgInstance == null) {
			fgInstance= new RubyTemplateAccess();
		}
		return fgInstance;
	}

	/**
	 * Returns this plug-in's template store.
	 * 
	 * @return the template store of this plug-in instance
	 */
	public TemplateStore getTemplateStore() {
		if (fStore == null) {
			fStore= new ContributionTemplateStore(getContextTypeRegistry(),RubyPlugin.getDefault().getPreferenceStore(), CUSTOM_TEMPLATES_KEY);
			try {
				fStore.load();
			} catch (IOException e) {
				RubyPlugin.log(e);
			}
			
			// Load extension templates
			TemplatePersistenceData[] tempData = getExtensionTemplateData();
			if(tempData != null) {
				for(int i = 0; i < tempData.length; i++) {
					fStore.add(tempData[i]);
				}
			}
		}
		return fStore;
	}
	
	/**
	 * Finds all extensions to the rubyTemplateProvider extension point and return their template data.
	 * 
	 * @return an array of TemplatePersistenceData
	 */
	private TemplatePersistenceData[] getExtensionTemplateData() {
		List extensions = new ArrayList();
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		
		IExtensionPoint[] points = reg.getExtensionPoints(RubyPlugin.PLUGIN_ID);
		IExtensionPoint point = null;
		
		// Search the extension registry for the rubyTemplateProvider extension point
		if(points != null){
			for (int i = 0; i < points.length; i++) {
				IExtensionPoint currentPoint = points[i];
				if(currentPoint.getUniqueIdentifier().endsWith("rubyTemplateProvider")){
					point = currentPoint;
					break;
				}				
			}
			
			// Find all extensions of the point
			if(point != null){
				IExtension[] exts = point.getExtensions();
				
				IRubyTemplateProvider prov = null;
				
				// Get the implementing class of the extension
				for (int i = 0; i < exts.length; i++) {
					IConfigurationElement[] elem = exts[i].getConfigurationElements();
					String attrs[] = elem[0].getAttributeNames();
					try {
						Object tempProv = elem[0].createExecutableExtension("class");
						if (tempProv instanceof IRubyTemplateProvider) {
							prov = (IRubyTemplateProvider) tempProv;
							extensions.add(prov);
						}
					} catch (CoreException e) {
						RubyPlugin.log(e);
					}
				}
			}
		}
		
		// Get the template data from the extensions
		if(extensions.size() > 0){
			for(int i=0; i< extensions.size(); i++){
				IRubyTemplateProvider currentProvider = (IRubyTemplateProvider) extensions.get(i);
				TemplatePersistenceData[] templates = currentProvider.getTemplateData();
				if(templates != null){
					return templates;
				}					
			}
		} 	
		return null;
	}

	/**
	 * Returns this plug-in's context type registry.
	 * 
	 * @return the context type registry for this plug-in instance
	 */
	public ContextTypeRegistry getContextTypeRegistry() {
		if (fContextTypeRegistry == null) {
			// create and configure the contexts available in the template editor
			fContextTypeRegistry= new ContributionContextTypeRegistry();
			fContextTypeRegistry.addContextType(new RubyContextType());
		}
		return fContextTypeRegistry;
	}

	public IPreferenceStore getPreferenceStore() {	    
		return RubyPlugin.getDefault().getPreferenceStore();
	}

	public void savePluginPreferences() {
		RubyPlugin.getDefault().savePluginPreferences();
	}
}
