package org.marketcetera.photon.ui.databinding;

import org.marketcetera.photon.views.SWTTestView;
import org.marketcetera.photon.views.ViewTestBase;


public class FormTextObservableValueTest extends ViewTestBase {

	public FormTextObservableValueTest(String name) {
		super(name);
	}

	/**
	 * Test that the getter and setter on the observable value both work.
	 * @throws Exception
	 */
	public void testFormTextObservableValue() throws Exception {
		SWTTestView view = (SWTTestView) getTestView();
		FormTextObservableValue observable = new FormTextObservableValue(view.getForm());
		assertEquals("Initial text", observable.getValue());
		assertEquals(String.class, observable.getValueType());
		observable.setValue("New title");
		assertEquals("New title", view.getForm().getText());
		
		
	}

	@Override
	protected String getViewID() {
		return SWTTestView.ID;
	}
}
