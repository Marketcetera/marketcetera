package org.marketcetera.photon.ui.validation.fix;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParsePosition;

import org.eclipse.core.databinding.conversion.Converter;
import org.marketcetera.photon.Messages;

public class StringToBigDecimalConverter
    extends Converter
    implements Messages
{

	private final NumberFormat numberFormat;

	public StringToBigDecimalConverter() {
		this(NumberFormat.getNumberInstance());
	}

	public StringToBigDecimalConverter(NumberFormat numberFormat) {
		super(String.class, BigDecimal.class);
		this.numberFormat = numberFormat;

	}

	public Object convert(Object fromObject) {
		if (!(fromObject instanceof String)) {
			throw new IllegalArgumentException(INVALID_SPECIFIED_VALUE.getText(fromObject));
		}
		String source = (String) fromObject;
		if (source.trim().length() == 0) {
			return null;
		}

		synchronized (numberFormat) {
			ParsePosition position = new ParsePosition(0);
			numberFormat.parse(source, position);

			if (position.getIndex() != source.length()
					|| position.getErrorIndex() > -1) {
				int errorIndex = (position.getErrorIndex() > -1) ? position
						.getErrorIndex() : position.getIndex();

				throw new IllegalArgumentException(
						"The value " + fromObject + " is not a valid decimal at character index: " + errorIndex); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		return new BigDecimal(source);

	}

}
