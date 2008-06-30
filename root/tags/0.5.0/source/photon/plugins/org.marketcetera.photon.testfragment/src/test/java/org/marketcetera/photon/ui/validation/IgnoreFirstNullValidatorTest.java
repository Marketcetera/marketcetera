package org.marketcetera.photon.ui.validation;

import junit.framework.TestCase;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.internal.ide.StatusUtil;

public class IgnoreFirstNullValidatorTest extends TestCase {

	public class ErrorValidator implements IValidator {

		public IStatus validate(Object value) {
			return StatusUtil.newStatus(IStatus.ERROR, "", new Exception());
		}

	}

	public void testIgnoresFirstNull() throws Exception {
		IgnoreFirstNullValidator ifnv = new IgnoreFirstNullValidator(new ErrorValidator());
		
		assertEquals(IStatus.OK, ifnv.validate(null).getSeverity());
		assertEquals(IStatus.ERROR, ifnv.validate(null).getSeverity());
	}
	
	public void testNoFirstNull(){
		IgnoreFirstNullValidator ifnv = new IgnoreFirstNullValidator(new ErrorValidator());
		
		assertEquals(IStatus.ERROR, ifnv.validate("asdf").getSeverity());
		assertEquals(IStatus.ERROR, ifnv.validate("asdf").getSeverity());
		
	}
}
