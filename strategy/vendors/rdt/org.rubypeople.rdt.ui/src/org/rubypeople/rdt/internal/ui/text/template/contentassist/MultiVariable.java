/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.text.template.contentassist;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.templates.TemplateVariable;


/**
 *
 */
public class MultiVariable extends TemplateVariable {
	private final Map fValueMap= new HashMap();
	private Object fSet;
	private Object fDefaultKey= null;

	public MultiVariable(String type, String defaultValue, int[] offsets) {
		super(type, defaultValue, offsets);
		fValueMap.put(fDefaultKey, new String[] { defaultValue });
		fSet= getDefaultValue();
	}

	/**
	 * Sets the values of this variable under a specific set.
	 *
	 * @param set the set identifier for which the values are valid
	 * @param values the possible values of this variable
	 */
	public void setValues(Object set, String[] values) {
		Assert.isNotNull(set);
		Assert.isTrue(values.length > 0);
		fValueMap.put(set, values);
		if (fDefaultKey == null) {
			fDefaultKey= set;
			fSet= getDefaultValue();
		}
	}


	/*
	 * @see org.eclipse.jface.text.templates.TemplateVariable#setValues(java.lang.String[])
	 */
	public void setValues(String[] values) {
		if (fValueMap != null) {
			Assert.isNotNull(values);
			Assert.isTrue(values.length > 0);
			fValueMap.put(fDefaultKey, values);
			fSet= getDefaultValue();
		}
	}


	/*
	 * @see org.eclipse.jface.text.templates.TemplateVariable#getValues()
	 */
	public String[] getValues() {
		return (String[]) fValueMap.get(fDefaultKey);
	}

	/**
	 * Returns the choices for the set identified by <code>set</code>.
	 *
	 * @param set the set identifier
	 * @return the choices for this variable and the given set, or
	 *         <code>null</code> if the set is not defined.
	 */
	public String[] getValues(Object set) {
		return (String[]) fValueMap.get(set);
	}

	/**
	 * @return
	 */
	public Object getSet() {
		return fSet;
	}

	public void setSet(Object set) {
		fSet= set;
	}

}
