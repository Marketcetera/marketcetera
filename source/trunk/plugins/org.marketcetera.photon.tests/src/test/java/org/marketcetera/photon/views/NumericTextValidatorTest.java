package org.marketcetera.photon.views;

import junit.framework.TestCase;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.photon.ui.NumericTextValidator;
import org.marketcetera.photon.ui.ValidationException;

public class NumericTextValidatorTest extends TestCase {

	public void testValidate() throws ValidationException {
						
		assertNull(NumericTextValidator.validateString("0", true, true, true, true));
		assertNull(NumericTextValidator.validateString("0", true, false, true, true));

		assertNull(NumericTextValidator.validateString("0.0", true, true, true, true));
			assertNotNull(NumericTextValidator.validateString("0.0", false, true, true, true));

		assertNull(NumericTextValidator.validateString("0.01", true, true, true, true));
			assertNotNull(NumericTextValidator.validateString("0.01", false, true, true, true));

		assertNull(NumericTextValidator.validateString("-0", true, true, true, true));
		assertNull(NumericTextValidator.validateString("-0", true, false, true, true));

		assertNull(NumericTextValidator.validateString("-0.0", true, true, true, true));
		assertNull(NumericTextValidator.validateString("-0.0", true, false, true, true));

		assertNull(NumericTextValidator.validateString("-0.01", true, true, true, true));
			assertNotNull(NumericTextValidator.validateString("-0.01", false, true, true, true));

		assertNull(NumericTextValidator.validateString("123", true, true, true, true));
		assertNull(NumericTextValidator.validateString("123", true, true, true, true));

		assertNull(NumericTextValidator.validateString("-123", true, true, true, true));
		assertNull(NumericTextValidator.validateString("-123", true, true, true, true));

		assertNull(NumericTextValidator.validateString("45.6e4", true, true, true, true));
			assertNotNull(NumericTextValidator.validateString("45.6e4", true, true, false, true));

		assertNull(NumericTextValidator.validateString("-45.6E4", true, true, true, true));
			assertNotNull(NumericTextValidator.validateString("-45.6E4", true, true, false, true));
		
		assertNull(NumericTextValidator.validateString(null, true, true, true, true));
			assertNotNull(NumericTextValidator.validateString(null, true, true, true, false));

		assertNull(NumericTextValidator.validateString("", true, true, true, true));
			assertNotNull(NumericTextValidator.validateString("", true, true, true, false));
	}
}


