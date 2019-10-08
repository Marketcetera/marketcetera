package com.marketcetera.colin.testbench;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import com.marketcetera.colin.testbench.elements.ui.DashboardViewElement;
import com.marketcetera.colin.testbench.elements.ui.LoginViewElement;
import com.marketcetera.colin.testbench.elements.ui.StorefrontViewElement;

public class LoginIT extends AbstractIT<LoginViewElement> {

	@Test
	public void loginWorks() {
		LoginViewElement loginView = openLoginView();
		assertEquals("Email", loginView.getUsernameLabel());
		loginView.login("barista@vaadin.com", "barista");
	}

	@Test
	public void logout() {
		LoginViewElement loginView = openLoginView();
		StorefrontViewElement storefront = loginView.login("barista@vaadin.com", "barista");
		storefront.getMenu().logout();
		Assert.assertTrue(getDriver().getCurrentUrl().endsWith("login"));
	}

	@Test
	public void loginToNotDefaultUrl() {
		LoginViewElement loginView = openLoginView(getDriver(), APP_URL + "dashboard");
		DashboardViewElement dashboard = loginView.login("admin@vaadin.com", "admin", DashboardViewElement.class);
		Assert.assertNotNull(dashboard);
	}

	@Test
	public void openLoginAfterLoggedIn() {
		loginToNotDefaultUrl();
		// Navigating to /login after user is logged in will forward to storefront view
		driver.get(APP_URL + "login");
		$(StorefrontViewElement.class).onPage().waitForFirst();
		Assert.assertTrue($(LoginViewElement.class).all().isEmpty());
	}

	@Override
	protected LoginViewElement openView() {
		return openLoginView();
	}

}