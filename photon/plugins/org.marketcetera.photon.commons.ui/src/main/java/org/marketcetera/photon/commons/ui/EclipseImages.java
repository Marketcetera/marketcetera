package org.marketcetera.photon.commons.ui;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.framework.Bundle;

/* $License$ */

/**
 * Provides access to bundle icons using the standard Eclipse conventions.
 * <p>
 * See <a
 * href="http://wiki.eclipse.org/User_Interface_Guidelines#Folder_Structure"
 * >http://wiki.eclipse.org/User_Interface_Guidelines#Folder_Structure</a>
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public enum EclipseImages {
    /**
     * Model objects.
     */
    OBJ("obj16/"), //$NON-NLS-1$

    /**
     * View images.
     */
    VIEW("view16/"), //$NON-NLS-1$

    /**
     * View images.
     */
    WIZBAN("wizban/"); //$NON-NLS-1$

    private static final String ICONS_PATH = "$nl$/icons/"; //$NON-NLS-1$

    private final String mPrefix;

    private EclipseImages(String prefix) {
        mPrefix = prefix;
    }

    /**
     * Provides an image descriptor for the given bundle and image name. The
     * enum instance knows where to find it according to convention.
     * 
     * @param bundleId
     *            the id of the bundle containing the icon
     * @param name
     *            the name of the icon
     * @return an image descriptor
     */
    public ImageDescriptor getImageDescriptor(String bundleId, String name) {
        String path = ICONS_PATH + mPrefix + name;
        Bundle bundle = Platform.getBundle(bundleId);
        if (bundle != null) {
            URL url = FileLocator.find(bundle, new Path(path), null);
            if (url != null) {
                return ImageDescriptor.createFromURL(url);
            }
        }
        return ImageDescriptor.getMissingImageDescriptor();
    }
}