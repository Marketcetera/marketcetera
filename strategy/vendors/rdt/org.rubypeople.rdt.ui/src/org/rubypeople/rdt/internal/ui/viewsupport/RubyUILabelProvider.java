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

import java.util.ArrayList;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.rubypeople.rdt.ui.RubyElementLabels;


public class RubyUILabelProvider implements ILabelProvider, IColorProvider {
    
    protected ListenerList fListeners = new ListenerList(1);
    
    protected RubyElementImageProvider fImageLabelProvider;
    protected StorageLabelProvider fStorageLabelProvider;
    
    private ArrayList fLabelDecorators;

    private int fImageFlags;
    private long fTextFlags;

    /**
     * Creates a new label provider with default flags.
     */
    public RubyUILabelProvider() {
        this(RubyElementLabels.ALL_DEFAULT, RubyElementImageProvider.OVERLAY_ICONS);
    }

    /**
     * @param textFlags Flags defined in <code>RubyElementLabels</code>.
     * @param imageFlags Flags defined in <code>RubyElementImageProvider</code>.
     */
    public RubyUILabelProvider(long textFlags, int imageFlags) {
        fImageLabelProvider= new RubyElementImageProvider();
        fLabelDecorators= null; 
        
        fStorageLabelProvider= new StorageLabelProvider();
        fImageFlags= imageFlags;
        fTextFlags= textFlags;
    }
    
    /**
     * Adds a decorator to the label provider
     */
    public void addLabelDecorator(ILabelDecorator decorator) {
        if (fLabelDecorators == null) {
            fLabelDecorators= new ArrayList(2);
        }
        fLabelDecorators.add(decorator);
    }
    
    /**
     * Sets the textFlags.
     * @param textFlags The textFlags to set
     */
    public final void setTextFlags(long textFlags) {
        fTextFlags= textFlags;
    }

    /**
     * Sets the imageFlags 
     * @param imageFlags The imageFlags to set
     */
    public final void setImageFlags(int imageFlags) {
        fImageFlags= imageFlags;
    }
    
    /**
     * Gets the image flags.
     * Can be overwriten by super classes.
     * @return Returns a int
     */
    public final int getImageFlags() {
        return fImageFlags;
    }

    /**
     * Gets the text flags.
     * @return Returns a int
     */
    public final long getTextFlags() {
        return fTextFlags;
    }
    
    /**
     * Evaluates the image flags for a element.
     * Can be overwriten by super classes.
     * @return Returns a int
     */
    protected int evaluateImageFlags(Object element) {
        return getImageFlags();
    }

    /**
     * Evaluates the text flags for a element. Can be overwriten by super classes.
     * @return Returns a int
     */
    protected long evaluateTextFlags(Object element) {
        return getTextFlags();
    }
    
    protected Image decorateImage(Image image, Object element) {
        if (fLabelDecorators != null && image != null) {
            for (int i= 0; i < fLabelDecorators.size(); i++) {
                ILabelDecorator decorator= (ILabelDecorator) fLabelDecorators.get(i);
                image= decorator.decorateImage(image, element);
            }
        }
        return image;
    }

    /* (non-Javadoc)
     * @see ILabelProvider#getImage
     */
    public Image getImage(Object element) {
        Image result= fImageLabelProvider.getImageLabel(element, evaluateImageFlags(element));
        if (result == null && (element instanceof IStorage)) {
            result= fStorageLabelProvider.getImage(element);
        }
        
        return decorateImage(result, element);
    }

    protected String decorateText(String text, Object element) {
        if (fLabelDecorators != null && text.length() > 0) {
            for (int i= 0; i < fLabelDecorators.size(); i++) {
                ILabelDecorator decorator= (ILabelDecorator) fLabelDecorators.get(i);
                text= decorator.decorateText(text, element);
            }
        }   
        return text;
    }


    /* (non-Javadoc)
     * @see ILabelProvider#getText
     */
    public String getText(Object element) {
        String result= RubyElementLabels.getTextLabel(element, evaluateTextFlags(element));
        if (result.length() == 0 && (element instanceof IStorage)) {
            result= fStorageLabelProvider.getText(element);
        }
        
        return decorateText(result, element);
    }

    /* (non-Javadoc)
     * @see IBaseLabelProvider#dispose
     */
    public void dispose() {
        if (fLabelDecorators != null) {
            for (int i= 0; i < fLabelDecorators.size(); i++) {
                ILabelDecorator decorator= (ILabelDecorator) fLabelDecorators.get(i);
                decorator.dispose();
            }
            fLabelDecorators= null;
        }
        fStorageLabelProvider.dispose();
        fImageLabelProvider.dispose();
    }
    
    /* (non-Javadoc)
     * @see IBaseLabelProvider#addListener(ILabelProviderListener)
     */
    public void addListener(ILabelProviderListener listener) {
        if (fLabelDecorators != null) {
            for (int i= 0; i < fLabelDecorators.size(); i++) {
                ILabelDecorator decorator= (ILabelDecorator) fLabelDecorators.get(i);
                decorator.addListener(listener);
            }
        }
        fListeners.add(listener);   
    }

    /* (non-Javadoc)
     * @see IBaseLabelProvider#isLabelProperty(Object, String)
     */
    public boolean isLabelProperty(Object element, String property) {
        return true;    
    }

    /* (non-Javadoc)
     * @see IBaseLabelProvider#removeListener(ILabelProviderListener)
     */
    public void removeListener(ILabelProviderListener listener) {
        if (fLabelDecorators != null) {
            for (int i= 0; i < fLabelDecorators.size(); i++) {
                ILabelDecorator decorator= (ILabelDecorator) fLabelDecorators.get(i);
                decorator.removeListener(listener);
            }
        }
        fListeners.remove(listener);    
    }
    
    public static ILabelDecorator[] getDecorators(boolean errortick, ILabelDecorator extra) {
        if (errortick) {
            if (extra == null) {
                return new ILabelDecorator[] {};
            } else {
                return new ILabelDecorator[] { extra };
            }
        }
        if (extra != null) {
            return new ILabelDecorator[] { extra };
        }
        return null;
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
        return null;
    }
    
    /**
     * Fires a label provider changed event to all registered listeners
     * Only listeners registered at the time this method is called are notified.
     *
     * @param event a label provider changed event
     *
     * @see ILabelProviderListener#labelProviderChanged
     */
    protected void fireLabelProviderChanged(final LabelProviderChangedEvent event) {
        Object[] listeners = fListeners.getListeners();
        for (int i = 0; i < listeners.length; ++i) {
            final ILabelProviderListener l = (ILabelProviderListener) listeners[i];
            Platform.run(new SafeRunnable() {
                public void run() {
                    l.labelProviderChanged(event);
                }
            });
        }
    }

}
