package org.marketcetera.photon.ui.validation.fix;

import junit.framework.Test;

import org.eclipse.core.databinding.observable.Realm;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MsgType;
import quickfix.field.OrdType;
import quickfix.field.Price;

public class PriceObservableValueTest extends FIXVersionedTestCase {

	
	public PriceObservableValueTest(String inName, FIXVersion version) {
		super(inName, version);
	}

	public static Test suite() {
		return new FIXVersionTestSuite(PriceObservableValueTest.class, FIXVersion.values());
	}

	public void testGetValueType(){
		Message message = this.msgFactory.createMessage(MsgType.ORDER_SINGLE);
		PriceObservableValue pov = new PriceObservableValue(Realm.getDefault(), message , Price.FIELD, fixDD.getDictionary());
		assertEquals(Object.class, pov.getValueType());
	}

	public void testSetValue() throws FieldNotFound {
		Message message = this.msgFactory.createMessage(MsgType.ORDER_SINGLE);
		PriceObservableValue pov = new PriceObservableValue(Realm.getDefault(), message , Price.FIELD, fixDD.getDictionary());
		pov.setValue(OrdType.MARKET);
		assertEquals(OrdType.MARKET, message.getChar(OrdType.FIELD));
		assertFalse(message.isSetField(Price.FIELD));
		
		pov.setValue(8675309);
		assertEquals(OrdType.LIMIT, message.getChar(OrdType.FIELD));
		assertEquals(8675309, message.getInt(Price.FIELD));

		pov.setValue(OrdType.MARKET);
		assertEquals(OrdType.MARKET, message.getChar(OrdType.FIELD));
		assertFalse(message.isSetField(Price.FIELD));
	}

	public void testGetValue() {
		Message message = this.msgFactory.createMessage(MsgType.ORDER_SINGLE);
		message.setField(new OrdType(OrdType.LIMIT));
		message.setField(new Price(8));

		PriceObservableValue pov = new PriceObservableValue(Realm.getDefault(), message , Price.FIELD, fixDD.getDictionary());
		assertEquals("8", pov.getValue());
		
		message.setField(new OrdType(OrdType.MARKET));
		message.removeField(Price.FIELD);
		
		assertEquals(OrdType.MARKET, pov.getValue());
	}

}
