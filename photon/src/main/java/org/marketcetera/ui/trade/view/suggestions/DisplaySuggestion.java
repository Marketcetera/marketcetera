package org.marketcetera.ui.trade.view.suggestions;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.trade.HasInstrument;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OrderSingleSuggestion;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.Suggestion;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/* $License$ */

/**
 * Provides a display-oriented {@link Suggestion} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DisplaySuggestion
        implements HasInstrument
{
    /**
     * Create a new DisplaySuggestion instance.
     *
     * @param inSuggestion a <code>Suggestion</code> value
     */
    public DisplaySuggestion(Suggestion inSuggestion)
    {
        sourceProperty.set(inSuggestion);
        identifierProperty.set(inSuggestion.getIdentifier());
        scoreProperty.set(inSuggestion.getScore());
        if(inSuggestion instanceof OrderSingleSuggestion) {
            OrderSingleSuggestion orderSingleSuggestion = (OrderSingleSuggestion)inSuggestion;
            instrumentProperty.set(orderSingleSuggestion.getOrder().getInstrument());
            orderTypeProperty.set(orderSingleSuggestion.getOrder().getOrderType());
            priceProperty.set(orderSingleSuggestion.getOrder().getPrice());
            quantityProperty.set(orderSingleSuggestion.getOrder().getQuantity());
            sideProperty.set(orderSingleSuggestion.getOrder().getSide());
            timestampProperty.set(new Date()); // TODO should be the timestamp from the suggestion, probably
        } else {
            throw new UnsupportedOperationException("Unsupported suggestion type: " + inSuggestion.getClass().getSimpleName());
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasInstrument#getInstrument()
     */
    @Override
    public Instrument getInstrument()
    {
        return instrumentProperty.get();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasInstrument#setInstrument(org.marketcetera.trade.Instrument)
     */
    @Override
    public void setInstrument(Instrument inInstrument)
    {
        instrumentProperty.set(inInstrument);
    }
    /**
     * Get the identifier value.
     *
     * @return a <code>String</code> value
     */
    public String getIdentifier()
    {
        return identifierProperty().get();
    }
    /**
     * Get the identifierProperty value.
     *
     * @return a <code>ReadOnlyStringProperty</code> value
     */
    public ReadOnlyStringProperty identifierProperty()
    {
        return identifierProperty;
    }
    /**
     * Get the score property.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getScore()
    {
        return scoreProperty().get();
    }
    /**
     * Get the scoreProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;BigDecimal&gt;</code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> scoreProperty()
    {
        return scoreProperty;
    }
    /**
     * Get the side property.
     *
     * @return a <code>Side</code> value
     */
    public Side getSide()
    {
        return sideProperty().get();
    }
    /**
     * Get the sideProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;Side&gt;</code> value
     */
    public ReadOnlyObjectProperty<Side> sideProperty()
    {
        return sideProperty;
    }
    /**
     * Get the quantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getQuantity()
    {
        return quantityProperty().get();
    }
    /**
     * Get the quantityProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;BigDecimal&gt;</code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> quantityProperty()
    {
        return quantityProperty;
    }
    /**
     * Get the price value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getPrice()
    {
        return priceProperty().get();
    }
    /**
     * Get the priceProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;BigDecimal&gt;</code> value
     */
    public ReadOnlyObjectProperty<BigDecimal> priceProperty()
    {
        return priceProperty;
    }
    /**
     * Get the instrumentProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;Instrument&gt;</code> value
     */
    public ReadOnlyObjectProperty<Instrument> instrumentProperty()
    {
        return instrumentProperty;
    }
    /**
     * Get the order type value.
     *
     * @return an <code>OrderType</code> value
     */
    public OrderType getOrderType()
    {
        return orderTypeProperty().get();
    }
    /**
     * Get the orderTypeProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;OrderType&gt;</code> value
     */
    public ReadOnlyObjectProperty<OrderType> orderTypeProperty()
    {
        return orderTypeProperty;
    }
    /**
     * Get the timestamp value.
     *
     * @return a <code>Date</code> value
     */
    public Date getTimestamp()
    {
        return timestampProperty().get();
    }
    /**
     * Get the timestampProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;Date&gt;</code> value
     */
    public ReadOnlyObjectProperty<Date> timestampProperty()
    {
        return timestampProperty;
    }
    /**
     * Get the sourceProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;Suggestion&gt;</code> value
     */
    public ReadOnlyObjectProperty<Suggestion> sourceProperty()
    {
        return sourceProperty;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return sourceProperty.get().toString();
    }
    /**
     * identifier property value
     */
    private final StringProperty identifierProperty = new SimpleStringProperty();
    /**
     * score property value
     */
    private final ObjectProperty<BigDecimal> scoreProperty = new SimpleObjectProperty<>();
    /**
     * side property value
     */
    private final ObjectProperty<Side> sideProperty = new SimpleObjectProperty<>();
    /**
     * quantity property value
     */
    private final ObjectProperty<BigDecimal> quantityProperty = new SimpleObjectProperty<>();
    /**
     * price property value
     */
    private final ObjectProperty<BigDecimal> priceProperty = new SimpleObjectProperty<>();
    /**
     * instrument property value
     */
    private final ObjectProperty<Instrument> instrumentProperty = new SimpleObjectProperty<>();
    /**
     * order type property value
     */
    private final ObjectProperty<OrderType> orderTypeProperty = new SimpleObjectProperty<>();
    /**
     * timestamp property value
     */
    private final ObjectProperty<Date> timestampProperty = new SimpleObjectProperty<>();
    /**
     * original source suggestion property value
     */
    private final ObjectProperty<Suggestion> sourceProperty = new SimpleObjectProperty<>();
}
