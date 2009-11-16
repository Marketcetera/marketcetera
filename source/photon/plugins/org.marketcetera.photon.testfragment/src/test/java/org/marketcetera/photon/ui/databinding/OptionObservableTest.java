package org.marketcetera.photon.ui.databinding;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.databinding.ITypedObservableValue;
import org.marketcetera.photon.commons.databinding.TypedObservableValueDecorator;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;
import org.marketcetera.photon.ui.databinding.OptionObservable;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;

import com.google.common.collect.ImmutableList;

/* $License$ */

/**
 * Tests {@link OptionObservable}, and also {@link CompoundObservableManager}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SimpleUIRunner.class)
public class OptionObservableTest {

    private abstract class TestTemplate<T> {
        protected String mSymbol = "ABC";
        protected String mExpiry = "200910";
        protected BigDecimal mStrike = BigDecimal.ONE;
        protected OptionType mType = OptionType.Put;

        public TestTemplate() {
            ITypedObservableValue<Instrument> instrument = TypedObservableValueDecorator
                    .create(Instrument.class);
            Option option = new Option(mSymbol, mExpiry, mStrike, mType);
            instrument.setValue(option);
            OptionObservable optionObservable = new OptionObservable(instrument);
            ITypedObservableValue<T> child = observeChild(optionObservable);
            assertThat(child.getTypedValue(), is(get(option)));
            /*
             * Change the parent instrument's value.
             */
            change();
            Option previous = option;
            option = new Option(mSymbol, mExpiry, mStrike, mType);
            assertThat(option, not(previous));
            instrument.setValue(option);
            assertThat(child.getTypedValue(), is(get(option)));
            /*
             * Make the parent instrument null.
             */
            instrument.setValue(null);
            assertThat(child.getTypedValue(), nullValue());
            /*
             * Set invalid instrument.
             */
            instrument.setValue(option);
            assertThat(child.getTypedValue(), is(get(option)));
            instrument.setValue(new Equity("X"));
            assertThat(child.getTypedValue(), nullValue());
            /*
             * Change the value on the child.
             */
            instrument.setValue(option);
            child.setValue(changeAgain());
            previous = option;
            option = new Option(mSymbol, mExpiry, mStrike, mType);
            assertThat(option, not(previous));
            assertThat(instrument.getTypedValue(), is((Instrument) option));
            /*
             * Make the child null.
             */
            child.setValue(null);
            assertThat(instrument.getTypedValue(), nullValue());
            /*
             * Subclasses can extend.
             */
            additionalTests(instrument, child);
            /*
             * Dispose parent.
             */
            instrument.dispose();
            assertTrue(child.isDisposed());
        }

        protected abstract ITypedObservableValue<T> observeChild(
                OptionObservable parent);

        protected abstract T get(Option option);

        protected abstract void change();

        protected abstract T changeAgain();

        protected void additionalTests(
                ITypedObservableValue<Instrument> instrument,
                ITypedObservableValue<T> child) {
        }
    }

    @Test
    @UI
    public void testObserveSymbol() throws Exception {
        new TestTemplate<String>() {
            @Override
            protected void change() {
                mSymbol = "IBM";
            }

            @Override
            protected String changeAgain() {
                return mSymbol = "METC";
            }

            @Override
            protected String get(Option option) {
                return option.getSymbol();
            }

            @Override
            protected ITypedObservableValue<String> observeChild(
                    OptionObservable parent) {
                return parent.observeSymbol();
            }

            @Override
            protected void additionalTests(
                    ITypedObservableValue<Instrument> instrument,
                    ITypedObservableValue<String> child) {
                child.setValue("X");
                Instrument option = new Option("X", mExpiry, mStrike, mType);
                assertThat(instrument.getTypedValue(), is(option));
                child.setValue("  ");
                assertThat(instrument.getTypedValue(), nullValue());
                child.setValue("X");
                assertThat(instrument.getTypedValue(), is(option));
                child.setValue("");
                assertThat(instrument.getTypedValue(), nullValue());
            }
        };
    }

    @Test
    @UI
    public void testObserveExpiry() throws Exception {
        new TestTemplate<String>() {
            @Override
            protected void change() {
                mExpiry = "20121212";
            }

            @Override
            protected String changeAgain() {
                return mExpiry = "20121213";
            }

            @Override
            protected String get(Option option) {
                return option.getExpiry();
            }

            @Override
            protected ITypedObservableValue<String> observeChild(
                    OptionObservable parent) {
                return parent.observeExpiry();
            }

            @Override
            protected void additionalTests(
                    ITypedObservableValue<Instrument> instrument,
                    ITypedObservableValue<String> child) {
                child.setValue("200912");
                Instrument option = new Option(mSymbol, "200912", mStrike, mType);
                assertThat(instrument.getTypedValue(), is(option));
                child.setValue("    \t");
                assertThat(instrument.getTypedValue(), nullValue());
                child.setValue("200912");
                assertThat(instrument.getTypedValue(), is(option));
                child.setValue("");
                assertThat(instrument.getTypedValue(), nullValue());
            }
        };
    }

    @Test
    @UI
    public void testObserveStrikePrice() throws Exception {
        new TestTemplate<BigDecimal>() {
            @Override
            protected void change() {
                mStrike = BigDecimal.TEN;
            }

            @Override
            protected BigDecimal changeAgain() {
                return mStrike = new BigDecimal(1.222);
            }

            @Override
            protected BigDecimal get(Option option) {
                return option.getStrikePrice();
            }

            @Override
            protected ITypedObservableValue<BigDecimal> observeChild(
                    OptionObservable parent) {
                return parent.observeStrikePrice();
            }
        };
    }

    @Test
    @UI
    public void testObserveType() throws Exception {
        new TestTemplate<OptionType>() {
            @Override
            protected void change() {
                mType = OptionType.Call;
            }

            @Override
            protected OptionType changeAgain() {
                return mType = OptionType.Put;
            }

            @Override
            protected OptionType get(Option option) {
                return option.getType();
            }

            @Override
            protected ITypedObservableValue<OptionType> observeChild(
                    OptionObservable parent) {
                return parent.observeOptionType();
            }
        };
    }

    @Test
    @UI
    public void testOtherObservablesPreservedWhenOptionInvalid()
            throws Exception {
        ITypedObservableValue<Instrument> instrument = TypedObservableValueDecorator
                .create(Instrument.class);
        Instrument option = new Option("ABC", "200910", BigDecimal.ONE,
                OptionType.Put);
        instrument.setValue(option);
        OptionObservable optionObservable = new OptionObservable(instrument);
        ImmutableList<IObservableValue> children = ImmutableList
                .<IObservableValue> of(optionObservable.observeSymbol(),
                        optionObservable.observeExpiry(), optionObservable
                                .observeStrikePrice(), optionObservable
                                .observeOptionType());
        for (IObservableValue observable : children) {
            Object value = observable.getValue();
            observable.setValue(null);
            assertThat(instrument.getValue(), nullValue());
            for (IObservableValue other : children) {
                if (other != observable) {
                    assertThat(other.getValue(), not(nullValue()));
                }
            }
            observable.setValue(value);
            assertThat(instrument.getTypedValue(), is(option));
        }
    }
}
