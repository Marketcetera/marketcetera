package org.marketcetera.photon.ui.databinding;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.jface.internal.databinding.provisional.swt.AbstractSWTObservableValue;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * Observable that knows how to get and set the text of a ScrolledForm
 * This class will not generate events if the text is changed "by hand"
 * out from under it.
 */
public class FormTextObservableValue extends AbstractSWTObservableValue {

	private final ScrolledForm form;

	/**
	 * @param form
	 */
	public FormTextObservableValue(ScrolledForm form) {
		super(form);
		this.form = form;
	}

	public void doSetValue(final Object value) {
		String oldValue = form.getText();
		form.setText(value == null ? "" : value.toString()); //$NON-NLS-1$
		fireValueChange(Diffs.createValueDiff(oldValue, form.getText()));
	}

	public Object doGetValue() {
		return form.getText();
	}

	public Object getValueType() {
		return String.class;
	}

}
