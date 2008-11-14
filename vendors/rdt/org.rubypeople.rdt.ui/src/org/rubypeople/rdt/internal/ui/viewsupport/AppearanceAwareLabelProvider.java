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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.RubyElementLabels;

/**
 * RubyUILabelProvider that respects settings from the Appearance preference page.
 * Triggers a viewer update when a preference changes.
 */
public class AppearanceAwareLabelProvider extends RubyUILabelProvider implements IPropertyChangeListener {

    public final static long DEFAULT_TEXTFLAGS= RubyElementLabels.M_PARAMETER_NAMES | RubyElementLabels.ROOT_VARIABLE | RubyElementLabels.REFERENCED_ROOT_POST_QUALIFIED;
    public final static int DEFAULT_IMAGEFLAGS= RubyElementImageProvider.OVERLAY_ICONS;
    
    private long fTextFlagMask;
    private int fImageFlagMask;

    /**
     * Constructor for AppearanceAwareLabelProvider.
     */
    public AppearanceAwareLabelProvider(long textFlags, int imageFlags) {
        super(textFlags, imageFlags);
        initMasks();
        PreferenceConstants.getPreferenceStore().addPropertyChangeListener(this);
    }

    /**
     * Creates a labelProvider with DEFAULT_TEXTFLAGS and DEFAULT_IMAGEFLAGS
     */ 
    public AppearanceAwareLabelProvider() {
        this(DEFAULT_TEXTFLAGS, DEFAULT_IMAGEFLAGS);
    }
    
    private void initMasks() {
        IPreferenceStore store= PreferenceConstants.getPreferenceStore();
        fTextFlagMask= -1;
        if (!store.getBoolean(PreferenceConstants.APPEARANCE_COMPRESS_PACKAGE_NAMES)) {
            fTextFlagMask ^= RubyElementLabels.P_COMPRESSED;
        }
        
        fImageFlagMask= -1;
    }

    /*
     * @see IPropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent event) {
        String property= event.getProperty();
        if (property.equals(PreferenceConstants.APPEARANCE_PKG_NAME_PATTERN_FOR_PKG_VIEW)
                || property.equals(PreferenceConstants.APPEARANCE_COMPRESS_PACKAGE_NAMES)) {
            initMasks();
            LabelProviderChangedEvent lpEvent= new LabelProviderChangedEvent(this, null); // refresh all
            fireLabelProviderChanged(lpEvent);
        }       
    }

    /*
     * @see IBaseLabelProvider#dispose()
     */
    public void dispose() {
        PreferenceConstants.getPreferenceStore().removePropertyChangeListener(this);
        super.dispose();
    }

    /*
     * @see RubyUILabelProvider#evaluateImageFlags()
     */
    protected int evaluateImageFlags(Object element) {
        return getImageFlags() & fImageFlagMask;
    }

    /*
     * @see RubyUILabelProvider#evaluateTextFlags()
     */
    protected long evaluateTextFlags(Object element) {
        return getTextFlags() & fTextFlagMask;
    }

}
