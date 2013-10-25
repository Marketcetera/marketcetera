package org.marketcetera.event.impl;

import static org.marketcetera.event.Messages.VALIDATION_CURRENCY_REQUIRED;
import static org.marketcetera.event.Messages.VALIDATION_EQUITY_REQUIRED;
import static org.marketcetera.event.Messages.VALIDATION_FUTURE_REQUIRED;
import static org.marketcetera.event.Messages.VALIDATION_OPTION_REQUIRED;
import static org.marketcetera.event.Messages.VALIDATION_BOND_REQUIRED;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.event.*;
import org.marketcetera.event.beans.*;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.*;
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
 * @since 2.0.0
 */
@NotThreadSafe
@ClassVersion("$Id$")
public abstract class QuoteEventBuilder<E extends QuoteEvent>
        implements EventBuilder<E>, OptionEventBuilder<QuoteEventBuilder<E>>, FutureEventBuilder<QuoteEventBuilder<E>>,CurrencyEventBuilder<QuoteEventBuilder<E>>, ConvertibleBondEventBuilder<QuoteEventBuilder<E>>
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
        QuoteBean quote = QuoteBean.getQuoteBeanFromEvent(inEvent,
                                                          QuoteAction.ADD);
        if(inEvent instanceof EquityEvent) {
            if(inEvent instanceof AskEvent) {
                return new EquityAskEventImpl(quote);
            } else {
                return new EquityBidEventImpl(quote);
            }
        }
        if(inEvent instanceof OptionEvent) {
            OptionBean option = OptionBean.getOptionBeanFromEvent((OptionEvent)inEvent);
            if(inEvent instanceof AskEvent) {
                return new OptionAskEventImpl(quote,
                                              option);
            } else {
                return new OptionBidEventImpl(quote,
                                              option);
            }
        }
        if(inEvent instanceof FutureEvent) {
            FutureBean future = FutureBean.getFutureBeanFromEvent((FutureEvent)inEvent);
            if(inEvent instanceof AskEvent) {
                return new FutureAskEventImpl(quote,
                                              future);
            } else {
                return new FutureBidEventImpl(quote,
                                              future);
            }
        }
        if(inEvent instanceof CurrencyEvent) {
            CurrencyBean currency = CurrencyBean.getCurrencyBeanFromEvent((CurrencyEvent)inEvent);
            if(inEvent instanceof AskEvent) {
                return new CurrencyAskEventImpl(quote,
                                              currency);
            } else {
                return new CurrencyBidEventImpl(quote,
                                                currency);
            }
        }
        if(inEvent instanceof ConvertibleBondEvent) {
            ConvertibleBondBean bond = ConvertibleBondBean.getConvertibleBondBeanFromEvent((ConvertibleBondEvent)inEvent);
            if(inEvent instanceof AskEvent) {
                return new ConvertibleBondAskEventImpl(quote,
                                                       bond);
            } else {
                return new ConvertibleBondBidEventImpl(quote,
                                                       bond);
            }
        }
        // from an asset class that is neither equity nor option
        throw new UnsupportedOperationException();
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
        QuoteBean quote = QuoteBean.getQuoteBeanFromEvent(inEvent,
                                                          inNewTimestamp,
                                                          inNewSize,
                                                          QuoteAction.CHANGE);
        if(inEvent instanceof EquityEvent) {
            if(inEvent instanceof AskEvent) {
                return (E)new EquityAskEventImpl(quote);
            } else {
                return (E)new EquityBidEventImpl(quote);
            }
        }
        if(inEvent instanceof OptionEvent) {
            OptionBean option = OptionBean.getOptionBeanFromEvent((OptionEvent)inEvent);
            if(inEvent instanceof AskEvent) {
                return (E)new OptionAskEventImpl(quote,
                                                 option);
            } else {
                return (E)new OptionBidEventImpl(quote,
                                                 option);
            }
        }
        if(inEvent instanceof FutureEvent) {
            FutureBean future = FutureBean.getFutureBeanFromEvent((FutureEvent)inEvent);
            if(inEvent instanceof AskEvent) {
                return (E)new FutureAskEventImpl(quote,
                                                 future);
            } else {
                return (E)new FutureBidEventImpl(quote,
                                                 future);
            }
        }
        if(inEvent instanceof CurrencyEvent) {
            CurrencyBean currency = CurrencyBean.getCurrencyBeanFromEvent((CurrencyEvent)inEvent);
            if(inEvent instanceof AskEvent) {
                return (E)new CurrencyAskEventImpl(quote,
                                                 currency);
            } else {
                return (E)new CurrencyBidEventImpl(quote,
                                                 currency);
            }
        }
        if(inEvent instanceof ConvertibleBondEvent) {
            ConvertibleBondBean bond = ConvertibleBondBean.getConvertibleBondBeanFromEvent((ConvertibleBondEvent)inEvent);
            if(inEvent instanceof AskEvent) {
                return (E)new ConvertibleBondAskEventImpl(quote,
                                                          bond);
            } else {
                return (E)new ConvertibleBondBidEventImpl(quote,
                                                          bond);
            }
        }
        // from an asset class that is neither equity nor option
        throw new UnsupportedOperationException();
    }
    /**
     * Creates a <code>QuoteEvent</code> of the same type as the given event
     * with the same attributes except for the {@link QuoteAction} which
     * will always be {@link QuoteAction#CHANGE}.
     *
     * @param inEvent a <code>QuoteEvent</code> value
     * @param inNewPrice a <code>BigDecimal</code> value
     * @param inNewSize a <code>BigDecimal</code> value
     * @param inNewQuoteDate a <code>String</code> value
     * @return a <code>QuoteEvent</code> value
     * @throws UnsupportedOperationException if the given <code>QuoteEvent</code> is for
     *  an unsupported asset class
     */
    @SuppressWarnings("unchecked")
    public static <E extends QuoteEvent> E change(E inEvent,
                                                  BigDecimal inNewPrice,
                                                  BigDecimal inNewSize,
                                                  String inNewQuoteDate)
    {
        QuoteBean quote = QuoteBean.getQuoteBeanFromEvent(inEvent,
                                                          QuoteAction.CHANGE);
        quote.setPrice(inNewPrice);
        quote.setSize(inNewSize);
        quote.setExchangeTimestamp(inNewQuoteDate);
        if(inEvent instanceof EquityEvent) {
            if(inEvent instanceof AskEvent) {
                return (E)new EquityAskEventImpl(quote);
            } else {
                return (E)new EquityBidEventImpl(quote);
            }
        }
        if(inEvent instanceof OptionEvent) {
            OptionBean option = OptionBean.getOptionBeanFromEvent((OptionEvent)inEvent);
            if(inEvent instanceof AskEvent) {
                return (E)new OptionAskEventImpl(quote,
                                                 option);
            } else {
                return (E)new OptionBidEventImpl(quote,
                                                 option);
            }
        }
        if(inEvent instanceof FutureEvent) {
            FutureBean future = FutureBean.getFutureBeanFromEvent((FutureEvent)inEvent);
            if(inEvent instanceof AskEvent) {
                return (E)new FutureAskEventImpl(quote,
                                                 future);
            } else {
                return (E)new FutureBidEventImpl(quote,
                                                 future);
            }
        }
        if(inEvent instanceof CurrencyEvent) {
            CurrencyBean currency = CurrencyBean.getCurrencyBeanFromEvent((CurrencyEvent)inEvent);
            if(inEvent instanceof AskEvent) {
                return (E)new CurrencyAskEventImpl(quote,
                                                 currency);
            } else {
                return (E)new CurrencyBidEventImpl(quote,
                                                 currency);
            }
        }
        if(inEvent instanceof ConvertibleBondEvent) {
            ConvertibleBondBean bond = ConvertibleBondBean.getConvertibleBondBeanFromEvent((ConvertibleBondEvent)inEvent);
            if(inEvent instanceof AskEvent) {
                return (E)new ConvertibleBondAskEventImpl(quote,
                                                          bond);
            } else {
                return (E)new ConvertibleBondBidEventImpl(quote,
                                                          bond);
            }
        }
        // from an asset class that is neither equity nor option
        throw new UnsupportedOperationException();
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
        QuoteBean quote = QuoteBean.getQuoteBeanFromEvent(inEvent,
                                                          QuoteAction.DELETE);
        if(inEvent instanceof EquityEvent) {
            if(inEvent instanceof AskEvent) {
                return (E)new EquityAskEventImpl(quote);
            } else {
                return (E)new EquityBidEventImpl(quote);
            }
        }
        if(inEvent instanceof OptionEvent) {
            OptionBean option = OptionBean.getOptionBeanFromEvent((OptionEvent)inEvent);
            if(inEvent instanceof AskEvent) {
                return (E)new OptionAskEventImpl(quote,
                                                 option);
            } else {
                return (E)new OptionBidEventImpl(quote,
                                                 option);
            }
        }
        if(inEvent instanceof FutureEvent) {
            FutureBean future = FutureBean.getFutureBeanFromEvent((FutureEvent)inEvent);
            if(inEvent instanceof AskEvent) {
                return (E)new FutureAskEventImpl(quote,
                                                 future);
            } else {
                return (E)new FutureBidEventImpl(quote,
                                                 future);
            }
        }
        if(inEvent instanceof CurrencyEvent) {
        	CurrencyBean currency = CurrencyBean.getCurrencyBeanFromEvent((CurrencyEvent)inEvent);
            if(inEvent instanceof AskEvent) {
                return (E)new CurrencyAskEventImpl(quote,
                                                   currency);
            } else {
                return (E)new CurrencyBidEventImpl(quote,
                                                   currency);
            }
        }
        if(inEvent instanceof ConvertibleBondEvent) {
            ConvertibleBondBean bond = ConvertibleBondBean.getConvertibleBondBeanFromEvent((ConvertibleBondEvent)inEvent);
            if(inEvent instanceof AskEvent) {
                return (E)new ConvertibleBondAskEventImpl(quote,
                                                          bond);
            } else {
                return (E)new ConvertibleBondBidEventImpl(quote,
                                                          bond);
            }
        }
        // from an asset class that is neither equity nor option
        throw new UnsupportedOperationException();
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
        } else if(inInstrument instanceof Future) {
                return futureAskEvent().withInstrument(inInstrument);
        } else if(inInstrument instanceof Currency) {
            return currencyAskEvent().withInstrument(inInstrument);
        } else if(inInstrument instanceof ConvertibleBond) {
            return convertibleBondAskEvent().withInstrument(inInstrument);
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
        } else if(inInstrument instanceof Future) {
            return futureBidEvent().withInstrument(inInstrument);
        } else if(inInstrument instanceof Currency) {
            return currencyBidEvent().withInstrument(inInstrument);
        } else if(inInstrument instanceof ConvertibleBond) {
            return convertibleBondBidEvent().withInstrument(inInstrument);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * Returns a <code>QuoteEventBuilder</code> suitable for constructing a new Equity <code>AskEvent</code> object.
     *
     * @return a <code>QuoteEventBuilder&lt;AskEvent&gt;</code> value
     * @throws IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not an {@link Equity}
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
                    return new EquityAskEventImpl(getQuote());
                }
                throw new IllegalArgumentException(VALIDATION_EQUITY_REQUIRED.getText());
            }
        };
    }
    /**
     * Returns a <code>QuoteEventBuilder</code> suitable for constructing a new Equity <code>BidEvent</code> object.
     *
     * @return a <code>QuoteEventBuilder&lt;BidEvent&gt;</code> value
     * @throws IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not an {@link Equity}
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
                    return new EquityBidEventImpl(getQuote());
                }
                throw new IllegalArgumentException(VALIDATION_EQUITY_REQUIRED.getText());
            }
        };
    }
    /**
     * Returns a <code>QuoteEventBuilder</code> suitable for constructing a new Option <code>AskEvent</code> object.
     *
     * @return a <code>QuoteEventBuilder&lt;AskEvent&gt;</code> value
     * @throws IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not an {@link Option}
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
                    return new OptionAskEventImpl(getQuote(),
                                                  getOption());
                }
                throw new IllegalArgumentException(VALIDATION_OPTION_REQUIRED.getText());
            }
        };
    }
    /**
     * Returns a <code>QuoteEventBuilder</code> suitable for constructing a new Option <code>BidEvent</code> object.
     *
     * @return a <code>QuoteEventBuilder&lt;BidEvent&gt;</code> value
     * @throws IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not an {@link Option}
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
                    return new OptionBidEventImpl(getQuote(),
                                                  getOption());
                }
                throw new IllegalArgumentException(VALIDATION_OPTION_REQUIRED.getText());
            }
        };
    }
    /**
     * Returns a <code>QuoteEventBuilder</code> suitable for constructing a new Future <code>AskEvent</code> object.
     *
     * @return a <code>QuoteEventBuilder&lt;AskEvent&gt;</code> value
     * @throws IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not a {@link Future}
     */
    public static QuoteEventBuilder<AskEvent> futureAskEvent()
    {
        return new QuoteEventBuilder<AskEvent>() {
            /* (non-Javadoc)
             * @see org.marketcetera.event.EventBuilder#create()
             */
            @Override
            public AskEvent create()
            {
                if(getQuote().getInstrument() instanceof Future) {
                    return new FutureAskEventImpl(getQuote(),
                                                  getFuture());
                }
                throw new IllegalArgumentException(VALIDATION_FUTURE_REQUIRED.getText());
            }
        };
    }
    /**
     * Returns a <code>QuoteEventBuilder</code> suitable for constructing a new Future <code>BidEvent</code> object.
     *
     * @return a <code>QuoteEventBuilder&lt;BidEvent&gt;</code> value
     * @throws IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not an {@link Future}
     */
    public static QuoteEventBuilder<BidEvent> futureBidEvent()
    {
        return new QuoteEventBuilder<BidEvent>() {
            /* (non-Javadoc)
             * @see org.marketcetera.event.EventBuilder#create()
             */
            @Override
            public BidEvent create()
            {
                if(getQuote().getInstrument() instanceof Future) {
                    return new FutureBidEventImpl(getQuote(),
                                                  getFuture());
                }
                throw new IllegalArgumentException(VALIDATION_FUTURE_REQUIRED.getText());
            }
        };
    }
    /**
     * Returns a <code>QuoteEventBuilder</code> suitable for constructing a new Currency <code>AskEvent</code> object.
     *
     * @return a <code>QuoteEventBuilder&lt;AskEvent&gt;</code> value
     * @throws IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not a {@link Currency}
     */
    public static QuoteEventBuilder<AskEvent> currencyAskEvent()
    {
        return new QuoteEventBuilder<AskEvent>() {
            /* (non-Javadoc)
             * @see org.marketcetera.event.EventBuilder#create()
             */
            @Override
            public AskEvent create()
            {
                if(getQuote().getInstrument() instanceof Currency) {
                    return new CurrencyAskEventImpl(getQuote(),
                                                  getCurrency());
                }
                throw new IllegalArgumentException(VALIDATION_CURRENCY_REQUIRED.getText());
            }
        };
    }
    /**
     * Returns a <code>QuoteEventBuilder</code> suitable for constructing a new Currency <code>BidEvent</code> object.
     *
     * @return a <code>QuoteEventBuilder&lt;BidEvent&gt;</code> value
     * @throws IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not an {@link Currency}
     */
    public static QuoteEventBuilder<BidEvent> currencyBidEvent()
    {
        return new QuoteEventBuilder<BidEvent>() {
            /* (non-Javadoc)
             * @see org.marketcetera.event.EventBuilder#create()
             */
            @Override
            public BidEvent create()
            {
                if(getQuote().getInstrument() instanceof Currency) {
                    return new CurrencyBidEventImpl(getQuote(),
                                                  getCurrency());
                }
                throw new IllegalArgumentException(VALIDATION_CURRENCY_REQUIRED.getText());
            }
        };
    }    
    /**
     * Returns a <code>QuoteEventBuilder</code> suitable for constructing a new ConvertibleBond <code>AskEvent</code> object.
     *
     * @return a <code>QuoteEventBuilder&lt;AskEvent&gt;</code> value
     * @throws IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not a {@link ConvertibleBond}
     */
    public static QuoteEventBuilder<AskEvent> convertibleBondAskEvent()
    {
        return new QuoteEventBuilder<AskEvent>() {
            /* (non-Javadoc)
             * @see org.marketcetera.event.EventBuilder#create()
             */
            @Override
            public AskEvent create()
            {
                if(getQuote().getInstrument() instanceof ConvertibleBond) {
                    return new ConvertibleBondAskEventImpl(getQuote(),
                                                           getConvertibleBond());
                }
                throw new IllegalArgumentException(VALIDATION_BOND_REQUIRED.getText());
            }
        };
    }
    /**
     * Returns a <code>QuoteEventBuilder</code> suitable for constructing a new ConvertibleBond <code>BidEvent</code> object.
     *
     * @return a <code>QuoteEventBuilder&lt;BidEvent&gt;</code> value
     * @throws IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not an {@link ConvertibleBond}
     */
    public static QuoteEventBuilder<BidEvent> convertibleBondBidEvent()
    {
        return new QuoteEventBuilder<BidEvent>() {
            /* (non-Javadoc)
             * @see org.marketcetera.event.EventBuilder#create()
             */
            @Override
            public BidEvent create()
            {
                if(getQuote().getInstrument() instanceof ConvertibleBond) {
                    return new ConvertibleBondBidEventImpl(getQuote(),
                                                           getConvertibleBond());
                }
                throw new IllegalArgumentException(VALIDATION_BOND_REQUIRED.getText());
            }
        };
    }
    /**
     * Sets the message id to use with the new event. 
     *
     * @param inMessageId a <code>long</code> value
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public QuoteEventBuilder<E> withMessageId(long inMessageId)
    {
        quote.setMessageId(inMessageId);
        return this;
    }
    /**
     * Sets the timestamp value to use with the new event.
     *
     * @param inTimestamp a <code>Date</code> value or <code>null</code>
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public QuoteEventBuilder<E> withTimestamp(Date inTimestamp)
    {
        quote.setTimestamp(inTimestamp);
        return this;
    }
    /**
     * Sets the source value to use with the new event.
     *
     * @param inSource an <code>Object</code> value or <code>null</code>
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public QuoteEventBuilder<E> withSource(Object inSource)
    {
        quote.setSource(inSource);
        return this;
    }
    /**
     * Sets the instrument value.
     *
     * @param inInstrument an <code>Instrument</code> value or <code>null</code>
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public QuoteEventBuilder<E> withInstrument(Instrument inInstrument)
    {
        quote.setInstrument(inInstrument);
        if(inInstrument instanceof Option) {
            option.setInstrument((Option)inInstrument);
        } else if(inInstrument instanceof Future) {
            future.setInstrument((Future)inInstrument);
        } else if(inInstrument instanceof Currency) {
            currency.setInstrument((Currency)inInstrument);
        }
        if(inInstrument == null) {
            option.setInstrument(null);
            future.setInstrument(null);
            currency.setInstrument(null);
            convertibleBond.setInstrument(null);
        }
        return this;
    }
    /**
     * Sets the price value.
     *
     * @param inPrice a <code>BigDecimal</code> value or <code>null</code>
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public QuoteEventBuilder<E> withPrice(BigDecimal inPrice)
    {
        quote.setPrice(inPrice);
        return this;
    }
    /**
     * Sets the size value.
     *
     * @param inSize a <code>BigDecimal</code> value or <code>null</code>
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public QuoteEventBuilder<E> withSize(BigDecimal inSize)
    {
        quote.setSize(inSize);
        return this;
    }
    /**
     * Sets the exchange value.
     *
     * @param inExchange a <code>String</code> value or <code>null</code>
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public QuoteEventBuilder<E> withExchange(String inExchange)
    {
        quote.setExchange(inExchange);
        return this;
    }
    /**
     * Sets the quoteDate value.
     *
     * @param inQuoteDate a <code>String</code> value or <code>null</code>
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public QuoteEventBuilder<E> withQuoteDate(String inQuoteDate)
    {
        quote.setExchangeTimestamp(inQuoteDate);
        return this;
    }
    /**
     * Sets the quote action value. 
     *
     * @param inAction a <code>QuoteAction</code> value or <code>null</code>
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public QuoteEventBuilder<E> withAction(QuoteAction inAction)
    {
        quote.setAction(inAction);
        return this;
    }
    /**
     * Sets the underlyingInstrument value.
     *
     * @param inUnderlyingInstrument an <code>Instrument</code> value or <code>null</code>
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public QuoteEventBuilder<E> withUnderlyingInstrument(Instrument inUnderlyingInstrument)
    {
        option.setUnderlyingInstrument(inUnderlyingInstrument);
        return this;
    }
    /**
     * Sets the expirationType value.
     *
     * @param inExpirationType an <code>ExpirationType</code> value or <code>null</code>
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public QuoteEventBuilder<E> withExpirationType(ExpirationType inExpirationType)
    {
        option.setExpirationType(inExpirationType);
        return this;
    }
    /**
     * Sets the multiplier value.
     *
     * @param inMultiplier a <code>BigDecimal</code> value
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public QuoteEventBuilder<E> withMultiplier(BigDecimal inMultiplier)
    {
        option.setMultiplier(inMultiplier);
        return this;
    }
    /**
     * Sets the hasDeliverable value.
     *
     * @param inHasDeliverable a <code>boolean</code> value
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public QuoteEventBuilder<E> hasDeliverable(boolean inHasDeliverable)
    {
        option.setHasDeliverable(inHasDeliverable);
        return this;
    }
    /**
     * Sets the <code>DeliveryType</code> value.
     *
     * @param inDeliveryType a <code>DeliveryType</code> value
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public final QuoteEventBuilder<E> withDeliveryType(DeliveryType inDeliveryType)
    {
        future.setDeliveryType(inDeliveryType);
        return this;
    }
    /**
     * Sets the <code>StandardType</code> value.
     *
     * @param inStandardType a <code>StandardType</code> value
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public final QuoteEventBuilder<E> withStandardType(StandardType inStandardType)
    {
        future.setStandardType(inStandardType);
        return this;
    }
    /**
     * Sets the <code>FutureType</code> value.
     *
     * @param inFutureType a <code>FutureType</code> value
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public final QuoteEventBuilder<E> withFutureType(FutureType inFutureType)
    {
        future.setType(inFutureType);
        return this;
    }
    /**
     * Sets the <code>FutureUnderlyingAssetType</code> value.
     *
     * @param inUnderlyingAssetType an <code>UnderlyingFutureAssetType</code> value
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public final QuoteEventBuilder<E> withUnderlyingAssetType(FutureUnderlyingAssetType inUnderlyingAssetType)
    {
        future.setUnderlyingAssetType(inUnderlyingAssetType);
        return this;
    }
    /**
     * Sets the provider symbol value.
     *
     * @param inProviderSymbol a <code>String</code> value
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public final QuoteEventBuilder<E> withProviderSymbol(String inProviderSymbol)
    {
        option.setProviderSymbol(inProviderSymbol);
        future.setProviderSymbol(inProviderSymbol);
        return this;
    }
    /**
     * Sets the event type.
     *
     * @param inEventType an <code>EventMetaType</code> value
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public final QuoteEventBuilder<E> withEventType(EventType inEventType)
    {
        quote.setEventType(inEventType);
        return this;
    }
    /**
     * Sets the contract size.
     *
     * @param inContractSize an <code>int</code> value
     * @return a <code>QuoteEventBuilder&lt;E&gt;</code> value
     */
    public final QuoteEventBuilder<E> withContractSize(int inContractSize)
    {
        future.setContractSize(inContractSize);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withParity(java.math.BigDecimal)
     */
    @Override
    public QuoteEventBuilder<E> withParity(BigDecimal inParity)
    {
        convertibleBond.setParity(inParity);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withUnderlyingEquity(org.marketcetera.core.trade.Equity)
     */
    @Override
    public QuoteEventBuilder<E> withUnderlyingEquity(Equity inEquity)
    {
        convertibleBond.setUnderlyingEquity(inEquity);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withMaturity(java.lang.String)
     */
    @Override
    public QuoteEventBuilder<E> withMaturity(String inMaturity)
    {
        convertibleBond.setMaturity(inMaturity);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withYield(java.math.BigDecimal)
     */
    @Override
    public QuoteEventBuilder<E> withYield(BigDecimal inYield)
    {
        convertibleBond.setYield(inYield);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withAmountOutstanding(java.math.BigDecimal)
     */
    @Override
    public QuoteEventBuilder<E> withAmountOutstanding(BigDecimal inAmountOutstanding)
    {
        convertibleBond.setAmountOutstanding(inAmountOutstanding);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withValueDate(java.lang.String)
     */
    @Override
    public QuoteEventBuilder<E> withValueDate(String inValueDate)
    {
        convertibleBond.setValueDate(inValueDate);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withTraceReportTime(java.lang.String)
     */
    @Override
    public QuoteEventBuilder<E> withTraceReportTime(String inTraceReportTime)
    {
        convertibleBond.setTraceReportTime(inTraceReportTime);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withConversionPrice(java.math.BigDecimal)
     */
    @Override
    public QuoteEventBuilder<E> withConversionPrice(BigDecimal inConversionPrice)
    {
        convertibleBond.setConversionPrice(inConversionPrice);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withConversionRatio(java.math.BigDecimal)
     */
    @Override
    public QuoteEventBuilder<E> withConversionRatio(BigDecimal inConversionRatio)
    {
        convertibleBond.setConversionRatio(inConversionRatio);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withAccruedInterest(java.math.BigDecimal)
     */
    @Override
    public QuoteEventBuilder<E> withAccruedInterest(BigDecimal inAccruedInterest)
    {
        convertibleBond.setAccruedInterest(inAccruedInterest);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withIssuePrice(java.math.BigDecimal)
     */
    @Override
    public QuoteEventBuilder<E> withIssuePrice(BigDecimal inIssuePrice)
    {
        convertibleBond.setIssuePrice(inIssuePrice);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withConversionPremium(java.math.BigDecimal)
     */
    @Override
    public QuoteEventBuilder<E> withConversionPremium(BigDecimal inConversionPremium)
    {
        convertibleBond.setConversionPremium(inConversionPremium);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withTheoreticalDelta(java.math.BigDecimal)
     */
    @Override
    public QuoteEventBuilder<E> withTheoreticalDelta(BigDecimal inTheoreticalDelta)
    {
        convertibleBond.setTheoreticalDelta(inTheoreticalDelta);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withIssueDate(java.lang.String)
     */
    @Override
    public QuoteEventBuilder<E> withIssueDate(String inIssueDate)
    {
        convertibleBond.setIssueDate(inIssueDate);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withIssuerDomicile(java.lang.String)
     */
    @Override
    public QuoteEventBuilder<E> withIssuerDomicile(String inIssuerDomicile)
    {
        convertibleBond.setIssuerDomicile(inIssuerDomicile);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withCurrency(java.lang.String)
     */
    @Override
    public QuoteEventBuilder<E> withCurrency(String inCurrency)
    {
        convertibleBond.setCurrency(inCurrency);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withBondCurrency(java.lang.String)
     */
    @Override
    public QuoteEventBuilder<E> withBondCurrency(String inBondCurrency)
    {
        convertibleBond.setBondCurrency(inBondCurrency);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withCouponRate(java.math.BigDecimal)
     */
    @Override
    public QuoteEventBuilder<E> withCouponRate(BigDecimal inCouponRate)
    {
        convertibleBond.setCouponRate(inCouponRate);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withPaymentFrequency(java.lang.String)
     */
    @Override
    public QuoteEventBuilder<E> withPaymentFrequency(String inPaymentFrequency)
    {
        convertibleBond.setPaymentFrequency(inPaymentFrequency);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withExchangeCode(java.lang.String)
     */
    @Override
    public QuoteEventBuilder<E> withExchangeCode(String inExchangeCode)
    {
        convertibleBond.setExchangeCode(inExchangeCode);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withCompanyName(java.lang.String)
     */
    @Override
    public QuoteEventBuilder<E> withCompanyName(String inCompanyName)
    {
        convertibleBond.setCompanyName(inCompanyName);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withRating(java.lang.String)
     */
    @Override
    public QuoteEventBuilder<E> withRating(String inRating)
    {
        convertibleBond.setRating(inRating);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withRatingID(java.lang.String)
     */
    @Override
    public QuoteEventBuilder<E> withRatingID(String inRatingID)
    {
        convertibleBond.setRatingID(inRatingID);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withParValue(java.math.BigDecimal)
     */
    @Override
    public QuoteEventBuilder<E> withParValue(BigDecimal inParValue)
    {
        convertibleBond.setParValue(inParValue);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withIsin(java.lang.String)
     */
    @Override
    public QuoteEventBuilder<E> withIsin(String inIsin)
    {
        convertibleBond.setIsin(inIsin);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withCusip(java.lang.String)
     */
    @Override
    public QuoteEventBuilder<E> withCusip(String inCusip)
    {
        convertibleBond.setCusip(inCusip);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withCusip(java.lang.String)
     */
    @Override
    public QuoteEventBuilder<E> withEstimatedSizeInd(String inEstimatedSizeInd)
    {
        convertibleBond.setEstimatedSizeInd(inEstimatedSizeInd);
        return this;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("QuoteEventBuilder [option=%s, quote=%s, future=%s, convertibleBond=%s]", //$NON-NLS-1$
                             option,
                             quote,
                             future,
                             convertibleBond);
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
     * @return an <code>OptionBean</code> value
     */
    protected final OptionBean getOption()
    {
        return option;
    }
    /**
     * Gets the future value.
     *
     * @return a <code>FutureBean</code> value
     */
    protected final FutureBean getFuture()
    {
        return future;
    }
    /**
     * Gets the currency value.
     *
     * @return a <code>CurrencyBean</code> value
     */
    protected final CurrencyBean getCurrency()
    {
        return currency;
    }
    /**
     * Gets the convertible bond value.
     *
     * @return a <code>ConvertibleBondBean</code> value
     */
    protected final ConvertibleBondBean getConvertibleBond()
    {
        return convertibleBond;
    }
    /**
     * the quote attributes
     */
    private final QuoteBean quote = new QuoteBean();
    /**
     * the option attributes
     */
    private final OptionBean option = new OptionBean();
    /**
     * the future attributes
     */
    private final FutureBean future = new FutureBean();
    /**
     * the currency attributes
     */
    private final CurrencyBean currency = new CurrencyBean();
    /**
     * the convertible bond attributes
     */
    private final ConvertibleBondBean convertibleBond = new ConvertibleBondBean();
}
