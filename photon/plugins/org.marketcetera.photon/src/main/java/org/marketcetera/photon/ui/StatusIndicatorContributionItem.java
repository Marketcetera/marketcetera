package org.marketcetera.photon.ui;

import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;
import org.eclipse.update.internal.ui.parts.ImageOverlayIcon;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonImages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Contribution item for a status indicator. It supports three status: on, off,
 * and error.
 * <p>
 * It is added via the <code>org.eclipse.ui.menus</code> extension point. Images
 * can be overlayed with a custom image, the path of which is also provided by
 * the extension point.
 * <p>
 * The Eclipse internal {@link ImageOverlayIcon} class is used intentionally to
 * avoid duplicating code.
 * <p>
 * TODO: test to verify this continues to work as expected.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: StatusIndicatorContributionItem.java 10782 2009-09-30 20:11:02Z
 *          will $
 * @since 1.0.0
 */
@SuppressWarnings("restriction")
@ClassVersion("$Id$")//$NON-NLS-1$
public abstract class StatusIndicatorContributionItem extends
		WorkbenchWindowControlContribution implements IExecutableExtension {

	private static final String OVERLAY_KEY = "overlay"; //$NON-NLS-1$

	private Image mOnImage;
	private Image mOffImage;
	private Image mErrorImage;
	private boolean mOverlays;

	/**
	 * Returns the image for the "on" status.
	 * 
	 * @return the image indicating an "on" status
	 */
	public Image getOnImage() {
		return mOnImage;
	}

	/**
	 * Returns the image for the "off" status.
	 * 
	 * @return the image indicating an "off" status
	 */
	public Image getOffImage() {
		return mOffImage;
	}

	/**
	 * Returns the image for the "error" status.
	 * 
	 * @return the image indicating an "error" status
	 */
	public Image getErrorImage() {
		return mErrorImage;
	}
	
	/**
	 * Subclasses can override to provide additional cleanup, but must be sure
	 * to call this method as well.
	 */
	@Override
	public void dispose() {
		// Dispose custom images
		if (mOverlays) {
			if (mOnImage != null)
				mOnImage.dispose();
			if (mOffImage != null)
				mOffImage.dispose();
			if (mErrorImage != null)
				mErrorImage.dispose();
		}
	}

	/**
	 * Subclasses may override to retrieve additional configuration, but must be sure to
	 * call this {@link StatusIndicatorContributionItem} implementation.
	 * 
	 * @see IExecutableExtension#setInitializationData(IConfigurationElement, String, Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		if (data instanceof Map) {
			Map<String, String> dataMap = (Map<String, String>) data;
			for (String key : dataMap.keySet()) {
				if (OVERLAY_KEY.equals(key)) {
					createImages(dataMap.get(key));
					break;
				}
			}
		}
	}

	private void createImages(String overlay) {
		ImageRegistry imageRegistry = PhotonPlugin.getDefault()
				.getImageRegistry();
		Image greenImage = imageRegistry.get(PhotonImages.GREEN_LED);
		Image grayImage = imageRegistry.get(PhotonImages.GRAY_LED);
		Image redImage = imageRegistry.get(PhotonImages.RED_LED);
		if (overlay != null) {
			ImageDescriptor overlayDescriptor = PhotonPlugin
					.getImageDescriptor(overlay);
			if (overlayDescriptor != null) {
				try {
					ImageDescriptor[][] overlays = new ImageDescriptor[][] {
							null, new ImageDescriptor[] { overlayDescriptor } };
					mOnImage = new ImageOverlayIcon(greenImage, overlays,
							getSize(greenImage)).createImage();
					mOffImage = new ImageOverlayIcon(grayImage, overlays,
							getSize(grayImage)).createImage();
					mErrorImage = new ImageOverlayIcon(redImage, overlays,
							getSize(redImage)).createImage();
					mOverlays = true;
					return;

				} catch (Exception e) {
					// fall through to defaults (without overlay)
					Messages.STATUS_INDICATOR_OVERLAY_ERROR.error(this, e,
							overlay);
				}
			}
		}
		mOnImage = greenImage;
		mOffImage = grayImage;
		mErrorImage = redImage;
	}

	private Point getSize(Image greenImage) {
		Rectangle r = greenImage.getBounds();
		return new Point(r.width, r.height);
	}

}
