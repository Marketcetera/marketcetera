package org.marketcetera.photon.ui.databinding;

import org.eclipse.core.databinding.conversion.IConverter;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.Message;

/**
 * This class "converts" a message into a boolean true, if
 * the message represents a new order single message.
 * 
 * @see FIXMessageUtil#isOrderSingle(Message)
 * 
 * @author gmiller
 *
 */
public class IsNewOrderMessageConverter implements IConverter {

	public Object convert(Object fromObject) {
		return FIXMessageUtil.isOrderSingle((Message) fromObject);
	}

	public Object getFromType() {
		return Message.class;
	}

	public Object getToType() {
		return Boolean.class;
	}

}
