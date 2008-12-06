package org.marketcetera.photon.ui.validation.fix;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.marketcetera.photon.parser.PriceImage;
import org.marketcetera.quickfix.CurrentFIXDataDictionary;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.field.OrdType;

public class PriceConverterBuilderTest extends TestCase {

	
	private PriceConverterBuilder priceConverterBuilder;

	@Override
	protected void setUp() throws Exception {
		CurrentFIXDataDictionary.setCurrentFIXDataDictionary(
				FIXDataDictionaryManager.initialize(FIXVersion.FIX_SYSTEM, 
						FIXVersion.FIX_SYSTEM.getDataDictionaryURL()));
		priceConverterBuilder = new PriceConverterBuilder(FIXDataDictionaryManager.getFIXDataDictionary(FIXVersion.FIX_SYSTEM).getDictionary());
		priceConverterBuilder.addMapping(OrdType.MARKET, PriceImage.MKT.getImage());
	}

	public void testNewToTargetConverter() {

		IConverter converter = priceConverterBuilder.newToTargetConverter();
		assertEquals(String.class, converter.getToType());
		assertEquals(Object.class, converter.getFromType());

		assertEquals(PriceImage.MKT.getImage(), converter.convert(OrdType.MARKET));
		assertEquals("10", converter.convert("10"));
		assertEquals(null, converter.convert('_'));
	}

	public void testNewToModelConverter() {
		IConverter converter = priceConverterBuilder.newToModelConverter();
		assertEquals(String.class, converter.getFromType());
		assertEquals(Object.class, converter.getToType());

		assertEquals(OrdType.MARKET, converter.convert(PriceImage.MKT.getImage()));
		assertEquals(OrdType.MARKET, converter.convert("mkt"));
		assertEquals(OrdType.MARKET, converter.convert("Mkt"));
		assertEquals(BigDecimal.TEN, converter.convert("10"));
		assertEquals(null, converter.convert(null));
		assertEquals(null, converter.convert("_"));
		assertEquals(null, converter.convert("1.2.3.4.5"));
	}

	public void testNewTargetAfterGetValidator() {
		IValidator validator = priceConverterBuilder.newTargetAfterGetValidator();
		assertEquals(IStatus.OK, validator.validate("MKT").getSeverity());
		assertEquals(IStatus.OK, validator.validate("mkt").getSeverity());
		assertEquals(IStatus.OK, validator.validate("MKT").getSeverity());
		assertEquals(IStatus.OK, validator.validate("mkT").getSeverity());
		assertEquals(IStatus.OK, validator.validate("123.4").getSeverity());
		assertEquals(IStatus.OK, validator.validate("-1123.4").getSeverity());
		
		assertEquals(IStatus.ERROR, validator.validate("").getSeverity());
		assertEquals(IStatus.ERROR, validator.validate(null).getSeverity());
		assertEquals(IStatus.ERROR, validator.validate("_").getSeverity());
		assertEquals(IStatus.ERROR, validator.validate("1.2.3.4.5").getSeverity());
	}

	public void testNewModelAfterGetValidator() {
		IValidator validator = priceConverterBuilder.newModelAfterGetValidator();
		assertEquals(IStatus.OK, validator.validate("123.4").getSeverity());
		//FIX 4.2 specific validation
		assertEquals(IStatus.OK, validator.validate(OrdType.FOREX_LIMIT).getSeverity());
		assertEquals(IStatus.OK, validator.validate(OrdType.FOREX_MARKET).getSeverity());
		assertEquals(IStatus.OK, validator.validate(OrdType.FOREX_PREVIOUSLY_QUOTED).getSeverity());
		assertEquals(IStatus.OK, validator.validate(OrdType.FOREX_SWAP).getSeverity());
		assertEquals(IStatus.OK, validator.validate(OrdType.FUNARI).getSeverity());
		assertEquals(IStatus.OK, validator.validate(OrdType.LIMIT_ON_CLOSE).getSeverity());
		assertEquals(IStatus.OK, validator.validate(OrdType.LIMIT_OR_BETTER).getSeverity());
		assertEquals(IStatus.OK, validator.validate(OrdType.LIMIT_WITH_OR_WITHOUT).getSeverity());
		assertEquals(IStatus.OK, validator.validate(OrdType.MARKET).getSeverity());
		assertEquals(IStatus.OK, validator.validate(OrdType.MARKET_IF_TOUCHED).getSeverity());
		assertEquals(IStatus.OK, validator.validate(OrdType.MARKET_ON_CLOSE).getSeverity());
		assertEquals(IStatus.OK, validator.validate(OrdType.MARKET_WITH_LEFTOVER_AS_LIMIT).getSeverity());
		assertEquals(IStatus.OK, validator.validate(OrdType.NEXT_FUND_VALUATION_POINT).getSeverity());
		assertEquals(IStatus.OK, validator.validate(OrdType.ON_BASIS).getSeverity());
		assertEquals(IStatus.OK, validator.validate(OrdType.ON_CLOSE).getSeverity());
		assertEquals(IStatus.OK, validator.validate(OrdType.PEGGED).getSeverity());
		assertEquals(IStatus.OK, validator.validate(OrdType.PREVIOUS_FUND_VALUATION_POINT).getSeverity());
		assertEquals(IStatus.OK, validator.validate(OrdType.PREVIOUSLY_INDICATED).getSeverity());
		assertEquals(IStatus.OK, validator.validate(OrdType.PREVIOUSLY_QUOTED).getSeverity());
		assertEquals(IStatus.OK, validator.validate(OrdType.STOP).getSeverity());
		assertEquals(IStatus.OK, validator.validate(OrdType.STOP_LIMIT).getSeverity());
		assertEquals(IStatus.OK, validator.validate(OrdType.WITH_OR_WITHOUT).getSeverity());

		assertEquals(IStatus.ERROR, validator.validate('_').getSeverity());
		assertEquals(IStatus.ERROR, validator.validate("1.2.3.4.5").getSeverity());
		assertEquals(IStatus.ERROR, validator.validate("1a").getSeverity());
		assertEquals(IStatus.ERROR, validator.validate("ASD").getSeverity());
	}

}
