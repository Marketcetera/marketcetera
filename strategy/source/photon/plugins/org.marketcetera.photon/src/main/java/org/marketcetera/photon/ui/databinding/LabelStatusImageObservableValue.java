package org.marketcetera.photon.ui.databinding;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.internal.databinding.provisional.swt.AbstractSWTObservableValue;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Label;

/**
 * This observable value sets the image shown by the given {@link Label}
 * depending on the severity of the {@link IStatus} given as input.
 * 
 * {@link IStatus#ERROR} and {@link IStatus#WARNING} correspond to the 
 * errorImage and warning image passed into the constructor.
 * 
 * @author gmiller
 *
 */
public class LabelStatusImageObservableValue extends AbstractSWTObservableValue {

	private final Image errorImage;
	private final Image warningImage;

	public LabelStatusImageObservableValue(Label label, Image errorImage, Image warningImage) {
		super(label);
		this.errorImage = errorImage;
		this.warningImage = warningImage;
	}

	@Override
	public Object getValueType() {
		return Status.class;
	}

	@Override
	protected Object doGetValue() {
		return null;
	}

	@Override
	protected void doSetValue(Object value) {
		int severity = ((IStatus)value).getSeverity();
		if (severity >= IStatus.ERROR){
			setImage(errorImage);
		} else if (severity >= IStatus.WARNING){
			setImage(warningImage);
		} else {
			setImage(null);
		}
	}

	private void setImage(Image theImage) {
		((Label)getWidget()).setImage(theImage);
	}
}
