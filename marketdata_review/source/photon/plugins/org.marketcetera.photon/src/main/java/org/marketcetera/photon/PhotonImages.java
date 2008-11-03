package org.marketcetera.photon;

import org.eclipse.jface.resource.ImageRegistry;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Class to manage shared images that are held for the lifetime of the plug-in.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public class PhotonImages {
	
	// Shared images
	public static final String GREEN_LED = "GREEN_LED";
	public static final String RED_LED = "RED_LED";
	public static final String GRAY_LED = "GRAY_LED";
	
	static void initializeSharedImages(ImageRegistry reg) {
		reg.put(GREEN_LED, PhotonPlugin.getImageDescriptor("icons/other/greenled.gif"));
		reg.put(RED_LED, PhotonPlugin.getImageDescriptor("icons/other/redled.gif"));
		reg.put(GRAY_LED, PhotonPlugin.getImageDescriptor("icons/other/grayled.gif"));		
	}
	
	
}
