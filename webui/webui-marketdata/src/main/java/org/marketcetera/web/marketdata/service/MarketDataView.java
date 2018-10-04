package org.marketcetera.web.marketdata.service;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.marketcetera.event.HasInstrument;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.service.WebMessageService;
import org.marketcetera.web.view.ContentView;
import org.marketcetera.web.view.MenuContent;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
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
        CssLayout symbolLayout = new CssLayout();
        VerticalLayout dataLayout = new VerticalLayout();
        HorizontalLayout dataChartLayout = new HorizontalLayout();
        symbolLayout.addStyleName("layout-with-border");
        dataLayout.addStyleName("layout-with-border");
        dataChartLayout.addStyleName("layout-with-border");
        symbolButton = new Button();
        symbolButton.setEnabled(false);
        symbol = new TextField();
        instrumentLabel = new Label();
        symbol.setCaption("Symbol");
        symbol.setSizeUndefined();
        symbol.addTextChangeListener(inEvent-> {
            symbolButton.setEnabled(StringUtils.trimToNull(inEvent.getText()) != null);
        });
        symbolLayout.setWidth("100%");
        symbolButton.addClickListener(inEvent -> {
            requestMarketData();
        });
        symbolButton.setSizeUndefined();
        symbolButton.setIcon(FontAwesome.ARROW_CIRCLE_O_RIGHT);
        instrumentLabel.setSizeUndefined();
        symbolLayout.addComponents(symbol,
                                   symbolButton,
                                   instrumentLabel);
        lastText = new TextField();
        lastText.setCaption("Last");
        bidText = new TextField();
        bidText.setCaption("Bid");
        offerText = new TextField();
        offerText.setCaption("Offer");
        openText = new TextField();
        openText.setCaption("Open");
        highText = new TextField();
        highText.setCaption("High");
        lowText = new TextField();
        lowText.setCaption("Low");
        closeText = new TextField();
        closeText.setCaption("Close");
        dataLayout.addComponents(lastText,
                                 bidText,
                                 offerText,
                                 openText,
                                 highText,
                                 lowText,
                                 closeText);
        final CssLayout chartLayout = new CssLayout();
        chartLayout.addStyleName("layout-with-border");
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
        chart.setWidth("100%");
        chartLayout.setWidth("100%");
        chartLayout.addComponent(chart);
        chartLayout.setResponsive(true);
        chart.setResponsive(true);
        dataChartLayout.setWidth("100%");
        dataChartLayout.setResponsive(true);
        dataChartLayout.addComponents(dataLayout,
                                      chartLayout);
        addComponents(symbolLayout,
                      dataChartLayout);
        setWidth("100%");
        setResponsive(true);
    }
    private TextField lastText;
    private TextField bidText;
    private TextField offerText;
    private TextField openText;
    private TextField highText;
    private TextField lowText;
    private TextField closeText;
    private Label instrumentLabel;
    private Button symbolButton;
    private TextField symbol;
    private synchronized void requestMarketData()
    {
        MarketDataClientService marketDataClientService = MarketDataClientService.getInstance();
        cancelMarketData();
        MarketDataRequestBuilder requestBuilder = MarketDataRequestBuilder.newRequest();
        requestBuilder.withSymbols(StringUtils.trim(symbol.getValue()));
        marketDataRequestToken = marketDataClientService.request(requestBuilder.create(),
                                                                 new MarketDataListener() {
            /* (non-Javadoc)
             * @see org.marketcetera.marketdata.MarketDataListener#receiveMarketData(org.marketcetera.event.Event)
             */
            @Override
            public void receiveMarketData(org.marketcetera.event.Event inMarketDataEvent)
            {
                if(inMarketDataEvent instanceof HasInstrument) {
                    String fullSymbol = ((HasInstrument)inMarketDataEvent).getInstrument().getFullSymbol();
                    if(fullSymbol == null) {
                        instrumentLabel.setValue("");
                    } else {
                        if(!fullSymbol.equals(instrumentLabel.getValue())) {
                            instrumentLabel.setValue(fullSymbol);
                        }
                    }
                }
            }
            /* (non-Javadoc)
             * @see org.marketcetera.marketdata.MarketDataListener#onError(java.lang.Throwable)
             */
            @Override
            public void onError(Throwable inThrowable)
            {
                SLF4JLoggerProxy.warn(MarketDataView.this,
                                      inThrowable);
                UI.getCurrent().access(() -> {
                    Notification.show("Market Data Request",
                                      ExceptionUtils.getRootCauseMessage(inThrowable),
                                      Notification.Type.ERROR_MESSAGE);
                });
                cancelMarketData();
            }
        });
    }
    private void cancelMarketData()
    {
        MarketDataClientService marketDataClientService = MarketDataClientService.getInstance();
        if(marketDataRequestToken != null) {
            try {
                marketDataClientService.cancel(marketDataRequestToken);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            } finally {
                marketDataRequestToken = null;
            }
        }
    }
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
    private String marketDataRequestToken;
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
