package org.marketcetera.web.trade.openorders.view;

import java.util.Locale;
import java.util.Properties;

import org.marketcetera.trade.Instrument;
import org.marketcetera.web.converters.DateConverter;
import org.marketcetera.web.converters.DecimalConverter;
import org.marketcetera.web.converters.UserConverter;
import org.marketcetera.web.trade.executionreport.AbstractHasFixMessageView;
import org.marketcetera.web.view.PagedDataContainer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.spring.annotation.SpringComponent;

/* $License$ */

/**
 * Provides a view for open orders.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OpenOrderView
        extends AbstractHasFixMessageView<DisplayOrderSummary>
{
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
        return NAME;
    }
    /**
     * Create a new OpenOrderView instance.
     *
     * @param inViewProperties a <code>Properties</code> value
     */
    public OpenOrderView(Properties inViewProperties)
    {
        super(inViewProperties);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#setGridColumns()
     */
    @Override
    protected void setGridColumns()
    {
        getGrid().setColumns("transactTime",
                             "orderId",
                             "orderStatus",
                             "side",
                             "instrument",
                             "orderQuantity",
                             "cumulativeQuantity",
                             "leavesQuantity",
                             "orderPrice",
                             "averagePrice",
                             "account",
                             "lastQuantity",
                             "lastPrice",
                             "actor");
        getGrid().getColumn("actor").setHeaderCaption("User").setConverter(UserConverter.instance);
        getGrid().getColumn("instrument").setConverter(new Converter<String,Instrument>() {
            @Override
            public Instrument convertToModel(String inValue,
                                             Class<? extends Instrument> inTargetType,
                                             Locale inLocale)
                    throws ConversionException
            {
                throw new UnsupportedOperationException(); // TODO
            }
            @Override
            public String convertToPresentation(Instrument inValue,
                                                Class<? extends String> inTargetType,
                                                Locale inLocale)
                    throws ConversionException
            {
                return inValue.getFullSymbol();
            }
            @Override
            public Class<Instrument> getModelType()
            {
                return Instrument.class;
            }
            @Override
            public Class<String> getPresentationType()
            {
                return String.class;
            }
            private static final long serialVersionUID = 2362260803441310303L;
        });
        getGrid().getColumn("orderPrice").setConverter(DecimalConverter.instance).setHeaderCaption("Ord Px");
        getGrid().getColumn("averagePrice").setConverter(DecimalConverter.instance).setHeaderCaption("Avg Px");
        getGrid().getColumn("lastPrice").setConverter(DecimalConverter.instance).setHeaderCaption("Last Px");
        getGrid().getColumn("lastQuantity").setHeaderCaption("Last Qty");
        getGrid().getColumn("leavesQuantity").setHeaderCaption("Leaves Qty");
        getGrid().getColumn("orderQuantity").setHeaderCaption("Ord Qty");
        getGrid().getColumn("transactTime").setConverter(DateConverter.instance);
        getGrid().getColumn("cumulativeQuantity").setHeaderCaption("Cum Qty");
        getGrid().getColumn("orderStatus").setHeaderCaption("Ord Status");
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#createBeanItemContainer()
     */
    @Override
    protected PagedDataContainer<DisplayOrderSummary> createDataContainer()
    {
        return new OrderSummaryPagedDataContainer(this);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#getViewSubjectName()
     */
    @Override
    protected String getViewSubjectName()
    {
        return "Open Orders";
    }
    /**
     * global name of this view
     */
    private static final String NAME = "Open Orders View";
    private static final long serialVersionUID = 1901286026590258969L;
}
