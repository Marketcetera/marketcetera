package org.marketcetera.photon.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.custom.CCombo;

import quickfix.DataDictionary;
import quickfix.Message;

public class FIXCComboExtractor extends AbstractFIXExtractor {

	private CCombo field;
	private Map<String, String> uiStringToMessageStringMap;
	private Map<String, String> messageStringToUIStringMap;

	public FIXCComboExtractor(CCombo field, int fieldNum, DataDictionary dictionary,
			Map<String, String> uiStringToMessageStringMap) {
		super(field, fieldNum, dictionary);
		this.field = field;
		this.uiStringToMessageStringMap = uiStringToMessageStringMap;
		this.messageStringToUIStringMap = new HashMap<String, String>();
		for (String aKey : uiStringToMessageStringMap.keySet()) {
			messageStringToUIStringMap.put(uiStringToMessageStringMap.get(aKey), aKey);
		}
	}

	public void modifyOrder(Message aMessage){
		insertString(aMessage, mapFromUIString(field.getText()));
	}

	@Override
	public void updateUI(Message aMessage) {
		field.setText(mapToUIString(extractString(aMessage)));
	}
	
	protected String mapToUIString(String messageString)
	{
		return messageStringToUIStringMap.get(messageString);
	}

	protected String mapFromUIString(String uiString)
	{
		return uiStringToMessageStringMap.get(uiString);
	}

	@Override
	public void clearUI() {
		field.setText("");
	}
	
}
