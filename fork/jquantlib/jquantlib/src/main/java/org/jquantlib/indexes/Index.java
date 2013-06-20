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

package org.jquantlib.indexes;

import java.util.Iterator;
import java.util.List;

import org.jquantlib.QL;
import org.jquantlib.lang.iterators.Iterables;
import org.jquantlib.math.Closeness;
import org.jquantlib.math.Constants;
import org.jquantlib.time.Calendar;
import org.jquantlib.time.Date;
import org.jquantlib.time.TimeSeries;
import org.jquantlib.util.DefaultObservable;
import org.jquantlib.util.Observable;
import org.jquantlib.util.Observer;

/**
 * Purely virtual base class for indexes
 *
 * @author Srinivas Hasti
 */
//TODO: Code review and comments
public abstract class Index implements Observable {

	//
    // public abstract methods
    //

    /**
	 * @return name of the Index
	 */
	public abstract String name();

	/**
	 * @return the calendar defining valid fixing dates
	 */
	public abstract Calendar fixingCalendar();

	/**
	 *  @return TRUE if the fixing date is a valid one
	 */
	public abstract boolean isValidFixingDate(Date fixingDate);

	/**
	 * @return the fixing at the given date. The date passed as arguments must be the actual calendar date of the
	 * fixing; no settlement days must be used.
	 */
	public abstract double fixing(Date fixingDate, boolean forecastTodaysFixing);


	//
	// public methods
	//

	/**
	 * @return the fixing TimeSeries
	 */
	public TimeSeries<Double> timeSeries() {
		return IndexManager.getInstance().get(name());
	}

	/**
	 * Stores the historical fixing at the given date
	 * <p>
	 * The date passed as arguments must be the actual calendar date of the
	 * fixing; no settlement days must be used.
	 */
	public void addFixing(final Date date, final double value, final boolean forceOverwrite) {
        final String tag = name();
        final TimeSeries<Double> ts = IndexManager.getInstance().get(tag);
        boolean noInvalidFixing = true;
        boolean noDuplicatedFixing = true;

        final boolean validFixing = isValidFixingDate(date);
        final double currentValue = ts.get(date);
        final boolean missingFixing = forceOverwrite || Closeness.isClose(currentValue, Constants.NULL_REAL);
        if (validFixing) {
            if (missingFixing) {
                ts.put(date, value);
            } else if (Closeness.isClose(currentValue, value)) {
                // Do nothing
            } else {
                noDuplicatedFixing = false;
            }
        } else {
            noInvalidFixing = false;
        }

        QL.ensure(noInvalidFixing , "at least one invalid fixing provided");  // QA:[RG]::verified // TODO: message
        QL.ensure(noDuplicatedFixing , "at least one duplicated fixing provided");  // QA:[RG]::verified // TODO: message
	}

	/**
	 * Stores historical fixings from a TimeSeries
	 * <p>
	 * The dates in the TimeSeries must be the actual calendar dates of the
	 * fixings; no settlement days must be used.
	 */
	public final void addFixings(final TimeSeries<Double> series, final boolean forceOverwrite) {
	    final Iterator<Date> itk = series.navigableKeySet().iterator();
	    final Iterator<Double> itv = series.values().iterator();
		addFixings(itk, itv, forceOverwrite);
	}

	/**
	 * Stores historical fixings at the given dates
	 * <p>
	 * The dates passed as arguments must be the actual calendar dates of the
	 * fixings; no settlement days must be used.
	 */
	public final void addFixings(final Iterator<Date> dates, final Iterator<Double> values, final boolean forceOverwrite) {
		final String tag = name();
		boolean missingFixing;
		boolean validFixing;
		boolean noInvalidFixing = true;
		boolean noDuplicatedFixing = true;
		final TimeSeries<Double> ts = IndexManager.getInstance().get(tag);

		for (final Date date : Iterables.unmodifiableIterable(dates)) {
            final double value = values.next();
            validFixing = isValidFixingDate(date);
            final double currentValue = ts.get(date);
            missingFixing = forceOverwrite || Closeness.isClose(currentValue, Constants.NULL_REAL);
            if (validFixing) {
                if (missingFixing) {
                    ts.put(date, value);
                } else if (Closeness.isClose(currentValue, value)) {
                    // Do nothing
                } else {
                    noDuplicatedFixing = false;
                }
            } else {
                noInvalidFixing = false;
            }
		}

		IndexManager.getInstance().put(tag, ts);

		QL.ensure(noInvalidFixing , "at least one invalid fixing provided");  // QA:[RG]::verified // TODO: message
		QL.ensure(noDuplicatedFixing , "at least one duplicated fixing provided");  // QA:[RG]::verified // TODO: message
	}


	/**
	 * Clear the fixings stored for the index
	 */
	public final void clearFixings() {
		IndexManager.getInstance().clearHistory(name());
	}

	public double fixing(final Date fixingDate){
        return fixing(fixingDate, false);
    }


	//
	// implements Observable
	//

	/**
	 * Implements multiple inheritance via delegate pattern to an inner class
	 *
	 */
	private final Observable delegatedObservable = new DefaultObservable(this);

	@Override
	public void addObserver(final Observer observer) {
		delegatedObservable.addObserver(observer);
	}

    @Override
	public int countObservers() {
		return delegatedObservable.countObservers();
	}

    @Override
	public void deleteObserver(final Observer observer) {
		delegatedObservable.deleteObserver(observer);
	}

    @Override
	public void notifyObservers() {
		delegatedObservable.notifyObservers();
	}

    @Override
	public void notifyObservers(final Object arg) {
		delegatedObservable.notifyObservers(arg);
	}

    @Override
	public void deleteObservers() {
		delegatedObservable.deleteObservers();
	}

    @Override
	public List<Observer> getObservers() {
		return delegatedObservable.getObservers();
	}

}
