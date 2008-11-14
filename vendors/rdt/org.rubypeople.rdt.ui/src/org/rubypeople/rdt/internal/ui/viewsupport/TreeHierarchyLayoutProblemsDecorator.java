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
package org.rubypeople.rdt.internal.ui.viewsupport;

import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.ui.ProblemsLabelDecorator;

/**
 * Special problem decorator for hierarchical package layout.
 * <p>
 * It only decorates package fragments which are not covered by the
 * <code>ProblemsLabelDecorator</code>.
 * </p>
 * 
 * @see org.eclipse.jdt.ui.ProblemsLabelDecorator 
 * @since 2.1
 */
public class TreeHierarchyLayoutProblemsDecorator extends ProblemsLabelDecorator {

	private boolean fIsFlatLayout;
	
	public TreeHierarchyLayoutProblemsDecorator() {
		this(false);
	}
	
	public TreeHierarchyLayoutProblemsDecorator(boolean isFlatLayout) {
		super(null);
		fIsFlatLayout= isFlatLayout;
	}
	
	protected int computePackageAdornmentFlags(ISourceFolder fragment) {
		if (!fIsFlatLayout && !fragment.isDefaultPackage()) {
			return super.computeAdornmentFlags(fragment.getResource());
		}
		return super.computeAdornmentFlags(fragment);
	}		

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.ProblemsLabelDecorator#computeAdornmentFlags(java.lang.Object)
	 */
	protected int computeAdornmentFlags(Object element) {
		if (element instanceof ISourceFolder) {
			return computePackageAdornmentFlags((ISourceFolder) element);
		}
		return super.computeAdornmentFlags(element);
	}
	
	public void setIsFlatLayout(boolean state) {
		fIsFlatLayout= state;
	}

}
