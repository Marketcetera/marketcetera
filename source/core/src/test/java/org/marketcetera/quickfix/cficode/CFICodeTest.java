package org.marketcetera.quickfix.cficode;

import junit.framework.TestCase;

import org.marketcetera.core.ExpectedTestFailure;

public class CFICodeTest extends TestCase {

	public void testOptionCFICode() throws Exception {
		OptionCFICode code;
		code = new OptionCFICode("OCASCS"); //$NON-NLS-1$
		checkOptionCode(code, CFICode.CATEGORY_OPTION, OptionCFICode.TYPE_CALL, OptionCFICode.EXERCISE_AMERICAN, OptionCFICode.UNDERLYING_STOCK, OptionCFICode.DELIVERY_CASH, OptionCFICode.STANDARD_STANDARD);
		code = new OptionCFICode("OPEIPN"); //$NON-NLS-1$
		checkOptionCode(code, CFICode.CATEGORY_OPTION, OptionCFICode.TYPE_PUT, OptionCFICode.EXERCISE_EUROPEAN, OptionCFICode.UNDERLYING_INDEX, OptionCFICode.DELIVERY_PHYSICAL, OptionCFICode.STANDARD_NON_STANDARD);
		code = new OptionCFICode("OCEDCS"); //$NON-NLS-1$
		checkOptionCode(code, CFICode.CATEGORY_OPTION, OptionCFICode.TYPE_CALL, OptionCFICode.EXERCISE_EUROPEAN, OptionCFICode.UNDERLYING_DEBT, OptionCFICode.DELIVERY_CASH, OptionCFICode.STANDARD_STANDARD);
		code = new OptionCFICode("OCECCS"); //$NON-NLS-1$
		checkOptionCode(code, CFICode.CATEGORY_OPTION, OptionCFICode.TYPE_CALL, OptionCFICode.EXERCISE_EUROPEAN, OptionCFICode.UNDERLYING_CURRENCY, OptionCFICode.DELIVERY_CASH, OptionCFICode.STANDARD_STANDARD);
		code = new OptionCFICode("OCEOCS"); //$NON-NLS-1$
		checkOptionCode(code, CFICode.CATEGORY_OPTION, OptionCFICode.TYPE_CALL, OptionCFICode.EXERCISE_EUROPEAN, OptionCFICode.UNDERLYING_OPTION, OptionCFICode.DELIVERY_CASH, OptionCFICode.STANDARD_STANDARD);
		code = new OptionCFICode("OCEFCS"); //$NON-NLS-1$
		checkOptionCode(code, CFICode.CATEGORY_OPTION, OptionCFICode.TYPE_CALL, OptionCFICode.EXERCISE_EUROPEAN, OptionCFICode.UNDERLYING_FUTURE, OptionCFICode.DELIVERY_CASH, OptionCFICode.STANDARD_STANDARD);
		code = new OptionCFICode("OCETCS"); //$NON-NLS-1$
		checkOptionCode(code, CFICode.CATEGORY_OPTION, OptionCFICode.TYPE_CALL, OptionCFICode.EXERCISE_EUROPEAN, OptionCFICode.UNDERLYING_COMMODITY, OptionCFICode.DELIVERY_CASH, OptionCFICode.STANDARD_STANDARD);
		code = new OptionCFICode("OCEWCS"); //$NON-NLS-1$
		checkOptionCode(code, CFICode.CATEGORY_OPTION, OptionCFICode.TYPE_CALL, OptionCFICode.EXERCISE_EUROPEAN, OptionCFICode.UNDERLYING_SWAP, OptionCFICode.DELIVERY_CASH, OptionCFICode.STANDARD_STANDARD);
		code = new OptionCFICode("OCEBCS"); //$NON-NLS-1$
		checkOptionCode(code, CFICode.CATEGORY_OPTION, OptionCFICode.TYPE_CALL, OptionCFICode.EXERCISE_EUROPEAN, OptionCFICode.UNDERLYING_BASKET, OptionCFICode.DELIVERY_CASH, OptionCFICode.STANDARD_STANDARD);
		code = new OptionCFICode("OCEMCS"); //$NON-NLS-1$
		checkOptionCode(code, CFICode.CATEGORY_OPTION, OptionCFICode.TYPE_CALL, OptionCFICode.EXERCISE_EUROPEAN, OptionCFICode.UNDERLYING_OTHER, OptionCFICode.DELIVERY_CASH, OptionCFICode.STANDARD_STANDARD);
		code = new OptionCFICode("OXXXXX"); //$NON-NLS-1$
		checkOptionCode(code, CFICode.CATEGORY_OPTION, CFICode.UNKNOWN_UNUSED, CFICode.UNKNOWN_UNUSED, CFICode.UNKNOWN_UNUSED, CFICode.UNKNOWN_UNUSED, CFICode.UNKNOWN_UNUSED);
		new ExpectedTestFailure(IllegalArgumentException.class, null) {
			@Override
			protected void execute() throws Throwable {
				OptionCFICode code = new OptionCFICode("BCASCS"); //$NON-NLS-1$
				if (code.isValid()) {
					assertTrue("Should never get here", false); //$NON-NLS-1$
				}
			}
		}.run();
		new ExpectedTestFailure(IllegalArgumentException.class, null) {
			@Override
			protected void execute() throws Throwable {
				OptionCFICode code = new OptionCFICode("OLASCS"); //$NON-NLS-1$
				if (code.isValid()) {
					assertTrue("Should never get here", false); //$NON-NLS-1$
				}
			}
		}.run();
		new ExpectedTestFailure(IllegalArgumentException.class, null) {
			@Override
			protected void execute() throws Throwable {
				OptionCFICode code = new OptionCFICode("OCLSCS"); //$NON-NLS-1$
				if (code.isValid()) {
					assertTrue("Should never get here", false); //$NON-NLS-1$
				}
			}
		}.run();
		new ExpectedTestFailure(IllegalArgumentException.class, null) {
			@Override
			protected void execute() throws Throwable {
				OptionCFICode code = new OptionCFICode("OCALCS"); //$NON-NLS-1$
				if (code.isValid()) {
					assertTrue("Should never get here", false); //$NON-NLS-1$
				}
			}
		}.run();
		new ExpectedTestFailure(IllegalArgumentException.class, null) {
			@Override
			protected void execute() throws Throwable {
				OptionCFICode code = new OptionCFICode("OCASLS"); //$NON-NLS-1$
				if (code.isValid()) {
					assertTrue("Should never get here", false); //$NON-NLS-1$
				}
			}
		}.run();
		new ExpectedTestFailure(IllegalArgumentException.class, null) {
			@Override
			protected void execute() throws Throwable {
				OptionCFICode code = new OptionCFICode("OCASCL"); //$NON-NLS-1$
				if (code.isValid()) {
					assertTrue("Should never get here", false); //$NON-NLS-1$
				}
			}
		}.run();
		
		new ExpectedTestFailure(StringIndexOutOfBoundsException.class, null) {
			@Override
			protected void execute() throws Throwable {
				OptionCFICode code = new OptionCFICode("OCASC"); //$NON-NLS-1$
				if (code.isValid()) {
					assertTrue("Should never get here", false); //$NON-NLS-1$
				}
			}
		}.run();
	}

	private void checkOptionCode(OptionCFICode code, char category, char type, char exercise, char underlying, char delivery, char standard) {
		assertEquals(category, code.getCategory());
		assertEquals(type, code.getType());
		assertEquals(exercise, code.getExercise());
		assertEquals(underlying, code.getUnderlying());
		assertEquals(delivery, code.getDelivery());
		assertEquals(standard, code.getStandard());
	}
}
