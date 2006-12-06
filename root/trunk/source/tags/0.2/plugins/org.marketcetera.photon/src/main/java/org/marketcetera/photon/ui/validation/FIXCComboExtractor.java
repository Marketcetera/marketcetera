package org.marketcetera.photon.ui.validation;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.custom.CCombo;

import quickfix.DataDictionary;
import quickfix.Message;

public class FIXCComboExtractor extends AbstractFIXExtractor {

	private CCombo field;
	private Map<String, String> uiStringToMessageStringMap;
	private Map<String, String> messageStringToUIStringMap;
	private final String defaultString;

	
	
	public FIXCComboExtractor(CCombo field, int fieldNum, DataDictionary dictionary,
			Map<String, String> uiStringToMessageStringMap) {
		this(field, fieldNum, dictionary, uiStringToMessageStringMap,"");
	}
	public FIXCComboExtractor(CCombo field, int fieldNum, DataDictionary dictionary,
				Map<String, String> uiStringToMessageStringMap, String defaultString) {
		super(field, fieldNum, dictionary);
		this.field = field;
		this.uiStringToMessageStringMap = uiStringToMessageStringMap;
		this.defaultString = defaultString;
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
		String mappedString = mapToUIString(extractString(aMessage));
		field.setText(mappedString == null ? defaultString : mappedString);
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
