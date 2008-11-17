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
package org.rubypeople.rdt.internal.ui.wizards.buildpaths;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;


public class CPVariableElementLabelProvider extends LabelProvider implements IColorProvider {
	
	private Image fJARImage;
	private Image fFolderImage;
	private boolean fShowResolvedVariables;
	
	private Color fResolvedBackground;
	
	public CPVariableElementLabelProvider(boolean showResolvedVariables) {
		ImageRegistry reg= RubyPlugin.getDefault().getImageRegistry();
		fJARImage= reg.get(RubyPluginImages.IMG_OBJS_EXTJAR);
		fFolderImage= PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
		fShowResolvedVariables= showResolvedVariables;
		fResolvedBackground= null;
	}
	
	/*
	 * @see LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		if (element instanceof CPVariableElement) {
			CPVariableElement curr= (CPVariableElement) element;
			IPath path= curr.getPath()[0];
			if (path.toFile().isFile()) {
				return fJARImage;
			}
			return fFolderImage;
		}
		return super.getImage(element);
	}

	/*
	 * @see LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if (element instanceof CPVariableElement) {
			CPVariableElement curr= (CPVariableElement)element;
			String name= curr.getName();
			IPath path= curr.getPath()[0];
			StringBuffer buf= new StringBuffer(name);
			if (curr.isReserved()) {
				buf.append(' ');
				buf.append(NewWizardMessages.CPVariableElementLabelProvider_reserved); 
			}
			if (path != null) {
				buf.append(" - "); //$NON-NLS-1$
				if (!path.isEmpty()) {
					buf.append(path.toOSString());
				} else {
					buf.append(NewWizardMessages.CPVariableElementLabelProvider_empty); 
				}
			}
			return buf.toString();
		}		
		
		
		return super.getText(element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
	 */
	public Color getForeground(Object element) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
	 */
	public Color getBackground(Object element) {
		if (element instanceof CPVariableElement) {
			CPVariableElement curr= (CPVariableElement) element;
			if (!fShowResolvedVariables && curr.isReserved()) {
				if (fResolvedBackground == null) {
					Display display= Display.getCurrent();
					fResolvedBackground= display.getSystemColor(SWT.COLOR_INFO_BACKGROUND);
				}
				return fResolvedBackground;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		super.dispose();
	}

}
