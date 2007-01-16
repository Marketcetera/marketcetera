package org.marketcetera.photon.ui;

import java.util.EnumMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.menus.AbstractWorkbenchTrimWidget;
import org.marketcetera.core.IFeedComponent;
import org.marketcetera.core.IFeedComponent.FeedStatus;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.PhotonPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;


/**
 * Bare-bones new status image widget for the status line.
 * 
 * @author andrei@lissovski.org
 */
public class StatusImageTrimWidget extends AbstractWorkbenchTrimWidget implements IExecutableExtension {

	private Composite composite;
	private Label imageLabel;

	private Image nullStatusImage;
	
	private static EnumMap<FeedStatus, Image> statusImageMap = new EnumMap<FeedStatus, Image>(
			FeedStatus.class);
	private String serviceName;
	private ServiceTracker serviceTracker;

	/**
	 * Create a new FeedStatusLineContribution with the given id.  The number of status
	 * indicators is determined by the size of the pFeedNames array parameter.
	 * 
	 */
	public StatusImageTrimWidget() {

		ImageDescriptor descriptor = PhotonPlugin.getImageDescriptor(IImageKeys.STATUS_OFFLINE);
		nullStatusImage = descriptor.createImage();
	}

	@Override
	public void init(IWorkbenchWindow workbenchWindow) {
		super.init(workbenchWindow);

		serviceTracker = new StatusLineServiceTracker(PhotonPlugin.getDefault().getBundleContext(), serviceName, null);
		serviceTracker.open();

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.menus.AbstractTrimWidget#fill(org.eclipse.swt.widgets.Composite, int, int)
	 */
	public void fill(Composite parent, int oldSide, int newSide) {
		
		composite = new Composite(parent, SWT.NONE);
		
		FillLayout layout = new FillLayout();
		layout.marginHeight = 4;
		layout.marginWidth  = 2;
		composite.setLayout(layout);
		
		imageLabel = new Label(composite, SWT.NONE);
		imageLabel.setImage(nullStatusImage);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.menus.AbstractTrimWidget#dispose()
	 */
	public void dispose() {
		if (composite != null && !composite.isDisposed())
			composite.dispose();
		composite = null;

	}
	
	/**
	 * I know somebody is going to get mad at me for putting
	 * stuff in the finalize() method, but given that fill() and
	 * dispose() get called repeatedly, I'm not sure where else to put
	 * this stuff...
	 */
	@Override
	protected void finalize() throws Throwable {
		if (serviceTracker != null){
			serviceTracker.close();
		}
		
		if (nullStatusImage != null && !nullStatusImage.isDisposed())
			nullStatusImage.dispose();
		nullStatusImage = null;
		for (Image image : statusImageMap.values()) {
			image.dispose();
		}
		super.finalize();
	}

	protected void serviceChanged(Object feed) {
		FeedStatus theStatus = FeedStatus.UNKNOWN;
		if (feed != null && feed instanceof IFeedComponent){
			theStatus = ((IFeedComponent)feed).getFeedStatus();
		}
		setStatus(theStatus);
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


	private final class StatusLineServiceTracker extends ServiceTracker {
		private StatusLineServiceTracker(BundleContext context, String clazz, ServiceTrackerCustomizer customizer) {
			super(context, clazz, customizer);
		}

		@Override
		public Object addingService(ServiceReference reference) {
			Object service = super.addingService(reference);
			serviceChanged(service);
			return service;
		}

		@Override
		public void modifiedService(ServiceReference reference, Object service) {
			super.modifiedService(reference, service);
			serviceChanged(service);
		}

		@Override
		public void removedService(ServiceReference reference, Object service) {
			super.removedService(reference, service);
			serviceChanged(service);
		}
	}


	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		String dataString = data.toString();
		setServiceName(dataString);
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;

	}

}
