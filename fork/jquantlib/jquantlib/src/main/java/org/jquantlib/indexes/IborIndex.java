/*
 Copyright (C) 2008 Srinivas Hasti

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
 Copyright (C) 2000, 2001, 2002, 2003 RiskMap srl
 Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008 StatPro Italia srl

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

package org.jquantlib.indexes;


import org.jquantlib.QL;
import org.jquantlib.currencies.Currency;
import org.jquantlib.daycounters.DayCounter;
import org.jquantlib.lang.exceptions.LibraryException;
import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.BusinessDayConvention;
import org.jquantlib.time.Calendar;
import org.jquantlib.time.Date;
import org.jquantlib.time.Period;

/**
 * Base class for Inter-Bank-Offered-Rate indexes (e.g. %Libor, etc.)
 *
 * @author Srinivas Hasti
 * @author Zahid Hussain
 */
// TODO: code review :: license, class comments, comments for access modifiers, comments for @Override
public class IborIndex extends InterestRateIndex {

    private final BusinessDayConvention convention_;
    private final Handle<YieldTermStructure> termStructure_;
    private final boolean endOfMonth_;

    public IborIndex(
            final String familyName,
            final Period tenor,
            final /*@Natural*/ int fixingDays,
            final Currency currency,
            final Calendar fixingCalendar,
            final BusinessDayConvention convention,
            final boolean endOfMonth,
            final DayCounter dayCounter,
            final Handle<YieldTermStructure> h) {
        super(familyName, tenor, fixingDays, currency, fixingCalendar, dayCounter);

        this.convention_ = convention;
        this.termStructure_ = h;
        this.endOfMonth_ = endOfMonth;
        if (termStructure_ != null) {
        	termStructure_.addObserver(this);
        }
    }

    public IborIndex(
            final String familyName,
            final Period tenor,
            final /*@Natural*/ int fixingDays,
            final Currency currency,
            final Calendar fixingCalendar,
            final BusinessDayConvention convention,
            final boolean endOfMonth,
            final DayCounter dayCounter) {
    	this(familyName, tenor, fixingDays, currency, fixingCalendar, 
    		convention, endOfMonth, dayCounter, new Handle<YieldTermStructure>());
    }


    public IborIndex clone(final Handle<YieldTermStructure> h) {
        final IborIndex clone = new IborIndex(
                					this.familyName(),
                					this.tenor(),
                					this.fixingDays(),
                					this.currency(),
                					this.fixingCalendar(),
                					this.businessDayConvention(),
                					this.endOfMonth(),
                					this.dayCounter(),
                					h);
        return clone;
    }


    protected static BusinessDayConvention euriborConvention(final Period p) {
        switch (p.units()) {
        case Days:
        case Weeks:
            return BusinessDayConvention.Following;
        case Months:
        case Years:
            return BusinessDayConvention.ModifiedFollowing;
        default:
            throw new LibraryException("invalid time units"); // QA:[RG]::verified // TODO: message
        }
    }

    protected static boolean euriborEOM(final Period p) {
        switch (p.units()) {
        case Days:
        case Weeks:
            return false;
        case Months:
        case Years:
            return true;
        default:
            throw new LibraryException("invalid time units");  // QA:[RG]::verified // TODO: message
        }
    }


    /*
     * (non-Javadoc)
     *
     * @see org.jquantlib.indexes.InterestRateIndex#forecastFixing(org.jquantlib.util.Date)
     */
    @Override
    protected double forecastFixing(final Date fixingDate) {
        QL.require(! termStructure_.empty() , "no forecasting term structure set to " + name());  // QA:[RG]::verified // TODO: message
        final Date fixingValueDate = valueDate(fixingDate);
        final Date endValueDate = maturityDate(fixingValueDate);
        final double fixingDiscount = termStructure_.currentLink().discount(fixingValueDate);
        final double endDiscount = termStructure_.currentLink().discount(endValueDate);
        final double fixingPeriod = dayCounter().yearFraction(fixingValueDate, endValueDate);
        return (fixingDiscount / endDiscount - 1.0) / fixingPeriod;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jquantlib.indexes.InterestRateIndex#getTermStructure()
     */
    @Override
    public Handle<YieldTermStructure> termStructure() {
        return termStructure_;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jquantlib.indexes.InterestRateIndex#maturityDate(org.jquantlib.util.Date)
     */
    @Override
    public Date maturityDate(final Date valueDate) {
        return fixingCalendar().advance(valueDate, tenor_, convention_, endOfMonth_);
    }

    public BusinessDayConvention businessDayConvention() {
        return convention_;
    }

    public boolean endOfMonth() {
        return endOfMonth_;
    }

}
