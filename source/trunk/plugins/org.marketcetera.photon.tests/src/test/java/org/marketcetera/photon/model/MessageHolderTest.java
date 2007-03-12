package org.marketcetera.photon.model;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.core.IncomingMessageHolder;
import org.marketcetera.photon.core.MessageHolder;
import org.marketcetera.photon.core.OutgoingMessageHolder;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.Message;
import quickfix.field.Side;

public class MessageHolderTest extends TestCase 
{
    private FIXMessageFactory msgFactory = FIXVersion.FIX42.getMessageFactory();
	public void testIncomingMessageHolder()
	{
		Message message = msgFactory.newLimitOrder("asdf", Side.BUY, BigDecimal.TEN, new MSymbol("123"), BigDecimal.ONE, '\0', "asd");
		IncomingMessageHolder holder = new IncomingMessageHolder(message);
		assertEquals(message, holder.getMessage());
	}

	public void testOutgoingMessageHolder()
	{
		Message message = msgFactory.newLimitOrder("asdf", Side.BUY, BigDecimal.TEN, new MSymbol("123"), BigDecimal.ONE, '\0', "asd");
		OutgoingMessageHolder holder = new OutgoingMessageHolder(message);
		assertEquals(message, holder.getMessage());
	}

	public void testMessageHolder()
	{
		Message message = msgFactory.newLimitOrder("asdf", Side.BUY, BigDecimal.TEN, new MSymbol("123"), BigDecimal.ONE, '\0', "asd");
		MessageHolder holder = new MessageHolder(message);
		assertEquals(message, holder.getMessage());
	}

}
