package com.marketcetera.colin.testbench;

import static org.hamcrest.CoreMatchers.containsString;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.marketcetera.colin.testbench.elements.components.OrderCardElement;
import com.marketcetera.colin.testbench.elements.ui.OrderItemEditorElement;
import com.marketcetera.colin.testbench.elements.ui.StorefrontViewElement;
import com.marketcetera.colin.testbench.elements.ui.StorefrontViewElement.OrderEditorElement;
import com.marketcetera.colin.testbench.elements.ui.UsersViewElement;
import com.marketcetera.colin.ui.utils.WebUiConst;

public class StorefrontViewIT extends AbstractIT<StorefrontViewElement> {

	@Override
	protected StorefrontViewElement openView() {
		return openLoginView().login("admin@vaadin.com", "admin");
	}

	@Test
	public void editOrder() {
		StorefrontViewElement storefrontPage = openView();

		int orderIndex = new Random().nextInt(10);

		OrderCardElement order = storefrontPage.getOrderCard(orderIndex);
		Assert.assertNotNull(order);
		int initialCount = Integer.parseInt(order.getGoodsCount(0));

		order.click();
		ButtonElement editBtn = storefrontPage.getOrderDetails().getEditButton();
		editBtn.click();
		Assert.assertThat(getDriver().getCurrentUrl(), containsString(WebUiConst.PAGE_STOREFRONT_EDIT));

		OrderEditorElement orderEditor = storefrontPage.getOrderEditor();
		orderEditor.getOrderItemEditor(0).clickAmountFieldPlus();

		orderEditor.review();
		storefrontPage.getOrderDetails().getSaveButton().click();

		NotificationElement notification = $(NotificationElement.class).last();
		Assert.assertThat(notification.getText(), containsString("was updated"));

		order = storefrontPage.getOrderCard(orderIndex);
		Assert.assertNotNull(order);
		int currentCount = Integer.parseInt(order.getGoodsCount(0));
		Assert.assertEquals(initialCount + 1, currentCount);

	}

	@Test
	public void testDialogs() {
		StorefrontViewElement storefrontPage = openView();
		openAllDialogs(storefrontPage);

		UsersViewElement usersPage = storefrontPage.getMenu().navigateToUsers();
		storefrontPage = usersPage.getMenu().navigateToStorefront();

		openAllDialogs(storefrontPage);
	}

	private void openAllDialogs(StorefrontViewElement storefrontPage) {
		storefrontPage.getSearchBar().getCreateNewButton().click();
		Assert.assertTrue(storefrontPage.getDialog().get().isOpen());
		storefrontPage.getOrderEditor().cancel();
		Assert.assertFalse(storefrontPage.getDialog().get().isOpen());

		storefrontPage.getSearchBar().getCreateNewButton().click();
		Assert.assertTrue(storefrontPage.getDialog().get().isOpen());

		storefrontPage.getOrderEditor().cancel();
		Assert.assertFalse(storefrontPage.getDialog().get().isOpen());

		OrderCardElement order = storefrontPage.getOrderCard(0);
		Assert.assertNotNull(order);
		order.click();

		Assert.assertTrue(storefrontPage.getOrderDetails().isDisplayed());

		storefrontPage.getOrderDetails().getCancelButton().click();
		Assert.assertFalse(storefrontPage.getDialog().get().isOpen());
	}

	@Test
	public void testTextFieldValidation() {
		StorefrontViewElement storefrontPage = openView();

		int orderIndex = new Random().nextInt(10);

		OrderCardElement order = storefrontPage.getOrderCard(orderIndex);
		Assert.assertNotNull(order);
		order.click();

		ButtonElement editBtn = storefrontPage.getOrderDetails().getEditButton();
		editBtn.click();

		OrderEditorElement orderEditor = storefrontPage.getOrderEditor();
		TextFieldElement customerNameField = orderEditor.getCustomerNameField();
		testFieldOverflow(customerNameField);
		testClearRequiredField(customerNameField);
		testFieldOverflow(orderEditor.getCustomerDetailsField());

		OrderItemEditorElement firstOrderItemEditor = orderEditor.getOrderItemEditor(0);
		testFieldOverflow(firstOrderItemEditor.getCommentField());
	}

	private void testFieldOverflow(TextFieldElement textFieldElement) {
		textFieldElement.setValue(IntStream.range(0, 256).mapToObj(i -> "A").collect(Collectors.joining()));
		Assert.assertEquals("maximum length is 255 characters", getErrorMessage(textFieldElement));
	}

	private void testClearRequiredField(TextFieldElement textFieldElement) {
		textFieldElement.setValue("");
		Assert.assertEquals("must not be blank", getErrorMessage(textFieldElement));
	}

	private String getErrorMessage(TextFieldElement textFieldElement) {
		return textFieldElement.getPropertyString("errorMessage");
	}
}
