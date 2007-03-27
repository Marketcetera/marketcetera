package org.marketcetera.photon.ui.validation.fix;

import org.eclipse.swt.widgets.Control;

import quickfix.DataDictionary;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Message;

public abstract class AbstractFIXExtractor {

	public static final String EXTRACTOR_KEY = "EXTRACTOR";
	
	protected int fieldNum;
	protected DataDictionary dictionary;

	public AbstractFIXExtractor(Control control, int fieldNum, DataDictionary dictionary) {
		this.fieldNum = fieldNum;
		this.dictionary = dictionary;
		control.setData(EXTRACTOR_KEY, this);
	}

	public DataDictionary getDictionary() {
		return dictionary;
	}

	public int getFieldNum() {
		return fieldNum;
	}

	protected String extractString(Message aMessage) {
		FieldMap map = null;
		if (dictionary.isHeaderField(fieldNum)){
			map = aMessage.getHeader();
		} else if (dictionary.isTrailerField(fieldNum)){
			map = aMessage.getTrailer();
		} else if (dictionary.isField(fieldNum)) {
			map = aMessage;
		}
		if (map != null){
			try {
				return map.getString(fieldNum);
			} catch (FieldNotFound e) {
				return "";
			}
		}
		return "";
	}

	protected void insertString(Message aMessage, String textString) {
		FieldMap map = null;
		if (dictionary.isHeaderField(fieldNum)){
			map = aMessage.getHeader();
		} else if (dictionary.isTrailerField(fieldNum)){
			map = aMessage.getTrailer();
		} else if (dictionary.isField(fieldNum)) {
			map = aMessage;
		}
		if (map != null){
			map.setString(fieldNum, textString);
		}
	}

	public abstract void modifyOrder(Message aMessage);
	
	public abstract void updateUI(Message aMessage);

	public abstract void clearUI();

}
