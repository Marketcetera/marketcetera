package com.marketcetera.colin.ui.utils.converters;

import com.vaadin.flow.templatemodel.ModelEncoder;

public class LongToStringConverter implements ModelEncoder<Long, String> {

	@Override
	public String encode(Long modelValue) {
		if (modelValue == null) {
			return null;
		}
		return modelValue.toString();
	}

	@Override
	public Long decode(String presentationValue) {
		if (presentationValue == null) {
			return null;
		}
		return Long.parseLong(presentationValue);
	}

}
