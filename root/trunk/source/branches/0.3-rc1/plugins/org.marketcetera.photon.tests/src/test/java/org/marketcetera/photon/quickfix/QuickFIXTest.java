package org.marketcetera.photon.quickfix;

import junit.framework.TestCase;
import quickfix.FieldNotFound;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.field.Symbol;

public class QuickFIXTest extends TestCase {

	public void testMessageConstructor() throws InvalidMessage, FieldNotFound
	{
		String messageString = "8=FIX.4.29=12035=W31=17.5955=asdf268=2269=0270=17.58271=200273=01:20:04275=BGUS269=1270=17.59271=300273=01:20:04275=BGUS10=207";
		Message aMessage = new Message(messageString, false);
		assertEquals("asdf",aMessage.getString(Symbol.FIELD));
	}
}
