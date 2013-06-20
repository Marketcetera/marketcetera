/*
Copyright (C) 2008 John Martin

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
 Copyright (C) 2006, 2007 Ferdinando Ametrano
 Copyright (C) 2006 Katiuscia Manzoni
 Copyright (C) 2006 StatPro Italia srl

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
package org.jquantlib.instruments;

import org.jquantlib.QL;
import org.jquantlib.Settings;
import org.jquantlib.daycounters.DayCounter;
import org.jquantlib.daycounters.Thirty360;
import org.jquantlib.indexes.IborIndex;
import org.jquantlib.pricingengines.PricingEngine;
import org.jquantlib.pricingengines.swap.DiscountingSwapEngine;
import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.BusinessDayConvention;
import org.jquantlib.time.Calendar;
import org.jquantlib.time.Date;
import org.jquantlib.time.DateGeneration;
import org.jquantlib.time.Period;
import org.jquantlib.time.Schedule;
import org.jquantlib.time.TimeUnit;

/**
 * This class provides a more comfortable way to instantiate standard market swap.
 *
 * @author John Martin
 */
// TODO: code review :: license, class comments, comments for access modifiers, comments for @Override
// TODO: consider refactoring this class and make it an inner class
public class MakeVanillaSwap {

    private final Period swapTenor_;
    private final IborIndex iborIndex_;
    private final /*Rate*/ double fixedRate_;
    private final Period forwardStart_;

    private Date effectiveDate_, terminationDate_;
    private Calendar fixedCalendar_, floatCalendar_;

    private VanillaSwap.Type type_;
    private /*@Real*/ double nominal_;
    private Period fixedTenor_, floatTenor_;
    private BusinessDayConvention fixedConvention_, fixedTerminationDateConvention_;
    private BusinessDayConvention floatConvention_, floatTerminationDateConvention_;
    private DateGeneration.Rule fixedRule_, floatRule_;
    private boolean fixedEndOfMonth_, floatEndOfMonth_;
    private Date fixedFirstDate_, fixedNextToLastDate_;
    private Date floatFirstDate_, floatNextToLastDate_;
    private /*@Spread*/ double floatSpread_;
    private DayCounter fixedDayCount_, floatDayCount_;
    private PricingEngine engine_;

    //XXX: remove
    // private Array fixingDays;

    public MakeVanillaSwap (
            final Period swapTenor,
            final IborIndex index) {
        this(swapTenor, index, 0.0, new Period(0,TimeUnit.Days));
    }

    public MakeVanillaSwap (
            final Period swapTenor,
            final IborIndex index,
            final /*Rate*/ double fixedRate) {
        this(swapTenor, index, fixedRate, new Period(0,TimeUnit.Days));
    }

    public MakeVanillaSwap(
            final Period swapTenor,
            final IborIndex index,
            final /* @Rate */ double fixedRate,
            final Period forwardStart) {
        this.swapTenor_ = (swapTenor);
        iborIndex_ = (index);
        fixedRate_ = (fixedRate);
        forwardStart_ = (forwardStart);
        //FIXME : JM port from quantlib :: code review :: effectiveDate_ = Date.maxDate();
        fixedCalendar_ = (index.fixingCalendar());
        floatCalendar_ = (index.fixingCalendar());
        type_ = (VanillaSwap.Type.Payer);
        nominal_ = (1.0);
        fixedTenor_ = (new Period(1, TimeUnit.Years));
        floatTenor_ = (index.tenor());
        fixedConvention_ = (BusinessDayConvention.ModifiedFollowing);
        fixedTerminationDateConvention_ = (BusinessDayConvention.ModifiedFollowing);
        floatConvention_ = (index.businessDayConvention());
        floatTerminationDateConvention_ = (index.businessDayConvention());
        fixedRule_ = (DateGeneration.Rule.Backward);
        floatRule_ = (DateGeneration.Rule.Backward);
        fixedEndOfMonth_ = (false);
        floatEndOfMonth_ = (false);
        //FIXME : JM port from quantlib
        //fixedFirstDate_ = Date.maxDate();
        //fixedNextToLastDate_ = Date.maxDate();
        //floatFirstDate_ = Date.maxDate();
        //floatNextToLastDate_ = Date.maxDate();
        floatSpread_ = (0.0);
        fixedDayCount_ = (new Thirty360());
        floatDayCount_ = (index.dayCounter());
        engine_ = new DiscountingSwapEngine(index.termStructure());
    }


    public VanillaSwap value() /* @ReadOnly */ {
        QL.validateExperimentalMode();

        Date startDate;
        if (!effectiveDate_.isNull()) {
            startDate = effectiveDate_;
        } else {
            /*@Natural*/ final int fixingDays = iborIndex_.fixingDays();
            final Date referenceDate = new Settings().evaluationDate();
            final Date spotDate = floatCalendar_.advance(referenceDate, fixingDays, TimeUnit.Days);
            startDate = spotDate.add(forwardStart_);
        }

//XXX: remove this block
//
//        Date startDate;
//        if (effectiveDate_ != null) {
//            startDate = effectiveDate_;
//        } else {
//            Date spotDate;
//            if (fixingDays == null) {
//                final int firstFixing = iborIndex_.fixingDays();
//                final Date referenceDate = new Settings().evaluationDate();
//                spotDate = floatCalendar_.advance (referenceDate, firstFixing, TimeUnit.Days);
//            } else {
//                final int firstFixing = (int) fixingDays.get(0);
//                final Date referenceDate = new Settings().evaluationDate();
//                spotDate = floatCalendar_.advance (referenceDate, firstFixing, TimeUnit.Days);
//            }
//            startDate = spotDate.add (forwardStart_);
//        }



        Date endDate;
        if (!terminationDate_.isNull()) {
            endDate = terminationDate_;
        } else {
            endDate = startDate.add (swapTenor_);
        }

        final Schedule fixedSchedule = new Schedule(startDate, endDate,
                fixedTenor_, fixedCalendar_,
                fixedConvention_,
                fixedTerminationDateConvention_,
                fixedRule_, fixedEndOfMonth_,
                fixedFirstDate_, fixedNextToLastDate_);

        final Schedule floatSchedule = new Schedule(startDate, endDate,
                floatTenor_, floatCalendar_,
                floatConvention_,
                floatTerminationDateConvention_,
                floatRule_ , floatEndOfMonth_,
                floatFirstDate_, floatNextToLastDate_);

        double usedFixedRate = fixedRate_;

        if (Double.isNaN (fixedRate_)) {
            QL.require(!iborIndex_.termStructure().empty(), "no forecasting term structure set to " + iborIndex_.name()); // TODO: message

            final VanillaSwap temp = new VanillaSwap(
                    type_,
                    nominal_,
                    fixedSchedule,
                    0.0,
                    fixedDayCount_,
                    floatSchedule,
                    iborIndex_,
                    floatSpread_,
                    floatDayCount_,
                    BusinessDayConvention.Following
                    /* , fixingDays */);

            // ATM on the forecasting curve
            temp.setPricingEngine(new DiscountingSwapEngine(iborIndex_.termStructure()));
            usedFixedRate = temp.fairRate();
        }

        //FIXME: remove parameter "fixingDays"
        final VanillaSwap swap = new VanillaSwap (
                type_,
                nominal_,
                fixedSchedule,
                usedFixedRate,
                fixedDayCount_,
                floatSchedule,
                iborIndex_,
                floatSpread_,
                floatDayCount_,
                BusinessDayConvention.Following
                /*, fixingDays */);
        swap.setPricingEngine (engine_);
        return swap;
    }



    public MakeVanillaSwap receiveFixed(final boolean flag) {
        type_ = flag ? VanillaSwap.Type.Receiver : VanillaSwap.Type.Payer;
        return this;
    }

    public MakeVanillaSwap withType(final VanillaSwap.Type type) {
        type_ = type;
        return this;
    }

    public MakeVanillaSwap withNominal(/* Real */final double n) {
        nominal_ = n;
        return this;
    }

    public MakeVanillaSwap withEffectiveDate(final Date effectiveDate) {
        effectiveDate_ = effectiveDate;
        return this;
    }

    public MakeVanillaSwap withTerminationDate(final Date terminationDate) {
        terminationDate_ = terminationDate;
        return this;
    }

    public MakeVanillaSwap withRule(final DateGeneration.Rule r) {
        fixedRule_ = r;
        floatRule_ = r;
        return this;
    }

    public MakeVanillaSwap withDiscountingTermStructure(final Handle<YieldTermStructure> discountingTermStructure) {
        engine_ = (new DiscountingSwapEngine(discountingTermStructure));
        return this;
    }

    public MakeVanillaSwap withFixedLegTenor(final Period t) {
        fixedTenor_ = t;
        return this;
    }

    public MakeVanillaSwap withFixedLegCalendar(final Calendar cal) {
        fixedCalendar_ = cal;
        return this;
    }

    public MakeVanillaSwap withFixedLegConvention(final BusinessDayConvention bdc) {
        fixedConvention_ = bdc;
        return this;
    }

    public MakeVanillaSwap withFixedLegTerminationDateConvention(final BusinessDayConvention bdc) {
        fixedTerminationDateConvention_ = bdc;
        return this;
    }

    public MakeVanillaSwap withFixedLegRule(final DateGeneration.Rule r) {
        fixedRule_ = r;
        return this;
    }

    public MakeVanillaSwap withFixedLegEndOfMonth(final boolean flag) {
        fixedEndOfMonth_ = flag;
        return this;
    }

    public MakeVanillaSwap withFixedLegFirstDate(final Date d) {
        fixedFirstDate_ = d;
        return this;
    }

    public MakeVanillaSwap withFixedLegNextToLastDate(final Date d) {
        fixedNextToLastDate_ = d;
        return this;
    }

    public MakeVanillaSwap withFixedLegDayCount(final DayCounter dc) {
        fixedDayCount_ = dc;
        return this;
    }

    public MakeVanillaSwap withFloatingLegTenor(final Period t) {
        floatTenor_ = t;
        return this;
    }

    public MakeVanillaSwap withFloatingLegCalendar(final Calendar cal) {
        floatCalendar_ = cal;
        return this;
    }

    public MakeVanillaSwap withFloatingLegConvention(final BusinessDayConvention bdc) {
        floatConvention_ = bdc;
        return this;
    }

    public MakeVanillaSwap withFloatingLegTerminationDateConvention(final BusinessDayConvention bdc) {
        floatTerminationDateConvention_ = bdc;
        return this;
    }

    public MakeVanillaSwap withFloatingLegRule(final DateGeneration.Rule r) {
        floatRule_ = r;
        return this;
    }

    public MakeVanillaSwap withFloatingLegEndOfMonth(final boolean flag) {
        floatEndOfMonth_ = flag;
        return this;
    }

    public MakeVanillaSwap withFloatingLegFirstDate(final Date d) {
        floatFirstDate_ = d;
        return this;
    }

    public MakeVanillaSwap withFloatingLegNextToLastDate(final Date d) {
        floatNextToLastDate_ = d;
        return this;
    }

    public MakeVanillaSwap withFloatingLegDayCount(final DayCounter dc) {
        floatDayCount_ = dc;
        return this;
    }

    public MakeVanillaSwap withFloatingLegSpread(/* Spread */final double sp) {
        floatSpread_ = sp;
        return this;
    }

    //FIXME: remove these methods
//    public MakeVanillaSwap withFixingDays (final int fixingDays) {
//        this.fixingDays = new Array (1);
//        this.fixingDays.set(0, fixingDays);
//        return this;
//    }
//
//    public MakeVanillaSwap withFixingDays (final Array fixingDays) {
//        this.fixingDays = fixingDays;
//        return this;
//    }

}
