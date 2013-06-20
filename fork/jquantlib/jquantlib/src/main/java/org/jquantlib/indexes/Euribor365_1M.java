package org.jquantlib.indexes;

import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;

/**
 * <p>1-month Euribor365 index </p>    
 */
public class Euribor365_1M extends Euribor365 {


    //
    // public methods
    //

    public Euribor365_1M(final Handle < YieldTermStructure > h) {
        super(new Period(1, TimeUnit.Months), h);
    }

}
