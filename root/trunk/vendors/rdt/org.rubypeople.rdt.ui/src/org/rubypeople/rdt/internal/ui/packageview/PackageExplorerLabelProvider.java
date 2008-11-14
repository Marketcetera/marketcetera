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
package org.rubypeople.rdt.internal.ui.packageview;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.util.Assert;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.internal.ui.viewsupport.AppearanceAwareLabelProvider;

/**
 * Provides the labels for the Package Explorer.
 * <p>
 * It provides labels for the packages in hierarchical layout and in all
 * other cases delegates it to its super class.
 * </p>
 * @since 2.1
 */
public class PackageExplorerLabelProvider extends AppearanceAwareLabelProvider {
	
	private PackageExplorerContentProvider fContentProvider;

	private boolean fIsFlatLayout;
	private PackageExplorerProblemsDecorator fProblemDecorator;

	public PackageExplorerLabelProvider(long textFlags, int imageFlags, PackageExplorerContentProvider cp) {
		super(textFlags, imageFlags);
		fProblemDecorator= new PackageExplorerProblemsDecorator();
		addLabelDecorator(fProblemDecorator);
		Assert.isNotNull(cp);
		fContentProvider= cp;
	}


	public String getText(Object element) {
		
		if (fIsFlatLayout || !(element instanceof ISourceFolder))
			return super.getText(element);			

		ISourceFolder fragment = (ISourceFolder) element;
		
		if (fragment.isDefaultPackage()) {
			return super.getText(fragment);
		} else {
			Object parent= fContentProvider.getSourceFolderProvider().getParent(fragment);
			if (parent instanceof ISourceFolder) {
				return getNameDelta((ISourceFolder) parent, fragment);
			} else if (parent instanceof IFolder) {
				int prefixLength= getPrefixLength((IFolder) parent);
				return fragment.getElementName().substring(prefixLength);
			}
			else return super.getText(fragment);
		}
	}
	
	private int getPrefixLength(IFolder folder) {
		Object parent= fContentProvider.getParent(folder);
		int folderNameLenght= folder.getName().length() + 1;
		if(parent instanceof ISourceFolder) {
			String fragmentName= ((ISourceFolder)parent).getElementName();
			return fragmentName.length() + 1 + folderNameLenght;
		} else if (parent instanceof IFolder) {
			return getPrefixLength((IFolder)parent) + folderNameLenght;
		} else {
			return folderNameLenght;
		}
	}
	
	private String getNameDelta(ISourceFolder topFragment, ISourceFolder bottomFragment) {
		
		String topName= topFragment.getElementName();
		String bottomName= bottomFragment.getElementName();
		
		if(topName.equals(bottomName))
			return topName;
		
		String deltaname= bottomName.substring(topName.length()+1);	
		return deltaname;
	}
	
	public void setIsFlatLayout(boolean state) {
		fIsFlatLayout= state;
		fProblemDecorator.setIsFlatLayout(state);
	}
}
