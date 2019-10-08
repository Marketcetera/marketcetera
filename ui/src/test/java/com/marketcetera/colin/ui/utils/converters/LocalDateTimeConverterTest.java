package com.marketcetera.colin.ui.utils.converters;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;

import org.junit.Test;

import com.marketcetera.colin.test.FormattingTest;

public class LocalDateTimeConverterTest extends FormattingTest {
	@Test
	public void formattingShoudBeLocaleIndependent() {
		LocalDateTimeConverter converter = new LocalDateTimeConverter();
		String result = converter.encode(LocalDateTime.of(2016, 11, 27, 22, 15, 33));
		assertEquals("27.11.2016 10:15 PM", result);
	}
}
