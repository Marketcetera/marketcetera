package org.marketcetera.core;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

@ClassVersion("$Id$") //$NON-NLS-1$
public class BigDecimalUtils {
//	 default to read a double primitive value of 18 digit
//	 precision
	public static final NumberFormat DEFAULT_DECIMAL_FORMAT =
	    new DecimalFormat ("#.0#################",new DecimalFormatSymbols(Locale.US));  //$NON-NLS-1$

    public static final MathContext DEFAULT_CONTEXT = new MathContext(4);


	public static BigDecimal add (BigDecimal a, BigDecimal b) {
	    if (a == null) return (b == null) ? BigDecimal.ZERO : b;
	    return a.add (b);
	}

	public static BigDecimal subtract (BigDecimal a, BigDecimal b) {
	    if (a == null) return (b == null) ? BigDecimal.ZERO : b;
	    return a.subtract (b);
	}

	public static BigDecimal multiply (double a, double b) {
	    String s = DEFAULT_DECIMAL_FORMAT.format(a);
	    BigDecimal bd = new BigDecimal (s); //non-i18n
	    return multiply (bd, b);
	}

	public static BigDecimal multiply (BigDecimal a, double b) {
	    String s = DEFAULT_DECIMAL_FORMAT.format(b);
	    BigDecimal bd = new BigDecimal (s); //non-i18n
	    return multiply (a, bd);
	}

	public static BigDecimal multiply (BigDecimal a, BigDecimal b) {
	    if (a == null) return (b == null) ? BigDecimal.ZERO : b;
	    return a.multiply (b);
	}

	public static BigDecimal divide (BigDecimal a, BigDecimal b) { return divide(a, b, MathContext.DECIMAL128); }

	public static BigDecimal divide (BigDecimal a, BigDecimal b, MathContext context) {
	    if (a == null) return (b == null) ? BigDecimal.ZERO : b;
	    return a.divide (b, context);
	}
	
	/** Returns the BigDecimal value n with trailing zeroes removed. */
	public static BigDecimal trim(BigDecimal n) {
		try {
            int scale;
            while((scale = n.scale()) > 0) {
                n = n.setScale(scale - 1);
            }
		} catch (ArithmeticException e) {
			// no more trailing zeroes so exit.
		}
		return n;
	}

}
