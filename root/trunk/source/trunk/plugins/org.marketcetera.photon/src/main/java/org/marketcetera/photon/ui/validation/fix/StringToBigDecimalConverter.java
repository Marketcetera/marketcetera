package org.marketcetera.photon.ui.validation.fix;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParsePosition;

import org.eclipse.core.databinding.conversion.Converter;

public class StringToBigDecimalConverter extends Converter {

	private final NumberFormat numberFormat;

	public StringToBigDecimalConverter(){
		this(NumberFormat.getNumberInstance());
	}
	
	public StringToBigDecimalConverter(NumberFormat numberFormat) {
		super(String.class, BigDecimal.class);
		this.numberFormat = numberFormat;
		
	}

	public Object convert(Object fromObject) {
		if (!(fromObject instanceof String)) {
			throw new IllegalArgumentException(
					"'fromObject' not instanceof String"); //$NON-NLS-1$
		}
		String source = (String) fromObject;
		if (source.trim().length() == 0) {
			return null;
		}

		Number result = null;

		synchronized (numberFormat) {
			ParsePosition position = new ParsePosition(0);
			result = numberFormat.parse(source, position);

			if (position.getIndex() != source.length()
					|| position.getErrorIndex() > -1) {
				int errorIndex = (position.getErrorIndex() > -1) ? position
						.getErrorIndex() : position.getIndex();

				throw new IllegalArgumentException(
						"FromObject " + fromObject + " was invalid at character " + errorIndex); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		

		return new BigDecimal(source);

	}

}
