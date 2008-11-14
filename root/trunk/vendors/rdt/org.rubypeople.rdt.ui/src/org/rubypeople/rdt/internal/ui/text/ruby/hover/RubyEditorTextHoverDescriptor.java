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

package org.rubypeople.rdt.internal.ui.text.ruby.hover;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.Assert;
import org.eclipse.swt.SWT;
import org.osgi.framework.Bundle;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.EditorUtility;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.text.ruby.hover.IRubyEditorTextHover;

/**
 * Describes a Ruby editor text hover.
 *
 * @since 1.0
 */
public class RubyEditorTextHoverDescriptor {

	private static final String RUBY_EDITOR_TEXT_HOVER_EXTENSION_POINT= "org.rubypeople.rdt.ui.rubyEditorTextHovers"; //$NON-NLS-1$
	private static final String HOVER_TAG= "hover"; //$NON-NLS-1$
	private static final String ID_ATTRIBUTE= "id"; //$NON-NLS-1$
	private static final String CLASS_ATTRIBUTE= "class"; //$NON-NLS-1$
	private static final String LABEL_ATTRIBUTE= "label"; //$NON-NLS-1$
	private static final String ACTIVATE_PLUG_IN_ATTRIBUTE= "activate"; //$NON-NLS-1$
	private static final String DESCRIPTION_ATTRIBUTE= "description"; //$NON-NLS-1$

	public static final String NO_MODIFIER= "0"; //$NON-NLS-1$
	public static final String DISABLED_TAG= "!"; //$NON-NLS-1$
	public static final String VALUE_SEPARATOR= ";"; //$NON-NLS-1$

	private int fStateMask;
	private String fModifierString;
	private boolean fIsEnabled;

	private IConfigurationElement fElement;


	/**
	 * Returns all Ruby editor text hovers contributed to the workbench.
	 */
	public static RubyEditorTextHoverDescriptor[] getContributedHovers() {
		IExtensionRegistry registry= Platform.getExtensionRegistry();
		IConfigurationElement[] elements= registry.getConfigurationElementsFor(RUBY_EDITOR_TEXT_HOVER_EXTENSION_POINT);
		RubyEditorTextHoverDescriptor[] hoverDescs= createDescriptors(elements);
		initializeFromPreferences(hoverDescs);
		return hoverDescs;
	}

	/**
	 * Computes the state mask for the given modifier string.
	 *
	 * @param modifiers	the string with the modifiers, separated by '+', '-', ';', ',' or '.'
	 * @return the state mask or -1 if the input is invalid
	 */
	public static int computeStateMask(String modifiers) {
		if (modifiers == null)
			return -1;

		if (modifiers.length() == 0)
			return SWT.NONE;

		int stateMask= 0;
		StringTokenizer modifierTokenizer= new StringTokenizer(modifiers, ",;.:+-* "); //$NON-NLS-1$
		while (modifierTokenizer.hasMoreTokens()) {
			int modifier= EditorUtility.findLocalizedModifier(modifierTokenizer.nextToken());
			if (modifier == 0 || (stateMask & modifier) == modifier)
				return -1;
			stateMask= stateMask | modifier;
		}
		return stateMask;
	}

	/**
	 * Creates a new Ruby Editor text hover descriptor from the given configuration element.
	 */
	private RubyEditorTextHoverDescriptor(IConfigurationElement element) {
		Assert.isNotNull(element);
		fElement= element;
	}

	/**
	 * Creates the Ruby editor text hover.
	 */
	public IRubyEditorTextHover createTextHover() {
 		String pluginId = fElement.getContributor().getName();
		boolean isHoversPlugInActivated= Platform.getBundle(pluginId).getState() == Bundle.ACTIVE;
		if (isHoversPlugInActivated || canActivatePlugIn()) {
			try {
				return (IRubyEditorTextHover)fElement.createExecutableExtension(CLASS_ATTRIBUTE);
			} catch (CoreException x) {
				RubyPlugin.log(new Status(IStatus.ERROR, RubyPlugin.getPluginId(), 0, RubyHoverMessages.RubyTextHover_createTextHover, null));
			}
		}

		return null;
	}

	//---- XML Attribute accessors ---------------------------------------------

	/**
	 * Returns the hover's id.
	 */
	public String getId() {
			return fElement.getAttribute(ID_ATTRIBUTE);
	}

	/**
	 * Returns the hover's class name.
	 */
	public String getHoverClassName() {
		return fElement.getAttribute(CLASS_ATTRIBUTE);
	}

	/**
	 * Returns the hover's label.
	 */
	public String getLabel() {
		String label= fElement.getAttribute(LABEL_ATTRIBUTE);
		if (label != null)
			return label;

		// Return simple class name
		label= getHoverClassName();
		int lastDot= label.lastIndexOf('.');
		if (lastDot >= 0 && lastDot < label.length() - 1)
			return label.substring(lastDot + 1);
		else
			return label;
	}

	/**
	 * Returns the hover's description.
	 *
	 * @return the hover's description or <code>null</code> if not provided
	 */
	public String getDescription() {
		return fElement.getAttribute(DESCRIPTION_ATTRIBUTE);
	}


	public boolean canActivatePlugIn() {
		return Boolean.valueOf(fElement.getAttribute(ACTIVATE_PLUG_IN_ATTRIBUTE)).booleanValue();
	}

	public boolean equals(Object obj) {
		if (obj == null || !obj.getClass().equals(this.getClass()) || getId() == null)
			return false;
		return getId().equals(((RubyEditorTextHoverDescriptor)obj).getId());
	}

	public int hashCode() {
		return getId().hashCode();
	}

	private static RubyEditorTextHoverDescriptor[] createDescriptors(IConfigurationElement[] elements) {
		List result= new ArrayList(elements.length);
		for (int i= 0; i < elements.length; i++) {
			IConfigurationElement element= elements[i];
			if (HOVER_TAG.equals(element.getName())) {
				RubyEditorTextHoverDescriptor desc= new RubyEditorTextHoverDescriptor(element);
				result.add(desc);
			}
		}
		return (RubyEditorTextHoverDescriptor[])result.toArray(new RubyEditorTextHoverDescriptor[result.size()]);
	}

	private static void initializeFromPreferences(RubyEditorTextHoverDescriptor[] hovers) {
		String compiledTextHoverModifiers= RubyPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.EDITOR_TEXT_HOVER_MODIFIERS);

		StringTokenizer tokenizer= new StringTokenizer(compiledTextHoverModifiers, VALUE_SEPARATOR);
		HashMap idToModifier= new HashMap(tokenizer.countTokens() / 2);

		while (tokenizer.hasMoreTokens()) {
			String id= tokenizer.nextToken();
			if (tokenizer.hasMoreTokens())
				idToModifier.put(id, tokenizer.nextToken());
		}

		String compiledTextHoverModifierMasks= RubyPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.EDITOR_TEXT_HOVER_MODIFIER_MASKS);

		tokenizer= new StringTokenizer(compiledTextHoverModifierMasks, VALUE_SEPARATOR);
		HashMap idToModifierMask= new HashMap(tokenizer.countTokens() / 2);

		while (tokenizer.hasMoreTokens()) {
			String id= tokenizer.nextToken();
			if (tokenizer.hasMoreTokens())
				idToModifierMask.put(id, tokenizer.nextToken());
		}

		for (int i= 0; i < hovers.length; i++) {
			String modifierString= (String)idToModifier.get(hovers[i].getId());
			boolean enabled= true;
			if (modifierString == null)
				modifierString= DISABLED_TAG;

			if (modifierString.startsWith(DISABLED_TAG)) {
				enabled= false;
				modifierString= modifierString.substring(1);
			}

			if (modifierString.equals(NO_MODIFIER))
				modifierString= ""; //$NON-NLS-1$

			hovers[i].fModifierString= modifierString;
			hovers[i].fIsEnabled= enabled;
			hovers[i].fStateMask= computeStateMask(modifierString);
			if (hovers[i].fStateMask == -1) {
				// Fallback: use stored modifier masks
				try {
					hovers[i].fStateMask= Integer.parseInt((String)idToModifierMask.get(hovers[i].getId()));
				} catch (NumberFormatException ex) {
					hovers[i].fStateMask= -1;
				}
				// Fix modifier string
				int stateMask= hovers[i].fStateMask;
				if (stateMask == -1)
					hovers[i].fModifierString= ""; //$NON-NLS-1$
				else
					hovers[i].fModifierString= EditorUtility.getModifierString(stateMask);
			}
		}
	}

	/**
	 * Returns the configured modifier getStateMask for this hover.
	 *
	 * @return the hover modifier stateMask or -1 if no hover is configured
	 */
	public int getStateMask() {
		return fStateMask;
	}

	/**
	 * Returns the modifier String as set in the preference store.
	 *
	 * @return the modifier string
	 */
	public String getModifierString() {
		return fModifierString;
	}

	/**
	 * Returns whether this hover is enabled or not.
	 *
	 * @return <code>true</code> if enabled
	 */
	public boolean isEnabled() {
		return fIsEnabled;
	}

	/**
	 * Returns this hover descriptors configuration element.
	 *
	 * @return the configuration element
	 * @since 3.0
	 */
	public IConfigurationElement getConfigurationElement() {
		return fElement;
	}
}
