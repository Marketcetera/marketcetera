package org.marketcetera.photon.internal.strategy;

import static org.junit.Assert.*;

import org.eclipse.core.runtime.IStatus;
import org.junit.Test;

/* $License$ */

/**
 * Test {@link StrategyValidation}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public class StrategyValidationTest {

	@Test
	public void testValidateDisplayNameNotBlank() {
		IStatus status = StrategyValidation
				.validateDisplayNameNotBlank("MyStrategy");
		assertTrue(status.isOK());
		status = StrategyValidation.validateDisplayNameNotBlank(" ");
		assertEquals(IStatus.ERROR, status.getSeverity());
		assertEquals(Messages.STRATEGY_VALIDATION_REQUIRED_FIELD_BLANK
				.getText(Messages.STRATEGYUI_DISPLAY_NAME_LABEL), status
				.getMessage());
	}

	@Test
	public void testValidateNotBlank() {
		String field = "ABC";
		IStatus status = StrategyValidation
				.validateNotBlank(field, "MyStrategy");
		assertTrue(status.isOK());
		status = StrategyValidation.validateNotBlank(field, " ");
		assertEquals(IStatus.ERROR, status.getSeverity());
		assertEquals(Messages.STRATEGY_VALIDATION_REQUIRED_FIELD_BLANK
				.getText(field), status
				.getMessage());
	}

}
