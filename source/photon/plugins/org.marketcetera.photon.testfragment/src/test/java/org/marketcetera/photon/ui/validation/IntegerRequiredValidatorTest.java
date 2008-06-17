package org.marketcetera.photon.ui.validation;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IStatus;
import org.marketcetera.core.ExpectedTestFailure;

public class IntegerRequiredValidatorTest extends TestCase {

	public void testValidate() {
		final IntegerRequiredValidator validator = new IntegerRequiredValidator();
		assertEquals(IStatus.ERROR, validator.validate(null).getSeverity());
		assertEquals(IStatus.OK, validator.validate("12345").getSeverity());
		assertEquals(IStatus.OK, validator.validate("-12345").getSeverity());
		assertEquals(IStatus.OK, validator.validate("-0").getSeverity());
		assertEquals(IStatus.ERROR, validator.validate("3.14159").getSeverity());
		new ExpectedTestFailure(IllegalArgumentException.class){
			@Override
			protected void execute() throws Throwable {
				validator.validate(new Boolean(false));
			}
		}.run();
	}

}
