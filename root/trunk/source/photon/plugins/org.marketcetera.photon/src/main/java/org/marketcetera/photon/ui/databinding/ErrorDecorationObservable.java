package org.marketcetera.photon.ui.databinding;

import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;

/**
 * This observable value will show or hide error and warning images
 * using a ControlDecoration, based on the severity of the IStatus input.
 * 
 * When {@link #setValue(Object)} is called with an IStatus with a severity
 * of {@link IStatus#ERROR}, the appropriate image will be shown.  An IStatus
 * with severity {@link IStatus#OK} will hide the image.  All other severities
 * will cause a warning image to be shown.
 * 
 * This observable assumes that the {@link ControlDecoration} has been stored in
 * the data hash of the control itself at {@link #CONTROL_DECORATOR_KEY}.
 * 
 * @see Control#setData(String, Object)
 * @see Control#getData(String)
 * 
 * @author gmiller
 *
 */
public class ErrorDecorationObservable extends AbstractObservableValue {

	private static final String CONTROL_DECORATOR_KEY = "CONTROL_DECORATOR_KEY"; //$NON-NLS-1$

	public static final String CONTROL_DEFAULT_COLOR = "CONTROL_DEFAULT_COLOR"; //$NON-NLS-1$

	private final Control whichControl;

	private final Image errorImage;

	private final Image warningImage;


	public ErrorDecorationObservable(Control whichControl, Image errorImage, Image warningImage) {
		this.whichControl = whichControl;
		this.errorImage = errorImage;
		this.warningImage = warningImage;
	}

	@Override
	protected Object doGetValue() {
		return null;
	}
	
	@Override
	protected void doSetValue(Object value) {
		IStatus status = (IStatus)value;
		if (status == null){
			status = Status.OK_STATUS;
		}
		showErrorForControl(whichControl, status.getSeverity(), status.getMessage());
	}
	
	/**
	 * Shows an error image for the given control, or clears it depending on the value
	 * of severity.  Message is used as the mouse-over text.
	 * @param aControl the control to decorate
	 * @param severity the severity, one of the {@link IStatus} constants
	 * @param message the human readable reason
	 */
	public void showErrorForControl(Control aControl, int severity,
			String message) {
		Object cd;
		if (((cd = aControl.getData(CONTROL_DECORATOR_KEY)) != null)
				&& cd instanceof ControlDecoration) {
			ControlDecoration controlDecoration = ((ControlDecoration) cd);
			if (severity == IStatus.OK) {
				controlDecoration.hide();
			} else {
				if (severity == IStatus.ERROR) {
					controlDecoration.setImage(errorImage);
				} else {
					controlDecoration.setImage(warningImage);
				}
				if (message != null) {
					controlDecoration.setDescriptionText(message);
				}
				controlDecoration.show();
			}
		}
	}

	public Object getValueType() {
		return IStatus.class;
	}

}
