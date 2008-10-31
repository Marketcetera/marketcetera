package org.marketcetera.photon.ui.validation;

import junit.framework.Test;

import org.eclipse.core.runtime.IStatus;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.field.MsgType;
import quickfix.field.TimeInForce;

public class DataDictionaryValidatorTest extends FIXVersionedTestCase {

	public DataDictionaryValidatorTest(String inName, FIXVersion version) {
		super(inName, version);
	}

	public static Test suite() {
		return new FIXVersionTestSuite(DataDictionaryValidatorTest.class, FIXVersion.values());
	}
	
	public void testDataDictionaryValidatorTest() throws Exception {
		DataDictionaryValidator validator = new DataDictionaryValidator(
				this.fixDD.getDictionary(),
				MsgType.FIELD,
				"",
				PhotonPlugin.ID);
		
		assertEquals(IStatus.OK, validator.validate(MsgType.ALLOCATION_INSTRUCTION).getSeverity());
		assertEquals(IStatus.OK, validator.validate(MsgType.EXECUTION_REPORT).getSeverity());
		assertEquals(IStatus.ERROR, validator.validate("123456789101112").getSeverity());
	}
	
	public void testTimeInForceSpecialCase() throws Exception {
		DataDictionaryValidator validator = new DataDictionaryValidator(
				this.fixDD.getDictionary(),
				TimeInForce.FIELD,
				"",
				PhotonPlugin.ID);
		
		assertEquals(IStatus.OK, validator.validate(TimeInForce.IMMEDIATE_OR_CANCEL).getSeverity());
		int result = validator.validate(TimeInForce.AT_THE_CLOSE).getSeverity();
		double version = fixVersion.getVersionAsDouble();
		if (version >= 4.3 || version == 0.0)
		{
			assertEquals(IStatus.OK, result);
		} else {
			assertEquals(IStatus.WARNING, result);
		}
		assertEquals(IStatus.ERROR, validator.validate('#').getSeverity());
	}
}
