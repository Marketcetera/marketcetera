package org.marketcetera.photon.ui;

import java.util.EnumMap;
import java.util.Map;

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
 * Status image widget for the status line.
 * 
 * @author gmiller
 * @author andrei@lissovski.org
 */
public class StatusImageTrimWidget extends AbstractWorkbenchTrimWidget implements IExecutableExtension {

	private Composite composite;
	private Label imageLabel;

	private Image nullStatusImage;
	
	private EnumMap<FeedStatus, Image> statusImageMap = new EnumMap<FeedStatus, Image>(
			FeedStatus.class);
	private String serviceName;
	private ServiceTracker serviceTracker;
	private char idChar;
	private String name;
	private String feedID;


	public StatusImageTrimWidget() {
	}

	@Override
	public void init(IWorkbenchWindow workbenchWindow) {
		super.init(workbenchWindow);

		ImageDescriptor overlayDescriptor = PhotonPlugin.getImageDescriptor("icons/overlay/"+idChar+"-template.gif");

		for (FeedStatus aStatus : FeedStatus.values()) {
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

				Image newImage = createImageWithOverlay(descriptor, overlayDescriptor);
				statusImageMap.put(aStatus, newImage);
		}		
		ImageDescriptor nullDescriptor = PhotonPlugin.getImageDescriptor(IImageKeys.STATUS_OFFLINE);
		nullStatusImage = createImageWithOverlay(nullDescriptor, overlayDescriptor);

		serviceTracker = new StatusLineServiceTracker(PhotonPlugin.getDefault().getBundleContext(), serviceName, null);
		serviceTracker.open();
	}

	private Image createImageWithOverlay(ImageDescriptor baseDescriptor, ImageDescriptor overlayDescriptor) {
		ImageDescriptor[][] overlayDescriptors = new ImageDescriptor[2][2];
		overlayDescriptors[1][1] = overlayDescriptor;
		Image baseImage = null;
	
		baseImage = baseDescriptor.createImage();
		ImageOverlayIcon overlay = new ImageOverlayIcon(baseImage, 
				overlayDescriptors
				);
		Image toReturn = overlay.createImage();
		baseImage.dispose();

		return toReturn;
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

		updateStatus();
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

	protected void serviceChanged(Object service) {
		FeedStatus theStatus = getServiceStatus(service);
		updateStatus(theStatus);
	}

	private FeedStatus getServiceStatus(Object service) {
		FeedStatus theStatus = FeedStatus.UNKNOWN;
		if (service != null && service instanceof IFeedComponent){
			IFeedComponent feedComponent = ((IFeedComponent)service);
			feedID = feedComponent.getID();
			theStatus = feedComponent.getFeedStatus();
		}
		return theStatus;
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
		return theImage;
	}

	private void updateStatus() {
		FeedStatus feedStatus = getServiceStatus(serviceTracker.getService());
		updateStatus(feedStatus);
	}
	
	private void updateStatus(final FeedStatus aStatus) {
		if (imageLabel == null)
			return;

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!imageLabel.isDisposed()){
					imageLabel.setImage(getStatusImage(aStatus));
					String statusString = aStatus == null ? FeedStatus.UNKNOWN.name() : aStatus.name();
					String nameString = feedID==null ? " " : " \""+ feedID +"\" ";
					imageLabel.setToolTipText(name + nameString + statusString);
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
		if ("class".equals(propertyName) && data != null && data instanceof Map){
			Map<String, String> dataMap = (Map<String, String>) data;
			for (String aKey : dataMap.keySet()) {
				if ("service".equals(aKey)){
					setServiceName(dataMap.get(aKey));
				} else if ("idChar".equals(aKey)){
					idChar = dataMap.get(aKey).toLowerCase().charAt(0);
				} else if ("name".equals(aKey)){
					name = dataMap.get(aKey);
				}
			}
		}
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

}
