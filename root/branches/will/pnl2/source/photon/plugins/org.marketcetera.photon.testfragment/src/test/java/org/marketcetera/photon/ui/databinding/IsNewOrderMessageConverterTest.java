package org.marketcetera.photon.ui.databinding;

import java.math.BigDecimal;

import junit.framework.Test;

import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.Message;
import quickfix.field.MsgType;

public class IsNewOrderMessageConverterTest extends FIXVersionedTestCase {

	public IsNewOrderMessageConverterTest(
			String name, FIXVersion version) {
		super(name, version);
	}
	public static Test suite() {
		return new FIXVersionTestSuite(IsNewOrderMessageConverterTest.class, FIXVersion.values());
	}
	public void testIsNewOrderMessageConverter() throws Exception {
		IsNewOrderMessageConverter converter = new IsNewOrderMessageConverter();
		Message newOrderMessage = this.msgFactory.newBasicOrder();
		assertEquals(Boolean.TRUE, (Boolean)converter.convert(newOrderMessage));
		newOrderMessage.getHeader().removeField(MsgType.FIELD);
		assertEquals(Boolean.FALSE, converter.convert(newOrderMessage));
		Message cancelReplaceMessage = this.msgFactory.newCancelReplacePrice("1", "2", BigDecimal.ONE);
		assertEquals(Boolean.FALSE, converter.convert(cancelReplaceMessage));
	}
}
