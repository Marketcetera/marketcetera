package org.marketcetera.photon.views;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.marketcetera.photon.parser.ILexerFIXImage;
import org.marketcetera.photon.ui.validation.fix.EnumStringConverterBuilder;

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
			EnumStringConverterBuilder<?> converterBuilder) {
		UpdateValueStrategy updateValueStrategy = new UpdateValueStrategy();
		updateValueStrategy.setAfterGetValidator(converterBuilder
				.newModelAfterGetValidator());
		updateValueStrategy.setConverter(converterBuilder
				.newToTargetConverter());
		return updateValueStrategy;
	}

	public UpdateValueStrategy createToModelUpdateValueStrategy(
			EnumStringConverterBuilder<?> converterBuilder) {
		UpdateValueStrategy updateValueStrategy = new UpdateValueStrategy();
		updateValueStrategy.setAfterGetValidator(converterBuilder
				.newTargetAfterGetValidator());
		updateValueStrategy
				.setConverter(converterBuilder.newToModelConverter());
		return updateValueStrategy;
	}

}
