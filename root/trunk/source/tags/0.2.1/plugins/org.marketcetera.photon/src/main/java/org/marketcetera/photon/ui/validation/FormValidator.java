package org.marketcetera.photon.ui.validation;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Control;

public class FormValidator {

	private final IMessageDisplayer displayer;
	private List<Control> controls = new LinkedList<Control>();

	public FormValidator(IMessageDisplayer displayer) {
		this.displayer = displayer;
	}

	public void register(Control control, boolean validateOnFocusLoss) {
		controls.add(control);
		if (validateOnFocusLoss){
			control.addFocusListener(new FocusListener(){
				public void focusGained(FocusEvent e) {}
				public void focusLost(FocusEvent e) {
					validateAllEditing();
				}
			});
		}
	}

	protected void validateAllEditing(){
		String warning = null;
		List<ValidationException> exceptions = new LinkedList<ValidationException>();
		for (Control aControl : controls) {
			Object controlHighglighterObj = aControl
					.getData(IControlValidator.CONTROL_HIGHLIGHTER_KEY);
			if (controlHighglighterObj != null
					&& controlHighglighterObj instanceof IControlHighlighter) {
				((IControlHighlighter) controlHighglighterObj).clearHighlight();
			}

			Object validatorObj = aControl
					.getData(IControlValidator.VALIDATOR_KEY);
			if (validatorObj != null
					&& validatorObj instanceof IControlValidator) {
				try {
					warning = ((IControlValidator) validatorObj).validateEditing();
				} catch (ValidationException ex) {
					exceptions.add(ex);
				}
			}

			
		}
		updateUI(warning, exceptions);

	}

	private void updateUI(String warning, List<ValidationException> exceptions) {
		if (warning != null) {
			displayer.showWarning(warning);
		} else {
			displayer.clearMessage();
		}
		for (ValidationException ex : exceptions) {
			displayer.showError(ex.getLocalizedMessage());
			Object controlHighglighterObj = ex.getOffendingControl().getData(
					IControlValidator.CONTROL_HIGHLIGHTER_KEY);
			if (controlHighglighterObj != null
					&& controlHighglighterObj instanceof IControlHighlighter) {
				((IControlHighlighter) controlHighglighterObj).highlightError();
			}

		}
	}

	public void validateAll(){
		String warning = null;
		List<ValidationException> exceptions = new LinkedList<ValidationException>();
		for (Control aControl : controls) {
			Object controlHighglighterObj = aControl
					.getData(IControlValidator.CONTROL_HIGHLIGHTER_KEY);
			if (controlHighglighterObj != null
					&& controlHighglighterObj instanceof IControlHighlighter) {
				((IControlHighlighter) controlHighglighterObj).clearHighlight();
			}

			Object validatorObj = aControl
					.getData(IControlValidator.VALIDATOR_KEY);
			if (validatorObj != null
					&& validatorObj instanceof IControlValidator) {
				try {
					warning = ((IControlValidator) validatorObj).validate();
				} catch (ValidationException ex) {
					exceptions.add(ex);
				}
			}
		}
		updateUI(warning, exceptions);
	}
}
