/*
 Copyright (C) 2008 Richard Gomes

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

package org.jquantlib.termstructures.yieldcurves;

import java.util.List;

import org.jquantlib.math.interpolations.Interpolation;
import org.jquantlib.math.interpolations.Interpolation.Interpolator;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Date;
import org.jquantlib.util.Pair;

/**
 *
 * @author Richard Gomes
 */


//FIXME: This class needs full code review


public interface Traits {

    /**
     * value at reference
     */
    public double initialValue(YieldTermStructure curve);

    /**
     * initial guess
     */
    public double initialGuess();

    /**
     * further guesses
     */
    public double guess(final YieldTermStructure curve, final Date d);

    /**
     * possible constraints based on previous values
     */
    public double minValueAfter(int i, final double[] data);

    /**
     * possible constraints based on maximum values
     */
    public double maxValueAfter(int i, final double[] data);

    /**
     * update with new guess
     */
    public void updateGuess(final double[] data, double value, int i);

    public boolean dummyInitialValue() /* @ReadOnly */;
    public Date initialDate(final YieldTermStructure curve) /* @ReadOnly */;
    public int maxIterations() /* @ReadOnly */;


    public interface Curve extends YieldTermStructure {

        @Override
        public Date maxDate() /* @ReadOnly */;
        public Date[] dates() /* @ReadOnly */;
        public /*@Time*/ double[] times() /* @ReadOnly */;

        public List<Pair<Date, /* @Rate */Double>> nodes() /* @ReadOnly */;

        public double[] data();
        public /*@DiscountFactor*/ double discountImpl(final /*@Time*/ double t) /* @ReadOnly */;

        @Override
        public Date referenceDate() /* @ReadOnly */;
        @Override
        public double timeFromReference(final Date date) /* @ReadOnly */;

        @Override
        public void update();

        public Interpolator interpolator() /* @ReadOnly */;
        public Interpolation interpolation() /* @ReadOnly */;
        public void setInterpolation(final Interpolation interpolation);


        //FIXME:: remove these methods. SEE: http://bugs.jquantlib.org/view.php?id=464
        // Ideally, we should employ Array<T> which could mimick closer std::vector (which is a dynamic array).
        // Doing so, we would not be obliged to overwrite an existing data structure, but we could simply
        // rezise it and add more data. Then these 3 methods below could be removed.
        //
        // Other methods in this interface would be affected too, as the client code employs the same
        // variables to call all methods defined here.
        //
        // These same issues appear in Traits.Curve too.
        public void setDates (final Date[] dates);
        public void setTimes (/*@Time*/ double[] times);
        public void setData (final double[] data);

    }

}
