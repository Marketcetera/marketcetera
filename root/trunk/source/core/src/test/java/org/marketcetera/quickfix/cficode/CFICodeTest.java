package org.marketcetera.quickfix.cficode;

import junit.framework.TestCase;

import org.marketcetera.core.ExpectedTestFailure;

public class CFICodeTest extends TestCase {

	public void testOptionCFICode() throws Exception {
		OptionCFICode code;
		code = new OptionCFICode("OCASCS");
		checkOptionCode(code, CFICode.CATEGORY_OPTION, OptionCFICode.TYPE_CALL, OptionCFICode.EXERCISE_AMERICAN, OptionCFICode.UNDERLYING_STOCK, OptionCFICode.DELIVERY_CASH, OptionCFICode.STANDARD_STANDARD);
		code = new OptionCFICode("OPEIPN");
		checkOptionCode(code, CFICode.CATEGORY_OPTION, OptionCFICode.TYPE_PUT, OptionCFICode.EXERCISE_EUROPEAN, OptionCFICode.UNDERLYING_INDEX, OptionCFICode.DELIVERY_PHYSICAL, OptionCFICode.STANDARD_NON_STANDARD);
		code = new OptionCFICode("OCEDCS");
		checkOptionCode(code, CFICode.CATEGORY_OPTION, OptionCFICode.TYPE_CALL, OptionCFICode.EXERCISE_EUROPEAN, OptionCFICode.UNDERLYING_DEBT, OptionCFICode.DELIVERY_CASH, OptionCFICode.STANDARD_STANDARD);
		code = new OptionCFICode("OCECCS");
		checkOptionCode(code, CFICode.CATEGORY_OPTION, OptionCFICode.TYPE_CALL, OptionCFICode.EXERCISE_EUROPEAN, OptionCFICode.UNDERLYING_CURRENCY, OptionCFICode.DELIVERY_CASH, OptionCFICode.STANDARD_STANDARD);
		code = new OptionCFICode("OCEOCS");
		checkOptionCode(code, CFICode.CATEGORY_OPTION, OptionCFICode.TYPE_CALL, OptionCFICode.EXERCISE_EUROPEAN, OptionCFICode.UNDERLYING_OPTION, OptionCFICode.DELIVERY_CASH, OptionCFICode.STANDARD_STANDARD);
		code = new OptionCFICode("OCEFCS");
		checkOptionCode(code, CFICode.CATEGORY_OPTION, OptionCFICode.TYPE_CALL, OptionCFICode.EXERCISE_EUROPEAN, OptionCFICode.UNDERLYING_FUTURE, OptionCFICode.DELIVERY_CASH, OptionCFICode.STANDARD_STANDARD);
		code = new OptionCFICode("OCETCS");
		checkOptionCode(code, CFICode.CATEGORY_OPTION, OptionCFICode.TYPE_CALL, OptionCFICode.EXERCISE_EUROPEAN, OptionCFICode.UNDERLYING_COMMODITY, OptionCFICode.DELIVERY_CASH, OptionCFICode.STANDARD_STANDARD);
		code = new OptionCFICode("OCEWCS");
		checkOptionCode(code, CFICode.CATEGORY_OPTION, OptionCFICode.TYPE_CALL, OptionCFICode.EXERCISE_EUROPEAN, OptionCFICode.UNDERLYING_SWAP, OptionCFICode.DELIVERY_CASH, OptionCFICode.STANDARD_STANDARD);
		code = new OptionCFICode("OCEBCS");
		checkOptionCode(code, CFICode.CATEGORY_OPTION, OptionCFICode.TYPE_CALL, OptionCFICode.EXERCISE_EUROPEAN, OptionCFICode.UNDERLYING_BASKET, OptionCFICode.DELIVERY_CASH, OptionCFICode.STANDARD_STANDARD);
		code = new OptionCFICode("OCEMCS");
		checkOptionCode(code, CFICode.CATEGORY_OPTION, OptionCFICode.TYPE_CALL, OptionCFICode.EXERCISE_EUROPEAN, OptionCFICode.UNDERLYING_OTHER, OptionCFICode.DELIVERY_CASH, OptionCFICode.STANDARD_STANDARD);
		code = new OptionCFICode("OXXXXX");
		checkOptionCode(code, CFICode.CATEGORY_OPTION, CFICode.UNKNOWN_UNUSED, CFICode.UNKNOWN_UNUSED, CFICode.UNKNOWN_UNUSED, CFICode.UNKNOWN_UNUSED, CFICode.UNKNOWN_UNUSED);
		new ExpectedTestFailure(IllegalArgumentException.class, null) {
			@Override
			protected void execute() throws Throwable {
				OptionCFICode code = new OptionCFICode("BCASCS");
				if (code.isValid()) {
					assertTrue("Should never get here", false);
				}
			}
		}.run();
		new ExpectedTestFailure(IllegalArgumentException.class, null) {
			@Override
			protected void execute() throws Throwable {
				OptionCFICode code = new OptionCFICode("OLASCS");
				if (code.isValid()) {
					assertTrue("Should never get here", false);
				}
			}
		}.run();
		new ExpectedTestFailure(IllegalArgumentException.class, null) {
			@Override
			protected void execute() throws Throwable {
				OptionCFICode code = new OptionCFICode("OCLSCS");
				if (code.isValid()) {
					assertTrue("Should never get here", false);
				}
			}
		}.run();
		new ExpectedTestFailure(IllegalArgumentException.class, null) {
			@Override
			protected void execute() throws Throwable {
				OptionCFICode code = new OptionCFICode("OCALCS");
				if (code.isValid()) {
					assertTrue("Should never get here", false);
				}
			}
		}.run();
		new ExpectedTestFailure(IllegalArgumentException.class, null) {
			@Override
			protected void execute() throws Throwable {
				OptionCFICode code = new OptionCFICode("OCASLS");
				if (code.isValid()) {
					assertTrue("Should never get here", false);
				}
			}
		}.run();
		new ExpectedTestFailure(IllegalArgumentException.class, null) {
			@Override
			protected void execute() throws Throwable {
				OptionCFICode code = new OptionCFICode("OCASCL");
				if (code.isValid()) {
					assertTrue("Should never get here", false);
				}
			}
		}.run();
		
		new ExpectedTestFailure(StringIndexOutOfBoundsException.class, null) {
			@Override
			protected void execute() throws Throwable {
				OptionCFICode code = new OptionCFICode("OCASC");
				if (code.isValid()) {
					assertTrue("Should never get here", false);
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
