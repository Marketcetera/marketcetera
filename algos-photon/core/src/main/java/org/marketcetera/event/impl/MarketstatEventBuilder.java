package org.marketcetera.event.impl;

import static org.marketcetera.event.Messages.VALIDATION_CURRENCY_REQUIRED;
import static org.marketcetera.event.Messages.VALIDATION_EQUITY_REQUIRED;
import static org.marketcetera.event.Messages.VALIDATION_FUTURE_REQUIRED;
import static org.marketcetera.event.Messages.VALIDATION_OPTION_REQUIRED;
import static org.marketcetera.event.Messages.VALIDATION_BOND_REQUIRED;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.event.EventType;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.beans.*;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Constructs {@link MarketstatEvent} objects.
 * 
 * <p>Construct a <code>MarketstatEvent</code> by getting a <code>MarketstatEventBuilder</code>,
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
public abstract class MarketstatEventBuilder
        implements EventBuilder<MarketstatEvent>, OptionEventBuilder<MarketstatEventBuilder>, FutureEventBuilder<MarketstatEventBuilder>, CurrencyEventBuilder<MarketstatEventBuilder>, ConvertibleBondEventBuilder<MarketstatEventBuilder>
{
    /**
     * Returns a <code>MarketstatEventBuilder</code> suitable for constructing a new <code>MarketstatEvent</code> object.
     *
     * <p>The type of marketstat event returned will match the type of the given <code>Instrument</code>,
     * i.e., an Equity-type marketstat event for an {@link Equity}, an Option-type marketstat event for an
     * {@link Option}, etc.
     * 
     * @param inInstrument an <code>Instrument</code> value indicating the type of {@link MarketstatEvent} to create
     * @return a <code>MarketstatEventBuilder</code> value
     * @throws UnsupportedOperationException if the asset class of the given <code>Instrument</code> isn't supported
     */
    public static MarketstatEventBuilder marketstat(Instrument inInstrument)
    {
        if(inInstrument instanceof Equity) {
            return equityMarketstat().withInstrument(inInstrument);
        } else if(inInstrument instanceof Option) {
            return optionMarketstat().withInstrument(inInstrument);
        } else if(inInstrument instanceof Future) {
            return futureMarketstat().withInstrument(inInstrument);
        } else if(inInstrument instanceof Currency) {
        	return currencyMarketstat().withInstrument(inInstrument);
        } else if(inInstrument instanceof ConvertibleBond) {
            return convertibleBondMarketstat().withInstrument(inInstrument);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * Returns a <code>MarketstatEventBuilder</code> suitable for constructing a new <code>MarketstatEvent</code> object
     * of type <code>Equity</code>.
     *
     * @return a <code>MarketstatEventBuilder</code> value
     * @throws IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not an {@link Equity}
     */
    public static MarketstatEventBuilder equityMarketstat()
    {
        return new MarketstatEventBuilder()
        {
            @Override
            public MarketstatEvent create()
            {
                if(!(getMarketstat().getInstrument() instanceof Equity)) {
                    throw new IllegalArgumentException(VALIDATION_EQUITY_REQUIRED.getText());
                }
                return new EquityMarketstatEventImpl(getMarketstat());
            }
        };
    }    
    /**
     * Returns a <code>MarketstatEventBuilder</code> suitable for constructing a new <code>MarketstatEvent</code> object
     * of type <code>Currency</code>.
     *
     * @return a <code>MarketstatEventBuilder</code> value
     * @throws IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not an {@link Currency}
     */
    public static MarketstatEventBuilder currencyMarketstat()
    {
        return new MarketstatEventBuilder()
        {
            @Override
            public MarketstatEvent create()
            {
                if(!(getMarketstat().getInstrument() instanceof Currency)) {
                    throw new IllegalArgumentException(VALIDATION_CURRENCY_REQUIRED.getText());
                }
                return new CurrencyMarketstatEventImpl(getMarketstat(),getCurrency());
            }
        };
    }
    
    /**
     * Returns a <code>MarketstatEventBuilder</code> suitable for constructing a new <code>MarketstatEvent</code> object
     * of type <code>Option</code>.
     *
     * @return a <code>MarketstatEventBuilder</code> value
     * @throws IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not an {@link Option}
     */
    public static MarketstatEventBuilder optionMarketstat()
    {
        return new MarketstatEventBuilder()
        {
            @Override
            public MarketstatEvent create()
            {
                if(!(getMarketstat().getInstrument() instanceof Option)) {
                    throw new IllegalArgumentException(VALIDATION_OPTION_REQUIRED.getText());
                }
                return new OptionMarketstatEventImpl(getMarketstat(),
                                                     getOption(),
                                                     getVolumeChange(),
                                                     getInterestChange());
            }
        };
    }
    /**
     * Returns a <code>MarketstatEventBuilder</code> suitable for constructing a new <code>MarketstatEvent</code> object
     * of type <code>Future</code>.
     *
     * @return a <code>MarketstatEventBuilder</code> value
     * @throws IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not a {@link Future}
     */
    public static MarketstatEventBuilder futureMarketstat()
    {
        return new MarketstatEventBuilder()
        {
            @Override
            public MarketstatEvent create()
            {
                if(!(getMarketstat().getInstrument() instanceof Future)) {
                    throw new IllegalArgumentException(VALIDATION_FUTURE_REQUIRED.getText());
                }
                return new FutureMarketstatEventImpl(getMarketstat(),
                                                     getFuture());
            }
        };
    }
    /**
     * Returns a <code>MarketstatEventBuilder</code> suitable for constructing a new <code>MarketstatEvent</code> object
     * of type <code>ConvertibleBond</code>.
     *
     * @return a <code>MarketstatEventBuilder</code> value
     * @throws IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not a {@link ConvertibleBond}
     */
    public static MarketstatEventBuilder convertibleBondMarketstat()
    {
        return new MarketstatEventBuilder()
        {
            @Override
            public MarketstatEvent create()
            {
                if(!(getMarketstat().getInstrument() instanceof ConvertibleBond)) {
                    throw new IllegalArgumentException(VALIDATION_BOND_REQUIRED.getText());
                }
                return new ConvertibleBondMarketstatEventImpl(getMarketstat(),
                                                              getConvertibleBond());
            }
        };
    }
    /**
     * Sets the message id to use with the new event. 
     *
     * @param inMessageId a <code>long</code> value
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withMessageId(long inMessageId)
    {
        marketstat.setMessageId(inMessageId);
        return this;
    }
    /**
     * Sets the timestamp value to use with the new event.
     *
     * @param inTimestamp a <code>Date</code> value or <code>null</code>
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withTimestamp(Date inTimestamp)
    {
        marketstat.setTimestamp(inTimestamp);
        return this;
    }
    /**
     * Sets the source value to use with the new event.
     *
     * @param inSource an <code>Object</code> value or <code>null</code>
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public MarketstatEventBuilder withSource(Object inSource)
    {
        marketstat.setSource(inSource);
        return this;
    }
    /**
     * Sets the instrument value.
     *
     * @param inInstrument an <code>Instrument</code> value or <code>null</code>
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withInstrument(Instrument inInstrument)
    {
        marketstat.setInstrument(inInstrument);
        if(inInstrument instanceof Option) {
            option.setInstrument((Option)inInstrument);
        } else if(inInstrument instanceof Future) {
            future.setInstrument((Future)inInstrument);
        }else if(inInstrument instanceof Currency) {
            currency.setInstrument((Currency)inInstrument);
        } else if(inInstrument instanceof ConvertibleBond) {
            convertibleBond.setInstrument((ConvertibleBond)inInstrument);
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
     * Sets the openPrice value.
     *
     * @param inOpenPrice a <code>BigDecimal</code> value or <code>null</code>
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withOpenPrice(BigDecimal inOpenPrice)
    {
        marketstat.setOpen(inOpenPrice);
        return this;
    }
    /**
     * Sets the highPrice value.
     *
     * @param inHighPrice a <code>BigDecimal</code> value or <code>null</code>
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withHighPrice(BigDecimal inHighPrice)
    {
        marketstat.setHigh(inHighPrice);
        return this;
    }
    /**
     * Sets the lowPrice value.
     *
     * @param inLowPrice a <code>BigDecimal</code> value or <code>null</code>
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withLowPrice(BigDecimal inLowPrice)
    {
        marketstat.setLow(inLowPrice);
        return this;
    }
    /**
     * Sets the closePrice value.
     *
     * @param inClosePrice a <code>BigDecimal</code> value or <code>null</code>
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withClosePrice(BigDecimal inClosePrice)
    {
        marketstat.setClose(inClosePrice);
        return this;
    }
    /**
     * Sets the previousClosePrice value.
     *
     * @param inPreviousClosePrice a <code>BigDecimal</code> value or <code>null</code>
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withPreviousClosePrice(BigDecimal inPreviousClosePrice)
    {
        marketstat.setPreviousClose(inPreviousClosePrice);
        return this;
    }
    /**
     * Sets the volume value.
     *
     * @param inVolume a <code>BigDecimal</code> value or <code>null</code>
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withVolume(BigDecimal inVolume)
    {
        marketstat.setVolume(inVolume);
        return this;
    }
    /**
     * Sets the value value.
     *
     * @param inValue a <code>BigDecimal</code> value or <code>null</code>
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withValue(BigDecimal inValue)
    {
        marketstat.setValue(inValue);
        return this;
    }
    /**
     * Sets the closeDate value.
     *
     * @param inCloseDate a <code>String</code> value or <code>null</code>
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withCloseDate(String inCloseDate)
    {
        marketstat.setCloseDate(inCloseDate);
        return this;
    }
    /**
     * Sets the previousCloseDate value.
     *
     * @param inPreviousCloseDate a <code>String</code> value or <code>null</code>
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withPreviousCloseDate(String inPreviousCloseDate)
    {
        marketstat.setPreviousCloseDate(inPreviousCloseDate);
        return this;
    }
    /**
     * Sets the tradeHighTime value.
     *
     * @param inTradeHighTime a <code>String</code> value or <code>null</code>
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withTradeHighTime(String inTradeHighTime)
    {
        marketstat.setTradeHighTime(inTradeHighTime);
        return this;
    }
    /**
     * Sets the tradeLowTime value.
     *
     * @param inTradeLowTime a <code>String</code> value or <code>null</code>
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withTradeLowTime(String inTradeLowTime)
    {
        marketstat.setTradeLowTime(inTradeLowTime);
        return this;
    }
    /**
     * Sets the openExchange value.
     *
     * @param inOpenExchange a <code>String</code> value or <code>null</code>
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withOpenExchange(String inOpenExchange)
    {
        marketstat.setOpenExchange(inOpenExchange);
        return this;
    }
    /**
     * Sets the highExchange value.
     *
     * @param inHighExchange a <code>String</code> value or <code>null</code>
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withHighExchange(String inHighExchange)
    {
        marketstat.setHighExchange(inHighExchange);
        return this;
    }
    /**
     * Sets the lowExchange value.
     *
     * @param inLowExchange a <code>String</code> value or <code>null</code>
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withLowExchange(String inLowExchange)
    {
        marketstat.setLowExchange(inLowExchange);
        return this;
    }
    /**
     * Sets the closeExchange value.
     *
     * @param inCloseExchange a <code>String</code> value or <code>null</code>
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withCloseExchange(String inCloseExchange)
    {
        marketstat.setCloseExchange(inCloseExchange);
        return this;
    }
    /**
     * Sets the underlyingInstrument value.
     *
     * @param inUnderlyingInstrument an <code>Instrument</code> value or <code>null</code>
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withUnderlyingInstrument(Instrument inUnderlyingInstrument)
    {
        option.setUnderlyingInstrument(inUnderlyingInstrument);
        return this;
    }
    /**
     * Sets the expirationType value.
     *
     * @param inExpirationType an <code>ExpirationType</code> value or <code>null</code>
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withExpirationType(ExpirationType inExpirationType)
    {
        option.setExpirationType(inExpirationType);
        return this;
    }
    /**
     * Sets the multiplier value.
     *
     * @param inMultiplier a <code>BigDecimal</code> value
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withMultiplier(BigDecimal inMultiplier)
    {
        option.setMultiplier(inMultiplier);
        return this;
    }
    /**
     * Sets the hasDeliverable value.
     *
     * @param inHasDeliverable a <code>boolean</code> value
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder hasDeliverable(boolean inHasDeliverable)
    {
        option.setHasDeliverable(inHasDeliverable);
        return this;
    }
    /**
     * Sets the <code>DeliveryType</code> value.
     *
     * @param inDeliveryType a <code>DeliveryType</code> value
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withDeliveryType(DeliveryType inDeliveryType)
    {
        future.setDeliveryType(inDeliveryType);
        return this;
    }
    /**
     * Sets the <code>StandardType</code> value.
     *
     * @param inStandardType a <code>StandardType</code> value
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withStandardType(StandardType inStandardType)
    {
        future.setStandardType(inStandardType);
        return this;
    }
    /**
     * Sets the <code>FutureType</code> value.
     *
     * @param inFutureType a <code>FutureType</code> value
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withFutureType(FutureType inFutureType)
    {
        future.setType(inFutureType);
        return this;
    }
    /**
     * Sets the <code>FutureUnderlyingAssetType</code> value.
     *
     * @param inUnderlyingAssetType an <code>UnderlyingFutureAssetType</code> value
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withUnderlyingAssetType(FutureUnderlyingAssetType inUnderlyingAssetType)
    {
        future.setUnderlyingAssetType(inUnderlyingAssetType);
        return this;
    }
    /**
     * Sets the provider symbol value.
     *
     * @param inProviderSymbol a <code>String</code> value
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withProviderSymbol(String inProviderSymbol)
    {
        option.setProviderSymbol(inProviderSymbol);
        future.setProviderSymbol(inProviderSymbol);
        return this;
    }
    /**
     * Sets the contract size.
     *
     * @param inContractSize an <code>int</code> value
     * @return a <code>MarketstatEventBuilder&lt;E&gt;</code> value
     */
    public final MarketstatEventBuilder withContractSize(int inContractSize)
    {
        future.setContractSize(inContractSize);
        return this;
    }
    /**
     * Sets the change in volume.
     *
     * @param inVolumeChange a <code>BigDecimal</code> value
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withVolumeChange(BigDecimal inVolumeChange)
    {
        volumeChange = inVolumeChange;
        return this;
    }
    /**
     * Sets the change in interest.
     *
     * @param inInterestChange a <code>BigDecimal</code> value
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withInterestChange(BigDecimal inInterestChange)
    {
        interestChange = inInterestChange;
        return this;
    }
    /**
     * Sets the event type.
     *
     * @param inEventType an <code>EventType</code> value
     * @return a <code>MarketstatEventBuilder</code> value
     */
    public final MarketstatEventBuilder withEventType(EventType inEventType)
    {
        marketstat.setEventType(inEventType);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withParity(java.math.BigDecimal)
     */
    @Override
    public MarketstatEventBuilder withParity(BigDecimal inParity)
    {
        convertibleBond.setParity(inParity);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withUnderlyingEquity(org.marketcetera.core.trade.Equity)
     */
    @Override
    public MarketstatEventBuilder withUnderlyingEquity(Equity inEquity)
    {
        convertibleBond.setUnderlyingEquity(inEquity);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withMaturity(java.lang.String)
     */
    @Override
    public MarketstatEventBuilder withMaturity(String inMaturity)
    {
        convertibleBond.setMaturity(inMaturity);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withYield(java.math.BigDecimal)
     */
    @Override
    public MarketstatEventBuilder withYield(BigDecimal inYield)
    {
        convertibleBond.setYield(inYield);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withAmountOutstanding(java.math.BigDecimal)
     */
    @Override
    public MarketstatEventBuilder withAmountOutstanding(BigDecimal inAmountOutstanding)
    {
        convertibleBond.setAmountOutstanding(inAmountOutstanding);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withValueDate(java.lang.String)
     */
    @Override
    public MarketstatEventBuilder withValueDate(String inValueDate)
    {
        convertibleBond.setValueDate(inValueDate);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withTraceReportTime(java.lang.String)
     */
    @Override
    public MarketstatEventBuilder withTraceReportTime(String inTraceReportTime)
    {
        convertibleBond.setTraceReportTime(inTraceReportTime);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withConversionPrice(java.math.BigDecimal)
     */
    @Override
    public MarketstatEventBuilder withConversionPrice(BigDecimal inConversionPrice)
    {
        convertibleBond.setConversionPrice(inConversionPrice);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withConversionRatio(java.math.BigDecimal)
     */
    @Override
    public MarketstatEventBuilder withConversionRatio(BigDecimal inConversionRatio)
    {
        convertibleBond.setConversionRatio(inConversionRatio);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withAccruedInterest(java.math.BigDecimal)
     */
    @Override
    public MarketstatEventBuilder withAccruedInterest(BigDecimal inAccruedInterest)
    {
        convertibleBond.setAccruedInterest(inAccruedInterest);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withIssuePrice(java.math.BigDecimal)
     */
    @Override
    public MarketstatEventBuilder withIssuePrice(BigDecimal inIssuePrice)
    {
        convertibleBond.setIssuePrice(inIssuePrice);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withConversionPremium(java.math.BigDecimal)
     */
    @Override
    public MarketstatEventBuilder withConversionPremium(BigDecimal inConversionPremium)
    {
        convertibleBond.setConversionPremium(inConversionPremium);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withTheoreticalDelta(java.math.BigDecimal)
     */
    @Override
    public MarketstatEventBuilder withTheoreticalDelta(BigDecimal inTheoreticalDelta)
    {
        convertibleBond.setTheoreticalDelta(inTheoreticalDelta);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withIssueDate(java.lang.String)
     */
    @Override
    public MarketstatEventBuilder withIssueDate(String inIssueDate)
    {
        convertibleBond.setIssueDate(inIssueDate);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withIssuerDomicile(java.lang.String)
     */
    @Override
    public MarketstatEventBuilder withIssuerDomicile(String inIssuerDomicile)
    {
        convertibleBond.setIssuerDomicile(inIssuerDomicile);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withCurrency(java.lang.String)
     */
    @Override
    public MarketstatEventBuilder withCurrency(String inCurrency)
    {
        convertibleBond.setCurrency(inCurrency);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withBondCurrency(java.lang.String)
     */
    @Override
    public MarketstatEventBuilder withBondCurrency(String inBondCurrency)
    {
        convertibleBond.setBondCurrency(inBondCurrency);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withCouponRate(java.math.BigDecimal)
     */
    @Override
    public MarketstatEventBuilder withCouponRate(BigDecimal inCouponRate)
    {
        convertibleBond.setCouponRate(inCouponRate);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withPaymentFrequency(java.lang.String)
     */
    @Override
    public MarketstatEventBuilder withPaymentFrequency(String inPaymentFrequency)
    {
        convertibleBond.setPaymentFrequency(inPaymentFrequency);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withExchangeCode(java.lang.String)
     */
    @Override
    public MarketstatEventBuilder withExchangeCode(String inExchangeCode)
    {
        convertibleBond.setExchangeCode(inExchangeCode);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withCompanyName(java.lang.String)
     */
    @Override
    public MarketstatEventBuilder withCompanyName(String inCompanyName)
    {
        convertibleBond.setCompanyName(inCompanyName);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withRating(java.lang.String)
     */
    @Override
    public MarketstatEventBuilder withRating(String inRating)
    {
        convertibleBond.setRating(inRating);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withRatingID(java.lang.String)
     */
    @Override
    public MarketstatEventBuilder withRatingID(String inRatingID)
    {
        convertibleBond.setRatingID(inRatingID);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withParValue(java.math.BigDecimal)
     */
    @Override
    public MarketstatEventBuilder withParValue(BigDecimal inParValue)
    {
        convertibleBond.setParValue(inParValue);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withIsin(java.lang.String)
     */
    @Override
    public MarketstatEventBuilder withIsin(String inIsin)
    {
        convertibleBond.setIsin(inIsin);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withCusip(java.lang.String)
     */
    @Override
    public MarketstatEventBuilder withCusip(String inCusip)
    {
        convertibleBond.setCusip(inCusip);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withEstimatedSizeInd(java.lang.String)
     */
    @Override
    public MarketstatEventBuilder withEstimatedSizeInd(String inEstimatedSizeInd)
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
        return String.format("MarketstatEventBuilder [marketstat=%s, option=%s, future=%s, convertibleBond=%s]", //$NON-NLS-1$
                             marketstat,
                             option,
                             future,
                             convertibleBond);
    }
    /**
     * Get the marketstat value.
     *
     * @return a <code>MarketstatBean</code> value
     */
    protected final MarketstatBean getMarketstat()
    {
        return marketstat;
    }
    /**
     * Gets the option value.
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
     * Gets the volume change value.
     *
     * @return a <code>BigDecimal</code> value
     */
    protected final BigDecimal getVolumeChange()
    {
        return volumeChange;
    }
    /**
     * Gets the interest change volume. 
     *
     * @return a <code>BigDecimal</code> value
     */
    protected final BigDecimal getInterestChange()
    {
        return interestChange;
    }
    /**
     * the marketstat attributes 
     */
    private MarketstatBean marketstat = new MarketstatBean();
    /**
     *  the option attributes
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
    /**
     * the change in volume since the previous close, may be <code>null</code> 
     */
    private BigDecimal volumeChange;
    /**
     * the change in interest since the previous close, may be <code>null</code>
     */
    private BigDecimal interestChange;
}
