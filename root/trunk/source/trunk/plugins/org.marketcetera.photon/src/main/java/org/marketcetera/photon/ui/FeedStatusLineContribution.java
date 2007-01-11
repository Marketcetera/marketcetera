package org.marketcetera.photon.ui;

import java.util.EnumMap;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.IFeedComponent;
import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.core.IFeedComponent.FeedStatus;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.PhotonPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * The FeedStatusLineContribution manifests as an array of LED-like images at the
 * bottom of the application window that indicate the status of one or more of
 * the "feed" connections.  This could be anything from a market data feed to
 * a JMS connection to a database connection.  The images reflect status by
 * changing color in response to a call to {@link #setStatus(String, FeedStatus)}
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public class FeedStatusLineContribution extends ContributionItem implements
		IFeedComponentListener {


	private final class StatusLineServiceTracker extends ServiceTracker {
		private StatusLineServiceTracker(BundleContext context, String clazz, ServiceTrackerCustomizer customizer) {
			super(context, clazz, customizer);
		}

		@Override
		public Object addingService(ServiceReference reference) {
			Object service = context.getService(reference);
			serviceChanged(service);
			return service;
		}

		@Override
		public void modifiedService(ServiceReference reference, Object service) {
			serviceChanged(service);
		}

		@Override
		public void removedService(ServiceReference reference, Object service) {
			serviceChanged(service);
		}
	}


	private static EnumMap<FeedStatus, Image> statusImageMap = new EnumMap<FeedStatus, Image>(
			FeedStatus.class);
	private Label imageLabel;
	private String serviceName;
	private Image nullStatusImage;
	private ServiceTracker serviceTracker;

	/**
	 * Create a new FeedStatusLineContribution with the given id.  The number of status
	 * indicators is determined by the size of the pFeedNames array parameter.
	 * 
	 * @param id the id of this status line contribution (used by the RCP)
	 * @param pFeedNames the names of the feeds for which to keep status
	 */
	public FeedStatusLineContribution(String id, String serviceName) {
		super(id);
		this.serviceName = serviceName;

		serviceTracker = new StatusLineServiceTracker(PhotonPlugin.getDefault().getBundleContext(), serviceName, null);
		serviceTracker.open();
		
		ImageDescriptor descriptor = PhotonPlugin.getImageDescriptor(IImageKeys.STATUS_OFFLINE);
		nullStatusImage = descriptor.createImage();
	}

	
	protected void serviceChanged(Object feed) {
		FeedStatus theStatus = FeedStatus.UNKNOWN;
		if (feed != null && feed instanceof IFeedComponent){
			theStatus = ((IFeedComponent)feed).getFeedStatus();
		}
		setStatus(theStatus);
	}


	/**
	 * Puts the correct number of status indicator images into the status line.
	 * 
	 * @see org.eclipse.jface.action.ContributionItem#fill(org.eclipse.swt.widgets.Composite)
	 */
	public void fill(Composite parent) {
		imageLabel = new Label(parent, SWT.NONE);
		imageLabel.setImage(getStatusImage(FeedStatus.OFFLINE));
	}

	/**
	 * Returns the Image associated with the specified {@link FeedStatus} enum.
	 * This caches the images for later retrieval.
	 * 
	 * @param aStatus the status for which to get an image.
	 * @return the image associated with the given status
	 */
	protected Image getStatusImage(FeedStatus aStatus) {
		if (aStatus == null){
			return nullStatusImage;
		}
		Image theImage = statusImageMap.get(aStatus);
		if (theImage == null) {
			ImageDescriptor descriptor;
			switch (aStatus) {
			case AVAILABLE:
				descriptor = PhotonPlugin.getImageDescriptor(IImageKeys.STATUS_AVAILABLE);
				break;
			case ERROR:
				descriptor = PhotonPlugin.getImageDescriptor(IImageKeys.STATUS_ERROR);
				break;

			case OFFLINE:
			case UNKNOWN:
			default:
				descriptor = PhotonPlugin.getImageDescriptor(IImageKeys.STATUS_OFFLINE);
				break;
			}

			theImage = descriptor.createImage();
			statusImageMap.put(aStatus, theImage);
		}
		return theImage;
	}

	public void setStatus(final FeedStatus aStatus) {
		if (imageLabel == null)
			return;
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				if (!imageLabel.isDisposed()){
					imageLabel.setImage(getStatusImage(aStatus));
					String name = aStatus == null ? FeedStatus.UNKNOWN.name() : aStatus.name();
					imageLabel.setToolTipText(serviceName + " " + name);
				}
			}
		});
	}

	/**
	 * Updates the status of the appropriate status indicator based on 
	 * the values found in a changed FeedComponent.
	 * 
	 * @param fc the feed component that has changed
	 * @see org.marketcetera.core.IFeedComponentListener#feedComponentChanged(org.marketcetera.core.IFeedComponent)
	 */
	public void feedComponentChanged(IFeedComponent fc) {
		setStatus(fc.getFeedStatus());
	}


	/**
	 * Disposes of the cached Image objects.
	 * @see org.eclipse.jface.action.ContributionItem#dispose()
	 */
	@Override
	public void dispose() {
		for (Image image : statusImageMap.values()) {
			image.dispose();
		}
		nullStatusImage.dispose();
		serviceTracker.close();
		super.dispose();
	}

}
