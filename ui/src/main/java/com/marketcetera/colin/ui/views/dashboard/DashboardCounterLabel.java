package com.marketcetera.colin.ui.views.dashboard;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.templatemodel.TemplateModel;
import com.marketcetera.colin.ui.views.storefront.beans.OrdersCountData;

@Tag("dashboard-counter-label")
@JsModule("./src/views/dashboard/dashboard-counter-label.js")
public class DashboardCounterLabel extends PolymerTemplate<TemplateModel> {

	@Id("title")
	private H4 title;

	@Id("subtitle")
	private Div subtitle;

	@Id("count")
	private Span count;

	public void setOrdersCountData(OrdersCountData data) {
		title.setText(data.getTitle());
		subtitle.setText(data.getSubtitle());
		count.setText(String.valueOf(data.getCount()));
	}
}
