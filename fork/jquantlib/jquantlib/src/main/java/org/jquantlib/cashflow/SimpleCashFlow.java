/*
 Copyright (C) 2009 John Nichol

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
 This file is part of QuantLib, a free-software/open-source library 
 for financial quantitative analysts and developers - http://quantlib.org/ 
 QuantLib is free software: you can redistribute it and/or modify it 
 under the terms of the QuantLib license.  You should have received a 
 copy of the license along with this program; if not, please email 
 <quantlib-dev@lists.sf.net>. The license is also available online 
 at <http://quantlib.org/license.shtml>. 
 
 This program is distributed in the hope that it will be useful, but WITHOUT 
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 FOR A PARTICULAR PURPOSE.  See the license for more details.
 */
package org.jquantlib.cashflow;

import org.jquantlib.time.Date;
import org.jquantlib.util.TypedVisitor;
import org.jquantlib.util.Visitor;

/**
 * Predetermined cash flow
 * <p>
 * This cash flow pays a predetermined amount at a given date.
 *
 * @author John Nichol
 */
// TODO: code review :: license, class comments, comments for access modifiers, comments for @Override
public class SimpleCashFlow extends CashFlow {

    //
    // protected fields
    //

    protected double amount;
    protected Date paymentDate;

    //
    // public constructors
    //

    public SimpleCashFlow(final double amount,
            final Date paymentDate){
        this.amount = amount;
        this.paymentDate = paymentDate;
    }


    //
    // public methods
    //

    public double amount(){
        return amount;
    }

    //
    // implements Event
    //

    @Override
    public Date date() {
        return paymentDate.clone();
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
