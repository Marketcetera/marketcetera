package com.marketcetera.colin.testbench;

import org.junit.Assert;
import org.junit.Test;

import com.marketcetera.colin.testbench.elements.components.DashboardLCounterLabelElement;
import com.marketcetera.colin.testbench.elements.ui.DashboardViewElement;
import com.marketcetera.colin.testbench.elements.ui.StorefrontViewElement;

public class DashboardViewIT extends AbstractIT<DashboardViewElement> {

	@Override
	protected DashboardViewElement openView() {
		StorefrontViewElement storefront = openLoginView().login("admin@vaadin.com", "admin");
		return storefront.getMenu().navigateToDashboard();
	}

	@Test
	public void checkRowsCount() {
		DashboardViewElement dashboardPage = openView();
		Assert.assertEquals(4, dashboardPage.getBoard().getRows().size());
	}

	@Test
	public void checkCounters() {
		DashboardViewElement dashboardPage = openView();
		int numLabels = dashboardPage.getBoard().getRows().get(0).$(DashboardLCounterLabelElement.class).all().size();
		Assert.assertEquals(4, numLabels);
	}

}
