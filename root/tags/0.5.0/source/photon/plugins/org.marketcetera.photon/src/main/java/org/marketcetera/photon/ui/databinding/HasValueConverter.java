package org.marketcetera.photon.ui.databinding;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * This "converts" a non null value to a boolean value of true.
 * Null values and zero-length strings are converted
 * to {@link Boolean#FALSE}
 */
public class HasValueConverter implements IConverter {

	public Object convert(Object fromObject) {
		if (fromObject instanceof String){
			return ((String)fromObject).length() > 0;
		}
		return (fromObject != null);
	}

	public Object getFromType() {
		return Object.class;
	}

	public Object getToType() {
		return Boolean.class;
	}

}
