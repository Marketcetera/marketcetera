package org.marketcetera.web.marketdata.detail.view;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import org.dussan.vaadin.dcharts.DCharts;
import org.dussan.vaadin.dcharts.base.elements.XYaxis;
import org.dussan.vaadin.dcharts.base.elements.XYseries;
import org.dussan.vaadin.dcharts.data.DataSeries;
import org.dussan.vaadin.dcharts.events.click.ChartDataClickEvent;
import org.dussan.vaadin.dcharts.events.click.ChartDataClickHandler;
import org.dussan.vaadin.dcharts.events.mouseenter.ChartDataMouseEnterEvent;
import org.dussan.vaadin.dcharts.events.mouseenter.ChartDataMouseEnterHandler;
import org.dussan.vaadin.dcharts.events.mouseleave.ChartDataMouseLeaveEvent;
import org.dussan.vaadin.dcharts.events.mouseleave.ChartDataMouseLeaveHandler;
import org.dussan.vaadin.dcharts.metadata.TooltipAxes;
import org.dussan.vaadin.dcharts.metadata.XYaxes;
import org.dussan.vaadin.dcharts.metadata.locations.TooltipLocations;
import org.dussan.vaadin.dcharts.metadata.renderers.AxisRenderers;
import org.dussan.vaadin.dcharts.options.Axes;
import org.dussan.vaadin.dcharts.options.Highlighter;
import org.dussan.vaadin.dcharts.options.Options;
import org.dussan.vaadin.dcharts.options.Series;
import org.dussan.vaadin.dcharts.renderers.series.OhlcRenderer;
import org.dussan.vaadin.dcharts.renderers.tick.AxisTickRenderer;
import org.marketcetera.core.BigDecimalUtil;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.XmlService;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.HasInstrument;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.AssetClass;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.marketdata.service.MarketDataClientService;
import org.marketcetera.web.service.WebMessageService;
import org.marketcetera.web.view.AbstractContentView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/* $License$ */

/**
 * Provides a view for system Fix Sessions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@EnableAutoConfiguration
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MarketDataDetailView
        extends AbstractContentView
        implements MarketDataListener
{
    /**
     * Create a new MarketDataView instance.
     *
     * @param inParent a <code>Window</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    public MarketDataDetailView(Window inParent,
                                NewWindowEvent inEvent,
                                Properties inViewProperties)
    {
        super(inParent,
              inEvent,
              inViewProperties);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#attach()
     */
    @Override
    public void attach()
    {
        super.attach();
        setSizeFull();
        VerticalLayout dataLayout = new VerticalLayout();
        VerticalLayout chartLayout = new VerticalLayout();
        CssLayout dataChartLayout = new CssLayout();
        dataLayout.addStyleName("layout-with-border2");
        dataLayout.setWidthUndefined();
        dataLayout.setHeightUndefined();
        chartLayout.addStyleName("layout-with-border2");
        chartLayout.setWidthUndefined();
        chartLayout.setHeightUndefined();
        dataChartLayout.addStyleName("layout-with-border3");
        lastText = new TextField();
        lastText.setCaption("Last");
        lastText.setWidth("100%");
        bidText = new TextField();
        bidText.setCaption("Bid");
        bidText.setWidth("100%");
        offerText = new TextField();
        offerText.setCaption("Offer");
        offerText.setWidth("100%");
        openText = new TextField();
        openText.setCaption("Open");
        openText.setWidth("100%");
        highText = new TextField();
        highText.setCaption("High");
        highText.setWidth("100%");
        lowText = new TextField();
        lowText.setCaption("Low");
        lowText.setWidth("100%");
        closeText = new TextField();
        closeText.setCaption("Close");
        closeText.setWidth("100%");
        dataLayout.addComponents(lastText,
                                 bidText,
                                 offerText,
                                 openText,
                                 highText,
                                 lowText,
                                 closeText);
        setupChart();
        chart.setSizeFull();
        chartLayout.addComponent(chart);
        chartLayout.setSizeFull();
        dataChartLayout.addComponents(dataLayout,
                                      chartLayout);
        dataChartLayout.setSizeFull();
        addComponents(dataChartLayout);
//        setWidth("100%");
        requestMarketData();
    }
    private void setupChart()
    {
        // https://www.investopedia.com/articles/trading/10/data-based-intraday-chart-intervals.asp
        DataSeries dataSeries = new DataSeries();
        dataSeries.newSeries()
            .add("04/13/2009",120.01,124.25,115.76,123.42,"s")
            .add("04/14/2009",121.73,127.2,118.6,123.9,"t");
        Axes axes = new Axes()
                .addAxis(new XYaxis().setRenderer(AxisRenderers.DATE))
                .addAxis(new XYaxis(XYaxes.Y).setTickOptions(new AxisTickRenderer().setPrefix("$")));
        Series series = new Series().addSeries(new XYseries().setRendererOptions(new OhlcRenderer().setCandleStick(true)));
        Highlighter highlighter = new Highlighter()
                .setShow(true)
                .setShowTooltip(true)
                .setTooltipAlwaysVisible(true)
                .setKeepTooltipInsideChart(true)
                .setTooltipLocation(TooltipLocations.NORTH_WEST)
                .setTooltipAxes(TooltipAxes.XY_OHLC);
        Options options = new Options().setAxes(axes).setSeries(series).setHighlighter(highlighter);
        chart = new DCharts();
        chart.setEnableChartDataMouseEnterEvent(true);
        chart.setEnableChartDataMouseLeaveEvent(true);
        chart.setEnableChartDataClickEvent(true);
        chart.addHandler(new ChartDataMouseEnterHandler() {
            @Override
            public void onChartDataMouseEnter(ChartDataMouseEnterEvent inEvent)
            {
                System.out.println("CHART DATA MOUSE ENTER: " + inEvent.getChartData());
            }
        });
        chart.addHandler(new ChartDataMouseLeaveHandler() {
            @Override
            public void onChartDataMouseLeave(ChartDataMouseLeaveEvent inEvent)
            {
                System.out.println("CHART DATA MOUSE LEAVE: " + inEvent.getChartData());
            }
        });
        chart.addHandler(new ChartDataClickHandler() {
            @Override
            public void onChartDataClick(ChartDataClickEvent inEvent)
            {
                System.out.println("CHART DATA MOUSE CLICK: " + inEvent.getChartData());
            }
        });
        chart.setDataSeries(dataSeries).setOptions(options).show();
    }
    private DCharts chart;
    /* (non-Javadoc)
     * @see com.vaadin.ui.AbstractComponent#detach()
     */
    @Override
    public void detach()
    {
        cancelMarketData();
        super.detach();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataListener#receiveMarketData(org.marketcetera.event.Event)
     */
    @Override
    public void receiveMarketData(org.marketcetera.event.Event inEvent)
    {
        if(isAttached()) {
            SLF4JLoggerProxy.trace(this,
                                   "Received {}",
                                   inEvent);
            if(inEvent instanceof BidEvent) {
                BidEvent bidEvent = (BidEvent)inEvent;
                bidText.setValue(BigDecimalUtil.renderCurrency(bidEvent.getPrice()));
            } else if(inEvent instanceof AskEvent) {
                AskEvent askEvent = (AskEvent)inEvent;
                offerText.setValue(BigDecimalUtil.renderCurrency(askEvent.getPrice()));
            } else if(inEvent instanceof TradeEvent) {
                TradeEvent tradeEvent = (TradeEvent)inEvent;
                lastText.setValue(BigDecimalUtil.renderCurrency(tradeEvent.getPrice()));
            } else if(inEvent instanceof MarketstatEvent) {
                MarketstatEvent marketstatEvent = (MarketstatEvent)inEvent;
                openText.setValue(BigDecimalUtil.renderCurrency(marketstatEvent.getOpen()));
                highText.setValue(BigDecimalUtil.renderCurrency(marketstatEvent.getHigh()));
                lowText.setValue(BigDecimalUtil.renderCurrency(marketstatEvent.getLow()));
                closeText.setValue(BigDecimalUtil.renderCurrency(marketstatEvent.getClose()));
            } else {
                return;
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataListener#onError(java.lang.Throwable)
     */
    @Override
    public void onError(Throwable inThrowable)
    {
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        if(getNewWindowEvent() instanceof HasInstrument) {
            instrument = ((HasInstrument)getNewWindowEvent()).getInstrument();
        } else {
            String rawInstrument = getViewProperties().getProperty(Instrument.class.getCanonicalName());
            try {
                instrument = xmlService.unmarshall(rawInstrument);
            } catch (JAXBException e) {
                throw new UnsupportedOperationException("Launching event must have an instrument: " + getNewWindowEvent().getClass().getSimpleName() + " or view properties must include an instrument: " + PlatformServices.getMessage(e));
            }
        }
        try {
            getViewProperties().setProperty(Instrument.class.getCanonicalName(),
                                            xmlService.marshall(instrument));
        } catch (JAXBException e) {
            throw new UnsupportedOperationException("Unable to persist instrument in the view properties: " + PlatformServices.getMessage(e));
        }
    }
    private void requestMarketData()
    {
        String symbol = instrument.getFullSymbol();
        AssetClass assetClass = AssetClass.getFor(instrument.getSecurityType());
        MarketDataClientService marketDataClientService = serviceManager.getService(MarketDataClientService.class);
        MarketDataRequestBuilder requestBuilder = MarketDataRequestBuilder.newRequest();
        requestBuilder.withAssetClass(assetClass).withSymbols(symbol).withContent(Content.LATEST_TICK,
                                                                                  Content.MARKET_STAT,
                                                                                  Content.TOP_OF_BOOK);
        marketDataRequestToken = marketDataClientService.request(requestBuilder.create(),
                                                    this);
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
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent inEvent)
    {
    }
    private String marketDataRequestToken;
    /**
     * Get the webMessageService value.
     *
     * @return a <code>WebMessageService</code> value
     */
    WebMessageService getWebMessageService()
    {
        return webMessageService;
    }
    /**
     * Sets the webMessageService value.
     *
     * @param inWebMessageService a <code>WebMessageService</code> value
     */
    void setWebMessageService(WebMessageService inWebMessageService)
    {
        webMessageService = inWebMessageService;
    }
    /**
     * provides access to web message services
     */
    private WebMessageService webMessageService;
    private TextField lastText;
    private TextField bidText;
    private TextField offerText;
    private TextField openText;
    private TextField highText;
    private TextField lowText;
    private TextField closeText;
    /**
     * instrument for which to display detailed market data
     */
    private Instrument instrument;
    /**
     * global name of this view
     */
    private static final String NAME = "Market Data Detail View";
    @Autowired
    private XmlService xmlService;
    private static final long serialVersionUID = 1901286026590258969L;
}
