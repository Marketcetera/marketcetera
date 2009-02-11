package org.marketcetera.photon.ui.databinding;

import org.eclipse.jface.internal.databinding.provisional.swt.AbstractSWTObservableValue;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Label;

/**
 * This observable value sets the image shown on the given label,
 * based on whether the input is boolean true or false.  
 * 
 * @author gmiller
 *
 */
public class LabelBooleanImageObservableValue extends AbstractSWTObservableValue {

	private final Image trueImage;
	private final Image falseImage;
	private Boolean current;
	
	public LabelBooleanImageObservableValue(Label label, Image trueImage, Image falseImage) {
		super(label);
		this.trueImage = trueImage;
		this.falseImage = falseImage;
	}

	@Override
	public Object getValueType() {
		return Boolean.class;
	}

	@Override
	protected Object doGetValue() {
		return current;
	}

	@Override
	protected void doSetValue(Object value) {
		Boolean bValue = (Boolean) value;
		current = bValue;
		if (bValue == null){
			setImage(null);
		} else if (bValue){
			setImage(trueImage);
		} else {
			setImage(falseImage);
		}
	}

	private void setImage(Image theImage) {
		((Label)getWidget()).setImage(theImage);
	}
}
