package com.marketcetera.colin.ui.utils.converters;

import static com.marketcetera.colin.ui.dataproviders.DataProviderUtil.convertIfNotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.vaadin.flow.templatemodel.ModelEncoder;
import com.marketcetera.colin.backend.data.OrderState;

public class OrderStateConverter implements ModelEncoder<OrderState, String> {

	private Map<String, OrderState> values;

	public OrderStateConverter() {
		values = Arrays.stream(OrderState.values())
				.collect(Collectors.toMap(OrderState::toString, Function.identity()));
	}

	@Override
	public OrderState decode(String presentationValue) {
		return convertIfNotNull(presentationValue, values::get);
	}

	@Override
	public String encode(OrderState modelValue) {
		return convertIfNotNull(modelValue, OrderState::toString);
	}

}
