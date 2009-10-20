package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.HasEquity;
import org.marketcetera.event.OptionEvent;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.beans.InstrumentBean;
import org.marketcetera.event.beans.MarketDataBean;
import org.marketcetera.event.beans.OptionBean;
import org.marketcetera.event.util.QuoteAction;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
public abstract class QuoteEventBuilder<E extends QuoteEvent>
        extends EventBuilderImpl
        implements EventBuilder<E>
{
    /**
     * 
     *
     *
     * @param inEvent
     * @return
     * @throws EventValidationException
     */
    public static QuoteEvent add(QuoteEvent inEvent)
    {
        if(inEvent instanceof HasEquity) {
            HasEquity equityEvent = (HasEquity)inEvent;
            if(inEvent instanceof AskEvent) {
                AskEvent askEvent = (AskEvent)inEvent;
                return new EquityAskEventImpl(askEvent.getMessageId(),
                                              askEvent.getTimestamp(),
                                              equityEvent.getEquity(),
                                              askEvent.getExchange(),
                                              askEvent.getPrice(),
                                              askEvent.getSize(),
                                              askEvent.getEventTime(),
                                              QuoteAction.ADD);
            } else {
                BidEvent bidEvent = (BidEvent)inEvent;
                return new EquityBidEventImpl(bidEvent.getMessageId(),
                                              bidEvent.getTimestamp(),
                                              equityEvent.getEquity(),
                                              bidEvent.getExchange(),
                                              bidEvent.getPrice(),
                                              bidEvent.getSize(),
                                              bidEvent.getEventTime(),
                                              QuoteAction.ADD);
            }
        } else if(inEvent instanceof OptionEvent) {
            OptionEvent optionEvent = (OptionEvent)inEvent;
            if(inEvent instanceof AskEvent) {
                AskEvent askEvent = (AskEvent)inEvent;
                return new OptionAskEventImpl(askEvent.getMessageId(),
                                              askEvent.getTimestamp(),
                                              optionEvent.getOption(),
                                              askEvent.getExchange(),
                                              askEvent.getPrice(),
                                              askEvent.getSize(),
                                              askEvent.getEventTime(),
                                              optionEvent.getUnderlyingEquity(),
                                              optionEvent.getStrike(),
                                              optionEvent.getOptionType(),
                                              optionEvent.getExpiry(),
                                              optionEvent.hasDeliverable(),
                                              optionEvent.getMultiplier(),
                                              optionEvent.getExpirationType(),
                                              QuoteAction.ADD);
            } else {
                BidEvent bidEvent = (BidEvent)inEvent;
                return new OptionBidEventImpl(bidEvent.getMessageId(),
                                              bidEvent.getTimestamp(),
                                              optionEvent.getOption(),
                                              bidEvent.getExchange(),
                                              bidEvent.getPrice(),
                                              bidEvent.getSize(),
                                              bidEvent.getEventTime(),
                                              optionEvent.getUnderlyingEquity(),
                                              optionEvent.getStrike(),
                                              optionEvent.getOptionType(),
                                              optionEvent.getExpiry(),
                                              optionEvent.hasDeliverable(),
                                              optionEvent.getMultiplier(),
                                              optionEvent.getExpirationType(),
                                              QuoteAction.ADD);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * 
     *
     *
     * @param inEvent
     * @param inNewTimestamp
     * @param inNewSize
     * @return
     * @throws EventValidationException
     */
    @SuppressWarnings("unchecked")
    public static <E extends QuoteEvent> E change(E inEvent,
                                                  Date inNewTimestamp,
                                                  BigDecimal inNewSize)
    {
        if(inEvent instanceof HasEquity) {
            HasEquity equityEvent = (HasEquity)inEvent;
            if(inEvent instanceof AskEvent) {
                AskEvent askEvent = (AskEvent)inEvent;
                return (E)new EquityAskEventImpl(askEvent.getMessageId(),
                                                 inNewTimestamp,
                                                 equityEvent.getEquity(),
                                                 askEvent.getExchange(),
                                                 askEvent.getPrice(),
                                                 inNewSize,
                                                 askEvent.getEventTime(),
                                                 QuoteAction.CHANGE);
            } else {
                BidEvent bidEvent = (BidEvent)inEvent;
                return (E)new EquityBidEventImpl(bidEvent.getMessageId(),
                                                 inNewTimestamp,
                                                 equityEvent.getEquity(),
                                                 bidEvent.getExchange(),
                                                 bidEvent.getPrice(),
                                                 inNewSize,
                                                 bidEvent.getEventTime(),
                                                 QuoteAction.CHANGE);
            }
        } else if(inEvent instanceof OptionEvent) {
            OptionEvent optionEvent = (OptionEvent)inEvent;
            if(inEvent instanceof AskEvent) {
                AskEvent askEvent = (AskEvent)inEvent;
                return (E)new OptionAskEventImpl(askEvent.getMessageId(),
                                                 inNewTimestamp,
                                                 optionEvent.getOption(),
                                                 askEvent.getExchange(),
                                                 askEvent.getPrice(),
                                                 inNewSize,
                                                 askEvent.getEventTime(),
                                                 optionEvent.getUnderlyingEquity(),
                                                 optionEvent.getStrike(),
                                                 optionEvent.getOptionType(),
                                                 optionEvent.getExpiry(),
                                                 optionEvent.hasDeliverable(),
                                                 optionEvent.getMultiplier(),
                                                 optionEvent.getExpirationType(),
                                                 QuoteAction.CHANGE);
            } else {
                BidEvent bidEvent = (BidEvent)inEvent;
                return (E)new OptionBidEventImpl(bidEvent.getMessageId(),
                                                 inNewTimestamp,
                                                 optionEvent.getOption(),
                                                 bidEvent.getExchange(),
                                                 bidEvent.getPrice(),
                                                 inNewSize,
                                                 bidEvent.getEventTime(),
                                                 optionEvent.getUnderlyingEquity(),
                                                 optionEvent.getStrike(),
                                                 optionEvent.getOptionType(),
                                                 optionEvent.getExpiry(),
                                                 optionEvent.hasDeliverable(),
                                                 optionEvent.getMultiplier(),
                                                 optionEvent.getExpirationType(),
                                                 QuoteAction.CHANGE);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * 
     *
     *
     * @param inEvent
     * @return
     * @throws EventValidationException
     */
    @SuppressWarnings("unchecked")
    public static <E extends QuoteEvent> E delete(E inEvent)
    {
        if(inEvent instanceof HasEquity) {
            HasEquity equityEvent = (HasEquity)inEvent;
            if(inEvent instanceof AskEvent) {
                AskEvent askEvent = (AskEvent)inEvent;
                return (E)new EquityAskEventImpl(askEvent.getMessageId(),
                                                 askEvent.getTimestamp(),
                                                 equityEvent.getEquity(),
                                                 askEvent.getExchange(),
                                                 askEvent.getPrice(),
                                                 askEvent.getSize(),
                                                 askEvent.getEventTime(),
                                                 QuoteAction.DELETE);
            } else {
                BidEvent bidEvent = (BidEvent)inEvent;
                return (E)new EquityBidEventImpl(bidEvent.getMessageId(),
                                                 bidEvent.getTimestamp(),
                                                 equityEvent.getEquity(),
                                                 bidEvent.getExchange(),
                                                 bidEvent.getPrice(),
                                                 bidEvent.getSize(),
                                                 bidEvent.getEventTime(),
                                                 QuoteAction.DELETE);
            }
        } else if(inEvent instanceof OptionEvent) {
            OptionEvent optionEvent = (OptionEvent)inEvent;
            if(inEvent instanceof AskEvent) {
                AskEvent askEvent = (AskEvent)inEvent;
                return (E)new OptionAskEventImpl(askEvent.getMessageId(),
                                                 askEvent.getTimestamp(),
                                                 optionEvent.getOption(),
                                                 askEvent.getExchange(),
                                                 askEvent.getPrice(),
                                                 askEvent.getSize(),
                                                 askEvent.getEventTime(),
                                                 optionEvent.getUnderlyingEquity(),
                                                 optionEvent.getStrike(),
                                                 optionEvent.getOptionType(),
                                                 optionEvent.getExpiry(),
                                                 optionEvent.hasDeliverable(),
                                                 optionEvent.getMultiplier(),
                                                 optionEvent.getExpirationType(),
                                                 QuoteAction.DELETE);
            } else {
                BidEvent bidEvent = (BidEvent)inEvent;
                return (E)new OptionBidEventImpl(bidEvent.getMessageId(),
                                                 bidEvent.getTimestamp(),
                                                 optionEvent.getOption(),
                                                 bidEvent.getExchange(),
                                                 bidEvent.getPrice(),
                                                 bidEvent.getSize(),
                                                 bidEvent.getEventTime(),
                                                 optionEvent.getUnderlyingEquity(),
                                                 optionEvent.getStrike(),
                                                 optionEvent.getOptionType(),
                                                 optionEvent.getExpiry(),
                                                 optionEvent.hasDeliverable(),
                                                 optionEvent.getMultiplier(),
                                                 optionEvent.getExpirationType(),
                                                 QuoteAction.DELETE);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * 
     *
     *
     * @param inInstrument
     * @return
     */
    public static QuoteEventBuilder<AskEvent> newAskEvent(Instrument inInstrument)
    {
        if(inInstrument instanceof Equity) {
            return newEquityAskEvent().withInstrument(inInstrument);
        } else if(inInstrument instanceof Option) {
            return newOptionAskEvent().withInstrument(inInstrument);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * 
     *
     *
     * @param inInstrument
     * @return
     */
    public static QuoteEventBuilder<BidEvent> newBidEvent(Instrument inInstrument)
    {
        if(inInstrument instanceof Equity) {
            return newEquityBidEvent().withInstrument(inInstrument);
        } else if(inInstrument instanceof Option) {
            return newOptionBidEvent().withInstrument(inInstrument);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * 
     *
     *
     * @return
     */
    public static EquityAskEventBuilder newEquityAskEvent()
    {
        return new EquityAskEventBuilder();
    }
    /**
     * 
     *
     *
     * @return
     */
    public static EquityBidEventBuilder newEquityBidEvent()
    {
        return new EquityBidEventBuilder();
    }
    /**
     * 
     *
     *
     * @return
     */
    public static OptionAskEventBuilder newOptionAskEvent()
    {
        return new OptionAskEventBuilder();
    }
    /**
     * 
     *
     *
     * @return
     */
    public static OptionBidEventBuilder newOptionBidEvent()
    {
        return new OptionBidEventBuilder();
    }
    /**
     *
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static final class EquityAskEventBuilder
        extends QuoteEventBuilder<AskEvent>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.event.EventBuilder#create()
         */
        @Override
        public AskEvent create()
        {
            return new EquityAskEventImpl(getMessageId(),
                                          getTimestamp(),
                                          (Equity)getInstrument(),
                                          getExchange(),
                                          getPrice(),
                                          getSize(),
                                          getQuoteDate(),
                                          getQuoteAction());
        }
    }
    /**
     *
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static final class EquityBidEventBuilder
            extends QuoteEventBuilder<BidEvent>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.event.EventBuilder#create()
         */
        @Override
        public BidEvent create()
        {
            return new EquityBidEventImpl(getMessageId(),
                                          getTimestamp(),
                                          (Equity)getInstrument(),
                                          getExchange(),
                                          getPrice(),
                                          getSize(),
                                          getQuoteDate(),
                                          getQuoteAction());
        }
    }
    /**
     *
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static final class OptionAskEventBuilder
            extends QuoteEventBuilder<AskEvent>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.event.EventBuilder#create()
         */
        @Override
        public AskEvent create()
        {
            return new OptionAskEventImpl(getMessageId(),
                                          getTimestamp(),
                                          (Option)getInstrument(),
                                          getExchange(),
                                          getPrice(),
                                          getSize(),
                                          getQuoteDate(),
                                          getUnderlyingEquity(),
                                          getStrike(),
                                          getOptionType(),
                                          getExpiry(),
                                          getHasDeliverable(),
                                          getMultiplier(),
                                          getExpirationType(),
                                          getQuoteAction());
        }
    }
    /**
     *
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static final class OptionBidEventBuilder
            extends QuoteEventBuilder<BidEvent>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.event.EventBuilder#create()
         */
        @Override
        public BidEvent create()
        {
            return new OptionBidEventImpl(getMessageId(),
                                          getTimestamp(),
                                          (Option)getInstrument(),
                                          getExchange(),
                                          getPrice(),
                                          getSize(),
                                          getQuoteDate(),
                                          getUnderlyingEquity(),
                                          getStrike(),
                                          getOptionType(),
                                          getExpiry(),
                                          getHasDeliverable(),
                                          getMultiplier(),
                                          getExpirationType(),
                                          getQuoteAction());
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.AbstractEventBuilder#withMessageId(long)
     */
    @Override
    public QuoteEventBuilder<E> withMessageId(long inMessageId)
    {
        super.withMessageId(inMessageId);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.AbstractEventBuilder#withTimestamp(java.util.Date)
     */
    @Override
    public QuoteEventBuilder<E> withTimestamp(Date inTimestamp)
    {
        super.withTimestamp(inTimestamp);
        return this;
    }
    /**
     * Sets the instrument value.
     *
     * @param a <code>I</code> value
     */
    public final QuoteEventBuilder<E> withInstrument(Instrument inInstrument)
    {
        instrument.setInstrument(inInstrument);
        return this;
    }
    /**
     * Sets the price value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final QuoteEventBuilder<E> withPrice(BigDecimal inPrice)
    {
        exchangeCommon.setPrice(inPrice);
        return this;
    }
    /**
     * Sets the size value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final QuoteEventBuilder<E> withSize(BigDecimal inSize)
    {
        exchangeCommon.setSize(inSize);
        return this;
    }
    /**
     * Sets the exchange value.
     *
     * @param a <code>String</code> value
     */
    public final QuoteEventBuilder<E> withExchange(String inExchange)
    {
        exchangeCommon.setExchange(inExchange);
        return this;
    }
    /**
     * Sets the quoteDate value.
     *
     * @param a <code>String</code> value
     */
    public final QuoteEventBuilder<E> withQuoteDate(String inQuoteDate)
    {
        exchangeCommon.setExchangeTimestamp(inQuoteDate);
        return this;
    }
    /**
     * 
     *
     *
     * @param inAction
     * @return
     */
    public final QuoteEventBuilder<E> withAction(QuoteAction inAction)
    {
        action = inAction;
        return this;
    }
    /**
     * Sets the underlyingEquity value.
     *
     * @param a <code>Equity</code> value
     */
    public final QuoteEventBuilder<E> withUnderlyingEquity(Equity inUnderlyingEquity)
    {
        option.setUnderlyingEquity(inUnderlyingEquity);
        return this;
    }
    /**
     * Sets the expiry value.
     *
     * @param a <code>String</code> value
     */
    public final QuoteEventBuilder<E> withExpiry(String inExpiry)
    {
        option.setExpiry(inExpiry);
        return this;
    }
    /**
     * Sets the optionType value.
     *
     * @param a <code>OptionType</code> value
     */
    public final QuoteEventBuilder<E> ofOptionType(OptionType inOptionType)
    {
        option.setOptionType(inOptionType);
        return this;
    }
    /**
     * Sets the expirationType value.
     *
     * @param a <code>ExpirationType</code> value
     */
    public final QuoteEventBuilder<E> ofExpirationType(ExpirationType inExpirationType)
    {
        option.setExpirationType(inExpirationType);
        return this;
    }
    /**
     * Sets the multiplier value.
     *
     * @param a <code>int</code> value
     */
    public final QuoteEventBuilder<E> withMultiplier(int inMultiplier)
    {
        option.setMultiplier(inMultiplier);
        return this;
    }
    /**
     * Sets the hasDeliverable value.
     *
     * @param a <code>boolean</code> value
     */
    public final QuoteEventBuilder<E> hasDeliverable(boolean inHasDeliverable)
    {
        option.setHasDeliverable(inHasDeliverable);
        return this;
    }
    /**
     * Get the instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    protected final Instrument getInstrument()
    {
        return instrument.getInstrument();
    }
    /**
     * Get the price value.
     *
     * @return a <code>BigDecimal</code> value
     */
    protected final BigDecimal getPrice()
    {
        return exchangeCommon.getPrice();
    }
    /**
     * Get the size value.
     *
     * @return a <code>BigDecimal</code> value
     */
    protected final BigDecimal getSize()
    {
        return exchangeCommon.getSize();
    }
    /**
     * Get the exchange value.
     *
     * @return a <code>String</code> value
     */
    protected final String getExchange()
    {
        return exchangeCommon.getExchange();
    }
    /**
     * Get the quoteDate value.
     *
     * @return a <code>String</code> value
     */
    protected final String getQuoteDate()
    {
        return exchangeCommon.getExchangeTimestamp();
    }
    /**
     * 
     *
     *
     * @return
     */
    protected final QuoteAction getQuoteAction()
    {
        return action;
    }

    /**
     * Get the underlyingEquity value.
     *
     * @return a <code>Equity</code> value
     */
    protected final Equity getUnderlyingEquity()
    {
        return option.getUnderlyingEquity();
    }
    /**
     * Get the expiry value.
     *
     * @return a <code>String</code> value
     */
    protected final String getExpiry()
    {
        return option.getExpiry();
    }
    /**
     * Get the strike value.
     *
     * @return a <code>BigDecimal</code> value
     */
    protected final BigDecimal getStrike()
    {
        return option.getStrike();
    }
    /**
     * Get the optionType value.
     *
     * @return a <code>OptionType</code> value
     */
    protected final OptionType getOptionType()
    {
        return option.getOptionType();
    }
    /**
     * Get the expirationType value.
     *
     * @return a <code>ExpirationType</code> value
     */
    protected final ExpirationType getExpirationType()
    {
        return option.getExpirationType();
    }
    /**
     * Get the multiplier value.
     *
     * @return a <code>int</code> value
     */
    protected final int getMultiplier()
    {
        return option.getMultiplier();
    }
    /**
     * Get the hasDeliverable value.
     *
     * @return a <code>boolean</code> value
     */
    protected final boolean getHasDeliverable()
    {
        return option.getHasDeliverable();
    }
    /**
     * Get the action value.
     *
     * @return a <code>QuoteAction</code> value
     */
    protected final QuoteAction getAction()
    {
        return action;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.AbstractEventBuilder#setDefaults()
     */
    @Override
    protected void setDefaults()
    {
        super.setDefaults();
        if(action == null) {
            action = QuoteAction.ADD;
        }
    }
    /**
     * 
     */
    private QuoteAction action;
    /**
     * 
     */
    private final MarketDataBean exchangeCommon = new MarketDataBean();
    /**
     * 
     */
    private final OptionBean option = new OptionBean();
    /**
     * 
     */
    private final InstrumentBean instrument = new InstrumentBean();
}
