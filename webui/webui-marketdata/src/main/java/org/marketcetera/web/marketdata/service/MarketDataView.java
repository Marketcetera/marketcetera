package org.marketcetera.web.marketdata.service;

import org.dussan.vaadin.dcharts.DCharts;
import org.dussan.vaadin.dcharts.base.elements.XYaxis;
import org.dussan.vaadin.dcharts.data.DataSeries;
import org.dussan.vaadin.dcharts.data.Ticks;
import org.dussan.vaadin.dcharts.metadata.renderers.AxisRenderers;
import org.dussan.vaadin.dcharts.metadata.renderers.SeriesRenderers;
import org.dussan.vaadin.dcharts.options.Axes;
import org.dussan.vaadin.dcharts.options.Highlighter;
import org.dussan.vaadin.dcharts.options.Options;
import org.dussan.vaadin.dcharts.options.SeriesDefaults;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.service.WebMessageService;
import org.marketcetera.web.view.ContentView;
import org.marketcetera.web.view.MenuContent;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/* $License$ */

/**
 * Provides a view for system Fix Sessions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
public class MarketDataView
        extends CssLayout
        implements ContentView,MenuContent
{
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#attach()
     */
    @Override
    public void attach()
    {
        super.attach();
        setSizeFull();
        symbol = new TextField();
        symbol.setCaption("Symbol");
        symbol.setSizeUndefined();
        CssLayout symbolLayout = new CssLayout();
        symbolLayout.setWidth("100%");
        Button symbolButton = new Button();
        symbolButton.addClickListener(inEvent -> {
            // TODO resolve symbol, set instrument label, launch market data request
        });
        symbolButton.setSizeUndefined();
        symbolButton.setIcon(FontAwesome.ARROW_CIRCLE_O_RIGHT);
        Label instrumentLabel = new Label();
        instrumentLabel.setSizeUndefined();
        symbolLayout.addComponents(symbol,
                                   symbolButton,
                                   instrumentLabel);
        grid = new Grid();
        grid.setSelectionMode(SelectionMode.NONE);
        grid.setHeightMode(HeightMode.CSS);
        grid.setWidth("100%");
        grid.setColumns("Exchange","Trade\nPx","Trade\nSize","Bid\nSize","Bid\nPx","Ask\nPx","Ask\nSize","Open","High","Low","Close","Vol","VWAP");
        final VerticalLayout layout = new VerticalLayout();
        DataSeries dataSeries = new DataSeries().add(1, 5, 8, 2, 3);
        SeriesDefaults seriesDefaults = new SeriesDefaults().setRenderer(SeriesRenderers.BAR);
        Axes axes = new Axes().addAxis(
                                       new XYaxis().setRenderer(AxisRenderers.CATEGORY)
                                       .setTicks(new Ticks()
                                                 .add("a", "b", "c", "d", "e")));

        Highlighter highlighter = new Highlighter()
                .setShow(false);

        Options options = new Options()
                .setSeriesDefaults(seriesDefaults)
                .setAxes(axes)
                .setHighlighter(highlighter);

        DCharts chart = new DCharts()
                .setDataSeries(dataSeries)
                .setOptions(options)
                .show();
        layout.addComponent(chart);
        addComponents(symbolLayout,
                      grid,
                      layout);
    }
    private TextField symbol;
    private Grid grid;
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
        return NAME;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getMenuCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Market Data";
    }
    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent inEvent)
    {
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getWeight()
     */
    @Override
    public int getWeight()
    {
        return 500;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getCategory()
     */
    @Override
    public MenuContent getCategory()
    {
        return null;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getMenuIcon()
     */
    @Override
    public Resource getMenuIcon()
    {
        return FontAwesome.AREA_CHART;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getCommand()
     */
    @Override
    public Command getCommand()
    {
        return new MenuBar.Command() {
            @Override
            public void menuSelected(MenuItem inSelectedItem)
            {
                webMessageService.post(new NewWindowEvent() {
                    @Override
                    public String getWindowTitle()
                    {
                        return getMenuCaption();
                    }
                    @Override
                    public Component getComponent()
                    {
                        return MarketDataView.this;
                    }
                });
            }
            private static final long serialVersionUID = 49365592058433460L;
        };
    }
    /**
     * provides access to web message services
     */
    @Autowired
    private WebMessageService webMessageService;
    /**
     * global name of this view
     */
    private static final String NAME = "Cluster View";
    /**
     * edit action label
     */
    private final String ACTION_EDIT = "Edit";
    /**
     * start action label
     */
    private final String ACTION_START = "Start";
    /**
     * stop action label
     */
    private final String ACTION_STOP = "Stop";
    /**
     * enable action label
     */
    private final String ACTION_ENABLE = "Enable";
    /**
     * disable action label
     */
    private final String ACTION_DISABLE = "Disable";
    /**
     * delete action label
     */
    private final String ACTION_DELETE = "Delete";
    /**
     * edit sequence numbers label
     */
    private final String ACTION_SEQUENCE = "Update Sequence Numbers";
    private static final long serialVersionUID = 1901286026590258969L;
}
