package org.jquantlib.indexes;

import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;

/**
 * <p>1-month Euribor index </p>    
 */
public class Euribor1M extends Euribor {


    //
    // public methods
    //

    public Euribor1M(final Handle < YieldTermStructure > h) {
        super(new Period(1, TimeUnit.Months), h);
    }

}
