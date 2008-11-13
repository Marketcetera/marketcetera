package org.marketcetera.messagehistory;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.marketcetera.core.MSymbol;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.Message;
import quickfix.field.Side;

public class MessageHolderTest extends TestCase 
{
    private FIXMessageFactory msgFactory = FIXVersion.FIX42.getMessageFactory();
	public void testIncomingMessageHolder()
	{
		Message message = msgFactory.newLimitOrder("asdf", Side.BUY, BigDecimal.TEN, new MSymbol("123"), BigDecimal.ONE, '\0', "asd"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		IncomingMessageHolder holder = new IncomingMessageHolder(message);
		assertEquals(message, holder.getMessage());
	}

	public void testMessageHolder()
	{
		Message message = msgFactory.newLimitOrder("asdf", Side.BUY, BigDecimal.TEN, new MSymbol("123"), BigDecimal.ONE, '\0', "asd"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		MessageHolder holder = new MessageHolder(message);
		assertEquals(message, holder.getMessage());
	}

}
