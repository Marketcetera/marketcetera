package org.rubypeople.rdt.internal.ui.dialogs;

import org.eclipse.core.runtime.IStatus;

public interface ISelectionValidator {

	IStatus validate(Object[] selection);

}