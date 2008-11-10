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

package org.rubypeople.rdt.internal.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.rubypeople.rdt.launching.IRuntimeLoadpathEntry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class VariableLoadpathEntry extends AbstractRuntimeLoadpathEntry {
	public static final String TYPE_ID = "org.rubypeople.rdt.launching.loadpathentry.variableLoadpathEntry"; //$NON-NLS-1$
	private String variableString;
	
	public VariableLoadpathEntry() {
	}
	
	public VariableLoadpathEntry(String variableString) {
		this.variableString = variableString;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.launching.AbstractRuntimeLoadpathEntry#buildMemento(org.w3c.dom.Document, org.w3c.dom.Element)
	 */
	protected void buildMemento(Document document, Element memento) throws CoreException {
		memento.setAttribute("variableString", variableString); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry2#initializeFrom(org.w3c.dom.Element)
	 */
	public void initializeFrom(Element memento) throws CoreException {
		variableString = memento.getAttribute("variableString"); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry2#getTypeId()
	 */
	public String getTypeId() {
		return TYPE_ID;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry2#getRuntimeLoadpathEntries(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public IRuntimeLoadpathEntry[] getRuntimeLoadpathEntries(ILaunchConfiguration configuration) throws CoreException {
		return new IRuntimeLoadpathEntry[0];
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry2#getName()
	 */
	public String getName() {
		return variableString; 
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry#getType()
	 */
	public int getType() {
		return OTHER; 
	}
	/**
	 * @return Returns the variableString.
	 */
	public String getVariableString() {
		return variableString;
	}
	/**
	 * @param variableString The variableString to set.
	 */
	public void setVariableString(String variableString) {
		this.variableString = variableString;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry#getPath()
	 */
//	public IPath getPath() {
//		try {
//			String path = StringVariableManager.getDefault().performStringSubstitution(variableString);
//			return new Path(path);
//		} catch (CoreException ce) {
//			return null;
//		}
//	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		if (variableString != null)
			return variableString.hashCode();
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof VariableLoadpathEntry) {
			VariableLoadpathEntry other= (VariableLoadpathEntry)obj;
			if (variableString != null) {
				return variableString.equals(other.variableString);
			}
		}
		return false;
	}

}
