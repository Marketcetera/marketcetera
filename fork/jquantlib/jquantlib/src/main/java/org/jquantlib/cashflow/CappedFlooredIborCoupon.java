/*
Copyright (C) 2009 Ueli Hofstetter
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
package org.jquantlib.cashflow;

import org.jquantlib.QL;
import org.jquantlib.daycounters.DayCounter;
import org.jquantlib.indexes.IborIndex;
import org.jquantlib.math.Constants;
import org.jquantlib.time.Date;

/**
 * @author Ueli Hofstetter
 * @author John Martin
 */
public class CappedFlooredIborCoupon extends CappedFlooredCoupon {

    public CappedFlooredIborCoupon(
            final Date paymentDate,
            final /*@Real*/double nominal,
            final Date startDate,
            final Date endDate,
            final /*@Natural*/int fixingDays,
            final IborIndex index) {
        // default gearing to 1.0
        this (paymentDate, nominal, startDate, endDate, fixingDays, index, 1.0);
    }

    public CappedFlooredIborCoupon(
            final Date paymentDate,
            final /*@Real*/double nominal,
            final Date startDate,
            final Date endDate,
            final /*@Natural*/int fixingDays,
            final IborIndex index,
            final double gearing) {
        // default spread to 0.0
        this (paymentDate, nominal, startDate, endDate, fixingDays, index, gearing, 0.0);
    }

    public CappedFlooredIborCoupon(
            final Date paymentDate,
            final /*Real*/double nominal,
            final Date startDate,
            final Date endDate,
            final /*Natural*/int fixingDays,
            final IborIndex index,
            final double gearing,
            final double spread) {
        this (paymentDate, nominal, startDate, endDate, 
              fixingDays, index, gearing, spread, Constants.NULL_REAL, Constants.NULL_REAL);
    }

    public CappedFlooredIborCoupon(
            final Date paymentDate,
            final /*@Real*/double nominal,
            final Date startDate,
            final Date endDate,
            final /*@Natural*/int fixingDays,
            final IborIndex index,
            final double gearing,
            final double spread,
            final double cap,
            final double floor) {
        // default reference dates
        this (paymentDate, nominal, startDate, endDate, 
              fixingDays, index, gearing, spread, 
              cap, floor, new Date(), new Date()); 
    }

    public CappedFlooredIborCoupon(
            final Date paymentDate,
            final /*@Real*/double nominal,
            final Date startDate,
            final Date endDate,
            final /*@Natural*/int fixingDays,
            final IborIndex index,
            final double gearing,
            final double spread,
            final double cap,
            final double floor,
            final Date refPeriodStart,
            final Date refPeriodEnd) {
        // default day counter
        this (paymentDate, nominal, startDate, endDate, 
              fixingDays, index, gearing, spread, 
              cap, floor, refPeriodStart, refPeriodEnd, new DayCounter());
    }


    public CappedFlooredIborCoupon(
            final Date paymentDate,
            final /*Real*/double nominal,
            final Date startDate,
            final Date endDate,
            final /*Natural*/int fixingDays,
            final IborIndex index,
            final double gearing,
            final double spread,
            final double cap,
            final double floor,
            final Date refPeriodStart,
            final Date refPeriodEnd,
            final DayCounter dayCounter) {
        // default isInArrears false
        this (paymentDate, nominal, startDate, endDate, 
              fixingDays, index, gearing, spread, 
              cap, floor, refPeriodStart, refPeriodEnd,
              dayCounter, false);
    }

    public CappedFlooredIborCoupon(
            final Date paymentDate,
            final /*@Real*/double nominal,
            final Date startDate,
            final Date endDate,
            final /*@Natural*/int fixingDays,
            final IborIndex index,
            final double gearing,
            final double spread,
            final double cap,
            final double floor,
            final Date refPeriodStart,
            final Date refPeriodEnd,
            final DayCounter dayCounter,
            boolean isInArrears) {
        super (new IborCoupon(paymentDate, nominal, startDate, endDate, fixingDays,
            index, gearing, spread, refPeriodStart, refPeriodEnd,
                dayCounter, isInArrears), cap, floor);
        QL.validateExperimentalMode();
    }

}