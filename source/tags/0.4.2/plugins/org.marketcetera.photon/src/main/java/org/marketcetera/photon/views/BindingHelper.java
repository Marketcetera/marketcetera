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

	public void initStringToImageConverterBuilder(
			EnumStringConverterBuilder<String> converterBuilder,
			ILexerFIXImage[] lexerImages) {
		for (ILexerFIXImage lexerImage : lexerImages) {
			String image = lexerImage.getImage();
			String fixValue = lexerImage.getFIXStringValue();
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

}
