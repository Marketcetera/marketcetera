package org.marketcetera.photon.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.PhotonPlugin;

public class SWTTestView extends ViewPart {

	public static final String ID = "org.marketcetera.photon.views.SWTTestView";
	
	private Combo combo;

	private Text text;

	private ScrolledForm form;

	private Image trueImage;

	private Image falseImage;

	private Label imageLabel;

	@Override
	public void createPartControl(Composite parent) {
		form = new ScrolledForm(parent, SWT.NONE);
		form.setText("Initial text");
		combo = new Combo(form.getBody(), SWT.NONE);
		text = new Text(form.getBody(), SWT.NONE);
		
		trueImage = PhotonPlugin.getImageDescriptor(IImageKeys.ARROW_UP).createImage();
		falseImage = PhotonPlugin.getImageDescriptor(IImageKeys.ARROW_DOWN).createImage();
		imageLabel = new Label(form.getBody(), SWT.NONE);
	}

	@Override
	public void setFocus() {

	}

	public Combo getCombo() {
		return combo;
	}

	public Text getText() {
		return text;
	}
	
	public ScrolledForm getForm() {
		return form;
	}
	
	public Image getTrueImage() {
		return trueImage;
	}
	
	public Image getFalseImage() {
		return falseImage;
	}

	public Label getImageLabel() {
		return imageLabel;
	}
}

