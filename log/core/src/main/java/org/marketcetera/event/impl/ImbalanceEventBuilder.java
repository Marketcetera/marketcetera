package org.marketcetera.event.impl;

import static org.marketcetera.event.Messages.*;

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
 * Constructs {@link ImbalanceEvent} objects.
 * 
 * <p>Construct an <code>ImbalanceEvent</code> by getting an <code>ImbalanceEventBuilder</code>,
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
public abstract class ImbalanceEventBuilder
        implements EventBuilder<ImbalanceEvent>, OptionEventBuilder<ImbalanceEventBuilder>, FutureEventBuilder<ImbalanceEventBuilder>, CurrencyEventBuilder<ImbalanceEventBuilder>, ConvertibleBondEventBuilder<ImbalanceEventBuilder>
{
    /**
     * Returns a <code>ImbalanceEventBuilder</code> suitable for constructing a new <code>ImbalanceEvent</code> object.
     *
     * <p>The type of Imbalance event returned will match the type of the given <code>Instrument</code>,
     * i.e., an Equity-type Imbalance event for an {@link Equity}, an Option-type Imbalance event for an
     * {@link Option}, etc.
     * 
     * @param inInstrument an <code>Instrument</code> value indicating the type of {@link ImbalanceEvent} to create
     * @return an <code>ImbalanceEventBuilder</code> value
     * @throws UnsupportedOperationException if the asset class of the given <code>Instrument</code> isn't supported
     */
    public static ImbalanceEventBuilder Imbalance(Instrument inInstrument)
    {
        if(inInstrument instanceof Equity) {
            return equityImbalance().withInstrument(inInstrument);
        } else if(inInstrument instanceof Option) {
            return optionImbalance().withInstrument(inInstrument);
        } else if(inInstrument instanceof Future) {
            return futureImbalance().withInstrument(inInstrument);
        } else if(inInstrument instanceof Currency) {
            return currencyImbalance().withInstrument(inInstrument);
        } else if(inInstrument instanceof ConvertibleBond) {
            return convertibleBondImbalance().withInstrument(inInstrument);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * Returns a <code>ImbalanceEventBuilder</code> suitable for constructing a new <code>ImbalanceEvent</code> object
     * of type <code>Equity</code>.
     *
     * @return an <code>ImbalanceEventBuilder</code> value
     * @throws IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not an {@link Equity}
     */
    public static ImbalanceEventBuilder equityImbalance()
    {
        return new ImbalanceEventBuilder()
        {
            @Override
            public ImbalanceEvent create()
            {
                if(!(getImbalance().getInstrument() instanceof Equity)) {
                    throw new IllegalArgumentException(VALIDATION_EQUITY_REQUIRED.getText());
                }
                return new EquityImbalanceEvent(getImbalance());
            }
        };
    }    
    /**
     * Returns an <code>ImbalanceEventBuilder</code> suitable for constructing a new <code>ImbalanceEvent</code> object
     * of type <code>Currency</code>.
     *
     * @return an <code>ImbalanceEventBuilder</code> value
     * @throws IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not an {@link Currency}
     */
    public static ImbalanceEventBuilder currencyImbalance()
    {
        return new ImbalanceEventBuilder()
        {
            @Override
            public ImbalanceEvent create()
            {
                if(!(getImbalance().getInstrument() instanceof Currency)) {
                    throw new IllegalArgumentException(VALIDATION_CURRENCY_REQUIRED.getText());
                }
                return new CurrencyImbalanceEvent(getImbalance(),getCurrency());
            }
        };
    }
    /**
     * Returns an <code>ImbalanceEventBuilder</code> suitable for constructing a new <code>ImbalanceEvent</code> object
     * of type <code>Option</code>.
     *
     * @return an <code>ImbalanceEventBuilder</code> value
     * @throws IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not an {@link Option}
     */
    public static ImbalanceEventBuilder optionImbalance()
    {
        return new ImbalanceEventBuilder()
        {
            @Override
            public ImbalanceEvent create()
            {
                if(!(getImbalance().getInstrument() instanceof Option)) {
                    throw new IllegalArgumentException(VALIDATION_OPTION_REQUIRED.getText());
                }
                return new OptionImbalanceEvent(getImbalance(),
                                                getOption());
            }
        };
    }
    /**
     * Returns an <code>ImbalanceEventBuilder</code> suitable for constructing a new <code>ImbalanceEvent</code> object
     * of type <code>Future</code>.
     *
     * @return an <code>ImbalanceEventBuilder</code> value
     * @throws IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not a {@link Future}
     */
    public static ImbalanceEventBuilder futureImbalance()
    {
        return new ImbalanceEventBuilder()
        {
            @Override
            public ImbalanceEvent create()
            {
                if(!(getImbalance().getInstrument() instanceof Future)) {
                    throw new IllegalArgumentException(VALIDATION_FUTURE_REQUIRED.getText());
                }
                return new FutureImbalanceEvent(getImbalance(),
                                                getFuture());
            }
        };
    }
    /**
     * Returns an <code>ImbalanceEventBuilder</code> suitable for constructing a new <code>ImbalanceEvent</code> object
     * of type <code>ConvertibleBond</code>.
     *
     * @return an <code>ImbalanceEventBuilder</code> value
     * @throws IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not a {@link ConvertibleBond}
     */
    public static ImbalanceEventBuilder convertibleBondImbalance()
    {
        return new ImbalanceEventBuilder()
        {
            @Override
            public ImbalanceEvent create()
            {
                if(!(getImbalance().getInstrument() instanceof ConvertibleBond)) {
                    throw new IllegalArgumentException(VALIDATION_BOND_REQUIRED.getText());
                }
                return new ConvertibleBondImbalanceEvent(getImbalance(),
                                                         getConvertibleBond());
            }
        };
    }
    /**
     * Sets the auction type to use with the new event.
     *
     * @param inAuctionType an <code>AuctionType</code> value
     * @return an <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder withAuctionType(AuctionType inAuctionType)
    {
        imbalance.setAuctionType(inAuctionType);
        return this;
    }
    /**
     * Sets the exchange to use with the new event.
     *
     * @param inExchange a <code>String</code> value
     * @return an <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder withExchange(String inExchange)
    {
        imbalance.setExchange(inExchange);
        return this;
    }
    /**
     * Sets the far price to use with the new event.
     *
     * @param inFarPrice a <code>BigDecimal</code> value
     * @return an <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder withFarPrice(BigDecimal inFarPrice)
    {
        imbalance.setFarPrice(inFarPrice);
        return this;
    }
    /**
     * Sets the far price to use with the new event.
     *
     * @param inImbalanceVolume a <code>BigDecimal</code> value
     * @return an <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder withImbalanceVolume(BigDecimal inImbalanceVolume)
    {
        imbalance.setImbalanceVolume(inImbalanceVolume);
        return this;
    }
    /**
     * Sets the instrument status to use with the new event.
     *
     * @param inInstrumentStatus an <code>InstrumentStatus</code> value
     * @return an <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder withInstrumentStatus(InstrumentStatus inInstrumentStatus)
    {
        imbalance.setInstrumentStatus(inInstrumentStatus);
        return this;
    }
    /**
     * Sets the market status to use with the new event.
     *
     * @param inMarketStatus a <code>MarketStatus</code> value
     * @return an <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder withMarketStatus(MarketStatus inMarketStatus)
    {
        imbalance.setMarketStatus(inMarketStatus);
        return this;
    }
    /**
     * Sets the near price to use with the new event.
     *
     * @param inNearPrice a <code>BigDecimal</code> value
     * @return an <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder withNearPrice(BigDecimal inNearPrice)
    {
        imbalance.setNearPrice(inNearPrice);
        return this;
    }
    /**
     * Sets the paired volume to use with the new event.
     *
     * @param inPairedVolume a <code>BigDecimal</code> value
     * @return an <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder withPairedVolume(BigDecimal inPairedVolume)
    {
        imbalance.setPairedVolume(inPairedVolume);
        return this;
    }
    /**
     * Sets the reference price to use with the new event.
     *
     * @param inReferencePrice a <code>BigDecimal</code> value
     * @return an <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder withReferencePrice(BigDecimal inReferencePrice)
    {
        imbalance.setReferencePrice(inReferencePrice);
        return this;
    }
    /**
     * Sets the imbalance type to use with the new event.
     *
     * @param inImbalanceType an <code>ImbalanceType</code> value
     * @return an <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder withImbalanceType(ImbalanceType inImbalanceType)
    {
        imbalance.setImbalanceType(inImbalanceType);
        return this;
    }
    /**
     * Sets the short sale restricted value to use with the new event.
     *
     * @param inShortSaleRestricted a <code>boolean</code> value
     * @return an <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder withShortSaleRestricted(boolean inShortSaleRestricted)
    {
        imbalance.setShortSaleRestricted(inShortSaleRestricted);
        return this;
    }
    /**
     * Sets the message id to use with the new event. 
     *
     * @param inMessageId a <code>long</code> value
     * @return a <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder withMessageId(long inMessageId)
    {
        imbalance.setMessageId(inMessageId);
        return this;
    }
    /**
     * Sets the timestamp value to use with the new event.
     *
     * @param inTimestamp a <code>Date</code> value or <code>null</code>
     * @return a <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder withTimestamp(Date inTimestamp)
    {
        imbalance.setTimestamp(inTimestamp);
        return this;
    }
    /**
     * Sets the source value to use with the new event.
     *
     * @param inSource an <code>Object</code> value or <code>null</code>
     * @return a <code>ImbalanceEventBuilder</code> value
     */
    public ImbalanceEventBuilder withSource(Object inSource)
    {
        imbalance.setSource(inSource);
        return this;
    }
    /**
     * Sets the provider value to use with the new event.
     *
     * @param inProvider an <code>Object</code> value or <code>null</code>
     * @return a <code>TradeEventBuilder</code> value
     */
    public ImbalanceEventBuilder withProvider(String inProvider)
    {
        imbalance.setProvider(inProvider);
        return this;
    }
    /**
     * Sets the instrument value.
     *
     * @param inInstrument an <code>Instrument</code> value or <code>null</code>
     * @return a <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder withInstrument(Instrument inInstrument)
    {
        imbalance.setInstrument(inInstrument);
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
     * Sets the underlyingInstrument value.
     *
     * @param inUnderlyingInstrument an <code>Instrument</code> value or <code>null</code>
     * @return a <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder withUnderlyingInstrument(Instrument inUnderlyingInstrument)
    {
        option.setUnderlyingInstrument(inUnderlyingInstrument);
        return this;
    }
    /**
     * Sets the expirationType value.
     *
     * @param inExpirationType an <code>ExpirationType</code> value or <code>null</code>
     * @return a <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder withExpirationType(ExpirationType inExpirationType)
    {
        option.setExpirationType(inExpirationType);
        return this;
    }
    /**
     * Sets the multiplier value.
     *
     * @param inMultiplier a <code>BigDecimal</code> value
     * @return a <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder withMultiplier(BigDecimal inMultiplier)
    {
        option.setMultiplier(inMultiplier);
        return this;
    }
    /**
     * Sets the hasDeliverable value.
     *
     * @param inHasDeliverable a <code>boolean</code> value
     * @return a <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder hasDeliverable(boolean inHasDeliverable)
    {
        option.setHasDeliverable(inHasDeliverable);
        return this;
    }
    /**
     * Sets the <code>DeliveryType</code> value.
     *
     * @param inDeliveryType a <code>DeliveryType</code> value
     * @return a <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder withDeliveryType(DeliveryType inDeliveryType)
    {
        future.setDeliveryType(inDeliveryType);
        return this;
    }
    /**
     * Sets the <code>StandardType</code> value.
     *
     * @param inStandardType a <code>StandardType</code> value
     * @return a <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder withStandardType(StandardType inStandardType)
    {
        future.setStandardType(inStandardType);
        return this;
    }
    /**
     * Sets the <code>FutureType</code> value.
     *
     * @param inFutureType a <code>FutureType</code> value
     * @return a <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder withFutureType(FutureType inFutureType)
    {
        future.setType(inFutureType);
        return this;
    }
    /**
     * Sets the <code>FutureUnderlyingAssetType</code> value.
     *
     * @param inUnderlyingAssetType an <code>UnderlyingFutureAssetType</code> value
     * @return a <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder withUnderlyingAssetType(FutureUnderlyingAssetType inUnderlyingAssetType)
    {
        future.setUnderlyingAssetType(inUnderlyingAssetType);
        return this;
    }
    /**
     * Sets the provider symbol value.
     *
     * @param inProviderSymbol a <code>String</code> value
     * @return a <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder withProviderSymbol(String inProviderSymbol)
    {
        option.setProviderSymbol(inProviderSymbol);
        future.setProviderSymbol(inProviderSymbol);
        return this;
    }
    /**
     * Sets the contract size.
     *
     * @param inContractSize an <code>int</code> value
     * @return a <code>ImbalanceEventBuilder&lt;E&gt;</code> value
     */
    public final ImbalanceEventBuilder withContractSize(int inContractSize)
    {
        future.setContractSize(inContractSize);
        return this;
    }
    /**
     * Sets the event type.
     *
     * @param inEventType an <code>EventType</code> value
     * @return a <code>ImbalanceEventBuilder</code> value
     */
    public final ImbalanceEventBuilder withEventType(EventType inEventType)
    {
        imbalance.setEventType(inEventType);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withParity(java.math.BigDecimal)
     */
    @Override
    public ImbalanceEventBuilder withParity(BigDecimal inParity)
    {
        convertibleBond.setParity(inParity);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withUnderlyingEquity(org.marketcetera.core.trade.Equity)
     */
    @Override
    public ImbalanceEventBuilder withUnderlyingEquity(Equity inEquity)
    {
        convertibleBond.setUnderlyingEquity(inEquity);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withMaturity(java.lang.String)
     */
    @Override
    public ImbalanceEventBuilder withMaturity(String inMaturity)
    {
        convertibleBond.setMaturity(inMaturity);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withYield(java.math.BigDecimal)
     */
    @Override
    public ImbalanceEventBuilder withYield(BigDecimal inYield)
    {
        convertibleBond.setYield(inYield);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withAmountOutstanding(java.math.BigDecimal)
     */
    @Override
    public ImbalanceEventBuilder withAmountOutstanding(BigDecimal inAmountOutstanding)
    {
        convertibleBond.setAmountOutstanding(inAmountOutstanding);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withValueDate(java.lang.String)
     */
    @Override
    public ImbalanceEventBuilder withValueDate(String inValueDate)
    {
        convertibleBond.setValueDate(inValueDate);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withTraceReportTime(java.lang.String)
     */
    @Override
    public ImbalanceEventBuilder withTraceReportTime(String inTraceReportTime)
    {
        convertibleBond.setTraceReportTime(inTraceReportTime);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withConversionPrice(java.math.BigDecimal)
     */
    @Override
    public ImbalanceEventBuilder withConversionPrice(BigDecimal inConversionPrice)
    {
        convertibleBond.setConversionPrice(inConversionPrice);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withConversionRatio(java.math.BigDecimal)
     */
    @Override
    public ImbalanceEventBuilder withConversionRatio(BigDecimal inConversionRatio)
    {
        convertibleBond.setConversionRatio(inConversionRatio);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withAccruedInterest(java.math.BigDecimal)
     */
    @Override
    public ImbalanceEventBuilder withAccruedInterest(BigDecimal inAccruedInterest)
    {
        convertibleBond.setAccruedInterest(inAccruedInterest);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withIssuePrice(java.math.BigDecimal)
     */
    @Override
    public ImbalanceEventBuilder withIssuePrice(BigDecimal inIssuePrice)
    {
        convertibleBond.setIssuePrice(inIssuePrice);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withConversionPremium(java.math.BigDecimal)
     */
    @Override
    public ImbalanceEventBuilder withConversionPremium(BigDecimal inConversionPremium)
    {
        convertibleBond.setConversionPremium(inConversionPremium);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withTheoreticalDelta(java.math.BigDecimal)
     */
    @Override
    public ImbalanceEventBuilder withTheoreticalDelta(BigDecimal inTheoreticalDelta)
    {
        convertibleBond.setTheoreticalDelta(inTheoreticalDelta);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withIssueDate(java.lang.String)
     */
    @Override
    public ImbalanceEventBuilder withIssueDate(String inIssueDate)
    {
        convertibleBond.setIssueDate(inIssueDate);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withIssuerDomicile(java.lang.String)
     */
    @Override
    public ImbalanceEventBuilder withIssuerDomicile(String inIssuerDomicile)
    {
        convertibleBond.setIssuerDomicile(inIssuerDomicile);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withCurrency(java.lang.String)
     */
    @Override
    public ImbalanceEventBuilder withCurrency(String inCurrency)
    {
        convertibleBond.setCurrency(inCurrency);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withBondCurrency(java.lang.String)
     */
    @Override
    public ImbalanceEventBuilder withBondCurrency(String inBondCurrency)
    {
        convertibleBond.setBondCurrency(inBondCurrency);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withCouponRate(java.math.BigDecimal)
     */
    @Override
    public ImbalanceEventBuilder withCouponRate(BigDecimal inCouponRate)
    {
        convertibleBond.setCouponRate(inCouponRate);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withPaymentFrequency(java.lang.String)
     */
    @Override
    public ImbalanceEventBuilder withPaymentFrequency(String inPaymentFrequency)
    {
        convertibleBond.setPaymentFrequency(inPaymentFrequency);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withExchangeCode(java.lang.String)
     */
    @Override
    public ImbalanceEventBuilder withExchangeCode(String inExchangeCode)
    {
        convertibleBond.setExchangeCode(inExchangeCode);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withCompanyName(java.lang.String)
     */
    @Override
    public ImbalanceEventBuilder withCompanyName(String inCompanyName)
    {
        convertibleBond.setCompanyName(inCompanyName);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withRating(java.lang.String)
     */
    @Override
    public ImbalanceEventBuilder withRating(String inRating)
    {
        convertibleBond.setRating(inRating);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withRatingID(java.lang.String)
     */
    @Override
    public ImbalanceEventBuilder withRatingID(String inRatingID)
    {
        convertibleBond.setRatingID(inRatingID);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withParValue(java.math.BigDecimal)
     */
    @Override
    public ImbalanceEventBuilder withParValue(BigDecimal inParValue)
    {
        convertibleBond.setParValue(inParValue);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withIsin(java.lang.String)
     */
    @Override
    public ImbalanceEventBuilder withIsin(String inIsin)
    {
        convertibleBond.setIsin(inIsin);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withCusip(java.lang.String)
     */
    @Override
    public ImbalanceEventBuilder withCusip(String inCusip)
    {
        convertibleBond.setCusip(inCusip);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.impl.ConvertibleBondEventBuilder#withEstimatedSizeInd(java.lang.String)
     */
    @Override
    public ImbalanceEventBuilder withEstimatedSizeInd(String inEstimatedSizeInd)
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
        return String.format("ImbalanceEventBuilder [Imbalance=%s, option=%s, future=%s, currency=%s, convertibleBond=%s]", //$NON-NLS-1$
                             imbalance,
                             option,
                             future,
                             currency,
                             convertibleBond);
    }
    /**
     * Get the Imbalance value.
     *
     * @return an <code>ImbalanceBean</code> value
     */
    protected final ImbalanceBean getImbalance()
    {
        return imbalance;
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
     * the Imbalance attributes 
     */
    private ImbalanceBean imbalance = new ImbalanceBean();
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
}
