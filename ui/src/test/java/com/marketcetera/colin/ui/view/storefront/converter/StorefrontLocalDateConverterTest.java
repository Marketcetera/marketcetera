/**
 *
 */
package com.marketcetera.colin.ui.view.storefront.converter;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.Test;

import com.marketcetera.colin.test.FormattingTest;
import com.marketcetera.colin.ui.views.storefront.converters.StorefrontDate;
import com.marketcetera.colin.ui.views.storefront.converters.StorefrontLocalDateConverter;

public class StorefrontLocalDateConverterTest extends FormattingTest {

	@Test
	public void formattingShoudBeLocaleIndependent() {
		StorefrontLocalDateConverter converter = new StorefrontLocalDateConverter();
		StorefrontDate result = converter.encode(LocalDate.of(2017, 8, 22));
		assertEquals("Aug 22", result.getDay());
		assertEquals("2017-08-22", result.getDate());
		assertEquals("Tuesday", result.getWeekday());
	}
}
