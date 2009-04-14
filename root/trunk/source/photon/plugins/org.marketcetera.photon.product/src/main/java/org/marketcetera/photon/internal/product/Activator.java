package org.marketcetera.photon.internal.product;

import org.marketcetera.photon.positions.ui.IPositionLabelProvider;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/* $License$ */

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		context.registerService(IPositionLabelProvider.class.getName(),
				new PhotonPositionLabelProvider(), null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

}
