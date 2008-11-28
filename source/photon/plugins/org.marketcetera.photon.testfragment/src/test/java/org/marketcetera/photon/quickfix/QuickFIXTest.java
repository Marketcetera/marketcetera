package org.marketcetera.photon.quickfix;

import junit.framework.TestCase;

import org.marketcetera.quickfix.FIXVersion;

import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.FieldType;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.field.OrderQty;
import quickfix.field.Side;
import quickfix.field.Symbol;

public class QuickFIXTest extends TestCase {

	public void testMessageConstructor() throws InvalidMessage, FieldNotFound
	{
		String messageString = "8=FIX.4.29=12035=W31=17.5955=asdf268=2269=0270=17.58271=200273=01:20:04275=BGUS269=1270=17.59271=300273=01:20:04275=BGUS10=207";
		Message aMessage = new Message(messageString, false);
		assertEquals("asdf",aMessage.getString(Symbol.FIELD));
	}
	
	public void testQuickFIXAssumptions() throws ConfigError {
		boolean orderQtyIsInt;
		DataDictionary dictionary;
		FIXVersion version;

		version = FIXVersion.FIX40;
		dictionary = new DataDictionary(version.getDataDictionaryURL());
		orderQtyIsInt = (FieldType.Int == dictionary.getFieldTypeEnum(OrderQty.FIELD));
		assertEquals(true, orderQtyIsInt);
		assertTrue(FieldType.String.equals(dictionary.getFieldTypeEnum(Side.FIELD)));

		version = FIXVersion.FIX41;
		dictionary = new DataDictionary(version.getDataDictionaryURL());
		orderQtyIsInt = (FieldType.Int == dictionary.getFieldTypeEnum(OrderQty.FIELD));
		assertEquals(true, orderQtyIsInt);
		assertTrue(FieldType.String.equals(dictionary.getFieldTypeEnum(Side.FIELD)));

		version = FIXVersion.FIX42;
		dictionary = new DataDictionary(version.getDataDictionaryURL());
		orderQtyIsInt = (FieldType.Int == dictionary.getFieldTypeEnum(OrderQty.FIELD));
		assertEquals(false, orderQtyIsInt);
		assertTrue(FieldType.Char.equals(dictionary.getFieldTypeEnum(Side.FIELD)));
		
		version = FIXVersion.FIX43;
		dictionary = new DataDictionary(version.getDataDictionaryURL());
		orderQtyIsInt = (FieldType.Int == dictionary.getFieldTypeEnum(OrderQty.FIELD));
		assertEquals(false, orderQtyIsInt);
		assertTrue(FieldType.Char.equals(dictionary.getFieldTypeEnum(Side.FIELD)));
		
		version = FIXVersion.FIX44;
		dictionary = new DataDictionary(version.getDataDictionaryURL());
		orderQtyIsInt = (FieldType.Int == dictionary.getFieldTypeEnum(OrderQty.FIELD));
		assertEquals(false, orderQtyIsInt);
		assertTrue(FieldType.Char.equals(dictionary.getFieldTypeEnum(Side.FIELD)));
		
		version = FIXVersion.FIX_SYSTEM;
		dictionary = new DataDictionary(version.getDataDictionaryURL());
		orderQtyIsInt = (FieldType.Int == dictionary.getFieldTypeEnum(OrderQty.FIELD));
		assertEquals(false, orderQtyIsInt);
		assertTrue(FieldType.Char.equals(dictionary.getFieldTypeEnum(Side.FIELD)));
		
	
	}
}
