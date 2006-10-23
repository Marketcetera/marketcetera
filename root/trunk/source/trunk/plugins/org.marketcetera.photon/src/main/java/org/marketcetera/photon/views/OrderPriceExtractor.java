package org.marketcetera.photon.views;

import org.eclipse.swt.widgets.Text;
import org.marketcetera.photon.parser.PriceImage;
import org.marketcetera.photon.ui.FIXTextExtractor;

import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.OrdType;
import quickfix.field.Price;

public class OrderPriceExtractor extends FIXTextExtractor {

	public OrderPriceExtractor(Text field, DataDictionary dictionary) {
		super(field, Price.FIELD, dictionary);
	}

	@Override
	public void modifyOrder(Message aMessage) {
		if (PriceImage.MKT.getImage().equals(field.getText())){
			aMessage.setField(new OrdType(OrdType.MARKET));
			aMessage.removeField(Price.FIELD);
		} else {
			aMessage.setField(new OrdType(OrdType.LIMIT));
			super.modifyOrder(aMessage);
		}
	}

	@Override
	public void updateUI(Message aMessage) {
		try {
			char ordTypeChar = aMessage.getChar(OrdType.FIELD);
			if (ordTypeChar == OrdType.MARKET){
				field.setText(PriceImage.MKT.getImage());
			} else if (ordTypeChar == OrdType.LIMIT){
				super.updateUI(aMessage);
			}
		} catch (FieldNotFound fnf) {
		}
	}

}
