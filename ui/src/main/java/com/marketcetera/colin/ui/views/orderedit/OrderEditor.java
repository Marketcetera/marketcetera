package com.marketcetera.colin.ui.views.orderedit;

import static com.marketcetera.colin.ui.dataproviders.DataProviderUtil.createItemLabelGenerator;

import java.time.LocalTime;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.validator.BeanValidator;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.templatemodel.TemplateModel;
import com.marketcetera.colin.backend.data.OrderState;
import com.marketcetera.colin.backend.data.entity.Order;
import com.marketcetera.colin.backend.data.entity.PickupLocation;
import com.marketcetera.colin.backend.data.entity.Product;
import com.marketcetera.colin.backend.data.entity.User;
import com.marketcetera.colin.backend.service.PickupLocationService;
import com.marketcetera.colin.backend.service.ProductService;
import com.marketcetera.colin.ui.crud.CrudEntityDataProvider;
import com.marketcetera.colin.ui.dataproviders.DataProviderUtil;
import com.marketcetera.colin.ui.events.CancelEvent;
import com.marketcetera.colin.ui.utils.FormattingUtils;
import com.marketcetera.colin.ui.utils.converters.LocalTimeConverter;
import com.marketcetera.colin.ui.views.storefront.events.ReviewEvent;
import com.marketcetera.colin.ui.views.storefront.events.ValueChangeEvent;

@Tag("order-editor")
@JsModule("./src/views/orderedit/order-editor.js")
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OrderEditor extends PolymerTemplate<OrderEditor.Model> {

	public interface Model extends TemplateModel {
		void setTotalPrice(String totalPrice);

		void setStatus(String status);
	}

	@Id("title")
	private H2 title;

	@Id("metaContainer")
	private Div metaContainer;

	@Id("orderNumber")
	private Span orderNumber;

	@Id("status")
	private ComboBox<OrderState> status;

	@Id("dueDate")
	private DatePicker dueDate;

	@Id("dueTime")
	private ComboBox<LocalTime> dueTime;

	@Id("pickupLocation")
	private ComboBox<PickupLocation> pickupLocation;

	@Id("customerName")
	private TextField customerName;

	@Id("customerNumber")
	private TextField customerNumber;

	@Id("customerDetails")
	private TextField customerDetails;

	@Id("cancel")
	private Button cancel;

	@Id("review")
	private Button review;

	@Id("itemsContainer")
	private Div itemsContainer;

	private OrderItemsEditor itemsEditor;

	private User currentUser;

	private BeanValidationBinder<Order> binder = new BeanValidationBinder<>(Order.class);

	private final LocalTimeConverter localTimeConverter = new LocalTimeConverter();

	@Autowired
	public OrderEditor(PickupLocationService locationService, ProductService productService) {
		DataProvider<PickupLocation, String> locationDataProvider = new CrudEntityDataProvider<>(locationService);
		DataProvider<Product, String> productDataProvider = new CrudEntityDataProvider<>(productService);
		itemsEditor = new OrderItemsEditor(productDataProvider);

		itemsContainer.add(itemsEditor);

		cancel.addClickListener(e -> fireEvent(new CancelEvent(this, false)));
		review.addClickListener(e -> fireEvent(new ReviewEvent(this)));

		status.setItemLabelGenerator(createItemLabelGenerator(OrderState::getDisplayName));
		status.setDataProvider(DataProvider.ofItems(OrderState.values()));
		status.addValueChangeListener(
				e -> getModel().setStatus(DataProviderUtil.convertIfNotNull(e.getValue(), OrderState::name)));
		binder.forField(status)
				.withValidator(new BeanValidator(Order.class, "state"))
				.bind(Order::getState, (o, s) -> {
					o.changeState(currentUser, s);
				});

		dueDate.setRequired(true);
		binder.bind(dueDate, "dueDate");

		SortedSet<LocalTime> timeValues = IntStream.rangeClosed(8, 16).mapToObj(i -> LocalTime.of(i, 0))
				.collect(Collectors.toCollection(TreeSet::new));
		dueTime.setItems(timeValues);
		dueTime.setItemLabelGenerator(localTimeConverter::encode);
		binder.bind(dueTime, "dueTime");

		pickupLocation.setItemLabelGenerator(createItemLabelGenerator(PickupLocation::getName));
		pickupLocation.setDataProvider(locationDataProvider);
		binder.bind(pickupLocation, "pickupLocation");
		pickupLocation.setRequired(false);

		customerName.setRequired(true);
		binder.bind(customerName, "customer.fullName");

		customerNumber.setRequired(true);
		binder.bind(customerNumber, "customer.phoneNumber");

		binder.bind(customerDetails, "customer.details");

		itemsEditor.setRequiredIndicatorVisible(true);
		binder.bind(itemsEditor, "items");

		itemsEditor.addPriceChangeListener(e -> setTotalPrice(e.getTotalPrice()));

		ComponentUtil.addListener(itemsEditor, ValueChangeEvent.class, e -> review.setEnabled(hasChanges()));
		binder.addValueChangeListener(e -> {
			if (e.getOldValue() != null) {
				review.setEnabled(hasChanges());
			}
		});
	}

	public boolean hasChanges() {
		return binder.hasChanges() || itemsEditor.hasChanges();
	}

	public void clear() {
		binder.readBean(null);
		itemsEditor.setValue(null);
	}

	public void close() {
		setTotalPrice(0);
	}

	public void write(Order order) throws ValidationException {
		binder.writeBean(order);
	}

	public void read(Order order, boolean isNew) {
		binder.readBean(order);

		this.orderNumber.setText(isNew ? "" : order.getId().toString());
		title.setVisible(isNew);
		metaContainer.setVisible(!isNew);

		if (order.getState() != null) {
			getModel().setStatus(order.getState().name());
		}

		review.setEnabled(false);
	}

	public Stream<HasValue<?, ?>> validate() {
		Stream<HasValue<?, ?>> errorFields = binder.validate().getFieldValidationErrors().stream()
				.map(BindingValidationStatus::getField);

		return Stream.concat(errorFields, itemsEditor.validate());
	}

	public Registration addReviewListener(ComponentEventListener<ReviewEvent> listener) {
		return addListener(ReviewEvent.class, listener);
	}

	public Registration addCancelListener(ComponentEventListener<CancelEvent> listener) {
		return addListener(CancelEvent.class, listener);
	}

	private void setTotalPrice(int totalPrice) {
		getModel().setTotalPrice(FormattingUtils.formatAsCurrency(totalPrice));
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}
}
