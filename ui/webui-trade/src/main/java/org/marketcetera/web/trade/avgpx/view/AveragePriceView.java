package org.marketcetera.web.trade.avgpx.view;

import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.trade.AverageFillPrice;
import org.marketcetera.trade.TradePermissions;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.SessionUser;
import org.marketcetera.web.converters.DecimalConverter;
import org.marketcetera.web.converters.InstrumentConverter;
import org.marketcetera.web.converters.SideConverter;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.trade.event.TradeOrderEvent;
import org.marketcetera.web.view.AbstractPagedGridView;
import org.marketcetera.web.view.PagedDataContainer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Window;

/* $License$ */

/**
 * Provides a view for average prices.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AveragePriceView
        extends AbstractPagedGridView<AverageFillPrice>
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.AbstractGridView#attach()
     */
    @Override
    public void attach()
    {
        super.attach();
        getCreateNewButton().setVisible(false);
        getActionSelect().setNullSelectionAllowed(false);
        getActionSelect().setReadOnly(true);
        getGrid().addSelectionListener(inEvent -> {
            AverageFillPrice selectedObject = getSelectedItem();
            getActionSelect().removeAllItems();
            if(selectedObject == null) {
                getActionSelect().setReadOnly(true);
            } else {
                getActionSelect().setReadOnly(false);
                if(authzHelperService.hasPermission(TradePermissions.SendOrderAction)) {
                    getActionSelect().addItem(ACTION_TRADE);
                }
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.AbstractGridView#setGridColumns()
     */
    @Override
    protected void setGridColumns()
    {
        getGrid().setColumns("instrument",
                             "side",
                             "cumulativeQuantity",
                             "averagePrice");
        // avg px and cum qty are not sortable because they don't appear in the group-by clause of the database query
        getGrid().getColumn("averagePrice").setConverter(DecimalConverter.instance).setHeaderCaption("Avg Px").setSortable(false);
        getGrid().getColumn("cumulativeQuantity").setHeaderCaption("Cum Qty").setSortable(false);
        getGrid().getColumn("instrument").setConverter(InstrumentConverter.instance);
        getGrid().getColumn("side").setConverter(SideConverter.instance);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
        return NAME;
    }
    /**
     * Create a new AveragePriceView instance.
     *
     * @param inParentWindow a <code>Window</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    public AveragePriceView(Window inParentWindow,
                            NewWindowEvent inEvent,
                            Properties inViewProperties)
    {
        super(inParentWindow,
              inEvent,
              inViewProperties);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#createBeanItemContainer()
     */
    @Override
    protected PagedDataContainer<AverageFillPrice> createDataContainer()
    {
        return new AveragePricePagedDataContainer(this);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#getViewSubjectName()
     */
    @Override
    protected String getViewSubjectName()
    {
        return "Average Price";
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#onActionSelect(com.vaadin.data.Property.ValueChangeEvent)
     */
    @Override
    protected void onActionSelect(ValueChangeEvent inEvent)
    {
        AverageFillPrice selectedItem = getSelectedItem();
        if(selectedItem == null || inEvent.getProperty().getValue() == null) {
            return;
        }
        String action = String.valueOf(inEvent.getProperty().getValue());
        SLF4JLoggerProxy.info(this,
                              "{}: {} {} '{}'",
                              SessionUser.getCurrentUser().getUsername(),
                              getViewName(),
                              action,
                              selectedItem);
        switch(action) {
            case ACTION_TRADE:
                    String averageFillPriceXml;
                    try {
                        averageFillPriceXml = xmlService.marshall(selectedItem);
                    } catch (JAXBException e) {
                        Notification.show("Unable to trade " + selectedItem + ": " + PlatformServices.getMessage(e),
                                          Type.ERROR_MESSAGE);
                        return;
                    }
                    Properties tradeProperties = new Properties();
                    tradeProperties.setProperty(AverageFillPrice.class.getCanonicalName(),
                                                averageFillPriceXml);
                    TradeOrderEvent tradeOrderEvent = applicationContext.getBean(TradeOrderEvent.class,
                                                                                 selectedItem,
                                                                                 tradeProperties);
                    webMessageService.post(tradeOrderEvent);
                    return;
            default:
                throw new UnsupportedOperationException("Unsupported action: " + action);
        }
    }
    /**
     * trade action
     */
    private static final String ACTION_TRADE = "Trade";
    /**
     * global name of this view
     */
    private static final String NAME = "Average Price View";
    private static final long serialVersionUID = 6606975159117626231L;
}
