package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.EquityEvent;
import org.marketcetera.event.OptionEvent;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.beans.OptionBean;
import org.marketcetera.event.beans.QuoteBean;
import org.marketcetera.event.util.QuoteAction;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Constructs {@link QuoteEvent} objects.
 * 
 * <p>Construct a <code>QuoteEvent</code> by getting a <code>QuoteEventBuilder</code>,
 * setting the appropriate attributes on the builder, and calling {@link #create()}.  Note that
 * the builder does no validation.  The object does its own validation with {@link #create()} is
 * called.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
@ClassVersion("$Id$")
public abstract class QuoteEventBuilder<E extends QuoteEvent>
        extends AbstractEventBuilderImpl
        implements EventBuilder<E>
{
    /**
     * Creates a <code>QuoteEvent</code> of the same type as the given event
     * with the same attributes except for the {@link QuoteAction} which
     * will always be {@link QuoteAction#ADD}.
     *
     * @param inEvent a <code>QuoteEvent</code> value
     * @return a <code>QuoteEvent</code> value
     * @throws UnsupportedOperationException if the given <code>QuoteEvent</code> is for
     *  an unsupported asset class
     */
    public static QuoteEvent add(QuoteEvent inEvent)
    {
        if(inEvent instanceof EquityEvent) {
            EquityEvent equityEvent = (EquityEvent)inEvent;
            if(inEvent instanceof AskEvent) {
                AskEvent askEvent = (AskEvent)inEvent;
                return new EquityAskEventImpl(askEvent.getMessageId(),
                                              askEvent.getTimestamp(),
                                              equityEvent.getEquity(),
                                              askEvent.getExchange(),
                                              askEvent.getPrice(),
                                              askEvent.getSize(),
                                              askEvent.getExchangeTimestamp(),
                                              QuoteAction.ADD);
            } else {
                BidEvent bidEvent = (BidEvent)inEvent;
                return new EquityBidEventImpl(bidEvent.getMessageId(),
                                              bidEvent.getTimestamp(),
                                              equityEvent.getEquity(),
                                              bidEvent.getExchange(),
                                              bidEvent.getPrice(),
                                              bidEvent.getSize(),
                                              bidEvent.getExchangeTimestamp(),
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
                                              askEvent.getExchangeTimestamp(),
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
                                              bidEvent.getExchangeTimestamp(),
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
     * Creates a <code>QuoteEvent</code> of the same type as the given event
     * with the same attributes except for the {@link QuoteAction} which
     * will always be {@link QuoteAction#CHANGE}.
     *
     * @param inEvent a <code>QuoteEvent</code> value
     * @return a <code>QuoteEvent</code> value
     * @throws UnsupportedOperationException if the given <code>QuoteEvent</code> is for
     *  an unsupported asset class
     */
    @SuppressWarnings("unchecked")
    public static <E extends QuoteEvent> E change(E inEvent,
                                                  Date inNewTimestamp,
                                                  BigDecimal inNewSize)
    {
        if(inEvent instanceof EquityEvent) {
            EquityEvent equityEvent = (EquityEvent)inEvent;
            if(inEvent instanceof AskEvent) {
                AskEvent askEvent = (AskEvent)inEvent;
                return (E)new EquityAskEventImpl(askEvent.getMessageId(),
                                                 inNewTimestamp,
                                                 equityEvent.getEquity(),
                                                 askEvent.getExchange(),
                                                 askEvent.getPrice(),
                                                 inNewSize,
                                                 askEvent.getExchangeTimestamp(),
                                                 QuoteAction.CHANGE);
            } else {
                BidEvent bidEvent = (BidEvent)inEvent;
                return (E)new EquityBidEventImpl(bidEvent.getMessageId(),
                                                 inNewTimestamp,
                                                 equityEvent.getEquity(),
                                                 bidEvent.getExchange(),
                                                 bidEvent.getPrice(),
                                                 inNewSize,
                                                 bidEvent.getExchangeTimestamp(),
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
                                                 askEvent.getExchangeTimestamp(),
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
                                                 bidEvent.getExchangeTimestamp(),
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
     * Creates a <code>QuoteEvent</code> of the same type as the given event
     * with the same attributes except for the {@link QuoteAction} which
     * will always be {@link QuoteAction#DELETE}.
     *
     * @param inEvent a <code>QuoteEvent</code> value
     * @return a <code>QuoteEvent</code> value
     * @throws UnsupportedOperationException if the given <code>QuoteEvent</code> is for
     *  an unsupported asset class
     */
    @SuppressWarnings("unchecked")
    public static <E extends QuoteEvent> E delete(E inEvent)
    {
        if(inEvent instanceof EquityEvent) {
            EquityEvent equityEvent = (EquityEvent)inEvent;
            if(inEvent instanceof AskEvent) {
                AskEvent askEvent = (AskEvent)inEvent;
                return (E)new EquityAskEventImpl(askEvent.getMessageId(),
                                                 askEvent.getTimestamp(),
                                                 equityEvent.getEquity(),
                                                 askEvent.getExchange(),
                                                 askEvent.getPrice(),
                                                 askEvent.getSize(),
                                                 askEvent.getExchangeTimestamp(),
                                                 QuoteAction.DELETE);
            } else {
                BidEvent bidEvent = (BidEvent)inEvent;
                return (E)new EquityBidEventImpl(bidEvent.getMessageId(),
                                                 bidEvent.getTimestamp(),
                                                 equityEvent.getEquity(),
                                                 bidEvent.getExchange(),
                                                 bidEvent.getPrice(),
                                                 bidEvent.getSize(),
                                                 bidEvent.getExchangeTimestamp(),
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
                                                 askEvent.getExchangeTimestamp(),
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
                                                 bidEvent.getExchangeTimestamp(),
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
     * Returns a <code>QuoteEventBuilder</code> suitable for constructing a new <code>AskEvent</code> object.
     *
     * <p>The type of <code>AskEvent</code> returned will match the type of the given <code>Instrument</code>,
     * i.e., an Equity-type <code>AskEvent</code> for an {@link Equity}, an Option-type <code>AskEvent</code> for an
     * {@link Option}, etc.
     * 
     * @param inInstrument an <code>Instrument</code> value indicating the type of {@link AskEvent} to create
     * @return a <code>QuoteEventBuilder&lt;AskEvent&gt;</code> value
     * @throws UnsupportedOperationException if the asset class of the given <code>Instrument</code> isn't supported
     */
    public static QuoteEventBuilder<AskEvent> askEvent(Instrument inInstrument)
    {
        if(inInstrument instanceof Equity) {
            return equityAskEvent().withInstrument(inInstrument);
        } else if(inInstrument instanceof Option) {
            return optionAskEvent().withInstrument(inInstrument);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * Returns a <code>QuoteEventBuilder</code> suitable for constructing a new <code>BidEvent</code> object.
     *
     * <p>The type of <code>BidEvent</code> returned will match the type of the given <code>Instrument</code>,
     * i.e., an Equity-type <code>BidEvent</code> for an {@link Equity}, an Option-type <code>BidEvent</code> for an
     * {@link Option}, etc.
     * 
     * @param inInstrument an <code>Instrument</code> value indicating the type of {@link BidEvent} to create
     * @return a <code>QuoteEventBuilder&lt;BidEvent&gt;</code> value
     * @throws UnsupportedOperationException if the asset class of the given <code>Instrument</code> isn't supported
     */
    public static QuoteEventBuilder<BidEvent> bidEvent(Instrument inInstrument)
    {
        if(inInstrument instanceof Equity) {
            return equityBidEvent().withInstrument(inInstrument);
        } else if(inInstrument instanceof Option) {
            return optionBidEvent().withInstrument(inInstrument);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * Returns a <code>QuoteEventBuilder</code> suitable for constructing a new Equity <code>AskEvent</code> object.
     *
     * @return a <code>QuoteEventBuilder&lt;AskEvent&gt;</code> value
     * @throw IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not an {@link Equity}
     */
    public static QuoteEventBuilder<AskEvent> equityAskEvent()
    {
        return new QuoteEventBuilder<AskEvent>() {
            /* (non-Javadoc)
             * @see org.marketcetera.event.EventBuilder#create()
             */
            @Override
            public AskEvent create()
            {
                if(getQuote().getInstrument() instanceof Equity) {
                    return new EquityAskEventImpl(getMessageId(),
                                                  getTimestamp(),
                                                  (Equity)getQuote().getInstrument(),
                                                  getQuote().getExchange(),
                                                  getQuote().getPrice(),
                                                  getQuote().getSize(),
                                                  getQuote().getExchangeTimestamp(),
                                                  getQuote().getAction());
                }
                throw new IllegalArgumentException();
            }
        };
    }
    /**
     * Returns a <code>QuoteEventBuilder</code> suitable for constructing a new Equity <code>BidEvent</code> object.
     *
     * @return a <code>QuoteEventBuilder&lt;BidEvent&gt;</code> value
     * @throw IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not an {@link Equity}
     */
    public static QuoteEventBuilder<BidEvent> equityBidEvent()
    {
        return new QuoteEventBuilder<BidEvent>() {
            /* (non-Javadoc)
             * @see org.marketcetera.event.EventBuilder#create()
             */
            @Override
            public BidEvent create()
            {
                if(getQuote().getInstrument() instanceof Equity) {
                    return new EquityBidEventImpl(getMessageId(),
                                                  getTimestamp(),
                                                  (Equity)getQuote().getInstrument(),
                                                  getQuote().getExchange(),
                                                  getQuote().getPrice(),
                                                  getQuote().getSize(),
                                                  getQuote().getExchangeTimestamp(),
                                                  getQuote().getAction());
                }
                throw new IllegalArgumentException();
            }
        };
    }
    /**
     * Returns a <code>QuoteEventBuilder</code> suitable for constructing a new Option <code>AskEvent</code> object.
     *
     * @return a <code>QuoteEventBuilder&lt;AskEvent&gt;</code> value
     * @throw IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not an {@link Option}
     */
    public static QuoteEventBuilder<AskEvent> optionAskEvent()
    {
        return new QuoteEventBuilder<AskEvent>() {
            /* (non-Javadoc)
             * @see org.marketcetera.event.EventBuilder#create()
             */
            @Override
            public AskEvent create()
            {
                if(getQuote().getInstrument() instanceof Option) {
                    return new OptionAskEventImpl(getMessageId(),
                                                  getTimestamp(),
                                                  (Option)getQuote().getInstrument(),
                                                  getQuote().getExchange(),
                                                  getQuote().getPrice(),
                                                  getQuote().getSize(),
                                                  getQuote().getExchangeTimestamp(),
                                                  getOption().getUnderlyingEquity(),
                                                  getOption().getStrike(),
                                                  getOption().getOptionType(),
                                                  getOption().getExpiry(),
                                                  getOption().hasDeliverable(),
                                                  getOption().getMultiplier(),
                                                  getOption().getExpirationType(),
                                                  getQuote().getAction());
                }
                throw new IllegalArgumentException();
            }
        };
    }
    /**
     * Returns a <code>QuoteEventBuilder</code> suitable for constructing a new Option <code>BidEvent</code> object.
     *
     * @return a <code>QuoteEventBuilder&lt;BidEvent&gt;</code> value
     * @throw IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not an {@link Option}
     */
    public static QuoteEventBuilder<BidEvent> optionBidEvent()
    {
        return new QuoteEventBuilder<BidEvent>() {
            /* (non-Javadoc)
             * @see org.marketcetera.event.EventBuilder#create()
             */
            @Override
            public BidEvent create()
            {
                if(getQuote().getInstrument() instanceof Option) {
                    return new OptionBidEventImpl(getMessageId(),
                                                  getTimestamp(),
                                                  (Option)getQuote().getInstrument(),
                                                  getQuote().getExchange(),
                                                  getQuote().getPrice(),
                                                  getQuote().getSize(),
                                                  getQuote().getExchangeTimestamp(),
                                                  getOption().getUnderlyingEquity(),
                                                  getOption().getStrike(),
                                                  getOption().getOptionType(),
                                                  getOption().getExpiry(),
                                                  getOption().hasDeliverable(),
                                                  getOption().getMultiplier(),
                                                  getOption().getExpirationType(),
                                                  getQuote().getAction());
                }
                throw new IllegalArgumentException();
            }
        };
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
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public final QuoteEventBuilder<E> withInstrument(Instrument inInstrument)
    {
        quote.setInstrument(inInstrument);
        return this;
    }
    /**
     * Sets the price value.
     *
     * @param a <code>BigDecimal</code> value
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public final QuoteEventBuilder<E> withPrice(BigDecimal inPrice)
    {
        quote.setPrice(inPrice);
        return this;
    }
    /**
     * Sets the size value.
     *
     * @param a <code>BigDecimal</code> value
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public final QuoteEventBuilder<E> withSize(BigDecimal inSize)
    {
        quote.setSize(inSize);
        return this;
    }
    /**
     * Sets the exchange value.
     *
     * @param a <code>String</code> value
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public final QuoteEventBuilder<E> withExchange(String inExchange)
    {
        quote.setExchange(inExchange);
        return this;
    }
    /**
     * Sets the quoteDate value.
     *
     * @param a <code>String</code> value
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public final QuoteEventBuilder<E> withQuoteDate(String inQuoteDate)
    {
        quote.setExchangeTimestamp(inQuoteDate);
        return this;
    }
    /**
     * Sets the quote action value. 
     *
     * @param inAction a <code>QuoteAction</code> value
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public final QuoteEventBuilder<E> withAction(QuoteAction inAction)
    {
        quote.setAction(inAction);
        return this;
    }
    /**
     * Sets the underlyingEquity value.
     *
     * @param a <code>Equity</code> value
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
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
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
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
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public final QuoteEventBuilder<E> withOptionType(OptionType inOptionType)
    {
        option.setOptionType(inOptionType);
        return this;
    }
    /**
     * Sets the expirationType value.
     *
     * @param a <code>ExpirationType</code> value
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public final QuoteEventBuilder<E> withExpirationType(ExpirationType inExpirationType)
    {
        option.setExpirationType(inExpirationType);
        return this;
    }
    /**
     * Sets the multiplier value.
     *
     * @param a <code>int</code> value
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
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
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public final QuoteEventBuilder<E> hasDeliverable(boolean inHasDeliverable)
    {
        option.setHasDeliverable(inHasDeliverable);
        return this;
    }
    /**
     * Sets the strike value.
     *
     * @param a <code>BigDecimal</code> value
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public final QuoteEventBuilder<E> withStrike(BigDecimal inStrike)
    {
        option.setStrike(inStrike);
        return this;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("QuoteEventBuilder [option=").append(option).append(", quote=").append(quote) //$NON-NLS-1$ //$NON-NLS-2$
                .append(", getMessageId()=").append(getMessageId()).append(", getTimestamp()=").append(getTimestamp()) //$NON-NLS-1$ //$NON-NLS-2$
                .append("]"); //$NON-NLS-1$
        return builder.toString();
    }
    /**
     * Get the quote value.
     *
     * @return a <code>QuoteBean</code> value
     */
    protected final QuoteBean getQuote()
    {
        return quote;
    }
    /**
     * Get the option value.
     *
     * @return a <code>OptionBean</code> value
     */
    protected final OptionBean getOption()
    {
        return option;
    }
    /**
     * the quote attributes
     */
    private final QuoteBean quote = new QuoteBean();
    /**
     * the option attributes
     */
    private final OptionBean option = new OptionBean();
}
