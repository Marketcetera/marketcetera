package org.marketcetera.photon.ui.databinding;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.photon.commons.databinding.ITypedObservableValue;
import org.marketcetera.photon.commons.databinding.TypedObservableValueDecorator;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableList;

/* $License$ */

/**
 * Observes an instrument as an option and provides child observables for the
 * option components.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class OptionObservable extends
        CompoundObservableManager<ITypedObservableValue<Instrument>> {

    private final ITypedObservableValue<String> mSymbol;
    private final ITypedObservableValue<String> mExpiry;
    private final ITypedObservableValue<BigDecimal> mStrikePrice;
    private final ITypedObservableValue<OptionType> mOptionType;

    /**
     * Constructor.
     * 
     * @param instrument
     *            the instrument to observe
     */
    public OptionObservable(ITypedObservableValue<Instrument> instrument) {
        super(instrument);
        mSymbol = TypedObservableValueDecorator.create(String.class);
        mExpiry = TypedObservableValueDecorator.create(String.class);
        mStrikePrice = TypedObservableValueDecorator.create(BigDecimal.class);
        mOptionType = TypedObservableValueDecorator.create(OptionType.class);
        init(ImmutableList.of(mSymbol, mExpiry, mStrikePrice, mOptionType));
    }

    @Override
    protected void updateChildren() {
        Instrument instrument = getParent().getTypedValue();
        if (instrument instanceof Option) {
            Option option = (Option) instrument;
            setIfChanged(mSymbol, option.getSymbol());
            setIfChanged(mExpiry, option.getExpiry());
            setIfChanged(mStrikePrice, option.getStrikePrice());
            setIfChanged(mOptionType, option.getType());
        } else {
            setIfChanged(mSymbol, null);
            setIfChanged(mExpiry, null);
            setIfChanged(mStrikePrice, null);
            setIfChanged(mOptionType, null);
        }
    }

    @Override
    protected void updateParent() {
        String symbol = mSymbol.getTypedValue();
        String expiry = mExpiry.getTypedValue();
        BigDecimal strike = mStrikePrice.getTypedValue();
        OptionType type = mOptionType.getTypedValue();
        Option newValue = null;
        if (StringUtils.isNotBlank(symbol) && StringUtils.isNotBlank(expiry)
                && strike != null && type != null) {
            newValue = new Option(symbol, expiry, strike, type);
        }
        ITypedObservableValue<Instrument> instrument = getParent();
        setIfChanged(instrument, newValue);
    }

    /**
     * Observes the option symbol.
     * 
     * @return the option symbol observable
     */
    public ITypedObservableValue<String> observeSymbol() {
        return mSymbol;
    }

    /**
     * Observes the option expiry.
     * 
     * @return the option expiry observable
     */
    public ITypedObservableValue<String> observeExpiry() {
        return mExpiry;
    }

    /**
     * Observes the option strike price.
     * 
     * @return the option strike price observable
     */
    public ITypedObservableValue<BigDecimal> observeStrikePrice() {
        return mStrikePrice;
    }

    /**
     * Observes the option type.
     * 
     * @return the option type observable
     */
    public ITypedObservableValue<OptionType> observeOptionType() {
        return mOptionType;
    }
}
