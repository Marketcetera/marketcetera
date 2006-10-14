package org.marketcetera.photon.model;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.marketcetera.core.AccountID;
import org.marketcetera.core.InternalID;
import org.marketcetera.core.MSymbol;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.Message;
import quickfix.field.Side;

public class MessageHolderTest extends TestCase 
{

	public void testIncomingMessageHolder()
	{
		Message message = FIXMessageUtil.newLimitOrder(new InternalID("asdf"), Side.BUY, BigDecimal.TEN, new MSymbol("123"), BigDecimal.ONE, '\0', new AccountID("asd"));
		IncomingMessageHolder holder = new IncomingMessageHolder(message);
		assertEquals(message, holder.getMessage());
	}

	public void testOutgoingMessageHolder()
	{
		Message message = FIXMessageUtil.newLimitOrder(new InternalID("asdf"), Side.BUY, BigDecimal.TEN, new MSymbol("123"), BigDecimal.ONE, '\0', new AccountID("asd"));
		OutgoingMessageHolder holder = new OutgoingMessageHolder(message);
		assertEquals(message, holder.getMessage());
	}

	public void testMessageHolder()
	{
		Message message = FIXMessageUtil.newLimitOrder(new InternalID("asdf"), Side.BUY, BigDecimal.TEN, new MSymbol("123"), BigDecimal.ONE, '\0', new AccountID("asd"));
		MessageHolder holder = new MessageHolder(message);
		assertEquals(message, holder.getMessage());
	}

}
