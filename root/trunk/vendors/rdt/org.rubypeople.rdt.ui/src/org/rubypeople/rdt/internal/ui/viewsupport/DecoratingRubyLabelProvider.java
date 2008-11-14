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

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.ui.ProblemsLabelDecorator;

public class DecoratingRubyLabelProvider extends DecoratingLabelProvider implements IColorProvider {
    
    /**
     * Decorating label provider for Ruby. Combines a RubyUILabelProvider
     * with problem and override indicuator with the workbench decorator (label
     * decorator extension point).
     */
    public DecoratingRubyLabelProvider(RubyUILabelProvider labelProvider) {
        this(labelProvider, true);
    }

    /**
     * Decorating label provider for Ruby. Combines a RubyUILabelProvider
     * (if enabled with problem indicator) with the workbench
     * decorator (label decorator extension point).
     */
    public DecoratingRubyLabelProvider(RubyUILabelProvider labelProvider, boolean errorTick) {
        super(labelProvider, PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());
        if (errorTick) {
            labelProvider.addLabelDecorator(new ProblemsLabelDecorator(null));
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
     */
    public Color getForeground(Object element) {
        // label provider is a RubyUILabelProvider
        return ((IColorProvider) getLabelProvider()).getForeground(element);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
     */
    public Color getBackground(Object element) {
        // label provider is a RubyUILabelProvider
        return ((IColorProvider) getLabelProvider()).getBackground(element);
    }

}
