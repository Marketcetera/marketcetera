/*
 Copyright (C) 2008 Daniel Kong

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

import java.util.ArrayList;
import java.util.List;

import org.jquantlib.QL;
import org.jquantlib.time.Date;
import org.jquantlib.util.TypedVisitor;
import org.jquantlib.util.Visitor;

/**
 * Predetermined cash flow
 * <p>
 * This cash flow pays a predetermined amount at a given date.
 *
 * @author Daniel Kong
 */
@SuppressWarnings("PMD.AbstractNaming")
public abstract class Dividend extends CashFlow {

	protected Date date;

	public Dividend (final Date date) {
		super();
		this.date = date;
	}


	//
	// overrides Event
	//

	@Override
	public Date date() {
		return date;
	}


	//
	// public abstract methods
	//

	public abstract double amount(final double underlying);


	//
	// public static methods
	//

	public static List<? extends Dividend> DividendVector(final List<Date> dividendDates, final List<Double> dividends) {
	    QL.require(dividendDates.size() == dividends.size() , "size mismatch between dividend dates and amounts");  // QA:[RG]::verified // TODO: message
        final List<Dividend> items = new ArrayList<Dividend>(dividendDates.size());
        for (int i=0; i<dividendDates.size(); i++) {
            items.add(new FixedDividend(dividends.get(i), dividendDates.get(i)));
        }
        return items;
    }


    //
    // implements TypedVisitable
    //

    @Override
    public void accept(final TypedVisitor<Object> v) {
        final Visitor<Object> v1 = (v!=null) ? v.getVisitor(this.getClass()) : null;
        if (v1 != null) {
            v1.visit(this);
        } else {
            super.accept(v);
        }
    }

}
