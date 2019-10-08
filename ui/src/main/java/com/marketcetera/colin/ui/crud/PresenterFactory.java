/**
 *
 */
package com.marketcetera.colin.ui.crud;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.marketcetera.colin.app.security.CurrentUser;
import com.marketcetera.colin.backend.data.entity.Order;
import com.marketcetera.colin.backend.service.OrderService;
import com.marketcetera.colin.ui.views.storefront.StorefrontView;

@Configuration
public class PresenterFactory {

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public EntityPresenter<Order, StorefrontView> orderEntityPresenter(OrderService crudService, CurrentUser currentUser) {
		return new EntityPresenter<>(crudService, currentUser);
	}

}
