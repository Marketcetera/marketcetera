package org.marketcetera.photon.ui.databinding;

import org.eclipse.swt.widgets.Label;
import org.marketcetera.photon.views.SWTTestView;
import org.marketcetera.photon.views.ViewTestBase;

public class LabelBooleanImageObservableValueTest extends ViewTestBase {

	public LabelBooleanImageObservableValueTest(String name) 
	{
		super(name);
	}

	/**
	 * Test that the getter and setter on the observable value both work.
	 * @throws Exception
	 */
	public void testFormTextObservableValue() throws Exception {
		SWTTestView view = (SWTTestView) getTestView();
		Label imageLabel = view.getImageLabel();
		LabelBooleanImageObservableValue observable = new LabelBooleanImageObservableValue(
				imageLabel, 
				view.getTrueImage(),
				view.getFalseImage());
		assertNull(imageLabel.getImage());
		observable.setValue(true);
		assertSame(view.getTrueImage(), imageLabel.getImage());
		observable.setValue(false);
		assertSame(view.getFalseImage(), imageLabel.getImage());
		observable.setValue(null);
		assertNull(imageLabel.getImage());
	}

	@Override
	protected String getViewID() {
		return SWTTestView.ID;
	}
}
