package org.marketcetera.photon.views;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.validation.IValidator;
import org.marketcetera.photon.parser.ILexerFIXImage;
import org.marketcetera.photon.ui.validation.fix.EnumStringConverterBuilder;
import org.marketcetera.photon.ui.validation.fix.IConverterBuilder;

public class BindingHelper {

	public void initCharToImageConverterBuilder(
			EnumStringConverterBuilder<Character> converterBuilder,
			ILexerFIXImage[] lexerImages) {
		for (ILexerFIXImage lexerImage : lexerImages) {
			String image = lexerImage.getImage();
			char fixValue = lexerImage.getFIXCharValue();
			converterBuilder.addMapping(fixValue, image);
		}
	}

	public void initIntToImageConverterBuilder(
			EnumStringConverterBuilder<Integer> converterBuilder,
			ILexerFIXImage[] lexerImages) {
		for (ILexerFIXImage lexerImage : lexerImages) {
			String image = lexerImage.getImage();
			int fixValue = lexerImage.getFIXIntValue();
			converterBuilder.addMapping(fixValue, image);
		}
	}

	public UpdateValueStrategy createToTargetUpdateValueStrategy(
			IConverterBuilder converterBuilder,
			IValidator modelAfterGetValidator) {
		UpdateValueStrategy updateValueStrategy = new UpdateValueStrategy();
		IValidator validator = modelAfterGetValidator;
		if (validator == null) {
			validator = converterBuilder.newModelAfterGetValidator();
		}
		if (validator != null) {
			updateValueStrategy.setAfterGetValidator(validator);
		}
		updateValueStrategy.setConverter(converterBuilder
				.newToTargetConverter());
		return updateValueStrategy;
	}

	public UpdateValueStrategy createToTargetUpdateValueStrategy(
			IConverterBuilder converterBuilder) {
		return createToTargetUpdateValueStrategy(converterBuilder, null);
	}

	public UpdateValueStrategy createToModelUpdateValueStrategy(
			IConverterBuilder converterBuilder,
			IValidator targetAfterGetValidator) {
		UpdateValueStrategy updateValueStrategy = new UpdateValueStrategy();
		IValidator validator = targetAfterGetValidator;
		if (validator == null) {
			validator = converterBuilder.newTargetAfterGetValidator();
		}
		if (validator != null) {
			updateValueStrategy.setAfterGetValidator(validator);
		}
		updateValueStrategy
				.setConverter(converterBuilder.newToModelConverter());
		return updateValueStrategy;
	}

	public UpdateValueStrategy createToModelUpdateValueStrategy(
			IConverterBuilder converterBuilder) {
		return createToModelUpdateValueStrategy(converterBuilder, null);
	}

}
