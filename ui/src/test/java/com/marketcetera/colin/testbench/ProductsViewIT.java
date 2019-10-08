package com.marketcetera.colin.testbench;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.grid.testbench.GridTHTDElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.marketcetera.colin.testbench.elements.ui.ProductsViewElement;
import com.marketcetera.colin.testbench.elements.ui.StorefrontViewElement;

public class ProductsViewIT extends AbstractIT<ProductsViewElement> {

	private static Random r = new Random();

	@Override
	protected ProductsViewElement openView() {
		StorefrontViewElement storefront = openLoginView().login("admin@vaadin.com", "admin");
		return storefront.getMenu().navigateToProducts();
	}

	@Test
	public void editProductTwice() {
		ProductsViewElement productsPage = openView();

		String uniqueName = "Unique cake name " + r.nextInt();
		String initialPrice = "98.76";
		int rowNum = createProduct(productsPage, uniqueName, initialPrice);
		productsPage.openRowForEditing(rowNum);

		Assert.assertTrue(productsPage.isEditorOpen());
		String newValue = "New " + uniqueName;
		TextFieldElement nameField = productsPage.getProductName();
		nameField.setValue(newValue);

		productsPage.getEditorSaveButton().click();
		Assert.assertFalse(productsPage.isEditorOpen());
		GridElement grid = productsPage.getGrid();
		Assert.assertEquals(rowNum, grid.getCell(newValue).getRow());

		productsPage.openRowForEditing(rowNum);
		newValue = "The " + newValue;
		nameField = productsPage.getProductName();
		nameField.setValue(newValue);

		productsPage.getEditorSaveButton().click();
		Assert.assertFalse(productsPage.isEditorOpen());
		Assert.assertEquals(rowNum, grid.getCell(newValue).getRow());
	}

	@Test
	public void editProduct() {
		ProductsViewElement productsPage = openView();

		String url = getDriver().getCurrentUrl();

		String uniqueName = "Unique cake name " + r.nextInt();
		String initialPrice = "98.76";
		int rowIndex = createProduct(productsPage, uniqueName, initialPrice);

		productsPage.openRowForEditing(rowIndex);
		Assert.assertTrue(getDriver().getCurrentUrl().length() > url.length());

		Assert.assertTrue(productsPage.isEditorOpen());

		TextFieldElement price = productsPage.getPrice();
		Assert.assertEquals(initialPrice, price.getValue());

		price.setValue("123.45");

		productsPage.getEditorSaveButton().click();

		Assert.assertFalse(productsPage.isEditorOpen());

		Assert.assertTrue(getDriver().getCurrentUrl().endsWith("products"));

		productsPage.openRowForEditing(rowIndex);

		price = productsPage.getPrice(); // Requery the price element.
		Assert.assertEquals("123.45", price.getValue());

		// Return initial value
		price.setValue(initialPrice);

		productsPage.getEditorSaveButton().click();
		Assert.assertFalse(productsPage.isEditorOpen());
	}

	@Test
	public void testCancelConfirmationMessage() {
		ProductsViewElement productsPage = openView();

		productsPage.getNewItemButton().get().click();
		Assert.assertTrue(productsPage.isEditorOpen());
		productsPage.getProductName().setValue("Some name");
		productsPage.getEditorCancelButton().click();
		Assert.assertEquals(productsPage.getDiscardConfirmDialog().getHeaderText(), "Discard changes");
	}

	private int createProduct(ProductsViewElement productsPage, String name, String price) {
		productsPage.getSearchBar().getCreateNewButton().click();

		Assert.assertTrue(productsPage.isEditorOpen());

		TextFieldElement nameField = productsPage.getProductName();
		TextFieldElement priceField = productsPage.getPrice();

		nameField.setValue(name);
		priceField.setValue(price);

		productsPage.getEditorSaveButton().click();
		Assert.assertFalse(productsPage.isEditorOpen());

		return waitUntil((ExpectedCondition<GridTHTDElement>) wd -> productsPage.getGrid().getCell(name)).getRow();
	}

}
