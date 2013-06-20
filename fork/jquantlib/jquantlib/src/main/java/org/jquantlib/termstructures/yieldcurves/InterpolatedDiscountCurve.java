/*
Copyright (C) 2008 Richard Gomes
Copyright (C) 2009 John Martin

This source code is release under the BSD License.

This file is part of JQuantLib, a free-software/open-source library
for financial quantitative analysts and developers - http://jquantlib.org/

JQuantLib is free software: you can redistribute it and/or modify it
under the terms of the JQuantLib license.  You should have received a
copy of the license along with this program; if not, please email
<jquant-devel@lists.sourceforge.net>. The license is also available online at
<http://www.jquantlib.org/index.php/LICENSE.TXT>.

This program is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE.  See the license for more details.

JQuantLib is based on QuantLib. http://quantlib.org/
When applicable, the original copyright notice follows this notice.
 */

/*
 Copyright (C) 2002, 2003 Decillion Pty(Ltd)
 Copyright (C) 2005, 2006, 2008 StatPro Italia srl

 This file is part of QuantLib, a free-software/open-source library
 for financial quantitative analysts and developers - http://quantlib.org/

 QuantLib is free software: you can redistribute it and/or modify it
 under the terms of the QuantLib license.  You should have received a
 copy of the license along with this program; if not, please email
 <quantlib-dev@lists.sf.net>. The license is also available online at
 <http://quantlib.org/license.shtml>.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the license for more details.
 */

package org.jquantlib.termstructures.yieldcurves;

import java.util.ArrayList;
import java.util.List;

import org.jquantlib.QL;
import org.jquantlib.daycounters.DayCounter;
import org.jquantlib.lang.exceptions.LibraryException;
import org.jquantlib.lang.reflect.ReflectConstants;
import org.jquantlib.lang.reflect.TypeTokenTree;
import org.jquantlib.math.Closeness;
import org.jquantlib.math.interpolations.Interpolation;
import org.jquantlib.math.interpolations.Interpolation.Interpolator;
import org.jquantlib.math.matrixutilities.Array;
import org.jquantlib.termstructures.AbstractYieldTermStructure;
import org.jquantlib.time.Calendar;
import org.jquantlib.time.Date;
import org.jquantlib.util.Pair;

/**
 * Term structure based on interpolation of discount factors.
 * <p>
 * Log-linear interpolation guarantees piecewise-constant forward rates.
 *
 * @category yieldtermstructures
 *
 * @author Richard Gomes
 * @author John Martin
 */
public class InterpolatedDiscountCurve<I extends Interpolator> extends AbstractYieldTermStructure implements Traits.Curve {

    // TODO: all fields should be protected?  See: QL/C++

    private Date[]              dates;
    private /*@Time*/ double[]  times;
    private double[]            data;
    private Interpolation       interpolation;
    private final Interpolator  interpolator;


    //
    // private fields
    //

    final private Class<?>      classI;


    //
    // protected constructors
    //

    static private Interpolator constructInterpolator(final Class<?> klass) {
        if (klass==null)
            throw new LibraryException("null interpolator"); // TODO: message
        if (!Interpolator.class.isAssignableFrom(klass))
            throw new LibraryException(ReflectConstants.WRONG_ARGUMENT_TYPE);

        try {
            return (Interpolator) klass.newInstance();
        } catch (final Exception e) {
            throw new LibraryException("cannot create Interpolator", e); // TODO: message
        }
    }


    protected InterpolatedDiscountCurve(final DayCounter dc) {
        this(dc, new TypeTokenTree(InterpolatedDiscountCurve.class).getElement(0));
    }
    protected InterpolatedDiscountCurve(
            final DayCounter dc,
            final Class<?> interpolator) {
        this(dc, constructInterpolator(interpolator));
    }
    protected InterpolatedDiscountCurve(
            final DayCounter dc,
            final Interpolator interpolator) {
        super(dc);
        QL.validateExperimentalMode();

        this.classI = new TypeTokenTree(this.getClass()).getElement(0);
        if (classI != interpolator.getClass())
            throw new LibraryException(ReflectConstants.WRONG_ARGUMENT_TYPE);
        this.interpolator = interpolator;
    }

    protected InterpolatedDiscountCurve(
            final Date referenceDate,
            final DayCounter dc) {
        this(referenceDate, dc, new TypeTokenTree(InterpolatedDiscountCurve.class).getElement(0));
    }
    protected InterpolatedDiscountCurve(
            final Date referenceDate,
            final DayCounter dc,
            final Class<?> interpolator) {
        this(referenceDate, dc, constructInterpolator(interpolator));
    }
    protected InterpolatedDiscountCurve(
            final Date referenceDate,
            final DayCounter dc,
            final Interpolator interpolator) {
        super(referenceDate, new Calendar(), dc);
        QL.validateExperimentalMode();

        this.classI = interpolator.getClass();
//XXX     this.classI = new TypeTokenTree(this.getClass()).getElement(0);
//        if (classI != interpolator.getClass()) {
//            throw new LibraryException(ReflectConstants.WRONG_ARGUMENT_TYPE);
//        }
        this.interpolator = interpolator;
    }

    protected InterpolatedDiscountCurve(
            final /*@Natural*/ int settlementDays,
            final Calendar cal,
            final DayCounter dc) {
        this(settlementDays, cal, dc, new TypeTokenTree(InterpolatedDiscountCurve.class).getElement(0));
    }
    protected InterpolatedDiscountCurve(
            final /*@Natural*/ int settlementDays,
            final Calendar cal,
            final DayCounter dc,
            final Class<?> interpolator) {
        this(settlementDays, cal, dc, constructInterpolator(interpolator));
    }
    protected InterpolatedDiscountCurve(
            final /*@Natural*/ int settlementDays,
            final Calendar cal,
            final DayCounter dc,
            final Interpolator interpolator) {
        super(settlementDays, cal, dc);
        QL.validateExperimentalMode();

        this.classI = new TypeTokenTree(this.getClass()).getElement(0);
        if (classI != interpolator.getClass())
            throw new LibraryException(ReflectConstants.WRONG_ARGUMENT_TYPE);
        this.interpolator = interpolator;
    }

    protected InterpolatedDiscountCurve(
            final Date[]     dates,
            final double[]   discounts,
            final DayCounter dc,
            final Calendar cal) {
        this(dates, discounts, dc, cal, new TypeTokenTree(InterpolatedDiscountCurve.class).getElement(0));
    }
    protected InterpolatedDiscountCurve(
            final Date[]     dates,
            final double[]   discounts,
            final DayCounter dc,
            final Calendar cal,
            final Class<?> interpolator) {
        this(dates, discounts, dc, cal, constructInterpolator(interpolator));
    }


    //
    // public constructors
    //

    public InterpolatedDiscountCurve (
            final Date[] dates,
            final double[] discounts,
            final DayCounter dc,
            final Calendar calendar,
            final Interpolator interpolator) {
        super(dates[0], calendar, dc);
        
        QL.validateExperimentalMode();

        this.classI = new TypeTokenTree(this.getClass()).getElement(0);
        if (classI != interpolator.getClass())
            throw new LibraryException(ReflectConstants.WRONG_ARGUMENT_TYPE);

        QL.require (dates.length != 0, " Dates cannot be empty"); // TODO: message
        QL.require (discounts.length != 0, "Discounts cannot be empty"); // TODO: message
        QL.require (dates.length == data.length, "Dates must be the same size as Discounts"); // TODO: message
        QL.require (data[0] == 1.0, "Initial discount factor must be 1.0"); // TODO: message

        this.dates = dates; // TODO: clone() ?
        this.data = discounts; // TODO: clone() ?
        this.interpolator = interpolator;

        this.times = new double[dates.length];
        times[0] = 0.0;

        for (int i = 1; i < dates.length; ++ i) {
            QL.require (dates[i].gt (dates[i-1]), "Dates must be in ascending order"); // TODO: message
            QL.require (data[0] > 0, "Negative discount"); // TODO: message
            times[i] = dc.yearFraction (dates[0], dates[i]);
            QL.require(Closeness.isClose(times[i], times[i-1]),
            "two dates correspond to the same time under this curve's day count convention"); // TODO: message
        }

        this.interpolation = interpolator.interpolate(new Array(times), new Array(data));
        this.interpolation.update();
    }


    //
    // implement Traits.Curve
    //

    @Override
    public Date[] dates() {
        return dates;
    }

    @Override
    public Date maxDate() {
        final int last = dates.length-1;
        return dates[last];
    }

    @Override
    public List<Pair<Date, Double>> nodes() {
        final List<Pair<Date, Double>> nodes = new ArrayList<Pair<Date, Double>>();
        for (int i = 0; i < dates.length; ++i) {
            nodes.add(new Pair<Date, Double>(dates[i], data[i]));
        }
        return nodes;
    }

    @Override
    public double[] times() {
        return times;
    }

    @Override
    public double[] data() {
        return data;
    }

    @Override
    public double discountImpl(final double t) {
        return interpolation.op(t, true);
    }

    @Override
    public Interpolation interpolation() {
        return interpolation;
    }

    @Override
    public Interpolator interpolator() {
        return interpolator;
    }

    @Override
    public void setInterpolation(final Interpolation interpolation) {
        this.interpolation = interpolation;
    }

    @Override
    public void setData(final double[] data) {
        this.data = data; // TODO: clone() ?
    }


    @Override
    public void setDates(final Date[] dates) {
        this.dates = dates; // TODO: clone() ?
    }


    @Override
    public void setTimes(final double[] times) {
        this.times = times; // TODO: clone() ?
    }

}
